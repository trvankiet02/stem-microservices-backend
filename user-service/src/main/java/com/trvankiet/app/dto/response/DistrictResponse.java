package com.trvankiet.app.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DistrictResponse {

    private Integer id;
    private String name;

}
