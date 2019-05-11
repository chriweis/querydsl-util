
set schema PUBLIC;

create table PERSON_TYPE (
  ID_1 bigint,
  ID_2 bigint,
  LABEL varchar2(255) unique,
  constraint PERSON_TYPE_PK primary key (ID_1, ID_2)
);

create table PERSON (
  ID bigint primary key,
  PERSON_TYPE_FK_1 bigint not null,
  PERSON_TYPE_FK_2 bigint not null,
  NAME varchar2(255),
  constraint PERSON_TYPE_FK foreign key (PERSON_TYPE_FK_1, PERSON_TYPE_FK_2) references PERSON_TYPE
);

create table COUNTRY (
  ID bigint primary key,
  NAME varchar2(255)
);

create table ADDRESS (
  ID bigint primary key,
  PERSON_ID bigint
    not null
    references PERSON,
  COUNTRY_ID bigint
    not null
    references COUNTRY,
  VALUE varchar2(255)
);

insert into PERSON_TYPE values (1, 1, 'A / A');
insert into PERSON_TYPE values (2, 2, 'B / B');

insert into PERSON (ID, NAME, PERSON_TYPE_FK_1, PERSON_TYPE_FK_2) values (
    1, 'Alice', (select ID_1 from PERSON_TYPE where LABEL = 'A / A'), (select ID_2 from PERSON_TYPE where LABEL = 'A / A'));

insert into PERSON (ID, NAME, PERSON_TYPE_FK_1, PERSON_TYPE_FK_2) values (
    2, 'Albert', (select ID_1 from PERSON_TYPE where LABEL = 'A / A'), (select ID_2 from PERSON_TYPE where LABEL = 'A / A'));

insert into PERSON (ID, NAME, PERSON_TYPE_FK_1, PERSON_TYPE_FK_2) values (
    3, 'Berta', (select ID_1 from PERSON_TYPE where LABEL = 'B / B'), (select ID_2 from PERSON_TYPE where LABEL = 'B / B'));

insert into PERSON (ID, NAME, PERSON_TYPE_FK_1, PERSON_TYPE_FK_2) values (
    4, 'Bob', (select ID_1 from PERSON_TYPE where LABEL = 'B / B'), (select ID_2 from PERSON_TYPE where LABEL = 'B / B'));

insert into COUNTRY (ID, NAME) values (1, 'Austria');
insert into COUNTRY (ID, NAME) values (2, 'Belize');
insert into COUNTRY (ID, NAME) values (3, 'Cuba');
insert into COUNTRY (ID, NAME) values (4, 'Denmark');

insert into address (ID, "VALUE", PERSON_ID, COUNTRY_ID) values (1, 'Alice #1', (select ID from PERSON where NAME = 'Alice'), (select ID from COUNTRY where NAME = 'Austria'));
insert into address (ID, "VALUE", PERSON_ID, COUNTRY_ID) values (2, 'Alice #2', (select ID from PERSON where NAME = 'Alice'), (select ID from COUNTRY where NAME = 'Belize'));
insert into address (ID, "VALUE", PERSON_ID, COUNTRY_ID) values (3, 'Albert #1', (select ID from PERSON where NAME = 'Albert'), (select ID from COUNTRY where NAME = 'Cuba'));
insert into address (ID, "VALUE", PERSON_ID, COUNTRY_ID) values (4, 'Albert #2', (select ID from PERSON where NAME = 'Albert'), (select ID from COUNTRY where NAME = 'Denmark'));
insert into address (ID, "VALUE", PERSON_ID, COUNTRY_ID) values (5, 'Berta #1', (select ID from PERSON where NAME = 'Berta'), (select ID from COUNTRY where NAME = 'Austria'));
insert into address (ID, "VALUE", PERSON_ID, COUNTRY_ID) values (6, 'Berta #2', (select ID from PERSON where NAME = 'Berta'), (select ID from COUNTRY where NAME = 'Belize'));
insert into address (ID, "VALUE", PERSON_ID, COUNTRY_ID) values (7, 'Bob #1', (select ID from PERSON where NAME = 'Bob'), (select ID from COUNTRY where NAME = 'Cuba'));
insert into address (ID, "VALUE", PERSON_ID, COUNTRY_ID) values (8, 'Bob #2', (select ID from PERSON where NAME = 'Bob'), (select ID from COUNTRY where NAME = 'Denmark'));
