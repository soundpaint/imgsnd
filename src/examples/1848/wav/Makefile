#
# $Source: /home/reuter/archive/project/ImageSound/IS/src/examples/1848/wav/Makefile.rca $
# $Revision: 1.2 $
# $Aliases: beta2 $
# $Author: reuter $
# $Date: Tue Jul 21 23:57:39 1998 $
# $State: Experimental $
#

SOURCES	=	Verdruss.pnm Diskussion.pnm Aufmarsch.pnm Kampf.pnm \
			Niederlage.pnm Flucht.pnm Reprise.pnm \
			Freiheit.pnm heckerlied.pnm Die\ Gedanken.pnm

OBJECTS = $(SOURCES:.pnm=.wav) $(SOURCES:.pnm=.sam)

all: $(OBJECTS)

clean:
	- rm *.sam *.wav

veryclean:
	make clean
	- rm *~

.SUFFIXES: $(SUFFIXES) .sam .wav

%.sam: ../%.pnm
	- java is.ImageSound -rate 44100 -minfreq 20 -maxfreq 4000 -out $(@) $<

%.wav: %.sam
	- java is.Sam2Any -type wav -out $(@) $<

heckerlied.sam: ../heckerlied.pnm
	- java is.ImageSound -rate 44100 -minfreq 130.8127827 -spctdist 10 \
		-length 60 -out $(@) $<

heckerlied2.sam: ../heckerlied2.pnm
	- java is.ImageSound -rate 44100 -minfreq 523.2511306 -spctdist 10 \
		-length 60 -out $(@) $<

Die\ Gedanken.sam: ../Die\ Gedanken.pnm
	- java is.ImageSound -rate 44100 -minfreq 130.8127827 -spctdist 10 \
		-length 25 -out "Die Gedanken.sam" "../Die Gedanken.pnm"

Die\ Gedanken.wav: Die\ Gedanken.sam
	- java is.Sam2Any -type wav -out "Die Gedanken.wav" "Die Gedanken.sam"

Verdruss.sam: ../Verdruss.pnm
	-java is.ImageSound -rate 44100 -minfreq 200 -maxfreq 2000 -length 25 \
		-out $(@) $<

Diskussion.sam: ../Diskussion.pnm
	-java is.ImageSound -rate 44100 -minfreq 200 -maxfreq 2000 -length 25 \
		-out $(@) $<

Freiheit.sam: ../Freiheit.pnm
	-java is.ImageSound -rate 44100 -minfreq 200 -maxfreq 2000 -length 25 \
		-out $(@) $<

Aufmarsch.sam: ../Aufmarsch.pnm
	-java is.ImageSound -rate 44100 -minfreq 65.4063913 -spctdist 10 \
		-length 40 -out $(@) $<

Kampf.sam: ../Kampf.pnm
	-java is.ImageSound -rate 44100 -minfreq 200 -maxfreq 2000 -length 35 \
		-out $(@) $<

Niederlage.sam: ../Niederlage.pnm
	-java is.ImageSound -rate 44100 -minfreq 523.2511306 -spctdist 10 \
		-length 25 -out $(@) $<

Flucht.sam: ../Flucht.pnm
	-java is.ImageSound -rate 44100 -minfreq 200 -maxfreq 2000 -length 25 \
		-out $(@) $<

Reprise.sam: ../Reprise.pnm
	-java is.ImageSound -rate 44100 -minfreq 65.4063913 -spctdist 10 \
		-length 35 -out $(@) $<
