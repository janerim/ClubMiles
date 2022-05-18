package org.triple.ClubMiles.exception;

public class UserNotExistException extends CustomException{

    public UserNotExistException(String errCode, String desc) {
        super(errCode, desc);
    }
}
