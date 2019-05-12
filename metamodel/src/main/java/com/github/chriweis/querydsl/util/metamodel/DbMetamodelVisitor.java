package com.github.chriweis.querydsl.util.metamodel;

public interface DbMetamodelVisitor {

    void visitTable(DbTable table);

    void afterVisitTable(DbTable table);

    void visitForeignKey(DbTableRelationship foreignKey, DbTable foreignKeyTable, DbTable keyTable);

    void afterVisitForeignKey(DbTableRelationship foreignKey, DbTable foreignKeyTable, DbTable keyTable);

    void visitInverseForeignKey(DbTableRelationship foreignKey, DbTable keyTable, DbTable foreignKeyTable);

    void afterVisitInverseForeignKey(DbTableRelationship foreignKey, DbTable keyTable, DbTable foreignKeyTable);
}
