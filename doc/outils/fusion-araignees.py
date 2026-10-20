"""Fusionne plusieurs araignées « part IA » (une par livrable) en une araignée de projet.

Usage : python3 fusion-araignees.py sortie.json entree1.json entree2.json ...

Chaque entrée a un « poids » (par défaut 1) : la part IA du projet est, sur chaque axe,
la moyenne des parts IA pondérée par ces poids. Le poids est un choix d'équipe à justifier
(par exemple le nombre de lignes produites, ou le temps passé, par livrable).
Les axes et les plafonds doivent être identiques dans toutes les entrées.
"""
import json
import sys


def charger(chemin):
    with open(chemin, encoding="utf-8") as fichier:
        return json.load(fichier)


def verifier_memes_axes(reference, autre, chemin):
    cles = [(a["nom"], a["niveau"], a["plafond"]) for a in reference["axes"]]
    if cles != [(a["nom"], a["niveau"], a["plafond"]) for a in autre["axes"]]:
        sys.exit(f"{chemin} : axes ou plafonds différents de ceux de la première entrée")


def fusionner(entrees):
    reference = entrees[0][1]
    for chemin, entree in entrees[1:]:
        verifier_memes_axes(reference, entree, chemin)
    poids_total = sum(entree.get("poids", 1) for _, entree in entrees)
    axes = []
    for i, axe in enumerate(reference["axes"]):
        somme = sum(entree.get("poids", 1) * entree["axes"][i]["ia"] for _, entree in entrees)
        axes.append({**axe, "ia": round(somme / poids_total)})
    return {
        "titre": "Part IA par compétence — projet (fusion)",
        "commentaire": "Généré par outils/fusion-araignees.py, ne pas modifier à la main. Sources : "
                       + ", ".join(f"{chemin} (poids {entree.get('poids', 1)})" for chemin, entree in entrees),
        "axes": axes,
    }


def main(sortie, chemins):
    projet = fusionner([(chemin, charger(chemin)) for chemin in chemins])
    with open(sortie, "w", encoding="utf-8") as fichier:
        json.dump(projet, fichier, ensure_ascii=False, indent=2)
        fichier.write("\n")


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2:])
