package com.trvankiet.app.repository;

import com.trvankiet.app.entity.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {
    List<Report> findAllByGroupIdAndIsReportToGroupManager(String groupId, Boolean isReportToGroupManager);
    List<Report> findAllByGroupIdAndIsReportToAdmin(String groupId, Boolean isReportToAdmin);
}
