package Scope;

import Types.IllegalTypeException;
import Types.Variable;
import main.IllegalCodeException;
import main.Sjavac;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.Sjavac.*;



public abstract class Scope {

    /* Data members */
    /* The scope's lines in the code */
    protected ArrayList<String> scopeLinesArray;
    /* the local variables of the upper scope */
    protected ArrayList<Variable> upperScopeVariables;
    /* The Scope local variables */
    protected ArrayList<Variable> localVariables;
    /*an array of all reachable variables*/
    protected ArrayList<ArrayList<Variable>> reachableVariables;
    /* The Scope's upper scope */
    protected Scope fatherScope;
    /*The Scope's method */
    public MethodScope fatherMethod;

    public static final Pattern SINGLE_PARAMETER_PATTERN = Pattern.compile("\\w+");


    public Scope(){
        reachableVariables.add(0, globalVariablesArray);
        reachableVariables.add(1, fatherMethod.methodParametersArray);
        if (!upperScopeVariables.equals(globalVariablesArray)){
            reachableVariables.add(2, upperScopeVariables);
        } else {
            reachableVariables.add(2, null);
        } reachableVariables.add(0, localVariables);
    }


    protected void scopeVariableFactory() throws IllegalTypeException {
        int openingCounter=0, closingCounter=0;
        for (String line : scopeLinesArray){
            Matcher closingMatcher = CLOSING_BRACKET_PATTERN.matcher(line),
                    openingMatcher = OPENING_BRACKET_PATTERN.matcher(line),
                    variableDeclarationMatcher = VARIABLE_DECLARATION_PATTERN.matcher(line);
            if(!line.equals(scopeLinesArray.get(0))){
                if (openingMatcher.find()){
                    openingCounter++;
                } else if (closingMatcher.find()){
                    closingCounter++;
                } else if ((variableDeclarationMatcher.find())&&(closingCounter!=openingCounter)){
                    ArrayList<Variable> newVariables = Variable.variableInstasiation(line, false);
                    if (localVariables.isEmpty()) {
                        localVariables.addAll(newVariables);
                    } else for (Variable variable: localVariables){
                        for (Variable newVariable:newVariables){
                            if ((newVariable.getType().equals(variable.getType()))&&
                                    (newVariable.getType().equals(variable.getType()))){
                                throw new IllegalTypeException("ERROR: trying to assign an already existing" +
                                        " variable");

                            }
                        }
                    } localVariables.addAll(newVariables);
                }
            }
        }
    }


    //TODO check that method calls inside a scope are valid

    /**
     * makes all the scopes instances inside the received upmost scope instance
     * @param upmostScope the scope to search scopes in
     */
    protected void subScopesFactory(Scope upmostScope, MethodScope method) throws IllegalCodeException {
        Scope fatherScope=upmostScope, currentScope=null;
        for (String line : scopeLinesArray){
            Matcher closingMatcher = CLOSING_BRACKET_PATTERN.matcher(line),
                    openingMatcher = OPENING_BRACKET_PATTERN.matcher(line);
            if (currentScope==null){
                if (closingMatcher.find()){
                    break;
                } else if (openingMatcher.find()){
                    if (!scopeLinesArray.get(0).equals(line)){
                        ArrayList<String> subScopeLinesArray = new ArrayList<>();
                        subScopeLinesArray.add(line);
                        currentScope = new ConditionScope(subScopeLinesArray, fatherScope, method);
                    }
                }
            } else {
                if (closingMatcher.find()) {
                    currentScope.scopeLinesArray.add(line);
                    currentScope.scopeVariableFactory();
                    currentScope = fatherScope;
                    ((ConditionScope) currentScope).conditionValidityManager();
                } else if (openingMatcher.find()){
                    ArrayList<String> subScopeLinesArray = new ArrayList<>();
                    subScopeLinesArray.add(line);
                    fatherScope = currentScope;
                    currentScope = new ConditionScope(subScopeLinesArray, fatherScope, method);
                } else {
                    currentScope.scopeLinesArray.add(line);
                }
            }
        } if (currentScope!=upmostScope){
            throw new IllegalScopeException("ERROR: malform method structure");
        }
    }

    boolean methodCallValidator(String line, Matcher methodCallMatcher) throws IllegalScopeException {
        boolean nameFlag = false, existenceFlag = false;
        String foundMethodName = line.substring(methodCallMatcher.start(), methodCallMatcher.end());
        MethodScope foundMethod=null;
        for (MethodScope method : Sjavac.methodsArray) {
            if (foundMethodName.startsWith(method.getMethodName())) {
                nameFlag = true;
                foundMethod = method;
            }
        } if (nameFlag){
            if (foundMethod.methodParametersArray.size()==0){
                return true;
            } else {
                int numberOfArgs = foundMethod.methodParametersArray.size(), argsCounter=0;
                //Matcher parameterMatcher = SINGLE_PARAMETER_PATTERN.matcher(line);
                String[] args = line.split("[(),]");
                for (String input:args){
                    argsCounter++;
                    //to check for a call with existing variables
                    for (ArrayList<Variable> array : reachableVariables) {
                        if (array!=null){
                            for (Variable variable : array){
                                if (variable.getName().equals(input)) {
                                    if ((variable.getValue()!=null)||(!array.equals(
                                            foundMethod.methodParametersArray))){
                                        existenceFlag = true;
                                    }
                                }
                            }
                        } if (!existenceFlag){
                            //check the call is done with the suitable types
                            Variable suitableVariable = foundMethod.methodParametersArray.get(argsCounter - 1);
                            if (!suitableVariable.isValid(input)){
                                throw new IllegalScopeException("ERROR: malformed method args in "+foundMethod.getMethodName());
                            } else {
                                existenceFlag=true;
                            }
                        }
                    }
                }
                if (argsCounter > numberOfArgs) {
                    throw new IllegalScopeException("ERROR: malformed method call in " + foundMethod.getMethodName());
                }
            }
        } else {
            throw new IllegalScopeException("ERROR: malformed method call in "+foundMethod.getMethodName());
        } return existenceFlag;
    }


    public String[] assignmentSplitter(String line){
        return line.split("[=;]");
    }

    public boolean assignmentManager(String assignmentLine, Scope scope) throws IllegalTypeException {
        //TODO make sure we support (regex wise) assignment of existing variables in setValue method
        //TODO make sure that in the above mentioned check we check if the assigned variable is not null
        boolean assignedFlag = false;
        try{
            String[] splittedAssignemnt = assignmentSplitter(assignmentLine);
            String variableName =  splittedAssignemnt[0];
            for (ArrayList<Variable> array : reachableVariables) {
                if (array!=null){
                    for (Variable localVariable : array){
                        if (localVariable.getName().equals(variableName)){
                            //the check if the variable is final and if the value is valid is done
                            // in the setValue method
                            localVariable.setValue(splittedAssignemnt[1]);
                            //if code gets here, everything went well
                            assignedFlag=true;
                            return assignedFlag;
                        }
                    }
                }
            }
        } catch (IllegalTypeException e){
            throw new IllegalTypeException("ERROR: assignment problem in "+scope.fatherMethod.getMethodName());
        } return assignedFlag;
    }
}





