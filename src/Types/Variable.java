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
    public static final String FINAL = "final";
    private static Pattern separatorPattern  = Pattern.compile("\\b\\w*\\b([ ]*=[ ]*((\\\"[^\"]*\\\")|([^ ,;]*)))*");
    private static Pattern splitterPattern = Pattern.compile("(\\b\\w*\\b)[ ]*=[ ]*(([^ ]*)|(\\\"[^\"]*\\\"))");


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
     *
     * @param declarationString
     * @param isGlobal
     * @return
     */
    public static ArrayList<Variable> variableInstasiation(String declarationString,boolean isGlobal) {

        ArrayList<Variable> variablesInstances = new ArrayList<>();

        //verify declaration structure
        if(!declarationValidator(declarationString))
            System.err.println("A bad declaration structure ");

        else{
            //prepare parameters
            String typeInput;
            List<String> variablesToCreate = variableSeparator(declarationString);
            boolean isFinal;

            //case it is final
            if(variablesToCreate.get(0).equals(FINAL)) {
                isFinal = true;
                typeInput = variablesToCreate.get(1);
                variablesToCreate = variablesToCreate.subList(2,variablesToCreate.size());
            }
            // cas it is not final
            else{
                isFinal = false;
                typeInput = variablesToCreate.get(0);
                System.out.println(variablesInstances.size());
//                List<String> temp = variablesToCreate.subList(1,variablesToCreate.size());
//                ArrayList<String> test = (ArrayList<String>)temp;
                variablesToCreate = variablesToCreate.subList(1,variablesToCreate.size());
            }

            Variable currVar = null;

            // run over variable signature and initialize it
            for(String varSignature : variablesToCreate){

                // verify the variable name is valid
                if (nameValidator(varSignature)) //todo exception for name validity
                    System.out.println("exception should be printed bad variable name");

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
        Pattern p = Pattern.compile("(\\b(_\\w+|[^\\d_ ]\\w*)\\b)[ ]*(=[ ]*[^ ]*)*");
        Matcher m = p.matcher(name);

        if(m.find())
            return true;
        return false;
    }

    /**
     * the method verifies a declaration line is in the correct structure.
     * whether it has multiple variables or a single one.
     *
     * @param name
     * @return
     */
    public static boolean declarationValidator(String name) {
        Pattern p = Pattern.compile(
                "^[ ]*(final )*[ ]*\\b(int|String|double|Char|boolean)\\b[ ]+(\\b\\w*\\b)[ ]*(=[ ]*" +
                        "((\\b\\w*\\b)|(\\\"[^\\\"]*\\\")))*[ ]*(,[ ]*(\\b\\w*\\b)[ ]*" +
                        "(=[ ]*(([^ \"]*)|(\\\"[^\\\"]*\\\")))*)*[ ]*;[ ]*$");
        Matcher m = p.matcher(name);
        if (m.find())
            return true;
        return false;
    }




    /*
     *the method receives a valid declaration line and separate it into sub array list sub string.
     * first cell is reserved to the declaration type. all other nodes are filled with variables.
     * @param declaration
     */
    public static ArrayList<String> variableSeparator(String declaration){

        Matcher match = separatorPattern.matcher(declaration);
        ArrayList<String> variableList = new ArrayList<>();

        String currentVar;
        //find all occurrences
        while(match.find()){
            currentVar = declaration.substring(match.start(), match.end());
            if(!currentVar.equals(""))
                variableList.add(currentVar);
        }
        return variableList;
    }


    public static String[] splitter(String variableWithAssign){
//        Pattern p = Pattern.compile("(\\b\\w*\\b)[ ]*=[ ]*((\\b\\w*\\b)|(\\\"[^\"]*\\\"))");
        Matcher m = splitterPattern.matcher(variableWithAssign);

        String[] splitted = null;
        splitted = new String[2];

        if(m.find()){
            splitted[0] = m.group(1);
            splitted[1] = m.group(2);
        }
        return splitted;
    }
}

