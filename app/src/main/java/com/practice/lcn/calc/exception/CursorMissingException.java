package com.practice.lcn.calc.exception;

/**
 * error indicating the cursor in the equation is missing.
 * @author lcn
 */
public class CursorMissingException extends RuntimeException {
    public CursorMissingException() {
        super("Error: Cursor is missing");
    }
}
