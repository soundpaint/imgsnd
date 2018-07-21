/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/soundedit/AudioOut.java.rca $
 * $Revision: 1.1 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Sat Jun 27 14:42:58 1998 $
 * $State: Experimental $
 */

/*
 * @(#)AudioOut.java 1.00 98/02/22
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

import java.io.PrintStream;
import java.io.IOException;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

class AudioOut extends SampleInputStream
{
  private int sample[] = null;

  private PrintStream err = System.err;
  private PrintStream out = System.out;

  AudioOut(PrintStream out, PrintStream err)
  {
    this.err = err;
    this.out = out;
  }

  synchronized void setSample(int sample[])
  {
    if (false && (sunAudioStream != null))
      {
	stopAudio();
	this.sample = sample;
	pos = 0;
	startAudio();
      }
    else
      {
	this.sample = sample;
	pos = 0;
      }
  }

  private SunAudioStream sunAudioStream = null;

  synchronized void startAudio()
  {
    out.println("[starting audio...]");
    out.flush();
    sunAudioStream = new SunAudioStream(this);
    sunAudioStream.enable();
    try
      {
	AudioPlayer.player.start(new AudioStream(sunAudioStream));
      }
    catch (IOException e)
      {
	stopAudio();
	err.println("[could not start audio: " + e.getMessage() + "]");
	err.flush();
      }
  }

  synchronized void stopAudio()
  {
    out.println("[stopping audio...]");
    out.flush();
    if (sunAudioStream != null)
      {
	sunAudioStream.disable();
	AudioPlayer.player.stop(sunAudioStream);
	sunAudioStream = null;
	pos = sample.length;
      }
  }

  private int pos = 0;

  public int read()
  {
    int data;
    if (pos < sample.length)
      return sample[pos++];
    else if (pos == sample.length)
      {
	pos = 0;
	return sample[pos++];
      }
    else
      throw new IllegalStateException("EOF");
  }

  public long skip(long n)
  {
    /* ensure that the number is a positive int */
    int i = ((int)(n & 0xEFFFFFFF)) % sample.length;
    pos += i;
    if (pos >= sample.length)
      pos -= sample.length;
    return n;
  }
}



