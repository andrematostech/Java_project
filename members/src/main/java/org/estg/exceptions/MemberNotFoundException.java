package org.estg.exceptions;

public class MemberNotFoundException extends RuntimeException {

    public MemberNotFoundException(String message) {
        super("(MembersService) - " + message);
    }

    public MemberNotFoundException(String message, Throwable cause) {
        super("(MembersService) - " + message, cause);
    }
}
