package com.github.chriweis.querydsl.util.metamodel;

import com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class DbMetamodelInstantiationTest {

    @Test
    public void shouldNotAllowModificationAfterSealing() {
        DbMetamodel metamodel = new DbMetamodel();

        metamodel.seal();

        assertThatThrownBy(() -> metamodel.addTable(mock(DbTable.class)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void shouldNotAllowModificationAfterInstantiation() {
        DbMetamodel metamodel = DbMetamodel.for$(QPerson.class.getPackage());

        assertThat(metamodel.isSealed()).isTrue();
        assertThatThrownBy(() -> metamodel.addTable(mock(DbTable.class)))
                .isInstanceOf(IllegalStateException.class);
    }
}
