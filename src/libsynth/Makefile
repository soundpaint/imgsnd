#
# $Source: /home/reuter/archive/project/ImageSound/IS/src/libsynth/Makefile.rca $
# $Revision: 1.1 $
# $Aliases: beta2 $
# $Author: reuter $
# $Date: Sat Jun 27 14:44:25 1998 $
# $State: Experimental $
#

include port.mak

#
# Object files
#

OBJ = is_SynthNativeImp.o

#
# Include libraries
#

IMPORT_LIB =

#
# generated source files
#

GENSRC = is_SynthNative.h

#
# Source Files
#

SRC = is_SynthNativeImp.c

#
# Include Directories
#

INCDIRS = -I. $(JAVA_INCDIRS)

all	= $(SYNTH_SHR_LIB) $(GENSRC)

all: $(all)

depend:
	makedepend -f Makefile -- $(CFLAGS) $(INCDIRS) -- $(SRC)

install:
	cp $(SYNTH_SHR_LIB) $(LIB_INST_DIR)/$(SYNTH_SHR_LIB)

tags:
	etags $(SRC) *.h

clean:
	- rm -f $(OBJ)

veryclean:
	- rm -f $(GENSRC) $(OBJ) $(all) *~ *.bak

execclean:
	- rm -f $(all)


$(SYNTH_SHR_LIB): $(OBJ)
	ld $(SLFLAGS) -o $@ $(OBJ) $(IMPORT_LIB)

is_SynthNative.h: ../../classes/is/SynthNative.class
	javah -classpath $(CLASSPATH) -jni is.SynthNative ; \
		touch is_SynthNative.h
# touch it, because javah may leave the date unchanged in certain cases

.SUFFIXES: $(SUFFIXES)

.c.o:
	$(CC) -c $(SCFLAGS) $(CFLAGS) $(INCDIRS) $<

is_SynthNativeImp.c: is_SynthNative.h

# DO NOT DELETE THIS LINE -- make depend depends on it.

is_SynthNativeImp.o: /usr/lib/java/include/StubPreamble.h
is_SynthNativeImp.o: /usr/lib/java/include/oobj.h
is_SynthNativeImp.o: /usr/lib/java/include/typedefs.h
is_SynthNativeImp.o: /usr/lib/java/include/genunix/typedefs_md.h
is_SynthNativeImp.o: /usr/include/sys/types.h /usr/include/linux/types.h
is_SynthNativeImp.o: /usr/include/linux/posix_types.h
is_SynthNativeImp.o: /usr/include/asm/posix_types.h /usr/include/asm/types.h
is_SynthNativeImp.o: /usr/include/sys/bitypes.h /usr/include/sys/stat.h
is_SynthNativeImp.o: /usr/include/features.h /usr/include/sys/cdefs.h
is_SynthNativeImp.o: /usr/include/linux/stat.h /usr/lib/java/include/bool.h
is_SynthNativeImp.o: /usr/lib/java/include/debug.h /usr/include/stdio.h
is_SynthNativeImp.o: /usr/include/libio.h /usr/include/_G_config.h
is_SynthNativeImp.o: /usr/lib/java/include/genunix/oobj_md.h
is_SynthNativeImp.o: /usr/include/unistd.h /usr/include/posix_opt.h
is_SynthNativeImp.o: /usr/include/gnu/types.h /usr/include/confname.h
is_SynthNativeImp.o: /usr/lib/java/include/signature.h
is_SynthNativeImp.o: /usr/lib/java/include/interpreter.h
is_SynthNativeImp.o: /usr/include/stdlib.h /usr/include/errno.h
is_SynthNativeImp.o: /usr/include/linux/errno.h /usr/include/asm/errno.h
is_SynthNativeImp.o: /usr/include/alloca.h /usr/include/string.h
is_SynthNativeImp.o: /usr/lib/java/include/config.h
is_SynthNativeImp.o: /usr/lib/java/include/jni.h
is_SynthNativeImp.o: /usr/lib/java/include/genunix/jni_md.h
is_SynthNativeImp.o: /usr/lib/java/include/alloc_cache.h is_SynthNative.h
