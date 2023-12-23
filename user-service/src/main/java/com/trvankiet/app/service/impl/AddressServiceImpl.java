package com.trvankiet.app.service.impl;

import com.trvankiet.app.dto.AddressDto;
import com.trvankiet.app.dto.request.AddressRequest;
import com.trvankiet.app.dto.request.DistrictRequest;
import com.trvankiet.app.dto.request.SchoolRequest;
import com.trvankiet.app.dto.response.DistrictResponse;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.dto.response.ProvinceResponse;
import com.trvankiet.app.dto.response.SchoolResponse;
import com.trvankiet.app.entity.District;
import com.trvankiet.app.entity.Province;
import com.trvankiet.app.entity.School;
import com.trvankiet.app.exception.wrapper.BadRequestException;
import com.trvankiet.app.exception.wrapper.NotFoundException;
import com.trvankiet.app.repository.DistrictRepository;
import com.trvankiet.app.repository.ProvinceRepository;
import com.trvankiet.app.repository.SchoolRepository;
import com.trvankiet.app.service.AddressService;
import com.trvankiet.app.service.MapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;import java.util.stream.Collector;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final SchoolRepository schoolRepository;
    private final MapperService mapperService;

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

    @Override
    public ResponseEntity<GenericResponse> getAllProvincesForAdmin(String token, Integer page, Integer size) {
        log.info("AddressServiceImpl, getAllProvincesForAdmin");
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").descending());
        Page<Province> provinceResponses = provinceRepository.findAll(pageable);
        List<AddressDto> provinceDtos = provinceResponses.getContent().stream()
        		.map(mapperService::mapToAddressDto).toList();
        		Map<String, Object> result = new HashMap();
        result.put("totalPage", provinceResponses.getTotalPages());
        result.put("totalElements", provinceResponses.getTotalElements());
        result.put("currentElements", provinceResponses.getNumberOfElements());
        result.put("currentPage", provinceResponses.getNumber());
        result.put("provinces", provinceResponses.getContent().stream().map(mapperService::mapToAddressDto).toList());
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Get all provinces successfully")
                .result(result)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> addProvince(String token, AddressRequest addressRequest) {
        log.info("AddressServiceImpl, addProvince");
        Province province = provinceRepository.findByCode(addressRequest.getCode()).orElse(null);
        if (province != null) {
            throw new BadRequestException("Province already exists");
        }
        province = provinceRepository.save(Province.builder()
                .code(addressRequest.getCode())
                .name(addressRequest.getName())
                .description(addressRequest.getDescription())
                .build());
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Add province successfully")
                .result(mapperService.mapToAddressDto(province))
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateProvince(String token, Integer id, AddressRequest addressRequest) {
        log.info("AddressServiceImpl, updateProvince");
        Province province = provinceRepository.findById(id).orElse(null);
        if (province == null) {
            throw new NotFoundException("Province not found");
        }
        province.setCode(addressRequest.getCode());
        province.setName(addressRequest.getName());
        province.setDescription(addressRequest.getDescription());
        provinceRepository.save(province);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Update province successfully")
                .result(mapperService.mapToAddressDto(province))
                .build());

    }

    @Override
    public ResponseEntity<GenericResponse> deleteProvince(String token, Integer id) {
        log.info("AddressServiceImpl, deleteProvince");
        Province province = provinceRepository.findById(id).orElse(null);
        if (province == null) {
            throw new NotFoundException("Province not found");
        }
        provinceRepository.delete(province);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Delete province successfully")
                .result(null)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getDistrictsForAdmin(String token, Integer provinceId) {
        log.info("AddressServiceImpl, getDistrictsForAdmin");
        List<AddressDto> districtResponses = districtRepository.findAllByProvinceId(provinceId)
                .stream()
                .map(mapperService::mapToAddressDto)
                .toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Get all districts successfully")
                .result(districtResponses)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> addDistrict(String token, DistrictRequest addressRequest) {
        log.info("AddressServiceImpl, addDistrict");
        Province province = provinceRepository.findById(addressRequest.getProvinceId()).orElse(null);
        if (province == null) {
            throw new NotFoundException("Province not found");
        }
        District district = districtRepository.findByCode(addressRequest.getCode()).orElse(null);
        if (district != null) {
            throw new BadRequestException("District already exists");
        }
        district = districtRepository.save(District.builder()
                .code(addressRequest.getCode())
                .name(addressRequest.getName())
                .description(addressRequest.getDescription())
                .province(province)
                .build());
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Add district successfully")
                .result(mapperService.mapToAddressDto(district))
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateDistrict(String token, Integer id, DistrictRequest addressRequest) {
        log.info("AddressServiceImpl, updateDistrict");
        District district = districtRepository.findById(id).orElse(null);
        if (district == null) {
            throw new NotFoundException("District not found");
        }
        district.setCode(addressRequest.getCode());
        district.setName(addressRequest.getName());
        district.setDescription(addressRequest.getDescription());
        if (!district.getProvince().getId().equals(addressRequest.getProvinceId())) {
            Province province = provinceRepository.findById(addressRequest.getProvinceId()).orElse(null);
            if (province == null) {
                throw new NotFoundException("Province not found");
            }
            district.setProvince(province);
        }
        districtRepository.save(district);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Update district successfully")
                .result(mapperService.mapToAddressDto(district))
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> deleteDistrict(String token, Integer id) {
        log.info("AddressServiceImpl, deleteDistrict");
        District district = districtRepository.findById(id).orElse(null);
        if (district == null) {
            throw new NotFoundException("District not found");
        }
        districtRepository.delete(district);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Delete district successfully")
                .result(null)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getSchoolsForAdmin(String token, Integer districtId) {
        log.info("AddressServiceImpl, getSchoolsForAdmin");
        List<AddressDto> schoolResponses = schoolRepository.findAllByDistrictId(districtId)
                .stream()
                .map(mapperService::mapToAddressDto)
                .toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Get all schools successfully")
                .result(schoolResponses)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> addSchool(String token, SchoolRequest addressRequest) {
        log.info("AddressServiceImpl, addSchool");
        District district = districtRepository.findById(addressRequest.getDistrictId()).orElse(null);
        if (district == null) {
            throw new NotFoundException("District not found");
        }
        School school = schoolRepository.findByCode(addressRequest.getCode()).orElse(null);
        if (school != null) {
            throw new BadRequestException("School already exists");
        }
        school = schoolRepository.save(School.builder()
                .code(addressRequest.getCode())
                .name(addressRequest.getName())
                .description(addressRequest.getDescription())
                .district(district)
                .build());
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Add school successfully")
                .result(mapperService.mapToAddressDto(school))
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateSchool(String token, Integer id, SchoolRequest addressRequest) {
        log.info("AddressServiceImpl, updateSchool");
        School school = schoolRepository.findById(id).orElse(null);
        if (school == null) {
            throw new NotFoundException("School not found");
        }
        school.setCode(addressRequest.getCode());
        school.setName(addressRequest.getName());
        school.setDescription(addressRequest.getDescription());
        if (!school.getDistrict().getId().equals(addressRequest.getDistrictId())) {
            District district = districtRepository.findById(addressRequest.getDistrictId()).orElse(null);
            if (district == null) {
                throw new NotFoundException("District not found");
            }
            school.setDistrict(district);
        }
        schoolRepository.save(school);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Update school successfully")
                .result(mapperService.mapToAddressDto(school))
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> deleteSchool(String token, Integer id) {
        log.info("AddressServiceImpl, deleteSchool");
        School school = schoolRepository.findById(id).orElse(null);
        if (school == null) {
            throw new NotFoundException("School not found");
        }
        schoolRepository.delete(school);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Delete school successfully")
                .result(null)
                .build());
    }


}
