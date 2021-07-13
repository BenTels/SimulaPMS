package nl.bentels.loa.simulapms.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;


public enum AgeClass {
	INFANT(2),
    CHILD(14),
    ADULT(64),
    SENIOR(Integer.MAX_VALUE);

    private final int maxAge;

    private AgeClass(final int maxAge) {
        this.maxAge = maxAge;
    }

    static AgeClass findAgeClassGivenDateOfBirth(final LocalDate dateOfBirth) {
        LocalDate currentDate = LocalDate.now();
        return findAgeClassOnSpecificDate(dateOfBirth, currentDate);
    }

    static AgeClass findAgeClassOnSpecificDate(final LocalDate dateOfBirth, final LocalDate comparisonDate) {
        long age = ChronoUnit.YEARS.between(dateOfBirth, comparisonDate);
        return Arrays.stream(values())
                .filter(ac -> 0 <= age && age <= ac.maxAge)
                .findFirst()
                .orElse(ADULT);
    }
}
