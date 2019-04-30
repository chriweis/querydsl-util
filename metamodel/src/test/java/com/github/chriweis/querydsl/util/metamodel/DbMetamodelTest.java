package com.github.chriweis.querydsl.util.metamodel;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class DbMetamodelTest {

    @Test
    public void shouldNotAllowAdditionOfTablesAfterSealing() {
        DbMetamodel metamodel = new DbMetamodel();

        metamodel.seal();

        assertThatThrownBy(() -> metamodel.addTable(mock(DbTable.class))).isInstanceOf(IllegalStateException.class);
    }
}
