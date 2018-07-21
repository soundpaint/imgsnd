/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/midi/List.java.rca $
 * $Revision: 1.1 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Sat Jun 27 14:29:55 1998 $
 * $State: Experimental $
 */

/*
 * @(#)List.java 1.00 98/04/25
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
 * This class implements a general purpose double linked list.<BR>
 * [PENDING: Still to do (future work):<BR>
 *   public List clone();
 *   // However, a ListElement alone is not publically cloneable, as it is
 *   // always bound to a list.
 *
 *   public List split(ListElement split_point);
 *   // After this operation, split_point and all successive elements
 *   // belong to the new list (or clone each list element?).
 *
 *   public void append(List appendix);
 *   // After this operation, all list elements of List appendix will belong
 *   // to this list, so that appendix will either be an empty list or 
 *   // invalid (or clone each list element?).
 * ]
 */
public class List extends ListElement
{
  boolean valid = true; // false, if this ListElement explicitly deleted
  ListElement first = null;
  ListElement last = null;
  int size = 0;

  public List() {}

  void checkListValidity() throws IllegalStateException
  {
    if (!valid)
      throw new IllegalStateException("list validation error");
  }

  /**
   * Returns true, if there is no element in this list.
   * @return True, if there is no element in this list.
   */
  public synchronized boolean isEmpty()
  {
    return size == 0;
  }

  /**
   * Returns the size of the list, i.e. the number of its elements.
   * @return The size of the list.
   */
  public synchronized int size()
  {
    return size;
  }

  /**
   * Returns the first element is the list or null if the list is empty.
   * @exception IllegalStateException If this List is invalid,
   *    because it has explicitly been deleted.
   */
  public synchronized ListElement firstElement()
  {
    checkListValidity();
    return first;
  }

  /**
   * Returns the last element is the list or null if the list is empty.
   * @exception IllegalStateException If this List is invalid,
   *    because it has explicitly been deleted.
   */
  public synchronized ListElement lastElement()
  {
    checkListValidity();
    return last;
  }

  /**
   * Inserts a new element that will become the first element of the list.
   * @param insertion The insertion element.
   * @return The inserted element.
   * @exception IllegalStateException If this List is invalid,
   *    because it has explicitly been deleted, or if the insertion element
   *    is already a member of some list.
   */
  public synchronized ListElement insertHead(ListElement insertion)
  {
    checkListValidity();
    if (first == null) // list empty
      {
	insertion.bindTo(this);
	size++;
	first = insertion;
	last = insertion;
	return insertion;
      }
    else
      return first.insertBefore(insertion);
  }

  /**
   * Inserts a new element that will become the last element of the list.
   * @param insertion The insertion element.
   * @return The inserted element.
   * @exception IllegalStateException If this List is invalid,
   *    because it has explicitly been deleted, or if the insertion element
   *    is already a member of some list.
   */
  public synchronized ListElement insertTail(ListElement insertion)
  {
    checkListValidity();
    if (first == null) // list empty
      return insertHead(insertion);
    else
      return last.insertAfter(insertion);
  }

  /**
   * Searches for first element whose content equals the given content. In
   * particular, this method envokes content.equals(list_element_content),
   * where list_element_content is the content of each visited list
   * element.<BR>
   * Note: For performance reasons in multi-threaded programs, this method
   * is not synchronized on the list as a whole, possibly resulting in an
   * IllegalStateException (see below), but it accesses list element data
   * only through synchronized methods, so that data integrity on the list
   * data structure as a whole is ensured.
   * @param content Any object (including null) to be found.
   * @return The first element whose content equals the given content, or
   *    null, if no such element is found.
   * @exception IllegalStateException If one of the visited list elements
   *    is invalid, because it has explicitly been deleted (possibly
   *    by a concurrently running thread, in which case this operation may
   *    be retried).
   */
  public ListElement find(ListElement element)
  {
    checkListValidity();
    return (first != null) ? first.find(element) : null;
  }

  /**
   * Searches for last element whose content equals the given content. In
   * particular, this method envokes content.equals(list_element_content),
   * where list_element_content is the content of each visited list
   * element.<BR>
   * Note: For performance reasons in multi-threaded programs, this method
   * is not synchronized on the list as a whole, possibly resulting in an
   * IllegalStateException (see below), but it accesses list element data
   * only through synchronized methods, so that data integrity on the list
   * data structure as a whole is ensured.
   * @param content Any object (including null) to be found.
   * @return The last element whose content equals the given content, or
   *    null, if no such element is found.
   * @exception IllegalStateException If one of the visited list elements
   *    is invalid, because it has explicitly been deleted (possibly
   *    by a concurrently running thread, in which case this operation may
   *    be retried).
   */
  public ListElement findReverse(ListElement element)
  {
    checkListValidity();
    return (last != null) ? last.findReverse(element) : null;
  }

  /**
   * Marks this list and all of its elements as invalid, so that any further
   * access to this list or any of its elements will result in an
   * IllegalStateException.
   * @exception IllegalStateException If this List is invalid,
   *    because it has explicitly been deleted.
   */
  public synchronized void delete()
  {
    checkListValidity();
    while (last != null)
      last.delete();
    valid = false;
  }
}
