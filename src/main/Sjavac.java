package main;

import FileParser.FileParser;
import Scope.MethodScope;
import Types.Variable;
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

//    private static BufferedReader lineReader;

    private static int openingBracketCounter = 0, closingBracketCounter = 0;

    private static boolean METHOD_SCOPE_FLAG = false;

    public static ArrayList<Variable> globalVariablesArray = new ArrayList<>();

    private static  ArrayList<String> linesArray  = new ArrayList<>();

    public static ArrayList<MethodScope> methodsArray = new ArrayList<>();

    public static final Pattern OPENING_BRACKET_PATTERN =Pattern.compile("(\\{)");
    public static final Pattern CLOSING_BRACKET_PATTERN =Pattern.compile("(})");
    public static final Pattern VARIABLE_DECLARATION_PATTERN =
            Pattern.compile("^[ ]*(final )*[ ]*\\b(int|String|double|Char|boolean)\\b[ ]+(\\b\\w*\\b)[ ]*(=[ ]*((\\b\\w*\\b)|" +
            "(\\\"[^\"]*\\\")))*[ ]*(,[ ]*(\\b\\w*\\b)" +
            "[ ]*(=[ ]*((\\b\\w*\\b)|(\\\"[^\"]*\\\")))*)*[ ]*;[ ]*$");
    private static final Pattern METHOD_DECLARATION_PATTERN = Pattern.compile("^\\s*(void)\\s+[a-zA-Z]\\w*\\s*" +
            "[(](\\s*((final\\s+)?)(int|String|double|Char|boolean)\\s+(\\w+)\\s*)?(\\s*(,)\\s*((final \\s*)?)" +
            "(int|String|double|Char|boolean)\\s+(\\w+)\\s*)*[)]\\s*(\\{)$");
    private static final Pattern END_OF_LINE_PATTERN =Pattern.compile("(\\{)|(^\\s*}\\s*$)|(;)");
    private static final Pattern COMMENT_PATTERN =Pattern.compile("[/]{2}");



    /*Constructor*/
    //todo what is the point of using Sjavac object? - everything is static
    /**
     * main method
     */
    private Sjavac() throws IllegalCodeException {

        try {
            //TODO check if need to nullify the globalVariablesArray
            upperScopeFactory();
            methodInitializer();
        } catch (IllegalCodeException e){
            throw e;
        }
    }

    /*Methods*/
    // todo - move into Factory class?
    /*
     * the method generates the global variable, and method scope blocks
     * @throws IllegalCodeException
     */
    private void upperScopeFactory() throws IllegalCodeException{
        ArrayList<String> methodLinesArray = new ArrayList<>();

        for (String line : linesArray){
            Matcher closingMatcher = CLOSING_BRACKET_PATTERN.matcher(line),
                    openingMatcher = OPENING_BRACKET_PATTERN.matcher(line),
                    globalVariableMatcher = VARIABLE_DECLARATION_PATTERN.matcher(line),
                    commentMatcher = COMMENT_PATTERN.matcher(line),
                    endMatcher = END_OF_LINE_PATTERN.matcher(line),
                    methodsMatcher = METHOD_DECLARATION_PATTERN.matcher(line);
            if (commentMatcher.find()) {
                if (!line.startsWith("//")) {
                    throw new IllegalCodeException();
                } else {
                    continue;
                }
            } else {
                } if (!METHOD_SCOPE_FLAG){
                    if (methodsMatcher.matches()) {
                        METHOD_SCOPE_FLAG = true;
                        methodLinesArray.add(line);
                    } else if (globalVariableMatcher.find()){
                        globalVariablesArray.addAll(Variable.variableInstantiation(line, true));
                    } else if (!line.equals("")){
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
    //todo WHAT
    /*
     *
     * @throws IllegalCodeException
     */
    private void methodInitializer() throws IllegalCodeException {
        try{
            for (MethodScope method: methodsArray){
                method.methodValidityManager();
            }
        } catch (IllegalCodeException e){
            throw new IllegalCodeException();
        }
    }

    /**
     *  main method
     * @param args io arguments
     */
    public static void main(String[] args){
        try{
            //todo what if file is empty?
            //parse file
             linesArray = FileParser.parseFile(args);
            //call Sjavac

              Sjavac runner =  new Sjavac();
              System.out.println("0");

        }catch (IllegalCodeException ice){
            System.err.println(ice.getMessage());
            System.out.println("1");
        }catch (IOException e){
            System.err.println(e.getMessage());
            System.out.println("2");
        }

    }
}


