
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

create table COMMENT_ON_PERSON (
  PERSON_ID bigint primary key,
  COMMENT varchar2(255)
  -- no foreign key on purpose
)
