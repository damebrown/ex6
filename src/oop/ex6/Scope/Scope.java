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
    ArrayList<String> scopeLinesArray;
    /* the local variables of the upper scope */
    ArrayList<Variable> upperScopeVariables;
    /* The oop.ex6.Types.Scope local variables */
    private ArrayList<Variable> localVariables;
    /*an array of all reachable variables*/
    public ArrayList<ArrayList<Variable>> reachableVariables;
    /* The oop.ex6.Types.Scope's upper scope */
    Scope fatherScope;
    /*The oop.ex6.Types.Scope's method */
    MethodScope fatherMethod;
    /*an array of all nested variables*/
    private static ArrayList<ArrayList<Variable>> nestedArray = new ArrayList<>();
    /*method call pattern*/
    private static final Pattern METHOD_CALL_PATTERN = Pattern.compile("(([a-zA-Z]\\w*){1})[(](\\w*)[)][;]");
    /*argument pattern*/
    private static final Pattern ARGUMENT_PATTERN = Pattern.compile("[^,();\\s]\\w*");

    /*Constructor*****/

    /**
     * initializes the instance's arrays
     */
    Scope() {
        scopeLinesArray = new ArrayList<>();
        upperScopeVariables = new ArrayList<>();
        localVariables = new ArrayList<>();
        reachableVariables = new ArrayList<>();
    }

    /*Methods*****/

    /**
     abstract method ran over by condition and method scopes
     */
    public abstract void scopeValidityManager() throws IllegalCodeException;


    /**
     * the method update to current reachable variables scope, by adding it the upper scopes variables
     */
    void variableUpdater() {
        if (!localVariables.isEmpty()) {
            reachableVariables.add(localVariables);
        } else {
            localVariables = new ArrayList<>();
            reachableVariables.add(localVariables);
        }
        if (!upperScopeVariables.equals(globalVariablesArray)) {
            if (!upperScopeVariables.isEmpty()) {
                reachableVariables.add(upperScopeVariables);
            }
        } else {
            upperScopeVariables = new ArrayList<>();
            reachableVariables.add(upperScopeVariables);
        }
        if (fatherMethod != null) {
            if (!fatherMethod.methodParametersArray.isEmpty()) {
                reachableVariables.add(fatherMethod.methodParametersArray);
            }
        }
        if (!globalVariablesArray.isEmpty()) {
            reachableVariables.add(globalVariablesArray);
        } else {
            globalVariablesArray = new ArrayList<>();
            reachableVariables.add(globalVariablesArray);
        }
    }


    /*
     * this function is an aid function for the scope's validity check. it checks calls for functions and
     * variable assignment lines.
     *
     * @param line  the line to check
     * @param scope the scope in which it's checking
     * @throws IllegalScopeException exception due to wrong function call
     * @throws IllegalTypeException  exception due to wrong variable assignment
     */
    void scopeValidityHelper(String line, Scope scope) throws IllegalScopeException, IllegalTypeException {
        Matcher methodCallMatcher = METHOD_CALL_PATTERN.matcher(line),
                assignmentMatcher = ASSIGNMENT_PATTERN.matcher(line);
        //checking for method calls validity
        if (methodCallMatcher.find()) {
            String name = methodCallMatcher.group(1);
            if (!methodCallValidator(line, name)) {
                throw new IllegalScopeException("ERROR: malformed call to method in " + scope.fatherMethod.
                        getMethodName());
            }
        }
        //checking for variable assignment validity
        else if (assignmentMatcher.find()) {
            assignmentManager(line, scope);
        }
    }

    /*
     * creates all condition scopes variables nested in a scope (if any)
     * @param line the line to check if there's a variables declaration in it
     * @throws IllegalTypeException calls variableInstantiation method that might cause IllegalTypeException
     */
    private ArrayList<Variable> variableFactory(String line) throws IllegalTypeException {
        Matcher variableDeclarationMatcher = VARIABLE_DECLARATION_PATTERN.matcher(line);
        //if a declaration is found, create the variable instance
        if (variableDeclarationMatcher.find()) {
            return Variable.variableInstantiation(line, false, reachableVariables);
        }
        return null;
    }


    /*
     * makes all the scopes instances inside the received upmost scope instance
     * @param upMostScope the scope to search scopes in
     */
    void subScopesFactory(Scope upMostScope, MethodScope method) throws IllegalCodeException {
        Scope fatherScope = upMostScope, currentScope = null;
        //the array of all nested scope's variables
        nestedArray = new ArrayList<>();
        nestedArray.add(new ArrayList<>());
        //iterates over the scope's lines
        for (String line : scopeLinesArray) {
            Matcher closingMatcher = CLOSING_BRACKET_PATTERN.matcher(line),
                    openingMatcher = OPENING_BRACKET_PATTERN.matcher(line);
            //if a new sub-scope wasn't yet found
            if (currentScope == null) {
                //checking for variable declaration, and creates them if such exists
                ArrayList<Variable> variables = variableFactory(line);
                //if a declaration exist, adds to the last array in nested
                if (variables != null) {
                    (nestedArray.get(nestedArray.size() - 1)).addAll(variables);
                }
                //if a closing bracket is found, method is done
                if (closingMatcher.find()) {
                    //if close, add all variables in the last array in nested
                    fatherScope.localVariables.addAll(nestedArray.get(nestedArray.size() - 1));
                    //remove last array
                    nestedArray.remove(nestedArray.size() - 1);
                    break;
                    //else create new sub-scope
                } else if (openingMatcher.find()) {
                    if (!scopeLinesArray.get(0).equals(line)) {
                        ArrayList<String> subScopeLinesArray = new ArrayList<>();
                        subScopeLinesArray.add(line);
                        currentScope = new ConditionScope(subScopeLinesArray, fatherScope, nestedArray.get(nestedArray.size() - 1));
                        nestedArray.add(new ArrayList<>());
                        //method.subScopesArray.add(currentScope);
                    }
                }
            } else {
                //checking for variable declaration, and creates them if such exists
                ArrayList<Variable> variables = variableFactory(line);
                //if a declaration exist, adds to the last array in nested
                if (variables != null) {
                    (nestedArray.get(nestedArray.size() - 1)).addAll(variables);
                }
                //if a closing is found, seal the existing subscope and assign the fatherscope to current
                if (closingMatcher.find()) {
                    //if close, add all variables in the last array in nested
                    currentScope.localVariables.addAll(nestedArray.get(nestedArray.size() - 1));
                    //remove last array
                    nestedArray.remove(nestedArray.size() - 1);
                    if (!currentScope.equals(upMostScope)) {
                        currentScope.scopeLinesArray.add(line);
                        currentScope = fatherScope;
                    }
                    //if opening is found, create new subscope
                } else if (openingMatcher.find()) {
                    ArrayList<String> subScopeLinesArray = new ArrayList<>();
                    subScopeLinesArray.add(line);
                    fatherScope = currentScope;
                    currentScope = new ConditionScope(subScopeLinesArray, fatherScope, nestedArray.get(nestedArray.size() - 1));
                    nestedArray.add(new ArrayList<>());
                    //method.subScopesArray.add(currentScope);
                } else {
                    //else- add the line to the current subscope's lines array
                    if (!currentScope.equals(upMostScope)) {
                        currentScope.scopeLinesArray.add(line);
                    }
                }
            }
            //if more scopes were created then closed, raise exception
        }
        if ((currentScope != upMostScope) && (currentScope != null)) {
            throw new IllegalScopeException("ERROR: malform method structure");
        }
    }

    /*
     * makes sure a method call is valid
     * @param line            the method call declaration line
     * @param foundMethodName the method called
     * @return true iff the call is valid
     * @throws IllegalScopeException in case of a wrong method call structure
     * @throws IndexOutOfBoundsException in case of a wrong arguments to the method
     */
    private boolean methodCallValidator(String line, String foundMethodName) throws IllegalScopeException,
            IndexOutOfBoundsException {
        boolean nameFlag = false, existenceFlag = false;
//        String foundMethodName = line.substring(methodNameMatcher.start(), methodNameMatcher.end());
        MethodScope foundMethod = null;
        for (MethodScope method : Sjavac.methodsArray) {
            if (foundMethodName.startsWith(method.getMethodName())) {
                nameFlag = true;
                foundMethod = method;
                break;
            }
        }
        if (nameFlag) {
            int numberOfArgs = foundMethod.methodParametersArray.size(), argsCounter = 0;
            //String[] args = line.split("[(),;\\s]");
            Matcher argumentMatcher = ARGUMENT_PATTERN.matcher(line);
            ArrayList<String> argsArray = new ArrayList<>();
            if (!argumentMatcher.find()) {
                if (foundMethod.methodParametersArray.size() == 0) {
                    return true;
                }
            } else {
                Matcher secArgumentMatcher = ARGUMENT_PATTERN.matcher(line);
                while (secArgumentMatcher.find()) {
                    String arg = line.substring(argumentMatcher.start(), argumentMatcher.end());
                    if ((!arg.startsWith(foundMethodName)) && (!arg.equals(""))) {
                        argsArray.add(arg);
                    }
                }
            }
            if (!foundMethod.reachableVariables.isEmpty()) {
                for (String input : argsArray) {
                    argsCounter++;
                    //to check for a call with existing variables
                    for (ArrayList<Variable> array : reachableVariables) {
                        if (array != null) {
                            for (Variable variable : array) {
                                if (variable.getName().equals(input)) {
                                    if ((variable.getValue() != null) || (!array.equals(
                                            foundMethod.methodParametersArray))) {
                                        existenceFlag = true;
                                    }
                                }
                            }
                        }
                        if (!existenceFlag) {
                            //check the call is done with the suitable types
                            if (!foundMethod.methodParametersArray.isEmpty()){
                                Variable suitableVariable = foundMethod.methodParametersArray.get(argsCounter - 1);
                                if (!suitableVariable.isValid(input)) {
                                    throw new IllegalScopeException("ERROR: malformed method args in " +
                                            foundMethod.getMethodName());
                                } else {
                                    existenceFlag = true;
                                }
                            }
                        }
                    }
                }
            }
            //checks for num of args validity
            if (argsArray.size() != numberOfArgs) {
                throw new IllegalScopeException("ERROR: malformed method call in " +
                        foundMethod.getMethodName());
            } else {
                //checks for option of assignment
                for (int index = 0; index < foundMethod.methodParametersArray.size(); index++) {
                    if (foundMethod.methodParametersArray.get(index).isValid(argsArray.get(index))) {
                        existenceFlag = true;
                        return existenceFlag;
                    }
                }
            }
        } else {
            throw new IllegalScopeException("ERROR: malformed method call in " + this.fatherMethod.getMethodName());
        }
        return existenceFlag;
    }

    /*
     * manages variables assignments
     *
     * @param assignmentLine the assignment line
     * @param scope          the scope in which there's the assignment
     * @return true iff the assignment is valid
     * @throws IllegalTypeException in case of wrong assignment
     */
    private boolean assignmentManager(String assignmentLine, Scope scope) throws IllegalTypeException,
            IndexOutOfBoundsException {
        boolean assignedFlag = false;
        Variable getterVariable, setterVariable;
        try {
            ArrayList<String> splatAssignment = assignmentSplitter(assignmentLine);
            for (int index = 0; index < (Math.floor(splatAssignment.size() / 2)); index++) {
                String variableName = splatAssignment.get(2 * index);
                String variableValue = splatAssignment.get(2 * index + 1);
                //searching for the variable there's an assignment to
                for (ArrayList<Variable> array : reachableVariables) {
                    if (array != null) {
                        for (Variable localVariable : array) {
                            if (localVariable.getName().equals(variableName)) {
                                getterVariable = localVariable;
                                if (!array.equals(this.fatherMethod.methodParametersArray)) {
                                    // the check if the variable is final and if the value is valid is done
                                    // in the setValue method
                                    if (Variable.nameValidator(variableValue)) {
                                        for (ArrayList<Variable> varArray : reachableVariables) {
                                            if (varArray != null) {
                                                for (Variable localVar : varArray) {
                                                    if (localVar.getName().equals(variableValue)) {
                                                        //if code gets here, everything went well
                                                        setterVariable = localVar;
                                                        getterVariable.setValue(setterVariable.getValue(),
                                                                variableName, this);
                                                        assignedFlag = true;
                                                    }
                                                }
                                            }
                                        }
                                        //if code gets here, everything went well
                                    } else {
                                        localVariable.setValue(variableValue, variableName, this);
                                        assignedFlag = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IllegalTypeException e) {
            throw new IllegalTypeException("ERROR: assignment problem in "+scope.fatherMethod.getMethodName());
        }
        return assignedFlag;
    }

    /*
     * splits variables-assignments-lines using regex's split function
     *
     * @param line the assignment lines
     * @return the string array of the splat line
     */
    private ArrayList<String> assignmentSplitter(String line) {
        String[] splat = line.split("[;,=\\s]");
        ArrayList<String> splatArray = new ArrayList<>();
        for (String note : splat) {
            if (!note.equals("")) {
                splatArray.add(note);
            }
        }
        return splatArray;
    }

}





