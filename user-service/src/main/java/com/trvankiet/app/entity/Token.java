package com.trvankiet.app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trvankiet.app.constant.TokenType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "tokens")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
public class Token extends AbstractMappedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "token_id")
    private String tokenId;

    @Column(name = "token", unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private TokenType type;

    private Boolean expired;

    private Boolean revoked;

    @Column(name = "expired_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime expiredAt;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "credential_id", referencedColumnName = "credential_id")
    private Credential credential;

}
