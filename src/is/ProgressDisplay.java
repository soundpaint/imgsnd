/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/is/ProgressDisplay.java.rca $
 * $Revision: 1.1 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Sat Jun 27 14:56:52 1998 $
 * $State: Experimental $
 */

/*
 * @(#)Synth.java 1.00 98/02/22
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

/**
 * This abstract Thread class is used to show the progress of any action
 * that may cover a greater amount of time.
 */
public class ProgressDisplay extends Thread
{

  private ProgressDisplayModel model;
  private long millis;
  private boolean active;

  /**
   * Creates a new ProgressDisplay Thread with the given time interval
   * as display refresh time. Use method run() to actually start this
   * thread.
   * @param model The progress display data model.
   * @param millis The time to wait until the next refresh of the progress
   *    display.
   */
  public ProgressDisplay(ProgressDisplayModel model, long millis)
  {
    super();
    this.model = model;
    this.millis = millis;
    active = false;
  }

  /**
   * Creates a new ProgressDisplay Thread with a time interval of 1000ms
   * as display refresh time. Use method run() to actually start this
   * thread.
   * @param model The progress display data model.
   */
  public ProgressDisplay(ProgressDisplayModel model)
  {
    this(model, 1000);
  }

  /**
   * The display loop of this Thread. Repeatedly refreshes the display with
   * the given time interval.
   */
  public void run()
  {
    active = true;
    while (active)
      {
	model.refreshDisplay();
	try
	  {
	    sleep(millis);
	  }
	catch (InterruptedException e)
	  {
	    System.out.println(e); System.out.flush();
	    active = false;
	  }
      }
  }
}
