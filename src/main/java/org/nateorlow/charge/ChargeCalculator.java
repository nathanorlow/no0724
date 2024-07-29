package org.nateorlow.charge;

import java.math.BigDecimal;

public interface ChargeCalculator {
    void addChargeListing(ChargeListing chargeListing);
    ChargeListing lookupChargeListingByToolType(String toolType);

    BigDecimal calculateBaseCharge(int chargeableDays, ChargeListing chargeListing);

    BigDecimal calculateDiscountCharge(BigDecimal baseCharge, int discountPercentage);
}
