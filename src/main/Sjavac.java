package main;
import Scope.*;
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
    public static final String GLOBAL_VAR_PREFIX = "^ *(int|String|double|char|boolean) +(\\w+)";

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

    public ArrayList<String> linesArray = new ArrayList<>();

    public ArrayList<MethodScope> methodsArray = new ArrayList<>();




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
        Pattern globalPattern = Pattern.compile("(^ *(}))|(\\{)|(^ *\"int|String|double|char|boolean) +(\\w+\")");
        Pattern methodsPattern =Pattern.compile("((void)[ ]+[a-zA-Z][a-zA-Z_0-9]*[ ]*(\\()\\w*(\\))(\\{)$)");
        ArrayList<String> methodsLinesArray = new ArrayList<>();
        fileParser();
        for (String line : linesArray){
            Matcher globalMatcher = globalPattern.matcher(line), methodsMatcher = methodsPattern.matcher(line);
            if (globalMatcher.find()){
                if (methodsMatcher.matches()){
                    methodsLinesArray.add(line);
                }
                if (openingCurlyBracketCounter==closingCurlyBracketCounter){}
                if (globalMatcher.group(1)!=null){
                    openingCurlyBracketCounter++;
                } if (globalMatcher.group(2)!=null){
                    closingCurlyBracketCounter++;
                } if (openingCurlyBracketCounter==closingCurlyBracketCounter){
                    if (!methodsLinesArray.isEmpty()){
                        MethodScope newMethod;
//                        newMethod = Scope.MethodScope(methodsLinesArray);
//                        methodsArray.add(newMethod);
//                        methodsLinesArray.clear();
                    }
                    if(globalMatcher.group(3)!=null){
                        globalVariablesArray.add(Variable.variableFactory(line));
                    }
                }
            }
        }
    }

    void fileParser() throws IOException{
        int linesCounter=0;
        for (String line = lineReader.readLine(); line!=null; line = lineReader.readLine()){
            linesArray.add(linesCounter, line);
            linesCounter++;
        }
    }

//    public void methodsFactory() throws IOException{
//        openingCurlyBracketCounter=0;
//        closingCurlyBracketCounter = 0;
//        Pattern methodsPattern =Pattern.compile("(^ *(})|(void)[ ]+[a-zA-Z][a-zA-Z_0-9]*[ ]*(\\()\\w*(\\))(\\{)$)|(\\{)");
//        String methodString=null;
//        for (String line = lineReader.readLine(); line!=null; line = lineReader.readLine()){
//            Matcher methodsMatcher = methodsPattern.matcher(line);
//            if (methodsMatcher.find()){
//                if (methodsMatcher.group(2).matches("(void)[ ]+[a-zA-Z][a-zA-Z_0-9]*[ ]*(\\()\\w*(\\))" +
//                        "(\\{)$")||(openingCurlyBracketCounter!=closingCurlyBracketCounter)){
//                    openingCurlyBracketCounter++;
//                    methodString = methodString.concat(methodString.substring(methodsMatcher.start(),
//                            methodsMatcher.end()));
//                } else if (methodsMatcher.)
//            }
//        }
//    }

}

//TODO build arrays of global variables and methods
//TODO make variable classes be able to differentiate between valid and invalid data assignment