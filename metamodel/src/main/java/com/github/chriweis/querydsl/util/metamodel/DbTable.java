package com.github.chriweis.querydsl.util.metamodel;

import com.github.chriweis.querydsl.util.util.Assert;
import com.querydsl.sql.RelationalPath;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

@Data
@ToString(exclude = {"metamodel"})
@EqualsAndHashCode(exclude = {"metamodel"})
public class DbTable {

    private final RelationalPath<?> relationalPath;
    private DbMetamodel metamodel;

    public static DbTable forRelationalPath(RelationalPath<?> relationalPath) {
        return new DbTable(relationalPath);
    }

    private DbTable(RelationalPath relationalPath) {
        this.relationalPath = relationalPath;
    }

    public DbTable setMetamodel(DbMetamodel metamodel) {
        Assert.state(this.metamodel == null);

        this.metamodel = metamodel;

        return this;
    }

    public Set<DbTableRelationship> getRelationships() {
        return metamodel.getRelationshipsOf(this);
    }

    public Set<DbTableRelationship> getForeignKeyRelationships() {
        return metamodel.getForeignKeyRelationshipsIn(this);
    }

    public Set<DbTableRelationship> getInverseForeignKeyRelationships() {
        return metamodel.getForeignKeyRelationshipsReferencing(this);
    }

    public void visit(DbMetamodelVisitor visitor) {
        getForeignKeyRelationships().stream()
                .forEach(foreignKeyRelationship -> {
                    DbTable keyTable = metamodel.getTableFor(foreignKeyRelationship.getKeyRelationalPath());
                    visitor.visitForeignKey(foreignKeyRelationship, this, keyTable);
                });
        getInverseForeignKeyRelationships().stream()
                .forEach(inverseForeignKeyRelationship -> {
                    DbTable foreignKeyTable = metamodel.getTableFor(inverseForeignKeyRelationship.getForeignKeyRelationalPath());
                    visitor.visitInverseForeignKey(inverseForeignKeyRelationship, this, foreignKeyTable);
                });
    }
}
