package Types;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Variable {
    public static final String STRING = "String";
    public static final String INT = "int";
    public static final String DOUBLE = "double";
    public static final String CHAR = "char";
    public static final String BOOLEAN = "boolean";

    /* Data members */

    protected boolean isFinal = false;
    protected boolean isGlobal;
    protected java.lang.String Type; //todo why?
    protected java.lang.String name;
    protected java.lang.String value;
//    private static String[] typeStrings={STRING, INT, DOUBLE, CHAR, BOOLEAN};


    public Variable() {}

    /**
     * a super constructor
     */
    public Variable(boolean isGlobal, boolean isFinal) {
        this.isGlobal = isGlobal;
        this.isFinal = isFinal;
    }

    /**
     * the method receives variable type, declaration string, isGlobal and isFinal flags and
     * return the matching variable
     * @param typeInput the given type
     * @param variableString the variable string
     * @param isGlobal global flag
     * @param isFinal final flag
     * @return the required variable instance
     */
    public static Variable variableInstasiation(String typeInput, String variableString, boolean isGlobal,
                                                boolean isFinal) {
        Variable variable = null;

//        for (String type: typeStrings){
        if (nameValidator(variableString)) //todo exception for name validity
            System.out.println("exception should be printed");
            switch (typeInput) {
                case STRING:
                    variable = new StringVariable(variableString, isGlobal, isFinal);
                    break;
                case INT:
                    variable = new IntVariable(variableString, isGlobal, isFinal);
                    break;
                case DOUBLE:
                    variable = new DoubleVariable(variableString, isGlobal, isFinal);
                    break;
                case CHAR:
                    variable = new CharVariable(variableString, isGlobal, isFinal);
                    break;
                case BOOLEAN:
                    variable = new IntVariable(variableString, isGlobal, isFinal);
                    break;
            }
        return variable;
    }

    /*
     *  the method receives variable name and verify it is valid according to
     *  (the given specification)
     * @param name the variable name (may include assignment in the string)
     * @return true if valid, false elsewhere.
     */
    private static boolean nameValidator(String name) {
        Pattern p = Pattern.compile("(\\b(_\\w+|[^\\d_ ]\\w+)\\b)([ ]*=[ ]*\\b\\w*\\b)*");
        Matcher m = p.matcher(name);

        if (m.find())
            return true;
        return false;
    }

    /**
     * the method verifies a declartion line is in the correct structure.
     * whether it has multiple variables or a single one.
     *
     * @param name
     * @return
     */
    private static boolean declarationValidator(String name) {
        Pattern p = Pattern.compile("^[ ]*\\b(int|boolean|String|char|double)\\b[ ]*(\\b\\w*\\b[ ]*([ ]*=[ ]*\\b\\w*\\b)?)[ ]*([ ]*,[ ]*\\b\\w*\\b[ ]*(=[ ]*\\b\\w*\\b)?)*[ ]*;$");
        Matcher m = p.matcher(name);
        if (m.find())
            return true;
        return false;
    }

    public static String[] splitter(String variableWithAssign){
        Pattern p = Pattern.compile("[ ]*(\\b\\w*\\b)[ ]*=[ ]*(.*)$");
        Matcher m = p.matcher(variableWithAssign);

        String[] splitted;
        splitted = new String[2];

        if(m.find()){
            splitted[0] = m.group(1);
            splitted[1] = m.group(2);
        }
        return splitted;

    }

}

