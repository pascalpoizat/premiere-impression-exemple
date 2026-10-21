// Navigation par onglets : l'onglet ouvert est dans l'URL (#candidatures, #tenue, #revision),
// si bien qu'un rechargement ou un lien ramène au même onglet.

const sections = () => [...document.querySelectorAll('section[data-onglet]')];

function afficherOngletCourant() {
  const demande = location.hash.slice(1);
  const nomsConnus = sections().map((section) => section.dataset.onglet);
  // ancre absente ou inconnue : premier onglet
  const courant = nomsConnus.includes(demande) ? demande : nomsConnus[0];
  sections().forEach((section) => { section.hidden = section.dataset.onglet !== courant; });
  document.querySelectorAll('.onglets a').forEach((lien) => {
    if (lien.hash === `#${courant}`) lien.setAttribute('aria-current', 'page');
    else lien.removeAttribute('aria-current');
  });
}

export function initialiserOnglets() {
  window.addEventListener('hashchange', afficherOngletCourant);
  afficherOngletCourant();
}
