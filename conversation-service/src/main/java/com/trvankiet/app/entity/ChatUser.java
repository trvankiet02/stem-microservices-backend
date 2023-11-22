package com.trvankiet.app.entity;

import com.trvankiet.app.constant.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

@Document(collection = "chat_users")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ChatUser implements Serializable {

    @Id
    @Field(name = "user_id")
    private String id;

    @Field(name = "first_name")
    private String firstName;

    @Field(name = "last_name")
    private String lastName;

    @Field(name = "avatar_url")
    private String avatarUrl;

    @Field(name = "status")
    private StatusEnum status;

    @Field(name = "last_online")
    private Data lastOnline;

}
