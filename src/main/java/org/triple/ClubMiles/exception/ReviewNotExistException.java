package org.triple.ClubMiles.exception;

public class ReviewNotExistException extends CustomException{

    public ReviewNotExistException(String errCode, String desc) {
        super(errCode, desc);
    }
}
