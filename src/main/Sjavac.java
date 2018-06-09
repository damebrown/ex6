package main;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
public class Sjavac {

    //scope factory and variable factory- primary scanning the code and creating
    //the syntax-
    // Pattern patt = Pattern.compile("~wanted pattern~");
    // Matcher matcher = patt.matcher("~wanted text to be searched~");
    //call matcher.find() or matcher.matches();

    /*Constants*/
    public File checkedFile;



    /*Constructor*/


    /**
     *
     * @param arg
     */
    public Sjavac(String arg) throws IOException {
        try {
            checkedFile = new File(arg);
            BufferedReader lineReader = new BufferedReader(new FileReader(checkedFile));
            // call global var. factory?
            // call method factory?
        } catch (IOException e){
            //do something
        }
    }


    /*Methods*/

    public void globalVariableFactory(){}

    public void methodsFactory(){}
}
