package nl.bentels.loa.simulapms.temprestfrontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableLoadTimeWeaving;

@SpringBootApplication
@EnableLoadTimeWeaving
public class TempRestFrontendApplication {

    public static void main(final String[] args) {
        SpringApplication.run(TempRestFrontendApplication.class, args);
    }

}
