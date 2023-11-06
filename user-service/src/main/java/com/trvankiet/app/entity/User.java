package com.trvankiet.app.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.trvankiet.app.constant.Gender;
import com.trvankiet.app.constant.RoleBasedAuthority;
import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.Nationalized;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true, exclude = {})
@Builder
public class User extends AbstractMappedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String id;

    @Nationalized
    @Column(name = "first_name")
    private String firstName;

    @Nationalized
    @Column(name = "last_name")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleBasedAuthority role;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "email")
    private String email;

    @Builder.Default
    @Column(name = "phone")
    private String phone = "";

    @Column(name = "dob")
    private Date dob;

    @Builder.Default
    @Column(name = "avatar_url")
    private String avatarUrl = "";

    @Builder.Default
    @Column(name = "cover_url")
    private String coverUrl = "";

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    private Credential credential;

    /**
     * For Student and Teacher
     */
    @Column(name = "district")
    private String district;

    @Column(name = "province")
    private String province;

    @Column(name = "school")
    private String school;

    /**
     * For Student
     */
    @Column(name = "grade")
    private Integer grade;

    @ManyToMany(mappedBy = "students")
    @JsonBackReference
    @ToString.Exclude
    private List<User> parents;

    /**
     * For Teacher
     */
    @ElementCollection
    @CollectionTable(name = "teacher_subject", joinColumns = @JoinColumn(name = "user_id"))
    private List<String> subjects;

    /**
     * For Parent
     */

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "parent_student",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @JsonBackReference
    @ToString.Exclude
    private List<User> students;

}
