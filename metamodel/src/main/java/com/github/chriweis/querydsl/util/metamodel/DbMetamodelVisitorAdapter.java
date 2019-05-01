package com.github.chriweis.querydsl.util.metamodel;

public class DbMetamodelVisitorAdapter implements DbMetamodelVisitor {

    @Override
    public void visitTable(DbTable table) {
    }

    @Override
    public void visitForeignKey(DbTableRelationship foreignKey, DbTable foreignKeyTable, DbTable keyTable) {
    }

    @Override
    public void visitInverseForeignKey(DbTableRelationship foreignKey, DbTable keyTable, DbTable foreignKeyTable) {
    }
}
