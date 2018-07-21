giffiles	=	Verdruss.gif Flucht.gif Diskussion.gif Freiheit.gif \
			Aufmarsch.gif Kampf.gif Reprise.gif Niederlage.gif
midifiles	=	heckerlied.mid Die\ Gedanken.mid

gifobjs		=	$(giffiles:.gif=.pnm)
midobjs		=	$(midifiles:.mid=.pnm)
bookletinc	=	$(giffiles:.gif=.ps)

OBJECTS = $(midobjs) $(gifobjs) booklet

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

.SUFFIXES: $(SUFFIXES) .mid .gif .ppm .tex .dvi .ps

booklet: booklet.ps

booklet.ps: booklet.dvi
	dvips -o booklet.ps booklet.dvi

booklet.dvi: booklet.tex $(bookletinc)
	latex booklet

%.ps: %.pnm
	- pnmtops $< > $(@)

%.pnm: %.gif
	- giftopnm -verbose $< > $(@)

heckerlied.pnm: heckerlied.mid
	- java is.MIDI2PNM -timescale 300000 -pitchscale 10 \
		-pitchdecay -0.004 -pitchref 64 -velocscale 1.0 \
		-out $(@) $<

Die\ Gedanken.pnm: Die\ Gedanken.mid
	- java is.MIDI2PNM -timescale 300000 -pitchscale 10 \
		-pitchref 64 -velocscale 1.0 \
		-out "Die Gedanken.pnm" "Die Gedanken.mid"
