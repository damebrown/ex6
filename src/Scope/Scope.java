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
    /* The Scope local variables */
    protected ArrayList<Variable> localVariables;
    /* The Scope's upper scope */
    protected Scope fatherScope;
    /* the local variables of the upper scope */
    protected ArrayList<Variable> upperScopeVariables;




    public Scope(){}


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
    protected void subScopesFactory(Scope upmostScope) throws IllegalCodeException {
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
                        currentScope = new ConditionScope(subScopeLinesArray, fatherScope);
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
                    currentScope = new ConditionScope(subScopeLinesArray, fatherScope);
                } else {
                    currentScope.scopeLinesArray.add(line);
                }
            }
        } if (currentScope!=upmostScope){
            throw new IllegalScopeException("ERROR: malform method structure");
        }
    }
}





