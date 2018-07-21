#
# $Source: /home/reuter/archive/project/ImageSound/IS/src/defs.mak.rca $
# $Revision: 1.1 $
# $Aliases: beta2 $
# $Author: reuter $
# $Date: Sat Jun 27 14:26:22 1998 $
# $State: Experimental $
#
# defs.mak for ImageSound makefile
#
# Copyright (C) 1998  Juergen Reuter
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

# the ImageSound home directory
IS_HOME = $(PWD)/..

# the java runtime environment (JRE) home directory
JRE_HOME = $(PWD)/../../JRE

# java source files root dircetory
SRC_ROOT = $(IS_HOME)/src

# compiled class files root directory
# (run 'make classes' to generate)
CLS_ROOT = $(IS_HOME)/classes

# library directory; contains archive file with the compiled classes
# (run 'make lib' to generate)
LIB_DIR = $(IS_HOME)/lib

# api documentation directory; contains html files generated with javadoc
# (run 'make doc' to generate)
DOC_DIR = $(IS_HOME)/doc/api

# other (external) packages to be included when running javadoc
DOC_OTHER =

# java classpath environment variable
CLASSPATH = '$(JRE_HOME)/lib/classes.zip:$(CLS_ROOT):$(SRC_ROOT)'
