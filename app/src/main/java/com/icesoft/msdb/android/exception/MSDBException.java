package com.icesoft.msdb.android.exception;

public class MSDBException extends RuntimeException {

    public MSDBException(String cause) { super(cause); }
    public MSDBException(Throwable cause) {
        super(cause);
    }
}
