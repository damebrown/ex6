package Scope;

import Types.IllegalTypeException;
import Types.Variable;
import main.Sjavac;

import java.util.*;
import java.util.regex.Matcher;
import static main.Sjavac.*;



public abstract class Scope {

    /* Data members */
    /* The scope's lines in the code */
    protected ArrayList<String> scopeLinesArray;
    /* The Scope local variables */
    protected ArrayList<Variable> localVariables;
    /* The Scope's upper scope */
    protected Scope fatherScope;
    /* the local variables of the upper scope */
    protected ArrayList<Variable> upperScopeVariables;

    public Scope(){}


//    protected void scopeVariableFactory() throws IllegalTypeException {
//        int openingCounter=0, closingCounter=0;
//        for (String line : scopeLinesArray){
//            Matcher closingMatcher = CLOSING_BRACKET_PATTERN.matcher(line),
//                    openingMatcher = OPENING_BRACKET_PATTERN.matcher(line),
//                    globalVariableMatcher = VARIABLE_DECLARATION_PATTERN.matcher(line);
//            if(!line.equals(scopeLinesArray.get(0))){
//                break;
//            } else if (openingMatcher.find()){
//                openingCounter++;
//            } else if (closingMatcher.find()){
//                closingCounter++;
//            } else if (){
//                if (globalVariableMatcher.find()){
//                    ArrayList<Variable> variables = Variable.variableInstasiation(line, false);
//                    localVariables.addAll(variables);
//                }
//            }
//        }
//    }

    //TODO check that method calls inside a scope are valid

    /**
     * makes all the scopes instances inside the received upmost scope instance
     * @param upmostScope the scope to search scopes in
     */
    protected void subScopesFactory(Scope upmostScope) throws IllegalScopeException, IllegalTypeException {
        Scope fatherScope=upmostScope, currentScope=null;
        for (String line : scopeLinesArray){
            Matcher closingMatcher = CLOSING_BRACKET_PATTERN.matcher(line),
                    openingMatcher = OPENING_BRACKET_PATTERN.matcher(line),
                    globalVariableMatcher = VARIABLE_DECLARATION_PATTERN.matcher(line);
            if (currentScope==null){
                if (globalVariableMatcher.find()){
                    ArrayList<Variable> variables = Variable.variableInstasiation(line, false);
                    fatherScope.localVariables.addAll(variables);
                }
            } if (openingMatcher.find()){
                if (!scopeLinesArray.get(0).equals(line)){
                    ArrayList<String> subScopeLinesArray = new ArrayList<>();
                    subScopeLinesArray.add(line);
                    currentScope = new ConditionScope(subScopeLinesArray, fatherScope);
                }
            } else if (currentScope!=null) {
                if (!closingMatcher.find()) {
                    currentScope.scopeLinesArray.add(line);
                    if (!openingMatcher.find()){
                        if (globalVariableMatcher.find()){
                            ArrayList<Variable> variables = Variable.variableInstasiation(line, false);
                            currentScope.localVariables.addAll(variables);
                        } else {
                            ((ConditionScope) currentScope).conditionValidityManager();
                            currentScope = fatherScope;
                        }
                    }
                }
            }
        }
    }


}





