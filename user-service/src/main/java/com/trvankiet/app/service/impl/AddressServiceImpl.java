package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.response.DistrictResponse;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.dto.response.ProvinceResponse;
import com.trvankiet.app.dto.response.SchoolResponse;
import com.trvankiet.app.repository.DistrictRepository;
import com.trvankiet.app.repository.ProvinceRepository;
import com.trvankiet.app.repository.SchoolRepository;
import com.trvankiet.app.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final SchoolRepository schoolRepository;

    @Override
    public ResponseEntity<GenericResponse> getAllProvinces() {
        log.info("AddressServiceImpl, getAllProvinces");
        List<ProvinceResponse> provinceResponses = provinceRepository.findAll()
                .stream()
                .map(province -> ProvinceResponse.builder()
                        .id(province.getId())
                        .name(province.getName())
                        .build())
                .toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Get all provinces successfully")
                .result(provinceResponses)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getDistricts(Integer provinceId) {
        log.info("AddressServiceImpl, getDistricts");
        List<DistrictResponse> districtResponses = districtRepository.findAllByProvinceId(provinceId)
                .stream()
                .map(district -> DistrictResponse.builder()
                        .id(district.getId())
                        .name(district.getName())
                        .build())
                .toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Get all districts successfully")
                .result(districtResponses)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getSchools(Integer districtId) {
        log.info("AddressServiceImpl, getSchools");
        List<SchoolResponse> schoolResponses = schoolRepository.findAllByDistrictId(districtId)
                .stream()
                .map(school -> SchoolResponse.builder()
                        .id(school.getId())
                        .name(school.getName())
                        .build())
                .toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Get all schools successfully")
                .result(schoolResponses)
                .build());
    }
}
