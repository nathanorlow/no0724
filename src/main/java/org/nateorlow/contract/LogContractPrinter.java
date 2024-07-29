package org.nateorlow.contract;

public class LogContractPrinter implements ContractPrinter {
    public void printContract(Contract contract){
        for(String contractLine: contract.toStringList()){
            outputToLog(contractLine);
        }
    }

    //This can be spied on in unit tests
    public void outputToLog(String message){
        System.out.println(message);
    }
}
