// Section F2 : mensurations, prêt de vêtements et ajustement morphologique.
import * as api from './api.js';
import { $, echapper, tenter, pourcentage, EVENEMENT_PROFIL } from './commun.js';

const LIBELLES_MESURES = {
  TOUR_POITRINE: 'Tour de poitrine',
  TOUR_TAILLE: 'Tour de taille',
  TOUR_HANCHES: 'Tour de hanches',
  LONGUEUR_JAMBE: 'Longueur de jambe',
  CARRURE: 'Carrure',
};
const LIBELLES_VERDICTS = { JUSTE: 'un peu juste', IDEAL: 'idéal', AMPLE: 'ample' };

let profilCourant = null;
let types = [];

const formatCm = (valeur) => `${valeur.toLocaleString('fr-FR', { maximumFractionDigits: 1 })} cm`;
const formatEcart = (ecart) => `${ecart > 0 ? '+' : ''}${formatCm(ecart)}`;

async function chargerTypes() {
  types = await api.typesDeVetements();
  for (const { type } of types) {
    $('#type-vetement').append(new Option(type.toLowerCase(), type));
    $('#filtre-type').append(new Option(type.toLowerCase(), type));
  }
  afficherChampsMesures();
}

/** Les mesures à saisir dépendent du type : on les lit dans les règles exposées par l'API. */
function afficherChampsMesures() {
  const regles = types.find((t) => t.type === $('#type-vetement').value);
  $('#mesures-vetement').innerHTML = (regles?.criteres ?? []).map((critere) => `
    <label>${LIBELLES_MESURES[critere.mesure]}
      <input name="${critere.mesure}" type="number" step="0.5" min="1" max="300" required></label>`).join('');
}

async function chargerMensurations() {
  const formulaire = $('#form-mensurations');
  formulaire.reset();
  $('#compatibles').innerHTML = '<p class="aide">Renseignez vos mensurations, puis lancez la recherche.</p>';
  if (!profilCourant) return;
  try {
    const mensurations = await api.lireMensurations(profilCourant.id);
    for (const champ of ['tourPoitrine', 'tourTaille', 'tourHanches', 'longueurJambe', 'carrure']) {
      formulaire[champ].value = mensurations[champ];
    }
  } catch {
    // pas encore de mensurations pour ce profil : le formulaire reste vide
  }
}

async function enregistrerMensurations(evenement) {
  evenement.preventDefault();
  if (!profilCourant) return;
  const valeurs = Object.fromEntries([...new FormData(evenement.target)].map(([cle, valeur]) => [cle, Number(valeur)]));
  const enregistrees = await tenter(() => api.enregistrerMensurations(profilCourant.id, valeurs));
  if (enregistrees) await rechercher();
}

async function proposerVetement(evenement) {
  evenement.preventDefault();
  if (!profilCourant) return;
  const formulaire = evenement.target;
  const mesures = Object.fromEntries([...formulaire.querySelectorAll('#mesures-vetement input')]
    .map((champ) => [champ.name, Number(champ.value)]));
  const declare = await tenter(() => api.declarerVetement({
    proprietaireId: profilCourant.id,
    type: formulaire.type.value,
    nom: formulaire.nom.value,
    description: formulaire.description.value,
    mesures,
  }));
  if (declare) {
    formulaire.reset();
    afficherChampsMesures();
    formulaire.closest('details').open = false;
  }
}

async function rechercher(evenement) {
  evenement?.preventDefault();
  if (!profilCourant) return;
  const filtre = $('#form-recherche');
  const ajustements = await tenter(() => api.vetementsCompatibles(profilCourant.id, filtre.k.value, filtre.type.value));
  if (ajustements) afficherCompatibles(ajustements);
}

function afficherCompatibles(ajustements) {
  if (ajustements.length === 0) {
    $('#compatibles').innerHTML = '<p class="aide">Aucun vêtement ne vous va pour le moment.</p>';
    return;
  }
  $('#compatibles').innerHTML = ajustements.map(({ vetement, score, details }, rang) => `
    <div class="carte">
      <div class="entete">
        <strong>${rang + 1}. ${echapper(vetement.nom)}</strong>
        <span>${vetement.type.toLowerCase()} · ajustement <strong>${pourcentage(score)}</strong></span>
      </div>
      <div class="jauge"><div style="width:${pourcentage(score)}"></div></div>
      ${vetement.description ? `<p class="aide">${echapper(vetement.description)}</p>` : ''}
      <table>
        <tr><th>Mesure</th><th>Vous</th><th>Vêtement</th><th>Écart</th><th>Verdict</th></tr>
        ${details.map((d) => `<tr><td>${LIBELLES_MESURES[d.mesure]}</td><td>${formatCm(d.corps)}</td>
          <td>${formatCm(d.vetement)}</td><td>${formatEcart(d.ecart)}</td>
          <td class="verdict-${d.verdict}">${LIBELLES_VERDICTS[d.verdict]}</td></tr>`).join('')}
      </table>
    </div>`).join('');
}

export function initialiserTenue() {
  chargerTypes().catch(() => {});
  $('#type-vetement').addEventListener('change', afficherChampsMesures);
  $('#form-mensurations').addEventListener('submit', enregistrerMensurations);
  $('#form-vetement').addEventListener('submit', proposerVetement);
  $('#form-recherche').addEventListener('submit', rechercher);
  document.addEventListener(EVENEMENT_PROFIL, (evenement) => {
    profilCourant = evenement.detail;
    chargerMensurations();
  });
}
