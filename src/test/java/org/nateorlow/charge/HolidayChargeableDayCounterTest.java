package org.nateorlow.charge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class HolidayChargeableDayCounterTest {

    HolidayChargeableDayCounter holidayChargeableDayCounter;
    @BeforeEach
    void setup(){
        holidayChargeableDayCounter = new HolidayChargeableDayCounter();
    }

    @Test
    void testChargeableDaysForListing(){
        RentalPeriod fourDaysSeptSecond = new RentalPeriod("7/2/15", 4);
        ChargeListing allChargeableListing =
                ChargeListing.builder().weekdayChargeable(true).weekendChargeable(true).holidayChargeable(true).build();
        ChargeListing weekendChargeableListing =
                ChargeListing.builder().weekdayChargeable(false).weekendChargeable(true).holidayChargeable(false).build();

        int chargeableDaysAllFour =
                holidayChargeableDayCounter.chargeableDaysForListing(fourDaysSeptSecond, allChargeableListing);
        assertEquals(4, chargeableDaysAllFour);

        int chargeableDaysWeekendTwo =
                holidayChargeableDayCounter.chargeableDaysForListing(fourDaysSeptSecond, weekendChargeableListing);
        assertEquals(2, chargeableDaysWeekendTwo);
    }

    @Test
    void testIsObservedIndependenceDay(){
        LocalDate saturdayFourth = LocalDate.of(2020, Month.JULY, 4);
        LocalDate fridayThird = LocalDate.of(2020, Month.JULY, 3);
        assertFalse(holidayChargeableDayCounter.isObservedIndependenceDay(saturdayFourth));
        assertTrue(holidayChargeableDayCounter.isObservedIndependenceDay(fridayThird));

        LocalDate sundayFourth = LocalDate.of(2021, Month.JULY, 4);
        LocalDate mondayFifth = LocalDate.of(2021, Month.JULY, 5);
        assertFalse(holidayChargeableDayCounter.isObservedIndependenceDay(sundayFourth));
        assertTrue(holidayChargeableDayCounter.isObservedIndependenceDay(mondayFifth));

        LocalDate januaryFirst = LocalDate.of(2024, Month.JANUARY, 1);
        LocalDate julyEight = LocalDate.of(2024, Month.JULY, 8);
        LocalDate thursdayJulyFour = LocalDate.of(2024, Month.JULY, 4);
        assertFalse(holidayChargeableDayCounter.isObservedIndependenceDay(januaryFirst));
        assertFalse(holidayChargeableDayCounter.isObservedIndependenceDay(julyEight));
        assertTrue(holidayChargeableDayCounter.isObservedIndependenceDay(thursdayJulyFour));
    }

    @Test
    void testIsLaborDay(){
        LocalDate septFirst = LocalDate.of(2020, Month.SEPTEMBER, 1);
        LocalDate septSeven = LocalDate.of(2020, Month.SEPTEMBER, 7);
        LocalDate septFourteen = LocalDate.of(2020, Month.SEPTEMBER, 14);
        assertFalse(holidayChargeableDayCounter.isLaborDay(septFirst));
        assertTrue(holidayChargeableDayCounter.isLaborDay(septSeven));
        assertFalse(holidayChargeableDayCounter.isLaborDay(septFourteen));

        LocalDate janFirst = LocalDate.of(2024, Month.JANUARY, 1);
        assertFalse(holidayChargeableDayCounter.isLaborDay(janFirst));
    }

    @Test
    void testIsHoliday(){
        LocalDate septSeven = LocalDate.of(2020, Month.SEPTEMBER, 7);
        LocalDate thursdayJulyFour = LocalDate.of(2024, Month.JULY, 4);
        LocalDate janFirst = LocalDate.of(2024, Month.JANUARY, 1);
        assertTrue(holidayChargeableDayCounter.isHoliday(septSeven));
        assertTrue(holidayChargeableDayCounter.isHoliday(thursdayJulyFour));
        assertFalse(holidayChargeableDayCounter.isHoliday(janFirst));
    }
}