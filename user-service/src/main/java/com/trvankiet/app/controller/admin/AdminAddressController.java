package com.trvankiet.app.controller.admin;

import com.trvankiet.app.dto.request.AddressRequest;
import com.trvankiet.app.dto.request.DistrictRequest;
import com.trvankiet.app.dto.request.SchoolRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.service.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/addresses/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminAddressController {

    private final AddressService addressService;

    @GetMapping("/provinces")
    public ResponseEntity<GenericResponse> getAllProvinces(@RequestHeader("Authorization") String token,
                                                           @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                           @RequestParam(value = "size", defaultValue = "5") Integer size) {
        log.info("AdminAddressController, getAllProvinces");
        return addressService.getAllProvincesForAdmin(token, page - 1, size);
    }

    @PostMapping("/provinces")
    public ResponseEntity<GenericResponse> addProvince(@RequestHeader("Authorization") String token,
                                                       @RequestBody AddressRequest addressRequest) {
        log.info("AdminAddressController, addProvince");
        return addressService.addProvince(token, addressRequest);
    }

    @PutMapping("/provinces/{id}")
    public ResponseEntity<GenericResponse> updateProvince(@RequestHeader("Authorization") String token,
                                                          @PathVariable("id") Integer id,
                                                          @RequestBody AddressRequest addressRequest) {
        log.info("AdminAddressController, updateProvince");
        return addressService.updateProvince(token, id, addressRequest);
    }

    @DeleteMapping("/provinces/{id}")
    public ResponseEntity<GenericResponse> deleteProvince(@RequestHeader("Authorization") String token,
                                                          @PathVariable("id") Integer id) {
        log.info("AdminAddressController, deleteProvince");
        return addressService.deleteProvince(token, id);
    }

    @GetMapping("/districtsByProvince")
    public ResponseEntity<GenericResponse> districtsByProvince(@RequestHeader("Authorization") String token,
                                                               @RequestParam("pId") Integer provinceId,
                                                               @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                               @RequestParam(value = "size", defaultValue = "5") Integer size) {
        log.info("AdminAddressController, districtsByProvince");
        return addressService.getDistrictsForAdmin(token, provinceId, page - 1, size);
    }

    @PostMapping("/districts")
    public ResponseEntity<GenericResponse> addDistrict(@RequestHeader("Authorization") String token,
                                                       @RequestBody DistrictRequest addressRequest) {
        log.info("AdminAddressController, addDistrict");
        return addressService.addDistrict(token, addressRequest);
    }

    @PutMapping("/districts/{id}")
    public ResponseEntity<GenericResponse> updateDistrict(@RequestHeader("Authorization") String token,
                                                          @PathVariable("id") Integer id,
                                                          @RequestBody DistrictRequest addressRequest) {
        log.info("AdminAddressController, updateDistrict");
        return addressService.updateDistrict(token, id, addressRequest);
    }

    @DeleteMapping("/districts/{id}")
    public ResponseEntity<GenericResponse> deleteDistrict(@RequestHeader("Authorization") String token,
                                                          @PathVariable("id") Integer id) {
        log.info("AdminAddressController, deleteDistrict");
        return addressService.deleteDistrict(token, id);
    }

    @GetMapping("/schoolsByDistrict")
    public ResponseEntity<GenericResponse> schoolsByDistrict(@RequestHeader("Authorization") String token,
                                                             @RequestParam("dId") Integer districtId,
                                                             @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                             @RequestParam(value = "size", defaultValue = "5") Integer size ) {
        log.info("AdminAddressController, schoolsByDistrict");
        return addressService.getSchoolsForAdmin(token, districtId, page - 1, size);
    }

    @PostMapping("/schools")
    public ResponseEntity<GenericResponse> addSchool(@RequestHeader("Authorization") String token,
                                                     @RequestBody SchoolRequest addressRequest) {
        log.info("AdminAddressController, addSchool");
        return addressService.addSchool(token, addressRequest);
    }

    @PutMapping("/schools/{id}")
    public ResponseEntity<GenericResponse> updateSchool(@RequestHeader("Authorization") String token,
                                                        @PathVariable("id") Integer id,
                                                        @RequestBody SchoolRequest addressRequest) {
        log.info("AdminAddressController, updateSchool");
        return addressService.updateSchool(token, id, addressRequest);
    }

    @DeleteMapping("/schools/{id}")
    public ResponseEntity<GenericResponse> deleteSchool(@RequestHeader("Authorization") String token,
                                                        @PathVariable("id") Integer id) {
        log.info("AdminAddressController, deleteSchool");
        return addressService.deleteSchool(token, id);
    }
}
