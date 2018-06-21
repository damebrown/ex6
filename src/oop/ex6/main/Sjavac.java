package oop.ex6.main;

import oop.ex6.FileParser.FileParser;
import oop.ex6.Scope.MethodScope;
import oop.ex6.Types.Variable;
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
            Pattern.compile("^\\s*(final\\s+)?(int|String|double|Char|boolean)\\s+(\\w+)\\s*(=\\s*((\\w*)" +
                    "|(\\\"[^\\\"]*\\\")|(\\\'[^\\\']*\\\')|((-)?\\d([.]\\d)?)))*\\s*(,\\s*(\\w*)\\s*(=\\s*" +
                    "((\\w*)|([\\\"](\\w)[\\\"]))*)*)*\\s*(;)\\s*$");
    private static final Pattern METHOD_DECLARATION_PATTERN = Pattern.compile("^\\s*(void)\\s+[a-zA-Z]\\w*\\s*" +
            "[(](\\s*((final\\s+)?)(int|String|double|Char|boolean)\\s+(\\w+)\\s*)?(\\s*(,)\\s*((final \\s*)?)" +
            "(int|String|double|Char|boolean)\\s+(\\w+)\\s*)*[)]\\s*(\\{)$");
    private static final Pattern END_OF_LINE_PATTERN =Pattern.compile("(\\{)|(^\\s*}\\s*$)|(;)");
    private static final Pattern COMMENT_PATTERN =Pattern.compile("[/]{2}");



    /*Constructor*/
    //todo what is the point of using Sjavac object? - everything is static
    /**
     * oop.ex6.main method
     */
    private Sjavac(String[] args) throws IllegalCodeException, IOException {

        try {
            nullifyStaticVars();
            linesArray = FileParser.parseFile(args);
            upperScopeFactory();
            if (!methodsArray.isEmpty()){
                methodInitializer();
            }
        } catch (IllegalCodeException e){
            throw e;
        } catch (IOException e){
            throw new IOException();
        }
    }

    private void nullifyStaticVars(){
        openingBracketCounter=0;
        closingBracketCounter=0;
        linesArray.clear();
        globalVariablesArray.clear();
        methodsArray.clear();
        METHOD_SCOPE_FLAG=false;
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
                        methodLinesArray.add(line);
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
     *  oop.ex6.main method
     * @param args io arguments
     */
    public static void main(String[] args){
        try{
            new Sjavac(args);
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


