package oop.ex6.Scope;

import oop.ex6.Types.IllegalTypeException;
import oop.ex6.Types.Variable;
import oop.ex6.main.IllegalCodeException;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static oop.ex6.main.Sjavac.CLOSING_BRACKET_PATTERN;
import static oop.ex6.main.Sjavac.OPENING_BRACKET_PATTERN;
import static oop.ex6.main.Sjavac.VARIABLE_DECLARATION_PATTERN;

/**
 * the class represents a conditional scope object
 */
public class ConditionScope extends Scope{

    private MethodScope fatherMethod;

    private static Pattern BOOLEAN_PATTERN = Pattern.compile("^\\s*(if|while)\\s*[(]\\s*(true|false|\\w*" +
            "|[-]?\\d+(\\.?\\d+)*)\\s*(\\s*(((\\|){2}|(&&))\\s*(\\b\\w*\\b|[-]?\\d+(\\.?\\d+)*)\\s*)*)" +
            "\\s*[)]\\s*[{]\\s*");

    private final static Pattern DIGIT_PATTERN = Pattern.compile("(-?\\d+)(.\\d*)?+");
    private final static Pattern CONTENT_PARAM = Pattern.compile("(\\b\\w*\\b|[-]?\\d+(\\.?\\d+)*)");

    /**
     * condition scope constructor
     * @param arrayOfLines the scope lines
     * @param fatherScopeInput the parent scope
     * @param fatherMethodInput the father method
     */
    ConditionScope(ArrayList<String> arrayOfLines, Scope fatherScopeInput, MethodScope fatherMethodInput) throws IllegalScopeException {
        super();
        scopeLinesArray = arrayOfLines;
        fatherScope = fatherScopeInput;
        fatherMethod = fatherMethodInput;
        appendFatherScopeVariables();
        this.variableUpdater();
        this.checkCondition();
    }


    private void checkCondition() throws IllegalScopeException {
        String line = this.scopeLinesArray.get(0);
        Matcher conditionMatcher = BOOLEAN_PATTERN.matcher(line);
        if (!conditionMatcher.find()){
            throw new IllegalScopeException("ERROR: wrong brackets in condition scope");
        }
        if (!conditionContentValidator(line)){
            throw new IllegalScopeException("ERROR: condition argument is wrong");
        }
    }


    /*
     * the method add the upper scope variables
     */
    private void appendFatherScopeVariables(){
        if (!fatherScope.upperScopeVariables.isEmpty()){
            upperScopeVariables.addAll(fatherScope.upperScopeVariables);
        }
    }

    @Override
    /*
     *
     * @throws IllegalCodeException
     */
    public void scopeValidityManager() throws IllegalCodeException {
            if (!conditionValidityChecker()){
                throw new IllegalScopeException();
            }
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



    // add to condition class
    /*
     * the method verifies a given condition signature values are valid
     * @param conditionSignature
     * @return
     * @throws IllegalScopeException
     */
    private boolean conditionContentValidator(String conditionSignature) throws IllegalScopeException {

        Matcher booleanMatcher = CONTENT_PARAM.matcher(conditionSignature);
        // run over conditions
        String currentCond;
        while (booleanMatcher.find()) {
            currentCond = conditionSignature.substring(booleanMatcher.start(), booleanMatcher.end());
            Matcher digitMatcher = DIGIT_PATTERN.matcher(currentCond);
            if (!currentCond.equals("") && !currentCond.contains("if") && !currentCond.contains("while")) {
                //true or false or valid digit
                if (currentCond.equals("true") || currentCond.equals("false") || digitMatcher.find()) {
                    continue;
                }
                //case it is an assignment
                if (Variable.nameValidator(currentCond)) {
                    // run over reachable scopes (from the most specific one)
                    if (upperScopeVariables != null && !upperScopeVariables.isEmpty()) {
                        for (Variable var : upperScopeVariables) {
                            if (var.getName().equals(currentCond)) {
                                if (var.getValue() != null) {
                                    currentCond = var.getValue();
                                    return conditionContentValidator(currentCond);
                                } else
                                    throw new IllegalScopeException("ERROR: the given condition variable is null");
                            }
                        }

                    }
                    throw new IllegalScopeException("ERROR: variable is not declared");
                }
            }
        }
        return true;
    }

}

