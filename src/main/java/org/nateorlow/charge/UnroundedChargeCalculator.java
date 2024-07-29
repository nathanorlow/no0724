package org.nateorlow.charge;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * An unrounded charge calculator is useful if you want to do calculations,
 * and do rounding when it's time to print the results.
 *
 * Rounding early can cause roundoff errors, so it's plausible to round as late as possible
 */
public class UnroundedChargeCalculator implements ChargeCalculator {
    Map<String, ChargeListing> toolTypeToChargeListing;

    public UnroundedChargeCalculator(){
        this.toolTypeToChargeListing = new HashMap<>();
    }

    @Override
    public void addChargeListing(ChargeListing chargeListing){
        toolTypeToChargeListing.put(chargeListing.getToolType(), chargeListing);
    }

    public ChargeListing lookupChargeListingByToolType(String toolType){
        return toolTypeToChargeListing.get(toolType);
    }

    @Override
    public BigDecimal calculateBaseCharge(RentalPeriod rentalPeriod, int chargeableDays, ChargeListing chargeListing){
        return chargeListing.getDailyCharge().multiply(new BigDecimal(chargeableDays));
    }

    @Override
    public BigDecimal calculateDiscountCharge(BigDecimal baseCharge, int discountPercentage){
        return baseCharge.multiply(new BigDecimal(discountPercentage)).divide(new BigDecimal(100));
    }

}
