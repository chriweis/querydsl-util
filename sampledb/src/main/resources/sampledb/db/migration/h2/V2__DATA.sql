
set schema PUBLIC;

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

insert into ADDRESS (ID, "VALUE", PERSON_ID, COUNTRY_ID) values (1, 'Alice #1', (select ID from PERSON where NAME = 'Alice'), (select ID from COUNTRY where NAME = 'Austria'));
insert into ADDRESS (ID, "VALUE", PERSON_ID, COUNTRY_ID) values (2, 'Alice #2', (select ID from PERSON where NAME = 'Alice'), (select ID from COUNTRY where NAME = 'Belize'));
insert into ADDRESS (ID, "VALUE", PERSON_ID, COUNTRY_ID) values (3, 'Albert #1', (select ID from PERSON where NAME = 'Albert'), (select ID from COUNTRY where NAME = 'Cuba'));
insert into ADDRESS (ID, "VALUE", PERSON_ID, COUNTRY_ID) values (4, 'Albert #2', (select ID from PERSON where NAME = 'Albert'), (select ID from COUNTRY where NAME = 'Denmark'));
insert into ADDRESS (ID, "VALUE", PERSON_ID, COUNTRY_ID) values (5, 'Berta #1', (select ID from PERSON where NAME = 'Berta'), (select ID from COUNTRY where NAME = 'Austria'));
insert into ADDRESS (ID, "VALUE", PERSON_ID, COUNTRY_ID) values (6, 'Berta #2', (select ID from PERSON where NAME = 'Berta'), (select ID from COUNTRY where NAME = 'Belize'));
insert into ADDRESS (ID, "VALUE", PERSON_ID, COUNTRY_ID) values (7, 'Bob #1', (select ID from PERSON where NAME = 'Bob'), (select ID from COUNTRY where NAME = 'Cuba'));
insert into ADDRESS (ID, "VALUE", PERSON_ID, COUNTRY_ID) values (8, 'Bob #2', (select ID from PERSON where NAME = 'Bob'), (select ID from COUNTRY where NAME = 'Denmark'));

insert into COMMENT_ON_PERSON (PERSON_ID, COMMENT) values ((select ID from PERSON where NAME = 'Alice'), 'This is Alice');
insert into COMMENT_ON_PERSON (PERSON_ID, COMMENT) values ((select ID from PERSON where NAME = 'Albert'), 'This is Albert');
insert into COMMENT_ON_PERSON (PERSON_ID, COMMENT) values ((select ID from PERSON where NAME = 'Berta'), 'This is Berta');
insert into COMMENT_ON_PERSON (PERSON_ID, COMMENT) values ((select ID from PERSON where NAME = 'Bob'), 'This is Bob');
