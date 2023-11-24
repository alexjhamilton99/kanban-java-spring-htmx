package com.alexjhamilton.kanban.task.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table("tasks")
public record Task(
        @Id Long id,
        String name,
        String comment,
        LocalDate dueDate,
        Priority priority,
        Status status/*,
        @Column("owning_user_id") AggregateReference<OwningUser, Long> owningUser,
        @MappedCollection(idColumn = "task_id", keyColumn = "user_id") Set<TaggedUserRef> taggedUsers*/
) {

//    public void addTaggedUser(User user) {
//        taggedUsers.add(new TaggedUserRef(AggregateReference.to(user.id())));
//    }
//
//    public void removeTaggedUser(User user) {
//        taggedUsers.remove(new TaggedUserRef(AggregateReference.to(user.id())));
//    }

}
