package com.trvankiet.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trvankiet.app.constant.TokenType;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "tokens")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {})
@Data
@Builder
public class Token extends AbstractMappedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id", unique = true, nullable = false, updatable = false)
    private Integer tokenId;

    @Column(name = "token")
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TokenType type;

    @Column(name = "expired_at")
    private Date expiredAt;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "credential_id", referencedColumnName = "credential_id")
    private Credential credential;
}
