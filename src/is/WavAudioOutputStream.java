/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/is/WavAudioOutputStream.java.rca $
 * $Revision: 1.1 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Sat Jun 27 14:52:16 1998 $
 * $State: Experimental $
 */

/*
 * @(#)WavAudioOutputStream.java 1.00 98/02/22
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
 * Implements an audio output stream that conforms to the windows wav file
 * format.
 */
public class WavAudioOutputStream extends AudioOutputStream
{
  private int[] headerChunk =
  {
    0x52, 0x49, 0x46, 0x46, // chunk name "RIFF"
    0x00, 0x00, 0x00, 0x00, // chunk length; to be computed on the fly
    0x57, 0x41, 0x56, 0x45  // RIFF type "WAVE"
  };

  private int[] fmtChunk =
  {
    0x66, 0x6d, 0x74, 0x20, // chunk name "fmt "
    0x10, 0x00, 0x00, 0x00, // chunk length
    0x00, 0x00,             // format type (mono=0; stereo=1)
    0x01, 0x00,             // channel number
    0x44, 0xac, 0x00, 0x00, // sample rate in Hz
    0x88, 0x58, 0x01, 0x00, // bytes per second
    0x02, 0x00,             // bytes per sample
    0x10, 0x00              // bits per sample value
  };

  private int dataChunkLength;

  /**
   * Creates a new wav audio output stream.
   * The default format is 1 monophonic channel 16 bits linear with
   * 44100 samples per second.
   * @param out The underlying output stream.
   * @exception NullPointerException If out equals null.
   */
  public WavAudioOutputStream(OutputStream out)
  {
    super(out);
    // set default format
    channels = 1;
    bitsPerValue = 16;
    valuesPerSample = 1;
    encodingScheme = ENCODING_INTEGER;
    sampleRate = 44100;
  }

  /**
   * Returns a descriptive name of this audio file format.
   * @return A descriptive name of this audio file format.
   */
  public String getName()
  {
    return "Windows Wav File Format";
  }

  /**
   * Returns the maximum valid sample value.
   * This is usually a positive number.
   */
  public double getMaxValidSample()
  {
    if (bitsPerValue == 16)
      return 32767;
    else if (bitsPerValue == 8)
      return 127;
    else
      throw new IllegalStateException("invalid property: bitsPerValue");
  }

  /**
   * Returns the minimum valid sample value.
   * This may be a negative number.
   */
  public double getMinValidSample()
  {
    if (bitsPerValue == 16)
      return -32768;
    else if (bitsPerValue == 8)
      return -128;
    else
      throw new IllegalStateException("invalid property: bitsPerValue");
  }

  /**
   * Throws an IOException, if the properties of this wav audio output
   * stream are invalid. Also determines the file header data from the
   * properties.<BR>
   * The properties are determined by envoking the set methods of this class
   * with appropriate parameters, or by their respective default values.
   * @exception IOException If the properties of this wav audio output stream
   * are invalid.
   */
  public void checkProperties() throws IOException
  {
    int intSampleRate;
    if ((valuesPerSample < 1) || (valuesPerSample > 2))
      throw new IOException("values per sample not in the range [1..2]");
    if (channels != 1)
      throw new IOException("only single channel mode supported");
    try
      {
	intSampleRate = (int)sampleRate;
      }
    catch (Exception e)
      {
	throw new IOException("sample rate out of range");
      }
    if (encodingScheme != ENCODING_INTEGER)
      throw new IOException("only ENCODING_INTEGER valid");
    if ((bitsPerValue != 8) && (bitsPerValue != 16))
      throw new IOException("bits per value must be 8 or 16");
    fmtChunk[8] = valuesPerSample & 0xff;
    fmtChunk[9] = (valuesPerSample >> 8) & 0xff;
    fmtChunk[10] = channels & 0xff;
    fmtChunk[11] = (channels >>> 8) & 0xff;
    fmtChunk[12] = intSampleRate & 0xff;
    fmtChunk[13] = (intSampleRate >>> 8) & 0xff;
    fmtChunk[14] = (intSampleRate >>> 16) & 0xff;
    fmtChunk[15] = (intSampleRate >>> 24) & 0xff;
    int bytesPerSecond = bitsPerValue * valuesPerSample * intSampleRate / 8;
    fmtChunk[16] = bytesPerSecond & 0xff;
    fmtChunk[17] = (bytesPerSecond >>> 8) & 0xff;
    fmtChunk[18] = (bytesPerSecond >>> 16) & 0xff;
    fmtChunk[19] = (bytesPerSecond >>> 24) & 0xff;
    int bytesPerSample = bitsPerValue * valuesPerSample / 8;
    fmtChunk[20] = bytesPerSample & 0xff;
    fmtChunk[21] = (bytesPerSample >>> 8) & 0xff;
    fmtChunk[22] = bitsPerValue & 0xff;
    fmtChunk[23] = (bitsPerValue >>> 8) & 0xff;
    dataChunkLength = samples * bytesPerSample; // excludes chunk id/length
    int riffChunkLength =
      0x0c - 0x08 + // bare riff chunk length (excludes chunk id/length)
      0x18 + // fmt chunk total size (includes chunk id/length)
      dataChunkLength + 0x08; // data chunk total size (includes id/length)
    headerChunk[4] = riffChunkLength & 0xff;
    headerChunk[5] = (riffChunkLength >>> 8) & 0xff;
    headerChunk[6] = (riffChunkLength >>> 16) & 0xff;
    headerChunk[7] = (riffChunkLength >>> 24) & 0xff;
  }

  private void write_header() throws IOException
  {
    for (int i = 0; i < headerChunk.length; i++)
      write(headerChunk[i]);
    for (int i = 0; i < fmtChunk.length; i++)
      write(fmtChunk[i]);
    write((byte)'d');
    write((byte)'a');
    write((byte)'t');
    write((byte)'a');
    write(dataChunkLength & 0xff);
    write((dataChunkLength >>> 8) & 0xff);
    write((dataChunkLength >>> 16) & 0xff);
    write((dataChunkLength >>> 24) & 0xff);
  }

  private final static double HYSTERESIS = 1.0;
  private static int lastIntSample = 0;

  private int sampleConvert(double sample)
  {
    double doubleSample = sample * sampleScale;
    int intSample = (int)doubleSample;
    if (!(Math.abs(doubleSample - lastIntSample) > HYSTERESIS))
      intSample = lastIntSample;
    else
      lastIntSample = intSample;
    if (bitsPerValue == 16)
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
   * Writes an integer value to this wav audio output stream. The number of
   * relevant bits is determined through the bitsPerValue variable.
   * @param sample The sample value to be written, coded as a double value.
   * @exception IOException If an I/O error occurs.
   * @exception IllegalStateException If the encoding scheme is not set
   *    to ENCODING_INTEGER.
   * @see AudioOutputStream#encodingScheme
   */
  public void writeSample(double sample) throws IOException
  {
    if (encodingScheme != ENCODING_INTEGER)
      throw new IllegalStateException("invalid encoding scheme");
    if (state == STATE_CREATED)
      write_header();
    int intSample = sampleConvert(sample);
    write(intSample & 0xff);
    if (bitsPerValue == 16)
      write((intSample >>> 8) & 0xff);
  }
}
