package org.example;


import org.example.function.ITestInterfaceInstanceCration;
import org.example.operation.InterfaceClass;

import java.io.IOException;

// you can not directly create an instance of an interface because an interface is an abstract type.
//You can also create an instance of an interface using an anonymous class or the class that implements interface
public class CreatingInstanceForInterface{

    public static void main(String[] args) throws IOException {
        // Option 1. Now I am creating an instance of an class which implements the interface
        ITestInterfaceInstanceCration obj = new InterfaceClass();
        obj.testInstanceCreate();

        // Option 2. creating instance using anonymous class
        ITestInterfaceInstanceCration intr = new ITestInterfaceInstanceCration() {
            @Override
            public void testInstanceCreate() {
                System.out.println("I am able to create instance of an interface using anonymous class");
            }
        };
        intr.testInstanceCreate();

    }
}

