/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/is/SynthNative.java.rca $
 * $Revision: 1.1 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Sat Jun 27 14:53:21 1998 $
 * $State: Experimental $
 */

/*
 * @(#)Synth.java 1.00 98/02/22
 *
 * Copyright (C) 1998 Juergen Reuter
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package is;

import java.io.RandomAccessFile;

/**
 * This class provides the interface to the synthesizer native code.
 */
class SynthNative
{
  static { System.loadLibrary("synth"); }  

  native static void synth(int xsize, int ysize,
			   int totalSamples,
			   double expScale, double expGrowth,
			   Synth synth,
			   PNMReader reader, RandomAccessFile out);

  native static int getSampleCount();
  native static double getMinSampleValue();
  native static double getMaxSampleValue();
  native static double getAvgSampleValue();
}
