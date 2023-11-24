package com.alexjhamilton.kanban.task.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public record OwningUser(
        @Id Long id,
        String email,
        String firstName,
        String lastName
) {
}
