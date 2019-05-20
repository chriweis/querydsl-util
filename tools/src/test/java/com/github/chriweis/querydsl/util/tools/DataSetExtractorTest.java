package com.github.chriweis.querydsl.util.tools;

import com.github.chriweis.querydsl.util.AbstractTestDbTest;
import com.github.chriweis.querydsl.util.metamodel.DbMetamodel;
import com.github.chriweis.querydsl.util.metamodel.DbTableRelationship;
import com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson;
import org.junit.Test;

import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QAddress.address;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QCommentOnPerson.commentOnPerson;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QCountry.country;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson.person;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPersonType.personType;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class DataSetExtractorTest extends AbstractTestDbTest {

    DbMetamodel metamodel = DbMetamodel.for$(QPerson.class.getPackage());

    @Test
    public void shouldFetchTuplesForEachTable() {
        DataSetExtractor dataSetExtractor = new DataSetExtractor(testDb().sqlQueryFactory(), person, person.id.eq(1L));

        dataSetExtractor.extractFrom(metamodel);

        assertThat(dataSetExtractor.tuplesFor(personType)).hasSize(1);
        assertThat(dataSetExtractor.tuplesFor(person)).hasSize(1);
        assertThat(dataSetExtractor.tuplesFor(address)).hasSize(2);
        assertThat(dataSetExtractor.tuplesFor(country)).hasSize(2);
    }

    @Test
    public void shouldFetchTuplesForEachTableWithCustomForeignKey() {
        DbMetamodel metamodel = this.metamodel.with(new DbTableRelationship(commentOnPerson, singletonList(commentOnPerson.personId), person, singletonList(person.id)));
        DataSetExtractor dataSetExtractor = new DataSetExtractor(testDb().sqlQueryFactory(), person, person.id.eq(1L));

        dataSetExtractor.extractFrom(metamodel);

        assertThat(dataSetExtractor.tuplesFor(commentOnPerson)).hasSize(1);
    }

    @Test
    public void shouldFetchAllTuples() {
        DataSetExtractor dataSetExtractor = new DataSetExtractor(testDb().sqlQueryFactory(), person, person.id.eq(1L));

        dataSetExtractor.extractFrom(metamodel);

        assertThat(dataSetExtractor.extractedTuples()).hasSize(6);
    }

    @Test
    public void shouldIterateOverTuplesWithSimplePrimaryKey() {
        DataSetExtractor dataSetExtractor = new DataSetExtractor(testDb().sqlQueryFactory(), person, person.id.eq(1L));

        dataSetExtractor.extractFrom(metamodel);

        assertThat(dataSetExtractor.tuplesFor(address, 1)).hasSize(2);
    }

    @Test
    public void shouldIterateOverTuplesWithComplexPrimaryKey() {
        DataSetExtractor dataSetExtractor = new DataSetExtractor(testDb().sqlQueryFactory(), person, person.id.eq(1L));

        dataSetExtractor.extractFrom(metamodel);

        assertThat(dataSetExtractor.tuplesFor(personType, 1)).hasSize(1);
    }

    @Test
    public void shouldOrderTuplesWithComplexPrimaryKey() {
        DataSetExtractor dataSetExtractor = new DataSetExtractor(testDb().sqlQueryFactory(), person, person.id.eq(1L));

        DataExtractor dataExtractor = dataSetExtractor.extractFrom(metamodel).getDataExtractors().get(metamodel.getTableFor(personType));

        assertThat(dataExtractor.getPrimaryKeyOrder())
                .contains(personType.id1.asc(), personType.id2.asc());
    }
}
