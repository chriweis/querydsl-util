package com.github.chriweis.querydsl.util.metamodel;

public abstract class DbMetamodelVisitorAdapter<T extends DbMetamodelVisitor> implements DbMetamodelVisitor<T> {

    @Override
    public T visit(DbMetamodel metamodel) {
        return (T) this;
    }

    @Override
    public void visitTable(DbTable table) {
    }

    @Override
    public void afterVisitTable(DbTable table) {
    }

    @Override
    public void visitForeignKey(DbTableRelationship foreignKey, DbTable foreignKeyTable, DbTable keyTable) {
    }

    @Override
    public void afterVisitForeignKey(DbTableRelationship foreignKey, DbTable foreignKeyTable, DbTable keyTable) {
    }

    @Override
    public void visitInverseForeignKey(DbTableRelationship foreignKey, DbTable keyTable, DbTable foreignKeyTable) {
    }

    @Override
    public void afterVisitInverseForeignKey(DbTableRelationship foreignKey, DbTable keyTable, DbTable foreignKeyTable) {
    }
}
