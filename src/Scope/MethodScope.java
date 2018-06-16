package Scope;

import main.Sjavac;

import java.util.ArrayList;
import main.Sjavac.*;


public class MethodScope extends Scope {

    /*Constants*/
    private final String methodName=null;

    public MethodScope(ArrayList<String> arrayOfLines){
        fatherScope=null;
        if (methodValidityCheck()){
            subScopesFactory(this);
            upperScopeVariables = Sjavac.globalVariablesArray;
        } else {
            //raise exception
        }
    }
//TODO IMPORTANT!
// In a case of an un-initialized global variable (meaning it is not assigned a value anywhere
//outside a method), all methods may refer to it (regardless of their location in relation to its
//declaration), but every method using it (in an assignment, as an argument to a method call)
//must first assign a value to the global variable itself (even if it was assigned a value in some
//other method).

    private boolean methodValidityCheck(){
        //TODO check for: return statement, (equal number of opening and closing brackets?), validity of
        //parameters, validity of name,
        //todo lemamesh method name assignment!
        //todo lemamesh method-call-parameters-validity check
        return true;
    }

    public String getMethodName(){
        return methodName;
    }
}
