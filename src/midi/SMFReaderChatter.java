/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/midi/SMFReaderChatter.java.rca $
 * $Revision: 1.1 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Sat Jun 27 14:33:39 1998 $
 * $State: Experimental $
 */

/*
 * @(#)SMFReaderChatter.java 1.00 98/04/19
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

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * This class is part of the MIDI package.<BR>
 * It subclasses the default SMFReader class and overrides the use of
 * SMFEvent objects by SMFEventChatter objects.
 * @author Juergen Reuter
 * @version 1.0 27 Apr 98
 */
public class SMFReaderChatter extends SMFReader
{
  /**
   * Creates a new SMFReaderChatter that will read data from the specified
   * source.
   * @param source The InputStream that provides the data to be read.
   */
  public SMFReaderChatter(InputStream source)
  {
    super(source);
  }

  /**
   * A factory method that is used to create objects of class
   * SMFEventChatter.
   * @param runningStatus The running status byte to be used when creating
   *    the SMFEventChatter object.
   * @return A new, empty SMFEventChatter object.
   */
  protected SMFEvent createSMFEvent(int runningStatus)
  {
    return new SMFEventChatter(runningStatus);
  }

  /**
   * A factory method that is used to create objects of class
   * SMFEventChatter.
   * @return A new, empty SMFEventChatter object.
   */
  protected SMFEvent createSMFEvent()
  {
    return new SMFEventChatter();
  }

  /**
   * The main method.
   */
  public static void main(String args[]) throws IOException
  {
    if (args.length != 1)
      throw new IOException("Usage: SMFReaderChatter <filename>");
    String filename = args[0];
    FileInputStream source = new FileInputStream(filename);
    SMFReaderChatter reader = new SMFReaderChatter(source);
    reader.parseSMF();
  }
}
