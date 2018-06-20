import Scope.*;

import java.util.ArrayList;

public class Tester {

    public Tester() throws IllegalScopeException {
        ArrayList<String> array = new ArrayList<>();
        array.add("void methodName(string \"hi\"){");
        MethodScope method = new MethodScope(array);
        System.out.println(method.getMethodName());
    }

    public static void main(String[] args) throws IllegalScopeException {
        Tester tester = new Tester();

    }
}

