package Scope;

import Types.IllegalTypeException;

import java.util.ArrayList;

public class ConditionScope extends Scope{


    ConditionScope(ArrayList<String> arrayOfLines, Scope fatherScopeInput){
        scopeLinesArray = arrayOfLines;
        fatherScope = fatherScopeInput;
    }

    public void conditionValidityManager() throws IllegalScopeException, IllegalTypeException {
        if (conditionValidityChecker()){
            appendFatherScopeVariables();
            subScopesFactory(this);
        } else {
            throw new IllegalScopeException();        }
    }


    private void appendFatherScopeVariables(){
        upperScopeVariables.addAll(fatherScope.localVariables);
    }


    private boolean conditionValidityChecker(){
        //TODO check that conditions are valid, used parameters are valid,
        return true;
    }

}
//TODO note to self- because the method instance has all the method and the condition scope instance has only
//the relevant scope, the validity check of all of the method's structure will be done in the method scope's
//class, while in the condition scope class will be done only the validity of variable usage and the condition's
//validity
