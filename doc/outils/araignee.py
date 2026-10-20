"""Génère le graphe en araignée « part IA / part humaine » du dossier.

Usage : python3 araignee.py part-ia.json part-ia.svg

Les axes de bas niveau sont placés sur la moitié droite, ceux de haut niveau
sur la moitié gauche ; on compare la part IA réelle au plafond que l'équipe s'autorise.
"""
import json
import math
import sys
from html import escape

ACCENT = "#5b45d6"      # part IA
PLAFOND = "#1f9d6b"     # plafond visé
ENCRE = "#1b1f2e"
DISCRET = "#7a8094"
FILET = "#e3e5ec"
FOND = {"bas": "#f4f2fe", "haut": "#eef8f3"}
POLICE = "Inter, 'Helvetica Neue', Helvetica, Arial, sans-serif"

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


def moyenne(axes, niveau, cle):
    valeurs = [a[cle] for a in axes if a["niveau"] == niveau]
    return round(sum(valeurs) / len(valeurs))


def araignee(axes):
    angles_axes = angles(len(axes))
    svg = []
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
    # séparation verticale et titres des deux familles
    svg.append(f'<line x1="{CX}" y1="{CY - RAYON - 22}" x2="{CX}" y2="{CY + RAYON + 22}" stroke="{DISCRET}" stroke-width="0.8" stroke-dasharray="2 3"/>')
    svg.append(texte(CX - 8, 18, "HAUT NIVEAU", 7.5, 700, PLAFOND, "end", 'letter-spacing="0.8"'))
    svg.append(texte(CX + 8, 18, "BAS NIVEAU", 7.5, 700, ACCENT, "start", 'letter-spacing="0.8"'))
    # plafond (pointillés) puis part IA réelle (surface)
    svg.append(f'<polygon points="{polygone([a["plafond"] for a in axes], angles_axes)}" fill="none" '
               f'stroke="{PLAFOND}" stroke-width="1.6" stroke-dasharray="4 3"/>')
    svg.append(f'<polygon points="{polygone([a["ia"] for a in axes], angles_axes)}" fill="{ACCENT}" '
               f'fill-opacity="0.22" stroke="{ACCENT}" stroke-width="1.8" stroke-linejoin="round"/>')
    for axe, angle in zip(axes, angles_axes):
        x, y = point(angle, RAYON * axe["ia"] / 100)
        svg.append(f'<circle cx="{x:.1f}" cy="{y:.1f}" r="2.3" fill="{ACCENT}"/>')
    return svg


def legende(axes):
    x0, y = 425, 30
    svg = [texte(x0, y, "Part IA par compétence", 11, 700)]
    y += 16
    svg.append(f'<line x1="{x0}" y1="{y}" x2="{x0 + 18}" y2="{y}" stroke="{ACCENT}" stroke-width="6" stroke-opacity="0.5"/>')
    svg.append(texte(x0 + 24, y + 3, "part IA réelle", 7.5, 500, DISCRET))
    svg.append(f'<line x1="{x0 + 100}" y1="{y}" x2="{x0 + 118}" y2="{y}" stroke="{PLAFOND}" stroke-width="1.6" stroke-dasharray="4 3"/>')
    svg.append(texte(x0 + 124, y + 3, "plafond que l'équipe s'autorise", 7.5, 500, DISCRET))
    for niveau, titre in (("haut", "Haut niveau"), ("bas", "Bas niveau")):
        y += 24
        couleur = PLAFOND if niveau == "haut" else ACCENT
        svg.append(texte(x0, y, f"{titre} : IA {moyenne(axes, niveau, 'ia')} % en moyenne "
                                f"(plafond {moyenne(axes, niveau, 'plafond')} %)", 8.5, 700, couleur))
        famille = [a for a in axes if a["niveau"] == niveau]
        if niveau == "haut":
            famille.reverse()  # ordre du cycle de vie : de l'idéation à la validation
        for axe in famille:
            y += 14
            depasse = axe["ia"] > axe["plafond"]
            svg.append(texte(x0, y, axe["nom"], 8, 500, ENCRE))
            # barre : part IA (violet) sur fond part humaine (vert pâle), repère du plafond
            bx, largeur = x0 + 78, 150
            svg.append(f'<rect x="{bx}" y="{y - 6}" width="{largeur}" height="6" rx="3" fill="#d9f2e3"/>')
            svg.append(f'<rect x="{bx}" y="{y - 6}" width="{largeur * axe["ia"] / 100:.1f}" height="6" rx="3" fill="{ACCENT}"/>')
            px = bx + largeur * axe["plafond"] / 100
            svg.append(f'<line x1="{px:.1f}" y1="{y - 8.5}" x2="{px:.1f}" y2="{y + 2.5}" stroke="{PLAFOND}" stroke-width="1.6"/>')
            svg.append(texte(bx + largeur + 8, y, f'{axe["ia"]} %', 8, 700, "#c0392b" if depasse else ACCENT))
    return svg


def main(source, cible):
    with open(source, encoding="utf-8") as fichier:
        axes = json.load(fichier)["axes"]
    corps = araignee(axes) + legende(axes)
    svg = (f'<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 {LARGEUR} {HAUTEUR}" '
           f'width="{LARGEUR}" height="{HAUTEUR}">\n' + "\n".join(corps) + "\n</svg>\n")
    with open(cible, "w", encoding="utf-8") as fichier:
        fichier.write(svg)


if __name__ == "__main__":
    main(sys.argv[1], sys.argv[2])
