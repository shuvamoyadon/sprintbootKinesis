package org.example.function;

import org.example.bean.Person;

import java.util.List;

public interface IAddress {
    public List<String> getNewAddress(List<Person.Address> lstAdress);

}
