package org.triple.ClubMiles.controller.common;


import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response<T> {

    private final static String OK_RESPONSE = "OK";
    private final static String ERROR_RESPONSE = "ERROR";

    @ApiModelProperty(value = "응답 결과[OK|ERROR]", required = true)
    private String resultCode;
    @ApiModelProperty(value = "부가 메시지", required = true)
    private String description;
    @ApiModelProperty(value = "응답 데이타")
    private T data;

    public static <T> Response<T> OK(String desc, T data) {
        return Response.<T>builder()
                .resultCode(OK_RESPONSE)
                .description(desc == null ? "" : desc)
                .data(data)
                .build();
    }

    public static Response ERROR(String desc) {
        return Response.ERROR_WITH(ERROR_RESPONSE, desc);
    }

    public static Response ERROR_WITH(String errCode, String desc) {
        return Response.builder()
                .resultCode(errCode)
                .description(desc == null ? "" : desc)
                .build();
    }

    public static <T> Response<T> ERROR_WITH(String errCode, String desc, T data) {
        return Response.<T>builder()
                .resultCode(errCode)
                .description(desc == null ? "" : desc)
                .data(data)
                .build();
    }
}
