package com.github.chriweis.querydsl.util.tools;

import com.github.chriweis.querydsl.util.AbstractTestDbTest;
import com.github.chriweis.querydsl.util.metamodel.DbMetamodel;
import com.github.chriweis.querydsl.util.metamodel.DbTableRelationship;
import com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson;
import com.github.chriweis.querydsl.util.tools.DataExtractor.ExtractionQuery;
import org.junit.Test;

import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QAddress.address;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QCommentOnPerson.commentOnPerson;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QCountry.country;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson.person;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPersonType.personType;
import static java.util.Collections.singletonList;
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
    public void shouldFetchTuplesForEachTableWithCustomForeignKey() {
        DbMetamodel metamodel = this.metamodel.with(new DbTableRelationship(commentOnPerson, singletonList(commentOnPerson.personId), person, singletonList(person.id)));
        DataExtractor dataExtractor = new DataExtractor(testDb().sqlQueryFactory(), person, person.id.eq(1L));

        dataExtractor.extractFrom(metamodel);

        assertThat(dataExtractor.tuplesFor(commentOnPerson)).hasSize(1);
    }

    @Test
    public void shouldFetchAllTuples() {
        DataExtractor dataExtractor = new DataExtractor(testDb().sqlQueryFactory(), person, person.id.eq(1L));

        dataExtractor.extractFrom(metamodel);

        assertThat(dataExtractor.extractedTuples()).hasSize(6);
    }

    @Test
    public void shouldIterateOverTuplesWithSimplePrimaryKey() {
        DataExtractor dataExtractor = new DataExtractor(testDb().sqlQueryFactory(), person, person.id.eq(1L));

        dataExtractor.extractFrom(metamodel);

        assertThat(dataExtractor.tuplesFor(address, 1)).hasSize(2);
    }

    @Test
    public void shouldIterateOverTuplesWithComplexPrimaryKey() {
        DataExtractor dataExtractor = new DataExtractor(testDb().sqlQueryFactory(), person, person.id.eq(1L));

        dataExtractor.extractFrom(metamodel);

        assertThat(dataExtractor.tuplesFor(personType, 1)).hasSize(1);
    }

    @Test
    public void shouldOrderTuplesWithComplexPrimaryKey() {
        DataExtractor dataExtractor = new DataExtractor(testDb().sqlQueryFactory(), person, person.id.eq(1L));

        ExtractionQuery extractionQuery = dataExtractor.extractFrom(metamodel).getQueries().get(metamodel.getTableFor(personType));

        assertThat(extractionQuery.getPrimaryKeyOrder())
                .contains(personType.id1.asc(), personType.id2.asc());
    }
}
