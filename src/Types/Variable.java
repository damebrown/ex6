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


    public Variable(){}

    /**
     * a super constructor
     */
    public Variable(boolean isGlobal, boolean isFinal) {
        this.isGlobal = isGlobal;
        this.isFinal = isFinal;
    }

    /**
     * this method gets a line in which there's a decleration of a a variable, and calls the relevant
     * consturctors upon it
     * @param declarationString the line of declaration
     * @param isGlobal boolean, true if a global variable
     * @return arraylist of the instanced variables
     */
    public static ArrayList<Variable> variableInstasiation(String declarationString,boolean isGlobal) {

        ArrayList<Variable> variablesInstances = new ArrayList<>();

        //verify declaration structure
        if(!declarationValidator(declarationString))
            System.err.println("A bad declaration structure ");

        else{
            // todo handle final type bugs
            ArrayList<String> variablesToCreate = variableSeparator(declarationString);
            boolean isFinal = true;
            String typeInput = "int"; //to do
            Variable currVar = null;

            // run over variable signature and initialize it
            for(String varSignature : variablesToCreate){

                // verify the variable name is valid
                if (nameValidator(varSignature)) //todo exception for name validity
                    System.out.println("exception should be printed");

                // create the variable instance
                switch (typeInput) {
                    case STRING:
                        currVar = new StringVariable(varSignature, isGlobal, isFinal);
                        break;
                    case INT:
                        currVar = new IntVariable(varSignature, isGlobal, isFinal);
                        break;
                    case DOUBLE:
                        currVar = new DoubleVariable(varSignature, isGlobal, isFinal);
                        break;
                    case CHAR:
                        currVar = new CharVariable(varSignature, isGlobal, isFinal);
                        break;
                    case BOOLEAN:
                        currVar = new IntVariable(varSignature, isGlobal, isFinal);
                        break;
                }
                variablesInstances.add(currVar);
            }
        }
        return variablesInstances;
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
    public static boolean declarationValidator(String name) {
        Pattern p = Pattern.compile(
                "^[ ]*(final )*[ ]*\\b(int|String|double|Char|boolean)\\b[ ]+(\\b\\w*\\b)[ ]*(=[ ]*((\\b\\w*\\b)|" +
                        "(\\\"[^\"]*\\\")))*[ ]*(,[ ]*(\\b\\w*\\b)" +
                        "[ ]*(=[ ]*((\\b\\w*\\b)|(\\\"[^\"]*\\\")))*)*[ ]*;[ ]*$");
        Matcher m = p.matcher(name);
        if (m.find())
            return true;
        return false;
    }



    //todo final and type problem
    /*
     *the method receives a valid declaration line and separate it into sub array list sub string.
     * first cell is reserved to the declaration type. all other nodes are filled with variables.
     * @param declaration
     */
    private static ArrayList<String> variableSeparator(String declaration){
        Pattern variablePattern = Pattern.compile("(\\b\\w+\\b([ ]*=[ ]*\\b\\w+\\b)*)");
        Matcher match = variablePattern.matcher(declaration);
        ArrayList<String> variableTable = new ArrayList<>();

        String type;

        if (match.find()) {
            type = declaration.substring(match.start(), match.end());
            System.out.println(type);
            variableTable.add(type);
        }
        String currentVar;
        while(match.find()){
            currentVar = declaration.substring(match.start(), match.end());
            variableTable.add(currentVar);
        }
//
//        for(String str : variableTable)
//            System.out.println(str);
        return variableTable;
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

