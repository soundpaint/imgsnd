/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/midi/SMFEventChatter.java.rca $
 * $Revision: 1.1 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Sat Jun 27 14:33:22 1998 $
 * $State: Experimental $
 */

/*
 * @(#)SMFEventChatter.java 1.00 98/04/24
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

/**
 * This subclass of SMFEvent just overrides all dummy execXXX() methods of its
 * superclasses just by writing out this.toString() onto System.out.
 */
public class SMFEventChatter extends SMFEvent
{
  /**
   * Creates a new empty SMFEventChatter with no running status info.<BR>
   * Use method write() to write a sequence of bytes from a Standard MIDI
   * File that define the contents of this SMFEventChatter.
   * @see SMFEvent#write
   */
  public SMFEventChatter()
  {
    super();
  }

  /**
   * Creates a new empty SMFEventChatter with running status info.<BR>
   * Use method write() to write a sequence of bytes from a standard MIDI
   * File that define the contents of this SMFEventChatter.
   * @param runningStatus The status byte of the previous SMFEventChatter.
   *    Running status is valid only for Channel Voice and Channel Mode
   *    Messages, but not for System Messages.
   * @exception IllegalArgumentException If runningStatus leads in neither
   *    a Channel Voice nor a Channel Mode Message.
   */
  public SMFEventChatter(int runningStatus) throws IllegalArgumentException
  {
    super(runningStatus);
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Note Off event.
   * @param channel The MIDI channel (0..15) to apply.
   * @param pitch The note number (0..127) that represents the pitch.
   * @param velocity The note off velocity value (0..127).
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execNoteOff(int channel, int pitch, int velocity)
       throws IllegalArgumentException
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Note On event.
   * @param channel The MIDI channel (0..15) to apply.
   * @param pitch The note number (0..127) that represents the pitch.
   * @param velocity The note velocity value (0..127). A value of 0 indicates
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   *    a note off event with no note off velocity specified.
   */
  protected void execNoteOn(int channel, int pitch, int velocity)
       throws IllegalArgumentException
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Polyphonic Key Pressure (Aftertouch) event.
   * @param channel The MIDI channel (0..15) to apply.
   * @param pitch The note number (0..127) that represents the pitch.
   * @param pressure The pressure value (0..127).
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execAftertouch(int channel, int pitch, int velocity)
       throws IllegalArgumentException
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Program Change event.
   * @param channel The MIDI channel (0..15) to apply.
   * @param program The program number (0..127).
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execProgChange(int channel, int program)
       throws IllegalArgumentException
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Channel Pressure (Aftertouch) event.
   * @param channel The MIDI channel (0..15) to apply.
   * @param pressure The pressure value (0..127).
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execChannelPressure(int channel, int program)
       throws IllegalArgumentException
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Pitch Bend Change event.
   * @param channel The MIDI channel (0..15) to apply.
   * @param value The pitch bend change value (0..16383).
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execPitchWheel(int channel, short value)
       throws IllegalArgumentException
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Control Change event for a 2 bytes controller.
   * @param channel The MIDI channel (0..15) to apply.
   * @param controller The controller number (0...31)
   * @param isLSB If the LSB of the controller is to be changed.
   * @param value The new value (LSB or MSB) for the controller.
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execCtrlChange14(int channel, int controller,
				  boolean isLSB, int value)
       throws IllegalArgumentException
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Control Change event for a single byte controller.
   * @param channel The MIDI channel (0..15) to apply.
   * @param controller The controller number (64..120)
   * @param value The new value for the controller.
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execCtrlChange7(int channel, int controller, int value)
       throws IllegalArgumentException
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Reset All Controllers event.
   */
  protected void execResetAllControllers()
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Local Control Off event.
   */
  protected void execLocalControlOff()
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Local Control On event.
   */
  protected void execLocalControlOn()
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a All Notes Off event.
   */
  protected void execAllNotesOff()
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents an OmniModeOff event.
   */
  protected void execOmniModeOff()
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents an OmniModeOn event.
   */
  protected void execOmniModeOn()
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents an MonoModeOn event.
   * @param channels The number of channels to use (1..16) or 0, if the
   *    number of channels shall equal the number of voices in the receiver.
   */
  protected void execMonoModeOn(int channels)
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents an PolyModeOn event.
   */
  protected void execPolyModeOn()
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a MIDI Time Code Quarter Frame event.
   * @param type The message type (0..7).
   * @param value The message value (0..15).
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execMTCQuarterFrame(int type, int value)
       throws IllegalArgumentException
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Song Position Pointer event.
   * @param value The song position pointer value (0..16383).
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execSongPositionPointer(short value)
       throws IllegalArgumentException
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Song Select event.
   * @param value The song select number (0..127).
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execSongSelect(int value)
       throws IllegalArgumentException
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Tune Request event.
   */
  protected void execTuneRequest()
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Timing Clock event.
   */
  protected void execTimingClock()
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a System 0xf9 event (currently undefined).
   */
  private void execSystemF9()
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Song Start event.
   */
  protected void execSongStart()
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Song Continue event.
   */
  protected void execSongContinue()
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Song Stop event.
   */
  protected void execSongStop()
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a System 0xfd event (currently undefined).
   */
  private void execSystemFD()
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents an Active Sensing event.
   */
  protected void execActiveSensing()
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a System Reset event.
   */
  protected void execSystemReset()
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Sequence Number Meta Event.
   * @param s The sequence number (0x0000...0x3fff).
   * @exception IllegalArgumentException A subclass implementation may
   *    throw an IllegalArgumentException, if this event occurs at an
   *    illegal position, e.g. if it occurs more than once in a track.
   */
  protected void execSequenceNumber(short s) throws IllegalArgumentException
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Text Event.
   * @param text The text string.
   */
  protected void execText(String text)
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Cpyright Notice Meta Event.
   * @param notice The copyright notice.
   */
  protected void execCopyright(String notice)
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Sequence/Track Name Meta Event.
   * @param name The sequence/track name.
   */
  protected void execTrackName(String name)
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Instrument Name Meta Event.
   * @param name The instrument name.
   */
  protected void execInstrumentName(String name)
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Lyric Meta Event.
   * @param syllable The lyrics syllable.
   */
  protected void execLyric(String syllable)
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Marker Meta Event.
   * @param name The name of the marker.
   */
  protected void execMarker(String name)
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Cue Point Meta Event.
   * @param description The cue point description.
   */
  protected void execCuePoint(String description)
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF MIDI Channel Meta Event.
   * @param channel The channel prefix number (0..15) for any subsequent
   *    Meta Event System Exclusive Event.
   */
  protected void execMIDIChannel(int channel)
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF MIDI Port Meta Event.
   * @param port The port prefix number (0..255) for any subsequent
   *    Meta Event System Exclusive Event.
   */
  protected void execMIDIPort(int port)
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF End of Track Meta Event.
   */
  protected void execEndOfTrack()
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Set Tempo Meta Event.
   * @param tempo The tempo in microseconds per MIDI quarter-note.
   */
  protected void execSetTempo(int tempo)
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF SMPTE Offset Meta Event.
   * The SMPTE Offset designates the SMPTE time at which the track chunk is
   * supposed to start.
   * @param hours The offset time hours (0..255).
   * @param minutes The offset time minutes (0..59).
   * @param secs The offset time seconds (0..59).
   * @param frames The offset time frames (0..255).
   * @param subframes Fractional frames in 100ths of a frame (0..99).
   */
  protected void execSMPTEOffset(int hours, int minutes, int secs,
				 int frames, int subframes)
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Time Signature Meta Event.
   * @param nominator The nominator of the time signature as it would be
   *    notated.
   * @param denominator The denominator of the time signature as a negative
   *    power of 2: 2=quarter note, 3=eigth, etc.
   * @param clocks The number of MIDI clocks in a metronome click.
   * @param thirtyseconds The number of notated 32nd-notes in a MIDI
   *    quarter-note (24 MIDI clocks).
   */
  protected void execTimeSignature(int nominator, int denominator,
				   int clocks, int thirtyseconds)
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Key Signature Meta Event.
   * @param sharps The number of sharps.
   * @param minor True, if the signature represents a minor key. False,
   *    if the signature represents a major key.
   */
  protected void execKeySignature(int sharps, boolean minor)
  {
    System.out.println(this.toString());
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Sequencer-Specific Meta Event.
   * @param contents The contents of the sequencer-specific data as an
   *    array of Byte objects.
   */
  protected void execSequencerSpecific(Byte[] contents)
  {
    System.out.println(this.toString());
  }
}
