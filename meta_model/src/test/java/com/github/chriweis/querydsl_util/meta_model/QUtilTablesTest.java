package com.github.chriweis.querydsl_util.meta_model;

import com.github.chriweis.querydsl_util.sample_db.generated.querydsl.QAddress;
import com.github.chriweis.querydsl_util.sample_db.generated.querydsl.QPerson;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class QUtilTablesTest {

    @Test
    public void shouldFindTables() {
        assertThat(Qutil.tables(QPerson.class.getPackage())).hasSize(2);
        assertThat(Qutil.tables(QPerson.class.getPackage())).contains(QPerson.person);
        assertThat(Qutil.tables(QPerson.class.getPackage())).contains(QAddress.address);
    }
}
