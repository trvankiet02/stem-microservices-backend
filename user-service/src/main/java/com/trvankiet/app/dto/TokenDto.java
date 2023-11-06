package com.trvankiet.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.trvankiet.app.constant.TokenType;
import com.trvankiet.app.entity.Credential;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
public class TokenDto implements Serializable {

    private String token;
    private String type;
    private Boolean is_expired;
    private Boolean is_revoked;
    private Date expiredAt;
    private Date createdAt;
    private Date updatedAt;

}
