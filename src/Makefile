#
# $Source: /home/reuter/archive/project/ImageSound/IS/src/Makefile.rca $
# $Revision: 1.2 $
# $Aliases: beta1,beta2 $
# $Author: reuter $
# $Date: Mon Jun 29 00:19:47 1998 $
# $State: Experimental $
#

include defs.mak

#
# Packages
#

PACKAGES	= midi is soundedit

#
# Source Files
#

JAVA_MIDI	= $(SRC_ROOT)/midi
JAVA_IS		= $(SRC_ROOT)/is
JAVA_SOUNDEDIT	= $(SRC_ROOT)/soundedit

JAVA =	$(JAVA_MIDI)/ListElement.java \
	$(JAVA_MIDI)/List.java \
	$(JAVA_MIDI)/MIDIEvent.java \
	$(JAVA_MIDI)/SMFEvent.java \
	$(JAVA_MIDI)/SMFReader.java \
	$(JAVA_MIDI)/SMFEventChatter.java \
	$(JAVA_MIDI)/SMFReaderChatter.java \
	$(JAVA_MIDI)/GM.java \
	$(JAVA_IS)/ProgressDisplay.java \
	$(JAVA_IS)/AudioOutputStream.java \
	$(JAVA_IS)/SunAudioOutputStream.java \
	$(JAVA_IS)/WavAudioOutputStream.java \
	$(JAVA_IS)/PNMReader.java \
	$(JAVA_IS)/SynthNative.java \
	$(JAVA_IS)/Synth.java \
	$(JAVA_IS)/Sam2Any.java \
	$(JAVA_IS)/MIDI2PNM.java \
	$(JAVA_IS)/ImageSound.java \
	$(JAVA_SOUNDEDIT)/SampleInputStream.java \
	$(JAVA_SOUNDEDIT)/AudioOut.java \
	$(JAVA_SOUNDEDIT)/SunAudioStream.java \
	$(JAVA_SOUNDEDIT)/SoundEdit.java

#
# Target Files
#

CLASS_MIDI	= $(CLS_ROOT)/midi
CLASS_IS	= $(CLS_ROOT)/is
CLASS_SOUNDEDIT	= $(CLS_ROOT)/soundedit

CLASS =	$(CLASS_MIDI)/ListElement.class \
	$(CLASS_MIDI)/List.class \
	$(CLASS_MIDI)/MIDIEvent.class \
	$(CLASS_MIDI)/SMFEvent.class \
	$(CLASS_MIDI)/SMFReader.class \
	$(CLASS_MIDI)/SMFEventChatter.class \
	$(CLASS_MIDI)/SMFReaderChatter.class \
	$(CLASS_MIDI)/GM.class \
	$(CLASS_IS)/ProgressDisplay.class \
	$(CLASS_IS)/AudioOutputStream.class \
	$(CLASS_IS)/SunAudioOutputStream.class \
	$(CLASS_IS)/WavAudioOutputStream.class \
	$(CLASS_IS)/PNMReader.class \
	$(CLASS_IS)/SynthNative.class \
	$(CLASS_IS)/Synth.class \
	$(CLASS_IS)/Sam2Any.class \
	$(CLASS_IS)/MIDI2PNM.class \
	$(CLASS_IS)/ImageSound.class \
	$(CLASS_SOUNDEDIT)/SampleInputStream.class \
	$(CLASS_SOUNDEDIT)/AudioOut.class \
	$(CLASS_SOUNDEDIT)/SunAudioStream.class \
	$(CLASS_SOUNDEDIT)/SoundEdit.class

#
# Rules
#

all:
	make classes
	make libsynth
	make doc
	make lib
	make examples

libsynth: classes
	cd libsynth ; make all

examples: FORCE
	cd examples ; make all

classes: $(CLASS)

doc: classes
	$(JRE_HOME)/bin/javadoc -d $(DOC_DIR) -classpath $(CLASSPATH) \
		$(DOC_OTHER) $(PACKAGES)

lib: classes
	cd $(CLS_ROOT); $(JRE_HOME)/bin/jar -0cvf $(LIB_DIR)/classes.zip .

depend:
	cd libsynth ; make depend

tags:
	etags *.java

clean:
	- rm -rf $(CLS_ROOT)/*
	- cd examples ; make clean
	- cd libsynth ; make clean

veryclean:
	- rm -rf $(CLS_ROOT)/*
	- rm -f *~ *.bak
	- find $(SRC_ROOT) -name \*~ -exec /bin/rm -f {} \; -print
	- rm -f $(DOC_DIR)/*.html
	- rm -f $(LIB_DIR)/classes.zip
	- cd examples ; make veryclean
	cd libsynth ; make veryclean

execclean:
	cd libsynth ; make execclean

tgz:
	- cd .. ; mv -f src.tgz src.tgz.bak
	cd .. ; tar -cvf - --exclude=\*.wav --exclude=\*.au \
		--exclude=\*.sam --exclude=\*~ --exclude=\*.ps \
		--exclude=\*.dvi --exclude=\*.aux --exclude=\*.log \
		src | gzip --best > src.tgz

.SUFFIXES: $(SUFFIXES) .java .class

$(CLS_ROOT)%class: $(SRC_ROOT)%java
	$(JRE_HOME)/bin/javac -d $(CLS_ROOT) -classpath $(CLASSPATH) $<

FORCE:
