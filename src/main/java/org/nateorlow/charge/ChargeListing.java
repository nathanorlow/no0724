package org.nateorlow.charge;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * This is a policy for determining what days are chargeable for a particular tool
 * Tools can be looked up by their toolType
 */
@Builder
@Getter
public class ChargeListing {
    String toolType;
    BigDecimal dailyCharge;
    boolean weekdayChargeable;
    boolean weekendChargeable;
    boolean holidayChargeable;
}
