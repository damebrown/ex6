package Types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class StringVariable extends Variable {

    private final static Pattern VALIDITY_PATTERN = Pattern.compile("[\\\"\\\'].*[\\\"\\\']");  //todo include "" ?



    public StringVariable(String variableString, boolean isGlobal, boolean isFinal) throws IllegalTypeException {

        super(isGlobal, isFinal);
        type = "String";
        if (variableString.contains("=")) {
            String[] toAssign = splitter(variableString);

            this.name = toAssign[0];
            if (isValid(toAssign[1])) {
                this.value = toAssign[1];
            } else
                throw new IllegalTypeException();
        } else {
            this.name = variableString;
        }
    }
    @Override
    public  boolean isValid(String value) {
        Matcher stringMatcher = VALIDITY_PATTERN.matcher(value);

        return stringMatcher.matches();
    }

    @Override
    public void setValue(String value) {
        if(isValid(value))
            this.value =value;
    }

}
