package org.best.backspringboot.log.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "로그", description = "MongoDB 로그 조회 API")
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final MongoTemplate mongoTemplate;

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    @Operation(summary = "로그 목록 조회")
    @GetMapping
    public ResponseEntity<List<Map>> getLogs(
            @RequestParam(defaultValue = "")   String source,  // api, batch, socket
            @RequestParam(defaultValue = "")   String level,   // INFO, WARNING, ERROR
            @RequestParam(defaultValue = "50") int    size
    ) {
        Query query = new Query();

        if (!source.isEmpty()) query.addCriteria(Criteria.where("source").is(source));
        if (!level.isEmpty())  query.addCriteria(Criteria.where("level").is(level));

        query.with(Sort.by(Sort.Direction.DESC, "timestamp"));
        query.limit(size);

        List<Map> logs = mongoTemplate.find(query, Map.class, "socket_logs");
        return ResponseEntity.ok(logs);
    }
}
