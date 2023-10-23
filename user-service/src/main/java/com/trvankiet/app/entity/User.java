package com.trvankiet.app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.trvankiet.app.constant.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;

import lombok.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"credential"})
@Builder
public class User extends AbstractMappedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String userId;

    @Nationalized
    @Column(name = "first_name")
    private String firstName;

    @Nationalized
    @Column(name = "last_name")
    private String lastName;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Email
    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "dob")
    private Date dob;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Nationalized
    @Column(name = "about")
    private String about;

    @Nationalized
    @Column(name = "worked_at")
    private String workedAt;

    @Nationalized
    @Column(name = "address")
    private String address;

    @ToString.Exclude
    @JsonBackReference
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private Credential credential;

}
