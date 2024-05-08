package com.trvankiet.app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;

@Entity
@Table(name = "schools")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true, exclude = {})
@Builder
public class School extends AbstractMappedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "school_id")
    private Integer id;

    @Column(name = "school_code")
    private String code;

    @Nationalized
    @Column(name = "school_name")
    private String name;

    @Nationalized
    @Column(name = "school_description")
    private String description;

    @Column(name = "isDeleted")
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

}
