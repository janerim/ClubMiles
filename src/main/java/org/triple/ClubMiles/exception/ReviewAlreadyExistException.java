package org.triple.ClubMiles.exception;

public class ReviewAlreadyExistException extends CustomException{

    public ReviewAlreadyExistException(String errCode, String desc) {
        super(errCode, desc);
    }
}

