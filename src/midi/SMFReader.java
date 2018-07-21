/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/midi/SMFReader.java.rca $
 * $Revision: 1.1 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Sat Jun 27 14:32:51 1998 $
 * $State: Experimental $
 */

/*
 * @(#)SMFReader.java 1.00 98/04/19
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

package midi;

import java.io.InputStream;
import java.io.IOException;

/**
 * This class is part of the MIDI package.<BR>
 * It implements a director that reads Standard MIDI File (SMF) data from
 * a source InputStream and builds some object from this data. The
 * representation of this object is user-definable.
 * @author Juergen Reuter
 * @version 1.0 19 Apr 98
 */
public class SMFReader
{
  /**
   * The magic number for the header chunk of an SMF file.
   */
  public final static String MTHD_CHUNK_ID = "MThd";

  /**
   * The magic number for the data chunk of an SMF file.
   */
  public final static String MTRK_CHUNK_ID = "MTrk";

  /**
   * A constant indicating the first out of 4 currently defined SMPTE
   * standards.
   * @see #smpteStandard.
   */
  private final static byte SMPTE_STANDARD_1 = (byte)-24;

  /**
   * A constant indicating the second out of 4 currently defined SMPTE
   * standards.
   * @see #smpteStandard.
   */
  private final static byte SMPTE_STANDARD_2 = (byte)-25;

  /**
   * A constant indicating the third out of 4 currently defined SMPTE
   * standards.
   * @see #smpteStandard.
   */
  private final static byte SMPTE_STANDARD_3 = (byte)-29;

  /**
   * A constant indicating the fourth out of 4 currently defined SMPTE
   * standards.
   * @see #smpteStandard.
   */
  private final static byte SMPTE_STANDARD_4 = (byte)-30;

  /**
   * A constant indicating a typical frame resolution as used for the
   * MIDI Time Code.
   * @see #frameResolution
   */
  private final static byte FRAME_RESOLUTION_MTC = (byte)4;

  /**
   * A constant indicating a typical frame resolution that is referred to
   * as SMPTE bit resolution.
   * @see #frameResolution
   */
  private final static byte FRAME_RESOLUTION_SMPTE_BITS = (byte)80;

  /**
   * The source input stream.
   */
  private InputStream source;

  /**
   * The format of the SMF file (0..2).
   */
  private short format;

  /**
   * The number of expected data chunks (MTrk) in the SMF file.
   */
  private short mtrkNumber;

  /**
   * The Pulses Per Quarter Note (PPQN).
   * This is defined alternatively to the smpteStandard and frameResolution
   * values.
   * @see smpteStandard
   * @see frameResolution
   */
  private short ppqn = 0;

  /**
   * The SMPTE standard. This is defined alternatively to the ppqn value.
   * @see #ppqn
   */
  private byte smpteStandard = 0;

  /**
   * The frame resolution. This is defined alternativlely to the ppqn value.
   * @see #ppqn
   */
  private byte frameResolution = 0;

  private SMFReader() {}

  /**
   * Creates a new SMFReader that will read data from the specified
   * source.
   * @param source The InputStream that provides the data to be read.
   */
  public SMFReader(InputStream source)
  {
    this.source = source;
  }

  /**
   * This method is called as soon as an MTrk chunk has been completed.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param track A List object that represents a list of all SMFEvent objects
   *    of the current chunk.
   */
  protected void execMtrkChunk(List track)
  {
  }

  /**
   * This method is called as soon as all MTrk chunks have been completed.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param allTracks A List object that represents a list of tracks with
   *    each track being itself a List object that again represents a list of
   *    all SMFEvent objects of that track.
   */
  protected void execSMF(List allTracks)
  {
  }

  /**
   * Parses the MThd chunk of an SMF file.
   */
  private void parseMThdChunk() throws IOException
  {
    int data;
    char[] chunkID = new char[4];
    for (int i = 0; i < 4; i++)
      {
	data = source.read();
	if (data == -1)
	  throw new IOException("unexpected EOF");
	chunkID[i] = (char)data;
      }
    if (!new String(chunkID).equals(MTHD_CHUNK_ID))
      throw new IOException("bad magic number (" + chunkID + ")");
    int chunkLength = 0;
    for (int i = 0; i < 4; i++)
      {
	data = source.read();
	if (data == -1)
	  throw new IOException("unexpected EOF");
	chunkLength = (chunkLength << 8) | data;
      }
    if (chunkLength != 6)
      throw new IOException("invalid chunk length (" + chunkLength + ")");
    format = 0;
    for (int i = 0; i < 2; i++)
      {
	data = source.read();
	if (data == -1)
	  throw new IOException("unexpected EOF");
	format = (short)((format << 8) | data);
      }
    if ((format < 0) || (format > 2))
      throw new IOException("unknown format type");
    mtrkNumber = 0;
    for (int i = 0; i < 2; i++)
      {
	data = source.read();
	if (data == -1)
	  throw new IOException("unexpected EOF");
	mtrkNumber = (short)((mtrkNumber << 8) | data);
      }
    data = source.read();
    if (data == -1)
      throw new IOException("unexpected EOF");
    byte divisionHi = (byte)data;
    data = source.read();
    if (data == -1)
      throw new IOException("unexpected EOF");
    byte divisionLo = (byte)data;
    if (divisionHi > 0) // microseconds encoding
      ppqn = (short)((divisionHi << 8) | divisionLo);
    else // smpte encoding
      switch (divisionHi)
	{
	case SMPTE_STANDARD_1:
	case SMPTE_STANDARD_2:
	case SMPTE_STANDARD_3:
	case SMPTE_STANDARD_4:
	  if (divisionLo < 0)
	    throw new IOException("frame resolution must be positive");
	  smpteStandard = divisionHi;
	  frameResolution = divisionLo;
	  break;
	default:
	  throw new IOException("unknown smpte standard");
	}
  }

  /**
   * A factory method that is used to create objects of class SMFEvent or
   * a subclass. The default implementation really creates objects of
   * class SMFEvent. A subclass of this class may want to override this
   * method to return an object of some subclass of SMFEvent, instead.
   * @param runningStatus The running status byte to be used when creating
   *    the SMFEvent object.
   * @return A new, empty SMFEvent object.
   */
  protected SMFEvent createSMFEvent(int runningStatus)
  {
    return new SMFEvent(runningStatus);
  }

  /**
   * A factory method that is used to create objects of class SMFEvent or
   * a subclass. The default implementation really creates objects of
   * class SMFEvent. A subclass of this class may want to override this
   * method to return an object of some subclass of SMFEvent, instead.
   * @return A new, empty SMFEvent object.
   */
  protected SMFEvent createSMFEvent()
  {
    return new SMFEvent();
  }

  /**
   * Parses an MTrk chunk of an SMF file.
   */
  private List parseMTrkChunk() throws IOException
  {
    List eventList = null;
    int data;
    char[] chunkID = new char[4];
    for (int i = 0; i < 4; i++)
      {
	data = source.read();
	if (data == -1)
	  throw new IOException("unexpected EOF");
	chunkID[i] = (char)data;
      }
    int chunkLength = 0;
    for (int i = 0; i < 4; i++)
      {
	data = source.read();
	if (data == -1)
	  throw new IOException("unexpected EOF");
	chunkLength = (chunkLength << 8) | data;
      }
    if (!new String(chunkID).equals(MTRK_CHUNK_ID))
      source.skip(chunkLength);
    else
      {
	int runningStatus = 0;
	eventList = new List();
	long bytesRead = 0;
	while (bytesRead < chunkLength)
	  {
	    SMFEvent event;
	    if (runningStatus != 0)
	      event = createSMFEvent(runningStatus);
	    else
	      event = createSMFEvent();
	    for (; !event.isComplete(); bytesRead++)
	      {
		data = source.read();
		if (data == -1)
		  throw new IOException("unexpected EOF");
		event.write(data);
	      }
	    runningStatus = event.getRunningStatus();
	    eventList.insertTail(event);
	  }
      }
    execMtrkChunk(eventList);
    return eventList;
  }

  /**
   * Starts the parsing & building process. Parses data from the previously
   * specified source and builds the target object.
   * @return The target object of the building process.
   * @exception IOException If an i/o error occurs while reading data from
   *    the source or the format of the file is faulty.
   */
  public List parseSMF() throws IOException
  {
    try
      {
	parseMThdChunk();
      }
    catch (IOException e)
      {
	throw new IOException("SMF file format error: MThd chunk: " + e);
      }
    List chunkList = new List();
    for (int i = 0; i < mtrkNumber; i++)
      chunkList.insertTail(parseMTrkChunk());
    execSMF(chunkList);
    return chunkList;
  }
}
