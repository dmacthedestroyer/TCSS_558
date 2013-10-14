package tcss558.homework1;
/*
 * Spring 2013 TCSS 558 - Applied Distributed Computing
 * Institute of Technology, UW Tacoma
 * Written by Daniel M. Zimmerman
 * 
 * DO NOT MODIFY THIS CLASS EXCEPT FOR REFORMATTING OF BRACES! The results
 * it generates are used when grading your code, and changes to the class
 * may change its results.
 */

/**
 * WordDistance calculates the distance between two words,
 * using a very simple recursive algorithm. 
 *
 * @author Daniel M. Zimmerman
 * @version Spring 2013
 */
public final class WordDistance
{
  /**
   * The distance threshold. When two words are found to be more than this
   * far apart, the recursion stops and the distance is returned 
   * immediately. 
   */
  public static final int DISTANCE_THRESHOLD = 1;
  
  /**
   * Private constructor to prevent instantiation of this class.
   */
  private WordDistance()
  {
    // do nothing
  }
  
  /**
   * Calculates the distance between two words. If the distance exceeds
   * DISTANCE_THRESHOLD, some number greater than DISTANCE_THRESHOLD (but
   * not necessarily the actual distance) is returned. If either word is
   * null, Integer.MAX_VALUE is returned.<p>
   * Two words have a distance of 0 if they are identical, a distance of 1-2
   * if they are "close matches", and a distance greater than or equal to 3 
   * if they are not close at all.
   *
   * @param word_1 The first word.
   * @param word_2 The second word.
   * @return The distance between the two words. 
   */
  //@ ensures \result >= 0;
  public static int getDistance(final String word_1, final String word_2)
  {
    int result = Integer.MAX_VALUE;
    
    if ((word_1 != null) && (word_2 != null))
    {
      result = calculateDistance(word_1.toCharArray(), 0, 
                                 word_2.toCharArray(), 0, 0);
    }
    
    return result;
  }
  
  /**
   * The function that actually does the work in calculating the
   * distance between two words. If the distance exceeds DISTANCE_THRESHOLD,
   * some number greater than DISTANCE_THRESHOLD (but not necessarily the
   * actual distance) is returned. 
   *
   * @param word_1 The first word, as a character array.
   * @param offset_1 The current offset into the first word.
   * @param word_2 The second word, as a character array.
   * @param offset_2 The current offset into the second word.
   * @param the_distance The current distance.
   * @return The distance between the two words.
   */
  //@ requires (0 <= offset_1) && (offset_1 <= word_1.length);
  //@ requires (0 <= offset_2) && (offset_2 <= word_2.length);
  //@ requires the_distance >= 0;
  //@ ensures \result >= the_distance;
  private static int calculateDistance(final char[] word_1, final int offset_1, 
                                       final char[] word_2, final int offset_2, 
                                       final int the_distance)
  {
    int result = Integer.MAX_VALUE;
    
    if (the_distance > DISTANCE_THRESHOLD)
    {
      // we've exceeded the distance threshold, so stop recursing
      
      result = the_distance;
    }
    else if ((offset_1 < word_1.length) && (offset_2 < word_2.length))
    {
      // both strings have characters left in them
      
      if (word_1[offset_1] == word_2[offset_2])
      {
        // current characters are equal, don't increment the distance
        
        result = calculateDistance(word_1, offset_1 + 1, 
                                   word_2, offset_2 + 1, the_distance);
      }
      else
      {
        // try advancing a character in one word, then the other, then both, 
        // and return the minimum resulting distance
        
        final int distance1 = calculateDistance(word_1, offset_1 + 1, 
                                                word_2, offset_2, the_distance + 1);
        final int distance2 = calculateDistance(word_1, offset_1,
                                                word_2, offset_2 + 1, the_distance + 1);
        final int distance3 = calculateDistance(word_1, offset_1 + 1,
                                                word_2, offset_2 + 1, the_distance + 1);
        result = the_distance + Math.min(Math.min(distance1, distance2), distance3);
      }
    }
    else if (offset_1 == word_1.length)
    {
      // no more characters in word1, return number of characters in word2
      
      result = the_distance + (word_2.length - offset_2);
    }
    else if (offset_2 == word_2.length)
    {
      // no more characters in word2, return number of characters in word1
      
      result = the_distance + (word_1.length - offset_1);
    }
    
    return result;
  }
}
