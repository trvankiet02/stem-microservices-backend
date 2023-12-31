package com.trvankiet.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.trvankiet.app.constant.Provider;
import com.trvankiet.app.constant.RoleBasedAuthority;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "credentials")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"token"})
@Data
@Builder
public class Credential extends AbstractMappedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "credential_id", unique = true, nullable = false, updatable = false)
    private Integer credentialId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private Provider provider;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleBasedAuthority roleBasedAuthority;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @Column(name = "is_account_non_expired")
    private Boolean isAccountNonExpired;

    @Column(name = "is_account_non_locked")
    private Boolean isAccountNonLocked;

    @Column(name = "is_credentials_non_expired")
    private Boolean isCredentialsNonExpired;

    @Column(name = "locked_at")
    private Date lockedAt;

    @Column(name = "locked_reason")
    private String lockedReason;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "credential")
    private List<Token> token;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
}
