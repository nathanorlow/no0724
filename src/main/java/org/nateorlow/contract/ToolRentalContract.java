package org.nateorlow.contract;

import lombok.Builder;
import org.nateorlow.charge.ChargeCalculator;
import org.nateorlow.charge.ChargeListing;
import org.nateorlow.charge.ChargeableDayCounter;
import org.nateorlow.charge.RentalPeriod;
import org.nateorlow.tool.Tool;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

//See CheckoutTotalTest for testing of this class

/**
 * This class contains data for creating a rental contract
 * It can be passed into a ContractPrinter to output
 */
@Builder
public class ToolRentalContract implements Contract {
    Tool tool;
    RentalPeriod rentalPeriod;
    ChargeListing chargeListing;
    int discountPercent;
    ChargeCalculator chargeCalculator;
    ChargeableDayCounter chargeableDayCounter;

    public static final int ROUND_TO_PLACES = 2;
    public static final RoundingMode HALF_UP = RoundingMode.HALF_UP;
    public static final DateTimeFormatter MDY_FORMATTER = DateTimeFormatter.ofPattern("M/d/y");

    //toStringList is recommended instead
    public String toString(){
        return String.join("\n", toStringList());
    }

    /**
     * This can be called by a ContractPrinter, to output the lines somewhere such as a log
     * @return A list of lines representing this contract
     */
    public List<String> toStringList(){
        List<String> outputList = new LinkedList<>();
        printToolSectionToList(outputList);
        printDateSectionToList(outputList);
        printChargeSectionToList(outputList);
        return outputList;
    }

    void printToolSectionToList(List<String> outputList){
        outputList.add("Tool code: " + tool.getCode());
        outputList.add("Tool type: " + tool.getType());
        outputList.add("Tool brand: " + tool.getBrand());
    }

    void printDateSectionToList(List<String> outputList){
        outputList.add("Rental days: " + rentalPeriod.getRentalDayCount());
        outputList.add("Check out date: " + rentalPeriod.getStartDate().format(MDY_FORMATTER));
        outputList.add("Due date: " + rentalPeriod.getEndDate().format(MDY_FORMATTER));
    }

    /**
     * This outputs the charge section, but it also computes and formats some of the charge data
     * using the chargeableDayCounter and chargeCalculator
     * @param outputList List of lines representing the charge section of this contract
     */
    void printChargeSectionToList(List<String> outputList){
        final int chargeableDays = chargeableDayCounter.chargeableDaysForListing(rentalPeriod, chargeListing);
        final BigDecimal baseCharge = chargeCalculator.calculateBaseCharge(rentalPeriod, chargeableDays, chargeListing)
                                                      .setScale(ROUND_TO_PLACES, HALF_UP);
        final BigDecimal discountCharge = chargeCalculator.calculateDiscountCharge(baseCharge, discountPercent)
                                                          .setScale(ROUND_TO_PLACES, HALF_UP);
        final BigDecimal finalCharge = baseCharge.subtract(discountCharge);
        outputList.add("Daily rental charge: $" + chargeListing.getDailyCharge());
        outputList.add("Charge days: " + chargeableDays);
        outputList.add("Pre-discount charge: $" + baseCharge);
        outputList.add("Discount percent: " + discountPercent + "%");
        outputList.add("Discount amount: $" + discountCharge);
        outputList.add("Final Charge: $" + finalCharge);
    }

}
