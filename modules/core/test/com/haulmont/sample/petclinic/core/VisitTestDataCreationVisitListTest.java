package com.haulmont.sample.petclinic.core;


import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.FluentLoader;
import com.haulmont.cuba.core.global.TimeSource;
import com.haulmont.sample.petclinic.config.PetclinicTestdataConfig;
import com.haulmont.sample.petclinic.entity.pet.Pet;
import com.haulmont.sample.petclinic.entity.visit.Visit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitTestDataCreationVisitListTest {

    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);
    private static final LocalDateTime TOMORROW_MORNING = TOMORROW.atStartOfDay();

    VisitTestDataCreation visitTestDataCreation;

    @Mock
    PetclinicTestdataConfig petclinicTestdataConfig;
    @Mock
    TimeSource timeSource;
    @Mock
    DataManager dataManager;

    PetclinicData data;



    @BeforeEach
    void setup() {

        when(dataManager.create(Visit.class))
                .then(invocation -> new Visit());

        visitTestDataCreation = new VisitTestDataCreation(
            petclinicTestdataConfig,
            timeSource,
            dataManager,
            new RandomVisitDateTime()
        );

        when(timeSource.now())
                .thenReturn(ZonedDateTime.now());

        data = new PetclinicData();
    }

    @Test
    void given_noDaysInFutureShouldBeGenerated_when_generateVisits_then_noVisitIsInFuture() {

        // given:
        daysInPastToGenerateFor(10);
        daysInFutureToGenerateFor(0);
        visitAmountPerDay(10);

        possibleDescriptions("Fever, Disease");

        possiblePets(
                data.pet("Pikachu"),
                data.pet("Garchomp")
        );

        // when:
        List<Visit> visits = visitTestDataCreation.createVisits();

        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay();;
        // then:
        assertThat(
                visits.stream()
                        .map(Visit::getVisitStart)
                        .collect(Collectors.toList())
        )
                .allMatch(localDateTime -> localDateTime.isBefore(tomorrow));
    }

    @Test
    void given_daysInFutureShouldBeGenerated_when_generateVisits_then_amountOfVisitsIsDecreasingIntoTheFuture() {

        // given:
        daysInPastToGenerateFor(1);
        daysInFutureToGenerateFor(30);
        visitAmountPerDay(10);

        possibleDescriptions("Fever, Disease");

        possiblePets(
                data.pet("Pikachu"),
                data.pet("Garchomp")
        );

        // when:
        List<Visit> visits = visitTestDataCreation.createVisits();

        // then: the more far into the future, the less amount of visits are generated
        assertThat(amountOfVisitsForDate(visits, TOMORROW))
                .isLessThan(10);
        assertThat(amountOfVisitsForDate(visits, TOMORROW.plusDays(7)))
                .isLessThan(8);
        assertThat(amountOfVisitsForDate(visits, TOMORROW.plusDays(14)))
                .isLessThan(6);
        assertThat(amountOfVisitsForDate(visits, TOMORROW.plusDays(21)))
                .isLessThan(4);
    }

    private int amountOfVisitsForDate(List<Visit> visits, LocalDate date) {
        return futureVisits(visits)
                .stream()
                .collect(Collectors.groupingBy(visit -> visit.getVisitStart().toLocalDate()))
                .get(date)
        .size();
    }

    private List<Visit> futureVisits(List<Visit> visits) {

        return visits.stream()
                .filter(visit -> visit.getVisitStart().isAfter(TOMORROW_MORNING))
                .collect(Collectors.toList());
    }

    @Test
    void given_10VisitsPerDays_when_generateVisits_then_sizeIs10() {

        // given:
        daysInPastToGenerateFor(1);
        daysInFutureToGenerateFor(0);
        visitAmountPerDay(10);

        possibleDescriptions("Fever, Disease");

        possiblePets(
                data.pet("Pikachu"),
                data.pet("Garchomp")
        );

        // when:
        List<Visit> visits = visitTestDataCreation.createVisits();

        // then:
        assertThat(visits.size())
            .isEqualTo(10);
    }

    private void possiblePets(Pet... pets) {
        FluentLoader petLoaderMock = mock(FluentLoader.class);
        when(dataManager.load(Pet.class))
                .thenReturn(petLoaderMock);

        when(petLoaderMock.list())
                .thenReturn(asList(
                        pets
                ));
    }

    private void daysInPastToGenerateFor(int days) {
        when(petclinicTestdataConfig.getTestdataVisitStartAmountPastDays())
                .thenReturn(days);
    }

    private void daysInFutureToGenerateFor(int days) {
        when(petclinicTestdataConfig.getTestdataVisitStartAmountFutureDays())
                .thenReturn(days);
    }

    private void visitAmountPerDay(int amount) {
        when(petclinicTestdataConfig.getTestdataVisitAmountPerDay())
                .thenReturn(amount);
    }

    private void possibleDescriptions(String descriptions) {
        when(petclinicTestdataConfig.getTestdataVisitDescriptionOptions())
                .thenReturn(descriptions);
    }

}