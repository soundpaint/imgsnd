/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/soundedit/SoundEdit.java.rca $
 * $Revision: 1.1 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Sat Jun 27 14:40:53 1998 $
 * $State: Experimental $
 */

/*
 * @(#)SoundEdit.java 1.00 98/02/22
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * This utility lets a user interactively edit a sound.
 * A sound is - mathematically seen - equivalent to a periodic
 * function. The minimal period equals the pitch of the sound;
 * the waveform of the function represents the color of the sound.
 * This utility composes a sound by a harmonic series of sine waves
 * as basic constitutents. Note that a function could also be developed
 * by a series of other constitutents, such as polynomials as in a Taylor
 * series.
 */
public class SoundEdit
{
  private static BufferedReader in;
  private static PrintStream out;
  private static PrintStream err;
  private static AudioOut audioOut;

  private static boolean quit = false;

  private static int cmd0;
  private static String cmdarray[];

  private final static int CMD_HELP = 0;
  private final static int CMD_QUIT = 1;
  private final static int CMD_SETPARTIAL = 2;
  private final static int CMD_STARTAUDIO = 3;
  private final static int CMD_STOPAUDIO = 4;
  private final static int CMD_Z = 5;

  private final static String[] cmds0 =
  {
    "HELP", "QUIT", "SETPARTIAL", "STARTAUDIO", "STOPAUDIO", "Z"
  };

  private static int complete(String cmd0) throws IllegalArgumentException
  {
    for (int i = 0; i < cmds0.length; i++)
      {
	if (cmds0[i].startsWith(cmd0))
	  if ((i + 1 < cmds0.length) && (cmds0[i + 1].startsWith(cmd0)))
	    throw new IllegalArgumentException("ambiguous command");
	  else
	    return i;
      }
    throw new IllegalArgumentException("invalid command");
  }

  private static void parse(String command) throws IllegalArgumentException
  {
    int count = 0;
    StringTokenizer st;
    st = new StringTokenizer(command);
    count = st.countTokens();
    cmdarray = new String[count];
    st = new StringTokenizer(command);
    count = 0;
    while (st.hasMoreTokens())
      {
	cmdarray[count++] = st.nextToken();
      }
    cmd0 = complete(cmdarray[0].toUpperCase());
  }

  private static void help()
  {
    out.println("Commands may be abbreviated.  Commands are:");
    for (int i = 0; i < cmds0.length; i++)
      out.println(cmds0[i]);
    out.flush();
  }

  private static void setPartial() throws IllegalArgumentException
  {
    int partial;
    double volume;
    try
      {
	partial = Integer.parseInt(cmdarray[1]);
      }
    catch (NumberFormatException e)
      {
	throw new IllegalArgumentException("first arg not an int value");
      }
    try
      {
	volume = Double.valueOf(cmdarray[2]).doubleValue();
      }
    catch (NumberFormatException e)
      {
	throw new IllegalArgumentException("second arg not a double value");
      }
    if (partial < 0)
      throw new IllegalArgumentException("first arg must not be negative");
    if (partial >= PARTIALS)
      throw new IllegalArgumentException("first arg not below " + PARTIALS);
    partials[partial] = volume;
    updateSample();
  }

  private static void z()
  {
    out.println("SAMPLE = [");
    for (int i = 0; i < SAMPLE_SIZE; i++)
      {
	out.print(isample[i] + " ");
	if ((i & 3) == 0)
	  out.println();
      }
    out.println("]");
    out.flush();
  }

  private static void exec() throws IllegalArgumentException
  {
    switch (cmd0)
      {
      case CMD_QUIT:
	quit = true;
	break;
      case CMD_HELP:
	help();
	break;
      case CMD_SETPARTIAL:
	setPartial();
	break;
      case CMD_STARTAUDIO:
	audioOut.startAudio();
	break;
      case CMD_STOPAUDIO:
	audioOut.stopAudio();
	break;
      case CMD_Z:
	z();
	break;
      }
  }

  private static void welcome()
  {
    out.println("This is SoundEdit V1.0");
    out.println("Enter any command (or 'help' for a list of commands).");
  }

  private static void farewell()
  {
    out.println("That's all folks.");
  }

  private final static int PARTIALS = 10;
  private static double partials[] = new double[PARTIALS];

  private static void initData()
  {
    partials[0] = 1.0;
    for (int i = 1; i < partials.length; i++)
      partials[i] = 0.0;
    updateSample();
  }

  private final static int SAMPLE_SIZE = 80;

  private static double dsample[] = new double[SAMPLE_SIZE];
  private static int isample[] = new int[SAMPLE_SIZE];

  private static void updateSample()
  {
    double maxsam = 1.0;
    for (int i = 0; i < SAMPLE_SIZE; i++)
      {
	double dsam = 0.0;
	for (int j = 0; j < PARTIALS; j++)
	  dsam += (Math.sin(2.0 * Math.PI * (j + 1) * i / SAMPLE_SIZE) *
		   partials[j]);
	dsample[i] = dsam;
	maxsam = Math.max(maxsam, Math.abs(dsam));
      }
    for (int i = 0; i < SAMPLE_SIZE; i++)
      {
	isample[i] = (int)(dsample[i] * Integer.MAX_VALUE / maxsam);
      }
    audioOut.setSample(isample);
  }

  public static void main(String args[])
  {
    in = new BufferedReader(new InputStreamReader(System.in));
    out = System.out;
    err = System.err;
    audioOut = new AudioOut(out, err);
    initData();
    welcome();
    do
      {
	try
	  {
	    out.print("soundedit> ");
	    out.flush();
	    String command = in.readLine();
	    if (command.length() > 0)
	      {
		parse(command);
		exec();
	      }
	  }
	catch (IOException e)
	  {
	    err.println("FATAL I/O ERROR: " + e.getMessage());
	  }
	catch (IllegalArgumentException e)
	  {
	    err.println("?" + e.getMessage());
	  }
      }
    while (!quit);
    farewell();
  }
}
