package com.alexjhamilton.kanban.task;

import com.alexjhamilton.kanban.task.domain.Priority;
import com.alexjhamilton.kanban.task.domain.SortOrder;
import com.alexjhamilton.kanban.task.domain.Status;
import com.alexjhamilton.kanban.task.domain.TaskSortCol;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/tasks")
class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/sort")
    @ResponseBody
    String get(@RequestParam SortOrder sortOrder) {
        System.out.println(sortOrder + " :: " + sortOrder.name());
        return sortOrder + " :: " + sortOrder.name();
    }

    @GetMapping
    String getTasks(@RequestHeader(name = "Hx-Request", required = false) boolean htmxRequest,
                    @RequestParam(required = false, defaultValue = "%") String nameContains,
                    @RequestParam(required = false) Priority priority,
                    @RequestParam(required = false) Status status,
                    @RequestParam(required = false, defaultValue = "name") TaskSortCol taskSortCol,
                    @RequestParam(required = false, defaultValue = "asc") SortOrder sortOrder,
                    @RequestParam(required = false) Long idCursor,
                    @RequestParam(required = false) String nameCursor,
                    @RequestParam(required = false) Priority priorityCursor,
                    @RequestParam(required = false) Status statusCursor,
                    @RequestParam(required = false) LocalDate dueDateCursor,
                    Model model) {

        System.out.println('\n' + "htmxRequest = " + htmxRequest + ", nameContains = " + nameContains + ", priority ="
                + " " + priority + ", status = " + status + ", taskSortCol = " + taskSortCol + ", sortOrder = "
                + sortOrder + ", idCursor = " + idCursor + ", nameCursor = " + nameCursor + ", priorityCursor = "
                + priorityCursor + ", statusCursor = " + statusCursor + ", dueDateCursor = " + dueDateCursor + ","
                + "model = " + model + '\n');

        var tasks = (idCursor == null)
                ? taskService.fetchFirst25Dtos(nameContains, priority, status, taskSortCol, sortOrder)
                : taskService.fetchNext25Dtos(idCursor, nameCursor, priorityCursor, statusCursor, dueDateCursor,
                nameContains, priority, status, taskSortCol, sortOrder);

        model.addAttribute("tasks", tasks);

        System.out.println();
//        taskService.fetchSlice();
        System.out.println();

        // TODO: create search fragment
        // TODO: possibly create <tbody> fragment but may not need to because this is active search with infinite scroll
        return htmxRequest ? "fragments/general :: header" : "tasks/list";
    }

    // TODO: lazily load this and/or fire an event
    @GetMapping(value = "/count")
    @ResponseBody
    String getTaskCount(@RequestParam(required = false, defaultValue = "%") String name,
                        @RequestParam(required = false) Priority priority,
                        @RequestParam(required = false) Status status) {
        var count = taskService.countTasksByNamePriorityStatus(name, priority, status);

        return (count == 1) ? "1 result" : count + " results";
    }

//    @GetMapping("/next")


}
