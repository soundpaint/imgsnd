/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/midi/GM.java.rca $
 * $Revision: 1.1 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Sat Jun 27 14:29:05 1998 $
 * $State: Experimental $
 */

/*
 * @(#)GM.java 1.00 98/04/19
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
 * This class is part of the MIDI package.<BR>
 * It declares various GM stuff, such as GM patch/drum map idents.
 */
public class GM
{

  /**
   * An array with all 16 patch group names that are defined in GM.
   * Each patch group consists of 8 patches.
   * For patch group #i (1 <= i <= 16), GM_PATCH_GROUPS[i - 1]
   * contains its name. The patches of this group are patches
   * GM_PATCHES[j] with 16 * (i - 1) <= j < 16 * i.
   * @see #GM_PATCHES
   */
  public final static String[] GM_PATCH_GROUPS =
  {
    "Piano", // patches 1..8
    "Chromatic Percussion", // patches 9..16
    "Organ", // patches 17..24
    "Guitar", // patches 25..32
    "Bass", // patches 33..40
    "Solo Strings", // patches 41..48
    "Ensemble", // patches 49..56
    "Brass", // patches 57..64
    "Reed", // patches 65..72
    "Pipe", // patches 73..80
    "Synth Lead", // patches 81..88
    "Synth Pad", // patches 89..96
    "Synth Effects", // patches 97..104
    "Ethnic", // patches 105..112
    "Percussive", // patches 113..120
    "Sound Effects" // patches 121..128
  };

  /**
   * An array with all 128 patche names that are defined in GM.
   * The patches are structured into 16 patch groups with 8 patches
   * in each group.
   * @see GM_PATCH_GROUPS
   */
  public final static String[] GM_PATCHES =
  {
    // Piano
    "Acoustic Grand", "Bright Acoustic", "Electric Grand", "Honky-Tonk",
    "Electric Piano 1", "Electric Piano 2", "Harpsichord", "Clavinet",
    // Chromatic Percussion
    "Celesta", "Glockspiel", "Music Box", "Vibraphone",
    "Marimba", "Xylophone", "Tubular Bells", "Dulcimer",
    // Organ
    "Drawbar Organ", "Percussive Organ", "Rock Organ", "Church Organ",
    "Reed Organ", "Accordion", "Harmonica", "Tango Accordion",
    // Guitar
    "Nylon String Guitar", "Steel String Guitar", "Electric Jazz Guitar",
    "Electric Clean Guitar", "Electric Muted Guitar", "Overdriven Guitar",
    "Distortion Guitar", "Guitar Harmonics",
    // Bass
    "Acoustic Bass", "Electric Fingered Bass", "Electric Picked Bass",
    "Fretless Bass", "Slap Bass 1", "Slap Bass 2",
    "Synth Bass 1", "Synth Bass 2",
    // Solo Strings
    "Violin", "Viola", "Cello", "Contrabass",
    "Tremolo Strings", "Pizzicato Strings", "Orchestral Strings", "Timpani",
    // Ensemble
    "String Ensemble 1", "String Ensemble 2",
    "Synth Strings 1", "Synth Strings 2",
    "Choir Aahs", "Voice Oohs", "Synth Voice", "Orchestra Hit",
    // Brass
    "Trumpet", "Trombone", "Tuba", "Muted Trumpet",
    "French Horn", "Brass Section", "Synth Brass 1", "Synth Brass 2",
    // Reed
    "Soprano Sax", "Alto Sax", "Tenor Sax", "Baritone Sax",
    "Oboe", "English Horn", "Bassoon", "Clarinet",
    // Pipe
    "Piccolo", "Flute", "Recorder", "Pan Flute",
    "Blown Bottle", "Shakuhachi", "Whistle", "Ocarina",
    // Synth Lead
    "Square Lead", "Saw Lead", "Calliope Lead", "Chiffer Lead",
    "Charang Lead", "Voice Lead", "Fifths Lead", "Bass & Lead",
    // Synth Pad
    "New Age Pad", "Warm Pad", "Polysynth Pad", "Choir Pad",
    "Bowed Pad", "Metallic Pad", "Halo Pad", "Sweep Pad",
    // Synth Effects
    "Rain", "Soundtrack", "Crystal", "Atmosphere",
    "Brightness", "Goblins", "Echoes", "Sci-fi",
    // Ethnic
    "Sitar", "Banjo", "Shamisen", "Koto",
    "Kalimba", "Bag Pipe", "Fiddle", "Shanai",
    // Percussive
    "Tinle Bell", "Agogo", "Steel Drums", "Woodblock",
    "Taiko Drum", "Melodic Tom", "Synth Drum", "Reverse Cymbal",
    // Sound Effects
    "Guitar Fret Noise", "Breath Noise", "Seashore", "Bird Tweet",
    "Telephone Ring", "Helicopter", "Applause", "Gun Shot"
  };

  /**
   * An array with all drums sound names that are defined in GM.
   * This array represents the 47 drums sounds that are bound to
   * the keys with note numbers 35 through 81.
   */
  public final static String[] GM_DRUMS = // notes #35..81
  {
    "Accoustic Bass Drum",
    "Bass Drum 1",
    "Side Stick",
    "Acoustic Snare",
    "Hand Clap",
    "Electric Snare",
    "Low Floor Tom",
    "Closed Hi-Hat",
    "High Floor Tom",
    "Pedal Hi-Hat",
    "Low Tom",
    "Open Hi-Hat",
    "Low-Mid Tom",
    "Hi-Mid Tom",
    "Crash Cymbal 1",
    "High Tom",
    "Ride Cymbal 1",
    "Chinese Cymbal",
    "Ride Bell",
    "Tambourine",
    "Splash Cymbal",
    "Cowbell",
    "Crash Cymbal 2",
    "Vibraslap",
    "Ride Cymbal 2",
    "Hi Bongo",
    "Low Bongo",
    "Mute Hi Conga",
    "Open Hi Conga",
    "Low Conga",
    "High Timbale",
    "Low Timbale",
    "High Agogo",
    "Low Agogo",
    "Cabasa",
    "Maracas",
    "Short Whistle",
    "Long Whistle",
    "Short Guiro",
    "Long Guiro",
    "Claves",
    "Hi Wood Block",
    "Low Wood Block",
    "Mute Cuica",
    "Open Cuica",
    "Mute Triangle",
    "Open Triangle"
  };
}
