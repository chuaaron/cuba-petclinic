package com.haulmont.sample.petclinic.web.visit.visit.calendar;

import com.haulmont.cuba.core.global.DatatypeFormatter;
import com.haulmont.cuba.gui.components.Calendar;
import com.haulmont.cuba.gui.components.DatePicker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.haulmont.sample.petclinic.web.visit.visit.calendar.CalendarNavigationMode.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DayCalendarNavigationTest {
    private static final LocalDate MON = LocalDate.of(2020, 3, 30);
    private static final LocalDate TUE = MON.plusDays(1);
    private static final LocalDate WED = TUE.plusDays(1);
    private static final LocalDate THU = WED.plusDays(1);
    private static final LocalDate FRI = THU.plusDays(1);
    private static final LocalDate SAT = FRI.plusDays(1);
    private static final LocalDate SUN = SAT.plusDays(1);


    private static final LocalDateTime TUE_MIDNIGHT = LocalDateTime.of(TUE, LocalTime.MIDNIGHT);
    private static final LocalDateTime TUE_MAX = LocalDateTime.of(TUE, LocalTime.MAX);
    private static final LocalDateTime THU_MIDNIGHT = LocalDateTime.of(THU, LocalTime.MIDNIGHT);
    private static final LocalDateTime THU_MAX = LocalDateTime.of(THU, LocalTime.MAX);
    private DayCalendarNavigation sut;

    @Mock
    Calendar<LocalDateTime> calendar;
    @Mock
    DatePicker<LocalDate> calendarRangePicker;
    @Mock
    DatatypeFormatter datatypeFormatter;

    @BeforeEach
    void setUp() {
        sut = new DayCalendarNavigation(calendar, calendarRangePicker, datatypeFormatter);
    }

    @Test
    void given_wednesdayIsCurrentlyConfiguredAsStartDate_when_previous_then_calendarRangeIsTuesday_and_calendarPickerIsTuesday() {

        // when:
        sut.navigate(PREVIOUS, WED);

        calendarStartIs(TUE_MIDNIGHT);
        calendarEndIs(TUE_MAX);

        // and:
        calendarPickerIs(TUE);
    }

    @Test
    void given_wednesdayIsCurrentlyConfiguredAsStartDate_when_next_then_calendarRangeIsThursday_and_calendarPickerIsThursday() {

        // when:
        sut.navigate(NEXT, WED);

        calendarStartIs(THU_MIDNIGHT);
        calendarEndIs(THU_MAX);

        // and:
        calendarPickerIs(THU);
    }


    @Test
    void given_currentDateIsThursday_when_atDate_then_calendarRangeIsThursday_and_calendarPickerIsThursday() {

        // when:
        sut.navigate(AT_DATE, THU);

        calendarStartIs(THU.atStartOfDay());
        calendarEndIs(THU.atTime(LocalTime.MAX));

        // and:
        calendarPickerIs(THU);
    }

    private void calendarEndIs(LocalDateTime expectedEnd) {
        verify(calendar).setEndDate(expectedEnd);
    }

    private void calendarStartIs(LocalDateTime expectedStart) {
        verify(calendar).setStartDate(expectedStart);
    }

    private void calendarPickerIs(LocalDate expectedDate) {
        verify(calendarRangePicker).setValue(expectedDate);
    }
}