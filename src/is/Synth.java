/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/is/Synth.java.rca $
 * $Revision: 1.3 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Tue Jul 21 23:57:35 1998 $
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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.io.DataOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

/**
 * This class performs the FM synthesis from some input graphics file
 * and writes the result into a file.
 */
class Synth implements ProgressDisplayModel
{
  private final static double CD_RESOLUTION = 16.0; // [bits]
  private final static double LOG_2_10 = Math.log(10.0) / Math.log(2.0);

  private PrintWriter stdout;
  private String inFile;
  private String outFile;
  private InputStream in;
  private double sampleRate = 8000.0; // [Hz]
  private double sampleLength = 10.0; // [s]
  private double minFreq = 20; // [Hz]
  private double maxFreq = 4000; // [Hz]
  private double spctDist = 0; // [cents]
  private double tau = 0.02; // [s]
  private double dynamics = 20.0 * CD_RESOLUTION / LOG_2_10; // [dB]
  private boolean padding = true;
  private boolean maxprecision = false;
  private String info = null;
  private boolean nativeSynth = false;

  private Synth() {}

  /**
   * Creates a new Synth object.
   * @param stdout The output device to report messages.
   * @param inFile The source input file path.
   * @param outFile The destination output file path.
   * @exception IOException If an I/O error occurs.
   * @exception FileNotFoundException If the input file is not found.
   */
  Synth(PrintWriter stdout, String inFile, String outFile)
       throws IOException, FileNotFoundException
  {
    this.stdout = stdout;
    this.inFile = inFile;
    this.outFile = (outFile != null) ? outFile : outFileFromInFile(inFile);
    info = "Creator: " + ImageSound.VERSION + " from file " + inFile;
    in = new FileInputStream(inFile);
  }

  /**
   * Sets the sample rate. The default sample rate is 8000.0Hz.
   * @param sampleRate The sample rate in Hz. This should be a positive
   *    value.
   * @see #setMinFreq
   * @see #setMaxFreq
   * @see #setSpctDist
   */
  void setSampleRate(double sampleRate)
  {
    if (sampleRate <= 0.0)
      throw new IllegalArgumentException("sample rate out of range");
    this.sampleRate = sampleRate;
  }

  /**
   * Sets the sample length. The default sample length is 10 seconds.
   * @param sampleLength The sample length in seconds. This should be a value
   *    in the range 0.0 through 3600.0.
   */
  void setSampleLength(double sampleLength)
  {
    if ((sampleLength < 0.0) || (sampleLength > 3600.0))
      throw new IllegalArgumentException("sample length out of range");
    this.sampleLength = sampleLength;
  }

  /**
   * Sets the minimal frequency. The default minimal frequency is 20Hz.
   * @param minFreq The minimal frequency in Hz. This should be a value
   *    in the range 1 through 200000 and should be much smaller than
   *    half of the sample rate.
   * @see #setSampleRate
   */
  void setMinFreq(double minFreq)
  {
    if ((minFreq < 1.0) || (minFreq > 200000.0))
      throw new IllegalArgumentException("minimal frequency out of range");
    this.minFreq = minFreq;
  }

  /**
   * Sets the maximal frequency. The default maximal frequency is 4000Hz.
   * This is an alternative to define spctDist. If spctDist is defined,
   * the value of maxFreq becomes irrelevant.
   * @param maxFreq The maximal frequency in Hz. This should be a value
   *    in the range 1 through 200000 and should be not greater than
   *    half of the sample rate.
   * @see #setSampleRate
   * @see #setSpctDist
   */
  void setMaxFreq(double maxFreq)
  {
    if ((maxFreq < 1.0) || (maxFreq > 200000.0))
      throw new IllegalArgumentException("maximal frequency out of range");
    this.maxFreq = maxFreq;
  }

  /**
   * Sets the spectral distance per pixel. This is an alternative to define
   * maxFreq. The default spectral distance is undefined, indicating that
   * maxFreq should be used instead.
   * @param spctDist The spectral distance per pixel in cents. This should
   *    be a value in the range 1 through 12000 and should lead to a maximal
   *    frequency not greater than half of the sample rate. A value of 0
   *    indicates that the spectral distance is undefined and maxFreq should
   *    be used instead.
   * @see #setSampleRate
   * @see #setMaxFreq
   */
  void setSpctDist(double spctDist)
  {
    if ((spctDist < 0.0) || (spctDist > 12000.0))
      throw new IllegalArgumentException("spectral distance out of range");
    this.spctDist = spctDist;
  }

  /**
   * Sets the tau constant. All amplitude data is low-pass filtered over time
   * individually for each spectral line. The tau constant specifies the
   * half-life period in seconds of the low-pass. The default value is 0.02s.
   * @param tau The half-life period in seconds for the amplitude low-pass.
   */
  void setTau(double tau)
  {
    this.tau = tau;
  }

  /**
   * Sets the dynamics constant. This constant is used to define the
   * minimal output amplitude level that is taken into account. Sound
   * with an amplitude below that level may be regarded as total
   * silence for performance enhancement and for handling of padding
   * (see below). The value applies for each frequency (thus neglecting
   * that the audible limit depends on frequency).  The default value
   * is 20*16/ld(10)dB = 96.32959861dB, which is typical for 16 bit
   * resolution of CD Audio quality. A low value may result in poor
   * audio quality (and a shorter padding), but may enhance performance
   * significantly.
   * @param dynamics The max/min amplitude ratio in dB.
   * @see #setPadding
   * @see #setMaxPrecision
   */
  void setDynamics(double dynamics)
  {
    this.dynamics = dynamics;
  }

  /**
   * By default, ImageSound tries to avoid audible cracks at the end
   * of the sound data. This is done by adding additional time beyond
   * the last row or column of the image, until the sound has calmed
   * down below the audible limit. This limit is computed from the
   * dynamics option (see above). When setting padding to false,
   * padding is turned off - possibly resulting in an audible crack.
   * @param padding If false, turn off padding. Otherwise turn it on.
   * @see #setDynamics
   */
  void setPadding(boolean padding)
  {
    this.padding = padding;
  }

  /**
   * By default, ImageSound tries to speed up computation by ignoring
   * all amplitudes that run below the audible limit. The limit is
   * specified by the dynamics variable. When setting maxprecision to
   * true, ImageSound will not ignore these amplitudes.
   * @param maxprecision If false, allow to ignore amplitudes below
   *    the audible limit. Otherwise, force computation with maximal
   *    precision.
   * @see #setDynamics
   */
  void setMaxPrecision(boolean maxprecision)
  {
    this.maxprecision = maxprecision;
  }

  /**
   * Sets the info String. The default is a creator message including
   * the input file name.
   * @param info The info String.
   */
  void setInfo(String info)
  {
    this.info = info;
  }

  /**
   * Automatically creates a file name for the output file from the input file.
   */
  private String outFileFromInFile(String inFile)
  {
    if (inFile.endsWith(".pnm") || inFile.endsWith(".pbm") ||
	inFile.endsWith(".pgm") || inFile.endsWith(".ppm"))
      return inFile.substring(0, inFile.length() - 4) + ".sam";
    else
      return inFile + ".sam";
  }

  /**
   * Actually starts the compilation.
   * @param nativeSynth True, if native code is to be used for synthesis.
   *    False, if java code is to be used. In general, the native code is
   *    much faster than the java code, but it is platform dependend and
   *    hence available only for some platforms.
   */
  void synthesize(boolean nativeSynth) throws IOException
  {
    stdout.println("reading image file...");
    stdout.flush();
    PNMReader reader = new PNMReader(inFile, in, stdout);
    stdout.println("starting synthesis...");
    stdout.flush();
    this.nativeSynth = nativeSynth;
    if (nativeSynth)
      native_synthesize(reader);
    else
      synthesize(reader);
    in.close();
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
   * Note: This only works for doubles in the +/- range of a 1000th
   * MAXINT value.
   */
  private static String percentString(double d)
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
  private static String msString(long ms)
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

  private int sampleCount = 0;
  private int totalSamples = 0;
  private Date startDate;
  private int bytesPerSample = 8;

  /**
   * Refreshes the display that shows the current progress.
   * This must be implemented by a subclass when using this class.
   */
  public void refreshDisplay()
  {
    if (nativeSynth)
      sampleCount = SynthNative.getSampleCount();
    long nowtime = new Date().getTime();
    long runningtime = nowtime - startDate.getTime();
    double percent = (double)sampleCount / totalSamples;
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

  private double signum(double x)
  {
    return (x < 0) ? -1.0 : (x > 0) ? +1.0 : 0.0;
  }

  // absolute sample values below this limit are regarded to be equal to 0.0
  private final static double EPSILON = 1.0 / 2147483648.0;

  private RandomAccessFile out;

  private final static int BUFFER_SIZE = 2048; // must be a multiple of 8
  private byte[] outBuffer = new byte[BUFFER_SIZE];
  private int bufferIndex = 0;

  private void outFlush() throws IOException
  {
    out.write(outBuffer, 0, bufferIndex);
    bufferIndex = 0;
  }

  private void outWriteDouble(double d) throws IOException
  {
    long l = Double.doubleToLongBits(d);
    outBuffer[bufferIndex++] = (byte)((l >>> 56) & 0xFF);
    outBuffer[bufferIndex++] = (byte)((l >>> 48) & 0xFF);
    outBuffer[bufferIndex++] = (byte)((l >>> 40) & 0xFF);
    outBuffer[bufferIndex++] = (byte)((l >>> 32) & 0xFF);
    outBuffer[bufferIndex++] = (byte)((l >>> 24) & 0xFF);
    outBuffer[bufferIndex++] = (byte)((l >>> 16) & 0xFF);
    outBuffer[bufferIndex++] = (byte)((l >>>  8) & 0xFF);
    outBuffer[bufferIndex++] = (byte)((l >>>  0) & 0xFF);
    if (bufferIndex == BUFFER_SIZE)
      outFlush();
  }

  /**
   * Synthesizes the output.
   * @param reader The PNM reader.
   */
  private void synthesize(PNMReader reader) throws IOException
  {
    out = new RandomAccessFile(outFile, "rw");
    stdout.println("input file       = " + inFile);
    stdout.println("output file      = " + outFile);
    int xsize = reader.getXSize();
    int ysize = reader.getYSize();
    stdout.println("image x size     = " + xsize + " pixels");
    stdout.println("image y size     = " + ysize + " pixels");
    stdout.println("sample rate      = " + (float)sampleRate + "Hz");
    stdout.println("sample length    = " + (float)sampleLength + "s");
    totalSamples = (int)(sampleRate * sampleLength);
    stdout.println("total #samples   = " + totalSamples);
    double expGrowth = (spctDist == 0.0) ?
      Math.log(maxFreq / minFreq) / (xsize - 1) :
      spctDist * Math.log(2.0) / 1200;
    double expScale = 2.0 * Math.PI * minFreq / sampleRate;
    double epsilon = 1.0 / Math.pow(2.0, LOG_2_10 / 20.0 * dynamics);
    double[] freq = new double[xsize];
    double[] amp = new double[xsize];
    for (int i = 0; i < xsize; i++)
      {
	freq[i] = expScale * Math.exp(i * expGrowth);
	amp[i] = 0.0;
      }
    stdout.println("min frequency    = " +
		   (float)(freq[0] * sampleRate / 2.0 / Math.PI) +
		   "Hz");
    stdout.println("max frequency    = " +
		   (float)(freq[xsize - 1] * sampleRate / 2.0 / Math.PI) +
		   "Hz");
    stdout.println("spectrum resol.  = " +
		   (float)(Math.log(2.0) / expGrowth) +
		   " spectral lines per octave");
    stdout.println("spectral dist.   = " +
		   (float)(1200 * expGrowth / Math.log(2.0)) +
		   " cents");
    stdout.println("tau              = " + (float)tau + "s");
    if (freq[xsize - 1] > Math.PI)
      {
	stdout.println("WARNING: max frequency is higher than half sample ");
	stdout.println("         rate. This may lead to poor audio quality.");
      }
    stdout.flush();

    // write header chunk
    double minSampleValue = + 1.0 / 0.0; // +infty
    double maxSampleValue = - 1.0 / 0.0; // -infty
    double avgSampleValue = 0.0;
    out.writeUTF(".sam"); // magic number
    out.writeInt(0x20 + (info != null ?
			 info.length() + 2 : 0)); // header chunk length
    out.writeUTF("00010000"); // version id
    out.writeShort(1); // mono
    out.writeShort(1); // one channel
    out.writeInt(0x0007); // 64 bit float linear encoding
    out.writeInt((int)sampleRate); // sample rate
    if (info != null)
      out.writeUTF(info);

    // write data chunk
    out.writeUTF("data"); // magic number
    long fp = out.getFilePointer(); // remember this location
    out.writeInt(0); // placeholder for data chunk length
    out.writeDouble(0.0); // placeholder for minSample
    out.writeDouble(0.0); // placeholder for maxSample
    out.writeDouble(0.0); // placeholder for avgSample
    double sample[] = new double[totalSamples / ysize + 1];
    double sigma = Math.exp( -(1.0 / sampleRate) / tau);
    boolean stillPadding = padding;
    sampleCount = 0;
    startDate = new Date();
    refreshDisplay();
    ProgressDisplay pd = new ProgressDisplay(this);
    pd.start();

    for (int y = 0; (y < ysize) || stillPadding; y++)
      {
	int n = (int)(((long)totalSamples) * (y + 1) / ysize) - sampleCount;
	for (int i = 0; i < n; i++)
	  sample[i] = 0.0;
	double freqx, ampx, ampx1, t;
	for (int x = 0; x < xsize; x++)
	  {
	    ampx = amp[x];
	    ampx1 = (y < ysize) ? reader.getGrayPixel(x, y) * (1 - sigma) : 0;
	    if ((ampx1 >= epsilon) || (ampx >= epsilon) || maxprecision)
	      {
		freqx = freq[x];
		t = freqx * sampleCount;
		for (int i = 0; i < n; i++)
		  {
		    sample[i] += ampx * Math.sin(t);
		    t += freqx;
		    ampx *= sigma;
		    ampx += ampx1;
		  }
		amp[x] = ampx;
		try
		  {
		    // For some reason, linux-jdk1.1 seems to treat threads
		    // cooperatively (is this a bug in linux-jdk1.1?).
		    // Hence, we have to add a sleep() here to make the
		    // progress display run.
		    Thread.sleep(1);
		  }
		catch (InterruptedException e)
		  {
		    throw new IOException(e.toString());
		  }
	      }
	  }
	for (int i = 0; i < n; i++)
	  {
	    double samplei = sample[i];

	    // write out the enhanced sample
	    outWriteDouble(samplei);

	    // update min/max/average values
	    if (samplei > maxSampleValue)
	      maxSampleValue = samplei;
	    if (samplei < minSampleValue)
	      minSampleValue = samplei;
	    avgSampleValue += Math.abs(samplei);
	  }
	sampleCount += n;
	if ((y >= ysize - 1) && stillPadding)
	  {
	    stillPadding = false;
	    for (int x = 0;
		 (x < xsize) && !(stillPadding = stillPadding ||
				  (amp[x] >= epsilon));
		 x++) {}
	  }
      }
    outFlush();

    pd.stop();
    refreshDisplay();
    Date endDate = new Date();
    long time = (endDate.getTime() - startDate.getTime()) / 10;
    stdout.println();
    stdout.println("computing time   = " + (time / 100) + "." + (time % 100) +
		   "s");
    avgSampleValue /= totalSamples;
    stdout.println("min sample value = " + (float)minSampleValue);
    stdout.println("max sample value = " + (float)maxSampleValue);
    stdout.println("avg sample value = " + (float)avgSampleValue);
    stdout.flush();
    out.seek(fp); // go back to the placeholders
    out.writeInt((sampleCount << 3) + 0x1a); // data chunk length
    out.writeDouble(minSampleValue);
    out.writeDouble(maxSampleValue);
    out.writeDouble(avgSampleValue);
  }

  public void sleep()
  {
    try
      {
	Thread.sleep(1);
      }
    catch (InterruptedException e)
      {
	stdout.println("WARNING: sleep interrupted");
      }
  }

  /**
   * Synthesizes the output, but uses native code.
   * @param reader The PNM reader.
   * @param out The output file.
   */
  private void native_synthesize(PNMReader reader) throws IOException
  {
    RandomAccessFile out = new RandomAccessFile(outFile, "rw");
    stdout.println("input file       = " + inFile);
    stdout.println("output file      = " + outFile);
    int xsize = reader.getXSize();
    int ysize = reader.getYSize();
    stdout.println("image x size     = " + xsize + " Pixels");
    stdout.println("image y size     = " + ysize + " Pixels");
    stdout.println("sample rate      = " + (float)sampleRate + "Hz");
    stdout.println("sample length    = " + (float)sampleLength + "s");
    totalSamples = (int)(sampleRate * sampleLength);
    stdout.println("total #samples   = " + totalSamples);
    double expGrowth = (spctDist == 0.0) ?
      Math.log(maxFreq / minFreq) / (xsize - 1) :
      spctDist * Math.log(2.0) / 1200;
    double expScale = 2.0 * Math.PI * minFreq / sampleRate;
    stdout.println("min frequency    = " +
		   (float)(expScale * sampleRate / 2.0 / Math.PI) +
		   "Hz");
    stdout.println("max frequency    = " +
		   (float)(expScale * Math.exp((xsize - 1) * expGrowth) *
			   sampleRate / 2.0 / Math.PI) + "Hz");
    stdout.println("spectrum resol.  = " +
		   (float)(Math.log(2.0) / expGrowth) +
		   " spectral lines per octave");
    stdout.println("spectral dist.   = " +
		   (float)(1200 * expGrowth / Math.log(2.0)) +
		   " cents");
    stdout.flush();

    // write header chunk
    out.writeUTF(".sam"); // magic number
    out.writeInt(0x20 + (info != null ?
			 info.length() + 2 : 0)); // header chunk length
    out.writeUTF("00010000"); // version id
    out.writeShort(1); // mono
    out.writeShort(1); // one channel
    out.writeInt(0x0007); // 64 bit float linear encoding
    out.writeInt((int)sampleRate); // sample rate
    if (info != null)
      out.writeUTF(info);

    // write data chunk
    out.writeUTF("data"); // magic number
    out.writeInt((totalSamples << 3) + 0x1a); // data chunk length
    long fp = out.getFilePointer(); // remember this location
    out.writeDouble(0.0); // placeholder for minSample
    out.writeDouble(0.0); // placeholder for maxSample
    out.writeDouble(0.0); // placeholder for avgSample

    startDate = new Date();
    refreshDisplay();
    ProgressDisplay pd = new ProgressDisplay(this);
    pd.start();
    SynthNative.synth(xsize, ysize, totalSamples, expScale, expGrowth,
		      this, reader, out);
    pd.stop();
    refreshDisplay();
    Date endDate = new Date();
    long time = (endDate.getTime() - startDate.getTime()) / 10;
    stdout.println();
    stdout.println("computing time   = " + (time / 100) + "." + (time % 100) +
		   "s");
    double minSampleValue = SynthNative.getMinSampleValue();
    double maxSampleValue = SynthNative.getMaxSampleValue();
    double avgSampleValue = SynthNative.getAvgSampleValue();
    stdout.println("min sample value = " + (float)minSampleValue);
    stdout.println("max sample value = " + (float)maxSampleValue);
    stdout.println("avg sample value = " + (float)avgSampleValue);
    stdout.flush();
    out.seek(fp); // go back to the placeholders
    out.writeDouble(minSampleValue);
    out.writeDouble(maxSampleValue);
    out.writeDouble(avgSampleValue);
  }
}
