package com.example.lcn.calc.exception;

/**
 * error indicating the cursor in the equation is missing.
 */
public class CursorMissingException extends RuntimeException {
    public CursorMissingException() {
        super("Error: Cursor is missing");
    }
}
