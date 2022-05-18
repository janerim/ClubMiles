package org.triple.ClubMiles.exception;

public class PlaceNotExistException extends CustomException{

    public PlaceNotExistException(String errCode, String desc) {
        super(errCode, desc);
    }
}
