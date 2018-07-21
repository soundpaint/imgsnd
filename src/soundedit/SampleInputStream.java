/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/soundedit/SampleInputStream.java.rca $
 * $Revision: 1.1 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Sat Jun 27 14:40:03 1998 $
 * $State: Experimental $
 */

/*
 * @(#)SampleInputStream.java 1.00 98/02/22
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

/**
 * This abstract class is similar to java.io.InputStream, but operates on
 * integer values instead of bytes.
 */
public abstract class SampleInputStream
{
  /**
   * Reads the next int value of data from this input stream.
   * If no value is available because the end of the stream has been
   * reached, exception IllegalStateException is thrown.<BR>
   * This method blocks until input data is available, the end of the
   * stream is detected, or an exception is thrown. 
   * <P>
   * A subclass must provide an implementation of this method. 
   * @return The next int value of data.
   * @exception IOException If an I/O error occurs.
   * @exception IllegalStateException If no value is available because the
   *    end of the stream has been reached.
   */
  public abstract int read() throws IOException;

  /**
   * Reads up to <CODE>b.length</CODE> int values of data from this input 
   * stream into an array of int values. 
   * <P>
   * The <CODE>read</CODE> method of <CODE>InputStream</CODE> calls 
   * the <CODE>read</CODE> method of three arguments with the arguments 
   * <CODE>b</CODE>, <CODE>0</CODE>, and <CODE>b.length</CODE>. 
   * @param b The buffer into which the data is read.
   * @return The total number of int values read into the buffer.
   * @exception IOException If an I/O error occurs.
   * @exception IllegalStateException If no value is available because the
   *    end of the stream has been reached.
   * @see #read(byte[], int, int)
   */
  public int read(int b[]) throws IOException
  {
    return read(b, 0, b.length);
  }

  /**
   * Reads up to <CODE>len</CODE> int values of data from this input stream 
   * into an array of int values. This method blocks until some input is 
   * available. If the first argument is <CODE>null,</CODE> up to 
   * <CODE>len</CODE> int values are read and discarded. 
   * <P>
   * The <CODE>read</CODE> method of <CODE>InputStream</CODE> reads a 
   * single int value at a time using the read method of zero arguments to 
   * fill in the array. Subclasses are encouraged to provide a more 
   * efficient implementation of this method. 
   * @param b The buffer into which the data is read.
   * @param off The start offset of the data.
   * @param len The maximum number of int values read.
   * @return The total number of int values read into the buffer.
   * @exception  IOException  if an I/O error occurs.
   * @exception IllegalStateException If no value is available because the
   *    end of the stream has been reached.
   * @see #read()
   */
  public int read(int b[], int off, int len) throws IOException
  {
    if (len <= 0)
      return 0;
    int c = read();
    b[off] = c;
    int i = 1;
    try
      {
	for (; i < len ; i++)
	  {
	    try
	      {
		c = read();
	      }
	    catch (IllegalStateException e)
	      {
		break;
	      }
	    b[off + i] = c;
	  }
      }
    catch (IOException ee) {}
    return i;
  }

  /**
   * Skips over and discards <CODE>n</CODE> int values of data from this 
   * input stream. The <CODE>skip</CODE> method may, for a variety of 
   * reasons, end up skipping over some smaller number of int values, 
   * possibly <CODE>0</CODE>. The actual number of int values skipped is 
   * returned. 
   * <P>
   * The <CODE>skip</CODE> method of <CODE>InputStream</CODE> creates 
   * an int array of length <CODE>n</CODE> and then reads into it until 
   * <CODE>n</CODE> int values have been read or the end of the stream has 
   * been reached. Subclasses are encouraged to provide a more 
   * efficient implementation of this method. 
   * @param n The number of int values to be skipped.
   * @return The actual number of int values skipped.
   * @exception IOException If an I/O error occurs.
   * @exception IllegalStateException If no value is available because the
   *    end of the stream has been reached.
   */
  public long skip(long n) throws IOException
  {
    /* ensure that the number is a positive int */
    int data[] = new int[(int) (n & 0xEFFFFFFF)];
    return read(data);
  }

  /**
   * Returns the number of int values that can be read from this input 
   * stream without blocking. The available method of 
   * <CODE>InputStream</CODE> returns <CODE>0</CODE>. This method 
   * <B>should</B> be overridden by subclasses. 
   * @return The number of int values that can be read from this input stream
   *    without blocking.
   * @exception IOException If an I/O error occurs.
   */
  public int available() throws IOException
  {
    return 0;
  }

  /**
   * Closes this input stream and releases any system resources 
   * associated with the stream. 
   * <P>
   * The <CODE>close</CODE> method of <CODE>InputStream</CODE> does nothing.
   * @exception IOException If an I/O error occurs.
   */
  public void close() throws IOException {}

  /**
   * Marks the current position in this input stream. A subsequent 
   * call to the <CODE>reset</CODE> method repositions this stream at 
   * the last marked position so that subsequent reads re-read the same 
   * int values. 
   * <P>
   * The <CODE>readlimit</CODE> arguments tells this input stream to 
   * allow that many int values to be read before the mark position gets 
   * invalidated. 
   * <P>
   * The <CODE>mark</CODE> method of <CODE>InputStream</CODE> does nothing.
   * @param readlimit The maximum limit of int values that can be read before
   *    the mark position becomes invalid.
   * @see #reset()
   */
  public synchronized void mark(int readlimit) {}

  /**
   * Repositions this stream to the position at the time the 
   * <CODE>mark</CODE> method was last called on this input stream. 
   * <P>
   * The <CODE>reset</CODE> method of <CODE>InputStream</CODE> throws 
   * an <CODE>IOException</CODE>, because input streams, by default, do 
   * not support <CODE>mark</CODE> and <CODE>reset</CODE>.
   * <P>
   * Stream marks are intended to be used in
   * situations where you need to read ahead a little to see what's in
   * the stream. Often this is most easily done by invoking some
   * general parser. If the stream is of the type handled by the
   * parser, it just chugs along happily. If the stream is not of
   * that type, the parser should toss an exception when it fails,
   * which, if it happens within readlimit int values, allows the outer
   * code to reset the stream and try another parser.
   * @exception IOException If this stream has not been marked or if the
   *    mark has been invalidated.
   * @see #mark(int)
   * @see java.io.IOException
   */
  public synchronized void reset() throws IOException
  {
    throw new IOException("mark/reset not supported");
  }

  /**
   * Tests if this input stream supports the <CODE>mark</CODE> 
   * and <CODE>reset</CODE> methods. The <CODE>markSupported</CODE> 
   * method of <CODE>InputStream</CODE> returns <CODE>false</CODE>. 
   * @return <CODE>true</CODE> If this true type supports the mark and reset
   *    method; <CODE>false</CODE> otherwise.
   * @see #mark(int)
   * @see java.io.InputStream#reset()
   */
  public boolean markSupported()
  {
    return false;
  }
}
