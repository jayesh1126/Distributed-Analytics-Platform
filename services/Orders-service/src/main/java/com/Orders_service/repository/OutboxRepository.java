package com.Orders_service.repository;

import com.Orders_service.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, UUID>
{
    @Query(
            value = "SELECT * FROM event_outbox " +
                    "WHERE status IN ('NEW','FAILED') " +
                    "AND (retry_count IS NULL OR retry_count < 5) " +
                    "ORDER BY created_at " +
                    "FOR UPDATE SKIP LOCKED " +
                    "LIMIT :limit",
            nativeQuery = true
    )
    List<OutboxEvent> lockBatch(@Param("limit") int limit);
}
