package com.trvankiet.app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"token", "user", "role"})
@Builder
public class Credential extends AbstractMappedEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "credential_id")
    private String credentialId;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private Provider provider;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    private Role role;

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

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "credential")
    private List<Token> token;

    @ToString.Exclude
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

}
