#
# $Source: /home/reuter/archive/project/ImageSound/IS/src/libsynth/linux.mak.rca $
# $Revision: 1.1 $
# $Aliases: beta2 $
# $Author: reuter $
# $Date: Sat Jun 27 14:46:27 1998 $
# $State: Experimental $
#

SYNTH_SHR_LIB = libsynth.so
CFLAGS  = -g -O2 -Wall
SLFLAGS = -shared
SCFLAGS = -fpic
CC      = gcc
RANLIB  = echo
AR      = /usr/bin/ar
JAVA_INCDIRS = -I/usr/lib/java/include/ -I/usr/lib/java/include/genunix/
