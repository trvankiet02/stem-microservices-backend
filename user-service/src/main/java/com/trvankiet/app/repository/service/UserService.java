package com.trvankiet.app.repository.service;

import com.trvankiet.app.dto.request.UserInfoRequest;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.User;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {

    <S extends User> S save(S entity);

    <S extends User> Optional<S> findOne(Example<S> example);

    List<User> findAll(Sort sort);

    List<User> findAll();

    Optional<User> findById(String id);

    boolean existsById(String id);

    <S extends User> long count(Example<S> example);

    <S extends User> boolean exists(Example<S> example);

    long count();

    void deleteById(String id);

    void delete(User entity);

    ResponseEntity<GenericResponse> initCredentialInfo(String token, UserInfoRequest userInfoRequest);
}
