package oop.ex6.Scope;

import oop.ex6.Types.IllegalTypeException;
import oop.ex6.main.IllegalCodeException;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static oop.ex6.main.Sjavac.CLOSING_BRACKET_PATTERN;
import static oop.ex6.main.Sjavac.OPENING_BRACKET_PATTERN;

/**
 * the class represents a conditional scope object
 */
public class ConditionScope extends Scope{

    private MethodScope fatherMethod;

    private static Pattern BOOLEAN_PATTERN = Pattern.compile("^\\s*(if|while)\\s*[(]\\s*(true|false|\\b\\w*" +
            "\\b|[-]?\\d+(\\.?\\d+)*)(\\s*(((\\|\\|)|(&&))\\s*(\\b\\w*\\b|[-]?\\d+(\\.?\\d+)*)\\s*)*)" +
            "\\s*[)][{]\\s*");

    /**
     * condition scope constructor
     * @param arrayOfLines the scope lines
     * @param fatherScopeInput the parent scope
     * @param fatherMethodInput the father method
     */
    ConditionScope(ArrayList<String> arrayOfLines, Scope fatherScopeInput, MethodScope fatherMethodInput){
        scopeLinesArray = arrayOfLines;
        fatherScope = fatherScopeInput;
        fatherMethod = fatherMethodInput;
        appendFatherScopeVariables();
        this.variableUpdater();
    }
    //todo what?
    /*
     *
     * @throws IllegalCodeException
     */
    public void conditionValidityManager() throws IllegalCodeException {
        if (conditionValidityChecker()){
            subScopesFactory(this, fatherMethod);
        } else {
            throw new IllegalScopeException();        }
    }

    /*
     * the method add the upper scope variables
     */
    private void appendFatherScopeVariables(){
        upperScopeVariables.addAll(fatherScope.localVariables);
    }

    /*
     * the method verify a given scope structure is valid
     * @return true if valid
     * @throws IllegalScopeException
     * @throws IllegalTypeException
     */
    private boolean conditionValidityChecker() throws IllegalScopeException, IllegalTypeException {
        for (String line: scopeLinesArray){
            Matcher closingMatcher = CLOSING_BRACKET_PATTERN.matcher(line),
                    openingMatcher = OPENING_BRACKET_PATTERN.matcher(line);
            if (line.equals(scopeLinesArray.get(0))){
                Matcher conditionMatcher = BOOLEAN_PATTERN.matcher(line);
                if (!openingMatcher.find()){
                    throw new IllegalScopeException("ERROR: wrong brackets in condition scope");
                } if (!conditionMatcher.find()){
                    throw new IllegalScopeException("ERROR: wrong boolean condition in for/while loop");
                }
            } else if (line.equals(scopeLinesArray.get(scopeLinesArray.size()-1))){
                if (!closingMatcher.matches()){
                    throw new IllegalScopeException("ERROR: wrong brackets in condition scope");
                }
            } else {
                scopeValidityHelper(line, this);
            }
        }
        return true;
    }

}

