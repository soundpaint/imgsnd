#
# $Source: /home/reuter/archive/project/ImageSound/IS/src/examples/MIDI2PNM/Makefile.rca $
# $Revision: 1.2 $
# $Aliases: beta1,beta2 $
# $Author: reuter $
# $Date: Mon Jun 29 00:19:53 1998 $
# $State: Experimental $
#

all: trailer
	cd au ; make all
	cd wav ; make all

clean:
	- rm *.pnm
	cd au ; make clean
	cd wav ; make clean

veryclean:
	make clean
	- rm *~
	cd au ; make veryclean
	cd wav ; make veryclean

.SUFFIXES: $(SUFFIXES) .mid .pnm

trailer: trailer.pnm

trailer.pnm: trailer.mid
	- java is.MIDI2PNM -timescale 300000 -pitchscale 10 -pitchdecay -0.00 \
	-pitchref 64 -out trailer.pnm trailer.mid
