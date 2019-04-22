
set schema PUBLIC;

create table PERSON (
  ID bigint auto_increment primary key,
  NAME varchar2(255)
);

create table ADDRESS (
  ID bigint auto_increment primary key,
  PERSON_ID bigint
    references PERSON,
  VALUE varchar2(255)
);
