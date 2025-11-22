package monorepo.services.todo.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Todo entity representing a task in the database
 *
 * @author Claude
 * @since 2025/11/22
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoEntity {
    private String id;
    private String userId;
    private String title;
    private String description;
    private String status;
    private Integer priority;
    private String assignee;
    private Instant dueDate;
    private Instant createdAt;
    private Instant updatedAt;
}
