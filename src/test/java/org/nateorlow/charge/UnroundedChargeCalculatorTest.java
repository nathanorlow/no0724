package org.nateorlow.charge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UnroundedChargeCalculatorTest {

    UnroundedChargeCalculator unroundedChargeCalculator;
    @BeforeEach
    void setUp() {
        unroundedChargeCalculator = new UnroundedChargeCalculator();
    }

    @Test
    void testChargeListingStructure() {
        assertNull(unroundedChargeCalculator.lookupChargeListingByToolType("tooltype"),
                "before insertion, tooltype does not exist");
        final ChargeListing sampleChargelisting = ChargeListing.builder().toolType("tooltype").build();
        unroundedChargeCalculator.addChargeListing(sampleChargelisting);
        assertNotNull(unroundedChargeCalculator.lookupChargeListingByToolType("tooltype"),
        "After insertion, tooltype exists");
    }

    @Test
    void calculateBaseCharge() {
        final RentalPeriod twoDays = new RentalPeriod("1/1/24", 2);
        final ChargeListing threeFiftyChargeListing =
                ChargeListing.builder().toolType("threefifty").dailyCharge(new BigDecimal("3.50")).build();
        final ChargeListing oneNinetyNineChargeListing =
                ChargeListing.builder().toolType("oneninetynine").dailyCharge(new BigDecimal("1.99")).build();

        assertEquals(new BigDecimal("7.00"),
                unroundedChargeCalculator.calculateBaseCharge(twoDays, 2, threeFiftyChargeListing));

        assertEquals(new BigDecimal("3.98"),
                unroundedChargeCalculator.calculateBaseCharge(twoDays, 2, oneNinetyNineChargeListing));
    }

    @Test
    void calculateDiscountCharge() {
        BigDecimal quarterDiscount = unroundedChargeCalculator.calculateDiscountCharge(new BigDecimal("4.00"), 25);
        assertEquals( new BigDecimal("1.00"), quarterDiscount);
        BigDecimal noDiscount = unroundedChargeCalculator.calculateDiscountCharge(new BigDecimal("4.00"), 0);
        assertEquals( new BigDecimal("0.00"), noDiscount);
    }
}