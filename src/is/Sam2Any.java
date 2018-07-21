/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/is/Sam2Any.java.rca $
 * $Revision: 1.2 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Tue Jul 21 23:57:35 1998 $
 * $State: Experimental $
 */

/*
 * @(#)Sam2Any.java 1.00 98/02/22
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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.PrintWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;

/**
 * This application converts a sample file as produced by the ImageSound
 * utility (.sam file) into a sun audio file (.au file).
 */
public class Sam2Any implements ProgressDisplayModel
{
  private final static String VERSION = "Sam2Any/1.0 $Aliases: beta2 $";

  // sample scale modes
  private final static int MODE_MAX = 0;
  private final static int MODE_AVG = 1;
  private final static int MODE_CONST = 2;

  /*
   * command line arguments
   */
  private static String type = "au"; // default output: sun audio file format
  private static String scale = null;
  private static String info = null;
  private static String outFile = null;

  private static int argNoGreeting = 0;	     /*
					      * 0=unread;
					      * 1=argName read;
					      * 2=argValue read
					      */
  private static int argHelp = 0;	     /* dto. */
  private static int argVersion = 0;	     /* dto. */
  private static int argCopyright = 0;	     /* dto. */
  private static int argType = 0;	     /* dto. */
  private static int argScale = 0;	     /* dto. */
  private static int argInfo = 0;	     /* dto. */
  private static int argNoInfo = 0;	     /* dto. */
  private static int argOutFile = 0;	     /* dto. */

  private Sam2Any() {}

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
    out.println("The Sam2Any utility comes with ABSOLUTELY NO WARRANTY;");
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
    out.println("Usage: Sam2Any [-nogreeting] [-help] [-version]");
    out.println("  [-copyright] [-scale ( max | avg | <constant double> ) ]");
    out.println("  [-type ( au | wav ) ] [-info <any string>] [-noinfo]");
    out.println("  [-out file] file");
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
  private static int parseArgv(String argv[])
  {
    int argc;
    for (argc = 0; argc < argv.length; argc++)
      {
	if (argOutFile == 1)
	  {
	    outFile = argv[argc];
	    argOutFile++;
	  }
	else if (argType == 1)
	  {
	    type = argv[argc];
	    argType++;
	  }
	else if (argScale == 1)
	  {
	    scale = argv[argc];
	    argScale++;
	  }
	else if (argInfo == 1)
	  {
	    info = argv[argc];
	    argInfo++;
	  }
	else if ((argv[argc].equals("-nogreeting")) && (argNoGreeting == 0))
	  argNoGreeting++;
	else if ((argv[argc].equals("-help")) && (argHelp == 0))
	  argHelp++;
	else if ((argv[argc].equals("-?")) && (argHelp == 0))
	  argHelp++;
	else if ((argv[argc].equals("-version")) && (argVersion == 0))
	  argVersion++;
	else if ((argv[argc].equals("-copyright")) && (argCopyright == 0))
	  argCopyright++;
	else if ((argv[argc].equals("-scale")) && (argScale == 0))
	  argScale++;
	else if ((argv[argc].equals("-type")) && (argType == 0))
	  argType++;
	else if ((argv[argc].equals("-info")) && (argInfo == 0))
	  argInfo++;
	else if ((argv[argc].equals("-noinfo")) &&
		 (argNoInfo == 0))
	  argNoInfo++;
	else if ((argv[argc].equals("-out")) && (argOutFile == 0))
	  argOutFile++;
	else /* here starts the list of file names => we are done */
	  break;
      }
    return argc;
  }

  /**
   * Envokes the Sam2Any utility using the specified arguments as options as
   * if they were typed on the command line.
   *
   * @param argv The argument list as passed from the command line.
   *    Use option `-help' for details on options available.
   */
  public static void main(String[] argv)
  {
    int argc = parseArgv(argv);
    PrintWriter out = new PrintWriter(System.out);
    if (argHelp != 0)
      {
	printHelp(out);
	System.exit(-1);
      }
    else if (argVersion != 0)
      {
	printVersion(out);
	System.exit(1);
      }
    else if (argCopyright != 0)
      {
	printCopyright(out);
	System.exit(1);
      }
    else if ((argInfo != 0) && (argNoInfo != 0))
      {
	out.println("Only one of the options info and noinfo");
	out.println("may be specified.");
	out.flush();
	System.exit(-2);
      }
    else
      {
	if (argNoGreeting == 0)
	  printGreeting(out);
	int mode;
	double scaleConst = 0.0;
	if (argScale != 0)
	  if (scale.equals("max"))
	    mode = MODE_MAX;
	  else if (scale.equals("avg"))
	    mode = MODE_AVG;
	  else
	    {
	      mode = MODE_CONST;
	      try
		{
		  scaleConst = Double.valueOf(scale).doubleValue();
		}
	      catch (Exception e)
		{
		  out.println("Option scale: 'min' or 'avg' or constant");
		  out.println("value expected.");
		  out.flush();
		  System.exit(-2);
		}
	    }
	else
	  mode = MODE_MAX; // default scale mode
	String inFile = null;
	switch (argv.length - argc)
	  {
	  case 0:
	    printNoFiles(out);
	    System.exit(-2);
	  case 1:
	    inFile = argv[argc];
	    break;
	  default:
	    printMultipleFiles(out);
	    System.exit(-2);
	  }
	new Sam2Any(out, inFile, outFile, type, mode, scaleConst);
      }
  }

  /**
   * Automatically creates a file name for the output file from the input file.
   */
  private static String outFileFromInFile(String inFile, String newSuffix)
  {
    if (inFile.endsWith(".sam"))
      return inFile.substring(0, inFile.length() - 4) + "." + newSuffix;
    else
      return inFile + "." + newSuffix;
  }

  private static void readUTF(DataInputStream in, String s) throws Exception
  {
    if (!in.readUTF().equals(s))
      throw new Exception("file format error: '" + s + "' expected.");
  }

  /**
   * Return a pretty String representation of the given double as
   * fixed point value with a single digit after the decimal point.
   * Note: This only works for doubles in the +/- range of a 10th
   * MAXINT value.
   */
  private static String prettyDoubleToString(double d)
  {
    int i = (int)(d * 10);
    return "" + (i / 10) + "." + (i % 10);
  }

  /**
   * Returns the percentage value of the given double as string.
   */
  protected static String percentString(double d)
  {
    return prettyDoubleToString(d * 100) + "%";
  }

  /**
   * Return an appropriate String representation of the given number of
   * bytes in Bytes, kBytes, MBytes or whatsoever.
   */
  private String byteSize(long bytes)
  {
    if (bytes == 1)
      return "1 Byte";
    else if (bytes < 1024)
      return "" + prettyDoubleToString(bytes) + " Bytes";
    else if (bytes < 1048576)
      return "" + prettyDoubleToString((double)bytes / 1024) + " kBytes";
    else
      return "" + prettyDoubleToString((double)bytes / 1048576) + "MBytes";
  }

  /**
   * Given a number in the range 0..99, return a String representation of
   * the number with always two digits ("00".."99"). This method only
   * works properly for values in that range.
   */
  private static String twoDigits(long l)
  {
    l %= 100;
    return "" + (l / 10) + (l % 10);
  }

  /**
   * Given a time in milliseconds, return the time as a readable String.
   */
  protected static String msString(long ms)
  {
    if (ms >= 360000000)
      return "??:??:??";
    else
      {
	ms = Math.max(0, ms);
	long sec = (ms + 500) / 1000;
	long min = sec / 60;
	sec %= 60;
	long h = min / 60;
	min %= 60;
	return twoDigits(h) + ":" + twoDigits(min) + ":" + twoDigits(sec);
      }
  }

  private PrintWriter stdout;
  private int totalSamples;
  private int sampleCount, sampleCountMark;
  private Date startDate;
  private int bytesPerSample;

  /**
   * Refreshes the display that shows the current progress.
   * This must be implemented by a subclass when using this class.
   */
  public void refreshDisplay()
  {
    long nowtime = new Date().getTime();
    long runningtime = nowtime - startDate.getTime();
    double percent = (double)(sampleCount + 1) / totalSamples;
    long totaltime = (long)((double)runningtime / percent);
    long timeleft = totaltime - runningtime;
    String msg =
      sampleCount +
      " samples (" + byteSize(sampleCount * bytesPerSample) + ") of " +
      totalSamples + " (" + byteSize(totalSamples * bytesPerSample) + ") (" +
      percentString(percent) + "; ETA: " + msString(timeleft) + ")    ";
    stdout.print(msg.substring(0, Math.min(msg.length(), 79)) + "\r");
    stdout.flush();
  }

  private Sam2Any(PrintWriter stdout,
		  String inFile, String outFile,
		  String type, int mode, double scaleConst)
  {
    this.stdout = stdout;
    DataInputStream in;
    AudioOutputStream out;
    try
      {
	if (outFile == null)
	  outFile = outFileFromInFile(inFile, type);
	stdout.println("input file        = " + inFile);
	stdout.println("output file       = " + outFile);
	BufferedInputStream inStream =
	  new BufferedInputStream(new FileInputStream(inFile));
	in = new DataInputStream(inStream);
	BufferedOutputStream outStream =
	  new BufferedOutputStream(new FileOutputStream(outFile));
	if (type.equals("au"))
	  out = new SunAudioOutputStream(outStream);
	else if (type.equals("wav"))
	  out = new WavAudioOutputStream(outStream);
	else
	  throw new Exception("unknown output audio file format: " + type);
	stdout.println("output file type  = " + out.getName());
	stdout.println("output file suff. = " + type);

	// read header chunk
	stdout.println("reading header chunk...");
	readUTF(in, ".sam"); // magic number
	int headerChunkLength = in.readInt(); // header chunk length
	readUTF(in, "00010000"); // version info
	short values = in.readShort(); // values per sample (mono=1; stereo=2)
	if (values != 1)
	  throw new Exception("currently, only mono samples supported");
	stdout.println("values per sample = " + values);
	short channels = in.readShort(); // number of channels
	if (values != 1)
	  throw new Exception("currently, only single channel samples " +
			      "supported");
	stdout.println("channels          = " + channels);
	int sampleEncoding = in.readInt(); // sample value encoding type
	if (sampleEncoding != 7)
	  throw new Exception("currently, only linear 64 bit floats " +
			      "encoding supported");
	switch (sampleEncoding)
	  {
	  case 7:
	    stdout.println("sample encoding   = linear 64 bit floats");
	    break;
	  default:
	    throw new Exception("unknown sample value encoding: " +
				sampleEncoding);
	  }
	//out.setEncodingScheme(out.ENCODING_MU_LAW);
	//out.setBitsPerValue(8);
	out.setValuesPerSample(1);
	out.setChannels(1);
	int sampleRate = in.readInt(); // sample rate in Hz
	out.setSampleRate(sampleRate);
	stdout.println("sample rate       = " + (float)sampleRate + "Hz");
	String info;
	if (headerChunkLength > 0x20)
	  {
	    info = in.readUTF(); // character info
	    stdout.println("info              = " + info);
	  }
	else
	  {
	    info = null;
	    stdout.println("info              = <undefined>");
	  }
	stdout.flush();
	if (argNoInfo != 0)
	  out.setInfo(null);
	else if (Sam2Any.info != null)
	  out.setInfo(Sam2Any.info);
	else
	  out.setInfo(info);

	// read data chunk
	stdout.println("reading data chunk...");
	readUTF(in, "data"); // version info
	int dataChunkLength = in.readInt(); // data chunk length
	double minSample = Math.abs(in.readDouble());
	stdout.println("min sample        = " + (float)minSample);
	double maxSample = Math.abs(in.readDouble());
	stdout.println("max sample        = " + (float)maxSample);
	double avgSample = Math.abs(in.readDouble());
	stdout.println("avg sample        = " + (float)avgSample);
	double sampleScale;
	switch (mode)
	  {
	  case MODE_MAX:
	    sampleScale = 0.5 *
	      (out.getMaxValidSample() - out.getMinValidSample() + 1) /
	      Math.max(minSample, maxSample);
	    break;
	  case MODE_AVG:
	    sampleScale = 1.0;
	    break;
	  case MODE_CONST:
	    sampleScale = scaleConst;
	    break;
	  default:
	    throw new IllegalStateException("illegal mode");
	  }
	out.setSampleScale(sampleScale);
	totalSamples = (dataChunkLength - 0x1a) >>> 3;
	out.setSamples(totalSamples);
	stdout.println("total #samples    = " + totalSamples);
	bytesPerSample =
	  (out.getBitsPerValue() * out.getValuesPerSample() + 7) / 8;
	startDate = new Date();
	refreshDisplay();
	ProgressDisplay pd = new ProgressDisplay(this);
	pd.start();
	sampleCountMark = 0;
	sampleCount = 0;
	while (sampleCountMark < totalSamples)
	  {
	    sampleCountMark = Math.min(sampleCountMark + 0x800, totalSamples);
	    for (; sampleCount < sampleCountMark; sampleCount++)
	      out.writeSample(in.readDouble());
	    try
	      {
		// For some reason, linux-jdk1.1 seems to treat threads
		// cooperatively (is this a bug in linux-jdk1.1?).
		// Hence, we have to add a sleep() here to make the progress
		// display run.
		Thread.sleep(1);
	      }
	    catch (InterruptedException e)
	      {
		throw new RuntimeException(e.toString());
	      }
	  }
	pd.stop();
	refreshDisplay();
	Date endDate = new Date();
	long time = (endDate.getTime() - startDate.getTime()) / 10;
	stdout.println();
	stdout.println("computing time   = " + (time / 100) + "." +
		       (time % 100) + "s");
	stdout.flush();
	if (in.available() > 0)
	  throw new Exception("file format error: EOF expected");
	out.close();
	in.close();
      }
    catch (Exception e)
      {
	stdout.println();
	stdout.println("Fatal error: " + e);
	stdout.flush();
	System.exit(-2);
      }
  }
}
