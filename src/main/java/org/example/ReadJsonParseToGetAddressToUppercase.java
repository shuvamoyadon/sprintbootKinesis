package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bean.Person;
import org.example.operation.NewAddress;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


// In this example, learning is to read the json file and parse it . Get the address details and pass it to interface method
// of another class where the method is defined and return the list to cpature all the address to upper case from that class method
public class ReadJsonParseToGetAddressToUppercase {
    public static void main(String[] args) throws IOException {

        ObjectMapper obj = new ObjectMapper();
        List<Person> personList = obj.readValue(new File("/Users/shuvamoy/Documents/mylearning/src/main/resources/test.json"), new TypeReference<List<Person>>() {});

        System.out.println(personList);
        List<Person.Address> lstAddress = new ArrayList<>();

        for(Person person: personList) {
            System.out.println(person);
            lstAddress.add(person.getAddress());
        }

        //System.out.println(lstAddress);
//
//
        NewAddress newaddress = new NewAddress();
        System.out.println(newaddress.getNewAddress(lstAddress));
    }
}