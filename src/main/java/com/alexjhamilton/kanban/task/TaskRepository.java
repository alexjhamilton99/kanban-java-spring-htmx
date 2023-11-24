package com.alexjhamilton.kanban.task;

import com.alexjhamilton.kanban.task.domain.Priority;
import com.alexjhamilton.kanban.task.domain.Status;
import com.alexjhamilton.kanban.task.domain.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

interface TaskRepository extends PagingAndSortingRepository<Task, Long>, CrudRepository<Task, Long> {

    long countByNameContainsIgnoreCaseAndPriorityInAndStatusIn(String name, List<Priority> priorities,
                                                               List<Status> statuses);

}
