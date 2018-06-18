package Types;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharVariable extends Variable {

    /**
     *
     * @param variableString
     * @param isGlobal
     * @param isFinal
     */
    public CharVariable(String variableString,boolean isGlobal,boolean isFinal) {

        super(isGlobal,isFinal);
        if(variableString.contains("=")){
            String[] toAssign = splitter(variableString);
            this.name = toAssign[0];
            if(isValid(toAssign[1])) {
                this.value = toAssign[1];
            }
            else
                System.out.println("not good, not  valid char value ");
        }
        else{
            this.name = variableString;
        }
    }

    public static boolean isValid(String value){
        Pattern p = Pattern.compile("[\\\"\\\'].[\\\"\\\']"); //todo should include "" ?
        Matcher m = p.matcher(value);

        if(m.find())
            return true;
        return false;
    }
}
