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

    protected static Pattern ASSIGNMENT_PATTERN = Pattern.compile("^\\s*\\b\\w*\\b\\s*=\\s*(\\b\\w*\\b|[-]?\\d+(\\.?\\d+)|(\"[^\"]*\")|(\'.\'))\\s*;\\s*$");



    public Scope(){}


    //TODO check that method calls inside a scope are valid

    /**
     * makes all the scopes instances inside the received upmost scope instance
     * @param upmostScope the scope to search scopes in
     */
    protected void subScopesFactory(Scope upmostScope) throws IllegalCodeException {
        Scope fatherScope=upmostScope, currentScope=null;
        for (String line : scopeLinesArray){
            Matcher closingMatcher = CLOSING_BRACKET_PATTERN.matcher(line),
                    openingMatcher = OPENING_BRACKET_PATTERN.matcher(line),
                    variableDeclarationMatcher = VARIABLE_DECLARATION_PATTERN.matcher(line);
            if (currentScope==null){
                if (closingMatcher.find()){
                    return;
                } else if (variableDeclarationMatcher.find()){
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
                currentScope.scopeLinesArray.add(line);
                if (!closingMatcher.find()) {
                    if (!openingMatcher.find()){
                        if (variableDeclarationMatcher.find()){
                            ArrayList<Variable> variables = Variable.variableInstasiation(line, false);
                            currentScope.localVariables.addAll(variables);
                        }
                    }
                } else {
                    ((ConditionScope) currentScope).conditionValidityManager();
                    currentScope = fatherScope;
                }
            }
        }
    }
}





