package com.trvankiet.app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trvankiet.app.constant.TokenType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tokens")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true, exclude = {})
@Builder
public class Token extends AbstractMappedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "token_id")
    private String id;

    @Column(name = "token_value", unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type")
    private TokenType type;

    @Builder.Default
    @Column(name = "is_expired")
    private Boolean isExpired = false;

    @Builder.Default
    @Column(name = "is_revoked")
    private Boolean isRevoked = false;

    @Column(name = "expired_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Date expiredAt;

    @ManyToOne
    @JoinColumn(name = "credential_id")
    @ToString.Exclude
    private Credential credential;

}
