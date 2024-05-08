package com.trvankiet.app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "provinces")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true, exclude = {})
@Builder
public class Province extends AbstractMappedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "province_id")
    private Integer id;

    @Column(name = "province_code")
    private String code;

    @Nationalized
    @Column(name = "province_name")
    private String name;

    @Nationalized
    @Column(name = "province_description")
    private String description;

    @Column(name = "isDeleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "province", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<District> districts;

}
