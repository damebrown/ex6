package Types;

import java.util.*;

public abstract class Variable {

    /* Data members */

    private boolean isFinal = false;
    private boolean isGlobal;
    private java.lang.String Type;
    private java.lang.String name;
    private java.lang.String value;

    public Variable(java.lang.String type, java.lang.String name, java.lang.String value) {
        Type = type;
        this.name = name;
        this.value = value;
    }

    Variable(){}
}

