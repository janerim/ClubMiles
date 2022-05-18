package org.triple.ClubMiles.exception;

public class AttachedPhotoNotExistException extends CustomException {
    public AttachedPhotoNotExistException(String errCode, String desc) {
        super(errCode, desc);
    }
}
