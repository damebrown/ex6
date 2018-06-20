package Scope;

import Types.IllegalTypeException;
import main.IllegalCodeException;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionScope extends Scope{


    private static Pattern BOOLEAN_PATTERN = Pattern.compile("^\\s*(if|while)[(]\\s*(true|false|\\b\\w*\\b|[-]" +
            "?\\d+(\\.?\\d+))(\\s(((\\|\\|)|(\\&\\&))\\s*(\\b\\w*\\b|[-]?\\d+(\\.?\\d+))\\s))\\s[)][{]\\s*");

    ConditionScope(ArrayList<String> arrayOfLines, Scope fatherScopeInput){
        scopeLinesArray = arrayOfLines;
        fatherScope = fatherScopeInput;
    }

    public void conditionValidityManager() throws IllegalCodeException {
        if (conditionValidityChecker()){
            appendFatherScopeVariables();
            subScopesFactory(this);
        } else {
            throw new IllegalScopeException();        }
    }


    private void appendFatherScopeVariables(){
        upperScopeVariables.addAll(fatherScope.localVariables);
    }


    private boolean conditionValidityChecker() throws IllegalScopeException {
        //TODO check that conditions are valid, used parameters are valid,
        for (String line: scopeLinesArray){
            if (line.equals(scopeLinesArray.get(0))){
                Matcher conditionMatcher = BOOLEAN_PATTERN.matcher(line);
                    if (!conditionMatcher.find()){
                        throw new IllegalScopeException("ERROR: wrong boolean condition in for/while loop");
                    }
                }
            }
        return true;
        }



}
//TODO note to self- because the method instance has all the method and the condition scope instance has only
//the relevant scope, the validity check of all of the method's structure will be done in the method scope's
//class, while in the condition scope class will be done only the validity of variable usage and the condition's
//validity
