package org.example.operation;

import org.example.function.ITestInterfaceInstanceCration;

public class InterfaceClass implements ITestInterfaceInstanceCration {

    @Override
    public void testInstanceCreate() {
       System.out.println("I am able to create instance of an interface");
    }
}
