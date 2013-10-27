package tcss558.homework2;

/*
 * Spring 2013 TCSS 558 - Applied Distributed Computing
 * Institute of Technology, UW Tacoma
 * Written by Daniel M. Zimmerman
 * 
 * THIS IS SOLUTION CODE - if you are a student, delete this immediately.
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * WordList encapsulates a word list for the spelling server. It includes a 
 * method to return the subset of the word list that is "close" to a 
 * specified word, as defined in the assignment, plus other utility methods.
 *
 * @author Daniel M. Zimmerman
 * @version Spring 2013
 */
public class WordList
{
  // Instance Fields
  
  /**
   * The TreeSet containing the word list. We chose a TreeSet because it
   * sorts the entries and allows for relatively quick searching.
   */
  private final Set<String> my_words = new TreeSet<String>();
  
  // Constructor
  
  /**
   * Creates a WordList from the file with the specified name.
   *
   * @param the_file The name of the file.
   * @exception IOException if there is a problem reading the list from the 
   *  file.
   */
  public WordList(final String the_file) throws IOException
  {
    // create a reader for the file
    
    final FileReader file_reader = new FileReader(the_file);
    final BufferedReader buffered_reader = new BufferedReader(file_reader);
    
    // read the lines
    
    String line;
    
    do
    {
      line = buffered_reader.readLine();
      
      if (line != null && line.length() != 0) 
      {
        // it's a word (not a blank line)
          
        my_words.add(line);
      }
    }
    while (line != null);
    
    buffered_reader.close();
  }
  
  /**
   * Constructs a WordList from the specified collection of Strings.
   * 
   * @param the_words The words.
   */
  protected WordList(final Collection<String> the_words)
  {
    if (the_words != null)
    {
      my_words.addAll(the_words);
    }
  }
  
  // Instance Methods
  
  /**
   * Checks for a word in the list.
   * 
   * @param the_word The word to search for.
   * @return true if the specified word is in the word list, false 
   *  otherwise.
   */
  public synchronized boolean isInList(final String the_word)
  {
    return my_words.contains(the_word);
  }

  /**
   * Checks for words "close to" the specified word in the list.
   * 
   * @param the_word The word to check for closeness.
   * @return a sorted set containing all words in the word list that
   *  are "close" to the specified word, as described in the assignment. 
   */
  public synchronized SortedSet<String> getCloseWords(final String the_word)
  {
    final SortedSet<String> close_set = new TreeSet<String>();
    
    for (String s : my_words)
    {
      if (WordDistance.getDistance(the_word, s) <=
          WordDistance.DISTANCE_THRESHOLD)
      {
        // the word is close enough
        close_set.add(s);
      }
    }
    
    return close_set;
  }
  
  /**
   * Add a word to the list.
   * 
   * @param the_word The word to add.
   * @return true if the word is added, false if not.
   */
  public synchronized boolean add(final String the_word)
  {
    return my_words.add(the_word);
  }
  
  /**
   * Remove a word from the list.
   * 
   * @param the_word The word to remove.
   * @return true if the word is removed, false if not.
   */
  public synchronized boolean remove(final String the_word)
  {
    return my_words.remove(the_word);
  }
}
