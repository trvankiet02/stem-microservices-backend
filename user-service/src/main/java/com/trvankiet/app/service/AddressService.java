package com.trvankiet.app.service;

import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface AddressService {
    ResponseEntity<GenericResponse> getAllProvinces();

    ResponseEntity<GenericResponse> getDistricts(Integer provinceId);

    ResponseEntity<GenericResponse> getSchools(Integer districtId);
}
