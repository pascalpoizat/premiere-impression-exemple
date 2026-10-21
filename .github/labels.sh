#!/usr/bin/env bash
# Crée ou met à jour les labels d'issues du dépôt (voir CONTRIBUTING.md, « Issues et labels »).
# « Use this template » ne copie pas les labels : chaque équipe lance ce script une fois, depuis la racine du dépôt.
# Idempotent : le relancer ne change rien. Demande la CLI GitHub (gh) connectée.
set -euo pipefail

existe() {
  gh label list --limit 200 --json name --jq '.[].name' | grep -Fxq "$1"
}

# Renomme un label par défaut de GitHub plutôt que d'en créer un nouveau : les issues qui le portent le gardent.
renommer() {
  local ancien="$1" nouveau="$2"
  if existe "$ancien" && ! existe "$nouveau"; then
    gh label edit "$ancien" --name "$nouveau"
  fi
}

# Type : teintes pâles. Priorité : teintes vives. Une issue porte un label de chaque axe.
renommer 'feature' 'type: feature'
renommer 'enhancement' 'type: amélioration'
renommer 'bug' 'type: bugfix'
renommer 'documentation' 'type: doc'
gh label create 'type: feature' --color c2e0c6 --description 'Une feature complète (BD, back, API, front), portée par une personne, avec un algorithme' --force
gh label create 'type: amélioration' --color c5def5 --description 'Amélioration sans nouvelle feature : saisie, visualisation, optimisation, refactoring' --force
gh label create 'type: bugfix' --color f9d0c4 --description 'Correction d'"'"'un comportement incorrect' --force
gh label create 'type: chore' --color d4d4d4 --description 'Tâche technique sans effet sur l'"'"'application : outillage, configuration, dépendances' --force
gh label create 'type: doc' --color ffffff --description 'Documentation seule' --force
gh label create 'priorité: important' --color b60205 --description 'À traiter en premier' --force
gh label create 'priorité: moyen' --color fb8c00 --description 'À traiter dans l'"'"'itération' --force
gh label create 'priorité: faible' --color fbca04 --description 'À traiter si le temps le permet' --force

# Les autres labels commencent par « ~ ».
renommer 'duplicate' '~duplicate'
renommer 'invalid' '~invalid'
renommer 'wontfix' '~wontfix'
renommer 'question' '~question'
gh label create '~duplicate' --color cfd3d7 --description 'Doublon d'"'"'une issue ou d'"'"'une PR existante' --force
gh label create '~invalid' --color e4e669 --description 'Demande non valide' --force
gh label create '~wontfix' --color ededed --description 'Ne sera pas traité' --force
gh label create '~question' --color d876e3 --description 'Demande d'"'"'information' --force

# Labels par défaut de GitHub inutiles ici.
for inutile in 'good first issue' 'help wanted' 'accessibility'; do
  if existe "$inutile"; then
    gh label delete "$inutile" --yes
  fi
done
