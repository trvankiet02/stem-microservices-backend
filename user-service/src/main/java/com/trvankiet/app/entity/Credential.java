package com.trvankiet.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@EqualsAndHashCode(callSuper = true, exclude = {})
@Builder
public class Credential extends AbstractMappedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "credential_id")
    private String id;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @Builder.Default
    @Column(name = "is_account_non_expired")
    private Boolean isAccountNonExpired = true;

    @Builder.Default
    @Column(name = "is_account_non_locked")
    private Boolean isAccountNonLocked = true;

    @Builder.Default
    @Column(name = "is_credentials_non_expired")
    private Boolean isCredentialsNonExpired = true;

    @Builder.Default
    @Column(name = "locked_at")
    private Date lockedAt = null;

    @Builder.Default
    @Column(name = "locked_reason")
    private String lockedReason = "";

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id")
    @ToString.Exclude
    private Role role;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    @ToString.Exclude
    private Provider provider;

    @OneToMany(mappedBy = "credential", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Token> tokens;

}
