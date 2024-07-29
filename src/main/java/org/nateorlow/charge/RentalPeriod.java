package org.nateorlow.charge;

import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
public class RentalPeriod {
    LocalDate startDate;
    Integer rentalDayCount;
    LocalDate firstChargeDate; //next day after the start date
    LocalDate endDate; //this is typically computed based on startDate and rentalDayCount

    public RentalPeriod(String startDateStringMDY, Integer rentalDayCount){
        this.startDate = LocalDate.parse(startDateStringMDY, DateTimeFormatter.ofPattern("M/d/y"));
        this.rentalDayCount = rentalDayCount;
        this.computeFirstChargeDate();
        this.computeEndDate();
    }

    void computeFirstChargeDate(){
        firstChargeDate = startDate.plusDays(1);
    }

    //This can be used when printing contracts
    void computeEndDate(){
        endDate = startDate.plusDays(rentalDayCount);
    }

}
