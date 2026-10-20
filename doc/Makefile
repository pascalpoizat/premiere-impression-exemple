# Compile le dossier en PDF : Markdown -> HTML (pandoc) -> PDF (WeasyPrint), mise en page dans style.css
# Prérequis : pandoc et weasyprint (pip install weasyprint)
PANDOC_OPTS = --standalone --css=style.css --pdf-engine=weasyprint -V document-css=false -M lang=fr

dossier.pdf: dossier.md style.css part-ia.svg
	pandoc dossier.md -o $@ $(PANDOC_OPTS)

# graphe en araignée « part IA / part humaine », généré depuis part-ia.json
part-ia.svg: part-ia.json outils/araignee.py
	python3 outils/araignee.py part-ia.json $@

# page de projet (GitHub Pages) : site/ + dernière version du dossier, assemblés dans build/site
site: dossier.pdf part-ia.svg
	rm -rf ../build/site && mkdir -p ../build/site
	cp -R ../site/. ../build/site/
	cp dossier.pdf part-ia.svg ../build/site/

.PHONY: clean site
clean:
	rm -f dossier.pdf part-ia.svg
