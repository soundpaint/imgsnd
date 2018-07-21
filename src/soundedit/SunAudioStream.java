/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/soundedit/SunAudioStream.java.rca $
 * $Revision: 1.1 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Sat Jun 27 14:38:30 1998 $
 * $State: Experimental $
 */

/*
 * @(#)SunAudioStream.java 1.00 98/02/22
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
package soundedit;

import java.io.IOException;
import java.io.InputStream;

public class SunAudioStream extends InputStream
{
  private int[] header =
  {
    46, 115, 110, 100,            // ".snd"
    0, 0, 0, 40,                  // sample data starts at #28h
    0, 0, 0, 0,                   // possibly endless data
    0, 0, 0, 1,                   // 8 bit µ-law encoding
    0, 0, 31, 64,                 // 8kHz
    0, 0, 0, 1                    // 1 channel
  };

  private SampleInputStream sampleInputStream;
  private int index;
  private String title;
  private int titleLen;
  private boolean stopped = false;

  private SunAudioStream() {}

  /**
   * Creates a new audio stream that conforms to the sun audio format.
   * The data is in 8 bit µ-law encoded format and provides a single
   * channel of sound data.
   */
  public SunAudioStream(SampleInputStream sampleInputStream)
  {
    this.sampleInputStream = sampleInputStream;
    index = - header.length;
    title = "";
    titleLen = 0;
    setInputLevel(1.0f);
  }

  /**
   * This method is called shortly after initialization and each time
   * the global sample rate is about to change. It puts the given sample
   * rate into the header data of this stream.
   */
  public void setSampleRate(int sampleRate)
  {
    header[16] = (sampleRate >> 24) & 0xff;
    header[17] = (sampleRate >> 16) & 0xff;
    header[18] = (sampleRate >>  8) & 0xff;
    header[19] = (sampleRate >>  0) & 0xff;
  }

  private int level;

  /*
   * Allow 1% tolerance accuracy loss in MAX_VALUE for integer values.
   */
  private final static int MAX_VALUE = (int) (0.99 * Integer.MAX_VALUE);

  /**
   * Sets the input level of this audio stream.
   * @param level The input level. The input level is clipped to fit into
   *    the range [-1.0f, +1.0f].
   */
  public void setInputLevel(float level)
  {
    if (level < -1.0f) level = -1.0f;
    if (level > +1.0f) level = +1.0f;
    this.level = (int) (level * ((float) MAX_VALUE));
  }

  /**
   * Get the input level of this audio stream.
   * @return The input level. The input level is a value in the range
   *    [-1.0f, +1.0f].
   */
  public float getInputLevel()
  {
    return ((float) level) / ((float) MAX_VALUE);
  }

  /**
   * Returns the number of bytes that can be read from this audio stream
   * without blocking.
   * @exception IOException If an I/O error occurs.
   */
  public int available() throws java.io.IOException
  {
    if (stopped) return -1;
    else return sampleInputStream.available() - index;
  }

  /**
   * Repositions this stream so that the audio stream header is to be read
   * again. The audio data part of the stream, however, is not reset. This
   * means, after having read the header, the audio data continues where it
   * had stopped when setBack() was called.
   */
  private void setBack()
  {
    index = - header.length - title.length();
  }

  /**
   * Reads the next byte of data from this input stream. The value byte is
   * returned as an int in the range 0 to 255. If no byte is available because
   * the end of the stream has been reached, the value -1 is returned. This
   * method blocks until input data is available, the end of the stream is
   * detected, or an exception is thrown.
   * A subclass must provide an implementation of this method. 
   * @return The next byte of data, or -1 if the end of the stream is reached. 
   * @exception IOException If an I/O error occurs. 
   */
  public int read() throws IOException
  {
    int data;
    if (index >= 0)
      if (sampleInputStream != null)
	data = 64 + (byte)((((long) sampleInputStream.read()) * level) >> 56);
      else
	data = 64;
    else
      if (index < - titleLen)
	data = header[header.length + index++ + titleLen];
      else data = (byte) title.charAt(index++ + titleLen);
    return data;
  }

  /**
   * Skips over and discards n bytes of data from this input stream. The skip
   * method may, for a variety of reasons, end up skipping over some smaller
   * number of bytes, possibly 0. The actual number of bytes skipped is
   * returned.<BR>
   * The skip method of InputStream creates a byte array of length n and then
   * reads into it until n bytes have been read or the end of the stream has
   * been reached. Subclasses are encouraged to provide a more efficient
   * implementation of this method. 
   * @param n The number of bytes to be skipped. 
   * @return The actual number of bytes skipped. 
   * @exception IOException If an I/O error occurs.
   */
  public long skip(long n) throws IOException
  {
    long m = 0;
    if (index < 0)
      if (n < -index)
	{
	  index += n;
	  return n;
	}
      else
	{
	  n += index;
	  m -= index;
	}
    if (sampleInputStream != null)
      n = sampleInputStream.skip(n) + m;
    else
      n += m;
    index = 0;
    return n;
  }

  /**
   * Disables the current audio stream. Specifically, forces method available()
   * to return the value -1, regardless of the actual availability of
   * audio data.
   */
  public void disable() { stopped = true; }

  /**
   * Enables the current audio stream. Specifically, the audio stream is set
   * back to the start of its header, and method available() is no more
   * forced to return the value -1.
   */
  public void enable() { setBack(); stopped = false; }
}
