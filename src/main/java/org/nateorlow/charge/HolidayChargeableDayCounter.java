package org.nateorlow.charge;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;

public class HolidayChargeableDayCounter implements ChargeableDayCounter {

    /**
     * Count number of chargeable days (from the rental period's total days), for a particular start date +
     * The policy in chargeListing will be used to determine which days are chargeable
     * @param rentalPeriod Rental period for calculation
     * @param chargeListing Policy for determining what is chargeable
     * @return number of chargeable days
     */
    @Override
    public Integer chargeableDaysForListing(RentalPeriod rentalPeriod, ChargeListing chargeListing){
        int chargeableDays = 0;
        for(int daysElapsed = 0; daysElapsed < rentalPeriod.getRentalDayCount(); daysElapsed++){
            LocalDate checkDate = rentalPeriod.getFirstChargeDate().plusDays(daysElapsed);
                if(isNonHolidayWeekday(checkDate) && chargeListing.isWeekdayChargeable()){
                    chargeableDays += 1;
                }else if(isNonHolidayWeekend(checkDate) && chargeListing.isWeekendChargeable()){
                    chargeableDays += 1;
                }else if(isHoliday(checkDate) && chargeListing.isHolidayChargeable()){
                    chargeableDays += 1;
                }
        }
        return chargeableDays;
    }

    //visible for testing
    /**
     * Check if a date is Observed Independence Day or Labor Day
     * @param localDate
     * @return true if it is one of those holidays
     */
    public boolean isHoliday(LocalDate localDate){
        return isObservedIndependenceDay(localDate) || isLaborDay(localDate);
    }

    //visible for testing
    /**
     * Check if a day is observed Independence Day (July 4, except nearest day if that's a weekend)
     * @param localDate
     * @return true if it is observed Independence Day
     */
    public boolean isObservedIndependenceDay(LocalDate localDate){
        //Independence day is always observed in July. If the date is July, then check the day and date
        if(Month.JULY != localDate.getMonth()){
            return false;
        }

        //Dates in July that are observed independence day:
        // Friday July 3 (since Saturday is July 4)
        // Monday July 5 (since Sunday is July 4)
        // [Weekday] July 4, since Saturday/Sunday July 4 are covered above
        return ( ((DayOfWeek.FRIDAY == localDate.getDayOfWeek()) && (localDate.getDayOfMonth() == 3))
            ||   ((DayOfWeek.MONDAY == localDate.getDayOfWeek()) && (localDate.getDayOfMonth() == 5))
            ||   ( !isWeekend(localDate) && (localDate.getDayOfMonth() == 4)) );
    }

    //visible for testing
    /**
     * Check if a day is Labor Day (first Monday in September)
     * @param localDate
     * @return
     */
    public boolean isLaborDay(LocalDate localDate){
        //the first Monday in September is a Monday in September with number 1 to 7
        return (Month.SEPTEMBER.equals(localDate.getMonth()))
                && DayOfWeek.MONDAY.equals(localDate.getDayOfWeek())
                && (localDate.getDayOfMonth() <= 7);
    }

    private boolean isWeekend(LocalDate localDate){
        return (DayOfWeek.SATURDAY.equals(localDate.getDayOfWeek())
                || DayOfWeek.SUNDAY.equals(localDate.getDayOfWeek()));
    }

    private boolean isNonHolidayWeekend(LocalDate localDate){
        return ( !isHoliday(localDate) && isWeekend(localDate));
    }

    private boolean isNonHolidayWeekday(LocalDate localDate){
        return ( !isHoliday(localDate) && !isNonHolidayWeekend(localDate));
    }

}
