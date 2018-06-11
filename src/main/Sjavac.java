package main;
import Types.Variable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.*;
public class Sjavac {
    public static final String OPENING_BRACKET = "{";
    public static final String CLOSING_BRACKET = "}";
    public static final String GLOBAL_VAR_PREFIX = "^(int|String|double|char|boolean) +(\\w+)";

    //scope factory and variable factory- primary scanning the code and creating
    //the syntax-
    // Pattern patt = Pattern.compile("~wanted pattern~");
    // Matcher matcher = patt.matcher("~wanted text to be searched~");
    //call matcher.find() or matcher.matches();

    /*Constants*/
    private File checkedFile;

    private BufferedReader lineReader;

    private int openingCurlyBracketCounter=0;

    private int closingCurlyBracketCounter=0;

    private ArrayList<Variable> globalVariablesArray = new ArrayList<>();




    /*Constructor*/


    /**
     *
     * @param arg
     */
    public Sjavac(String arg) throws IOException {
        try {
            checkedFile = new File(arg);
            lineReader = new BufferedReader(new FileReader(checkedFile));
            // call global var. factory?
            // call method factory?
        } catch (IOException e){
            //do something
        }
    }


    /*Methods*/

    void globalVariableFactory() throws  IOException{
        Pattern globalPattern = Pattern.compile("(\\{)|(})|(^\"int|String|double|char|boolean) +(\\w+\")");
        for (String line = lineReader.readLine(); line!=null; line = lineReader.readLine()){
            Matcher globalMatcher = globalPattern.matcher(line);
            if (globalMatcher.find()){
                if (globalMatcher.group(1).matches("\\{")){
                    openingCurlyBracketCounter++;
                } else if (globalMatcher.group(2).matches("}")){
                    closingCurlyBracketCounter++;
                } else if (globalMatcher.group(3).matches(GLOBAL_VAR_PREFIX)){
                    if (openingCurlyBracketCounter==closingCurlyBracketCounter){
                        globalVariablesArray.add(Variable.globalVariableInstasiation(line));
                    }
                }
            }

        }
    }


    public void methodsFactory(){
        Pattern methodsPattern = Pattern.compile("(^(void)[ ]+[a-zA-Z][a-zA-Z_0-9]*[ ]*(\\()\\w*(\\))(\\{)$)");

    }
}
