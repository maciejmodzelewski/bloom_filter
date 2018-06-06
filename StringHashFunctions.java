/*
 * Copyright (C) 2018 Maciej Modzelewski <https://maciejmodzelewski.com/contact/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package bloomfilter;

/**
 * This class provides a set of hashing functions for <code>String</code>.
 * @author Maciej Modzelewski <https://maciejmodzelewski.com/contact/>
 * @version 0.1 (14 April 2018)
 */
public class StringHashFunctions {
    // DO NOT change this prime number
    // if you want to keep compatibility
    // with previously done hashes
    private static final int PRIME_NUMBER = 99989;
    
    /**
     * Returns a hash value of the specified <code>String</code> element.
     * @param s the element whose hash is to be generated
     * @param size the maximum size of the hash
     * @return the hash of the <code>String</code> element
     */
    public static int indexValueHash(String s, int size) {
        int sum = 0;
        char[] sChar = s.toCharArray();
        
        for (int i = 0; i < sChar.length; i++) {
            if (sChar.length == 1) {
                sum = sChar[i] ^ 1;
            } else {
                sum += (~i << sChar[i]) ^ size ^ 17;
            }
        }
        
        if (sum < 0) {
            sum >>>= 1;
        }
        
        return sum % size;
    }
    
    /**
     * Returns a hash value of the specified <code>String</code> element.
     * @param s the element whose hash is to be generated
     * @param size the maximum size of the hash
     * @return the hash of the <code>String</code> element
     */
    public static int crossHash(String s, int size) {
        int sum = 0;
        char[] sChar = s.toCharArray();
        
        for (int i = 0; i < sChar.length; i++) {
            if (i == 0 || i == sChar.length - 1 && sChar.length != 2) {
                sum += sChar[i] * ~3;
            } else {
                sum += (sChar[i] << sChar[sChar.length - i]) ^ ~size;
            } 
        }
        
        if (sum < 0) {
            sum >>>= 1;
        }
        
        return sum % size;
    }
    
    /**
     * Returns a hash value of the specified <code>String</code> element.
     * @param s the element whose hash is to be generated
     * @param size the maximum size of the hash
     * @return the hash of the <code>String</code> element 
     */
    public static int primeHash(String s, int size) {
        int sum = 0;
        char[] sChar = s.toCharArray();
        
        for (int i = 0; i < sChar.length; i++) {
            sum ^= (sChar[i] * PRIME_NUMBER << i);
        }
        
        if (sum < 0) {
            sum >>>= 1;
        }
        
        return sum % size;
    }
    
    /**
     * Returns a hash value of the specified <code>String</code> element.
     * @param s the element whose hash is to be generated
     * @param size the maximum size of the hash
     * @return the hash of the <code>String</code> element 
     */
    public static int simpleHash(String s, int size) {
        int hash = s.hashCode();
        
        if (hash < 0) {
            hash >>>= 1;
        }
        
        return hash % size;
    }
}