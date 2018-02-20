package com.slb.manara;

public class WellTimestampConversionUnitException extends Exception {
    // Parameterless Constructor
    public WellTimestampConversionUnitException() {
    }
    // Constructor that accepts a message
    public WellTimestampConversionUnitException(String reason, String statement) {
        super(reason+" : "+statement);
    }

    public WellTimestampConversionUnitException(String reason, String statement,Throwable cause) {
        super(reason+" : "+statement,cause);
    }
}
