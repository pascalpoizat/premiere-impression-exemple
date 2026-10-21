// F3 : saisie des minutes disponibles par jour de révision (le budget), par jours de la semaine (comme les
// jours d'un réveil) ou jour par jour dans un calendrier mensuel, qui affiche aussi le temps planifié par jour.

const MINUTES_PAR_JOUR_PAR_DEFAUT = 0;
const MINUTES_PAR_JOUR_MAX = 1440;
// jours de la semaine dans l'ordre d'affichage, avec leur numéro JavaScript (getDay : 0 = dimanche)
const SEMAINE = [
  { numero: 1, initiale: 'L', nom: 'lundi' },
  { numero: 2, initiale: 'M', nom: 'mardi' },
  { numero: 3, initiale: 'M', nom: 'mercredi' },
  { numero: 4, initiale: 'J', nom: 'jeudi' },
  { numero: 5, initiale: 'V', nom: 'vendredi' },
  { numero: 6, initiale: 'S', nom: 'samedi' },
  { numero: 0, initiale: 'D', nom: 'dimanche' },
];
// un raccourci remplace la sélection, il ne s'y ajoute pas
const RACCOURCIS = {
  Tous: [1, 2, 3, 4, 5, 6, 0],
  Semaine: [1, 2, 3, 4, 5],
  Samedi: [6],
  Dimanche: [0],
  'Week-end': [6, 0],
};

let jours = [];
let volumes = [];
let planifies = [];
let selection = new Set();
let quandLeBudgetChange = () => {};

/** Date locale au format AAAA-MM-JJ (toISOString donnerait la date UTC, décalée la nuit). */
const dateIso = (date) => [date.getFullYear(), String(date.getMonth() + 1).padStart(2, '0'),
  String(date.getDate()).padStart(2, '0')].join('-');

const volumeValide = (texte) => {
  const minutes = Number(texte);
  return texte !== '' && Number.isInteger(minutes) && minutes >= 0 && minutes <= MINUTES_PAR_JOUR_MAX;
};

/**
 * Affiche la saisie dans `zone` pour ces jours de révision ; budget et temps planifié valent 0 au départ.
 * `siLeBudgetChange` est appelé quand un budget change : le planning calculé n'est alors plus valable.
 */
export function afficherSaisieDesDisponibilites(zone, joursDeRevision, siLeBudgetChange) {
  jours = joursDeRevision;
  volumes = jours.map(() => MINUTES_PAR_JOUR_PAR_DEFAUT);
  planifies = jours.map(() => 0);
  selection = new Set();
  quandLeBudgetChange = siLeBudgetChange;
  zone.innerHTML = `<p class="aide">Minutes disponibles par jour, jusqu'à la veille de l'entretien (${jours.length} jours) :
      choisissez des jours de la semaine et appliquez-leur un volume, ou cliquez sur un jour du calendrier.</p>
    <div class="saisie-rapide">
      <div id="bascules-jours" class="boutons-groupes" role="group" aria-label="Jours de la semaine">
        ${SEMAINE.map((j) => `<button type="button" data-numero="${j.numero}" aria-label="${j.nom}"
          aria-pressed="false">${j.initiale}</button>`).join('')}
      </div>
      <div id="raccourcis-jours" class="boutons-groupes" role="group" aria-label="Raccourcis">
        ${Object.keys(RACCOURCIS).map((nom) => `<button type="button">${nom}</button>`).join('')}
      </div>
      <div class="ligne">
        <label>Volume (min) <input id="volume-jours" type="number" min="0" max="${MINUTES_PAR_JOUR_MAX}" step="15"
          value="${MINUTES_PAR_JOUR_PAR_DEFAUT}"></label>
        <button type="button" id="appliquer-volume">Appliquer</button>
        <button type="button" id="reinitialiser-volume">Réinitialiser</button>
      </div>
    </div>
    <p class="aide">Dans chaque case : le jour, le temps planifié (au milieu) et le budget (en bas), en minutes.</p>
    <div id="calendrier"></div>`;
  zone.querySelectorAll('#bascules-jours button').forEach((bouton) =>
    bouton.addEventListener('click', () => basculer(Number(bouton.dataset.numero))));
  zone.querySelectorAll('#raccourcis-jours button').forEach((bouton) =>
    bouton.addEventListener('click', () => selectionner(RACCOURCIS[bouton.textContent])));
  zone.querySelector('#appliquer-volume').addEventListener('click', appliquerVolume);
  zone.querySelector('#reinitialiser-volume').addEventListener('click', () => affecterAuxJoursSelectionnes(0));
  // Entrée dans le champ volume applique au lieu de lancer le planning (le champ est dans son formulaire)
  zone.querySelector('#volume-jours').addEventListener('keydown', (evenement) => {
    if (evenement.key === 'Enter') {
      evenement.preventDefault();
      appliquerVolume();
    }
  });
  afficherCalendrier();
}

/** Minutes disponibles, une valeur par jour de révision, dans l'ordre des jours. */
export const lireDisponibilites = () => [...volumes];

/** Le temps planifié d'un jour est la somme des durées de ses révisions dans le planning reçu. */
export function afficherTempsPlanifies(planning) {
  const parDate = new Map(planning.jours.map((jour) => [jour.date, jour.blocs.reduce((somme, b) => somme + b.duree, 0)]));
  planifies = jours.map((jour) => parDate.get(dateIso(jour)) ?? 0);
  afficherCalendrier();
}

/** Solution simple : un budget modifié remet le temps planifié à 0 (signaler les valeurs fausses est une perspective). */
function changerLesBudgets(nouveaux) {
  if (nouveaux.every((minutes, j) => minutes === volumes[j])) return;
  volumes = nouveaux;
  planifies = jours.map(() => 0);
  quandLeBudgetChange();
}

function basculer(numero) {
  if (selection.has(numero)) selection.delete(numero);
  else selection.add(numero);
  afficherSelection();
}

function selectionner(numeros) {
  selection = new Set(numeros);
  afficherSelection();
}

function afficherSelection() {
  document.querySelectorAll('#bascules-jours button').forEach((bouton) =>
    bouton.setAttribute('aria-pressed', String(selection.has(Number(bouton.dataset.numero)))));
  afficherCalendrier();
}

function appliquerVolume() {
  const champ = document.querySelector('#volume-jours');
  if (!volumeValide(champ.value)) {
    champ.setCustomValidity(`Un nombre entier de minutes entre 0 et ${MINUTES_PAR_JOUR_MAX}.`);
    champ.reportValidity();
    champ.setCustomValidity('');
    return;
  }
  affecterAuxJoursSelectionnes(Number(champ.value));
}

function affecterAuxJoursSelectionnes(minutes) {
  changerLesBudgets(jours.map((jour, j) => (selection.has(jour.getDay()) ? minutes : volumes[j])));
  afficherCalendrier();
}

/** Un bloc par mois touché, semaines du lundi au dimanche ; les jours hors de la révision sont grisés. */
function afficherCalendrier() {
  const calendrier = document.querySelector('#calendrier');
  const indices = new Map(jours.map((jour, j) => [dateIso(jour), j]));
  const mois = [...new Set(jours.map((jour) => `${jour.getFullYear()}-${jour.getMonth()}`))];
  calendrier.innerHTML = mois.map((cle) => {
    const [annee, numeroMois] = cle.split('-').map(Number);
    return blocDuMois(annee, numeroMois, indices);
  }).join('');
  calendrier.querySelectorAll('td[data-date]').forEach((cellule) =>
    cellule.addEventListener('click', () => modifierLeJour(cellule)));
}

function blocDuMois(annee, numeroMois, indices) {
  const premier = new Date(annee, numeroMois, 1);
  const nombreDeJours = new Date(annee, numeroMois + 1, 0).getDate();
  const decalage = (premier.getDay() + 6) % 7; // lundi en première colonne
  const cellules = Array.from({ length: decalage }, () => '<td class="hors"></td>');
  for (let jour = 1; jour <= nombreDeJours; jour++) {
    const date = new Date(annee, numeroMois, jour);
    const j = indices.get(dateIso(date));
    cellules.push(j === undefined ? `<td class="hors">${jour}</td>`
      : `<td data-date="${dateIso(date)}" class="${selection.has(date.getDay()) ? 'selectionne' : ''}"
          title="Jour, temps planifié et budget (min) : cliquer pour modifier le budget">
          <span class="numero">${jour}</span><span class="revision${planifies[j] > 0 ? ' planifie' : ''}">${planifies[j]}</span>
          <span class="volume">${volumes[j]}</span></td>`);
  }
  while (cellules.length % 7 !== 0) cellules.push('<td class="hors"></td>');
  const semaines = Array.from({ length: cellules.length / 7 }, (_, s) => `<tr>${cellules.slice(7 * s, 7 * s + 7).join('')}</tr>`);
  const titre = premier.toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' });
  return `<table class="mois"><caption>${titre}</caption>
    <tr>${SEMAINE.map((j) => `<th>${j.initiale}</th>`).join('')}</tr>${semaines.join('')}</table>`;
}

/** Le jour devient un champ : Entrée ou un clic ailleurs valide, Échap annule. */
function modifierLeJour(cellule) {
  if (cellule.querySelector('input')) return;
  const j = jours.findIndex((jour) => dateIso(jour) === cellule.dataset.date);
  const volume = cellule.querySelector('.volume');
  const champ = document.createElement('input');
  Object.assign(champ, { type: 'number', min: 0, max: MINUTES_PAR_JOUR_MAX, step: 15, value: volumes[j] });
  champ.setAttribute('aria-label', `Minutes le ${cellule.dataset.date}`);
  volume.replaceWith(champ);
  champ.focus();
  champ.select();
  let termine = false;
  const terminer = (valider) => {
    if (termine) return;
    termine = true;
    if (valider && volumeValide(champ.value)) {
      changerLesBudgets(volumes.map((minutes, k) => (k === j ? Number(champ.value) : minutes)));
    }
    afficherCalendrier();
  };
  champ.addEventListener('keydown', (evenement) => {
    if (evenement.key === 'Enter' || evenement.key === 'Escape') {
      evenement.preventDefault(); // Entrée lancerait le planning : le calendrier est dans son formulaire
      terminer(evenement.key === 'Enter');
    }
  });
  champ.addEventListener('blur', () => terminer(true));
}
