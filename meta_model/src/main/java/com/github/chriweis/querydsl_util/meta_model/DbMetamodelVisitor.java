package com.github.chriweis.querydsl_util.meta_model;

public interface DbMetamodelVisitor {

    void visitRequired(DbTable table, DbTable requiredTable, DbTableLink tableLink);

    void visitDependant(DbTable table, DbTable dependantTable, DbTableLink tableLink);
}
