package com.alexjhamilton.kanban.task.domain;

import java.time.LocalDate;

public interface TaskPrj {
    Long getId();

    String getName();

    Priority getPriority();

    Status getStatus();

    LocalDate getDueDate();
}
