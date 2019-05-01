package com.github.chriweis.querydsl.util;

import com.github.chriweis.querydsl.util.sampledb.generated.hibernate.Person;

public abstract class AbstractTestDbTest {

    private TestDb testDb;

    public TestDb testDb() {
        if (testDb == null) {
            testDb = new TestDb(TestDb.TestDbConfiguration.builder()
                    .tempFolderPath("/tmp/migration/")
                    .databaseName("test")
                    .migrationsLocation("/sampledb/db/migration/h2")
                    .hibernateAnnotatedClassesPackage(Person.class.getPackage())
                    .propertiesResource("/hibernate.default.properties")
                    .build());
            testDb.initialize();
        }
        return testDb;
    }
}
