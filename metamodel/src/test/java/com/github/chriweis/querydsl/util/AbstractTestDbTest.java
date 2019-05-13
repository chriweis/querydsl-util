package com.github.chriweis.querydsl.util;

import com.github.chriweis.querydsl.util.TestDb.InitializationMode;
import com.github.chriweis.querydsl.util.sampledb.generated.hibernate.Person;

import static com.github.chriweis.querydsl.util.TestDb.InitializationMode.WithData;

public abstract class AbstractTestDbTest {

    private TestDb testDb;

    protected TestDb testDb() {
        if (testDb == null) {
            testDb = newTestDb(WithData);
        }
        return testDb;
    }

    protected TestDb newTestDb(InitializationMode initializationMode) {
        TestDb testDb = new TestDb(TestDb.TestDbConfiguration.builder()
                .tempFolderPath("/tmp/migration/")
                .databaseName("test")
                .migrationsLocation("/sampledb/db/migration/h2")
                .hibernateAnnotatedClassesPackage(Person.class.getPackage())
                .propertiesResource("/hibernate.default.properties")
                .build());
        testDb.initialize(initializationMode);
        return testDb;
    }
}
