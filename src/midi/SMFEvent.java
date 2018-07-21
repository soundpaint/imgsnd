/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/midi/SMFEvent.java.rca $
 * $Revision: 1.2 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Tue Jul 21 23:57:42 1998 $
 * $State: Experimental $
 */

/*
 * @(#)SMFEvent.java 1.00 98/04/19
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
 * Each instance of this class represents a single event of a Standard
 * MIDI File (SMF).
 */
public class SMFEvent extends MIDIEvent
{

  /**
   * The maximum possible value of a variable length quantity.
   */
  public final static int MAX_QUANTITY_VALUE = 0x0fffffff;

  /**
   * The constant that is used to lead in SMF Meta Events.
   */
  public final static int META_EVENT = 0xff;

  /**
   * This array contains verbatim labels for all 128 defined and undefined
   * Meta Events.
   */
  public final static String[] META_EVENTS =
  {
    // 0x00
    "Sequence Number",  "Text Event",
    "Copyright Notice", "Sequence/Track Name",
    "Instrument Name",  "Lyric",
    "Marker",           "Cue Point",
    "Undefined (0x08)", "Undefined (0x09)",
    "Undefined (0x0a)", "Undefined (0x0b)",
    "Undefined (0x0c)", "Undefined (0x0d)",
    "Undefined (0x0e)", "Undefined (0x0f)",
    // 0x10
    "Undefined (0x00)", "Undefined (0x01)",
    "Undefined (0x02)", "Undefined (0x03)",
    "Undefined (0x04)", "Undefined (0x05)",
    "Undefined (0x06)", "Undefined (0x07)",
    "Undefined (0x08)", "Undefined (0x09)",
    "Undefined (0x0a)", "Undefined (0x0b)",
    "Undefined (0x0c)", "Undefined (0x0d)",
    "Undefined (0x0e)", "Undefined (0x0f)",
    // 0x20
    "MIDI Channel",     "MIDI Port",
    "Undefined (0x22)", "Undefined (0x23)",
    "Undefined (0x24)", "Undefined (0x25)",
    "Undefined (0x26)", "Undefined (0x27)",
    "Undefined (0x28)", "Undefined (0x29)",
    "Undefined (0x2a)", "Undefined (0x2b)",
    "Undefined (0x2c)", "Undefined (0x2d)",
    "Undefined (0x2e)", "End of Track",
    // 0x30
    "Undefined (0x30)", "Undefined (0x31)",
    "Undefined (0x32)", "Undefined (0x33)",
    "Undefined (0x34)", "Undefined (0x35)",
    "Undefined (0x36)", "Undefined (0x37)",
    "Undefined (0x38)", "Undefined (0x39)",
    "Undefined (0x3a)", "Undefined (0x3b)",
    "Undefined (0x3c)", "Undefined (0x3d)",
    "Undefined (0x3e)", "Undefined (0x3f)",
    // 0x40
    "Undefined (0x40)", "Undefined (0x41)",
    "Undefined (0x42)", "Undefined (0x43)",
    "Undefined (0x44)", "Undefined (0x45)",
    "Undefined (0x46)", "Undefined (0x47)",
    "Undefined (0x48)", "Undefined (0x49)",
    "Undefined (0x4a)", "Undefined (0x4b)",
    "Undefined (0x4c)", "Undefined (0x4d)",
    "Undefined (0x4e)", "Undefined (0x4f)",
    // 0x50
    "Undefined (0x50)", "Set Tempo",
    "Undefined (0x52)", "Undefined (0x53)",
    "SMPTE Offset",     "Undefined (0x55)",
    "Undefined (0x56)", "Undefined (0x57)",
    "Time Signature",   "Key Signature",
    "Undefined (0x5a)", "Undefined (0x5b)",
    "Undefined (0x5c)", "Undefined (0x5d)",
    "Undefined (0x5e)", "Undefined (0x5f)",
    // 0x60
    "Undefined (0x60)", "Undefined (0x61)",
    "Undefined (0x62)", "Undefined (0x63)",
    "Undefined (0x64)", "Undefined (0x65)",
    "Undefined (0x66)", "Undefined (0x67)",
    "Undefined (0x68)", "Undefined (0x69)",
    "Undefined (0x6a)", "Undefined (0x6b)",
    "Undefined (0x6c)", "Undefined (0x6d)",
    "Undefined (0x6e)", "Undefined (0x6f)",
    // 0x70
    "Undefined (0x70)", "Undefined (0x71)",
    "Undefined (0x72)", "Undefined (0x73)",
    "Undefined (0x74)", "Undefined (0x75)",
    "Undefined (0x76)", "Undefined (0x77)",
    "Undefined (0x78)", "Undefined (0x79)",
    "Undefined (0x7a)", "Undefined (0x7b)",
    "Undefined (0x7c)", "Undefined (0x7d)",
    "Undefined (0x7e)", "Proprietary Meta Event"
  };

  private int deltaTime = 0;
  private boolean deltaTimeComplete = false;
  private boolean midiEvent = false;
  private boolean sysExEvent = false;
  private boolean metaEvent = false;
  private boolean typeRead = false;
  private int length = 0; // byte length of meta event / sysex event
  private boolean lengthComplete = false;
  private byte type = 0; // meta event type
  private Vector metaContents = null; // meta event contents

  /**
   * Returns true, if this SMFEvent represents a usual MIDI event.
   * @return True, if this SMFEvent represents a usual MIDI event.
   */
  public boolean isMIDIEvent() { return midiEvent; }

  /**
   * Returns true, if this SMFEvent represents a SysEx event.
   * @return True, if this SMFEvent represents a SysEx event.
   */
  public boolean isSysExEvent() { return sysExEvent; }

  /**
   * Returns true, if this SMFEvent represents a meta event.
   * @return True, if this SMFEvent represents a meta event.
   */
  public boolean isMetaEvent() { return metaEvent; }

  /**
   * Creates a new SMFEvent from the specified MIDIEvent.
   * @param deltaTime The delta time for the event.
   * @param event The MIDIEvent to use as source.
   * @see #write
   */
  public SMFEvent(int deltaTime, MIDIEvent event)
  {
    super();
    super.copyFrom(event);
    this.deltaTime = deltaTime;
    deltaTimeComplete = true;
    midiEvent = true;
  }

  /**
   * Creates a new empty SMFEvent with no running status info.<BR>
   * Use method write() to write a sequence of bytes from a Standard MIDI
   * File that define the contents of this SMF event.
   * @see #write
   */
  public SMFEvent()
  {
    super();
  }

  /**
   * Creates a new empty SMFEvent with running status info.<BR>
   * Use method write() to write a sequence of bytes from a standard MIDI
   * File that define the contents of this SMFEvent.
   * @param runningStatus The status byte of the previous SMFEvent.
   *    Running status is valid only for Channel Voice and Channel Mode
   *    Messages, but not for System Messages.
   * @exception IllegalArgumentException If runningStatus leads in neither
   *    a Channel Voice nor a Channel Mode Message.
   */
  public SMFEvent(int runningStatus) throws IllegalArgumentException
  {
    super(runningStatus);
  }

  /**
   * This method is called as soon as this SMFEvent has been completed.
   * It may also be called any time after the SMFEvent has been completed.
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
    if (metaEvent)
      execMetaEvent(type, metaContents);
    else
      super.exec();
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Sequence Number Meta Event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param s The sequence number (0x0000...0x3fff).
   * @exception IllegalArgumentException A subclass implementation may
   *    throw an IllegalArgumentException, if this event occurs at an
   *    illegal position, e.g. if it occurs more than once in a track.
   */
  protected void execSequenceNumber(short s) throws IllegalArgumentException
  {
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Text Event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param text The text string.
   */
  protected void execText(String text)
  {
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Cpyright Notice Meta Event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param notice The copyright notice.
   */
  protected void execCopyright(String notice)
  {
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Sequence/Track Name Meta Event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param name The sequence/track name.
   */
  protected void execTrackName(String name)
  {
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Instrument Name Meta Event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param name The instrument name.
   */
  protected void execInstrumentName(String name)
  {
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Lyric Meta Event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param syllable The lyrics syllable.
   */
  protected void execLyric(String syllable)
  {
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Marker Meta Event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param name The name of the marker.
   */
  protected void execMarker(String name)
  {
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Cue Point Meta Event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param description The cue point description.
   */
  protected void execCuePoint(String description)
  {
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF MIDI Channel Meta Event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param channel The channel prefix number (0..15) for any subsequent
   *    Meta Event System Exclusive Event.
   */
  protected void execMIDIChannel(int channel)
  {
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF MIDI Port Meta Event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param port The port prefix number (0..255) for any subsequent
   *    Meta Event System Exclusive Event.
   */
  protected void execMIDIPort(int port)
  {
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF End of Track Meta Event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   */
  protected void execEndOfTrack()
  {
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Set Tempo Meta Event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param tempo The tempo in microseconds per MIDI quarter-note.
   */
  protected void execSetTempo(int tempo)
  {
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF SMPTE Offset Meta Event.
   * The SMPTE Offset designates the SMPTE time at which the track chunk is
   * supposed to start.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param hours The offset time hours (0..255).
   * @param minutes The offset time minutes (0..59).
   * @param secs The offset time seconds (0..59).
   * @param frames The offset time frames (0..255).
   * @param subframes Fractional frames in 100ths of a frame (0..99).
   */
  protected void execSMPTEOffset(int hours, int minutes, int secs,
				 int frames, int subframes)
  {
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Time Signature Meta Event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
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
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Key Signature Meta Event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param sharps The signed number of sharps (typ. -7..+7).
   * @param minor True, if the signature represents a minor key. False,
   *    if the signature represents a major key.
   */
  protected void execKeySignature(int sharps, boolean minor)
  {
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Sequencer-Specific Meta Event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   * @param contents The contents of the sequencer-specific data as an
   *    array of Byte objects.
   */
  protected void execSequencerSpecific(Byte[] contents)
  {
  }

  /**
   * Returns the metaContents element at position #index as an unsigned
   * byte value.
   * @param index The index to be accessed.
   * @return The unsigned byte value (0..255) of the addressed contents.
   */
  private int getMetaContents(int index)
  {
    return ((Byte)metaContents.elementAt(index)).byteValue() & 0xff;
  }

  /**
   * This method is called as soon as this SMFEvent has been completed and
   * if it represents a SMF Meta Event.
   * The default implementation does nothing. Subclasses are encouraged to
   * override this method.
   */
  protected void execMetaEvent(int type, Vector metaContents)
  {
    switch (type)
      {
      case 0x00:
	if (strictArgCheck)
	  if (length != 0x02)
	    throw new IllegalArgumentException("Sequence Number Event: " +
					       "length != 0x02");
	execSequenceNumber((short)((getMetaContents(0) << 7) |
				   getMetaContents(1)));
	break;
      case 0x01:
      case 0x02:
      case 0x03:
      case 0x04:
      case 0x05:
      case 0x06:
      case 0x07:
	StringBuffer text = new StringBuffer(metaContents.size());
	for (int i = 0; i < metaContents.size(); i++)
	  text.append((char)(getMetaContents(i)));
	switch (type)
	  {
	  case 0x01:
	    execText(text.toString());
	    break;
	  case 0x02:
	    execCopyright(text.toString());
	    break;
	  case 0x03:
	    execTrackName(text.toString());
	    break;
	  case 0x04:
	    execInstrumentName(text.toString());
	    break;
	  case 0x05:
	    execLyric(text.toString());
	    break;
	  case 0x06:
	    execMarker(text.toString());
	    break;
	  case 0x07:
	    execCuePoint(text.toString());
	    break;
	  }
	break;
      case 0x20:
	if (strictArgCheck)
	  if (length != 0x01)
	    throw new IllegalArgumentException("MIDI Channel Event: " +
					       "length != 0x01");
	int channel = getMetaContents(0);
	if (strictArgCheck)
	  if (channel > 15)
	    throw new IllegalArgumentException("MIDI Channel Event: channel " +
					       "out of range: " + channel);
	execMIDIChannel(channel);
	break;
      case 0x21:
	if (strictArgCheck)
	  if (length != 0x01)
	    throw new IllegalArgumentException("MIDI Port Event: " +
					       "length != 0x01");
	execMIDIPort(getMetaContents(0));
	break;
      case 0x2f:
	if (strictArgCheck)
	  if (length != 0x00)
	    throw new IllegalArgumentException("End of Track Event: " +
					       "length != 0x00");
	execEndOfTrack();
	break;
      case 0x51:
	if (strictArgCheck)
	  if (length != 0x03)
	    throw new IllegalArgumentException("Set Tempo Event: " +
					       "length != 0x03");
	execSetTempo((getMetaContents(0) << 14) |
		     (getMetaContents(1) << 7) |
		     getMetaContents(2));
	break;
      case 0x54:
	if (strictArgCheck)
	  if (length != 0x05)
	    throw new IllegalArgumentException("SMPTE Offset Event: " +
					       "length != 0x05");
	int hr = getMetaContents(0);
	int mn = getMetaContents(1);
	int se = getMetaContents(2);
	int fr = getMetaContents(3);
	int ff = getMetaContents(4);
	if (strictArgCheck)
	  {
	    if (mn > 59)
	      throw new IllegalArgumentException("SMPTE Offset Event: " +
						 "minutes out of range: " +
						 mn);
	    if (se > 59)
	      throw new IllegalArgumentException("SMPTE Offset Event: " +
						 "seconds out of range: " +
						 se);
	    if (ff > 99)
	      throw new IllegalArgumentException("SMPTE Offset Event: " +
						 "fractional farmes out of " +
						 "range: " + ff);
	  }
	execSMPTEOffset(hr, mn, se, fr, ff);
	break;
      case 0x58:
	if (strictArgCheck)
	  if (length != 0x04)
	    throw new IllegalArgumentException("Time Signature Event: " +
					       "length != 0x04");
	execTimeSignature(getMetaContents(0),
			  getMetaContents(1),
			  getMetaContents(2),
			  getMetaContents(3));
	break;
      case 0x59:
	if (strictArgCheck)
	  if (length != 0x02)
	    throw new IllegalArgumentException("Key Signature Event: " +
					       "length != 0x02");
	int sharps = (byte)getMetaContents(0); // force signed 8 bits
	int minor = getMetaContents(1);
	if (strictArgCheck)
	  {
	    if ((sharps < -7) || (sharps > +7))
	      throw new IllegalArgumentException("Key Signature Event: " +
						 "invalid sharps: " + sharps);
	    if ((minor != 0) && (minor != 1))
	      throw new IllegalArgumentException("Key Signature Event: " +
						 "invalid minor: " + minor);
	  }
	execKeySignature(sharps, minor != 0);
	break;
      case 0x7f:
	Byte[] contents = new Byte[metaContents.size()];
	metaContents.copyInto(contents);
	execSequencerSpecific(contents);
	break;
      default:
	if (strictArgCheck)
	  throw new IllegalArgumentException("Unknown Meta Event: " + type);
	break;
      }
  }

  /**
   * Returns true, if this SMFEvent is complete and no more accepts
   * any further data.
   * @return True, if this SMFEvent is complete.
   */
  public boolean isDeltaTimeComplete() { return deltaTimeComplete; }

  /**
   * Returns the delta time of this SMFEvent.
   * @return The delta time of this SMFEvent.
   * @exception IllegalStateException If not enough bytes have been written
   *    to this SMFEvent so far, to make the delta time complete.
   */
  public int getDeltaTime()
  {
    if (!isDeltaTimeComplete())
      throw new IllegalStateException("delta time not yet complete");
    else
      return deltaTime;
  }

  /**
   * Returns the running status byte for any succeeding SMF event.
   * @return The status byte of this event or, if this event is a real-time
   *    message, the running status that has been passed to the constructor
   *    method of this event. Returns 0, if a running status can not be
   *    computed from this event (e.g. because this event represents a SysEx
   *    event or this event represents a real-time message or a meta event,
   *    but no running status has been passed to this event.
   * @exception IllegalArgumentException If the status byte has not been
   *    written to this SMFEvent so far.
   */
  public int getRunningStatus()
  {
    if (!metaEvent)
      return super.getRunningStatus();
    else if (runningStatus == 0)
      return 0;
    else
      return runningStatus & 0xff;
  }

  /**
   * Returns a variable length quantity as a byte array of variable length
   * for the specified value.
   * [PENDING: This method even allows to generate quantities that cover
   * more than 4 bytes, as long as the resulting value is not above
   * 0x0fffffff (e.g. 0x000fffffff). The SMF spec does not clearly state,
   * if this is allowed or not.]
   * @param value The int value to be converted into a variable length
   *    quantity.
   * @return The variable length quantity as a byte array of variable length.
   * @exception IllegalStateException If value is below zero or above
   *    MAX_QUANTITY_VALUE.
   * @see #MAX_QUANTITY_VALUE
   */
  public static byte[] intToVariableLengthQuantity(int value)
  {
    if ((value < 0x00) || (value > MAX_QUANTITY_VALUE))
      throw new IllegalArgumentException("value");
    int copyOfValue = value;
    int size;
    for (size = 0; copyOfValue > 0; size++)
      copyOfValue >>>= 7;
    if (size == 0) size = 1;
    byte[] quantity = new byte[size];
    for (size--; size >= 0; size--)
      {
	quantity[size] = (byte)(value & 0x7f);
	value >>>= 7;
      }
    return quantity;
  }

  /**
   * Adds data to the specified variable length quantity value.
   * [PENDING: This method even allows to parse quantities that cover
   * more than 4 bytes, as long as the resulting value is not above
   * 0x0fffffff (e.g. 0x000fffffff). The SMF spec does not clearly state,
   * if this is allowed or not.]
   * @param quantity The current value of some variable length quantity.
   * @param data The next 7 bits to be added. The most significant bit
   *    of this parameter is ignored.
   * @return The value of the augmented variable length quantity.
   * @exception IllegalArgumentException If the current value of the
   *    quantity is already out of range or if the value augmented
   *    quantity will be out of range.
   * @execption IllegalArgumentException If data is not an unsigned byte
   *    value in the range 0..255.
   */
  public static int addToVariableLengthQuantity(int quantity, int data)
       throws IllegalArgumentException
  {
    if ((quantity < 0x00) || (quantity > MAX_QUANTITY_VALUE))
      throw new IllegalArgumentException("quantity");
    if ((data < 0x00) || (data > 0xff))
      throw new IllegalArgumentException("data not in byte range");
    quantity = (quantity << 7) | (data & 0x7f);
    if (quantity > MAX_QUANTITY_VALUE)
      throw new IllegalArgumentException("data");
    else
      return quantity;
  }

  /**
   * Adds a data byte to this SMF Meta Event contents.
   * @param data The data byte to be added.
   * @exception IllegalArgumentException If adding the specified byte would
   *    lead to an illegal Meta Event.
   */
  private void addToMetaEventContents(byte data)
       throws IllegalArgumentException
  {
    metaContents.addElement(new Byte(data));
    if (metaContents.size() == length)
      makeComplete();
  }

  /**
   * Adds a data byte to this SMF Meta Event.
   * @param data The data byte to be added.
   * @exception IllegalArgumentException If adding the specified byte would
   *    lead to an illegal Meta Event.
   */
  private void addToMetaEvent(byte data) throws IllegalArgumentException
  {
    if (!typeRead)
      {
	if (strictArgCheck)
	  if (data < 0) // data >= 0x80
	    throw new IllegalArgumentException("meta event type >= 0x80");
	type = data;
	typeRead = true;
      }
    else if (!lengthComplete)
      {
	length = addToVariableLengthQuantity(length, data);
	if (data >= 0) // data < 0x80
	  {
	    lengthComplete = true;
	    metaContents = new Vector(length);
	    if (length == 0)
	      makeComplete();
	  }
      }
    else
      addToMetaEventContents(data);
  }

  /**
   * Adds a data byte to the underlying SysEx MIDIEvent.
   * @param data The data byte to be added.
   * @exception IllegalArgumentException If adding the specified byte would
   *    lead to an illegal SysEx MIDIEvent.
   */
  private void addToMIDISysExEvent(int data)
       throws IllegalArgumentException
  {
    if (true) throw new RuntimeException("not fully implemented yet");
    super.write(data);
  }

  /**
   * Adds a data byte to this SMF SysEx Event.
   * @param data The data byte to be added.
   * @exception IllegalArgumentException If adding the specified byte would
   *    lead to an illegal SMF SysEx Event.
   */
  private void addToSysExEvent(int data) throws IllegalArgumentException
  {
    if (!lengthComplete)
      {
	length = addToVariableLengthQuantity(length, data);
	if (data < 0x80)
	  lengthComplete = true;
      }
    else
      addToMIDISysExEvent(data);
  }

  /**
   * Adds a data byte to this SMFEvent.
   * @param data The data byte to be added.
   * @exception IllegalArgumentException If adding the specified byte would
   *    lead to an illegal SMFEvent.
   */
  private void addToEvent(int data) throws IllegalArgumentException
  {
    if (midiEvent)
      super.write(data);
    else if (complete)
      throw new IllegalArgumentException("MIDI event already complete");
    else if (sysExEvent)
      addToSysExEvent(data);
    else if (metaEvent)
      addToMetaEvent((byte)data);
    else
      if (data == META_EVENT)
	metaEvent = true;
      else if (data == SYSTEM_SOX)
	{
	  sysExEvent = true;
	  super.write(data);
	}
      else if (data == SYSTEM_EOX)
	{
	  sysExEvent = true;
	  super.write(SYSTEM_SOX); // SMF special SysEx handling: F7 -> F0
	}
      else if (data >= SYSTEM_TIMING_CLOCK)
	throw new IllegalArgumentException("invalid status byte: system " +
					   "messages other than SysEx are " +
					   "not allowed (" + data + ")");
      else
	{
	  midiEvent = true;
	  super.write(data);
	}
  }

  /**
   * Writes an SMF byte into this object to actually build this SMFEvent.
   * Use this method as often as needed to consecutively construct the event.
   * @param data The SMF byte to be added. If no valid runningStatus was
   *    given, the first byte written after delta time is complete must be a
   *    status byte; otherwise, the status byte may be ommited; then the
   *    runningStatus is automatically used as status byte.
   * @exception IllegalArgumentException If adding the specified byte would
   *    lead to an illegal SMFEvent.
   */
  public void write(int data) throws IllegalArgumentException
  {
    if (!deltaTimeComplete)
      {
	deltaTime = addToVariableLengthQuantity(deltaTime, data);
	if (data < 0x80)
	  deltaTimeComplete = true;
      }
    else
      addToEvent(data);
  }

  /**
   * Transmits a byte representation of this SMFEvent via the specified
   * output stream.
   * @param out The output stream where to transmit this SMFEvent.
   * @param runningStatus The running status byte. The array will contain
   *    a status byte only if the status byte does not equal the given
   *    running status. To force submission of the status byte, just set the
   *    running status to 0.
   * @exception IllegalArgumentException If runningStatus is not a value in
   *    the range 0..255.
   * @exception IllegalStateException If this SMFEvent is not yet complete
   *    or if it is a System Exclusive Message, as such messages are not
   *    supported by this method.
   * @exception IOException If an I/O error occurs.
   * @see #isComplete
   */
  public void transmit(OutputStream out, int runningStatus) throws IOException
  {
    if (!complete)
      throw new IllegalStateException("SMFEvent not yet complete");
    out.write(intToVariableLengthQuantity(deltaTime));
    if (midiEvent || sysExEvent)
      super.transmit(out, runningStatus);
    else if (metaEvent)
      {
	out.write(META_EVENT);
	out.write(type);
	for (int i = 0; i < metaContents.size(); i++)
	  out.write(getMetaContents(i));
      }
    else
      throw new IllegalStateException("internal error");
  }

  /**
   * Copies all instance variables from the specified SMFEvent into this
   * one, so that, effectively, this object becomes a clone of the
   * specified argument.<BR>
   * This method is supplied as an alternative to the clone() method,
   * because a subclass of SMFEvent can not create a clone of a SMFEvent
   * object on itself, as a pure SMFEvent object may not be typecasted
   * into some subclass object; however, an instance of this class may
   * actually be an instance of a subclass of this class, and then
   * you can, from within the subclass, call this method to copy all
   * private instance variables of SMFEvent into the clone.
   * @param event The SMFEvent to be cloned.
   */
  protected void copyFrom(SMFEvent event)
  {
    deltaTime = event.deltaTime;
    deltaTimeComplete = event.deltaTimeComplete;
    midiEvent = event.midiEvent;
    sysExEvent = event.sysExEvent;
    metaEvent = event.metaEvent;
    typeRead = event.typeRead;
    length = event.length;
    lengthComplete = event.lengthComplete;
    type = event.type;
    metaContents = event.metaContents;
  }

  /**
   * Provided that this object is a Meta Event, returns a String
   * representation of it, even if it is not yet completed.
   */
  private String metaEventToString()
  {
    StringBuffer s = new StringBuffer();
    if (typeRead)
      {
	s.append("=" + META_EVENTS[type]);
	if (complete)
	  switch (type)
	    {
	    case 0x00:
	      if (metaContents.size() >= 2)
		s.append("; Sequence Number=" +
			 ((getMetaContents(0) << 7) |
			  getMetaContents(1)));
	      break;
	    case 0x01:
	    case 0x02:
	    case 0x03:
	    case 0x04:
	    case 0x05:
	    case 0x06:
	    case 0x07:
	      StringBuffer text = new StringBuffer(metaContents.size());
	      for (int i = 0; i < metaContents.size(); i++)
		text.append((char)(getMetaContents(i)));
	      switch (type)
		{
		case 0x01:
		  s.append("; Text=" + text);
		  break;
		case 0x02:
		  s.append("; Copyright=" + text);
		  break;
		case 0x03:
		  s.append("; Track Name=" + text);
		  break;
		case 0x04:
		  s.append("; Instrument Name=" + text);
		  break;
		case 0x05:
		  s.append("; Lyric=" + text);
		  break;
		case 0x06:
		  s.append("; Marker Name=" + text);
		  break;
		case 0x07:
		  s.append("; Cue Point Description=" + text);
		  break;
		}
	      break;
	    case 0x20:
	      s.append("; Channel=" + getMetaContents(0));
	      break;
	    case 0x21:
	      s.append("; Port=" + getMetaContents(0));
	      break;
	    case 0x2f:
	      break; // no further parameter
	    case 0x51:
	      s.append("; Microseconds=" +
		       ((getMetaContents(0) << 14) |
		       (getMetaContents(1) << 7) |
		       getMetaContents(2)));
	      break;
	    case 0x54:
	      s.append("; Hours=" + getMetaContents(0) +
		       "; Minutes=" + getMetaContents(1) +
		       "; Seconds=" + getMetaContents(2) +
		       "; Frames=" + getMetaContents(3) +
		       "; Fractional Frames=" + getMetaContents(4));
	      break;
	    case 0x58:
	      s.append("; Nominator=" + getMetaContents(0) +
		       "; Denominator=" + getMetaContents(1) +
		       "; Metronome Clicks=" + getMetaContents(2) +
		       "; 32nds per Quarter=" + getMetaContents(3));
	      break;
	    case 0x59:
	      s.append("; Sharps=" + getMetaContents(0) +
		       "; Minor=" + (getMetaContents(1) != 0));
	      break;
	    case 0x7f:
	      Byte[] contents = new Byte[metaContents.size()];
	      metaContents.copyInto(contents);
	      s.append("Synth Specific=" + contents);
	      break;
	    }
      }
    return s.toString();
  }

  /**
   * Returns a String representation of this object, even if it is not
   * yet completed.
   */
  public String toString()
  {
    StringBuffer s = new StringBuffer();
    if (deltaTimeComplete)
      {
	s.append("delta=" + deltaTime);
	if (midiEvent || sysExEvent)
	  s.append("; event=" + super.toString());
	else if (metaEvent)
	  {
	    s.append("; meta event");
	    s.append(metaEventToString());
	  }
      }
    return s.toString();
  }
}
