package com.github.chriweis.querydsl.util.metamodel;

import com.github.chriweis.querydsl.util.util.Assert;
import com.querydsl.core.types.Path;
import com.querydsl.sql.RelationalPathBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Data
@AllArgsConstructor(access = PRIVATE)
@ToString(exclude = {"metamodel"})
@EqualsAndHashCode(exclude = {"metamodel"})
public class DbTable {

    private final RelationalPathBase<?> relationalPath;
    private DbMetamodel metamodel;

    public static DbTable forRelationalPath(RelationalPathBase<?> relationalPath) {
        return new DbTable(relationalPath);
    }

    private DbTable(RelationalPathBase relationalPath) {
        this.relationalPath = relationalPath;
    }

    DbTable freshClone() {
        return new DbTable(relationalPath);
    }

    public DbTable setMetamodel(DbMetamodel metamodel) {
        Assert.state(this.metamodel == null);

        this.metamodel = metamodel;

        return this;
    }

    public Set<DbTableRelationship> relationships() {
        return metamodel.relationshipsOf(this);
    }

    public Set<DbTableRelationship> foreignKeyRelationships() {
        return metamodel.foreignKeyRelationshipsIn(this);
    }

    public Set<DbTableRelationship> inverseForeignKeyRelationships() {
        return metamodel.inverseForeignKeyRelationshipsIn(this);
    }

    public List<? extends Path<?>> primaryKeyFields() {
        return relationalPath.getPrimaryKey().getLocalColumns();
    }

    public void visit(DbMetamodelVisitor visitor) {
        foreignKeyRelationships().stream()
                .forEach(foreignKeyRelationship -> {
                    DbTable keyTable = metamodel.tableFor(foreignKeyRelationship.getKeyRelationalPath());
                    visitor.visitForeignKey(foreignKeyRelationship, this, keyTable);
                    visitor.afterVisitForeignKey(foreignKeyRelationship, this, keyTable);
                });
        inverseForeignKeyRelationships().stream()
                .forEach(inverseForeignKeyRelationship -> {
                    DbTable foreignKeyTable = metamodel.tableFor(inverseForeignKeyRelationship.getForeignKeyRelationalPath());
                    visitor.visitInverseForeignKey(inverseForeignKeyRelationship, this, foreignKeyTable);
                    visitor.afterVisitInverseForeignKey(inverseForeignKeyRelationship, this, foreignKeyTable);
                });
    }
}
