package com.trvankiet.app.service;

import com.trvankiet.app.dto.*;
import com.trvankiet.app.entity.Credential;
import com.trvankiet.app.entity.Relationship;
import com.trvankiet.app.entity.Token;
import com.trvankiet.app.entity.User;

public interface MapperService {
    UserDto mapToUserDto(User user);
    CredentialDto mapToCredentialDto(Credential credential);
    TokenDto mapToTokenDto(Token token);
    RelationshipDto mapToRelationDto(Relationship relationship);
    AnotherUserDto mapToAnotherUserDto(User user);
}
