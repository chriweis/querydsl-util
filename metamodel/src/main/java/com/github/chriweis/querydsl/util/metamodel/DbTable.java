package com.github.chriweis.querydsl.util.metamodel;

import com.github.chriweis.querydsl.util.util.Assert;
import com.querydsl.sql.RelationalPath;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

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

    public void setMetamodel(DbMetamodel metamodel) {
        Assert.state(this.metamodel == null);

        this.metamodel = metamodel;
    }

    public Set<DbTableLink> getRequired() {
        return metamodel.getReferences().stream()
                .filter(tableLink -> tableLink.getFrom() == relationalPath)
                .collect(toSet());
    }

    public Set<DbTableLink> getDependant() {
        return metamodel.getReferences().stream()
                .filter(tableLink -> tableLink.getTo() == relationalPath)
                .collect(toSet());
    }

    public void visit(DbMetamodelVisitor visitor) {
        getRequired().stream()
                .forEach(tableLink -> {
                    DbTable requiredTable = metamodel.getTableFor(tableLink.getTo());
                    visitor.visitRequired(this, requiredTable, tableLink);
                });
        getDependant().stream()
                .forEach(tableLink -> {
                    DbTable dependantTable = metamodel.getTableFor(tableLink.getFrom());
                    visitor.visitDependant(this, dependantTable, tableLink);
                });
    }
}
