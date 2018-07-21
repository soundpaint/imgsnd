/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/is/PNMReader.java.rca $
 * $Revision: 1.1 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Sat Jun 27 14:51:00 1998 $
 * $State: Experimental $
 */

/*
 * @(#)PNMReader.java 1.00 98/02/22
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.io.IOException;

/**
 * This class reads a PNM (portable anymap) image stream and outputs the
 * image data as a two-dimensional array of true-color integers.
 */
public class PNMReader
{
  private String name; // name of image source
  private StreamTokenizer tokenizer; // tokenized image input data
  private int magic; // magic number "PX" with (X == 3) or (X == 6)
  private int xsize, ysize, maxcolor; // image properties
  private int[][] pixmap; // image contents
  private PrintWriter out; // standard output for user info

  private PNMReader() {}

  /**
   * Creates a new PNMReader from the given InputStream.
   * @param name The name of the image source (for error messages).
   * @param in The original source of the image as InputStream.
   * @param out The standard output device. This is used to print out
   *    information for the user while reading the image.
   * @exception IOException If an I/O error occurs.
   * @exception IllegalStateException If a file format error occurs.
   */
  public PNMReader(String name, InputStream in, PrintWriter out)
       throws IOException
  {
    this.name = name;
    this.out = out;
    init_tokenizer(in);
    read_image();
  }

  /**
   * Initializes the stream tokenizer.
   * @param in The InputStream that serves as source for the tokenizer.
   * @exception IOException If an I/O error occurs.
   */
  private void init_tokenizer(InputStream in) throws IOException
  {
    tokenizer =
      new StreamTokenizer(new BufferedReader(new InputStreamReader(in)));
    tokenizer.resetSyntax();
    tokenizer.wordChars((int)'A', (int)'Z');
    tokenizer.wordChars((int)'a', (int)'z');
    tokenizer.wordChars((int)'0', (int)'9');
    tokenizer.whitespaceChars((int)'\u0000', (int)'\u0020');
    // tokenizer.ordinaryChar((int)'\\');
    tokenizer.commentChar((int)'#');
    // tokenizer.quoteChar((int)'\'');
    tokenizer.parseNumbers();
    tokenizer.eolIsSignificant(false);
    // tokenizer.slashStarComments(true);
    // tokenizer.slashSlashComments(true);
    tokenizer.lowerCaseMode(false);
  }

  /**
   * Throws an IllegalStateException with the specified message and the
   * line where the error occurred.
   * @param msg The error message to be reported.
   * @exception IllegalStateException Always.
   */
  private void badFileFormat(String msg)
  {
    throw new IllegalStateException("PNM Image " + name + ", line " +
				    tokenizer.lineno() + ": " +
				    "Bad File Format: " + msg);
  }

  /**
   * Actually reads data via the tokenizer to create a memory image of the
   * original PNM stream.
   * @exception IOException If an I/O error occurs.
   * @exception IllegalStateException If a file format error occurs.
   */
  private void read_image() throws IOException, IllegalStateException
  {
    int ttype;
    magic = 0;
    ttype = tokenizer.nextToken();
    if (ttype == tokenizer.TT_WORD)
      if (tokenizer.sval.equals("P1"))
	magic = 1;
      else if (tokenizer.sval.equals("P2"))
	magic = 2;
      else if (tokenizer.sval.equals("P3"))
	magic = 3;
      else if (tokenizer.sval.equals("P4"))
	magic = 4;
      else if (tokenizer.sval.equals("P5"))
	magic = 5;
      else if (tokenizer.sval.equals("P6"))
	magic = 6;
    if (magic == 0)
      badFileFormat("Magic number (\"P1\" .. \"P6\") " +
		    "expected; found: " + tokenizer.sval);
    out.print("Magic P" + magic + " (portable ");
    switch (magic)
      {
      case 1:
      case 4:
	out.print("bitmap pbm");
	break;
      case 2:
      case 5:
	out.print("graymap pgm");
	break;
      case 3:
      case 6:
	out.print("pixmap ppm");
	break;
      }
    if (magic <= 3)
      out.print(" (ascii)");
    else
      out.print(" (raw)");
    out.println(")");
    ttype = tokenizer.nextToken();
    if (ttype != tokenizer.TT_NUMBER)
      badFileFormat("int (image x-size) expected");
    xsize = (int)tokenizer.nval;
    if ((xsize < 0) || (xsize > 65535))
      badFileFormat("x-size out of range [0..65535]");
    out.println("image xsize : " + xsize);
    ttype = tokenizer.nextToken();
    if (ttype != tokenizer.TT_NUMBER)
      badFileFormat("int (image y-size) expected");
    ysize = (int)tokenizer.nval;
    if ((ysize < 0) || (ysize > 65535))
      badFileFormat("y-size out of range [0..65535]");
    out.println("image ysize : " + ysize);
    if ((magic == 1) || (magic == 4))
      maxcolor = 1;
    else
      {
	ttype = tokenizer.nextToken();
	if (ttype != tokenizer.TT_NUMBER)
	  badFileFormat("int (max color) expected");
	maxcolor = (int)tokenizer.nval;
	if ((maxcolor < 0) || (maxcolor > 255))
	  badFileFormat("max color out of range [0..255]");
      }
    out.println("max color   : " + maxcolor);
    out.flush();
    pixmap = new int[ysize][xsize];
    switch (magic)
      {
      case 1:
	parseP1();
	break;
      case 2:
	parseP2();
	break;
      case 3:
	parseP3();
	break;
      case 4:
	parseP4();
	break;
      case 5:
	parseP5();
	break;
      case 6:
	parseP6();
	break;
      }
    ttype = tokenizer.nextToken();
    if (ttype != tokenizer.TT_EOF)
      out.println("WARNING: EOF expected (ignored)");
  }

  private void parseP1() throws IOException
  {
    int ttype;
    for (int y = 0; y < ysize; y++)
      for (int x = 0; x < xsize; x++)
	{
	  ttype = tokenizer.nextToken();
	  if (ttype != tokenizer.TT_NUMBER)
	    badFileFormat("int (pixel value) expected");
	  int pixel = (int)tokenizer.nval;
	  if ((pixel < 0) || (pixel > maxcolor))
	    badFileFormat("pixel out of range [0.." + maxcolor + "]");
	  pixel = (pixel == 0) ? 0xff : 0x00;
	  pixmap[y][x] = (pixel << 16) | (pixel << 8) | pixel;
	}
  }

  private void parseP2() throws IOException
  {
    int ttype;
    for (int y = 0; y < ysize; y++)
      for (int x = 0; x < xsize; x++)
	{
	  ttype = tokenizer.nextToken();
	  if (ttype != tokenizer.TT_NUMBER)
	    badFileFormat("int (pixel value) expected");
	  int pixel = (int)tokenizer.nval;
	  if ((pixel < 0) || (pixel > maxcolor))
	    badFileFormat("pixel out of range [0.." + maxcolor + "]");
	  pixmap[y][x] = (pixel << 16) | (pixel << 8) | pixel;
	}
  }

  private void parseP3() throws IOException
  {
    int ttype;
    for (int y = 0; y < ysize; y++)
      for (int x = 0; x < xsize; x++)
	{
	  ttype = tokenizer.nextToken();
	  if (ttype != tokenizer.TT_NUMBER)
	    badFileFormat("int (pixel value) expected");
	  int pixel_red = (int)tokenizer.nval;
	  if ((pixel_red < 0) || (pixel_red > maxcolor))
	    badFileFormat("pixel (red) out of range [0.." + maxcolor + "]");
	  ttype = tokenizer.nextToken();
	  if (ttype != tokenizer.TT_NUMBER)
	    badFileFormat("int (pixel value) expected");
	  int pixel_green = (int)tokenizer.nval;
	  if ((pixel_green < 0) || (pixel_green > maxcolor))
	    badFileFormat("pixel (green) out of range [0.." + maxcolor + "]");
	  ttype = tokenizer.nextToken();
	  if (ttype != tokenizer.TT_NUMBER)
	    badFileFormat("int (pixel value) expected");
	  int pixel_blue = (int)tokenizer.nval;
	  if ((pixel_blue < 0) || (pixel_blue > maxcolor))
	    badFileFormat("pixel (blue) out of range [0.." + maxcolor + "]");
	  pixmap[y][x] = (pixel_red << 16) | (pixel_green << 8) | pixel_blue;
	}
  }

  private void parseP4() throws IOException
  {
    int ttype;
    tokenizer.resetSyntax();
    tokenizer.eolIsSignificant(false);
    tokenizer.slashStarComments(false);
    tokenizer.slashSlashComments(false);
    tokenizer.lowerCaseMode(false);
    int pixelbyte = 0x0;
    int whitespace = tokenizer.nextToken();
    for (int y = 0; y < ysize; y++)
      {
	int bitmask = 0x0;
	for (int x = 0; x < xsize; x++)
	  {
	    bitmask >>>= 1;
	    if (bitmask == 0x0)
	      {
		bitmask = 0x80;
		pixelbyte = tokenizer.nextToken();
	      }
	    int pixel = pixelbyte & bitmask;
	    pixel = (pixel == 0) ? 0xff : 0x00;
	    pixmap[y][x] = (pixel << 16) | (pixel << 8) | pixel;
	  }
      }
  }

  private void parseP5() throws IOException
  {
    int ttype;
    tokenizer.resetSyntax();
    tokenizer.eolIsSignificant(false);
    tokenizer.slashStarComments(false);
    tokenizer.slashSlashComments(false);
    tokenizer.lowerCaseMode(false);
    int whitespace = tokenizer.nextToken();
    for (int y = 0; y < ysize; y++)
      for (int x = 0; x < xsize; x++)
	{
	  ttype = tokenizer.nextToken();
	  int pixel = ttype;
	  if ((pixel < 0) || (pixel > maxcolor))
	    badFileFormat("pixel out of range [0.." + maxcolor + "]");
	  pixmap[y][x] = (pixel << 16) | (pixel << 8) | pixel;
	}
  }

  private void parseP6() throws IOException
  {
    int ttype;
    tokenizer.resetSyntax();
    tokenizer.eolIsSignificant(false);
    tokenizer.slashStarComments(false);
    tokenizer.slashSlashComments(false);
    tokenizer.lowerCaseMode(false);
    int whitespace = tokenizer.nextToken();
    for (int y = 0; y < ysize; y++)
      for (int x = 0; x < xsize; x++)
	{
	  ttype = tokenizer.nextToken();
	  int pixel_red = ttype;
	  if ((pixel_red < 0) || (pixel_red > maxcolor))
	    badFileFormat("pixel (red) out of range [0.." + maxcolor + "]");
	  ttype = tokenizer.nextToken();
	  int pixel_green = ttype;
	  if ((pixel_green < 0) || (pixel_green > maxcolor))
	    badFileFormat("pixel (green) out of range [0.." + maxcolor + "]");
	  ttype = tokenizer.nextToken();
	  int pixel_blue = ttype;
	  if ((pixel_blue < 0) || (pixel_blue > maxcolor))
	    badFileFormat("pixel (blue) out of range [0.." + maxcolor + "]");
	  pixmap[y][x] = (pixel_red << 16) | (pixel_green << 8) | pixel_blue;
	}
  }

  /**
   * Returns the x size of the image.
   * @return The x size of the image.
   */
  public int getXSize() { return xsize; }

  /**
   * Returns the y size of the image.
   * @return The y size of the image.
   */
  public int getYSize() { return ysize; }

  /**
   * Returns the pixel at position (x, y).
   * @param x The x coordinate of the pixel.
   * @param y The y coordinate of the pixel.
   * @return The pixel at position (x, y) as an integer value. The uppermost
   *    byte of this integer is unused; the next lower byte contains the
   *    red partial, followed by a a byte with the green partial; the lowermost
   *    byte contains the blue partial.
   */
  public int getPixel(int x, int y)
  {
    return pixmap[y][x];
  }

  private final static double gray_scale = 1.0 / (3.0 * 255.0);

  /**
   * Returns the pixel at position (x, y) as gray scale value.
   * @param x The x coordinate of the pixel.
   * @param y The y coordinate of the pixel.
   * @return The pixel at position (x, y) as a float value (0.0 .. 1.0) that
   *    represents the gray scale of the pixel.
   */
  public double getGrayPixel(int x, int y)
  {
    int pixel = pixmap[y][x];
    return
      (((pixel >> 16) & 0xff) + ((pixel >> 8) & 0xff) + (pixel & 0xff)) *
      gray_scale;
  }

}
