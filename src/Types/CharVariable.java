package Types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharVariable extends Variable {

    private final static Pattern VALIDITY_PATTERN = Pattern.compile("[\\\"\\\'].[\\\"\\\']"); //todo should include "" ?

    /**
     *
     * @param variableString
     * @param isGlobal
     * @param isFinal
     */
    public CharVariable(String variableString,boolean isGlobal,boolean isFinal) throws IllegalTypeException {

        super(isGlobal,isFinal);
        type="char";
        if(variableString.contains("=")){
            String[] toAssign = splitter(variableString);
            this.name = toAssign[0];
            if(isValid(toAssign[1])) {
                this.value = toAssign[1];
            }
            else
                throw new IllegalTypeException();
        }
        else{
            this.name = variableString;
        }
    }
    @Override
    public  boolean isValid(String value){
        Matcher charMatcher = VALIDITY_PATTERN.matcher(value);

        if(charMatcher.find())
            return true;
        return false;
    }
    @Override
    public void setValue(String value) {
        if(isValid(value))
            this.value =value;
    }
}
