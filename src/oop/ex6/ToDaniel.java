package oop.ex6;
import java.util.regex.*;
import oop.ex6.Scope.IllegalScopeException;
public class ToDaniel {


    // add to top of the condition class
    private final static Pattern DIGIT_PATTERN = Pattern.compile("(-?\\d+)(.\\d*)?+");
    private final static Pattern CONTENT_PARAM = Pattern.compile("(\\b\\w*\\b|[-]?\\d+(\\.?\\d+)*)");



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
                if (currentCond.equals("true") || currentCond.equals("false") || digitMatcher.find())
                    continue;

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
