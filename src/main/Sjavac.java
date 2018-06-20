package main;

import Scope.IllegalScopeException;
import Scope.MethodScope;
import Types.Variable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.*;
// i think it fucking works madafakaaaaa!!!!
public class Sjavac {

    //scope factory and variable factory- primary scanning the code and creating
    //the syntax-
    // Pattern patt = Pattern.compile("~wanted pattern~");
    // Matcher matcher = patt.matcher("~wanted text to be searched~");
    //call matcher.find() or matcher.matches();

    /*Constants*/
    private static File checkedFile;

    private static BufferedReader lineReader;

    private static int openingBracketCounter =0, closingBracketCounter =0;

    private static boolean METHOD_SCOPE_FLAG=false;

    public static ArrayList<Variable> globalVariablesArray = new ArrayList<>();

    public ArrayList<String> linesArray = new ArrayList<>();

    public static ArrayList<MethodScope> methodsArray = new ArrayList<>();

    public static final Pattern OPENING_BRACKET_PATTERN =Pattern.compile("(\\{)");
    public static final Pattern CLOSING_BRACKET_PATTERN =Pattern.compile("(})");
    public static final Pattern VARIABLE_DECLARATION_PATTERN =
            Pattern.compile("^[ ]*(final )*[ ]*\\b(int|String|double|Char|boolean)\\b[ ]+(\\b\\w*\\b)[ ]*(=[ ]*((\\b\\w*\\b)|" +
            "(\\\"[^\"]*\\\")))*[ ]*(,[ ]*(\\b\\w*\\b)" +
            "[ ]*(=[ ]*((\\b\\w*\\b)|(\\\"[^\"]*\\\")))*)*[ ]*;[ ]*$");
    private static final Pattern METHOD_DECLARATION_PATTERN = Pattern.compile("^\\s*(void)\\s+[a-zA-Z]\\w*\\s*" +
            "[(](\\s*((final\\s+)?)(int|String|double|Char|boolean)\\s+(\\w+)\\s*)?(\\s*(,)\\s*((final \\s*)?)" +
            "(int|String|double|Char|boolean)\\s+(\\w+)\\s*)*[)](\\{)$");
    public static final Pattern END_OF_LINE_PATTERN =Pattern.compile("(\\{)|(^\\s*}\\s*$)|(;)");
    public static final Pattern COMMENT_PATTERN =Pattern.compile("[/]{2}");
    public static final Pattern EMPTY_LINE_PATTERN = Pattern.compile("\\s");



    /*Constructor*/

    /**
     * main method
     * @param arg file's path
     */
    public Sjavac(String arg) throws IOException, IllegalCodeException {
        try {
            //TODO check if need to nullify the globalVariablesArray
            checkedFile = new File(arg);
            lineReader = new BufferedReader(new FileReader(checkedFile));
            upperScopeFactory();
            methodInitializer();
        } catch (IOException e) {
            throw new IOException();
        } catch (IllegalCodeException e){
            throw new IllegalCodeException();
        }
    }

    /*Methods*/

    //TODO check for method calls outside of a scope

    void upperScopeFactory() throws IOException, IllegalCodeException{
        ArrayList<String> methodLinesArray = new ArrayList<>();
        fileParser();
        for (String line : linesArray){
            Matcher closingMatcher = CLOSING_BRACKET_PATTERN.matcher(line),
                    openingMatcher = OPENING_BRACKET_PATTERN.matcher(line),
                    globalVariableMatcher = VARIABLE_DECLARATION_PATTERN.matcher(line),
                    commentMatcher = COMMENT_PATTERN.matcher(line),
                    endMatcher = END_OF_LINE_PATTERN.matcher(line),
                    emptyLineMatcher = EMPTY_LINE_PATTERN.matcher(line),
                    methodsMatcher = METHOD_DECLARATION_PATTERN.matcher(line);
            if (commentMatcher.find()) {
                if (!line.startsWith("//")) {
                    throw new IllegalCodeException();
                } else {
                    break;
                }
            } else {
                } if (!METHOD_SCOPE_FLAG){
                    if (methodsMatcher.matches()) {
                        METHOD_SCOPE_FLAG = true;
                    } else if (globalVariableMatcher.find()){
                        globalVariablesArray.addAll(Variable.variableInstasiation(line, true));
                    } else if (!emptyLineMatcher.matches()){
                        throw new IllegalCodeException();
                    }
                } else {
                    if (!endMatcher.find()){
                        throw new IllegalCodeException();
                    }
                    methodLinesArray.add(line);
                } if (openingMatcher.find()){
                    openingBracketCounter++;
                } if (closingMatcher.find()){
                    closingBracketCounter++;
                } if (openingBracketCounter == closingBracketCounter){
                    if (!methodLinesArray.isEmpty()){
                        methodsArray.add(new MethodScope(methodLinesArray));
                        methodLinesArray.clear();
                        METHOD_SCOPE_FLAG=false;
                    }
                }
        } if (closingBracketCounter != openingBracketCounter){
            throw new IllegalCodeException();
        }
    }


    private void methodInitializer() throws IllegalCodeException {
        try{
            for (MethodScope method: methodsArray){
                method.methodValidityManager();
            }
        } catch (IllegalCodeException e){
            throw new IllegalCodeException();
        }
    }

    void fileParser() throws IOException{
        int linesCounter=0;
        for (String line = lineReader.readLine(); line!=null; line = lineReader.readLine()){
            linesArray.add(linesCounter, line);
            linesCounter++;
        }
    }


    public static void main(String[] args) throws IOException, IllegalCodeException {
        for (String filePath: args){
            new Sjavac(filePath);
        }
    }
}


//TODO make variable classes be able to differentiate between valid and invalid data assignment