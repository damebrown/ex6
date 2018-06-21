package oop.ex6.Scope;

import oop.ex6.Types.IllegalTypeException;
import oop.ex6.Types.Variable;
import oop.ex6.main.IllegalCodeException;
import oop.ex6.main.Sjavac;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static oop.ex6.main.Sjavac.*;


/**
 * The class represents an abstract scope object
 */
public abstract class Scope {

    /* Data members */
    /* The scope's lines in the code */
    protected ArrayList<String> scopeLinesArray;
    /* the local variables of the upper scope */
    protected ArrayList<Variable> upperScopeVariables;
    /* The oop.ex6.Types.Scope local variables */
    protected ArrayList<Variable> localVariables;
    /*an array of all reachable variables*/
    public ArrayList<ArrayList<Variable>> reachableVariables;
    /* The oop.ex6.Types.Scope's upper scope */
    protected Scope fatherScope;
    /*The oop.ex6.Types.Scope's method */
    public MethodScope fatherMethod;

    static final Pattern METHOD_CALL_PATTERN = Pattern.compile("([a-zA-Z]\\w*){1}[(](\\w*)[)][;]");

    /*Constructor*/

    protected Scope(){
            scopeLinesArray = new ArrayList<>();
            upperScopeVariables =  new ArrayList<>();
            localVariables = new ArrayList<>();
            reachableVariables = new ArrayList<ArrayList<Variable>>();
        }

    /**
     *  the method update to current reachable variables scope, by adding it the upper scopes variables
     */
    protected void variableUpdater(){
        if (!globalVariablesArray.isEmpty()){
            reachableVariables.add(globalVariablesArray);
        } if (fatherMethod!=null){
            if(!fatherMethod.methodParametersArray.isEmpty()){
                reachableVariables.add(fatherMethod.methodParametersArray);
            }
        }
        if (!upperScopeVariables.equals(globalVariablesArray)){
            if (!upperScopeVariables.isEmpty()){
                reachableVariables.add(upperScopeVariables);
            }
        }if (!localVariables.isEmpty()){
            reachableVariables.add(localVariables);
        }
    }

    protected void scopeVariableFactory() throws IllegalTypeException {
        int openingCounter=0, closingCounter=0;
        for (String line : scopeLinesArray){
            Matcher closingMatcher = CLOSING_BRACKET_PATTERN.matcher(line),
                    openingMatcher = OPENING_BRACKET_PATTERN.matcher(line),
                    variableDeclarationMatcher = VARIABLE_DECLARATION_PATTERN.matcher(line);
            if(line.equals(scopeLinesArray.get(0))){
                openingCounter++;
            } else {
                if (openingMatcher.find()){
                    openingCounter++;
                } else if (closingMatcher.find()){
                    closingCounter++;
                } else if ((variableDeclarationMatcher.find())&&(closingCounter!=openingCounter)){
                    ArrayList<Variable> newVariables = Variable.variableInstantiation(line, false);
                    if (localVariables.isEmpty()) {
                        localVariables.addAll(newVariables);
                    } else for (Variable variable: localVariables){
                        for (Variable newVariable:newVariables){
                            if (newVariable.getType().equals(variable.getType())){
                                throw new IllegalTypeException("ERROR: trying to assign an already existing" +
                                        " variable");

                            }
                        }
                    } localVariables.addAll(newVariables);
                }
            }
        }
    }

    protected void scopeValidityHelper(String line, Scope scope) throws IllegalScopeException,
            IllegalTypeException {
        Matcher methodCallMatcher = METHOD_CALL_PATTERN.matcher(line),
                assignmentMatcher = ASSIGNMENT_PATTERN.matcher(line);
        //checking for method calls validity
        if (methodCallMatcher.find()){
            if (!methodCallValidator(line, methodCallMatcher)){
                throw new IllegalScopeException("ERROR: malformed call to method in "+scope.fatherMethod.
                        getMethodName());
            }
        }
        //checking for variable assignment validity
        else if (assignmentMatcher.find()){
            assignmentManager(line,scope);
        }
    }

    /**
     * makes all the scopes instances inside the received upmost scope instance
     * @param upMostScope the scope to search scopes in
     */
    void subScopesFactory(Scope upMostScope, MethodScope method) throws IllegalCodeException, IllegalTypeException {
        Scope fatherScope=upMostScope, currentScope=null;
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
                        method.subScopesArray.add(currentScope);
                    }
                }
            } else {
                if (closingMatcher.find()) {
                    if (!currentScope.equals(upMostScope)){
                        currentScope.scopeLinesArray.add(line);
                    }
                    currentScope.scopeVariableFactory();
                    currentScope = fatherScope;
                    if (!currentScope.equals(upMostScope)){
                        currentScope.scopeValidityManager();
                    }
                } else if (openingMatcher.find()){
                    ArrayList<String> subScopeLinesArray = new ArrayList<>();
                    subScopeLinesArray.add(line);
                    fatherScope = currentScope;
                    currentScope = new ConditionScope(subScopeLinesArray, fatherScope, method);
                    method.subScopesArray.add(currentScope);
                } else {
                    if (!currentScope.equals(upMostScope)){
                        currentScope.scopeLinesArray.add(line);
                    }
                }
            }
        } if (currentScope!=upMostScope){
            throw new IllegalScopeException("ERROR: malform method structure");
        }
    }

    abstract void scopeValidityManager() throws IllegalCodeException ;

    boolean methodCallValidator(String line, Matcher methodCallMatcher) throws IllegalScopeException,
            IndexOutOfBoundsException {
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

    boolean assignmentManager(String assignmentLine, Scope scope) throws IllegalTypeException {
        //TODO make sure we support (regex wise) assignment of existing variables in setValue method
        //TODO make sure that in the above mentioned check we check if the assigned variable is not null
        boolean assignedFlag = false;
        try{
            String[] splatAssignment = assignmentSplitter(assignmentLine);
            for(int index=0;index<=(splatAssignment.length);index++){
                String variableName =  splatAssignment[2*index];
                for (ArrayList<Variable> array : reachableVariables) {
                    if (array!=null){
                        for (Variable localVariable : array){
                            if (localVariable.getName().equals(variableName)){
                                //the check if the variable is final and if the value is valid is done
                                // in the setValue method
                                localVariable.setValue(splatAssignment[1], variableName, this);
                                //if code gets here, everything went well
                                assignedFlag=true;
                            }
                        }
                    }
                }
            }
        } catch (IllegalTypeException e) {
            throw new IllegalTypeException("ERROR: assignment problem in "+scope.fatherMethod.getMethodName());
        } catch (IndexOutOfBoundsException e){
            throw new IndexOutOfBoundsException("ERROR: wrong variable assignment structure");
        } return assignedFlag;
    }

    String[] assignmentSplitter(String line){
        return line.split("[=;,\\s]");
    }

}





