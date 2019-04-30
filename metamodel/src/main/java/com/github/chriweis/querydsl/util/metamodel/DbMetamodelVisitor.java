package com.github.chriweis.querydsl.util.metamodel;

public interface DbMetamodelVisitor {

    void visitRequired(DbTable table, DbTable requiredTable, DbTableLink tableLink);

    void visitDependant(DbTable table, DbTable dependantTable, DbTableLink tableLink);
}
