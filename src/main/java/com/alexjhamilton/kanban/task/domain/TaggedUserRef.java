package com.alexjhamilton.kanban.task.domain;

import com.alexjhamilton.kanban.user.User;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("tagged_users")
public record TaggedUserRef(
        @Column("user_id") AggregateReference<User, Long> taggedUser
) {
}
