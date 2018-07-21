/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/is/ImageSound.java.rca $
 * $Revision: 1.4 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Tue Jul 21 23:57:35 1998 $
 * $State: Experimental $
 */

/*
 * @(#)ImageSound.java 1.00 98/02/22
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

import java.io.PrintWriter;

/**
 * This application transforms any PNM (portable anymap) image into an
 * audio file (.sam file). This is done by interpreting each line of the
 * image as a spectral representation of sound for a certain amount of time.
 * The sound is synthesized by applying FM synthesis on each line of the
 * image.
 */
public class ImageSound
{
  final static String VERSION = "ImageSound/1.0 $Aliases: beta2 $";

  private final static double CD_RESOLUTION = 16.0; // [bits]
  private final static double LOG_2_10 = Math.log(10.0) / Math.log(2.0);

  /*
   * command line arguments
   */
  private static String outFile = null;
  private static double sampleRate = 8000.0; // [Hz]
  private static double sampleLength = 10.0; // [s]
  private static double minFreq = 20; // [Hz]
  private static double maxFreq = 4000; // [Hz]
  private static double spctDist = 100; // [cents]
  private static double tau = 0.02; // [s]
  private static double dynamics = 20.0 * CD_RESOLUTION / LOG_2_10; // [dB]
  private static String info = null;

  private static int argNoGreeting = 0;	     /*
					      * 0=unread;
					      * 1=argName read;
					      * 2=argValue read
					      */
  private static int argHelp = 0;            /* dto. */
  private static int argVersion = 0;         /* dto. */
  private static int argCopyright = 0;       /* dto. */
  private static int argOutFile = 0;         /* dto. */
  private static int argSampleRate = 0;	     /* dto. */
  private static int argSampleLength = 0;    /* dto. */
  private static int argMinFreq = 0;         /* dto. */
  private static int argMaxFreq = 0;         /* dto. */
  private static int argSpctDist = 0;        /* dto. */
  private static int argTau = 0;             /* dto. */
  private static int argDynamics = 0;        /* dto. */
  private static int argNoPadding = 0;       /* dto. */
  private static int argMaxPrecision = 0;    /* dto. */
  private static int argInfo = 0;            /* dto. */
  private static int argNoInfo = 0;          /* dto. */
  private static int argNativeSynth = 0;     /* dto. */

  private ImageSound() {}

  private static void printVersion(PrintWriter out)
  {
    out.println(VERSION);
    out.println("Copyright (C) 1998 Juergen Reuter");
    out.println();
    out.flush();
    if (out.checkError()) System.exit(-2);
  }

  private static void printCopyright(PrintWriter out)
  {
    printVersion(out);
    out.println("This program is free software; you can redistribute it");
    out.println("and/or modify it under the terms of the GNU General Public");
    out.println("License as published by the Free Software Foundation;");
    out.println("either version 2 of the License, or (at your option) any");
    out.println("later version.");
    out.println();
    out.println("This program is distributed in the hope that it will be");
    out.println("useful, but WITHOUT ANY WARRANTY; without even the implied");
    out.println("warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR");
    out.println("PURPOSE.  See the GNU General Public License for more");
    out.println("details.");
    out.println();
    out.println("You should have received a copy of the GNU General Public");
    out.println("License along with this program; if not, write to the Free");
    out.println("Software Foundation, Inc., 59 Temple Place - Suite 330,");
    out.println("Boston, MA  02111-1307, USA.");
    out.println();
    out.flush();
    if (out.checkError()) System.exit(-2);
  }

  private static void printGreeting(PrintWriter out)
  {
    printVersion(out);
    out.println("The ImageSound utility comes with ABSOLUTELY NO WARRANTY;");
    out.println("for details run this program with option `-copyright'.");
    out.println("This is free software, and you are welcome to redistribute");
    out.println("it under certain conditions; run with option `-copyright'");
    out.println("for details.");
    out.println("Run with option `-nogreeting' to suppress this message.");
    out.println();
    out.flush();
    if (out.checkError()) System.exit(-2);
  }

  private static void printHelp(PrintWriter out)
  {
    printVersion(out);
    out.println("Usage: ImageSound [-nogreeting] [-help] [-version] ");
    out.println("  [-copyright] [-rate <sample rate in Hz>]");
    out.println("  [-length <sample length in seconds>]");
    out.println("  [-minfreq <spectrum lower limit in Hz>]");
    out.println("  [-maxfreq <spectrum upper limit in Hz>]");
    out.println("  [-spctdist <spectral distance per pixel in cents>]");
    out.println("  [-tau <half-life of amplitude low-pass in seconds>]");
    out.println("  [-dynamics <max/min amplitude ratio in dB>]");
    out.println("  [-nopadding] [-maxprecision] [-info <any string>]");
    out.println("  [-noinfo] [-nativesynth] [-out file] file");
    out.println();
    out.println("Exit status codes:");
    out.println("Exit  1 : ok");
    out.println("Exit -1 : ordinary exit through help");
    out.println("Exit -2 : error");
    out.flush();
    if (out.checkError()) System.exit(-2);
  }

  private static void printMultipleFiles(PrintWriter out)
  {
    out.println("There were multiple input files specified.");
    printHelp(out);
    if (out.checkError()) System.exit(-2);
  }

  private static void printNoFiles(PrintWriter out)
  {
    out.println("There were no input files specified.");
    printHelp(out);
    if (out.checkError()) System.exit(-2);
  }

  /**
   * Returns the argument counter's last value that points to the first
   * file argument.
   */
  private static int parseArgv(PrintWriter out, String argv[])
  {
    int argc = 0;
    String paramName = null;
    try
      {
	for (argc = 0; argc < argv.length; argc++)
	  {
	    if (argOutFile == 1)
	      {
		paramName = "outfile";
		outFile = argv[argc];
		argOutFile++;
	      }
	    else if (argSampleRate == 1)
	      {
		paramName = "rate";
		sampleRate = Double.valueOf(argv[argc]).doubleValue();
		argSampleRate++;
	      }
	    else if (argSampleLength == 1)
	      {
		paramName = "length";
		sampleLength = Double.valueOf(argv[argc]).doubleValue();
		argSampleLength++;
	      }
	    else if (argMinFreq == 1)
	      {
		paramName = "minfreq";
		minFreq = Double.valueOf(argv[argc]).doubleValue();
		argMinFreq++;
	      }
	    else if (argMaxFreq == 1)
	      {
		paramName = "maxfreq";
		maxFreq = Double.valueOf(argv[argc]).doubleValue();
		argMaxFreq++;
	      }
	    else if (argSpctDist == 1)
	      {
		paramName = "spctdist";
		spctDist = Double.valueOf(argv[argc]).doubleValue();
		argSpctDist++;
	      }
	    else if (argTau == 1)
	      {
		paramName = "tau";
		tau = Double.valueOf(argv[argc]).doubleValue();
		argTau++;
	      }
	    else if (argTau == 1)
	      {
		paramName = "dynamics";
		dynamics = Double.valueOf(argv[argc]).doubleValue();
		argDynamics++;
	      }
	    else if (argInfo == 1)
	      {
		paramName = "info";
		info = argv[argc];
		argInfo++;
	      }
	    else if ((argv[argc].equals("-nogreeting")) &&
		     (argNoGreeting == 0))
	      argNoGreeting++;
	    else if ((argv[argc].equals("-help")) && (argHelp == 0))
	      argHelp++;
	    else if ((argv[argc].equals("-?")) && (argHelp == 0))
	      argHelp++;
	    else if ((argv[argc].equals("-version")) && (argVersion == 0))
	      argVersion++;
	    else if ((argv[argc].equals("-copyright")) && (argCopyright == 0))
	      argCopyright++;
	    else if ((argv[argc].equals("-rate")) && (argSampleRate == 0))
	      argSampleRate++;
	    else if ((argv[argc].equals("-length")) && (argSampleLength == 0))
	      argSampleLength++;
	    else if ((argv[argc].equals("-minfreq")) && (argMinFreq == 0))
	      argMinFreq++;
	    else if ((argv[argc].equals("-maxfreq")) && (argMaxFreq == 0))
	      argMaxFreq++;
	    else if ((argv[argc].equals("-spctdist")) && (argSpctDist == 0))
	      argSpctDist++;
	    else if ((argv[argc].equals("-tau")) && (argTau == 0))
	      argTau++;
	    else if ((argv[argc].equals("-dynamics")) && (argDynamics == 0))
	      argDynamics++;
	    else if ((argv[argc].equals("-nopadding")) && (argNoPadding == 0))
	      argNoPadding++;
	    else if ((argv[argc].equals("-maxprecision")) &&
		     (argMaxPrecision == 0))
	      argMaxPrecision++;
	    else if ((argv[argc].equals("-info")) && (argInfo == 0))
	      argInfo++;
	    else if ((argv[argc].equals("-noinfo")) &&
		     (argNoInfo == 0))
	      argNoInfo++;
	    else if ((argv[argc].equals("-nativesynth")) &&
		     (argNativeSynth == 0))
	      argNativeSynth++;
	    else if ((argv[argc].equals("-out")) && (argOutFile == 0))
	      argOutFile++;
	    else /* here starts the list of file names => we are done */
	      break;
	  }
      }
    catch (Exception e)
      {
	String msg = (paramName != null) ?
	  "Error while parsing parameter value of " + paramName + ": " :
	  "";
	out.println(msg + e);
	out.flush();
	System.exit(-2);
      }
    return argc;
  }

  /**
   * Envokes the ImageSound utility using the specified arguments as
   * options as if they were typed on the command line.
   *
   * @param argv The argument list as passed from the command line.
   *    Use option `-help' for details on options available.
   */
  public static void main(String[] argv)
  {
    PrintWriter err = new PrintWriter(System.err);
    int argc = parseArgv(err, argv);
    if (argHelp != 0)
      {
	printHelp(err);
	System.exit(-1);
      }
    else if (argVersion != 0)
      {
	printVersion(err);
	System.exit(1);
      }
    else if (argCopyright != 0)
      {
	printCopyright(err);
	System.exit(1);
      }
    else if ((argInfo != 0) && (argNoInfo != 0))
      {
	err.println("Only one of the options info and noinfo");
	err.println("may be specified.");
	err.flush();
	System.exit(-2);
      }
    else if ((argMaxFreq != 0) && (argSpctDist != 0))
      {
	err.println("Only one of the options maxfreq and spctdist");
	err.println("may be specified.");
	err.flush();
	System.exit(-2);
      }
    else
      {
	if (argNoGreeting == 0)
	  printGreeting(err);
	String inFile = null;
	switch (argv.length - argc)
	  {
	  case 0:
	    printNoFiles(err);
	    System.exit(-2);
	  case 1:
	    inFile = argv[argc];
	    break;
	  default:
	    printMultipleFiles(err);
	    System.exit(-2);
	  }
	try
	  {
	    Synth synth = new Synth(err, inFile, outFile);
	    synth.setSampleRate(sampleRate);
	    synth.setSampleLength(sampleLength);
	    synth.setMinFreq(minFreq);
	    if (argSpctDist != 0)
	      synth.setSpctDist(spctDist);
	    else
	      synth.setMaxFreq(maxFreq);
	    synth.setTau(tau);
	    synth.setDynamics(dynamics);
	    synth.setPadding(argNoPadding == 0);
	    synth.setMaxPrecision(argMaxPrecision == 0);
	    if (info != null)
	      synth.setInfo(info);
	    else if (argNoInfo != 0)
	      synth.setInfo(null);
	    synth.synthesize(argNativeSynth != 0);
	    err.println("[done]");
	    err.flush();
	  }
	catch (Exception e)
	  {
	    err.println("Fatal error: " + e);
	    e.printStackTrace(err);
	    err.flush();
	    System.exit(-2);
	  }
      }
  }
}
