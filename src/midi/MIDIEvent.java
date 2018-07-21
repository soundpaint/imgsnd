/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/midi/MIDIEvent.java.rca $
 * $Revision: 1.2 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Tue Jul 21 23:57:42 1998 $
 * $State: Experimental $
 */

/*
 * @(#)MIDIEvent.java 1.00 98/04/19
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

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

/**
 * Each instance of this class represents a single MIDI event.
 */
public class MIDIEvent extends ListElement
{

  /**
   * An array that contains the names of all 128 pitches that are defined
   * in the specification.
   */
  public final static String[] PITCHES =
  {
    "C0", "C#0", "D0", "D#0", "E0", "F0",
    "F#0", "G0", "G#0", "A0", "A#0", "B0",
    "C1", "C#1", "D1", "D#1", "E1", "F1",
    "F#1", "G1", "G#1", "A1", "A#1", "B1",
    "C2", "C#2", "D2", "D#2", "E2", "F2",
    "F#2", "G2", "G#2", "A2", "A#2", "B2",
    "C3", "C#3", "D3", "D#3", "E3", "F3",
    "F#3", "G3", "G#3", "A3", "A#3", "B3",
    "C4", "C#4", "D4", "D#4", "E4", "F4",
    "F#4", "G4", "G#4", "A4", "A#4", "B4",
    "C5", "C#5", "D5", "D#5", "E5", "F5",
    "F#5", "G5", "G#5", "A5", "A#5", "B5",
    "C6", "C#6", "D6", "D#6", "E6", "F6",
    "F#6", "G6", "G#6", "A6", "A#6", "B6",
    "C7", "C#7", "D7", "D#7", "E7", "F7",
    "F#7", "G7", "G#7", "A7", "A#7", "B7",
    "C8", "C#8", "D8", "D#8", "E8", "F8",
    "F#8", "G8", "G#8", "A8", "A#8", "B8",
    "C9", "C#9", "D9", "D#9", "E9", "F9",
    "F#9", "G9", "G#9", "A9", "A#9", "B9",
    "C10", "C#10", "D10", "D#10", "E10", "F10",
    "F#10", "G10"
  };

  /************************** STATUS BYTES ENCODING **************************/

  /**
   * A binary mask to determine the channel of a status byte.
   * Upon an arbitrary status byte, apply the binary "&" operator with
   * this mask. The result is the channel. This is valid for all voice
   * category messages.
   * @see #CATEGORY_NOTE_OFF
   * @see #CATEGORY_NOTE_ON
   * @see #CATEGORY_AFTERTOUCH
   * @see #CATEGORY_CTRL_CHANGE
   * @see #CATEGORY_PROG_CHANGE
   * @see #CATEGORY_CHANNEL_PRESSURE
   * @see #CATEGORY_PITCH_WHEEL
   */
  public final static int CHANNEL_MASK = 0x0f;

  /**
   * A binary mask to determine the category of a status byte.
   * Upon an arbitrary status byte, apply the binary "&" operator with
   * this mask. Then compare the result with the category constants to
   * determine the category of the status byte.
   * @see #CATEGORY_NOTE_OFF
   * @see #CATEGORY_NOTE_ON
   * @see #CATEGORY_AFTERTOUCH
   * @see #CATEGORY_CTRL_CHANGE
   * @see #CATEGORY_PROG_CHANGE
   * @see #CATEGORY_CHANNEL_PRESSURE
   * @see #CATEGORY_PITCH_WHEEL
   * @see #CATEGORY_SYSTEM
   */
  public final static int CATEGORY_MASK = 0xf0;

  /**
   * A voice category constant indicating a note off status byte.
   * Use this constant in combination with the category mask.
   * @see #CATEGORY_MASK
   */
  public final static int CATEGORY_NOTE_OFF = 0x80;

  /**
   * A voice category constant indicating a note on status byte.
   * Use this constant in combination with the category mask.
   * @see #CATEGORY_MASK
   */
  public final static int CATEGORY_NOTE_ON = 0x90;

  /**
   * A voice category constant indicating an aftertouch status byte.
   * Use this constant in combination with the category mask.
   * @see #CATEGORY_MASK
   */
  public final static int CATEGORY_AFTERTOUCH = 0xa0;

  /**
   * A voice category constant indicating a control change status byte.
   * Use this constant in combination with the category mask.
   * @see #CATEGORY_MASK
   */
  public final static int CATEGORY_CTRL_CHANGE = 0xb0;

  /**
   * A voice category constant indicating a program patch change status byte.
   * Use this constant in combination with the category mask.
   * @see #CATEGORY_MASK
   */
  public final static int CATEGORY_PROG_CHANGE = 0xc0;

  /**
   * A voice category constant indicating a channel pressure status byte.
   * Use this constant in combination with the category mask.
   * @see #CATEGORY_MASK
   */
  public final static int CATEGORY_CHANNEL_PRESSURE = 0xd0;

  /**
   * A voice category constant indicating a pitch wheel status byte.
   * Use this constant in combination with the category mask.
   * @see #CATEGORY_MASK
   */
  public final static int CATEGORY_PITCH_WHEEL = 0xe0;

  /**
   * A category constant indicating a system message status byte.
   * Use this constant in combination with the category mask.
   * @see #CATEGORY_MASK
   */
  public final static int CATEGORY_SYSTEM = 0xf0;

  /**
   * A pseudo controller number that is used to address a
   * "Reset All Controllers" Channel Mode Message event.
   */
  public final static int MODE_RESET_ALL_CONTROLLERS = 121;

  /**
   * A pseudo controller number that is used to address a
   * "Local Control" Channel Mode Message event.
   */
  public final static int MODE_LOCAL_CONTROL = 122;

  /**
   * A pseudo controller number that is used to address a
   * "All Notes Off" Channel Mode Message event.
   */
  public final static int MODE_ALL_NOTES_OFF = 123;

  /**
   * A pseudo controller number that is used to address a
   * "Omni Mode Off" Channel Mode Message event.
   */
  public final static int MODE_OMNI_MODE_OFF = 124;

  /**
   * A pseudo controller number that is used to address a
   * "Omni Mode On" Channel Mode Message event.
   */
  public final static int MODE_OMNI_MODE_ON = 125;

  /**
   * A pseudo controller number that is used to address a
   * "Mono Mode On" Channel Mode Message event.
   */
  public final static int MODE_MONO_MODE_ON = 126;

  /**
   * A pseudo controller number that is used to address a
   * "Poly Mode On" Channel Mode Message event.
   */
  public final static int MODE_POLY_MODE_ON = 127;

  /**
   * The "Start of System Exclusive" (SOX) status byte.
   * This is a system common message.
   */
  public final static int SYSTEM_SOX = 0xf0;

  /**
   * The "MIDI Time Clock Quarter Frame" status byte.
   * This is a system common message.
   */
  public final static int SYSTEM_MTC_QUARTER_FRAME = 0xf1;

  /**
   * The "Song Position Pointer" status byte.
   * This is a system common message.
   */
  public final static int SYSTEM_SONG_POSITION_POINTER = 0xf2;

  /**
   * The "Song Select" status byte.
   * This is a system common message.
   */
  public final static int SYSTEM_SONG_SELECT = 0xf3;

  /**
   * The 0xf4 status byte (currently undefined).
   * This is a system common message.
   */
  private final static int SYSTEM_F4 = 0xf4;

  /**
   * The 0xf5 status byte (currently undefined).
   * This is a system common message.
   */
  private final static int SYSTEM_F5 = 0xf5;

  /**
   * The "Tune Request" status byte.
   * This is a system common message.
   */
  public final static int SYSTEM_TUNE_REQUEST = 0xf6;

  /**
   * The "End of System Exclusive" (EOX) status byte.
   * This is a system common message.
   */
  public final static int SYSTEM_EOX = 0xf7;

  /**
   * The "Timing Clock" status byte.
   * This is a system real-time message.
   */
  public final static int SYSTEM_TIMING_CLOCK = 0xf8;

  /**
   * The 0xf9 status byte.
   * This is a (currently undefined) system real-time message.
   */
  private final static int SYSTEM_F9 = 0xf9;

  /**
   * The "Song Start" status byte.
   * This is a system real-time message.
   */
  public final static int SYSTEM_SONG_START = 0xfa;

  /**
   * The "Song Continue" status byte.
   * This is a system real-time message.
   */
  public final static int SYSTEM_SONG_CONTINUE = 0xfb;

  /**
   * The "Song Stop" status byte.
   * This is a system real-time message.
   */
  public final static int SYSTEM_SONG_STOP = 0xfc;

  /**
   * The 0xfd status byte.
   * This is a (currently undefined) system real-time message.
   */
  private final static int SYSTEM_FD = 0xfd;

  /**
   * The "Active Sensing" status byte.
   * This is a system real-time message.
   */
  public final static int SYSTEM_ACTIVE_SENSING = 0xfe;

  /**
   * The "System Reset" status byte.
   * This is a system real-time message.
   */
  public final static int SYSTEM_RESET = 0xff;

  /**
   * The names of the 8 MIDI messages categories that classify the MIDI
   * status bytes into the ranges
   * (0x80..0x8f), (0x90..0x9f), ..., (0xf0..0xff).
   */
  public final static String[] CATEGORIES =
  {
    "Note Off", "Note On", "Aftertouch", "Control Change",
    "Program Change", "Channel Pressure", "Pitch Bend", "System"
  };

  /**
   * The 16 System Messages names.
   */
  public final static String[] SYSTEM_MESSAGES =
  {
    "Exclusive Message", "MTC Quarter Frame", "Song Position Pointer",
    "Song Select", "Undefined (0xf4)", "Undefined (0xf5)", "Tune Request",
    "EOX", "Timing Clock", "Undefined (0xf9)", "Song Start", "Song Continue",
    "Song Stop", "Undefined (0xfd)", "Active Sensing", "Reset"
  };

  /**
   * This array contains the 32 names of all 2 byte (14 relevant bits)
   * controllers that are part of the MIDI specification.<BR>
   * These are used in control change events. The LSB values of these 32
   * controllers are stored as controller numbers 0 through 31; the MSB
   * values are stored as controller numbers 32 through 63, respectively.
   */
  public final static String[] CONTROLLERS_14 =
  {
    "Bank Select",
    "Modulation Wheel or Lever",
    "Breath Controller",
    "Undefined (0x03)",
    "Foot Pedal",
    "Portamento Time",
    "Data Entry",
    "Volume",
    "Balance",
    "Undefined (0x09)",
    "Pan Position",
    "Expression",
    "Effect Control 1",
    "Effect Control 2",
    "Undefined (0x0e)",
    "Undefined (0x0f)",
    "General Purpose Slider #1",
    "General Purpose Slider #2",
    "General Purpose Slider #3",
    "General Purpose Slider #4",
    "Undefined (0x14)",
    "Undefined (0x15)",
    "Undefined (0x16)",
    "Undefined (0x17)",
    "Undefined (0x18)",
    "Undefined (0x19)",
    "Undefined (0x1a)",
    "Undefined (0x1b)",
    "Undefined (0x1c)",
    "Undefined (0x1d)",
    "Undefined (0x1e)",
    "Undefined (0x1f)"
  };

  /**
   * This array contains the 64 names of all SINGLE byte (7 relevant bits)
   * controllers that are part of the MIDI specification.<BR>
   * These are used in control change events. The values of these 64
   * controllers are stored as controller numbers 64 through 127.
   */
  public final static String[] CONTROLLERS_7 =
  {
    "Hold Pedal (on/off)",
    "Portamento (on/off)",
    "Sustenuto Pedal (on/off)",
    "Soft Pedal (on/off)",
    "Legato Pedal (on/off)",
    "Hold 2 Pedal (on/off)",
    "Sound Variation",
    "Sound Timbre",
    "Sound Release Time",
    "Sound Attack Time",
    "Sound Brightness",
    "Sound Control 6",
    "Sound Control 7",
    "Sound Control 8",
    "Sound Control 9",
    "Sound Control 10",
    "General Purpose Button 1 (on/off)",
    "General Purpose Button 2 (on/off)",
    "General Purpose Button 3 (on/off)",
    "General Purpose Button 4 (on/off)",
    "Undefined (0x54)",
    "Undefined (0x55)",
    "Undefined (0x56)",
    "Undefined (0x57)",
    "Undefined (0x58)",
    "Undefined (0x59)",
    "Undefined (0x5a)",
    "Effects Depth",
    "Tremolo Depth",
    "Chorus Depth",
    "Celeste (Detune) Depth",
    "Phaser Depth",
    "Data Button Increment",
    "Data Button Decrement",
    "Non-Registered Parameter Number LSB",
    "Non-Registered Parameter Number MSB",
    "Registered Parameter Number LSB",
    "Registered Parameter Number MSB",
    "Undefined (0x66)",
    "Undefined (0x67)",
    "Undefined (0x68)",
    "Undefined (0x69)",
    "Undefined (0x6a)",
    "Undefined (0x6b)",
    "Undefined (0x6c)",
    "Undefined (0x6d)",
    "Undefined (0x6e)",
    "Undefined (0x6f)",
    "Undefined (0x70)",
    "Undefined (0x71)",
    "Undefined (0x72)",
    "Undefined (0x73)",
    "Undefined (0x74)",
    "Undefined (0x75)",
    "Undefined (0x76)",
    "Undefined (0x77)",
    "All Sound Off",
    "Reset All Controllers",
    "Local Control (on/off)",
    "All Notes Off",
    "Omni Mode Off",
    "Omni Mode On",
    "Mono Mode On",
    "Poly Mode On"
  };

  /**
   * The 128 manufacturer names that are represented by all of the
   * single byte encoded Manufacturer ID numbers.
   */
  public final static String[] MANUFACTURER_ID =
  {
    // 0x00 .. 0x1f American Group
    "<reserved>",
    "Sequential Circuits",
    "Big Briar / IDP",
    "Octave-Plateau",
    "Moog Music",
    "Passport Designs",
    "Lexicon",
    "Kurzweil",
    "Fender",
    "Stainway & Sons / Gulbransen",
    "AKG Acoustics / Delta Lab Research",
    "Voyce Music / Sound Composition Systems",
    "Waveframe Corp. / General Electro Music",
    "ADA / Techmar",
    "Garfield Elect. / Matthews Research",
    "Ensoniq",
    "Oberheim",
    "Apple Computer / Paia Electronics",
    "Grey Matter Response / Simmons Group Center",
    "Gentle Electric / DigiDesign",
    "Palm Tree Inst. / Fairlight",
    "JL Cooper",
    "Lowery",
    "Adams-Smith / Lin",
    "Emu Systems",
    "Harmony Systems",
    "ART",
    "Baldwin / Peavey",
    "Eventide",
    "Inventronics",
    "Undefined (0x1e)",
    "Clarity",
    // 0x20 .. 0x3f European Group
    "Passac / Bon Tempi",
    "SIEL",
    "Ircam",
    "Synthaxe",
    "Hohner",
    "Twister / Crumar",
    "Solton",
    "Jellinghaus MS",
    "Southworth / CTS",
    "PPG",
    "JEN",
    "SSL Limited",
    "Audio Vertrieb",
    "Undefined (0x2d)",
    "Undefined (0x2e)",
    "Elka",
    "Dynachord",
    "Undefined (0x31)",
    "Undefined (0x32)",
    "Undefined (0x33)",
    "Undefined (0x34)",
    "Undefined (0x35)",
    "Cheetah",
    "Undefined (0x37)",
    "Undefined (0x38)",
    "Undefined (0x39)",
    "Undefined (0x3a)",
    "Undefined (0x3b)",
    "Undefined (0x3c)",
    "Undefined (0x3d)",
    "Undefined (0x3e)",
    "Undefined (0x3f)",
    // 0x40 .. 0x5f Japanese Group
    "Kawai",
    "Roland",
    "Korg",
    "Yamaha",
    "Casio",
    "Undefined (0x45)",
    "Kamiya Studio",
    "Akai",
    "Japan Victor",
    "Meisosha",
    "Hoshino Gakki",
    "Fujitsu Elect.",
    "Sony",
    "Nisshin Onpa",
    "TEAC Corp.",
    "System Product",
    "Matsushita Electric",
    "Fostex",
    "Undefined (0x52)",
    "Undefined (0x53)",
    "Undefined (0x54)",
    "Undefined (0x55)",
    "Undefined (0x56)",
    "Undefined (0x57)",
    "Undefined (0x58)",
    "Undefined (0x59)",
    "Undefined (0x5a)",
    "Undefined (0x5b)",
    "Undefined (0x5c)",
    "Undefined (0x5d)",
    "Undefined (0x5e)",
    "Undefined (0x5f)",
    // 0x60 .. 0x7f
    "Undefined (0x60)",
    "Undefined (0x61)",
    "Undefined (0x62)",
    "Undefined (0x63)",
    "Undefined (0x64)",
    "Undefined (0x65)",
    "Undefined (0x66)",
    "Undefined (0x67)",
    "Undefined (0x68)",
    "Undefined (0x69)",
    "Undefined (0x6a)",
    "Undefined (0x6b)",
    "Undefined (0x6c)",
    "Undefined (0x6d)",
    "Undefined (0x6e)",
    "Undefined (0x6f)",
    "Undefined (0x70)",
    "Undefined (0x71)",
    "Undefined (0x72)",
    "Undefined (0x73)",
    "Undefined (0x74)",
    "Undefined (0x75)",
    "Undefined (0x76)",
    "Undefined (0x77)",
    "Undefined (0x78)",
    "Undefined (0x79)",
    "Undefined (0x7a)",
    "Undefined (0x7b)",
    "Undefined (0x7c)",
    "Educational Use",
    "Non-Real Time Universal SysEx ID",
    "Real Time Universal SysEx ID"
  };

  /**
   * Further 48 manufacturer names that are represented by the
   * three bytes encoded Manufacturer ID numbers
   * 0x00 0x00 0x00 through 0x00 0x00 0x2f.
   */
  public final static String[] MANUFACTURER_ID_00_00 =
  {
    // 0x00 0x00 0x00
    "Undefined (0x000000)",
    "Undefined (0x000001)",
    "Undefined (0x000002)",
    "Undefined (0x000003)",
    "Undefined (0x000004)",
    "Undefined (0x000005)",
    "Undefined (0x000006)",
    "Digital Music Corp.",
    "Undefined (0x000008)",
    "Undefined (0x000009)",
    "Undefined (0x00000a)",
    "IVL Technologies",
    "Southern Music Systems",
    "Lake Butler Sound",
    "Undefined (0x00000e)",
    "Undefined (0x00000f)",
    // 0x00 0x00 0x10
    "DOD Electronics",
    "Undefined (0x000011)",
    "Undefined (0x000012)",
    "Undefined (0x000013)",
    "Perfect Fretworks",
    "Undefined (0x000015)",
    "Opcode",
    "Undefined (0x000017)",
    "Spatial Sound",
    "KMX",
    "Undefined (0x00001a)",
    "Undefined (0x00001b)",
    "Undefined (0x00001c)",
    "Undefined (0x00001d)",
    "Undefined (0x00001e)",
    "Undefined (0x00001f)",
    // 0x00 0x00 0x20
    "Axxes",
    "Undefined (0x000021)",
    "Undefined (0x000022)",
    "Undefined (0x000023)",
    "Undefined (0x000024)",
    "Undefined (0x000025)",
    "Undefined (0x000026)",
    "Undefined (0x000027)",
    "Undefined (0x000028)",
    "Undefined (0x000029)",
    "Undefined (0x00002a)",
    "Undefined (0x00002b)",
    "Undefined (0x00002c)",
    "Undefined (0x00002d)",
    "Undefined (0x00002e)",
    "Undefined (0x00002f)"
  };

  /**
   * This boolean is used while checking incoming arguments.
   * If false, even tolerate data that seems to be faulty but
   * does not hurt. If true, do not tolerate such data, but
   * throw an IllegalArgumentException.
   */
  protected final static boolean strictArgCheck = false;

  /**
   * The number of bytes written into this object.
   */
  private byte count = 0;

  /**
   * The status byte of the previous MIDI event, if available.
   */
  protected byte runningStatus = 0;

  /**
   * The status byte of the current MIDI event.
   */
  private byte statusByte = 0;

  /**
   * The first data byte of the current MIDI event, if it available,
   * and if it is not a system exclusive event.
   */
  private byte dataByte1 = 0;

  /**
   * The first data byte of the current MIDI event, if it available,
   * and if it is not a system exclusive event.
   */
  private byte dataByte2 = 0;

  /**
   * The system exclusive message data, if this MIDI event represents a
   * system exclusive message. The vector starts with the Manufacturer ID.
   */
  private Vector sysexData = null;

  /**
   * This boolean becomes true, as soon as enough bytes have been
   * written to this MIDIEvent to make it complete.
   */
  protected boolean complete = false;

  /**
   * If this MIDIEvent represents a Channel Voice or Channel Mode Message,
   * this variable contains the number (0..15) of the channel involved.
   */
  private byte channel; // for channel voice messages only

  /**
   * Messages may be interferred by System Real Time Messages.
   * This method returns true, if the specified data represents a
   * MIDI System Real Time Message.
   * @param data An arbitrary unsigned byte value (0..255).
   * @return True, if the data represents a MIDI System Real Time Message.
   * @exception IllegalArgumentException If the data is not in the range of
   *    an unsigned byte (0..255).
   */
  public static boolean isSystemRealTimeMsg(int data)
  {
    if ((data < 0) || (data > 0xff))
      throw new IllegalArgumentException("data not an unsigned byte");
    else
      return data >= SYSTEM_TIMING_CLOCK;
  }

  /**
   * Creates a new empty MIDIEvent with no running status info.
   * Use method write() to write a sequence of MIDI bytes that define
   * the contents of this MIDIEvent.
   * @see #write
   */
  public MIDIEvent()
  {
    super();
  }

  /**
   * Creates a new empty MIDIEvent with running status info.<BR>
   * Use method write() to write a sequence of MIDI bytes that define
   * the contents of this MIDIEvent.
   * @param runningStatus The status byte of the previous MIDIEvent.
   *    Running status is valid only for Channel Voice and Channel Mode
   *    Messages, but not for System Messages.
   * @exception IllegalArgumentException If runningStatus leads in neither
   *    a Channel Voice nor a Channel Mode Message.
   */
  public MIDIEvent(int runningStatus) throws IllegalArgumentException
  {
    super();
    if ((runningStatus < CATEGORY_NOTE_OFF) ||
	(runningStatus >= CATEGORY_SYSTEM)) // not in 0x80..0xef
      throw new IllegalArgumentException("runningStatus");
    this.runningStatus = (byte)runningStatus;
  }

  /**
   * Returns true, if this MIDIEvent is complete and no more accepts
   * any further data.
   * @return True, if this MIDIEvent is complete.
   */
  public boolean isComplete() { return complete; }

  /**
   * This method is called whenever this MIDIEvent is found to have become
   * complete. It calls method exec() to allow subclasses to implement
   * further functionality on event completion.
   */
  protected void makeComplete()
  {
    complete = true;
    exec();
  }

  /**
   * Given a signed byte value in the range -128..+127, returns an unsigned
   * byte in the range 0..255.
   * @param b The signed byte value.
   * @return The unsigned byte value.
   */
  private int unsigned(byte b)
  {
    return b & 0xff;
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed.
   * It may also be called any time after the MIDIEvent has been completed.
   * The default implementation delegates the control flow to the next
   * more specific execXXX() method.
   * Subclasses may want to override this method.
   * @execption IllegalStateException If this event has not been completed
   *    by now.
   */
  public void exec() throws IllegalStateException
  {
    if (!complete)
      throw new IllegalStateException("event not completed by now");
    int category = statusByte & CATEGORY_MASK;
    switch (category)
      {
      case CATEGORY_NOTE_OFF:
	execNoteOff(unsigned(channel),
		    unsigned(dataByte1), unsigned(dataByte2));
	break;
      case CATEGORY_NOTE_ON:
	execNoteOn(unsigned(channel),
		   unsigned(dataByte1), unsigned(dataByte2));
	break;
      case CATEGORY_AFTERTOUCH:
	execAftertouch(unsigned(channel),
		       unsigned(dataByte1), unsigned(dataByte2));
	break;
      case CATEGORY_CTRL_CHANGE:
	execCtrlChange(unsigned(channel),
		       unsigned(dataByte1), unsigned(dataByte2));
	break;
      case CATEGORY_PITCH_WHEEL:	
	execPitchWheel(unsigned(channel),
		       (unsigned(dataByte2) << 7) | unsigned(dataByte1));
	break;
      case CATEGORY_PROG_CHANGE:
	execProgChange(unsigned(channel), unsigned(dataByte1));
	break;
      case CATEGORY_CHANNEL_PRESSURE:
	execChannelPressure(unsigned(channel), unsigned(dataByte1));
	break;
      case CATEGORY_SYSTEM:
	switch (unsigned(statusByte))
	  {
	  case SYSTEM_SOX:
	    execSysEx();
	    // [PENDING: full implementation of SysEx needs something like
	    // execSysEx(contents); ]
	    break;
	  case SYSTEM_MTC_QUARTER_FRAME:
	    execMTCQuarterFrame(dataByte1 >>> 4, dataByte1 & 0x0f);
	    break;
	  case SYSTEM_TUNE_REQUEST:
	    execTuneRequest();
	    break;
	  case SYSTEM_SONG_SELECT:
	    execSongSelect(unsigned(dataByte1));
	    break;
	  case SYSTEM_SONG_POSITION_POINTER:
	    execSongPositionPointer((unsigned(dataByte2) << 7) |
				    unsigned(dataByte1));
	    break;
	  case SYSTEM_TIMING_CLOCK:
	    execTimingClock();
	    break;
	  case SYSTEM_F9:
	    execSystemF9();
	    break;
	  case SYSTEM_SONG_START:
	    execSongStart();
	    break;
	  case SYSTEM_SONG_CONTINUE:
	    execSongContinue();
	    break;
	  case SYSTEM_SONG_STOP:
	    execSongStop();
	    break;
	  case SYSTEM_FD:
	    execSystemFD();
	    break;
	  case SYSTEM_ACTIVE_SENSING:
	    execActiveSensing();
	    break;
	  case SYSTEM_RESET:
	    execSystemReset();
	    break;
	  }
	break;
      }
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Note Off event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param channel The MIDI channel (0..15) to apply.
   * @param pitch The note number (0..127) that represents the pitch.
   * @param velocity The note off velocity value (0..127).
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execNoteOff(int channel, int pitch, int velocity)
       throws IllegalArgumentException
  {
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Note On event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
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
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Polyphonic Key Pressure (Aftertouch) event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param channel The MIDI channel (0..15) to apply.
   * @param pitch The note number (0..127) that represents the pitch.
   * @param pressure The pressure value (0..127).
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execAftertouch(int channel, int pitch, int velocity)
       throws IllegalArgumentException
  {
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Control Change event.
   * The default implementation examines the event and subsequently calls
   * one of the following methods, depending on the controller number:
   * execCtrlChange14, execCtrlChange7, execChannelModeMsg
   * Subclasses may override this method.
   * @param channel The MIDI channel (0..15) to apply.
   * @param pitch The note number (0..127) that represents the pitch.
   * @param pressure The pressure value (0..127).
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   * @see #execCtrlChange14
   * @see #execCtrlChange7
   * @see #execChannelModeMsg
   */
  protected void execCtrlChange(int channel, int controller, int value)
       throws IllegalArgumentException
  {
    if ((controller < 0x00) || (controller > 0xff))
      throw new IllegalArgumentException("controller not a byte");
    else if (controller < 0x40)
      {
	int courseController = controller & 0x20;
	boolean isLSB = controller < 0x20;
	if (strictArgCheck)
	  {
	    if (CONTROLLERS_14[controller].startsWith("Undefined"))
	      throw new IllegalArgumentException("controller undefined");
	    if ((0x30 <= controller) && (controller < 0x34))
	      throw new IllegalArgumentException("controller undefined");
	  }
	execCtrlChange14(channel, courseController, isLSB, value);
      }
    else if (controller < 0x79)
      {
	if (strictArgCheck)
	  if (CONTROLLERS_7[controller].startsWith("Undefined"))
	    throw new IllegalArgumentException("controller undefined");
	execCtrlChange7(channel, controller, value);
      }
    else execChannelModeMsg(channel, controller, value);
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Program Change event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param channel The MIDI channel (0..15) to apply.
   * @param program The program number (0..127).
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execProgChange(int channel, int program)
       throws IllegalArgumentException
  {
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Channel Pressure (Aftertouch) event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param channel The MIDI channel (0..15) to apply.
   * @param pressure The pressure value (0..127).
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execChannelPressure(int channel, int program)
       throws IllegalArgumentException
  {
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Pitch Bend Change event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param channel The MIDI channel (0..15) to apply.
   * @param value The pitch bend change value (0..16383).
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execPitchWheel(int channel, int value)
       throws IllegalArgumentException
  {
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Control Change event for a 2 bytes controller.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
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
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Control Change event for a single byte controller.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param channel The MIDI channel (0..15) to apply.
   * @param controller The controller number (64..120)
   * @param value The new value for the controller.
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execCtrlChange7(int channel, int controller, int value)
       throws IllegalArgumentException
  {
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Channel Mode Message event.
   * The default implementation examines the event and subsequently calls
   * one of the following methods, depending on the controller number:
   * execResetAllControllers, execLocalControlOff, execLocalControlOn,
   * execAllNotesOff, execOmniModeOff, execOmniModeOn, execMonoModeOn,
   * execPolyModeOn
   * @param channel The MIDI channel (0..15) to apply.
   * @param controller The controller number (64..120)
   * @param value The new value for the controller.
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   * @see #execResetAllControllers
   * @see #execLocalControlOff
   * @see #execLocalControlOn
   * @see #execAllNotesOff
   * @see #execOmniModeOff
   * @see #execOmniModeOn
   * @see #execMonoModeOn
   * @see #execPolyModeOn
   */
  protected void execChannelModeMsg(int channel, int controller, int value)
       throws IllegalArgumentException
  {
    switch (controller)
      {
      case MODE_RESET_ALL_CONTROLLERS:
	if (strictArgCheck)
	  if (value != 0)
	    throw new IllegalArgumentException("value != 0");
	execResetAllControllers();
	break;
      case MODE_LOCAL_CONTROL:
	if (strictArgCheck)
	  if ((value != 0) && (value != 127))
	    throw new IllegalArgumentException("value != 0 && value != 127");
	if (value < 0x40)
	  execLocalControlOff();
	else
	  execLocalControlOn();
	break;
      case MODE_ALL_NOTES_OFF:
	if (strictArgCheck)
	  if (value != 0)
	    throw new IllegalArgumentException("value != 0");
	execAllNotesOff();
	break;
      case MODE_OMNI_MODE_OFF:
	if (strictArgCheck)
	  if (value != 0)
	    throw new IllegalArgumentException("value != 0");
	execOmniModeOff();
	break;
      case MODE_OMNI_MODE_ON:
	if (strictArgCheck)
	if (value != 0)
	  throw new IllegalArgumentException("value != 0");
	execOmniModeOn();
	break;
      case MODE_MONO_MODE_ON:
	if (strictArgCheck)
	if (value > 16)
	  throw new IllegalArgumentException("value > 16");
	execMonoModeOn(value);
	break;
      case MODE_POLY_MODE_ON:
	if (strictArgCheck)
	  if (value != 0)
	    throw new IllegalArgumentException("value != 0");
	execPolyModeOn();
	break;
      }
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Reset All Controllers event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   */
  protected void execResetAllControllers() {}

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Local Control Off event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   */
  protected void execLocalControlOff() {}

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Local Control On event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   */
  protected void execLocalControlOn() {}

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a All Notes Off event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   */
  protected void execAllNotesOff() {}

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents an OmniModeOff event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   */
  protected void execOmniModeOff() {}

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents an OmniModeOn event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   */
  protected void execOmniModeOn() {}

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents an MonoModeOn event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param channels The number of channels to use (1..16) or 0, if the
   *    number of channels shall equal the number of voices in the receiver.
   */
  protected void execMonoModeOn(int channels) {}

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents an PolyModeOn event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   */
  protected void execPolyModeOn() {}

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a SysEx event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execSysEx() throws IllegalArgumentException
  {
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a MIDI Time Code Quarter Frame event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param type The message type (0..7).
   * @param value The message value (0..15).
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execMTCQuarterFrame(int type, int value)
       throws IllegalArgumentException
  {
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Song Position Pointer event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param value The song position pointer value (0..16383).
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execSongPositionPointer(int value)
       throws IllegalArgumentException
  {
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Song Select event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param value The song select number (0..127).
   * @exception IllegalArgumentException If the arguments form an invalid
   *    MIDI event.
   */
  protected void execSongSelect(int value)
       throws IllegalArgumentException
  {
  }

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Tune Request event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   */
  protected void execTuneRequest() {}

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Timing Clock event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   */
  protected void execTimingClock() {}

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a System 0xf9 event (currently undefined).
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   */
  private void execSystemF9() {}

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Song Start event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   */
  protected void execSongStart() {}

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Song Continue event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   */
  protected void execSongContinue() {}

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a Song Stop event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   */
  protected void execSongStop() {}

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a System 0xfd event (currently undefined).
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   */
  private void execSystemFD() {}

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents an Active Sensing event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   */
  protected void execActiveSensing() {}

  /**
   * This method is called as soon as this MIDIEvent has been completed and
   * if it represents a System Reset event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   */
  protected void execSystemReset() {}

  /**
   * Sets the status byte of this MIDIEvent.
   * @param statusByte The status byte of this MIDIEvent.
   * @exception IllegalArgumentException If statusByte is invalid.
   */
  private void setStatusByte(byte statusByte) throws IllegalArgumentException
  {
    this.statusByte = statusByte;
    count++;
    if ((statusByte & CATEGORY_MASK) == CATEGORY_SYSTEM)
      {
	int unsignedStatusByte = unsigned(statusByte);
	if (unsignedStatusByte == SYSTEM_EOX)
	  throw new IllegalArgumentException("EOX is not a valid status " +
					     "byte");
	if ((unsignedStatusByte == SYSTEM_F4) ||
	    (unsignedStatusByte == SYSTEM_F5))
	  throw new IllegalArgumentException("unknown system common " +
					     "message: " + statusByte);
	if (strictArgCheck)
	  if ((unsignedStatusByte == SYSTEM_F9) ||
	      (unsignedStatusByte == SYSTEM_FD))
	    throw new IllegalArgumentException("unknown system real time " +
					       "message: " + statusByte);
	if ((unsignedStatusByte == SYSTEM_TUNE_REQUEST) ||
	    (unsignedStatusByte >= SYSTEM_TIMING_CLOCK)) // Sys Real-Time Msg
	  makeComplete();
      }
    else // channel voice message
      channel = (byte)(statusByte & CHANNEL_MASK);
  }

  /**
   * Adds a data byte to this MIDIEvent.
   * @param data The data byte to be added.
   * @exception IllegalArgumentException If adding the specified byte would
   *    lead to an illegal MIDIEvent.
   */
  private void addDataByte(byte data) throws IllegalArgumentException
  {
    int category = statusByte & CATEGORY_MASK;
    switch (category)
      {
      case CATEGORY_NOTE_OFF:
      case CATEGORY_NOTE_ON:
      case CATEGORY_AFTERTOUCH:
      case CATEGORY_CTRL_CHANGE:
      case CATEGORY_PITCH_WHEEL:	
	if (count == 1)
	  {
	    dataByte1 = data;
	    count++;
	  }
	else if (count == 2)
	  {
	    dataByte2 = data;
	    count++;
	    makeComplete();
	  }
	break;
      case CATEGORY_PROG_CHANGE:
      case CATEGORY_CHANNEL_PRESSURE:
	dataByte1 = data;
	count++;
	makeComplete();
	break;
      case CATEGORY_SYSTEM:
	switch (unsigned(statusByte))
	  {
	  case SYSTEM_SOX:
	    // [PENDING: if (unsigned(data) != SYSTEM_EOX)
	    // { collect SysEx data } ]
	    if (unsigned(data) < SYSTEM_TIMING_CLOCK) // 0x80 <= data < 0xf0
	      if (unsigned(data) == SYSTEM_EOX)
		makeComplete(); // SysEx normally terminated
	      else
		makeComplete(); // SysEx aborted
	    break;
	  case SYSTEM_MTC_QUARTER_FRAME:
	  case SYSTEM_SONG_POSITION_POINTER:
	  case SYSTEM_SONG_SELECT:
	    if (count == 1)
	      {
		dataByte1 = data;
		count++;
		if (statusByte != SYSTEM_SONG_POSITION_POINTER)
		  makeComplete();
	      }
	    else if (count == 2) // song position pointer only
	      {
		dataByte2 = data;
		count++;
		makeComplete();
	      }
	    break;
	  }
	break;
      }
  }

  /**
   * Writes a MIDI byte into this object to actually build this MIDIEvent.
   * Use this method as often as needed to consecutively construct the event.
   * @param data The MIDI byte to be added. This must contain the value of an
   *    unsigned byte (0..255). If no valid runningStatus was given, the first
   *    byte written must be a status byte; otherwise, the status byte may be
   *    ommited; then the runningStatus is automatically used as status byte.
   * @exception IllegalArgumentException If adding the specified byte would
   *    lead to an illegal MIDIEvent.
   */
  public void write(int data) throws IllegalArgumentException
  {
    if ((data < 0x00) || (data > 0xff))
      throw new IllegalArgumentException("data not in byte range");
    if (count == 0)
      if (data < 0x80)
	if (runningStatus == 0) // no running status given
	  throw new IllegalArgumentException("data not a status byte and " +
					     "no running status given (" +
					     data + ")");
	else
	  {
	    setStatusByte(runningStatus);
	    write(data);
	  }
      else setStatusByte((byte)data);
    else
      if (data >= 0x80)
	throw new IllegalArgumentException("data seems to be a status byte");
      else if (complete)
	throw new IllegalArgumentException("MIDI event already complete");
      else
	addDataByte((byte)data);
  }

  /**
   * Returns the status byte of this SMFEvent.
   * @return The status byte of this SMFEvent as an int value in the
   *    range 0..255.
   * @exception IllegalStateException If the status byte has not been written
   *    to this MIDIEvent so far.
   */
  public int getStatusByte()
  {
    if (count == 0)
      throw new IllegalStateException("status byte not available");
    else
      return unsigned(statusByte);
  }

  /**
   * Returns the running status byte for any succeeding MIDI event.
   * @return The status byte of this event or, if this event is a real-time
   *    message, the running status that has been passed to the constructor
   *    method of this event (128..239).<BR>
   *    Returns 0, if a running status can not be computed from this event
   *    (e.g. because this event represents a SysEx event or this event
   *    represents a real-time message, but no running status has been passed
   *    to this event.
   * @exception IllegalArgumentException If the status byte has not been
   *    written to this MIDIEvent so far.
   */
  public int getRunningStatus()
  {
    if (count == 0)
      throw new IllegalStateException("status byte not available");
    int unsignedStatusByte = unsigned(statusByte);
    if (unsignedStatusByte < CATEGORY_SYSTEM)
      return unsignedStatusByte;
    else if (unsignedStatusByte < SYSTEM_TIMING_CLOCK)
      return 0;
    else if (runningStatus == 0)
      return 0;
    else
      return unsigned(runningStatus);
  }

  /**
   * For all kinds of messages except System Exclusive Messages, returns
   * an array that contains a sequence of MIDI bytes that represent this
   * MIDIEvent. Effectively, this array contains the values that have
   * been written into this object, except for the status byte, that may
   * be added or left, depending on the running status setting.
   * @param runningStatus The running status byte. The array will contain
   *    a status byte only if the status byte does not equal the given
   *    running status. To force submission of the status byte, just set the
   *    running status to 0.
   * @return An array of (signed) bytes that contains this MIDIEvent as a
   *    sequence of MIDI (signed) bytes.
   * @exception IllegalArgumentException If runningStatus is not a value in
   *    the range 0..255.
   * @exception IllegalStateException If this MIDIEvent is not yet complete
   *    or if it is a System Exclusive Message, as such messages are not
   *    supported by this method.
   * @see #isComplete
   */
  public byte[] getContents(int runningStatus)
  {
    if (!complete)
      throw new IllegalStateException("MIDIEvent not yet complete");
    if ((runningStatus < 0x00) || (runningStatus > 0xff))
      throw new IllegalArgumentException("runningStatus not in byte range");
    if (unsigned(statusByte) == SYSTEM_SOX)
      throw new IllegalStateException("method not applicable on SysEx data");
    byte[] contents;
    int offset;
    if (runningStatus == unsigned(statusByte))
      {
	contents = new byte[count - 1];
	offset = 0;
      }
    else
      {
	contents = new byte[count];
	contents[0] = statusByte;
	offset = 1;
      }
    if (count > 1)
      {
	contents[offset] = dataByte1;
	if (count > 2)
	  contents[++offset] = dataByte2;
      }
    return contents;
  }

  /**
   * Transmits a byte representation of this MIDIEvent via the specified
   * output stream.
   * @param out The output stream where to transmit this MIDIEvent.
   * @param runningStatus The running status byte. The array will contain
   *    a status byte only if the status byte does not equal the given
   *    running status. To force submission of the status byte, just set the
   *    running status to 0.
   * @exception IllegalArgumentException If runningStatus is not a value in
   *    the range 0..255.
   * @exception IllegalStateException If this MIDIEvent is not yet complete
   *    or if it is a System Exclusive Message, as such messages are not
   *    supported by this method.
   * @exception IOException If an I/O error occurs.
   * @see #isComplete
   */
  public void transmit(OutputStream out, int runningStatus) throws IOException
  {
    out.write(getContents(runningStatus));
  }

  /**
   * Returns a textual representation of the specified controller or
   * Channel Mode Message.
   * @param controller The number of the controller or Channel Mode Message.
   * @return A textual representation of the controller or Channel Mode
   *    Message.
   * @exception IllegalArgumentException If controller >= 0x80.
   */
  private String controllerToString(int controller)
  {
    if ((controller >= 0) && (controller < 0x80))
      if (controller < 0x40)
	if (controller < 0x20)
	  return CONTROLLERS_14[controller] + "(coarse)";
	else
	  return CONTROLLERS_14[controller - 0x20] + "(fine)";
      else
	return CONTROLLERS_7[controller - 0x40];
    else
      throw new IllegalArgumentException("controller >= 0x80 or negative");
  }

  /**
   * Copies all instance variables from the specified MIDIEvent into this
   * one, so that, effectively, this object becomes a clone of the
   * specified argument.<BR>
   * This method is supplied as an alternative to the clone() method,
   * because a subclass of MIDIEvent can not create a clone of a MIDIEvent
   * object on itself, as a pure MIDIEvent object may not be typecasted
   * into some subclass object; however, an instance of this class may
   * actually be an instance of a subclass of this class, and then
   * you can, from within the subclass, call this method to copy all
   * private instance variables of MIDIEvent into the clone.
   * @param event The MIDIEvent to be cloned.
   */
  protected void copyFrom(MIDIEvent event)
  {
    count = event.count;
    runningStatus = event.runningStatus;
    statusByte = event.statusByte;
    dataByte1 = event.dataByte1;
    dataByte2 = event.dataByte2;
    sysexData = event.sysexData;
    complete = event.complete;
    channel = event.channel;
  }

  /**
   * Returns a String representation of this object, even if it is not
   * yet completed.
   */
  public String toString()
  {
    StringBuffer s = new StringBuffer();
    if (count > 0)
      {
	int category = statusByte & CATEGORY_MASK;
	s.append(CATEGORIES[category >> 4]);
	if (category < CATEGORY_SYSTEM) // 0x80 <= category < 0xf0
	  {
	    s.append("; channel=" + (category & CHANNEL_MASK));
	    if (complete)
	      switch (category)
		{
		case CATEGORY_NOTE_OFF:
		case CATEGORY_NOTE_ON:
		  s.append("; key=" + PITCHES[unsigned(dataByte1)] +
			   "; velocity=" + unsigned(dataByte2));
		  break;
		case CATEGORY_AFTERTOUCH:
		  s.append("; key=" + PITCHES[unsigned(dataByte1)] +
			   "; pressure=" + unsigned(dataByte2));
		  break;
		case CATEGORY_CTRL_CHANGE:
		  s.append("; controller=" + unsigned(dataByte1) + "(" +
			   controllerToString(unsigned(dataByte1)) +
			   "); value=" + unsigned(dataByte2));
		  break;
		case CATEGORY_PROG_CHANGE:
		  s.append("; program=" + unsigned(dataByte1));
		  break;
		case CATEGORY_CHANNEL_PRESSURE:
		  s.append("; pressure=" + unsigned(dataByte1));
		  break;
		case CATEGORY_PITCH_WHEEL:
		  s.append("; pitch=" + ((unsigned(dataByte2) << 7) |
					 unsigned(dataByte1)));
		  break;
		}
	  }
	else // 0xf0 <= category < 0xff
	  {
	    s.append(SYSTEM_MESSAGES[statusByte & CHANNEL_MASK]);
	    switch (unsigned(statusByte))
	      {
	      case SYSTEM_SOX:
		break;
	      case SYSTEM_SONG_POSITION_POINTER:
		s.append("; position=" + ((unsigned(dataByte2) << 7) |
					  unsigned(dataByte1)));
		break;
	      case SYSTEM_SONG_SELECT:
		s.append("; song number=" + unsigned(dataByte1));
		break;
	      }
	  }
      }
    return s.toString();
  }
}
