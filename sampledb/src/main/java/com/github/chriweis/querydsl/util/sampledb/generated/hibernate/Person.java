package com.github.chriweis.querydsl.util.sampledb.generated.hibernate;
// Generated May 21, 2019 9:06:08 PM by Hibernate Tools 5.3.7.Final


import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Person generated by hbm2java
 */
@Entity
@Table(name="PERSON"
    ,schema="PUBLIC"
    ,catalog="SAMPLEDB"
)
public class Person  implements java.io.Serializable {


     private long id;
     private PersonType personType;
     private String name;
     private Set<Address> addresses = new HashSet<Address>(0);

    public Person() {
    }

	
    public Person(long id, PersonType personType) {
        this.id = id;
        this.personType = personType;
    }
    public Person(long id, PersonType personType, String name, Set<Address> addresses) {
       this.id = id;
       this.personType = personType;
       this.name = name;
       this.addresses = addresses;
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
    @JoinColumns( { 
        @JoinColumn(name="PERSON_TYPE_FK_1", referencedColumnName="ID_1", nullable=false), 
        @JoinColumn(name="PERSON_TYPE_FK_2", referencedColumnName="ID_2", nullable=false) } )
    public PersonType getPersonType() {
        return this.personType;
    }
    
    public void setPersonType(PersonType personType) {
        this.personType = personType;
    }

    
    @Column(name="NAME")
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

@OneToMany(fetch=FetchType.LAZY, mappedBy="person")
    public Set<Address> getAddresses() {
        return this.addresses;
    }
    
    public void setAddresses(Set<Address> addresses) {
        this.addresses = addresses;
    }




}


