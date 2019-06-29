package com.practice.lcn.calc.exception;

/**
 * error indicating the symbol is undefined due to unknown reason.
 * @author lcn
 */
public class SymbolUndefinedException extends RuntimeException {
    public SymbolUndefinedException() {
        super("Error: Symbol is undefined");
    }
}
