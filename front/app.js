// Point d'entrée : gestion du profil courant, puis initialisation de chaque section.
import * as api from './api.js';
import { $, tenter, EVENEMENT_PROFIL } from './commun.js';
import { initialiserCandidatures } from './candidature.js';
import { initialiserTenue } from './tenue.js';
import { initialiserRevision } from './revision.js';

let profils = [];

function annoncerProfilCourant() {
  const profil = profils.find((p) => String(p.id) === $('#choix-profil').value) ?? null;
  document.dispatchEvent(new CustomEvent(EVENEMENT_PROFIL, { detail: profil }));
}

async function chargerProfils(idAChoisir) {
  profils = await api.listerProfils();
  $('#choix-profil').replaceChildren(...profils.map((p) => new Option(p.pseudo, p.id)));
  if (idAChoisir) $('#choix-profil').value = idAChoisir;
  annoncerProfilCourant();
}

async function creerProfil(evenement) {
  evenement.preventDefault();
  const formulaire = evenement.target;
  const profil = await tenter(() => api.creerProfil(Object.fromEntries(new FormData(formulaire))));
  if (profil) {
    formulaire.reset();
    formulaire.closest('details').open = false;
    await chargerProfils(profil.id);
  }
}

initialiserCandidatures();
initialiserTenue();
initialiserRevision();
$('#choix-profil').addEventListener('change', annoncerProfilCourant);
$('#form-profil').addEventListener('submit', creerProfil);
chargerProfils().catch(() => {
  $('#erreur').textContent = `API injoignable (${api.API_URL}) : le back est-il lancé ? `
    + 'La page est-elle ouverte sur http://localhost:8000 ? (voir front/README.md)';
});
