/*
 * $Source: /home/reuter/archive/project/ImageSound/IS/src/midi/ListElement.java.rca $
 * $Revision: 1.1 $
 * $Aliases: beta2 $
 * $Author: reuter $
 * $Date: Sat Jun 27 14:30:38 1998 $
 * $State: Experimental $
 */

/*
 * @(#)ListElement.java 1.00 98/04/25
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
 * This class implements a single element of a general purpose double linked
 * list as implemented by class List in the same package.
 */
public class ListElement
{
  private ListElement prev = null;
  private ListElement next = null;
  protected List list = null;

  protected ListElement() {}

  /**
   * Creates a new list element in the domain of the specified list.
   * @param list The list that will contain this list element.
   */
  ListElement(List list)
  {
    if (list == null)
      throw new NullPointerException("invalid list arg");
    else
      this.list = list;
  }

  /**
   * Returns the List that contains this ListElement as a member.
   * @return The List that contains this ListElement as a member.
   */
  public List getList()
  {
    return list;
  }

  /**
   * Declares this element as a member of the specified list.
   * @param list The List that will incorporate this element.
   * @exception IllegalStateException If this element is already a member
   *    of some list.
   * @exception NullPointerException If list is null.
   */
  void bindTo(List list)
  {
    list.checkListValidity();
    if ((this.list != null) || (prev != null) || (next != null))
      throw new IllegalStateException("invalid insertion element");
    this.list = list;
  }

  /**
   * Checks, if this ListElement is valid element of a valid list.
   * This implies:
   * <UL>
   * <LI>list is not null,
   * <LI>list is valid,
   * <LI>list cotains this element.
   * </UL>
   */
  private void checkElementValidity() throws IllegalStateException
  {
    if (list == null)
      throw new IllegalStateException("invalid list (null)");
    else if (!list.valid)
      throw new IllegalStateException("invalid list (!valid)");
  }

  /**
   * Returns the previous element is the list or null if there is no further
   * element.
   * @exception IllegalStateException If this ListElement is invalid,
   *    because it has explicitly been deleted.
   */
  public synchronized ListElement next()
  {
    checkElementValidity();
    return next;
  }

  /**
   * Returns the next element is the list or null if there is no further
   * element.
   * @exception IllegalStateException If this ListElement is marked invalid,
   *    because it has explicitly been deleted.
   */
  public synchronized ListElement prev()
  {
    checkElementValidity();
    return prev;
  }

  /**
   * Removes this list element from the list and marks it as invalid, so that
   * any further access to this list element will result in an
   * IllegalStateException.
   * @exception IllegalStateException If this ListElement is invalid,
   *    because it has explicitly been deleted.
   */
  public synchronized void delete()
  {
    checkElementValidity();
    if (prev != null)
      prev.next = next;
    else
      list.first = next;
    if (next != null)
      next.prev = prev;
    else
      list.last = prev;
    prev = null;
    next = null;
    list.size--;
    list = null;
  }

  /**
   * Inserts a new ListElement into the list after this element. The insertion
   * element must not already be a member of some list.
   * @param element The insertion element.
   * @return The inserted element.
   * @exception IllegalStateException If this ListElement is invalid,
   *    because it has explicitly been deleted, or if the insertion element
   *    is already a member of some list.
   */
  public synchronized ListElement insertAfter(ListElement element)
  {
    element.bindTo(list);
    element.prev = this;
    element.next = next;
    if (next != null)
      next.prev = element;
    else
      list.last = element;
    next = element;
    list.size++;
    return element;
  }

  /**
   * Inserts a new ListElement into the list before this element. The insertion
   * element must not already be a member of some list.
   * @param element The insertion element.
   * @return The inserted element.
   * @exception IllegalStateException If this ListElement is invalid,
   *    because it has explicitly been deleted, or if the insertion element
   *    is already a member of some list.
   */
  public synchronized ListElement insertBefore(ListElement element)
  {
    element.bindTo(list);
    element.prev = prev;
    element.next = this;
    if (prev != null)
      prev.next = element;
    else
      list.first = element;
    prev = element;
    list.size++;
    return element;
  }

  /**
   * Starting with this element, searches for next element in list order
   * that equals the given list element. In particular, this method envokes
   * element.equals(list_element), where list_element iterates through each
   * visited list element.<BR>
   * Note: For performance reasons in multi-threaded programs, this method
   * is synchronized neither on this list element nor on the list as a whole,
   * possibly resulting in an IllegalStateException (see below), but it
   * accesses list element data only through synchronized methods, so that
   * data integrity on the list data structure as a whole is ensured.
   * @param element Any non-null ListElement to be found.
   * @return The next element in list order that equals the given
   *    content, or null, if no such element is found.
   * @exception NullPointerException If element is null.
   * @exception IllegalStateException If this ListElement or one of the visited
   *    list elements is invalid, because it has explicitly been
   *    deleted (possibly by a concurrently running thread, in which case
   *    this operation may be retried).
   */
  public ListElement find(ListElement element)
  {
    if (element == null)
      throw new NullPointerException("element is null");
    ListElement match_element = this;
    while ((match_element != null) && !element.equals(match_element))
      match_element.next();
    return match_element;
  }

  /**
   * Starting with this element, searches for next element in reverse list
   * order that equals the given list element. In particular, this method
   * envokes element.equals(list_element), where list_element iterates
   * through each visited list element.<BR>
   * Note: For performance reasons in multi-threaded programs, this method
   * is synchronized neither on this list element nor on the list as a whole,
   * possibly resulting in an IllegalStateException (see below), but it
   * accesses list element data only through synchronized methods, so that
   * data integrity on the list data structure as a whole is ensured.
   * @param element Any non-null ListElement to be found.
   * @return The next element in reverse list order whose content equals the
   *    given content, or null, if no such element is found.
   * @exception NullPointerException If element is null.
   * @exception IllegalStateException If this ListElement or one of the visited
   *    list elements is invalid, because it has explicitly been
   *    deleted (possibly by a concurrently running thread, in which case
   *    this operation may be retried).
   */
  public ListElement findReverse(ListElement element)
  {
    if (element == null)
      throw new NullPointerException("element is null");
    ListElement match_element = this;
    while ((match_element != null) && !element.equals(match_element))
      match_element.prev();
    return match_element;
  }
}
