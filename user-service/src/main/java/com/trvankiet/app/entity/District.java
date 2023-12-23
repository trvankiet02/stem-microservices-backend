package com.trvankiet.app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "districts")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true, exclude = {})
@Builder
public class District extends AbstractMappedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "district_id")
    private Integer id;

    @Column(name = "district_code")
    private String code;

    @Nationalized
    @Column(name = "district_name")
    private String name;

    @Nationalized
    @Column(name = "district_description")
    private String description;

    @Column(name = "isDeleted")
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;

    @OneToMany(mappedBy = "district", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<School> schools;
}
