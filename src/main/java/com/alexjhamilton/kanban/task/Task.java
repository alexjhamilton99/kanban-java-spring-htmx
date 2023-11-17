package com.alexjhamilton.kanban.task;

import com.alexjhamilton.kanban.user.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.Set;

@Table("tasks")
public record Task(
        @Id Long id,
        String name,
        String comment,
        LocalDate dueDate,
        Priority priority,
        Status status,
        AggregateReference<OwningUser, Long> owningUser,
        @MappedCollection(idColumn = "task_id", keyColumn = "user_id") Set<TaggedUserRef> taggedUsers
) {

    public void addTaggedUser(User user) {
        taggedUsers.add(new TaggedUserRef(AggregateReference.to(user.id())));
    }

    public void removeTaggedUser(User user) {
        taggedUsers.remove(new TaggedUserRef(AggregateReference.to(user.id())));
    }

}
