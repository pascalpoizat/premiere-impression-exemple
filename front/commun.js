// Utilitaires partagés par les sections du front.

export const $ = (selecteur) => document.querySelector(selecteur);

// les textes viennent des utilisateurs : on les échappe avant de les insérer dans du HTML (anti-XSS)
export const echapper = (texte) => String(texte ?? '').replace(/[&<>"']/g, (c) => `&#${c.charCodeAt(0)};`);

export function signalerErreur(message = '') {
  $('#erreur').textContent = message;
}

/** Exécute une action asynchrone en affichant son éventuelle erreur. */
export async function tenter(action) {
  signalerErreur();
  try {
    return await action();
  } catch (erreur) {
    signalerErreur(erreur.message);
    return undefined;
  }
}

export const pourcentage = (valeur) => `${Math.round(valeur * 100)} %`;

/** Le profil courant est partagé entre sections via un événement du document. */
export const EVENEMENT_PROFIL = 'profil-courant';
