package com.trvankiet.app.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "user")
public class User {

    @Id
    private String id;

    @Field(name = "first_name", type = FieldType.Text)
    private String firstName;

}
