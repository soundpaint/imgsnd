/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/is/AudioOutputStream.java.rca $
 * $Revision: 1.2 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Tue Jul 21 23:57:37 1998 $
 * $State: Experimental $
 */

/*
 * @(#)AudioOutputStream.java 1.00 98/02/22
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

import java.io.OutputStream;
import java.io.IOException;

/**
 * This abstract class serves as parent class for any audio file format
 * OutputStream object.
 */
abstract public class AudioOutputStream extends OutputStream
{

  /**
   * Denotes an encoding scheme, where sample values are stored as
   * mu-law encoded integer values.
   */
  public final static int ENCODING_MU_LAW = 0;

  /**
   * Denotes an encoding scheme, where sample values are stored as
   * linear integer values.
   */
  public final static int ENCODING_INTEGER = 1;

  /**
   * Denotes an encoding scheme, where sample values are stored as
   * linear floating point real values.
   */
  public final static int ENCODING_FLOAT = 2;

  /**
   * Denotes an encoding scheme, where sample values are stored as
   * linear fixed point real values.
   */
  public final static int ENCODING_FIXED = 3;

  /**
   * The uppermost encoding scheme value that is pre-defined in this class.
   * This is useful when a subclass wants to define additional encoding
   * scheme values.
   * @see #getState
   */
  public final static int ENCODING_MAX = ENCODING_FIXED;

  /**
   * Denotes the state of this audio output stream when it has been created
   * but no data has been written out so far.
   * @see #getState
   */
  public final static int STATE_CREATED = 0;

  /**
   * Denotes the state of this audio output stream when some data has already
   * been written to the stream.
   * @see #getState
   */
  public final static int STATE_DATA_WRITTEN = 1;

  /**
   * Denotes the state of this audio output stream when it has been closed.
   * @see #getState
   */
  public final static int STATE_CLOSED = 2;

  /**
   * The uppermost state value that is pre-defined in this class.
   * This is useful when a subclass wants to define additional state
   * values.
   * @see #getState
   */
  public final static int STATE_MAX = STATE_CLOSED;

  /**
   * The underlying output stream.
   */
  protected OutputStream out;

  /**
   * The total number of samples that is expected to be written
   * to this audio output stream. The default is 0.
   * @see #setSamples
   */
  protected int samples = 0;

  /**
   * The number of bits that make up a single sample value.
   * The default is 8.
   * @see #setBitsPerValue
   */
  protected int bitsPerValue = 1;

  /**
   * The number of values that make up a single sample.
   * The default is 1 (monophonic sound).
   * @see #setValuesPerSample
   */
  protected int valuesPerSample = 1;

  /**
   * The number of channels in this audio output stream.
   * The default is 1.
   * @see #setChannels
   */
  protected int channels = 1;

  /**
   * The encoding scheme to be used in this audio output stream.
   * The default is ENCODING_INTEGER_LINEAR.
   * @see #setEncodingScheme
   */
  protected int encodingScheme = ENCODING_INTEGER;

  /**
   * The sample rate in samples per second.
   * The default is 44100 samples per second.
   * @see #setSampleRate
   */
  protected double sampleRate = 44100;

  /**
   * The informative string to be included in the audio output stream data.
   * The default is not to include at all a string.
   * @see #setInfo
   */
  protected String info = null;

  /**
   * The current state of this audio output stream.
   * This is initially set to the state STATE_CREATED.
   * @see #getState
   * @see #STATE_CREATED
   */
  protected int state = STATE_CREATED;

  /**
   * A factor that is used to scale the amplitude of all
   * sample values. Any subclass should encounter this value
   * when writing out sample values via method writeSample().
   * This is initially set to the value 1.0.
   * @see #setSampleScale
   * @see #writeSample(long)
   * @see #writeSample(double)
   */
  protected double sampleScale = 1.0;

  private AudioOutputStream() {}

  /**
   * Creates a new audio output stream that conforms to a certain audio
   * file format.
   * @param out The underlying output stream.
   * @exception NullPointerException If out equals null.
   */
  public AudioOutputStream(OutputStream out)
  {
    if (out == null)
      throw new NullPointerException("out");
    this.out = out;
  }

  /**
   * Returns a descriptive name of the audio file format.
   * @return A descriptive name of the audio file format.
   */
  abstract public String getName();

  /**
   * Returns the maximum valid sample value.
   * This is usually a positive number.
   */
  abstract public double getMaxValidSample();

  /**
   * Returns the minimum valid sample value.
   * This may be a negative number.
   */
  abstract public double getMinValidSample();

  /**
   * This method sets the total number of samples that is expected to be
   * written to this audio output stream. To be effective, it must be called
   * <EM>before</EM> writing to this stream. If the number is not known a
   * priori, a zero number (the default value) may be appropriate for
   * most applications (although, in general, this can not be suggested).
   * @param samples The total number of samples.
   * @exception IllegalArgumentException If samples is out of range;
   *    by default, if it is below 0.
   * @exception IllegalStateException If the state of this stream equals
   *    STATE_DATA_WRITTEN or STATE_CLOSED.
   * @see #getState
   * @see #getSamples
   * @see #samples
   */
  public void setSamples(int samples)
  {
    if (state == STATE_DATA_WRITTEN)
      throw new IllegalStateException("data already written to this stream");
    if (state == STATE_CLOSED)
      throw new IllegalStateException("audio output stream closed");
    if (samples < 0)
      throw new IllegalArgumentException("samples < 0");
    this.samples = samples;
  }

  /**
   * This method returns the total number of samples as it has been set
   * by using method setSamples.
   * @return The total number of samples.
   * @see #setSample
   */
  public int getSample() { return samples; }

  /**
   * This method sets the number of bits per sample value. To be effective,
   * it must be called <EM>before</EM> writing to this stream.
   * @param bitsPerValue The number of bits per sample value.
   * @exception IllegalArgumentException If bitsPerValue is out of range;
   *    by default, if it is below 1.
   * @exception IllegalStateException If the state of this stream equals
   *    STATE_DATA_WRITTEN or STATE_CLOSED.
   * @see #getState
   * @see #getBitsPerValue
   * @see #bitsPerValue
   */
  public void setBitsPerValue(int bitsPerSample)
  {
    if (state == STATE_DATA_WRITTEN)
      throw new IllegalStateException("data already written to this stream");
    if (state == STATE_CLOSED)
      throw new IllegalStateException("audio output stream closed");
    if (bitsPerValue < 1)
      throw new IllegalArgumentException("bitsPerValue < 1");
    this.bitsPerValue = bitsPerValue;
  }

  /**
   * This method returns the number of bits per sample value as it has been
   * set by using method setBitsPerValue.
   * @return The number of bits per sample value.
   * @see #setBitsPerValue
   */
  public int getBitsPerValue() { return bitsPerValue; }

  /**
   * This method sets the number of values that make up a single sample.
   * @param valuesPerSample The number of values per sample
   *    (1=mono; 2=stereo).
   * @exception IllegalArgumentException If valuesPerSample is out of range;
   *    by default, if valuesPerSample < 1.
   * @exception IllegalStateException If the state of this stream equals
   *    STATE_DATA_WRITTEN or STATE_CLOSED.
   * @see #getState
   * @see #getValuesPerSample
   * @see #valuesPerSample
   */
  public void setValuesPerSample(int valuesPerSample)
  {
    if (state == STATE_DATA_WRITTEN)
      throw new IllegalStateException("data already written to this stream");
    if (state == STATE_CLOSED)
      throw new IllegalStateException("audio output stream closed");
    if (valuesPerSample < 1)
      throw new IllegalArgumentException("valuesPerSample < 1");
    this.valuesPerSample = valuesPerSample;
  }

  /**
   * This method returns the number of values that make up a single sample
   * as it has been set by using method setValuesPerSample.
   * @return The number of values per sample.
   * @see #setValuesPerSample
   */
  public int getValuesPerSample() { return valuesPerSample; }

  /**
   * This method sets the number of channels of this audio output stream.
   * To be effective, it must be called <EM>before</EM> writing to this
   * stream.
   * @param channels The number of channels.
   * @exception IllegalArgumentException If channels is out of range;
   *    by default, if channels < 1.
   * @exception IllegalStateException If the state of this stream equals
   *    STATE_DATA_WRITTEN or STATE_CLOSED.
   * @see #getState
   * @see #getChannels
   * @see #channels
   */
  public void setChannels(int channels)
  {
    if (state == STATE_DATA_WRITTEN)
      throw new IllegalStateException("data already written to this stream");
    if (state == STATE_CLOSED)
      throw new IllegalStateException("audio output stream closed");
    if (channels < 1)
      throw new IllegalArgumentException("channels < 1");
    this.channels = channels;
  }

  /**
   * This method returns the number of channels as it has been set
   * by using method setChannels.
   * @return The number of channels.
   * @see #setChannels
   */
  public int getChannels() { return channels; }

  /**
   * This method sets the encoding scheme for the sample values.
   * To be effective, it must be called <EM>before</EM> writing to this
   * stream.
   * @param encodingScheme The encoding scheme for the sample values.
   * @exception IllegalArgumentException If the encoding scheme is
   *    out of range; by default, if it does not equal one of the
   *    encoding scheme constants.
   * @exception IllegalStateException If the state of this stream equals
   *    STATE_DATA_WRITTEN or STATE_CLOSED.
   * @see #getState
   * @see #getEncodingScheme
   * @see #encodingScheme
   */
  public void setEncodingScheme(int encodingScheme)
  {
    if (state == STATE_DATA_WRITTEN)
      throw new IllegalStateException("data already written to this stream");
    if (state == STATE_CLOSED)
      throw new IllegalStateException("audio output stream closed");
    if ((encodingScheme < 0) || (encodingScheme > ENCODING_MAX))
      throw new IllegalArgumentException("encoding scheme out of range");
    this.encodingScheme = encodingScheme;
  }

  /**
   * This method returns the encoding scheme for sample values as it has
   * been set by using method setSamples.
   * @return The encoding scheme for the sample values.
   * @see #setEncodingScheme
   */
  public int getEncodingScheme() { return encodingScheme; }

  /**
   * This method sets the sample rate.
   * To be effective, it must be called <EM>before</EM> writing to this
   * stream.
   * @param sampleRate The sample rate.
   * @exception IllegalArgumentException If the sample rate is out of range;
   *    by default, if it is not positive.
   * @exception IllegalStateException If the state of this stream equals
   *    STATE_DATA_WRITTEN or STATE_CLOSED.
   * @see #getState
   * @see #getSampleRate
   * @see #sampleRate
   */
  public void setSampleRate(double sampleRate)
  {
    if (state == STATE_DATA_WRITTEN)
      throw new IllegalStateException("data already written to this stream");
    if (state == STATE_CLOSED)
      throw new IllegalStateException("audio output stream closed");
    if (!(sampleRate > 0.0))
      throw new IllegalArgumentException("sampleRate not above 0.0");
    this.sampleRate = sampleRate;
  }

  /**
   * This method returns the sample rate as it has been set
   * by using method setSampleRate.
   * @return The sample rate.
   * @see #setSampleRate
   */
  public double getSampleRate() { return sampleRate; }

  /**
   * This methods stores a human readable, informative string to be
   * included into the audio output stream data.
   * To be effective, it must be called <EM>before</EM> writing to this
   * stream.
   * @param info The informative string. A value of null signals not to
   *    include any string.
   * @exception IllegalArgumentException If the informative string can not
   *    be included, e.g. because it is too long or the audio output stream
   *    does not support informative strings to be included. By default,
   *    IllegalArgumentException is never thrown.
   * @exception IllegalStateException If the state of this stream equals
   *    STATE_DATA_WRITTEN or STATE_CLOSED.
   * @see #getState
   * @see #getInfo
   * @see #info
   */
  public void setInfo(String info)
  {
    if (state == STATE_DATA_WRITTEN)
      throw new IllegalStateException("data already written to this stream");
    if (state == STATE_CLOSED)
      throw new IllegalStateException("audio output stream closed");
    this.info = info;
  }

  /**
   * This method returns the informative string as it has been set
   * by using method setInfo.
   * @return The informative string.
   * @see #setInfo
   */
  public String getInfo() { return info; }

  /**
   * This method sets the sample scale factor.
   * To be effective, it must be called <EM>before</EM> writing to this
   * stream.
   * @param sampleScale The sample scale factor.
   * @exception IllegalStateException If the state of this stream equals
   *    STATE_DATA_WRITTEN or STATE_CLOSED.
   * @see #getState
   * @see #getSampleScale
   * @see #sampleScale
   */
  public void setSampleScale(double sampleScale)
  {
    if (state == STATE_DATA_WRITTEN)
      throw new IllegalStateException("data already written to this stream");
    if (state == STATE_CLOSED)
      throw new IllegalStateException("audio output stream closed");
    this.sampleScale = sampleScale;
  }

  /**
   * This method returns the sample scale factor as it has been set
   * by using method setSampleScale.
   * @return The sample scale factor.
   * @see #setSampleScale
   */
  public double getSampleScale() { return sampleScale; }

  /**
   * Throws an IOException, if the properties of this audio output stream are
   * invalid.<BR>
   * The properties are determined by envoking the set methods of this class
   * with appropriate parameters, or by their respective default values.
   * By default, this method is automatically envoked from the write method
   * immediately before writing the first data to the audio output stream.
   * Hence, it may be a good idea for this method to set up further needed
   * data (e.g. file header data to be written before the actual sample data).
   * @exception IOException If the properties of this audio output stream are
   *    invalid.
   */
  abstract public void checkProperties() throws IOException;

  /**
   * Returns the current state of this audio output stream.
   * @return The current state of this audio output stream.
   * @see #STATE_CREATED
   * @see #STATE_DATA_WRITTEN
   * @see #STATE_CLOSED
   */
  public int getState()
  {
    return state;
  }

  /**
   * Writes an integer or mu-law sample value to this audio output stream.
   * The number of relevant bits is determined through the bitsPerValue
   * variable.
   * @param sample The integer or mu-law sample value to be written.
   * @exception IOException If an I/O error occurs.
   * @exception IllegalStateException If the encoding scheme is neither set
   *    to ENCODING_MU_LAW nor to ENCODING_INTEGER, or the audio output stream
   *    does not support writing either mu-law or integer sample values, as
   *    by default.
   * @see #encodingScheme
   */
  public void writeSample(long sample) throws IOException
  {
    throw new
      IllegalStateException("integer or mu-law sample values not supported");
  }

  /**
   * Writes a floating point or fixed point real sample value to this audio
   * output stream. The number of relevant bits is determined through the
   * bitsPerValue variable.
   * @param sample The float or fixed sample value to be written.
   * @exception IOException If an I/O error occurs.
   * @exception IllegalStateException If the encoding scheme is neither set
   *    to ENCODING_FLOAT nor to ENCODING_FIXED, or the audio output stream
   *    does not support writing either float or fixed sample values, as by
   *    default.
   * @see #encodingScheme
   */
  public void writeSample(double sample) throws IOException
  {
    throw
      new IllegalStateException("float or fixed sample values not supported");
  }

  /**
   * Writes the specified byte to this audio output stream.<BR>
   * This is meant for internal use only. Do not call this method from
   * another class (nor from a subclass), except if you subclass method
   * writeSample. You may subclass this method, if needed.
   * The default implementation of this method just passes the argument
   * to the write method of the underlying output stream; but before
   * doing this for the first time, it calls the checkProperties method
   * to ensure that the audio output stream is in a valid state before
   * writing any data, and then sets the state to the value
   * STATE_DATA_WRITTEN.
   * @param b The <code>byte</code>.
   * @exception IOException If an I/O error occurs.
   * @exception IllegalStateException By default, if the state of this
   *    stream equals STATE_CLOSED.
   * @see #checkProperties
   * @see #getState
   */
  public void write(int b) throws IOException
  {
    if (state == STATE_CLOSED)
      throw new IllegalStateException("audio output stream closed");
    else
      {
	if (state == STATE_CREATED)
	  {
	    checkProperties();
	    out.write(b);
	    state = STATE_DATA_WRITTEN;
	    return;
	  }
	out.write(b);
      }
  }

  /**
   * Flushes this audio output stream and forces any buffered output bytes
   * to be written out.
   * @exception IOException If an I/O error occurs.
   * @exception IllegalStateException If the state of this stream equals
   *    STATE_CLOSED.
   * @see #getState
   */
  public void flush() throws IOException
  {
    if (state == STATE_CLOSED)
      throw new IllegalStateException("audio output stream closed");
    out.flush();
  }

  /**
   * Closes this audio output stream and releases any system resources
   * associated with this stream.
   * @exception IOException If an I/O error occurs.
   * @exception IllegalStateException If the state of this stream already
   *    equals STATE_CLOSED.
   * @see #getState
   */
  public void close() throws IOException
  {
    if (state == STATE_CLOSED)
      throw new IllegalStateException("audio output stream closed");
    out.close();
    state = STATE_CLOSED;
  }
}
