package com.github.chriweis.querydsl.util.tools;

import com.github.chriweis.querydsl.util.AbstractTestDbTest;
import com.github.chriweis.querydsl.util.metamodel.DbMetamodel;
import com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson;
import org.junit.Test;

import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QAddress.address;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QCountry.country;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson.person;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPersonType.personType;
import static org.assertj.core.api.Assertions.assertThat;

public class DataExtractorTest extends AbstractTestDbTest {

    DbMetamodel metamodel = DbMetamodel.for$(QPerson.class.getPackage());

    @Test
    public void shouldFetchTuplesForEachTable() {
        DataExtractor dataExtractor = new DataExtractor(testDb().sqlQueryFactory(), person, person.id.eq(1L));

        dataExtractor.extractFrom(metamodel);

        assertThat(dataExtractor.tuplesFor(personType)).hasSize(1);
        assertThat(dataExtractor.tuplesFor(person)).hasSize(1);
        assertThat(dataExtractor.tuplesFor(address)).hasSize(2);
        assertThat(dataExtractor.tuplesFor(country)).hasSize(2);
    }

    @Test
    public void shouldFetchAllTuples() {
        DataExtractor dataExtractor = new DataExtractor(testDb().sqlQueryFactory(), person, person.id.eq(1L));

        dataExtractor.extractFrom(metamodel);

        assertThat(dataExtractor.extractedTuples()).hasSize(6);
    }
}
