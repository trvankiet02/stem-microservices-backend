package com.trvankiet.app.service;

import com.trvankiet.app.dto.CredentialDto;
import com.trvankiet.app.dto.request.*;
import com.trvankiet.app.dto.response.GenericResponse;
import com.trvankiet.app.entity.Credential;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

public interface CredentialService {
    <S extends Credential> S save(S entity);

    <S extends Credential> Optional<S> findOne(Example<S> example);

    List<Credential> findAll(Sort sort);

    List<Credential> findAll();

    Optional<Credential> findById(String id);

    CredentialDto findByIdDto(String id);

    Optional<Credential> findByUsername(String username);

    boolean existsById(String id);

    <S extends Credential> long count(Example<S> example);

    <S extends Credential> boolean exists(Example<S> example);

    long count();

    void deleteById(String id);

    void delete(Credential entity);

    ResponseEntity<GenericResponse> registerForTeacher(TeacherRegisterRequest teacherRegisterRequest);
//
//    ResponseEntity<GenericResponse> login(LoginRequest loginRequest);

    CredentialDto findByUsernameDto(String username);

    ResponseEntity<GenericResponse> login(LoginRequest loginRequest);

    ResponseEntity<GenericResponse> verify(String token);

    ResponseEntity<GenericResponse> verifyResetPassword(String token);

    ResponseEntity<GenericResponse> resetPassword(String token, ResetPasswordRequest resetPasswordRequest);

    ResponseEntity<GenericResponse> logout(String authorizationHeader);

    ResponseEntity<GenericResponse> registerForParent(ParentRegisterRequest parentRegisterRequest);

    ResponseEntity<GenericResponse> registerForStudent(StudentAndParentRequest studentAndParentRequest);
}
