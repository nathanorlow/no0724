package org.nateorlow.tool;

import lombok.Builder;
import lombok.Getter;

//Assumes there may be many tools, so creating a generic Tool, rather than objects like Chainsaw
@Getter
@Builder
public class Tool {
    String code;
    String type;
    String brand;

    //These might be in a database to avoid having to maintain them here
    //It's helpful to have them as constants, since they are used as keys in ChargeListing
    public static final String CHAINSAW = "Chainsaw";
    public static final String LADDER = "Ladder";
    public static final String JACKHAMMER = "Jackhammer";
}
