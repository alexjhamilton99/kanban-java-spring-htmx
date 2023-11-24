package com.alexjhamilton.kanban.task.domain;

import java.time.LocalDate;

public record TaskDto(
        Long id,
        String name,
        String priority,
        String status,
        LocalDate dueDate
) {

}