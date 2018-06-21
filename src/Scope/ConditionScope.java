package Scope;

import Types.IllegalTypeException;
import main.IllegalCodeException;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.Sjavac.CLOSING_BRACKET_PATTERN;
import static main.Sjavac.OPENING_BRACKET_PATTERN;

public class ConditionScope extends Scope{

    private MethodScope fatherMethod;

    private static Pattern BOOLEAN_PATTERN = Pattern.compile("^\\s*(if|while)\\s*[(]\\s*(true|false|\\b\\w*" +
            "\\b|[-]?\\d+(\\.?\\d+)*)(\\s*(((\\|\\|)|(&&))\\s*(\\b\\w*\\b|[-]?\\d+(\\.?\\d+)*)\\s*)*)" +
            "\\s*[)][{]\\s*");

    ConditionScope(ArrayList<String> arrayOfLines, Scope fatherScopeInput, MethodScope fatherMethodInput){
        scopeLinesArray = arrayOfLines;
        fatherScope = fatherScopeInput;
        fatherMethod = fatherMethodInput;
        appendFatherScopeVariables();
    }

    public void conditionValidityManager() throws IllegalCodeException {
        if (conditionValidityChecker()){
            subScopesFactory(this, fatherMethod);
        } else {
            throw new IllegalScopeException();        }
    }


    private void appendFatherScopeVariables(){
        upperScopeVariables.addAll(fatherScope.localVariables);
    }


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

