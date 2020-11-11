package nl.bentels.loa.simulapms.personrepo;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableLoadTimeWeaving;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@ComponentScan(basePackages = { "nl.bentels.loa.simulapms.personrepo" })
@EnableMongoRepositories(basePackages = { "nl.bentels.loa.simulapms.personrepo" })
@EnableLoadTimeWeaving
@EnableAutoConfiguration
public class PersonRepositoryConfiguration {

}
