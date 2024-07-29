package org.nateorlow;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.nateorlow.charge.*;
import org.nateorlow.contract.LogContractPrinter;
import org.nateorlow.tool.Inventory;
import org.nateorlow.tool.Tool;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutTotalTest {
        Checkout checkout;
        LogContractPrinter contractPrinterSpy; //will be used to view the output
    @BeforeEach
    void setUp() {
        Inventory inventory = new Inventory();
        ChargeableDayCounter holidayChargeableDayCounter = new HolidayChargeableDayCounter();
        ChargeCalculator unroundedChargeCalculator = new UnroundedChargeCalculator();
        contractPrinterSpy = Mockito.spy(LogContractPrinter.class);
        checkout = Checkout.builder()
                            .inventory(inventory)
                            .chargeableDayCounter(holidayChargeableDayCounter)
                            .chargeCalculator(unroundedChargeCalculator)
                            .contractPrinter(contractPrinterSpy)
                            .build();

        addSampleTools(inventory);
        addSampleChargeListings(unroundedChargeCalculator);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testInvalidDiscount() {
        final String toolCode = "JAKR";
        final String checkoutDate = "09/03/15";
        final int rentalDays = 5;
        final int discount = 101;
        Exception exceptionThrown = assertThrows(IllegalArgumentException.class,
                () -> checkout.checkoutUsingInput(toolCode, rentalDays, discount, checkoutDate),
                "101 should be an invalid discount");
        assertTrue(exceptionThrown.getMessage().contains("Discount percentage"));
    }

    void verifyContractPrinterLines(String[] expectedOutput){
        for(String outputLine : expectedOutput) {
            Mockito.verify(contractPrinterSpy).outputToLog(outputLine);
        }
    }

    @Test
    void testIndependenceObservedFriday() {
        final String toolCode = "LADW";
        final String checkoutDate = "7/2/20";
        final int rentalDays = 3;
        final int discount = 10;
        checkout.checkoutUsingInput(toolCode, rentalDays, discount, checkoutDate);
        String[] expectedOutput =
                {"Tool code: LADW",
            "Tool type: Ladder",
            "Tool brand: Werner",
            "Rental days: 3",
            "Check out date: 7/2/20",
            "Due date: 7/5/20",
            "Daily rental charge: $1.99",
            "Charge days: 2",
            "Pre-discount charge: $3.98",
            "Discount percent: 10%",
            "Discount amount: $0.40",
            "Final Charge: $3.58"};

        verifyContractPrinterLines(expectedOutput);
    }

    @Test
    void testIndependenceObservedMultipleDays(){
        final String toolCode = "CHNS";
        final String checkoutDate = "7/2/15";
        final int rentalDays = 5;
        final int discount = 25;
        checkout.checkoutUsingInput(toolCode, rentalDays, discount, checkoutDate);
        String[] expectedOutput =
                {"Tool code: CHNS",
                        "Tool type: Chainsaw",
                        "Tool brand: Stihl",
                        "Rental days: 5",
                        "Check out date: 7/2/15",
                        "Due date: 7/7/15",
                        "Daily rental charge: $1.49",
                        "Charge days: 3",
                        "Pre-discount charge: $4.47",
                        "Discount percent: 25%",
                        "Discount amount: $1.12",
                        "Final Charge: $3.35"};

        verifyContractPrinterLines(expectedOutput);
    }

    @Test
    void testLaborDay(){
        final String toolCode = "JAKD";
        final String checkoutDate = "9/3/15";
        final int rentalDays = 6;
        final int discount = 0;
        checkout.checkoutUsingInput(toolCode, rentalDays, discount, checkoutDate);
        String[] expectedOutput =
                {"Tool code: JAKD",
                        "Tool type: Jackhammer",
                        "Tool brand: DeWalt",
                        "Rental days: 6",
                        "Check out date: 9/3/15",
                        "Due date: 9/9/15",
                        "Daily rental charge: $2.99",
                        "Charge days: 3",
                        "Pre-discount charge: $8.97",
                        "Discount percent: 0%",
                        "Discount amount: $0.00",
                        "Final Charge: $8.97"};

        verifyContractPrinterLines(expectedOutput);

    }

    @Test
    void testLongRental(){
        final String toolCode = "JAKR";
        final String checkoutDate = "7/2/15";
        final int rentalDays = 9;
        final int discount = 0;
        checkout.checkoutUsingInput(toolCode, rentalDays, discount, checkoutDate);
        String[] expectedOutput =
                {"Tool code: JAKR",
                        "Tool type: Jackhammer",
                        "Tool brand: Ridgid",
                        "Rental days: 9",
                        "Check out date: 7/2/15",
                        "Due date: 7/11/15",
                        "Daily rental charge: $2.99",
                        "Charge days: 5",
                        "Pre-discount charge: $14.95",
                        "Discount percent: 0%",
                        "Discount amount: $0.00",
                        "Final Charge: $14.95"};

        verifyContractPrinterLines(expectedOutput);

    }

    @Test
    void testHalfOffDiscount(){
        final String toolCode = "JAKR";
        final String checkoutDate = "7/2/20";
        final int rentalDays = 4;
        final int discount = 50;
        checkout.checkoutUsingInput(toolCode, rentalDays, discount, checkoutDate);
        String[] expectedOutput =
                {"Tool code: JAKR",
                        "Tool type: Jackhammer",
                        "Tool brand: Ridgid",
                        "Rental days: 4",
                        "Check out date: 7/2/20",
                        "Due date: 7/6/20",
                        "Daily rental charge: $2.99",
                        "Charge days: 1",
                        "Pre-discount charge: $2.99",
                        "Discount percent: 50%",
                        "Discount amount: $1.50",
                        "Final Charge: $1.49"};

        verifyContractPrinterLines(expectedOutput);


    }

    @Test
    void validateInputArgumentsDiscount() {
        final String validToolCode = "LADW";
        final String validDate = "4/4/14";
        final int validDiscount = 50;
        final int validRentalDays = 2;
        Exception exceptionThrown;
        exceptionThrown = assertThrows(IllegalArgumentException.class,
                () -> checkout.validateInputArguments(validToolCode, 0, validDiscount, validDate),
                "0 should be an invalid rental days");
        assertTrue(exceptionThrown.getMessage().contains("Rental day count"));
        exceptionThrown = assertThrows(IllegalArgumentException.class,
                () -> checkout.validateInputArguments(validToolCode, validRentalDays, -1, validDate),
                "0 should be an invalid rental days");
        assertTrue(exceptionThrown.getMessage().contains("Discount percentage"));
    }

    //This data might normally be in a database
    private static void addSampleChargeListings(ChargeCalculator unroundedChargeCalculator) {
        final ChargeListing ladderChargeListing = ChargeListing.builder().toolType(Tool.LADDER)
                                                               .dailyCharge(new BigDecimal("1.99"))
                                                               .weekdayChargeable(true).weekendChargeable(true).holidayChargeable(false).build();
        unroundedChargeCalculator.addChargeListing(ladderChargeListing);
        final ChargeListing chainsawChargeListing = ChargeListing.builder().toolType(Tool.CHAINSAW)
                                                                 .dailyCharge(new BigDecimal("1.49"))
                                                                 .weekdayChargeable(true).weekendChargeable(false).holidayChargeable(true).build();
        unroundedChargeCalculator.addChargeListing(chainsawChargeListing);
        final ChargeListing jackhammerChargeListing = ChargeListing.builder().toolType(Tool.JACKHAMMER)
                                                                   .dailyCharge(new BigDecimal("2.99"))
                                                                   .weekdayChargeable(true).weekendChargeable(false).holidayChargeable(false).build();
        unroundedChargeCalculator.addChargeListing(jackhammerChargeListing);
    }

    //This data might normally be in a database
    private static void addSampleTools(Inventory inventory) {
        final Tool chainsaw = Tool.builder().code("CHNS").type(Tool.CHAINSAW).brand("Stihl").build();
        inventory.addTool(chainsaw);
        final Tool ladder = Tool.builder().code("LADW").type(Tool.LADDER).brand("Werner").build();
        inventory.addTool(ladder);
        final Tool dewaltJackhammer = Tool.builder().code("JAKD").type(Tool.JACKHAMMER).brand("DeWalt").build();
        inventory.addTool(dewaltJackhammer);
        final Tool ridgidJackhammer = Tool.builder().code("JAKR").type(Tool.JACKHAMMER).brand("Ridgid").build();
        inventory.addTool(ridgidJackhammer);
    }

}