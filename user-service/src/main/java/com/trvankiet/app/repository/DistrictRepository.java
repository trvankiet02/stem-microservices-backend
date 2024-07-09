package com.trvankiet.app.repository;

import com.trvankiet.app.entity.District;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, Integer> {

    List<District> findAllByProvinceId(Integer provinceId);
    Page<District> findAllByProvinceId(Integer provinceId, Pageable pageable);
    Optional<District> findByCode(String code);
}
