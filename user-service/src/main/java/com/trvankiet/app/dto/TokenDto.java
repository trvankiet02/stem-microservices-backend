package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trvankiet.app.constant.TokenType;
import com.trvankiet.app.entity.Credential;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class TokenDto implements Serializable {

    private String tokenId;
    private String token;
    private TokenType type;
    private Boolean expired;
    private Boolean revoked;

}
