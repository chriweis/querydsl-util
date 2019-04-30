package com.github.chriweis.querydsl.util.sampledb.generated.hibernate;
// Generated May 1, 2019 8:29:36 AM by Hibernate Tools 5.3.7.Final


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Address generated by hbm2java
 */
@Entity
@Table(name="ADDRESS"
    ,schema="PUBLIC"
    ,catalog="SAMPLEDB"
)
public class Address  implements java.io.Serializable {


     private Long id;
     private Person person;
     private String value;

    public Address() {
    }

    public Address(Person person, String value) {
       this.person = person;
       this.value = value;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)

    
    @Column(name="ID", unique=true, nullable=false)
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="PERSON_ID")
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


