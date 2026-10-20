// Client de l'API REST : seul module du front qui connaît les URL du back.
export const API_URL = 'http://localhost:8080/api/v1';

async function appeler(chemin, options = {}) {
  const reponse = await fetch(`${API_URL}${chemin}`, {
    headers: { 'Content-Type': 'application/json' },
    ...options,
  });
  const corps = await reponse.json().catch(() => null);
  if (!reponse.ok) {
    // erreurs au format problem+json (RFC 9457)
    throw new Error(corps?.detail ?? corps?.title ?? `Erreur HTTP ${reponse.status}`);
  }
  return corps;
}

const envoyer = (methode, chemin, corps) => appeler(chemin, { method: methode, body: JSON.stringify(corps) });

// F1 : profils, candidatures, analyse d'annonce
export const listerProfils = () => appeler('/profils');
export const creerProfil = (profil) => envoyer('POST', '/profils', profil);
export const listerCandidatures = (profilId) => appeler(`/candidatures?profilId=${profilId}`);
export const creerCandidature = (candidature) => envoyer('POST', '/candidatures', candidature);
export const analyserAnnonce = (candidatureId, k) => appeler(`/candidatures/${candidatureId}/analyse?k=${k}`);

// F2 : mensurations, vêtements, ajustement morphologique
export const lireMensurations = (profilId) => appeler(`/profils/${profilId}/mensurations`);
export const enregistrerMensurations = (profilId, mensurations) =>
  envoyer('PUT', `/profils/${profilId}/mensurations`, mensurations);
export const typesDeVetements = () => appeler('/vetements/types');
export const declarerVetement = (vetement) => envoyer('POST', '/vetements', vetement);
export const vetementsCompatibles = (profilId, k, type) =>
  appeler(`/profils/${profilId}/vetements-compatibles?k=${k}${type ? `&type=${type}` : ''}`);
