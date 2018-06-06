package bloomfilter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JFileChooser;

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

/**
 *
 * @author Maciej Modzelewski <https://maciejmodzelewski.com/contact/>
 * @version 
 */
class HashTester {
    private static final int INPUT_BUFFER_SIZE = 10000000;
    private BufferedReader reader;
    private BufferedWriter writer;
    
    public static void main(String[] args) {
        BufferedReader inputData;
        BufferedWriter outputData;
        HashTester tester = null;
        String className = "bloomfilter.StringHashFunctions";
        String[] methods = new String[]{"simpleHash", "crossHash", 
            "indexValueHash", "primeHash"};
        int size = 728570000;
        JFileChooser inputFileChooser = new JFileChooser();
        int inputResult = inputFileChooser.showOpenDialog(inputFileChooser);
        JFileChooser outputFileChooser = new JFileChooser();
        int outputResult = outputFileChooser.showOpenDialog(outputFileChooser);
        boolean append = false;
        try {
            inputData = new BufferedReader(new FileReader(inputFileChooser.getSelectedFile()));
            outputData = new BufferedWriter(new FileWriter(outputFileChooser.getSelectedFile(), append));
            
            tester = new HashTester(inputData, outputData);
            tester.hashStrings(className, methods, size);
            tester.setInput();
            tester.setOutput(true);
            for (size = 100000; size < 20000000; size *=10) {
                tester.testHashFunctions(className, methods, size);
            }
            
            tester.close();
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | 
                IllegalAccessException | IllegalArgumentException | 
                InvocationTargetException ex) {
            System.err.println(ex.toString());
//            ex.printStackTrace();
        } finally {
            try
            {
                tester.close();
            }
            catch (IOException ex)
            {
                System.err.println(ex.toString());
            }
        }
    }
    
    HashTester(BufferedReader input, BufferedWriter output) {
        reader = input;
        writer = output;
    }
    
    private void hashStrings(String className, String[] hashFunctions, int size) 
            throws IOException, ClassNotFoundException, NoSuchMethodException, 
            IllegalAccessException, IllegalArgumentException, 
            InvocationTargetException {
        String line;
        int[] hashes;
        String header = "";
        for (String hashFunction : hashFunctions) {
            header += "\"" + hashFunction + "\",";
        }
        writer.write(header.substring(0, header.length()-1));
        writer.newLine();
        reader.mark(HashTester.INPUT_BUFFER_SIZE);
        line = reader.readLine();
        while (line != null) {
            hashes = HashTester.hashString(className, hashFunctions, size, line);
            for (int i = 0; i < hashes.length; i++) {
                writer.write(String.valueOf(hashes[i]));
                if (i < hashes.length - 1) {
                    writer.write(',');
                }
            }
            writer.newLine();
            line = reader.readLine();
        }
        writer.flush();
        reader.reset();
    }
    
    private void testHashFunctions(String className, String[] hashFunctions, 
            int size) throws IOException, ClassNotFoundException, 
            NoSuchMethodException, IllegalAccessException, 
            IllegalArgumentException, InvocationTargetException {
        List<Set<Integer>> uniqueHashes;
        int[] hashes;
        String line;
        uniqueHashes = new ArrayList<>();
        for (String hashFunction : hashFunctions) {
            uniqueHashes.add(new HashSet<>());
        }
        reader.mark(HashTester.INPUT_BUFFER_SIZE);
        while ((line = reader.readLine()) != null) {
            hashes = HashTester.hashString(className, hashFunctions, size, line);
            for (int i = 0; i < hashes.length; i++) {
                uniqueHashes.get(i).add(hashes[i]);
            }
        }
        for (int i = 0; i < uniqueHashes.size(); i++) {
            writer.write(String.valueOf(uniqueHashes.get(i).size()));
            if (i < uniqueHashes.size() - 1) {
                writer.write(',');
            }
        }
        writer.newLine();
        writer.flush();
        reader.reset();
    }
    
    private void close() throws IOException {
        reader.close();
        writer.close();
    }
    
    private void setInput() throws FileNotFoundException {
        JFileChooser inputFileChooser = new JFileChooser();
        int inputResult = inputFileChooser.showOpenDialog(inputFileChooser);
        reader = new BufferedReader(new FileReader(inputFileChooser.getSelectedFile()));
    }
    
    private void setInput(BufferedReader input) {
        reader = input;
    }
    
    private void setOutput(boolean append) throws IOException {
        JFileChooser outputFileChooser = new JFileChooser();
        int outputResult = outputFileChooser.showOpenDialog(outputFileChooser);
        writer = new BufferedWriter(new FileWriter(outputFileChooser.getSelectedFile(),append));
    }
    
    private void setOutput(BufferedWriter output) {
        writer = output;
    }
    
    private static int[] hashString(String className, String[] hashFunctions, 
            int size, String s) throws ClassNotFoundException, 
            NoSuchMethodException, IllegalAccessException, 
            IllegalArgumentException, InvocationTargetException {
        Class<?> strHashFunc = Class.forName(className);
        Class<?>[] methodParams = {String.class, int.class};
        Method hashFunc;
        int[] hashes;
        hashes = new int[hashFunctions.length];
        for (int i = 0; i < hashFunctions.length; i++) {
            hashFunc = strHashFunc.getDeclaredMethod(hashFunctions[i], methodParams);
            hashes[i] = (int) hashFunc.invoke(hashFunctions[i], s, size);
        }
        return hashes;
    }
}
