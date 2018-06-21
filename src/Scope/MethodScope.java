package Scope;

import Types.IllegalTypeException;
import Types.Variable;
import main.IllegalCodeException;
import main.Sjavac;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.Sjavac.CLOSING_BRACKET_PATTERN;
import static main.Sjavac.OPENING_BRACKET_PATTERN;

/**
 * The class represents a method scope object
 */
public class MethodScope extends Scope {

    /*Constants*/
    private String methodName;

    public final ArrayList<Variable> methodParametersArray = new ArrayList<>();

    public ArrayList<Scope> subScopesArray = new ArrayList<>();

    private static final Pattern METHOD_NAME_PATTERN = Pattern.compile("(\\b\\s+[a-zA-Z]\\w*){1}");

    private static final Pattern RETURN_PATTERN = Pattern.compile("\\s*(return;)\\s*");

    private static final Pattern CLOSING_PATTERN = Pattern.compile("\\s*(})\\s*");

    private static final Pattern DECLARATION_PARAMETER_PATTERN = Pattern.compile(
            "(final\\s*)?\\s*(boolean|int|String|char|double)\\s+(\\w+)");


    //TODO is this needed?
//    private static final Pattern RAW_PARAMETERS_PATTERN = Pattern.compile("([-]?\\d+(\\.?\\d+)|(\"[^\"]*\")" +
//            "|(\'.\')|\\b\\w*\\b)");
//
//    private static Pattern ASSIGNMENT_DECONSTRUCTION = Pattern.compile("^\\s*(\\w*)(\\s*=\\s*)(\\w*|[-]?\\d+(\\.?\\d+)|(\"[^\"]*\")|(\'.\'))(\\s*;\\s*)$");


    /**
     *  A method scope constructor
     * @param arrayOfLines the method scope lines
     * @throws IllegalScopeException
     */
    public MethodScope(ArrayList<String> arrayOfLines) throws IllegalScopeException {
        try{
            scopeLinesArray = arrayOfLines;
            fatherScope=null;
            fatherMethod=this;
            generateArgs(arrayOfLines.get(0));
            methodNameAssigner(arrayOfLines.get(0));
            scopeVariableFactory();
        } catch (IllegalScopeException e){
            throw new IllegalScopeException("ERROR: something with a method is wrong");
        } catch (IllegalTypeException e) {
            e.printStackTrace();
        }
    }


//TODO IMPORTANT!
// In a case of an un-initialized global variable (meaning it is not assigned a value anywhere
//outside a method), all methods may refer to it (regardless of their location in relation to its
//declaration), but every method using it (in an assignment, as an argument to a method call)
//must first assign a value to the global variable itself (even if it was assigned a value in some
//other method).


    //TODO tommorrow thursday:
    //- support the thing with non initialized global variables
    //- the read the PDF again to encounter new problems we have'nt solved
    //- work with the school solution
    //- work with the tester


    /**
     * Verifies the method scope structure is valid
     * @return true in case it is valid
     * @throws IllegalCodeException throws exception in case it has a bad format
     */
    private boolean methodValidityChecker() throws IllegalCodeException {
        subScopesFactory(this, this);
        int openingBracketCounter =0, closingBracketCounter =0;
        for (String line: scopeLinesArray){
            Matcher returnMatcher = RETURN_PATTERN.matcher(line),
                    closingBracketMatcher = CLOSING_BRACKET_PATTERN.matcher(line),
                    openingBracketMatcher = OPENING_BRACKET_PATTERN.matcher(line),
                    lastLineMatcher = CLOSING_PATTERN.matcher(line);
            if (openingBracketMatcher.find()){
                openingBracketCounter++;
            } else if (closingBracketMatcher.find()){
                closingBracketCounter++;
                if (scopeLinesArray.get(scopeLinesArray.size()-1).equals(line)){
                    //checking that the last line is a closing bracket
                    if (!lastLineMatcher.matches()){
                        throw new IllegalScopeException("ERROR: malformed method structure in: "+
                                getMethodName());
                    }
                }
            } else if ((closingBracketCounter==openingBracketCounter-1)&&(!scopeLinesArray.get(0).
                    equals(line))){
                scopeValidityHelper(line, this);
            } //checking that the before last line is a return statement
            if (scopeLinesArray.get(scopeLinesArray.size()-2).equals(line)){
                scopeValidityHelper(line, this);
                if ((!(closingBracketCounter==openingBracketCounter-1))||(!returnMatcher.find())){
                    throw new IllegalScopeException("ERROR: malformed method structure in: "+getMethodName());
                }
            }
        } return true;
    }

    /*
     * the method generates the parameters that are given in the method signature
     * param declarationLine the variable declaration line
     * throws IllegalTypeException
     */
    private void generateArgs(String declarationLine) throws IllegalTypeException {
        System.out.println(declarationLine);
        Matcher parameterMatcher = DECLARATION_PARAMETER_PATTERN.matcher(declarationLine);
        // verify method structure
//        if (parameterMatcher.find()){
            String parameterDeclaration;
            //find all occurrences
            while (parameterMatcher.find()) {
                // while finding variables generate them and add to the method
                parameterDeclaration = declarationLine.substring(parameterMatcher.start(),
                        parameterMatcher.end());
                methodParametersArray.addAll(Variable.variableInstantiation(parameterDeclaration,false));
            }
//        }
    }
    //todo WHAT
    /**
     *
     * @throws IllegalCodeException
     */
    public void methodValidityManager() throws IllegalCodeException {
        upperScopeVariables = Sjavac.globalVariablesArray;
        if (!methodValidityChecker()){
            throw new IllegalScopeException();
        }
    }

    /**
     * Assign the current method it's name
     * @param declarationLine The method declaration signature
     * @throws IllegalScopeException
     */
    private void methodNameAssigner(String declarationLine) throws IllegalScopeException {
        Matcher nameMatcher = METHOD_NAME_PATTERN.matcher(declarationLine);
        if (nameMatcher.find()){
            methodName = declarationLine.substring(nameMatcher.start()+1, nameMatcher.end());
        } else {
            throw new IllegalScopeException();
        }
    }

    /**
     *  method name getter
     * @return the method name
     */
    public String getMethodName(){
        return methodName;
    }
}
