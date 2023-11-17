package com.alexjhamilton.kanban.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public record User(
        @Id Long id,
        String email,
        String firstName,
        String lastName
) {
}
