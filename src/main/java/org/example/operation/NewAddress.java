package org.example.operation;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.bean.Person;
import org.example.function.IAddress;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
public class NewAddress implements IAddress {

    private static int idCounter = 0;
    public NewAddress(){

    }


    public static String getNewId() {
        return String.valueOf(++idCounter);
    }

    @Override
    public List<String> getNewAddress(List<Person.Address> lst) {
        System.out.println(lst);
        List<String> addressList = new ArrayList<>();
        for(Person.Address adr: lst){

            System.out.println(adr);
            addressList.add(getNewId());
            addressList.add(adr.getStreet().toUpperCase());
            addressList.add(adr.getCity().toUpperCase());
        }
        return addressList;
    }
}
