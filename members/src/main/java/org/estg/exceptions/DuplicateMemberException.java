package org.estg.exceptions;

public class DuplicateMemberException extends RuntimeException {

    public DuplicateMemberException(String message) {
        super("(MembersService) - " + message);
    }

    public DuplicateMemberException(String message, Throwable cause) {
        super("(MembersService) - " + message, cause);
    }
}
