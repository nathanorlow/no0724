package org.nateorlow;

import lombok.Builder;
import org.nateorlow.charge.*;
import org.nateorlow.contract.ContractPrinter;
import org.nateorlow.contract.LogContractPrinter;
import org.nateorlow.contract.ToolRentalContract;
import org.nateorlow.tool.Inventory;
import org.nateorlow.tool.Tool;

import java.math.BigDecimal;

//For classes with several components, create them with builders instead of constructors
//this avoids having many constructors if component classes keep getting added
//It also makes it easier to add components without needing to remember the order

@Builder
public class Checkout {
    Inventory inventory;
    ChargeableDayCounter chargeableDayCounter;
    ChargeCalculator chargeCalculator;
    ContractPrinter contractPrinter;

    public static void main(String[] args) {
        //Maybe these could be injected using a dependency injection framework
        Inventory inventory = new Inventory();
        ChargeableDayCounter holidayChargeableDayCounter = new HolidayChargeableDayCounter();
        UnroundedChargeCalculator unroundedChargeCalculator = new UnroundedChargeCalculator();
        ContractPrinter logContractPrinter = new LogContractPrinter();
        Checkout checkout = Checkout.builder()
                                    .inventory(inventory)
                                    .chargeableDayCounter(holidayChargeableDayCounter)
                                    .chargeCalculator(unroundedChargeCalculator)
                                    .contractPrinter(logContractPrinter)
                                    .build();

        addSampleTools(inventory);
        addSampleChargeListings(unroundedChargeCalculator);
        //checkout.checkoutUsingInput("LADW", 3, 10, "7/2/20");
        checkout.checkoutUsingInput(args[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), args[3]);
    }

    //This was the order mentioned under Checkout

    /**
     * Checkout the tool --
     * Create a rental contract and print the contract
     * @param toolCode
     * @param rentalDayCount
     * @param discountPercent
     * @param checkoutDateStringMDY
     */
    void checkoutUsingInput(String toolCode, int rentalDayCount, int discountPercent, String checkoutDateStringMDY) {
        validateInputArguments(toolCode, rentalDayCount, discountPercent, checkoutDateStringMDY);
        final Tool tool = inventory.lookupToolByCode(toolCode);
        final RentalPeriod rentalPeriod = new RentalPeriod(checkoutDateStringMDY, rentalDayCount);
        final ChargeListing chargeListingForTool = chargeCalculator.lookupChargeListingByToolType(tool.getType());
        ToolRentalContract toolRentalContract = ToolRentalContract.builder()
                                                                  .tool(tool)
                                                                  .rentalPeriod(rentalPeriod)
                                                                  .chargeListing(chargeListingForTool)
                                                                  .discountPercent(discountPercent)
                                                                  .chargeableDayCounter(chargeableDayCounter)
                                                                  .chargeCalculator(chargeCalculator)
                                                                  .build();
        contractPrinter.printContract(toolRentalContract);
    }

    /**
     * Validate input, and throw a runtime exception for invalid data
     * @param toolCode
     * @param rentalDayCount
     * @param discountPercent
     * @param checkoutDateStringMDY
     */
    void validateInputArguments(String toolCode, int rentalDayCount, int discountPercent, String checkoutDateStringMDY){
        if(toolCode == null || toolCode.isEmpty()) {
            throw new IllegalArgumentException("Input tool code must not be null or empty");
        }else if(checkoutDateStringMDY == null || checkoutDateStringMDY.isEmpty()) {
            throw new IllegalArgumentException("Input check out date must not be null or empty");
        }else if(rentalDayCount <= 0){
            throw new IllegalArgumentException("Rental day count must be at least 1");
        }else if(discountPercent < 0 || discountPercent > 100){
            throw new IllegalArgumentException("Discount percentage must be in range 0 to 100");
        }
    }

    //This data might normally be in a database
    private static void addSampleChargeListings(UnroundedChargeCalculator unroundedChargeCalculator) {
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