package com.trvankiet.app.config.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.trvankiet.app.constant.GenericResponse;
import com.trvankiet.app.exception.wrapper.MyFeignException;
import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.IOException;
import java.io.InputStream;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String s, Response response) {
        GenericResponse genericResponse;
        try {
            InputStream body = response.body().asInputStream();
            ObjectMapper mapper = new ObjectMapper();
            genericResponse = mapper.readValue(body, GenericResponse.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new MyFeignException(genericResponse.getMessage());
    }
}
