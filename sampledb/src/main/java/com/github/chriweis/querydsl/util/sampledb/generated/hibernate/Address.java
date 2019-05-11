package com.github.chriweis.querydsl.util.sampledb.generated.hibernate;
// Generated May 11, 2019 11:51:36 PM by Hibernate Tools 5.3.7.Final


import javax.persistence.*;

/**
 * Address generated by hbm2java
 */
@Entity
@Table(name="ADDRESS"
    ,schema="PUBLIC"
    ,catalog="SAMPLEDB"
)
public class Address  implements java.io.Serializable {


    private long id;
    private Country country;
     private Person person;
     private String value;

    public Address() {
    }


    public Address(long id, Country country, Person person) {
        this.id = id;
        this.country = country;
        this.person = person;
    }

    public Address(long id, Country country, Person person, String value) {
        this.id = id;
        this.country = country;
       this.person = person;
       this.value = value;
    }

    @Id

    
    @Column(name="ID", unique=true, nullable=false)
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

@ManyToOne(fetch=FetchType.LAZY)
@JoinColumn(name = "COUNTRY_ID", nullable = false)
public Country getCountry() {
    return this.country;
}

    public void setCountry(Country country) {
        this.country = country;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSON_ID", nullable = false)
    public Person getPerson() {
        return this.person;
    }
    
    public void setPerson(Person person) {
        this.person = person;
    }

    
    @Column(name="VALUE")
    public String getValue() {
        return this.value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }




}


