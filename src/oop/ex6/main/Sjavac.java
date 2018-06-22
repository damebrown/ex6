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
            Pattern.compile("^\\s*(final\\s+)?(int|String|double|char|boolean)\\s+(\\w+)\\s*(=\\s*((\\w*)|" +
                    "(\\\"[^\\\"]*\\\")|(\\'[^\\']\\')|((-)?\\d([.]\\d)?)))*\\s*" +
                    "(,\\s*(\\w*)\\s*(=\\s*((\\w*)|(\\\"[^\\\"]*\\\")|(\\'[^\\']\\')))*)*\\s*(;)\\s*$");
    private static final Pattern METHOD_DECLARATION_PATTERN = Pattern.compile("^\\s*(void)\\s+[a-zA-Z]\\w*\\s*" +
            "[(](\\s*((final\\s+)?)(int|String|double|char|boolean)\\s+(\\w+)\\s*)?(\\s*(,)\\s*((final \\s*)?)" +
            "(int|String|double|char|boolean)\\s+(\\w+)\\s*)*[)]\\s*(\\{)$");
    private static final Pattern END_OF_LINE_PATTERN =Pattern.compile("(\\{)|(^\\s*}\\s*$)|(;)");
    private static final Pattern COMMENT_PATTERN =Pattern.compile("[/]{2}");
    private static final Pattern EMPTY_LINE_PATTERN = Pattern.compile("\\s*");
    public static final Pattern ASSIGNMENT_PATTERN = Pattern.compile("^\\s*\\b\\w*\\b\\s*=\\s*(\\b\\w*\\b|[-]?\\d+" +
            "(\\.?\\d+)|(\"[^\"]*\")|(\'.\'))\\s*;\\s*$");


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
            throw new IOException(e.getMessage());
        }
    }

    /*
     *
     */
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
            Matcher commentMatcher = COMMENT_PATTERN.matcher(line),
                    closingMatcher = CLOSING_BRACKET_PATTERN.matcher(line),
                    emptyLineMatcher = EMPTY_LINE_PATTERN.matcher(line),
                    openingMatcher = OPENING_BRACKET_PATTERN.matcher(line);
            if (commentMatcher.find()) {
                if (!line.startsWith("//")) {
                    throw new IllegalCodeException();
                } else {
                    continue;
                }
            } else {
                } if (!METHOD_SCOPE_FLAG){
                    Matcher assignmentMatcher = ASSIGNMENT_PATTERN.matcher(line),
                            globalVariableMatcher = VARIABLE_DECLARATION_PATTERN.matcher(line),
                            methodsMatcher = METHOD_DECLARATION_PATTERN.matcher(line);
                    if (methodsMatcher.matches()) {
                        METHOD_SCOPE_FLAG = true;
                        methodLinesArray.add(line);
                    } else if (globalVariableMatcher.find()){
//                        ArrayList<ArrayList<Variable>> test = null;
                        globalVariablesArray.addAll(Variable.variableInstantiation(line, null,true));
                    } else if ((!emptyLineMatcher.matches())&&((!assignmentMatcher.matches()))){
                        throw new IllegalCodeException();
                    }
                } else {
                    Matcher endMatcher = END_OF_LINE_PATTERN.matcher(line);
                    if (!endMatcher.find()){
                        if (!emptyLineMatcher.matches()){
                            throw new IllegalCodeException();
                        }
                    }
                    if (!emptyLineMatcher.matches()){
                        methodLinesArray.add(line);
                    }
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


    /*
     *
     * @throws IllegalCodeException
     */
    private void methodInitializer() throws IllegalCodeException {
        try{
            for (MethodScope method: methodsArray){
                method.scopeValidityManager();
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


