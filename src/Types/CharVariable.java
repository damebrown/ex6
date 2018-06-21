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
                throw new IllegalTypeException("ERROR: wrong "+getName()+" variable assignment");
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
    public void setValue(String assignValue) throws IllegalTypeException {
        String varToAssign = Variable.referenceAssign(assignValue);

        if(!varToAssign.equals(""))
            assignValue = varToAssign;
        if(isValid(assignValue)) {
            if(!this.isFinal) {
                this.value = assignValue;
            }throw new IllegalTypeException("Value cannot be assigned into final variable");
        }throw new IllegalTypeException("Illegal type, should be char type value");
    }
}
