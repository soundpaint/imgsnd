#
# $Source: /home/reuter/archive/project/ImageSound/IS/src/examples/MIDI2PNM/au/Makefile.rca $
# $Revision: 1.2 $
# $Aliases: beta1,beta2 $
# $Author: reuter $
# $Date: Mon Jun 29 00:19:53 1998 $
# $State: Experimental $
#

all: trailer

clean:
	- rm *.sam *.au

veryclean:
	make clean
	- rm *~

.SUFFIXES: $(SUFFIXES) .sam .au

trailer: trailer.sam trailer.au

trailer.sam: ../trailer.pnm
	- java is.ImageSound -rate 8000 -minfreq 200 -spctdist 10 \
		-out trailer.sam ../trailer.pnm

trailer.au: trailer.sam
	- java is.Sam2Any -type au -out trailer.au trailer.sam
