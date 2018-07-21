/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/is/MIDI2PNM.java.rca $
 * $Revision: 1.2 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Tue Jul 21 23:57:36 1998 $
 * $State: Experimental $
 */

/*
 * @(#)MIDI2PNM.java 1.00 98/04/19
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

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Vector;

import midi.List;
import midi.MIDIEvent;
import midi.SMFReader;

/**
 * This utility converts a Standard MIDI File (SMF) into a proper image
 * (portable anymap, PNM).
 * @author Juergen Reuter
 * @version 1.0 27 Apr 98
 */
public class MIDI2PNM extends SMFReader
{
  private final static String VERSION = "MIDI2PNM/1.0 $Aliases: beta2 $";

  /*
   * command line arguments
   */
  private static String timeScale = null;
  private static String pitchScale = null;
  private static String pitchDecay = null;
  private static String pitchRef = null;
  private static String velocScale = null;
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
  private static int argTimeScale = 0;	     /* dto. */
  private static int argPitchScale = 0;	     /* dto. */
  private static int argPitchDecay = 0;	     /* dto. */
  private static int argPitchRef = 0;	     /* dto. */
  private static int argVelocScale = 0;	     /* dto. */
  private static int argInfo = 0;	     /* dto. */
  private static int argNoInfo = 0;	     /* dto. */
  private static int argOutFile = 0;	     /* dto. */

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
    out.println("The MIDI2PNM utility comes with ABSOLUTELY NO WARRANTY;");
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
    out.println("Usage: MIDI2PNM [-nogreeting] [-help] [-version]");
    out.println("  [-copyright] [-timescale <microseconds per pixel>]");
    out.println("  [-pitchscale <pixels per semitone>]");
    out.println("  [-pitchdecay <double>] [-pitchref <0..127>]");
    out.println("  [-velocscale <factor>]");
    out.println("  [-info <any string>] [-noinfo]");
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
	else if (argTimeScale == 1)
	  {
	    timeScale = argv[argc];
	    argTimeScale++;
	  }
	else if (argPitchScale == 1)
	  {
	    pitchScale = argv[argc];
	    argPitchScale++;
	  }
	else if (argPitchDecay == 1)
	  {
	    pitchDecay = argv[argc];
	    argPitchDecay++;
	  }
	else if (argPitchRef == 1)
	  {
	    pitchRef = argv[argc];
	    argPitchRef++;
	  }
	else if (argVelocScale == 1)
	  {
	    velocScale = argv[argc];
	    argVelocScale++;
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
	else if ((argv[argc].equals("-timescale")) && (argTimeScale == 0))
	  argTimeScale++;
	else if ((argv[argc].equals("-pitchscale")) && (argPitchScale == 0))
	  argPitchScale++;
	else if ((argv[argc].equals("-pitchdecay")) && (argPitchDecay == 0))
	  argPitchDecay++;
	else if ((argv[argc].equals("-pitchref")) && (argPitchRef == 0))
	  argPitchRef++;
	else if ((argv[argc].equals("-velocscale")) && (argVelocScale == 0))
	  argVelocScale++;
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
   * Envokes the MIDI2PNM utility using the specified arguments as options as
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
	int timeScaleConst = 62500; // microseconds per pixel
	if (argTimeScale != 0)
	  try
	  {
	    timeScaleConst = Integer.valueOf(timeScale).intValue();
	  }
	catch (Exception e)
	  {
	    out.println("Option timescale: constant value expected.");
	    out.flush();
	    System.exit(-2);
	  }
	out.println("timescale = " + timeScaleConst +
		    " microseconds per pixel");
	double pitchScaleConst = 1.0; // one pixel per semitone
	if (argPitchScale != 0)
	  try
	  {
	    pitchScaleConst = Double.valueOf(pitchScale).doubleValue();
	  }
	catch (Exception e)
	  {
	    out.println("Option pitchscale: constant value expected.");
	    out.flush();
	    System.exit(-2);
	  }
	out.println("pitchscale = " + pitchScaleConst +
		    " pixels per semitone");
	double pitchDecayConst = 0.0; // no decay
	if (argPitchDecay != 0)
	  try
	  {
	    pitchDecayConst = Double.valueOf(pitchDecay).doubleValue();
	  }
	catch (Exception e)
	  {
	    out.println("Option pitchdecay: constant value expected.");
	    out.flush();
	    System.exit(-2);
	  }
	out.println("pitchdecay = " + pitchDecayConst +
		    " time of half-life period");
	int pitchRefConst = 0;
	if (argPitchRef != 0)
	  try
	  {
	    pitchRefConst = Integer.valueOf(pitchRef).intValue();
	    if ((pitchRefConst < 0) || (pitchRefConst > 127))
	      throw new IllegalArgumentException();
	  }
	catch (Exception e)
	  {
	    out.println("Option pitchref: constant value (0..127) expected.");
	    out.flush();
	    System.exit(-2);
	  }
	out.println("pitchref = " + MIDIEvent.PITCHES[pitchRefConst]);
	double velocScaleConst = 1.0;
	if (argVelocScale != 0)
	  try
	  {
	    velocScaleConst = Double.valueOf(velocScale).doubleValue();
	  }
	catch (Exception e)
	  {
	    out.println("Option velocscale: constant value expected.");
	    out.flush();
	    System.exit(-2);
	  }
	out.println("velocscale = " + velocScaleConst);
	velocScaleConst = 2.0 * velocScaleConst; // MIDI: 0..127; PNM: 0..255
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
	try
	  {
	    new MIDI2PNM(out, inFile, outFile, timeScaleConst,
			 pitchScaleConst, pitchDecayConst, pitchRefConst,
			 velocScaleConst).
	      convert();
	  }
	catch (IOException e)
	  {
	    e.printStackTrace(out);
	    out.flush();
	    System.exit(-2);
	  }
      }
  }

  private final static double TWENTYFOURTH = 1.0 / 24.0;

  /**
   * Given time in 24ths of a microsecond, returns the time in pixels.
   * @param time The absolute time in 24th of a microsecond.
   * @return The time in pixels.
   */
  private int getPixelsPer24thMicroseconds(long time)
  {
    if ((int)(time * pixelsPerMicrosecond * TWENTYFOURTH) < 0)
      {
	out.println("invalid time or pixelsPerMicrosecond");
	out.println("time: " + time);
	out.println("pixelsPerMicrosecond=" + pixelsPerMicrosecond);
	new Exception().printStackTrace(out);
	out.flush();
	System.exit(-2);
      }
    return (int)(time * pixelsPerMicrosecond * TWENTYFOURTH);
  }

  /**
   * Sets the tempo in microseconds per quarter note.
   * @param uspqn The tempo in microseconds per quarter note.
   */
  private void setMicrosecondsPerQuarterNote(int uspqn)
  {
    microsecondsPerQuarterNote = uspqn;
  }

  private void initTrack()
  {
    for (int i = 0; i < 128; i++)
      keys[i] = 0;
    setMicrosecondsPerQuarterNote(500000); // 120 bpm
    current24thsOfMicrosec = 0;
    prevNoteEventIn24thsOfMicrosec = 0;
  }

  /**
   * This method actually performs the conversion.
   * @exception IOException If an I/O error occurs.
   */
  private void convert() throws IOException
  {
    rtau = (pitchDecayConst * Math.log(0.5)) / 24000000.0;
    keys = new byte[128];
    xsize = 0;
    ysize = 0;
    minPixel = Integer.MAX_VALUE;
    maxPixel = Integer.MIN_VALUE;
    minPitch = 127;
    maxPitch = 0;
    max24thsOfMicrosec = 0;
    pixelTimeIn24thsOfMicrosec = new Vector();
    pixelTimeIn24thsOfMicrosec.addElement(new Long(0));
    out.println("[evaluating xsize & ysize...]"); out.flush();
    mode = 0; // evaluating xsize & ysize
    initTrack();
    List list = parseSMF();
    xsize = getPixelsPer24thMicroseconds(max24thsOfMicrosec);
    if (minPixel > maxPixel)
      ysize = 0;
    else
      ysize = maxPixel - minPixel + 1;
    if (minPitch > maxPitch)
      pitchsize = 0;
    else
      pitchsize = maxPitch - minPitch + 1;
    out.println("xsize = " + xsize); out.flush();
    out.println("ysize = " + ysize); out.flush();
    if ((xsize > 0) && (ysize > 0))
      {
	BufferedOutputStream pnm =
	  new BufferedOutputStream(new FileOutputStream(outFile));
	map = new byte[xsize][ysize];
	out.println("[creating bitmap image...]"); out.flush();
	mode = 1; // creating bitmap image
	initTrack();
	for (List track = (List)list.firstElement(); track != null;
	     track = (List)track.next())
	  {
	    for (SMFEvent event = (SMFEvent)track.firstElement();
		 event != null;
		 event = (SMFEvent)event.next())
	      event.exec();
	    execMtrkChunk(track);
	  }
	out.println("[writing pnm file...]"); out.flush();
	boolean rotate = true;
	if (rotate)
	  {
	    String header = "P5" + "\n" + ysize + "\n" + xsize + "\n" +
	      maxColor + "\n";
	    for (int i = 0; i < header.length(); i++)
	      pnm.write(header.charAt(i));
	    for (int x = 0; x < xsize; x++)
	      for (int y = 0; y < ysize; y++)
		pnm.write((int)map[x][y] & 0xff);
	  }
	else
	  {
	    String header = "P5" + "\n" + xsize + "\n" + ysize + "\n" +
	      maxColor + "\n";
	    for (int i = 0; i < header.length(); i++)
	      pnm.write(header.charAt(i));
	    for (int y = 0; y < ysize; y++)
	      for (int x = 0; x < xsize; x++)
		pnm.write((int)map[x][y] & 0xff);
	  }
	pnm.flush();
	pnm.close();
      }
    else
      out.println("empty pixmap -> nothing to do!");
    out.println("[done.]"); out.flush();
    out.close();
  }

  private PrintWriter out;
  private int microsecondsPerPixel;
  private double pixelsPerMicrosecond;
  private double pitchScaleConst;
  private double pitchDecayConst;
  private int pitchRefConst; // the pitch against that the decay runs
  private double velocScaleConst;
  private double rtau; // reverse tau

  private int microsecondsPerQuarterNote;
  private int mode;
  private long current24thsOfMicrosec;
  private long max24thsOfMicrosec;
  private long prevNoteEventIn24thsOfMicrosec;
  private byte[] keys;
  private int xsize;
  private int ysize;
  private int pitchsize;
  private final static int maxColor = 255;
  private int minPixel;
  private int maxPixel;
  private int minPitch;
  private int maxPitch;
  private byte[][] map;
  private Vector pixelTimeIn24thsOfMicrosec; // vector of Long objects

  /**
   * Automatically creates a file name for the output file from the input file.
   */
  private static String outFileFromInFile(String inFile)
  {
    if (inFile.endsWith(".mid"))
      return inFile.substring(0, inFile.length() - 4) + ".pnm";
    else
      return inFile + ".pnm";
  }

  private MIDI2PNM(PrintWriter out,
		   String inFile, String outFile, int microsecondsPerPixel,
		   double pitchScaleConst, double pitchDecayConst,
		   int pitchRefConst, double velocScaleConst)
       throws IOException
  {
    super(new FileInputStream(inFile));
    this.out = out;
    this.microsecondsPerPixel = microsecondsPerPixel;
    this.pixelsPerMicrosecond = 1.0 / microsecondsPerPixel;
    this.pitchScaleConst = pitchScaleConst;
    this.pitchDecayConst = pitchDecayConst;
    this.pitchRefConst = pitchRefConst;
    this.velocScaleConst = velocScaleConst;
    this.outFile = (outFile != null) ? outFile : outFileFromInFile(inFile);
  }

  /**
   * A factory method that is used to create objects of class
   * SMFEvent.
   * @param runningStatus The running status byte to be used when creating
   *    the SMFEventChatter object.
   * @return A new, empty SMFEventChatter object.
   */
  protected midi.SMFEvent createSMFEvent(int runningStatus)
  {
    return new SMFEvent(this, runningStatus);
  }

  /**
   * A factory method that is used to create objects of class
   * SMFEvent.
   * @return A new, empty SMFEventChatter object.
   */
  protected midi.SMFEvent createSMFEvent()
  {
    return new SMFEvent(this);
  }

  /**
   * This method is called as soon as an MTrk chunk has been completed.
   * @param track A List object that represents a list of all SMFEvent objects
   *    of the current chunk.
   */
  protected void execMtrkChunk(List track)
  {
    if (mode == 0)
      max24thsOfMicrosec =
	Math.max(max24thsOfMicrosec, current24thsOfMicrosec);

    // clear notes that are still "hanging"
    SMFEvent event = (SMFEvent)track.firstElement();
    if (event != null)
      for (int pitch = 0; pitch < 128; pitch++)
	if (keys[pitch] != 0)
	  event.execNoteOff(0, pitch, 0);

    out.println("[Track " + track + " done.]");
    out.flush();
    initTrack();
  }

  private class SMFEvent extends midi.SMFEvent
  {
    private MIDI2PNM main;

    SMFEvent(MIDI2PNM main)
    {
      super();
      this.main = main;
    }

    SMFEvent(MIDI2PNM main, int runningStatus)
    {
      super(runningStatus);
      this.main = main;
    }

    /**
     * This method is called as soon as this SMFEvent has been completed and
     * if it represents a SMF Set Tempo Meta Event.
     * @param tempo The tempo in microseconds per MIDI quarter-note.
     */
    protected void execSetTempo(int tempo)
    {
      setMicrosecondsPerQuarterNote(tempo);
      if (mode == 0)
	updatePixelTimeIn24thsOfMicrosec();
      super.execSetTempo(tempo);
    }

    /**
     * This method is used while evaluating xsize and ysize.
     * It is called on each Set Tempo and Note On/Off event.
     * It updates the pixelTimeIn24thsOfMicrosec array from the
     * last updated time up to the current time, current24thsOfMicrosec.
     */
    private void updatePixelTimeIn24thsOfMicrosec()
    {
      long prevTimeEventIn24thsOfMicrosec =
	((Long)pixelTimeIn24thsOfMicrosec.lastElement()).longValue();
      int leftPixelPos =
	getPixelsPer24thMicroseconds(prevTimeEventIn24thsOfMicrosec);
      int rightPixelPos =
	getPixelsPer24thMicroseconds(current24thsOfMicrosec);
      if (rightPixelPos > leftPixelPos)
	{
	  int deltaPixelPos = rightPixelPos - leftPixelPos;
	  long microseconds24thPerPixel = 24 * microsecondsPerPixel;
	  long running24thsOfMicrosec = prevTimeEventIn24thsOfMicrosec;
	  for (int pixelPos = leftPixelPos + 1;
	       pixelPos <= rightPixelPos;
	       pixelPos++)
	    {
	      running24thsOfMicrosec += microseconds24thPerPixel;
	      pixelTimeIn24thsOfMicrosec.
		addElement(new Long(running24thsOfMicrosec));
	    }
	}
    }

    private void updatePitchExtrema(int pitch)
    {
      if (pitch < minPitch)
	minPitch = pitch;
      if (pitch > maxPitch)
	maxPitch = pitch;
      int pixel = getPixelFromPitch(pitch, current24thsOfMicrosec);
      if (pixel < minPixel)
	minPixel = pixel;
      if (pixel > maxPixel)
	maxPixel = pixel;
    }

    /**
     * Returns the a representation of the specified pitch in terms of
     * an x coordinate in pixels.
     * @param pitch The pitch to be represented.
     * @param time The absolute time in 24ths of a microsecond. This will
     *    be used to take the decay parameter into account.
     * @return A representation of the specified pitch in terms of an x
     *    x coordinate in pixels.
     */
    private int getPixelFromPitch(int pitch, long time)
    {
      if ((pitch < 0) || (pitch > 127))
	throw new IllegalArgumentException("(pitch < 0) or (pitch > 127)");
      return
	(int)Math.round((pitchRefConst +
			 ((pitch - pitchRefConst) * Math.exp(-time * rtau))) *
			pitchScaleConst);
    }

    private void updateMap()
    {
      int leftx = getPixelsPer24thMicroseconds(prevNoteEventIn24thsOfMicrosec);
      int rightx = getPixelsPer24thMicroseconds(current24thsOfMicrosec);
      if (leftx < rightx)
	{
	  for (int pitch = minPitch; pitch < minPitch + pitchsize; pitch++)
	    if (keys[pitch] != 0)
	      for (int x = leftx; x < rightx; x++)
		{
		  long time;
		  try
		    {
		      time = ((Long)pixelTimeIn24thsOfMicrosec.
			      elementAt(x)).longValue();
		    }
		  catch (ArrayIndexOutOfBoundsException e)
		    {
		      out.print("WARNING: leftx=" + leftx);
		      out.print("; rightx=" + rightx);
		      out.println("; x=" + x);
		      out.flush();
		      time = current24thsOfMicrosec;
		    }
		  int y = getPixelFromPitch(pitch, time) - minPixel;
		  try
		    {
		      map[x][y] = keys[pitch];
		    }
		  catch (ArrayIndexOutOfBoundsException e)
		    {
		      out.print("WARNING: x=" + x + "; y=" + y);
		      out.println(" in map[" + map.length + "][" +
				  map[0].length + "]; " +
				  "pitch=" + pitch + "; time=" + time);
		      out.flush();
		      //break;
		    }
		}
	}
    }

    protected void execNoteOff(int channel, int pitch, int velocity)
	 throws IllegalArgumentException
    {
      if (mode == 0)
	{
	  updatePixelTimeIn24thsOfMicrosec();
	  updatePitchExtrema(pitch);
	}
      else if (mode == 1)
	{
	  updateMap();
	  keys[pitch] = 0;
	}
      prevNoteEventIn24thsOfMicrosec = current24thsOfMicrosec;
      super.execNoteOff(channel, pitch, velocity);
    }

    protected void execNoteOn(int channel, int pitch, int velocity)
	 throws IllegalArgumentException
    {
      if (mode == 0)
	{
	  updatePixelTimeIn24thsOfMicrosec();
	  updatePitchExtrema(pitch);
	}
      else if (mode == 1)
	{
	  updateMap();
	  keys[pitch] = (byte)Math.min(255.0, Math.max(0.0,
						       ((double)velocity) *
						       velocScaleConst));
	}
      prevNoteEventIn24thsOfMicrosec = current24thsOfMicrosec;
      super.execNoteOn(channel, pitch, velocity);
    }

    public void exec()
    {
      long tmp = current24thsOfMicrosec;
      switch (mode)
	{
	case 0: // evaluating xsize
	case 1: // creating bitmap image
	  current24thsOfMicrosec +=
	    (long)getDeltaTime() * microsecondsPerQuarterNote;
	  // type casting (long) is necessary here to avoid int overflow
	  // on multiplication
	  break;
	}
      super.exec();
    }
  }
}
