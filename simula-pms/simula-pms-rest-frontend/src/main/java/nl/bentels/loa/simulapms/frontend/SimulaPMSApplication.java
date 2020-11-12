package nl.bentels.loa.simulapms.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "nl.bentels.loa.simulapms.personrepo",
        "nl.bentels.loa.simulapms.frontend"
})
public class SimulaPMSApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SimulaPMSApplication.class, args);
    }
}
