#
# $Source: /home/reuter/archive/project/ImageSound/IS/src/examples/gif/wav/Makefile.rca $
# $Revision: 1.1 $
# $Aliases: beta2 $
# $Author: reuter $
# $Date: Sat Jun 27 15:17:03 1998 $
# $State: Experimental $
#

SOURCES	=	ball.pnm bouboule.pnm circle.pnm demon.pnm dots1.pnm \
		dots2.pnm helix1.pnm helix2.pnm helix3.pnm \
		rev-ball.pnm ramp.pnm vib.pnm

OBJECTS	=	$(SOURCES:.pnm=.wav) $(SOURCES:.pnm=.sam)

all: $(OBJECTS)

clean:
	- rm *.sam *.wav

veryclean:
	make clean
	- rm *~

.SUFFIXES: $(SUFFIXES) .sam .wav

%.sam: ../%.pnm
	- java -classpath $(CLASSPATH) is.ImageSound -rate 44100 -minfreq 20 \
		-maxfreq 4000 -out $(@) $<

%.wav: %.sam
	- java -classpath $(CLASSPATH) is.Sam2Any -type wav -out $(@) $<
