# imgsnd
Image Sound -- Conversion of B/W Images into Audio

Image Sound is a collection of command line tools for creating sound
files from image files.  Most of the code is written in Java.  The
core synthesis has also been implemented in C (via the JNI Java Native
Interface) to reduce computation time.  Nowadays, with JIT compilers
available all around and garbage collector performance penalty being
mitigated, one probably would no more implement a JNI version of the
synthesis.  Yet, the code shows the basics of synthesis in both
languages, Java in C.

Note: This project has been abandoned in 1998 (and it might pose a
challenge to get the code running out of the box on up-to-date systems
without various updates in the sources).  I just put here the original
sources as of July, 1998, for documentation purposes.  The source show
the origins of successor projects such as SoundPaint, the SoundColumn
or the ColorMovesSound project.
