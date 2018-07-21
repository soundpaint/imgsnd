/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/is/SunAudioOutputStream.java.rca $
 * $Revision: 1.1 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Sat Jun 27 14:52:35 1998 $
 * $State: Experimental $
 */

/*
 * @(#)SunAudioOutputStream.java 1.00 98/02/22
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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Implements an audio output stream that conforms to the sun audio file
 * format.
 */
public class SunAudioOutputStream extends AudioOutputStream
{
  private int intSampleRate; // integer encoded sample rate
  private int format; // data format
  private int dataSize; // data size in bytes

  /**
   * Creates a new sun audio output stream.
   * The default format is 1 monophonic channel 8 bits mu-law with
   * 8000 samples per second.
   * @param out The underlying output stream.
   * @exception NullPointerException If out equals null.
   */
  public SunAudioOutputStream(OutputStream out)
  {
    super(out);
    // set default format
    channels = 1;
    bitsPerValue = 8;
    valuesPerSample = 1;
    encodingScheme = ENCODING_MU_LAW;
    sampleRate = 8000;
  }

  /**
   * Returns a descriptive name of this audio file format.
   * @return A descriptive name of this audio file format.
   */
  public String getName()
  {
    return "Sun Audio File Format";
  }

  /**
   * Returns the maximum valid sample value.
   * This is usually a positive number.
   */
  public double getMaxValidSample()
  {
    switch (encodingScheme)
      {
      case ENCODING_MU_LAW:
	if (bitsPerValue == 8)
	  return 127; // 8 bits mu-law
	break;
      case ENCODING_INTEGER:
	switch (bitsPerValue)
	  {
	  case 8:
	    return 127; // 8 bit integer linear
	  case 16:
	    return 32767; // 16 bit integer linear
	  case 24:
	    return 8388607; // 24 bit integer linear
	  case 32:
	    return 2147483647; // 32 bit integer linear
	  }
	break;
      case ENCODING_FLOAT:
	switch (bitsPerValue)
	  {
	  case 32:
	    return Float.MAX_VALUE; // 32 bit float linear
	  case 64:
	    return Double.MAX_VALUE; // 64 bit float linear
	  }
	break;
      case ENCODING_FIXED:
	// not supported
	break;
      default:
	throw new IllegalStateException("invalid encoding scheme");
      }
    throw new IllegalStateException("invalid properties");
  }

  /**
   * Returns the minimum valid sample value.
   * This may be a negative number.
   */
  public double getMinValidSample()
  {
    switch (encodingScheme)
      {
      case ENCODING_MU_LAW:
	if (bitsPerValue == 8)
	  return 0; // 8 bits mu-law
	break;
      case ENCODING_INTEGER:
	switch (bitsPerValue)
	  {
	  case 8:
	    return -128; // 8 bit integer linear
	  case 16:
	    return -32768; // 16 bit integer linear
	  case 24:
	    return -8388608; // 24 bit integer linear
	  case 32:
	    return -2147483648; // 32 bit integer linear
	  }
	break;
      case ENCODING_FLOAT:
	switch (bitsPerValue)
	  {
	  case 32:
	    return Float.MIN_VALUE; // 32 bit float linear
	  case 64:
	    return Double.MIN_VALUE; // 64 bit float linear
	  }
	break;
      case ENCODING_FIXED:
	// not supported
	break;
      default:
	throw new IllegalStateException("invalid encoding scheme");
      }
    throw new IllegalStateException("invalid properties");
  }

  /**
   * Throws an IOException, if the properties of this sun audio output
   * stream are invalid. Also determines the file header data from the
   * properties.<BR>
   * The properties are determined by envoking the set methods of this class
   * with appropriate parameters, or by their respective default values.
   * @exception IOException If the properties of this sun audio output stream
   * are invalid.
   */
  public void checkProperties() throws IOException
  {
    format = -1;
    if (valuesPerSample != 1)
      throw new IOException("only one value per sample supported");
    try
      {
	intSampleRate = (int)sampleRate;
      }
    catch (Exception e)
      {
	throw new IOException("sample rate out of range");
      }
    switch (encodingScheme)
      {
      case ENCODING_MU_LAW:
	if (bitsPerValue == 8)
	  format = 1; // 8 bits mu-law
	break;
      case ENCODING_INTEGER:
	switch (bitsPerValue)
	  {
	  case 8:
	    format = 2; break; // 8 bit integer linear
	  case 16:
	    format = 3; break; // 16 bit integer linear
	  case 24:
	    format = 4; break; // 24 bit integer linear
	  case 32:
	    format = 5; break; // 32 bit integer linear
	  }
	break;
      case ENCODING_FLOAT:
	switch (bitsPerValue)
	  {
	  case 32:
	    format = 6; break; // 32 bit float linear
	  case 64:
	    format = 7; break; // 64 bit float linear
	  }
	break;
      case ENCODING_FIXED:
	// not supported
	break;
      default:
	throw new IOException("invalid encoding scheme");
      }
    if (format < 0)
      throw new IOException("unsupported or invalid properties combination");
    dataSize = samples * bitsPerValue * valuesPerSample * channels / 8;
  }

  private void write_header() throws IOException
  {
    int pos = info.length() + 0x0018;
    write((byte)'.');
    write((byte)'s');
    write((byte)'n');
    write((byte)'d');
    write((pos >> 24) & 0xff);
    write((pos >> 16) & 0xff);
    write((pos >>  8) & 0xff);
    write((pos >>  0) & 0xff);
    write((dataSize >> 24) & 0xff);
    write((dataSize >> 16) & 0xff);
    write((dataSize >>  8) & 0xff);
    write((dataSize >>  0) & 0xff);
    write((format >> 24) & 0xff);
    write((format >> 16) & 0xff);
    write((format >>  8) & 0xff);
    write((format >>  0) & 0xff);
    write((intSampleRate >> 24) & 0xff);
    write((intSampleRate >> 16) & 0xff);
    write((intSampleRate >>  8) & 0xff);
    write((intSampleRate >>  0) & 0xff);
    if (info != null)
      for (int i = 0; i < info.length(); i++)
	write((byte)info.charAt(i));
    state = STATE_DATA_WRITTEN;
  }

  private final static double HYSTERESIS = 1.0;
  private static int lastIntSample = 0;

  private int sampleConvert1(double sample)
  {
    double doubleSample = sample * sampleScale;
    int intSample = (int)doubleSample;
    if (!(Math.abs(doubleSample - lastIntSample) > HYSTERESIS))
      intSample = lastIntSample;
    else
      lastIntSample = intSample;
    if (intSample < 0)
      intSample = 0;
    else if (intSample > 127)
      intSample = 127;
    return (intSample >>> 1) + 64;
  }

  private int sampleConvert2(double sample)
  {
    double doubleSample = sample * sampleScale;
    int intSample = (int)doubleSample;
    if (!(Math.abs(doubleSample - lastIntSample) > HYSTERESIS))
      intSample = lastIntSample;
    else
      lastIntSample = intSample;
    if (bitsPerValue == 32)
      if (intSample < -2147483648)
	intSample = -2147483648;
      else if (intSample > 2147483647)
	intSample = 2147483647;
      else {}
    else if (bitsPerValue == 24)
      if (intSample < -8388608)
	intSample = -8388608;
      else if (intSample > 8388607)
	intSample = 8388607;
      else {}
    else if (bitsPerValue == 16)
      if (intSample < -32768)
	intSample = -32768;
      else if (intSample > 32767)
	intSample = 32767;
      else {}
    else if (bitsPerValue == 8)
      if (intSample < -128)
	intSample = -128;
      else if (intSample > 127)
	intSample = 127;
      else {}
    else
      throw new IllegalStateException("invalid bitsPerValue");
    return intSample;
  }

  /**
   * Writes a sample value to this sun audio output stream. The sample is
   * coded as double value, but may represent both, a floating/fixed point
   * value or an integer value.
   * The representation and number of relevant bits is determined through
   * the encodingScheme and bitsPerValue variables.
   * @param sample The sample value to be written.
   * @exception IOException If an I/O error occurs.
   * @see AudioOutputStream#encodingScheme
   * @see AudioOutputStream#bitsPerValue
   */
  public void writeSample(double sample) throws IOException
  {
    if (state == STATE_CREATED)
      write_header();
    int intSample;
    switch (encodingScheme)
      {
      case ENCODING_MU_LAW:
	intSample = sampleConvert1(sample);
	if (bitsPerValue >= 16)
	  throw new IllegalStateException("invalid bitsPerSample");
	write(intSample & 0xff);
	break;
      case ENCODING_INTEGER:
	intSample = sampleConvert2(sample);
	if (bitsPerValue >= 32)
	  write((intSample >>> 24) & 0xff);
	if (bitsPerValue >= 24)
	  write((intSample >>> 16) & 0xff);
	if (bitsPerValue >= 16)
	  write((intSample >>> 8) & 0xff);
	write(intSample & 0xff);
	break;
      case ENCODING_FLOAT:
	if (bitsPerValue == 32)
	  {
	    intSample = Float.floatToIntBits((float)sample);
	    write((intSample >>> 24) & 0xff);
	    write((intSample >>> 16) & 0xff);
	    write((intSample >>> 8) & 0xff);
	    write(intSample & 0xff);
	  }
	else // 64 bits
	  {
	    long longSample = Double.doubleToLongBits(sample);
	    int upper = (int)(longSample >>> 32);
	    int lower = (int)(longSample);
	    write((upper >>> 24) & 0xff);
	    write((upper >>> 16) & 0xff);
	    write((upper >>> 8) & 0xff);
	    write(upper & 0xff);
	    write((lower >>> 24) & 0xff);
	    write((lower >>> 16) & 0xff);
	    write((lower >>> 8) & 0xff);
	    write(lower & 0xff);
	  }
	break;
      case ENCODING_FIXED:
	throw new IllegalStateException("encoding scheme fixed not supported");
      default:
	throw new IllegalStateException("invalid encoding scheme");
      }
  }
}
