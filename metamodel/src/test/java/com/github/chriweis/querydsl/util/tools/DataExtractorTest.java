package com.github.chriweis.querydsl.util.tools;

import com.github.chriweis.querydsl.util.AbstractTestDbTest;
import com.github.chriweis.querydsl.util.metamodel.DbMetamodel;
import com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson;
import com.github.chriweis.querydsl.util.tools.DataExtractor.ExtractionQuery;
import com.querydsl.core.Tuple;
import com.querydsl.sql.RelationalPathBase;
import org.junit.Test;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QAddress.address;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QCountry.country;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPerson.person;
import static com.github.chriweis.querydsl.util.sampledb.generated.querydsl.QPersonType.personType;
import static org.assertj.core.api.Assertions.assertThat;

public class DataExtractorTest extends AbstractTestDbTest {

    @Test
    public void shouldFetchTuples() {
        DbMetamodel metamodel = DbMetamodel.for$(QPerson.class.getPackage());
        DataExtractor dataExtractor = new DataExtractor(testDb().sqlQueryFactory(), person, person.id.eq(1L));

        dataExtractor.extractFrom(metamodel);

        Map<RelationalPathBase, Stream<Tuple>> tuples = dataExtractor.getQueries().values().stream()
                .collect(Collectors.toMap(ExtractionQuery::getRelationalPath, ExtractionQuery::tuples));
        assertThat(tuples.get(personType)).hasSize(1);
        assertThat(tuples.get(person)).hasSize(1);
        assertThat(tuples.get(address)).hasSize(2);
        assertThat(tuples.get(country)).hasSize(2);
    }
}
