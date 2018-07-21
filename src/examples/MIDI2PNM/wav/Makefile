#
# $Source: /home/reuter/archive/project/ImageSound/IS/src/examples/MIDI2PNM/wav/Makefile.rca $
# $Revision: 1.1 $
# $Aliases: beta2 $
# $Author: reuter $
# $Date: Sat Jun 27 15:20:04 1998 $
# $State: Experimental $
#

all: trailer

clean:
	- rm *.sam *.wav

veryclean:
	make clean
	- rm *~

.SUFFIXES: $(SUFFIXES) .sam .wav

trailer: trailer.sam trailer.wav

trailer.sam: ../trailer.pnm
	- java is.ImageSound -rate 44100 -minfreq 200 -spctdist 10 \
		-out trailer.sam ../trailer.pnm

trailer.wav: trailer.sam
	- java is.Sam2Any -type wav -out trailer.wav trailer.sam
