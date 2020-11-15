package nl.bentels.loa.simulapms.personrepo;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ComponentScan(basePackages = { "nl.bentels.loa.simulapms.personrepo" })
@EnableMongoRepositories(basePackages = { "nl.bentels.loa.simulapms.personrepo" })
@EnableLoadTimeWeaving
@ImportResource("classpath:/META-INF/spring/model/load-time-weaving-aspect-config.xml")
@EnableAutoConfiguration
public class PersonRepositoryConfiguration {

    @Bean
    MongoTransactionManager transactionManager(final MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

}
