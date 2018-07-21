#
# $Source: /home/reuter/archive/project/ImageSound/IS/src/examples/gif/Makefile.rca $
# $Revision: 1.1 $
# $Aliases: beta2 $
# $Author: reuter $
# $Date: Sat Jun 27 15:16:18 1998 $
# $State: Experimental $
#

SOURCES	=	ball.gif bouboule.gif circle.gif demon.gif dots1.gif \
		dots2.gif helix1.gif helix2.gif helix3.gif \
		rev-ball.gif ramp.gif vib.gif

OBJECTS	=	$(SOURCES:.gif=.pnm) booklet

bookletinc	=	$(SOURCES:.gif=.ps)

all: $(OBJECTS)
	cd au ; make all
	cd wav ; make all

clean:
	- rm booklet.aux booklet.dvi booklet.log booklet.ps
	- rm *.pnm *.ps
	cd au ; make clean
	cd wav ; make clean

veryclean:
	make clean
	- rm *~
	cd au ; make veryclean
	cd wav ; make veryclean

.SUFFIXES: $(SUFFIXES) .gif .pnm .pbm .pgm .ppm .sam .au .tex .dvi .ps

booklet: booklet.ps

booklet.ps: booklet.dvi
	dvips -o booklet.ps booklet.dvi

booklet.dvi: booklet.tex $(bookletinc)
	latex booklet

%.ps: %.pnm
	- pnmtops $< > $(@)

%.pnm: %.gif
	- giftopnm -verbose $< > $(@)
