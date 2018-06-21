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
import static main.Sjavac.VARIABLE_DECLARATION_PATTERN;


public class MethodScope extends Scope {

    /*Constants*/
    private String methodName;

    public final ArrayList<Variable> methodParametersArray = new ArrayList<>();

    private static final Pattern METHOD_NAME_PATTERN = Pattern.compile("(\\b\\s+[a-zA-Z]\\w*){1}");

    private static final Pattern RETURN_PATTERN = Pattern.compile("\\s*(return;)\\s*");

    private static final Pattern CLOSING_PATTERN = Pattern.compile("\\s*(})\\s*");

    static final Pattern METHOD_CALL_PATTERN = Pattern.compile("([a-zA-Z]\\w*){1}[(](\\w*)[)][;]");

    private static final Pattern DECLARATION_PARAMETER_PATTERN = Pattern.compile(
            "(final\\s*)?(boolean|int|String|char|double)\\s+(\\w+)");

    private static final Pattern RAW_PARAMETERS_PATTERN = Pattern.compile("([-]?\\d+(\\.?\\d+)|(\"[^\"]*\")|(\'.\')|\\b\\w*\\b)");

    private static Pattern ASSIGNMENT_PATTERN = Pattern.compile("^\\s*\\b\\w*\\b\\s*=\\s*(\\b\\w*\\b|[-]?\\d+(\\.?\\d+)|(\"[^\"]*\")|(\'.\'))\\s*;\\s*$");

    private static Pattern ASSIGNMENT_DECONSTRUCTION = Pattern.compile("^\\s*(\\w*)(\\s*=\\s*)(\\w*|[-]?\\d+(\\.?\\d+)|(\"[^\"]*\")|(\'.\'))(\\s*;\\s*)$");





    public MethodScope(ArrayList<String> arrayOfLines) throws IllegalScopeException {
        try{
            scopeLinesArray = arrayOfLines;
            fatherScope=null;
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
    //- make the method validity checker count brackets and send the checks to the subscopes for every call
    //      or assignment etc.
    //- make assignment work in all scopes! things to be supported:
    //        assignment only if not final, assignment in right scopes, ...
    //- support the thing with non initialized global variables
    //- the read the PDF again to encounter new problems we have'nt solved
    //- work with the school solution
    //- work with the tester



    private boolean methodValidityChecker() throws IllegalCodeException {
        subScopesFactory(this, this);
        for (String line: scopeLinesArray){
            Matcher returnMatcher = RETURN_PATTERN.matcher(line),
                    methodCallMatcher = METHOD_CALL_PATTERN.matcher(line),
                    assignmentMatcher = ASSIGNMENT_PATTERN.matcher(line),
                    closingBracketMatcher = CLOSING_BRACKET_PATTERN.matcher(line),
                    openingBracketMatcher = OPENING_BRACKET_PATTERN.matcher(line),
                    lastLineMatcher = CLOSING_PATTERN.matcher(line);
            //checking for method calls validity
            if (methodCallMatcher.find()){
                if (!methodCallValidator(line, methodCallMatcher)){
                    throw new IllegalScopeException("ERROR: malformed call to method in "+getMethodName());
                }
            }
            //checking for variable assignment validity
            else if (assignmentMatcher.find()){

            }
            //checking that the before last line is a return statement
            else if (scopeLinesArray.get(scopeLinesArray.size()-2).equals(line)){
                if (!returnMatcher.find()){
                    throw new IllegalScopeException("ERROR: malformed method structure in: "+getMethodName());
                }
                //checking that the last line is a closing bracket
            } else if (scopeLinesArray.get(scopeLinesArray.size()-1).equals(line)){
                if (!lastLineMatcher.find()){
                    throw new IllegalScopeException("ERROR: malformed method structure in: "+getMethodName());
                }
            }
        }
        //todo lemamesh method-call-parameters-validity check
        return true;
    }

    private void generateArgs(String declarationLine) throws IllegalTypeException {
        Matcher parameterMatcher = DECLARATION_PARAMETER_PATTERN.matcher(declarationLine);
        // verify method structure
        if (parameterMatcher.matches()){
            String parameterDeclaration;
            //find all occurrences
            while (parameterMatcher.find()) {
                // while finding variables generate them and add to the method
                parameterDeclaration = declarationLine.substring(parameterMatcher.start(), parameterMatcher.end());
                methodParametersArray.addAll(Variable.variableInstasiation(parameterDeclaration,false));
            }
        }
    }


    public void methodValidityManager() throws IllegalCodeException {
        upperScopeVariables = Sjavac.globalVariablesArray;
        if (methodValidityChecker()){
            //subScopesFactory(this, this);
        } else {
            throw new IllegalScopeException();
        }
    }

    private void methodNameAssigner(String declarationLine) throws IllegalScopeException {
        Matcher nameMatcher = METHOD_NAME_PATTERN.matcher(declarationLine);
        if (nameMatcher.find()){
            methodName = declarationLine.substring(nameMatcher.start()+1, nameMatcher.end());
        } else {
            throw new IllegalScopeException();
        }
    }


    public String getMethodName(){
        return methodName;
    }
}
