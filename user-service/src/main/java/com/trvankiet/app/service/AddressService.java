package com.trvankiet.app.service;

import com.trvankiet.app.dto.request.AddressRequest;
import com.trvankiet.app.dto.request.DistrictRequest;
import com.trvankiet.app.dto.request.SchoolRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import org.springframework.http.ResponseEntity;

public interface AddressService {
    ResponseEntity<GenericResponse> getAllProvinces();

    ResponseEntity<GenericResponse> getDistricts(Integer provinceId);

    ResponseEntity<GenericResponse> getSchools(Integer districtId);

    ResponseEntity<GenericResponse> getAllProvincesForAdmin(String token, Integer page, Integer size);

    ResponseEntity<GenericResponse> addProvince(String token, AddressRequest addressRequest);

    ResponseEntity<GenericResponse> updateProvince(String token, Integer id, AddressRequest addressRequest);

    ResponseEntity<GenericResponse> deleteProvince(String token, Integer id);

    ResponseEntity<GenericResponse> getDistrictsForAdmin(String token, Integer provinceId);

    ResponseEntity<GenericResponse> addDistrict(String token, DistrictRequest addressRequest);

    ResponseEntity<GenericResponse> updateDistrict(String token, Integer id, DistrictRequest addressRequest);

    ResponseEntity<GenericResponse> deleteDistrict(String token, Integer id);

    ResponseEntity<GenericResponse> getSchoolsForAdmin(String token, Integer districtId);

    ResponseEntity<GenericResponse> addSchool(String token, SchoolRequest addressRequest);

    ResponseEntity<GenericResponse> updateSchool(String token, Integer id, SchoolRequest addressRequest);

    ResponseEntity<GenericResponse> deleteSchool(String token, Integer id);
}
