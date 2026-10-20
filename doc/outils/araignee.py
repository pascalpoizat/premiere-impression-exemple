"""Génère le graphe en araignée « part IA / part humaine » du dossier.

Usage : python3 araignee.py part-ia.json part-ia.svg [--representation=mixte|humain|ia]

Le JSON donne, par compétence, la part IA mesurée et le plafond que l'équipe s'autorise.
Chaque moitié du graphe peut montrer, au choix (REPRESENTATION ci-dessous ou option) :
- « humain » : la part humaine (100 − part IA) face à un plancher (100 − plafond), à dépasser ;
- « ia » : la part IA face au plafond, à ne pas dépasser.
Par défaut (« mixte »), le haut niveau, où l'humain doit dominer, montre la part humaine,
et le bas niveau, où l'IA peut dominer : chaque moitié reste ainsi lisible loin du centre.
Attention : un même rayon ne veut donc pas dire la même chose des deux côtés.
Les axes de bas niveau sont placés sur la moitié droite, ceux de haut niveau sur la moitié gauche.
"""
import json
import math
import sys
from html import escape

# ce que montre chaque moitié : "humain" ou "ia"
REPRESENTATIONS = {
    "mixte": {"haut": "humain", "bas": "ia"},
    "humain": {"haut": "humain", "bas": "humain"},
    "ia": {"haut": "ia", "bas": "ia"},
}
REPRESENTATION = "mixte"

COULEUR = {"humain": "#1f9d6b", "ia": "#5b45d6"}      # surface et barres
FOND_BARRE = {"humain": "#e4dffb", "ia": "#d9f2e3"}   # le reste de la barre : l'autre part
OBJECTIF = "#1b1f2e"    # plancher ou plafond visé
ALERTE = "#c0392b"      # objectif non tenu
ENCRE = "#1b1f2e"
DISCRET = "#7a8094"
FILET = "#e3e5ec"
FOND = {"bas": "#f4f2fe", "haut": "#eef8f3"}
POLICE = "Inter, 'Helvetica Neue', Helvetica, Arial, sans-serif"
LIBELLE = {"humain": ("part humaine", "plancher"), "ia": ("part IA", "plafond")}

CX, CY, RAYON = 195, 152, 108
LARGEUR, HAUTEUR = 720, 296


def point(angle_deg, rayon):
    angle = math.radians(angle_deg)
    return CX + rayon * math.cos(angle), CY + rayon * math.sin(angle)


def angles(nombre):
    # décalage d'un demi-pas : la séparation haut niveau / bas niveau est verticale
    pas = 360 / nombre
    return [-90 + (i + 0.5) * pas for i in range(nombre)]


def polygone(valeurs, angles_axes):
    return " ".join("%.1f,%.1f" % point(a, RAYON * v / 100) for v, a in zip(valeurs, angles_axes))


def texte(x, y, contenu, taille=9, poids=500, couleur=ENCRE, ancre="start", extra=""):
    return (f'<text x="{x:.1f}" y="{y:.1f}" font-family="{POLICE}" font-size="{taille}" '
            f'font-weight="{poids}" fill="{couleur}" text-anchor="{ancre}" {extra}>{escape(contenu)}</text>')


def valeur(axe, mesure):
    return 100 - axe["ia"] if mesure == "humain" else axe["ia"]


def objectif(axe, mesure):
    return 100 - axe["plafond"] if mesure == "humain" else axe["plafond"]


def tenu(axe):
    # le même critère quelle que soit la représentation : part IA au plus le plafond
    return axe["ia"] <= axe["plafond"]


def moyenne(axes, niveau, mesure, fonction):
    valeurs = [fonction(a, mesure) for a in axes if a["niveau"] == niveau]
    return round(sum(valeurs) / len(valeurs))


def araignee(axes, representation):
    angles_axes = angles(len(axes))
    mesures = [representation[a["niveau"]] for a in axes]
    svg = ['<defs>'
           f'<clipPath id="moitie-haut"><rect x="0" y="0" width="{CX}" height="{HAUTEUR}"/></clipPath>'
           f'<clipPath id="moitie-bas"><rect x="{CX}" y="0" width="{LARGEUR}" height="{HAUTEUR}"/></clipPath>'
           '</defs>']
    # fonds des deux moitiés
    svg.append(f'<path d="M{CX},{CY - RAYON - 6} A{RAYON + 6},{RAYON + 6} 0 0 1 {CX},{CY + RAYON + 6} Z" fill="{FOND["bas"]}"/>')
    svg.append(f'<path d="M{CX},{CY - RAYON - 6} A{RAYON + 6},{RAYON + 6} 0 0 0 {CX},{CY + RAYON + 6} Z" fill="{FOND["haut"]}"/>')
    # anneaux 25 / 50 / 75 / 100 %
    for pourcentage in (25, 50, 75, 100):
        anneau = polygone([pourcentage] * len(axes), angles_axes)
        svg.append(f'<polygon points="{anneau}" fill="none" stroke="{FILET}" stroke-width="0.8"/>')
        x, y = point(-90, RAYON * pourcentage / 100)
        svg.append(texte(x + 3, y + 3, f"{pourcentage}", 6.5, 500, DISCRET))
    # rayons et libellés des axes
    for axe, angle in zip(axes, angles_axes):
        x, y = point(angle, RAYON)
        svg.append(f'<line x1="{CX}" y1="{CY}" x2="{x:.1f}" y2="{y:.1f}" stroke="{FILET}" stroke-width="0.8"/>')
        lx, ly = point(angle, RAYON + 14)
        ancre = "start" if math.cos(math.radians(angle)) > 0.2 else "end" if math.cos(math.radians(angle)) < -0.2 else "middle"
        svg.append(texte(lx, ly + 3, axe["nom"], 8.5, 600, ENCRE, ancre))
    # séparation verticale et titres des deux moitiés, avec ce que chacune montre
    svg.append(f'<line x1="{CX}" y1="{CY - RAYON - 22}" x2="{CX}" y2="{CY + RAYON + 22}" stroke="{DISCRET}" stroke-width="0.8" stroke-dasharray="2 3"/>')
    for niveau, x, ancre in (("haut", CX - 8, "end"), ("bas", CX + 8, "start")):
        mesure = representation[niveau]
        svg.append(texte(x, 14, f"{niveau.upper()} NIVEAU", 7.5, 700, COULEUR[mesure], ancre, 'letter-spacing="0.8"'))
        svg.append(texte(x, 24, LIBELLE[mesure][0], 7, 500, COULEUR[mesure], ancre))
    # surface et objectif, chacun tracé une fois par moitié avec la mesure de cette moitié
    for niveau in ("haut", "bas"):
        mesure = representation[niveau]
        valeurs = [valeur(a, m) for a, m in zip(axes, mesures)]
        objectifs = [objectif(a, m) for a, m in zip(axes, mesures)]
        svg.append(f'<g clip-path="url(#moitie-{niveau})">'
                   f'<polygon points="{polygone(valeurs, angles_axes)}" fill="{COULEUR[mesure]}" fill-opacity="0.22" '
                   f'stroke="{COULEUR[mesure]}" stroke-width="1.8" stroke-linejoin="round"/>'
                   f'<polygon points="{polygone(objectifs, angles_axes)}" fill="none" stroke="{OBJECTIF}" '
                   f'stroke-width="1.4" stroke-dasharray="4 3"/></g>')
    for axe, mesure, angle in zip(axes, mesures, angles_axes):
        x, y = point(angle, RAYON * valeur(axe, mesure) / 100)
        svg.append(f'<circle cx="{x:.1f}" cy="{y:.1f}" r="2.3" fill="{COULEUR[mesure] if tenu(axe) else ALERTE}"/>')
    return svg


def legende(axes, representation):
    x0, y = 425, 30
    titre = ("Part humaine" if set(representation.values()) == {"humain"} else
             "Part IA" if set(representation.values()) == {"ia"} else "Part humaine et part IA")
    svg = [texte(x0, y, f"{titre} par compétence", 11, 700)]
    y += 16
    svg.append(f'<line x1="{x0}" y1="{y}" x2="{x0 + 18}" y2="{y}" stroke="{OBJECTIF}" stroke-width="1.4" stroke-dasharray="4 3"/>')
    svg.append(texte(x0 + 24, y + 3, "objectif de l'équipe (en rouge : non tenu)", 7.5, 500, DISCRET))
    for niveau, titre_niveau in (("haut", "Haut niveau"), ("bas", "Bas niveau")):
        mesure = representation[niveau]
        nom, cible = LIBELLE[mesure]
        y += 24
        svg.append(texte(x0, y, f"{titre_niveau} : {nom} {moyenne(axes, niveau, mesure, valeur)} % en moyenne "
                                f"({cible} {moyenne(axes, niveau, mesure, objectif)} %)", 8.5, 700, COULEUR[mesure]))
        famille = [a for a in axes if a["niveau"] == niveau]
        if niveau == "haut":
            famille.reverse()  # ordre du cycle de vie : de l'idéation à la validation
        for axe in famille:
            y += 14
            svg.append(texte(x0, y, axe["nom"], 8, 500, ENCRE))
            # barre : la part montrée, sur le fond de l'autre part, et le repère de l'objectif
            bx, largeur = x0 + 78, 150
            svg.append(f'<rect x="{bx}" y="{y - 6}" width="{largeur}" height="6" rx="3" fill="{FOND_BARRE[mesure]}"/>')
            svg.append(f'<rect x="{bx}" y="{y - 6}" width="{largeur * valeur(axe, mesure) / 100:.1f}" height="6" rx="3" fill="{COULEUR[mesure]}"/>')
            px = bx + largeur * objectif(axe, mesure) / 100
            svg.append(f'<line x1="{px:.1f}" y1="{y - 8.5}" x2="{px:.1f}" y2="{y + 2.5}" stroke="{OBJECTIF}" stroke-width="1.6"/>')
            svg.append(texte(bx + largeur + 8, y, f'{valeur(axe, mesure)} %', 8, 700, COULEUR[mesure] if tenu(axe) else ALERTE))
    return svg


def main(source, cible, nom_representation=REPRESENTATION):
    representation = REPRESENTATIONS[nom_representation]
    with open(source, encoding="utf-8") as fichier:
        axes = json.load(fichier)["axes"]
    corps = araignee(axes, representation) + legende(axes, representation)
    svg = (f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {LARGEUR} {HAUTEUR}" '
           f'width="{LARGEUR}" height="{HAUTEUR}">\n' + "\n".join(corps) + "\n</svg>\n")
    with open(cible, "w", encoding="utf-8") as fichier:
        fichier.write(svg)


if __name__ == "__main__":
    options = [a for a in sys.argv[3:] if a.startswith("--representation=")]
    main(sys.argv[1], sys.argv[2], options[0].split("=", 1)[1] if options else REPRESENTATION)
