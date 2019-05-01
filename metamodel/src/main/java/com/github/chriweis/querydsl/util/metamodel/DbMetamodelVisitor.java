package com.github.chriweis.querydsl.util.metamodel;

public interface DbMetamodelVisitor {

    void visitTable(DbTable table);

    void visitForeignKey(DbTableRelationship foreignKey, DbTable foreignKeyTable, DbTable keyTable);

    void visitInverseForeignKey(DbTableRelationship foreignKey, DbTable keyTable, DbTable foreignKeyTable);
}
