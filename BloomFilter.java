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
 * This class is a simple implementation of a Bloom filter to test whether an 
 * element is a member of a set.
 * @author Maciej Modzelewski <https://maciejmodzelewski.com/contact/>
 * @version 0.1 (14 April 2018)
 */
public class BloomFilter {
    private final int[] bitArray;
    private final Hasher hasher;

    /**
     * Constructs a new <code>BloomFilter</code> with the specified bit array.
     * @param aBitArray the bit array to be used by this filter
     */
    public BloomFilter(int[] aBitArray) {
        bitArray = aBitArray;
        hasher = new Hasher();
    }
    
    private class Hasher {
        private static final int NUMBER_OF_HASH_FUNCTIONS = 3;
        private final int[] hashes;
        
        private Hasher() {
            hashes = new int[NUMBER_OF_HASH_FUNCTIONS];
        }
        
        private int[] hashString(String aKey, int size) {
            for (int i = 0; i < hashes.length; i++) {
                int hashFunction = i;
                int hash = 0;
                
                switch (hashFunction) {
                    case 0: hash = StringHashFunctions.crossHash(aKey, size);
                            break;
                    case 1: hash = StringHashFunctions.indexValueHash(aKey, size);
                            break;
                    case 2: hash = StringHashFunctions.primeHash(aKey, size);
                            break;
                    default: break;
                }
                
                hashes[i] = hash;
            }
            
            return hashes;
        }
    }
    
    /**
     * Adds the specified element to the filter.
     * @param aKey the element to be added to this filter
     */
    public void addKey(String aKey) {
        int size = bitArray.length;
        int[] indexes = hasher.hashString(aKey, size);
        for (int index : indexes) {
            bitArray[index] = 1;
        }
    }
    
    /**
     * Adds the element with the specified keys to the filter.
     * @param hashes an array of three hashes that are keys for the added 
     * element
     */
    public void addKey(int[] hashes) {
        int size = bitArray.length;
        int[] indexes = hashes;
        for (int index : indexes) {
            bitArray[index] = 1;
        }
    }
    
    /**
     * Returns <code>true</code> if this filter contains the specified element.
     * @param aKey the element whose presence in this filter is to be tested
     * @return <code>true</code> if this filter contains the specified element
     */
    public boolean findKey(String aKey) {
        int size = bitArray.length;
        int[] indexes = hasher.hashString(aKey, size);
        for (int index : indexes) {
            if (bitArray[index] == 0) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns <code>true</code> if this filter contains the element with 
     * the specified keys.
     * @param hashes an array of three hashes that are keys whose presence 
     * in this filter is to be tested
     * @return <code>true</code> if this filter contains all specified keys
     */
    public boolean findKey(int[] hashes) {
        int size = bitArray.length;
        int[] indexes = hashes;
        for (int index : indexes) {
            if (bitArray[index] == 0) {
                return false;
            }
        }
        return true;
    }
}