package org.example.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class Person {

  private String name;
  private String age;
  private Address address;

  @Data
  @Setter
  @Getter
  public static class Address {
    @JsonProperty("street")
    private String street;
    @JsonProperty("city")
    private String city;

    @JsonProperty("zip")
    private String zip;

    @Override
    public String toString() {
      return "Address [street=" + street + ", city=" + city + ", zip=" + zip + "]";
    }

  }

  @Override
  public String toString() {
    return "Person [name=" + name + ", age=" + age + ", address=" + address + "]";
  }

}
