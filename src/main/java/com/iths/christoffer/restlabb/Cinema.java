package com.iths.christoffer.restlabb;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity
public class Cinema {

    @GeneratedValue @Id private long id;
    private String name;
    private String city;
    private String adress;

    public Cinema(String name, String city, String adress) {
        this.name = name;
        this.city = city;
        this.adress = adress;
    }
}
