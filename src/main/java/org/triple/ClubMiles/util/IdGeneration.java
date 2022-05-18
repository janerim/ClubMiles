package org.triple.ClubMiles.util;


import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * ID 생성
 */
@Configuration
public class IdGeneration {


    /**
     * ID 형식
     *  8자-4자-4자-4자-12자 = 총 36자('-'포함)
     */
    public String idGenerator(){
        UUID id = UUID.randomUUID();
        return id.toString();
    }
}
