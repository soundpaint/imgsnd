#
# $Source: /home/reuter/archive/project/ImageSound/IS/src/examples/cd-cover/Makefile.rca $
# $Revision: 1.1 $
# $Aliases: beta2 $
# $Author: reuter $
# $Date: Sat Jun 27 15:21:14 1998 $
# $State: Experimental $
#

OBJECTS = cd-cover.1848.ps cd-booklet.1848.ps

.SUFFIXES: $(SUFFIXES) .jpeg .gif .fig .eps .ps

all: $(OBJECTS)

clean:
	- rm cd-cover.1848.ps cd-booklet.1848.ps cd-logo.eps sfem-logo.eps

veryclean:
	make clean
	- rm *~

cd-cover.1848.ps: cd-cover.1848.fig cd-logo.eps sfem-logo.eps frbild.jpeg
	fig2dev -L ps -p portrait -P cd-cover.1848.fig $(@)

cd-booklet.1848.ps: cd-booklet.1848.fig cd-logo.eps sfem-logo.eps \
		frbild.jpeg frbild.light.gif
	fig2dev -L ps -l landscape -P cd-booklet.1848.fig $(@)

cd-logo.eps: cd-logo.fig
	fig2dev -L ps -p portrait $< $(@)

sfem-logo.eps: sfem-logo.fig
	fig2dev -L ps -p portrait $< $(@)
