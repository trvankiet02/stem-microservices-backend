package com.trvankiet.app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "events")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Event implements Serializable {

    @Id
    @Field(name = "event_id")
    private String eventId;

    @Field(name = "author_id")
    private String authorId;

    @Field(name = "event_name")
    private String eventName;

    @Field(name = "event_description")
    private String eventDescription;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "start_date")
    private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "end_date")
    private Date endDate;

    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "created_at")
    private Date createdAt;

    @LastModifiedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Field(name = "updated_at")
    private Date updatedAt;
}
