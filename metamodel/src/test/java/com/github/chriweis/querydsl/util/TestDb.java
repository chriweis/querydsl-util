package com.github.chriweis.querydsl.util;

import com.querydsl.sql.H2Templates;
import com.querydsl.sql.SQLQueryFactory;
import com.querydsl.sql.SQLTemplates;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.flywaydb.core.Flyway;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.reflections.Reflections;

import javax.persistence.Table;
import javax.sql.DataSource;
import java.io.File;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Function;

class TestDb {

    private final TestDbConfiguration configuration;

    private SessionFactory sessionFactory;
    private Session session;
    private SQLQueryFactory sqlQueryFactory;
    private DataSource dataSource;

    public TestDb(TestDbConfiguration configuration) {
        this.configuration = configuration;
    }

    public void initialize() {
        Flyway flyway = Flyway.configure()
                .dataSource(configuration.dbUrl(), configuration.dbUsername(), configuration.dbPassword())
                .locations(configuration.getMigrationsLocation())
                .baselineOnMigrate(true)
                .load();
        flyway.migrate();

        dataSource = flyway.getConfiguration().getDataSource();

        org.hibernate.cfg.Configuration hibernateConfiguration = new org.hibernate.cfg.Configuration();
        hibernateConfiguration.setProperties(configuration.getProperties());
        hibernateConfiguration.setPhysicalNamingStrategy(new PhysicalNamingStrategyStandardImpl() {
            @Override
            public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
                return null;
            }
        });
        Reflections reflections = new Reflections(configuration.getHibernateAnnotatedClassesPackage().getName());
        reflections.getTypesAnnotatedWith(Table.class).stream().forEach(hibernateConfiguration::addAnnotatedClass);
        sessionFactory = hibernateConfiguration.buildSessionFactory();
    }

    public Session getSession() {
        if (session == null) {
            session = sessionFactory.openSession();
            session.setHibernateFlushMode(FlushMode.MANUAL);
        }
        return session;
    }

    public <T> T doInTransaction(Function<Session, T> action) {
        Session session = getSession();
        session.setHibernateFlushMode(FlushMode.MANUAL);
        Transaction transaction = session.beginTransaction();
        final T result;
        try {
            result = action.apply(session);
            session.flush();
            transaction.commit();
            return result;
        } catch (Throwable t) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(t);
        }
    }

    public SQLQueryFactory sqlQueryFactory() {
        if (sqlQueryFactory == null) {
            SQLTemplates sqlTemplates = H2Templates.DEFAULT;
            com.querydsl.sql.Configuration configuration = new com.querydsl.sql.Configuration(sqlTemplates);
            sqlQueryFactory = new SQLQueryFactory(configuration, dataSource);
        }
        return sqlQueryFactory;
    }

    @Builder
    @Getter
    @ToString
    public static class TestDbConfiguration {

        public static final String DB_URL_PROPERTY = "hibernate.connection.url";
        public static final String DB_USERNAME_PROPERTY = "hibernate.connection.username";
        public static final String DB_PASSWORD_PROPERTY = "hibernate.connection.password";

        private String tempFolderPath;
        private String databaseName;
        private String migrationsLocation;
        private Package hibernateAnnotatedClassesPackage;
        private String propertiesResource;
        private Properties properties;

        @Builder.Default
        private boolean initialized = false;

        public Properties getProperties() {
            if (properties == null
                    && propertiesResource == null) {
                throw new IllegalStateException();
            }
            if (properties == null) {
                properties = new Properties();
                try (InputStream in = QUtilTablesTest.class.getResourceAsStream("/hibernate.default.properties")) {
                    properties.load(in);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (!initialized) {
                String url = properties.getProperty(DB_URL_PROPERTY);
                File dbFolder = new File(new File(tempFolderPath), databaseName);
                properties.setProperty(DB_URL_PROPERTY, url.replace("${dbFolder}", dbFolder.getAbsolutePath()));
            }
            return properties;
        }

        String getProperty(String key) {
            return getProperties().getProperty(key);
        }

        String dbUrl() {
            return getProperties().getProperty(DB_URL_PROPERTY);
        }

        String dbUsername() {
            return getProperties().getProperty(DB_USERNAME_PROPERTY);
        }

        String dbPassword() {
            return getProperties().getProperty(DB_PASSWORD_PROPERTY);
        }
    }
}
