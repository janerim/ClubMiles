package org.triple.ClubMiles.controller.common;

public class ErrorCode {

    //이벤트 관련 ERROR 100번대
    public static final String NOT_VALID_ACTION = "100";

    //파일 관련 ERROR 200번대
    public static final String PHOTO_NOT_EXIST = "200";
    public static final String FILE_NOT_VALID = "201";

    //사용자 관련 ERROR 300번대
    public static final String USER_NOT_EXIST = "300";

    //장소 관련 ERROR 400번대
    public static final String PLACE_NOT_EXIST = "400";

    //리뷰 관련 ERROR 500번대
    public static final String REVIEW_NOT_EXIST = "500";
    public static final String REVIEW_ALREADY_EXIST = "501";


}
