package Scope;

import Types.IllegalTypeException;
import main.Sjavac;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.Sjavac.VARIABLE_DECLARATION_PATTERN;


public class MethodScope extends Scope {

    /*Constants*/
    private String methodName;

    private final Pattern METHOD_NAME_PATTERN = Pattern.compile("(\\b\\s+[a-zA-Z]\\w*){1}");

    private final Pattern RETURN_PATTERN = Pattern.compile("\\s*(return;)\\s*");

    private final Pattern CLOSING_PATTERN = Pattern.compile("\\s*(})\\s*");

    private final Pattern METHOD_CALL_PATTERN = Pattern.compile("([a-zA-Z]\\w*){1}[(](\\w*)[)][;]");

    public MethodScope(ArrayList<String> arrayOfLines) throws IllegalScopeException {
        try{
            scopeLinesArray = arrayOfLines;
            fatherScope=null;
            methodNameAssigner(arrayOfLines.get(0));
        } catch (IllegalScopeException e){
            throw new IllegalScopeException();
        }
    }


//TODO IMPORTANT!
// In a case of an un-initialized global variable (meaning it is not assigned a value anywhere
//outside a method), all methods may refer to it (regardless of their location in relation to its
//declaration), but every method using it (in an assignment, as an argument to a method call)
//must first assign a value to the global variable itself (even if it was assigned a value in some
//other method).

    private boolean methodValidityChecker() throws IllegalScopeException{
        //TODO check for: validity of parameters
        for (String line: scopeLinesArray){
            Matcher returnMatcher = RETURN_PATTERN.matcher(line),
                    methodCallMatcher = METHOD_CALL_PATTERN.matcher(line),
                    variableDeclarationMatcher = VARIABLE_DECLARATION_PATTERN.matcher(line),
                    lastLineMatcher = CLOSING_PATTERN.matcher(line);
            if (methodCallMatcher.find()){
                boolean nameFlag = false;
                String foundMethod = line.substring(methodCallMatcher.start(), methodCallMatcher.end());
                for (MethodScope method : Sjavac.methodsArray){
                    if (foundMethod.startsWith(method.getMethodName())){
                        nameFlag = true;
                    }
                } if (nameFlag){
                    //TODO check that parameters of method call are valid
                }
            } else if (variableDeclarationMatcher.find()){
                //if (){}
                //TODO check: if it is global, raise exception
                //TODO check: if it is not global, that a variable with the same name doesn't exist in the scope

            } else if (scopeLinesArray.get(scopeLinesArray.size()-2).equals(line)){
                if (!returnMatcher.find()){
                    throw new IllegalScopeException();
                }
            } else if (scopeLinesArray.get(scopeLinesArray.size()-1).equals(line)){
                if (!lastLineMatcher.find()){
                    throw new IllegalScopeException();
                }
            }
        }
        //parameters, validity of name,
        //todo lemamesh method-call-parameters-validity check
        return true;
    }


    public void methodValidityManager() throws IllegalScopeException, IllegalTypeException {
        if (methodValidityChecker()){
            subScopesFactory(this);
            upperScopeVariables = Sjavac.globalVariablesArray;
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
