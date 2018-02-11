package com.slb.manara;

public class WellTimestampConversionUnitException extends Exception {
    // Parameterless Constructor
    public WellTimestampConversionUnitException() {
    }
    // Constructor that accepts a message
    public WellTimestampConversionUnitException(String message) {
        super(message);
    }

    public WellTimestampConversionUnitException(Throwable cause) {
        super(cause);
    }


    public WellTimestampConversionUnitException(String message,Throwable cause) {
        super(message,cause);
    }
}
