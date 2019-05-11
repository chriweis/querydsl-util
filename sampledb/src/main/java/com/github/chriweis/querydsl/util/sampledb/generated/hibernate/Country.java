package com.github.chriweis.querydsl.util.sampledb.generated.hibernate;
// Generated May 11, 2019 11:51:36 PM by Hibernate Tools 5.3.7.Final


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Country generated by hbm2java
 */
@Entity
@Table(name = "COUNTRY"
        , schema = "PUBLIC"
        , catalog = "SAMPLEDB"
)
public class Country implements java.io.Serializable {


    private long id;
    private String name;
    private Set<Address> addresses = new HashSet<Address>(0);

    public Country() {
    }


    public Country(long id) {
        this.id = id;
    }

    public Country(long id, String name, Set<Address> addresses) {
        this.id = id;
        this.name = name;
        this.addresses = addresses;
    }

    @Id


    @Column(name = "ID", unique = true, nullable = false)
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }


    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "country")
    public Set<Address> getAddresses() {
        return this.addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }


}


