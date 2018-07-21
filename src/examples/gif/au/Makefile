#
# $Source: /home/reuter/archive/project/ImageSound/IS/src/examples/gif/au/Makefile.rca $
# $Revision: 1.2 $
# $Aliases: beta1,beta2 $
# $Author: reuter $
# $Date: Mon Jun 29 00:19:50 1998 $
# $State: Experimental $
#

SOURCES	=	ball.pnm bouboule.pnm circle.pnm demon.pnm dots1.pnm \
		dots2.pnm helix1.pnm helix2.pnm helix3.pnm \
		rev-ball.pnm ramp.pnm vib.pnm

OBJECTS	=	$(SOURCES:.pnm=.au) $(SOURCES:.pnm=.sam)

all: $(OBJECTS)

clean:
	- rm *.sam *.au

veryclean:
	make clean
	- rm *~

.SUFFIXES: $(SUFFIXES) .sam .au

%.sam: ../%.pnm
	- java -classpath $(CLASSPATH) is.ImageSound -rate 8000 -minfreq 20 \
		-maxfreq 4000 -out $(@) $<

%.au: %.sam
	- java -classpath $(CLASSPATH) is.Sam2Any -type au -out $(@) $<
