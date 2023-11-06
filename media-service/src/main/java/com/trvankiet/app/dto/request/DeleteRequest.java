package com.trvankiet.app.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class DeleteRequest {

    private List<String> refUrls;

}
