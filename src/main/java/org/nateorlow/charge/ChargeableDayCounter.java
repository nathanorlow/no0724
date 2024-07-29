package org.nateorlow.charge;

public interface ChargeableDayCounter {
    Integer chargeableDaysForListing(RentalPeriod rentalPeriod, ChargeListing chargeListing);
}
