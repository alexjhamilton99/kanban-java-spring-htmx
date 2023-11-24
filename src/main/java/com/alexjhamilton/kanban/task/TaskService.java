package com.alexjhamilton.kanban.task;

import com.alexjhamilton.kanban.task.domain.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TaskService {

    private static final RowMapper<TaskDto> TASK_DTO_ROW_MAPPER = (rs, rowNum) -> new TaskDto(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getString("priority"),
            rs.getString("status"),
            rs.getDate("due_date").toLocalDate()
    );
    private final TaskRepository taskRepository;

    // TODO: switch to JdbcClient when upgrading to Spring Boot 3.2
    private final JdbcTemplate jdbcTemplate;

    private final static String FIND_FIRST_25_DTOS_SQL = """
            SELECT
                id, name, priority, status, due_date
            FROM tasks
            WHERE name ILIKE '%%' || ? || '%%'
            %s
            %s
            ORDER BY %s, id %s LIMIT 25
            """;

    private static final String FIND_NEXT_25_DTOS_SQL = """
            SELECT
                id, name, priority, status, due_date
            FROM tasks
            WHERE (%s, id) %c (?, ?)
            AND name ILIKE '%%' || ? || '%%'
            %s
            %s
            ORDER BY %s, id %s LIMIT 25
            """;

    public TaskService(TaskRepository taskRepository, JdbcTemplate jdbcTemplate) {
        this.taskRepository = taskRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TaskDto> fetchFirst25Dtos(String nameContains, Priority priority, Status status,
                                          TaskSortCol taskSortCol, SortOrder sortOrder) {
        var priorityClause = priority != null ? "AND priority = '%s'".formatted(priority) : "";
        var statusClause = status != null ? "AND status = '%s'".formatted(status) : "";
        var sortColOrder = taskSortCol.name() + ' ' + sortOrder.name().toUpperCase();

        var sql = FIND_FIRST_25_DTOS_SQL.formatted(priorityClause, statusClause, sortColOrder,
                sortOrder.name().toUpperCase());

        return jdbcTemplate.query(sql, TASK_DTO_ROW_MAPPER, nameContains);
    }

    public long countTasksByNamePriorityStatus(String nameContains, Priority priority, Status status) {
        var priorityList = (priority != null) ? List.of(priority) : List.of(Priority.values());
        var statusList = (status != null) ? List.of(status) : List.of(Status.values());

        return taskRepository.countByNameContainsIgnoreCaseAndPriorityInAndStatusIn(nameContains, priorityList,
                statusList);
    }

    public List<TaskDto> fetchNext25Dtos(Long idCursor, String nameCursor, Priority priorityCursor,
                                         Status statusCursor, LocalDate dueDateCursor, String nameContains,
                                         Priority priority, Status status, TaskSortCol taskSortCol, SortOrder sortOrder) {
        var priorityClause = (priority != null) ? "AND priority = '%s'".formatted(priority) : "";
        var statusClause = (status != null) ? "AND status = '%s'".formatted(status) : "";
        var sortColOrder = taskSortCol.name() + ' ' + sortOrder.name().toUpperCase();

        String sql = switch (sortOrder) {
            case SortOrder.asc -> FIND_NEXT_25_DTOS_SQL.formatted(taskSortCol.name(), '>', priorityClause,
                    statusClause, sortColOrder, SortOrder.asc.name().toUpperCase());
            case SortOrder.desc -> FIND_NEXT_25_DTOS_SQL.formatted(taskSortCol.name(), '<', priorityClause,
                    statusClause, sortColOrder, SortOrder.desc.name().toUpperCase());
        };

        return switch (taskSortCol) {
            case TaskSortCol.name -> jdbcTemplate.query(sql, TASK_DTO_ROW_MAPPER, nameCursor, idCursor, nameContains);
            case TaskSortCol.priority ->
                    jdbcTemplate.query(sql, TASK_DTO_ROW_MAPPER, priorityCursor, idCursor, nameContains);
            case TaskSortCol.status ->
                    jdbcTemplate.query(sql, TASK_DTO_ROW_MAPPER, statusCursor, idCursor, nameContains);
            case TaskSortCol.due_date ->
                    jdbcTemplate.query(sql, TASK_DTO_ROW_MAPPER, dueDateCursor, idCursor, nameContains);
        };
    }

}
