package com.trvankiet.app.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;
@EqualsAndHashCode(callSuper = true, exclude = {})
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "roles")
public class Role extends AbstractMappedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "role_id")
    private String id;

    @Column(name = "role_code", unique = true)
    private String code;

    @Column(name = "role_name")
    private String name;

    @Builder.Default
    @Column(name = "role_description")
    private String description = "";

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<Credential> credentials;

}
