// Section F1 : candidatures et analyse d'annonce.
import * as api from './api.js';
import { $, echapper, tenter, pourcentage, EVENEMENT_PROFIL } from './commun.js';

const K = 12;
const LIBELLE_AFFICHER = 'Afficher';
const LIBELLE_MASQUER = 'Masquer';
let profilCourant = null;

async function afficherCandidatures() {
  const liste = $('#liste-candidatures');
  liste.replaceChildren();
  $('#analyse').innerHTML = '<p class="aide">Choisissez une candidature pour analyser son annonce.</p>';
  if (!profilCourant) return;
  const profil = profilCourant;
  const candidatures = await tenter(() => api.listerCandidatures(profil.id)) ?? [];
  // le profil a changé pendant l'appel : cette réponse est périmée, celle du nouveau profil va suivre
  if (profil !== profilCourant) return;
  // remplacer d'un bloc, et non ajouter : deux réponses en vol pour le même profil ne s'additionnent pas (#12)
  liste.replaceChildren(...candidatures.map(elementDeListe));
  if (candidatures.length === 0) liste.innerHTML = '<li class="aide">Aucune candidature pour ce profil.</li>';
}

function elementDeListe(candidature) {
  const element = document.createElement('li');
  const date = candidature.dateHeureEntretien
    ? new Date(candidature.dateHeureEntretien).toLocaleString('fr-FR', { dateStyle: 'medium', timeStyle: 'short' })
    : 'date à fixer';
  element.innerHTML = `<strong>${echapper(candidature.poste)}</strong> — ${echapper(candidature.entreprise)}
    <span class="aide">${echapper(date)}</span>`;
  element.addEventListener('click', () => analyser(candidature, element));
  return element;
}

async function analyser(candidature, element) {
  document.querySelectorAll('#liste-candidatures li').forEach((li) => li.classList.remove('actif'));
  element.classList.add('actif');
  const analyse = await tenter(() => api.analyserAnnonce(candidature.id, K));
  // une autre candidature a été choisie entre-temps : on n'affiche pas cette analyse périmée
  if (analyse && element.classList.contains('actif')) afficherAnalyse(candidature, analyse);
}

function pastilles(motsCles, classe) {
  const poidsMax = Math.max(...motsCles.map((m) => m.poids), Number.EPSILON);
  return motsCles.map((m) => `<span class="pastille ${classe}" style="--intensite:${(m.poids / poidsMax).toFixed(2)}"
      title="poids ${m.poids.toFixed(4)} · racine ${echapper(m.racine)}">${echapper(m.terme)}</span>`).join('');
}

function afficherAnalyse(candidature, analyse) {
  const aTravailler = analyse.competencesATravailler;
  $('#analyse').innerHTML = `
    <h3>${echapper(candidature.poste)} — ${echapper(candidature.entreprise)}</h3>
    <p>Correspondance avec le profil <strong>${echapper(profilCourant.pseudo)}</strong> :</p>
    <div class="jauge"><div style="width:${pourcentage(analyse.correspondance)}"></div></div>
    <p class="score">${pourcentage(analyse.correspondance)}</p>
    <h4>Mots-clés de l'annonce</h4>
    <p>${pastilles(analyse.motsCles, '')}</p>
    <h4>Compétences à travailler (${aTravailler.length})</h4>
    <p>${aTravailler.length ? pastilles(aTravailler, 'manquante') : 'Toutes les compétences clés sont dans votre profil.'}</p>
    <h4>Annonce</h4>
    <button type="button" id="bascule-annonce">${LIBELLE_AFFICHER}</button>
    <p id="texte-annonce" class="annonce" hidden>${echapper(candidature.texteAnnonce)}</p>`;
  $('#bascule-annonce').addEventListener('click', basculerAnnonce);
}

/** L'annonce est masquée au départ ; le bouton porte l'action qu'il déclenchera. */
function basculerAnnonce() {
  const texte = $('#texte-annonce');
  texte.hidden = !texte.hidden;
  $('#bascule-annonce').textContent = texte.hidden ? LIBELLE_AFFICHER : LIBELLE_MASQUER;
}

async function enregistrer(evenement) {
  evenement.preventDefault();
  if (!profilCourant) return;
  const formulaire = evenement.target;
  const donnees = Object.fromEntries(new FormData(formulaire));
  const creee = await tenter(() => api.creerCandidature({
    ...donnees,
    profilId: profilCourant.id,
    dateHeureEntretien: donnees.dateHeureEntretien || null,
  }));
  if (creee) {
    formulaire.reset();
    await afficherCandidatures();
  }
}

export function initialiserCandidatures() {
  $('#form-candidature').addEventListener('submit', enregistrer);
  document.addEventListener(EVENEMENT_PROFIL, (evenement) => {
    profilCourant = evenement.detail;
    afficherCandidatures();
  });
}
