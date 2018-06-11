package Types;

import java.util.*;

public abstract class Variable {
    public static final String STRING = "String";
    public static final String INT = "int";
    public static final String DOUBLE = "double";
    public static final String CHAR = "char";
    public static final String BOOLEAN = "boolean";

    /* Data members */

    private boolean isFinal = false;
    private boolean isGlobal;
    private java.lang.String Type;
    private java.lang.String name;
    private java.lang.String value;
    private static String[] typeStrings={STRING, INT, DOUBLE, CHAR, BOOLEAN};

    Variable(){}

    public static Variable globalVariableInstasiation(String declarationLine){
        Variable variable = null;
        for (String type: typeStrings){
            if (declarationLine.startsWith(type)){
                switch (type){
                    case STRING:
                        variable = new StringVariable(declarationLine);
                        break;
                    case INT:
                        variable = new IntVariable(declarationLine);
                        break;
                    case DOUBLE:
                        variable = new DoubleVariable(declarationLine);
                        break;
                    case CHAR:
                        variable = new CharVariable(declarationLine);
                        break;
                    case BOOLEAN:
                        variable = new IntVariable(declarationLine);
                        break;
                }
            }
        } return variable;
    }
}

