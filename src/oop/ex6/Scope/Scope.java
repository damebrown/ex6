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

    static final Pattern METHOD_CALL_PATTERN = Pattern.compile("(([a-zA-Z]\\w*){1})[(](\\w*)[)][;]");
    public static final Pattern ARGUMENT_PATTERN =Pattern.compile("[^,();\\s]\\w*");
    public static final Pattern METHOD_NAME_PATTERN =Pattern.compile("(([a-zA-Z]\\w*){1})([(])");

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
        if (!localVariables.isEmpty()){
            reachableVariables.add(localVariables);
        }
        if (!upperScopeVariables.equals(globalVariablesArray)) {
            if (!upperScopeVariables.isEmpty()) {
                reachableVariables.add(upperScopeVariables);
            }
        }
        if (fatherMethod!=null){
            if(!fatherMethod.methodParametersArray.isEmpty()){
                reachableVariables.add(fatherMethod.methodParametersArray);
            }
        }
        if (!globalVariablesArray.isEmpty()){
            reachableVariables.add(globalVariablesArray);
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
                    if (!localVariables.isEmpty()) {
                        for (Variable newVariable : newVariables){
                            for (Variable variable : localVariables){
                                if (newVariable.getName().equals(variable.getName())){
                                    throw new IllegalTypeException("ERROR: trying to assign an already existing" +
                                            " variable");
                                }
                            }
                        } localVariables.addAll(newVariables);
                    } else{
                        localVariables.addAll(newVariables);
                    }
                }
            }
        }
    }

    /**
     * this function is an aid function for the scope's validity check. it checks calls for functions and
     * variable assignment lines.
     * @param line the line to check
     * @param scope the scope in which it's checking
     * @throws IllegalScopeException exception due to wrong function call
     * @throws IllegalTypeException exception due to wrong variable assignment
     */
    protected void scopeValidityHelper(String line, Scope scope) throws IllegalScopeException,
            IllegalTypeException {
        Matcher methodCallMatcher = METHOD_CALL_PATTERN.matcher(line),
                assignmentMatcher = ASSIGNMENT_PATTERN.matcher(line);
        //checking for method calls validity
        if (methodCallMatcher.find()){
            String name = methodCallMatcher.group(1);
            if (!methodCallValidator(line, name)){
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
        //iterates over the scope's lines
        for (String line : scopeLinesArray){
            Matcher closingMatcher = CLOSING_BRACKET_PATTERN.matcher(line),
                    openingMatcher = OPENING_BRACKET_PATTERN.matcher(line);
            //if a new sub-scope wasn't yet found
            if (currentScope==null){
                //if a closing bracket is found, method is done
                if (closingMatcher.find()){
                    break;
                    //else create new sub-scope
                } else if (openingMatcher.find()){
                    if (!scopeLinesArray.get(0).equals(line)){
                        ArrayList<String> subScopeLinesArray = new ArrayList<>();
                        subScopeLinesArray.add(line);
                        currentScope = new ConditionScope(subScopeLinesArray, fatherScope, method);
                        method.subScopesArray.add(currentScope);
                    }
                }
            } else {
                //if a closing is found, seal the existing subscope and assign the fatherscope to current
                if (closingMatcher.find()) {
                    if (!currentScope.equals(upMostScope)){
                        currentScope.scopeLinesArray.add(line);
                        currentScope.scopeVariableFactory();
                    }
                    currentScope = fatherScope;
                    if (!currentScope.equals(upMostScope)){
                        currentScope.scopeValidityManager();
                    }
                    //if opening is found, create new subscope
                } else if (openingMatcher.find()){
                    ArrayList<String> subScopeLinesArray = new ArrayList<>();
                    subScopeLinesArray.add(line);
                    fatherScope = currentScope;
                    currentScope = new ConditionScope(subScopeLinesArray, fatherScope, method);
                    method.subScopesArray.add(currentScope);
                    //else- add the line to the current subscope's lines array
                } else {
                    if (!currentScope.equals(upMostScope)){
                        currentScope.scopeLinesArray.add(line);
                    }
                }
            }
            //if more scopes were created then closed, raise exception
        } if ((currentScope!=upMostScope)&&(currentScope!=null)){
            throw new IllegalScopeException("ERROR: malform method structure");
        }
    }

    /*
    abstract method ran over by condition and method scopes
     */
    abstract void scopeValidityManager() throws IllegalCodeException ;

    boolean methodCallValidator(String line, String foundMethodName) throws IllegalScopeException,
            IndexOutOfBoundsException {
        boolean nameFlag = false, existenceFlag = false;
//        String foundMethodName = line.substring(methodNameMatcher.start(), methodNameMatcher.end());
        MethodScope foundMethod=null;
        for (MethodScope method : Sjavac.methodsArray) {
            if (foundMethodName.startsWith(method.getMethodName())) {
                nameFlag = true;
                foundMethod = method;
                break;
            }
        } if (nameFlag){
            if (foundMethod.methodParametersArray.size()==0){
                return true;
            } else {
                int numberOfArgs = foundMethod.methodParametersArray.size(), argsCounter=0;
                //String[] args = line.split("[(),;\\s]");
                Matcher argumentMatcher = ARGUMENT_PATTERN.matcher(line);
                ArrayList<String> argsArray = new ArrayList<>();
                while (argumentMatcher.find()){
                    String arg = line.substring(argumentMatcher.start(), argumentMatcher.end());
                    if ((!arg.startsWith(foundMethodName))&&(!arg.equals(""))){
                        argsArray.add(arg);
                    }
                }
                if (!reachableVariables.isEmpty()){
                    for (String input:argsArray){
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
        boolean assignedFlag = false;
        try{
            String[] splatAssignment = assignmentSplitter(assignmentLine);
            for(int index=0;index<=(splatAssignment.length);index++){
                String variableName =  splatAssignment[2*index];
                for (ArrayList<Variable> array : reachableVariables) {
                    if (array!=null){
                        for (Variable localVariable : array){
                            if (localVariable.getName().equals(variableName)){
                                if (!array.equals(this.fatherMethod.methodParametersArray)){
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





