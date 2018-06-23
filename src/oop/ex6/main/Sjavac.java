package oop.ex6.main;

import oop.ex6.FileParser.FileParser;
import oop.ex6.Scope.MethodScope;
import oop.ex6.Scope.Scope;
import oop.ex6.Types.Variable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.*;

public class Sjavac {


    /*Constants*/

    /*counters*/
    private static int openingBracketCounter = 0, closingBracketCounter = 0;
    /*flag*/
    private static boolean METHOD_SCOPE_FLAG = false;
    /*global array*/
    public static ArrayList<Variable> globalVariablesArray = new ArrayList<>();
    /*lines array*/
    private static ArrayList<String> linesArray = new ArrayList<>();
    /*array of all methods in the code*/
    public static ArrayList<MethodScope> methodsArray = new ArrayList<>();
    /*opening pattern*/
    public static final Pattern OPENING_BRACKET_PATTERN = Pattern.compile("(\\{)");
    /*closing pattern*/
    public static final Pattern CLOSING_BRACKET_PATTERN = Pattern.compile("(})");

    /*PATTERNS:*/
    /*variable pattern*/
    public static final Pattern VARIABLE_DECLARATION_PATTERN =
            Pattern.compile("^\\s*(final\\s+)?(int|String|double|char|boolean)\\s+(\\w+)\\s*(=\\s*((\\w*)|" +
                    "(\\\"[^\\\"]*\\\")|(\\'[^\\']\\')|((-)?\\d([.]\\d)?)))*\\s*" +
                    "(,\\s*(\\w*)\\s*(=\\s*((\\w*)|(\\\"[^\\\"]*\\\")|(\\'[^\\']\\')))*)*\\s*(;)\\s*$");
    /*method pattern*/
    private static final Pattern METHOD_DECLARATION_PATTERN = Pattern.compile("^\\s*(void)\\s+[a-zA-Z]\\w*" +
            "\\s*[(]\\s*((\\s*((final\\s+)?)(int|String|double|char|boolean)\\s+(\\w+)\\s*){1}(\\s*(,)\\s*" +
            "((final\\s+)?)(int|String|double|char|boolean)\\s+(\\w+)\\s*)*)*[\\)]\\s*(\\{)$");
    /*end of line pattern*/
    private static final Pattern END_OF_LINE_PATTERN = Pattern.compile("(\\{$)|(^\\s*}\\s*$)|(;$)");
    /*comment pattern*/
    private static final Pattern COMMENT_PATTERN = Pattern.compile("[/]{2}");
    /*assignment pattern*/
    public static final Pattern ASSIGNMENT_PATTERN = Pattern.compile("^\\s*\\b\\w*\\b\\s*=\\s*(\\b\\w*\\b|" +
            "[-]?\\d+(\\.?\\d+)|(\"[^\"]*\")|(\'.\'))\\s*;\\s*$");
    /*empty line pattern*/
    private static final Pattern EMPTY_LINE_PATTERN = Pattern.compile("\\s*");


    /*Constructor*/

    /**
     * oop.ex6.main method
     */
    private Sjavac(String[] args) throws IllegalCodeException, IOException {
        try {
            nullifyStaticVars();
            linesArray = FileParser.parseFile(args);
            upperScopeFactory();
            if (!methodsArray.isEmpty()) {
                methodInitializer();
            }
        } catch (IllegalCodeException e) {
            throw e;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    /*
     * nullifies all static constants
     */
    private void nullifyStaticVars() {
        openingBracketCounter = 0;
        closingBracketCounter = 0;
        linesArray.clear();
        globalVariablesArray.clear();
        methodsArray.clear();
        METHOD_SCOPE_FLAG = false;
    }

    /*Methods*/

    /*
     * the method generates the global variable and method scope blocks
     * @throws IllegalCodeException
     */
    private void upperScopeFactory() throws IllegalCodeException {
        ArrayList<String> methodLinesArray = new ArrayList<>();
        //iterates over lines
        for (String line : linesArray) {
            Matcher commentMatcher = COMMENT_PATTERN.matcher(line),
                    emptyMatcher = EMPTY_LINE_PATTERN.matcher(line),
                    closingMatcher = CLOSING_BRACKET_PATTERN.matcher(line),
                    openingMatcher = OPENING_BRACKET_PATTERN.matcher(line);
            if (commentMatcher.find()) {
                if (!line.startsWith("//")) {
                    throw new IllegalCodeException();
                } else {
                    continue;
                }
                //if this is not a line in a method
            } if (!METHOD_SCOPE_FLAG) {
                Matcher assignmentMatcher = ASSIGNMENT_PATTERN.matcher(line),
                        globalVariableMatcher = VARIABLE_DECLARATION_PATTERN.matcher(line),
                        methodsMatcher = METHOD_DECLARATION_PATTERN.matcher(line);
                if (methodsMatcher.matches()) {
                    METHOD_SCOPE_FLAG = true;
                    methodLinesArray.add(line);
                } else if (globalVariableMatcher.find()) {
                    ArrayList<ArrayList<Variable>> nestedArray = new ArrayList<>();
                    nestedArray.add(globalVariablesArray);
                    globalVariablesArray.addAll(Variable.variableInstantiation(line, true,
                            nestedArray));
                } else if ((!line.equals("")) && ((!assignmentMatcher.matches())) ||
                        (!emptyMatcher.matches())) {
                    throw new IllegalCodeException();
                }
            //else if this is a line in a method
            } else {
                Matcher endMatcher = END_OF_LINE_PATTERN.matcher(line);
                if (!endMatcher.find()) {
                    if ((!line.equals("")) && (!emptyMatcher.matches())) {
                        throw new IllegalCodeException();
                    }
                //check for empty lines
                } if (!line.equals("")) {
                    methodLinesArray.add(line);
                }
            //counts closing a opening brackets
            } if (openingMatcher.find()) {
                openingBracketCounter++;
            }
            if (closingMatcher.find()) {
                closingBracketCounter++;
            }
            //if even, a method is over, create it
            if (openingBracketCounter == closingBracketCounter) {
                if (!methodLinesArray.isEmpty()) {
                    methodsArray.add(new MethodScope(methodLinesArray));
                    methodLinesArray.clear();
                    METHOD_SCOPE_FLAG = false;
                }
            }
        }
        //if not even, the code is wrong
        if (closingBracketCounter != openingBracketCounter) {
            throw new IllegalCodeException();
        }
    }


    /*
     * this method check for all methods and their scope's validity
     * @throws IllegalCodeException
     */
    private void methodInitializer() throws IllegalCodeException {
        try {
            for (MethodScope method : methodsArray) {
                method.scopeValidityManager();
                if (!method.subScopesArray.isEmpty()) {
                    for (Scope subscope : method.subScopesArray) {
                        subscope.scopeValidityManager();
                    }
                }

            }
        } catch (IllegalCodeException e) {
            throw new IllegalCodeException();
        }
    }

    /**
     * oop.ex6.main method
     *
     * @param args io arguments
     */
    public static void main(String[] args) {
        try {
            new Sjavac(args);
            System.out.println("0");
        } catch (IllegalCodeException ice) {
            System.err.println(ice.getMessage());
            System.out.println("1");
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.out.println("2");
        }
    }
}


