package com.trvankiet.app.entity;

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
@Table(name = "providers")
public class Provider extends AbstractMappedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "provider_id")
    private String id;

    @Column(name = "provider_code", unique = true)
    private String code;

    @Column(name = "provider_name")
    private String name;

    @Builder.Default
    @Column(name = "provider_description")
    private String description = "";

    @OneToMany(mappedBy = "provider", fetch = FetchType.LAZY)
    private List<Credential> credentials;

}
