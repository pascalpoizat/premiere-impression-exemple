// Section F3 : concepts requis d'une candidature et planning de révision jusqu'à la veille de l'entretien.
import * as api from './api.js';
import { $, echapper, tenter, EVENEMENT_PROFIL } from './commun.js';
import { afficherSaisieDesDisponibilites, afficherTempsPlanifies, lireDisponibilites } from './disponibilites.js';

const JOUR_EN_MS = 24 * 60 * 60 * 1000;
const STRATEGIES = {
  CLOSE_TO_ACHIEVEMENT: 'CLOSE_TO_ACHIEVEMENT (CTA) : finir d\'abord ce qui est le plus proche de l\'objectif',
  MINIMAL_KNOWLEDGE: 'MINIMAL_KNOWLEDGE (MKW) : remonter d\'abord ce qu\'on connaît le moins',
};

let profilCourant = null;
let candidatures = [];

const candidatureChoisie = () => candidatures.find((c) => String(c.id) === $('#revision-candidature').value);

/** Jours de révision : d'aujourd'hui à la veille de l'entretien (le back vérifie le même nombre). */
function joursDeRevision(candidature) {
  if (!candidature?.dateHeureEntretien) return null;
  const aujourdhui = new Date();
  aujourdhui.setHours(0, 0, 0, 0);
  const entretien = new Date(candidature.dateHeureEntretien);
  entretien.setHours(0, 0, 0, 0);
  const n = Math.max(0, Math.round((entretien - aujourdhui) / JOUR_EN_MS));
  // on ajoute des jours de calendrier, pas des tranches de 24 h : un jour de changement d'heure dure 23 ou 25 h
  return Array.from({ length: n }, (_, j) => new Date(aujourdhui.getFullYear(), aujourdhui.getMonth(),
    aujourdhui.getDate() + j));
}

const formatJour = (date) => new Date(date).toLocaleDateString('fr-FR', { weekday: 'short', day: 'numeric', month: 'short' });
const formatDuree = (minutes) => (minutes >= 60 ? `${Math.floor(minutes / 60)} h ${String(minutes % 60).padStart(2, '0')}` : `${minutes} min`);

async function chargerCandidatures() {
  const profil = profilCourant;
  const reponse = profil ? await tenter(() => api.listerCandidatures(profil.id)) ?? [] : [];
  // le profil a changé pendant l'appel : cette réponse est périmée, celle du nouveau profil va suivre
  if (profil !== profilCourant) return;
  candidatures = reponse;
  $('#revision-candidature').replaceChildren(...candidatures.map((c) =>
    new Option(`${c.poste} — ${c.entreprise}`, c.id)));
  await afficherCandidature();
}

async function afficherCandidature() {
  $('#planning').innerHTML = '<p class="aide">Saisissez les concepts requis et vos disponibilités, puis planifiez.</p>';
  const candidature = candidatureChoisie();
  $('#form-concept').hidden = !candidature;
  if (!candidature) {
    $('#liste-concepts').innerHTML = '<p class="aide">Aucune candidature pour ce profil.</p>';
    $('#disponibilites').replaceChildren();
    return;
  }
  afficherDisponibilites(candidature);
  await afficherConcepts(candidature);
}

async function afficherConcepts(candidature) {
  const concepts = await tenter(() => api.listerConceptsRequis(candidature.id)) ?? [];
  if (concepts.length === 0) {
    $('#liste-concepts').innerHTML = '<p class="aide">Aucun concept requis : ajoutez-les par ordre d\'importance dans l\'annonce.</p>';
    return;
  }
  $('#liste-concepts').innerHTML = `<table>
    <tr><th>Rang</th><th>Concept</th><th>Actuel</th><th>Requis</th><th>Par niveau</th><th></th></tr>
    ${concepts.map((c) => `<tr><td>${c.rang}</td><td>${echapper(c.nom)}</td><td>${c.actuel}</td><td>${c.requis}</td>
      <td>${formatDuree(c.qtn)}</td><td><button type="button" data-id="${c.id}" title="Supprimer">✕</button></td></tr>`).join('')}
  </table>`;
  $('#liste-concepts').querySelectorAll('button[data-id]').forEach((bouton) => bouton.addEventListener('click',
    async () => {
      await tenter(() => api.supprimerConceptRequis(candidature.id, bouton.dataset.id));
      await afficherConcepts(candidature);
    }));
}

function afficherDisponibilites(candidature) {
  const jours = joursDeRevision(candidature);
  const zone = $('#disponibilites');
  if (jours === null) {
    zone.innerHTML = '<p class="aide">Cette candidature n\'a pas de date d\'entretien : impossible de planifier.</p>';
    return;
  }
  if (jours.length === 0) {
    zone.innerHTML = '<p class="aide">L\'entretien est aujourd\'hui ou passé : plus de jour de révision.</p>';
    return;
  }
  afficherSaisieDesDisponibilites(zone, jours, () => {
    $('#planning').innerHTML = '<p class="aide">Disponibilités modifiées : planifiez à nouveau.</p>';
  });
}

async function ajouterConcept(evenement) {
  evenement.preventDefault();
  const candidature = candidatureChoisie();
  if (!candidature) return;
  const formulaire = evenement.target;
  const ajoute = await tenter(() => api.ajouterConceptRequis(candidature.id, {
    nom: formulaire.nom.value,
    actuel: Number(formulaire.actuel.value),
    requis: Number(formulaire.requis.value),
    qtn: Number(formulaire.qtn.value),
  }));
  if (ajoute) {
    formulaire.reset();
    await afficherConcepts(candidature);
  }
}

async function planifier(evenement) {
  evenement.preventDefault();
  const candidature = candidatureChoisie();
  if (!candidature) return;
  const disponibilites = lireDisponibilites();
  const planning = await tenter(() => api.planningRevision(candidature.id, $('#strategie').value, disponibilites));
  if (planning) {
    afficherPlanning(planning);
    afficherTempsPlanifies(planning);
  }
}

function afficherPlanning(planning) {
  const joursAvecBlocs = planning.jours.filter((jour) => jour.blocs.length > 0);
  $('#planning').innerHTML = `
    <h3>Planning (${echapper(STRATEGIES[planning.strategie].split(' :')[0])})</h3>
    <p>Temps utilisé : <strong>${formatDuree(planning.tempsUtilise)}</strong> sur ${formatDuree(planning.tempsDisponible)}
      disponibles${planning.tempsInutilise > 0 ? ` · ${formatDuree(planning.tempsInutilise)} inutilisés` : ''}.</p>
    ${joursAvecBlocs.length === 0 ? '<p class="aide">Rien à réviser dans le temps disponible.</p>' : `<table>
      <tr><th>Jour</th><th>Révision</th></tr>
      ${joursAvecBlocs.map((jour) => `<tr data-date="${jour.date}"><td>${formatJour(jour.date)}</td><td>${jour.blocs.map((b) =>
        `${echapper(b.concept)} → niveau ${b.niveau} (${formatDuree(b.duree)})`).join('<br>')}</td></tr>`).join('')}
    </table>`}
    <h4>Niveaux atteints le jour de l'entretien</h4>
    <table>
      <tr><th>Concept</th><th>Actuel</th><th>Atteint</th><th>Requis</th></tr>
      ${planning.niveauxAtteints.map((n) => `<tr><td>${echapper(n.concept)}</td><td>${n.actuel}</td>
        <td><strong>${n.atteint}</strong></td><td>${n.requis}${n.atteint >= n.requis ? ' ✔' : ''}</td></tr>`).join('')}
    </table>`;
}

export function initialiserRevision() {
  $('#strategie').replaceChildren(...Object.entries(STRATEGIES).map(([valeur, libelle]) => new Option(libelle, valeur)));
  $('#revision-candidature').addEventListener('change', afficherCandidature);
  $('#form-concept').addEventListener('submit', ajouterConcept);
  $('#form-planning').addEventListener('submit', planifier);
  document.addEventListener(EVENEMENT_PROFIL, (evenement) => {
    profilCourant = evenement.detail;
    chargerCandidatures();
  });
}
