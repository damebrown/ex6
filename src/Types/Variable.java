package Types;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * the class represent a general variable object
 */
public abstract class Variable {
    private static final String STRING = "String";
    private static final String INT = "int";
    private static final String DOUBLE = "double";
    private static final String CHAR = "char";
    private static final String BOOLEAN = "boolean";
    private static final String FINAL = "final";
    private static final Pattern SEPARATOR_PATTERN = Pattern.compile("\\b\\w*\\b([ ]*=[ ]*((\\\"[^\"]*\\\")|([^ ,;]*)))*");
    private static final Pattern SPLITTER_PATTERN = Pattern.compile("(\\b\\w*\\b)[ ]*=[ ]*((\\\"[^\"]*\\\")|([^ ]*))");
    private static final Pattern NAME_PATTERN = Pattern.compile("(\\b(_\\w+|[^\\d_ ]\\w*)\\b)[ ]*(=[ ]*[^ ]*)*");
    private static final Pattern DECLARATION_PATTERN = Pattern.compile(
            "^[ ]*(final\\s*)?\\b(int|String|double|char|boolean)\\b[ ]+(\\b\\w*\\b)[ ]*(=[ ]*" +
                    "(([^ \\\"]*)|(\\\"[^\\\"]*\\\")))*[ ]*(,[ ]*(\\b\\w*\\b)[ ]*" +
                    "(=[ ]*(([^ \"]*)|(\\\"[^\\\"]*\\\")))*)*[ ]*;[ ]*$");


    /* Data members */

    protected boolean isFinal = false;
    protected boolean isGlobal;
    protected java.lang.String name;
    protected java.lang.String value;


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
    public static ArrayList<Variable> variableInstasiation(String declarationString,boolean isGlobal) throws
            IllegalTypeException {

        ArrayList<Variable> variablesInstances = new ArrayList<>();

        //verify declaration structure
        if(!declarationValidator(declarationString))
            throw new IllegalTypeException();

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
            // case it is not final
            else{
                isFinal = false;
                typeInput = variablesToCreate.get(0);
                variablesToCreate = variablesToCreate.subList(1,variablesToCreate.size());
            }

            Variable currVar = null;

            // run over variable signature and initialize it
            for(String varSignature : variablesToCreate){

                // verify the variable name is valid
                if (!nameValidator(varSignature)) {
                    throw new IllegalTypeException();
                }

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
                        currVar = new BooleanVariable(varSignature, isGlobal, isFinal);
                        break;
                }
                variablesInstances.add(currVar);
            }
        }
        return variablesInstances;
    }

    /**
     * the method verify if a given value is correct, based on the variable type demands.
     * @param value the value string to check
     * @return true if valid
     */
    public abstract boolean isValid(String value);



    public abstract void setValue(String value);

    /*
     *  the method receives variable name and verify it is valid according to
     *  (the given specification)
     * @param name the variable name (may include assignment in the string)
     * @return true if valid, false elsewhere.
     */
    private static boolean nameValidator(String name) {
        Matcher m = NAME_PATTERN.matcher(name);

        return m.find();
    }

    /**
     * the method verifies a declaration line is in the correct structure.
     * whether it has multiple variables or a single one.
     *
     * @param name
     * @return
     */
    public static boolean declarationValidator(String name) {
        Matcher m = DECLARATION_PATTERN.matcher(name);
        return m.find();
    }




    /*
     *the method receives a valid declaration line and separate it into sub array list sub string.
     * first cell is reserved to the declaration type. all other nodes are filled with variables.
     * @param declaration
     */
    private static ArrayList<String> variableSeparator(String declaration){

        Matcher match = SEPARATOR_PATTERN.matcher(declaration);
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

    /**
     *  The method is in charge of splitting variable assignment line into variable name string
     *  and value string
     * @param variableWithAssign the complete line to split
     * @return
     */
    protected static String[] splitter(String variableWithAssign){
        Matcher m = SPLITTER_PATTERN.matcher(variableWithAssign);

        String[] splitted = null;
        splitted = new String[2];

        if(m.find()){
            splitted[0] = m.group(1);
            splitted[1] = m.group(2);
        }
        return splitted;
    }
}

