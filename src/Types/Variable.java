package Types;

import main.Sjavac;
import Scope.*;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static main.Sjavac.globalVariablesArray;

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
                    "(=[ ]*(([^ \"]*)|(\\\"[^\\\"]*\\\")))*)*[ ]*;?[ ]*$");


    /* Data members */

    public boolean isFinal = false;
    protected boolean isGlobal;
    protected java.lang.String name;
    protected java.lang.String value;
    protected static java.lang.String type;


    public Variable(){}

    /**
     * a super constructor
     */
    public Variable(String variableString, boolean isGlobal, boolean isFinal) throws IllegalTypeException {
        this.isGlobal = isGlobal;
        this.isFinal = isFinal;
        if(variableString.contains("=")){
            String[] toAssign = splitter(variableString);
            if (globalDuplicateChecker(toAssign[0])){
                this.name = toAssign[0];
            } else {
                throw new IllegalTypeException("ERROR: unvacant global variable name assignment");
            }
            if(isValid(toAssign[1])) {
                this.value = toAssign[1];
            } else {
                throw new IllegalTypeException("ERROR: wrong "+getName()+" variable assignment");
            }
        } else if (globalDuplicateChecker(variableString)){
            this.name = variableString;
        } else {
            throw new IllegalTypeException("ERROR: unvacant global variable name assignment");
        }
    }

    boolean globalDuplicateChecker(String name){
        for (Variable global:globalVariablesArray){
            if (global.getName().equals(name)){
                return false;
            }
        } return true;
    }

    public String getName(){
        return name;
    }

    public String getType(){
        return type;
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

                //check if var exist, replace value if exist
                String existingVar = Variable.referenceAssign(varSignature);
                if (!existingVar.equals(""))
                    varSignature = existingVar;



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



    public void setValue(String assignValue) throws IllegalTypeException{
        try {
            String varToAssign = Variable.referenceAssign(assignValue);
            if(!varToAssign.equals(""))
                assignValue = varToAssign;
            if(isValid(assignValue)) {
                if(!this.isFinal) {
                    this.value = assignValue;
                } else {
                    throw new IllegalTypeException("ERROR: Value cannot be assigned into final variable");
                }
            } else {
                throw new IllegalTypeException("ERROR: Illegal type, should be "+getType()+" type value");
            }
        } catch (IllegalTypeException e){
            throw new IllegalTypeException("ERROR: wrong variable assignment");
        }
    }

    /*
     *  the method receives variable name and verify it is valid according to
     *  (the given specification)
     * @param name the variable name (may include assignment in the string)
     * @return true if valid, false elsewhere.
     */
    public static boolean nameValidator(String name) {
        Matcher nameMatcher = NAME_PATTERN.matcher(name);

        return nameMatcher.find();
    }

    /**
     * the method verifies a declaration line is in the correct structure.
     * whether it has multiple variables or a single one.
     *
     * @param name
     * @return
     */
    public static boolean declarationValidator(String name) {
        Matcher declarationMatcher = DECLARATION_PATTERN.matcher(name);
        return declarationMatcher.find();
    }


    public String getValue() {
        return value;
    }

    protected boolean existanceValidity(String nameToCheck) throws IllegalTypeException {
        for(Variable var : globalVariablesArray) {
            if(var.getName().equals(nameToCheck)){
                // case it is global and global
                if (this.isGlobal && var.isGlobal)
                    throw new IllegalTypeException("cannot name two globals with the same name!");
                // case it is global and local
                else if (!this.isGlobal && var.isGlobal )
                    return true;
            }
            //todo add local and local case?

            // case it is on the same scope?
        }

        return true; //todo edit
    }

    protected static String referenceAssign(String variableAssignment) throws IllegalTypeException {
        String newAssign = "";
        if (variableAssignment.contains("=")) {
            String[] splitLine = splitter(variableAssignment);

            for (Variable var : globalVariablesArray) {
                if (var.getName().equals(splitLine[1])) {
                    newAssign = splitLine[0] + "=" + var.getValue();
                }
            }
        }

        return newAssign;


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
        Matcher splitterMatcher = SPLITTER_PATTERN.matcher(variableWithAssign);

        String[] splitted = null;
        splitted = new String[2];

        if(splitterMatcher.find()){
            splitted[0] = splitterMatcher.group(1);
            splitted[1] = splitterMatcher.group(2);
        }
        return splitted;
    }
}

