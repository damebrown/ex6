package main;

import Scope.MethodScope;
import Types.Variable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.*;

public class Sjavac {

    //scope factory and variable factory- primary scanning the code and creating
    //the syntax-
    // Pattern patt = Pattern.compile("~wanted pattern~");
    // Matcher matcher = patt.matcher("~wanted text to be searched~");
    //call matcher.find() or matcher.matches();

    /*Constants*/
    private static File checkedFile;

    private static BufferedReader lineReader;

    private static int openingCurlyBracketCounter=0, closingCurlyBracketCounter=0;

    private static boolean METHOD_SCOPE_FLAG=false;

    public static ArrayList<Variable> globalVariablesArray = new ArrayList<>();

    public ArrayList<String> linesArray = new ArrayList<>();

    public ArrayList<MethodScope> methodsArray = new ArrayList<>();

    public static final Pattern OPENING_BRACKET_PATTERN =Pattern.compile("(\\{)");
    public static final Pattern CLOSING_BRACKET_PATTERN =Pattern.compile("(^ *(}))");
    public static final Pattern VARIABLE_DECLARATION_PATTERN =
            Pattern.compile("^[ ]*(final )*[ ]*\\b(int|String|double|Char|boolean)\\b[ ]+(\\b\\w*\\b)[ ]*(=[ ]*((\\b\\w*\\b)|" +
            "(\\\"[^\"]*\\\")))*[ ]*(,[ ]*(\\b\\w*\\b)" +
            "[ ]*(=[ ]*((\\b\\w*\\b)|(\\\"[^\"]*\\\")))*)*[ ]*;[ ]*$");
    public static final Pattern METHOD_DECLARATION_PATTERN =Pattern.compile("((void)[ ]+[a-zA-Z][a-zA-Z_0-9]*[ ]*(\\()\\w*(\\))(\\{)$)");
    public static final Pattern END_OF_LINE_PATTERN =Pattern.compile("(\\{)|(})|(;)");
    public static final Pattern COMMENT_PATTERN =Pattern.compile("[/]{2}");




    /*Constructor*/

    /**
     * main method
     * @param arg file's path
     */
    public Sjavac(String arg) throws IOException {
        try {
            //TODO check if need to nullify the globalVariablesArray
            checkedFile = new File(arg);
            lineReader = new BufferedReader(new FileReader(checkedFile));
            upperScopeFactory();
        } catch (IOException e){
            //do something
        }
    }

    /*Methods*/

    //TODO check for method calls outside of a scope

    void upperScopeFactory() throws IOException{
        ArrayList<String> methodLinesArray = new ArrayList<>();
        fileParser();
        for (String line : linesArray){
            Matcher closingMatcher = CLOSING_BRACKET_PATTERN.matcher(line),
                    openingMatcher = OPENING_BRACKET_PATTERN.matcher(line),
                    globalVariableMatcher = VARIABLE_DECLARATION_PATTERN.matcher(line),
                    commentMatcher = COMMENT_PATTERN.matcher(line),
                    endMatcher = END_OF_LINE_PATTERN.matcher(line),
                    methodsMatcher = METHOD_DECLARATION_PATTERN.matcher(line);
            if (!endMatcher.find()){
                //todo raise exception!!
            if (commentMatcher.find())
                break;
            } else {
                if (METHOD_SCOPE_FLAG){
                    methodLinesArray.add(line);
                    //TODO checking twice for (METHOD_SCOPE_FLAG) might cause error in case of nested method
                } else if (methodsMatcher.matches()) {
                    methodLinesArray.add(line);
                    METHOD_SCOPE_FLAG = true;
                } if (openingMatcher.find()){
                    openingCurlyBracketCounter++;
                    //TODO validate that if methodsMatcher.matches(), still gets in here
                } if (closingMatcher.find()){
                    closingCurlyBracketCounter++;
                } if (openingCurlyBracketCounter==closingCurlyBracketCounter){
                    if (!methodLinesArray.isEmpty()){
                        methodsArray.add(new MethodScope(linesArray));
                        methodLinesArray.clear();
                        METHOD_SCOPE_FLAG=false;
                    } if (globalVariableMatcher.find()){
                        globalVariablesArray.addAll(Variable.variableInstasiation(line, true));
                    } else if (!line.equals("")){
                        //todo raise exception
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

}


//TODO make variable classes be able to differentiate between valid and invalid data assignment