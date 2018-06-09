package Scope;

import Types.Variable;

import java.util.*;

public abstract class Scope {

    /* Data members */

    /* The instance sub scopes */
    public ArrayList<Scope> subScopes;

    /* The Scope local variables */
    public ArrayList<Variable> localVariables;


    public Scope(ArrayList<Scope> subScopes, ArrayList<Variable> localVariables) {
        this.subScopes = subScopes;
        this.localVariables = localVariables;
    }
}
