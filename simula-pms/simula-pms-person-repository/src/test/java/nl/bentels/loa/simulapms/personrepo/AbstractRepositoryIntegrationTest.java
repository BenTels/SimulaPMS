package nl.bentels.loa.simulapms.personrepo;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.extension.ExtendWith;
import org.opentest4j.MultipleFailuresError;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.MongoDriverInformation;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import nl.bentels.loa.simulapms.model.person.Person;
import nl.bentels.loa.simulapms.model.person.PhoneNumber;
import nl.bentels.loa.simulapms.personrepo.AbstractRepositoryIntegrationTest.TestMongoConfiguration;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { PersonRepositoryConfiguration.class, TestMongoConfiguration.class })
public abstract class AbstractRepositoryIntegrationTest {

    @Configuration
    @ImportResource("classpath:/META-INF/spring/model/integration-test-config.xml")
    protected static class TestMongoConfiguration extends AbstractMongoClientConfiguration {
        private static final String CONN_STRING = "mongodb+srv://cluster-bzt-test-0.ew5ad.mongodb.net/admin";
        private static final String USERNAME    = "bzt";
        private static final String PASSWD      = "bztPasswd";

        public @Bean MongoClientFactoryBean mongo() {
            MongoClientFactoryBean mongo = new MongoClientFactoryBean();
            mongo.setConnectionString(new ConnectionString(CONN_STRING));
            mongo.setCredential(new MongoCredential[] { MongoCredential.createScramSha1Credential(USERNAME, "admin", PASSWD.toCharArray()) });
            return mongo;
        }

        @Override
        public MongoClient mongoClient() {
            return MongoClients.create(MongoClientSettings.builder().credential(MongoCredential.createScramSha1Credential(USERNAME, "admin", PASSWD.toCharArray()))
                    .applyConnectionString(new ConnectionString(CONN_STRING)).build(), MongoDriverInformation.builder().build());
        }

        @Override
        protected String getDatabaseName() {
            return "test";
        }
    }

    protected void assertTels(Person tels) throws MultipleFailuresError {
        assertAll(
                () -> assertEquals("5f5f6c1c5fc0d0741b9c7732", tels.getId()),
                () -> assertEquals("Tels", tels.getLastName()),
                () -> assertEquals("Benjamin", tels.getFirstNames().get(0)),
                () -> assertEquals("Ze'ev", tels.getMiddleNames().get(0)),
                () -> assertEquals("bzt@bentels.nl", tels.getEmailAddresses().get(0)),
                () -> assertEquals(new PhoneNumber("31614435308", true), tels.getPhoneNumbers().get(0)),
                () -> assertNull(tels.getCorrespondenceAddress()),
                () -> assertNull(tels.getBillingAddress()),
                () -> assertNull(tels.getDateOfBirth()));
    }

}
