package tcss558.homework2;

/*
 * Spring 2013 TCSS 558 - Applied Distributed Computing
 * Institute of Technology, UW Tacoma
 * Written by Daniel M. Zimmerman
 * 
 * DO NOT MODIFY THIS CLASS EXCEPT FOR REFORMATTING OF BRACES!
 */

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.SortedSet;

/**
 * The interface to a remote object that checks spelling.
 * 
 * @author Daniel M. Zimmerman
 * @version Spring 2013
 */
public interface RemoteSpelling extends Remote
{
  /**
   * Check the spelling of the specified word.
   * 
   * @param the_word The word to check.
   * @return a sorted set of suggestions (which may be empty) if the word
   * is spelled incorrectly, or null if the word is spelled correctly.
   * @throws RemoteException if there is a problem completing the method call.
   */
  SortedSet<String> check(String the_word) throws RemoteException;
  
  /**
   * Add the specified word to the word list. If the word is already in
   * the word list, this method has no effect.
   * 
   * @param the_word The word to add.
   * @throws RemoteException if there is a problem completing the method call.
   */
  void add(String the_word) throws RemoteException;
  
  /**
   * Remove the specified word from the word list. If the word is not
   * in the word list, this method has no effect.
   * 
   * @param the_word The word to remove.
   * @throws RemoteException if there is a problem completing the method call.
   */
  void remove(String the_word) throws RemoteException;
}
