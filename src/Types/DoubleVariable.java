package Types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DoubleVariable extends Variable {

    private final static Pattern VALIDITY_PATTERN = Pattern.compile("(-?\\d+)(.\\d*)?+");  //todo is 093 valid?

    public DoubleVariable(String variableString,boolean isGlobal,boolean isFinal) throws IllegalTypeException {

        super(isGlobal,isFinal);
        type="double";
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
        Matcher doubleMatcher = VALIDITY_PATTERN.matcher(value);

        return doubleMatcher.matches();
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
        }throw new IllegalTypeException("Illegal type, should be double type value");
    }
}
