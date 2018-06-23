package oop.ex6.Scope;

import oop.ex6.Types.IllegalTypeException;
import oop.ex6.Types.Variable;
import oop.ex6.main.IllegalCodeException;
import oop.ex6.main.Sjavac;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static oop.ex6.main.Sjavac.CLOSING_BRACKET_PATTERN;
import static oop.ex6.main.Sjavac.OPENING_BRACKET_PATTERN;
import static oop.ex6.main.Sjavac.methodsArray;

/**
 * The class represents a method scope object
 */
public class MethodScope extends Scope {

    /*Constants*/
    /*the method's name*/
    private String methodName;
    /*the methods argument array*/
    ArrayList<Variable> methodParametersArray;
    /*an array of the method's nested sub scopes*/
    public ArrayList<Scope> subScopesArray;

    /*Patterns*/
    private static final Pattern METHOD_NAME_PATTERN = Pattern.compile("(\\b\\s+[a-zA-Z]\\w*){1}");
    private static final Pattern RETURN_PATTERN = Pattern.compile("\\s*(return\\s*;)\\s*");
    private static final Pattern CLOSING_PATTERN = Pattern.compile("\\s*(})\\s*");
    private static final Pattern DECLARATION_PARAMETER_PATTERN = Pattern.compile(
            "(final\\s*)?\\s*(boolean|int|String|char|double)\\s+(\\w+)");


    /*CONSTRUCTOR****/

    /**
     * A method scope constructor
     *
     * @param arrayOfLines the method scope lines
     * @throws IllegalScopeException in case of wrong scope structure
     */
    public MethodScope(ArrayList<String> arrayOfLines) throws IllegalScopeException{
        //calls super
        super();
        try {
            //initializes data members
            if (!arrayOfLines.isEmpty()) {
                scopeLinesArray.addAll(arrayOfLines);
            }
            subScopesArray = new ArrayList<>();
            methodParametersArray = new ArrayList<>();
            fatherScope = null;
            fatherMethod = this;
            this.variableUpdater();
            generateArgs(arrayOfLines.get(0));
            methodNameAssigner(arrayOfLines.get(0));
        } catch (IllegalScopeException e) {
            throw new IllegalScopeException("ERROR: something with a method is wrong");
        } catch (IllegalTypeException e) {
            e.printStackTrace();
        }
    }

    /*METHODS****/

    /*
     * Verifies the method scope structure is valid
     *
     * @return true in case it is valid
     * @throws IllegalCodeException throws exception in case it has a bad format
     */
    private boolean methodValidityChecker() throws IllegalCodeException {
        subScopesFactory(this, this);
        int openingBracketCounter = 0, closingBracketCounter = 0;
        //iterates over method's lines
        for (String line : scopeLinesArray) {
            Matcher returnMatcher = RETURN_PATTERN.matcher(line),
                    closingBracketMatcher = CLOSING_BRACKET_PATTERN.matcher(line),
                    openingBracketMatcher = OPENING_BRACKET_PATTERN.matcher(line),
                    lastLineMatcher = CLOSING_PATTERN.matcher(line);
            //counting brackets
            if (openingBracketMatcher.find()) {
                openingBracketCounter++;
            } else if (closingBracketMatcher.find()) {
                closingBracketCounter++;
                //checking for closing curly bracket in last line
                if (scopeLinesArray.get(scopeLinesArray.size() - 1).equals(line)) {
                    //checking that the last line is a closing bracket
                    if (!lastLineMatcher.matches()) {
                        //else- exception
                        throw new IllegalScopeException("ERROR: malformed method structure in: " +
                                getMethodName());
                    }
                }
            } else if ((closingBracketCounter == openingBracketCounter - 1) && (!scopeLinesArray.get(0).
                    equals(line))) {
                //checks for method calls and variable assignment
                scopeValidityHelper(line, this);
            } //checking that the before last line is a return statement
            if (scopeLinesArray.get(scopeLinesArray.size() - 2).equals(line)) {
                //checking for a return statement
                if ((!(closingBracketCounter == openingBracketCounter - 1)) || (!returnMatcher.find())) {
                    throw new IllegalScopeException("ERROR: malformed method structure in: " + getMethodName());
                }
            }
        }
        return true;
    }

    /*
     * the method generates the parameters that are given in the method signature
     * param declarationLine the variable declaration line
     * throws IllegalTypeException
     */
    private void generateArgs(String declarationLine) throws IllegalTypeException {
        Matcher parameterMatcher = DECLARATION_PARAMETER_PATTERN.matcher(declarationLine);
        // verify method structure
        String parameterDeclaration;
        //find all occurrences
        while (parameterMatcher.find()) {
            // while finding variables generate them and add to the method
            parameterDeclaration = declarationLine.substring(parameterMatcher.start(), parameterMatcher.end());
            ArrayList<Variable> newParams = Variable.variableInstantiation(parameterDeclaration,
                    false, reachableVariables);
            if (!newParams.isEmpty()) {
                for (Variable newParam : newParams) {
                    for (Variable param : newParams) {
                        if ((param.getName().equals(newParam.getName())) && (!param.equals(newParam))) {
                            throw new IllegalTypeException("ERROR: two method args with the same name");
                        }
                    }
                }
                methodParametersArray.addAll(newParams);
            }
        }
    }


    @Override
    /*
     * overrides the scopeValidityManager in Scope. validates the validity of the scope
     * @throws IllegalCodeException
     */
    public void scopeValidityManager() throws IllegalCodeException {
        if (!methodValidityChecker()) {
            throw new IllegalScopeException();
        }
    }

    /*
     * Assign the current method it's name
     *
     * @param declarationLine The method declaration signature
     * @throws IllegalScopeException
     */
    private void methodNameAssigner(String declarationLine) throws IllegalScopeException {
        Matcher nameMatcher = METHOD_NAME_PATTERN.matcher(declarationLine);
        //looking for the method's name
        if (nameMatcher.find()) {
            methodName = declarationLine.substring(nameMatcher.start() + 1, nameMatcher.end());
            if (!Sjavac.methodsArray.isEmpty()) {
                for (MethodScope method : methodsArray) {
                    //checking there is no other method with the same name
                    if (method.getMethodName().equals(methodName)) {
                        throw new IllegalScopeException("ERROR: already taken method name");
                    }
                }
            }
        } else {
            throw new IllegalScopeException("ERROR: method's illegal name");
        }
    }

    /*
     * method name getter
     *
     * @return the method name
     */
    String getMethodName() {
        return methodName;
    }
}
