package com.trvankiet.app.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;

@Entity
@Table(name = "subjects")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true, exclude = {})
@Builder
public class Subject extends AbstractMappedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Integer id;

    @Column(name = "subject_code")
    private String code;

    @Nationalized
    @Column(name = "subject_name")
    private String name;

    @Nationalized
    @Column(name = "subject_description")
    private String description;

}
