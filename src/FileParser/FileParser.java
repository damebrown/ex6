package FileParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * the class is in charge of parsing the file, and verify IO errors
 */
public class FileParser {

    /**
     * the method parse the given file lines, and throws exception in case it is invalid
     * @param args the given file path
     * @return Array list of the file lines
     * @throws IOException in case there is a invalid file, or too many arguments
     */
    public static ArrayList<String> parseFile(String[] args) throws IOException{

        ArrayList<String> linesArray = new ArrayList<>();
        if(args.length > 1)
            throw new IOException("ERROR: To many arguments were given");

        //try with resources!
        try (BufferedReader lineReader = new BufferedReader(new FileReader(new File(args[0])))){

            int linesCounter=0;
            for (String line = lineReader.readLine(); line!=null; line = lineReader.readLine()) {
                linesArray.add(linesCounter, line);
                linesCounter++;
            }
            return linesArray;

        } catch (IOException ie){
            throw new IOException("ERROR: File path is invalid");
        }
    }

}
