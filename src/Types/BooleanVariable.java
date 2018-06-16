package Types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BooleanVariable extends Variable {

    public BooleanVariable(String variableString,boolean isGlobal,boolean isFinal) {

        super(isGlobal,isFinal);
        if(variableString.contains("=")){ //todo verify empty string wont get ere
            String[] toAssign = splitter(variableString);
            this.name = toAssign[0];
            if(isValid(toAssign[1])) {
                this.value = toAssign[1];
            }
            else
                System.out.println("not good, do exception");
        }
        else{
            this.name = variableString;
        }
    }

    public static boolean isValid(String value){
        Pattern p = Pattern.compile("((-?\\d+(.\\d*)?+)|true|false)");  //todo is 093 valid?
        Matcher m = p.matcher(value);

        if(m.matches())
            return true;
        return false;
    }
}
