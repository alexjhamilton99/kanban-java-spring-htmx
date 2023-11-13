--liquibase formatted sql

--changeset alex.hamilton:1-users_table
CREATE TABLE users
(
    id         BIGSERIAL PRIMARY KEY,
    email      VARCHAR(255) NOT NULL UNIQUE,
    first_name VARCHAR(50)  NOT NULL,
    last_name  VARCHAR(50)  NOT NULL,
    password   VARCHAR(255) NOT NULL
);

--changeset alex.hamilton:2-insert_initial_users
INSERT INTO users (email, first_name, last_name, password)
VALUES ('alex.hamilton@p24q.onmicrosoft.com', 'Alex', 'Hamilton',
        '$2a$10$0dmbDaE/yTYo3Wbumz0LYeUontOlBHwE3iVFvAyi5EOeFmdYV2R6e'),
       ('AdeleV@p24q.onmicrosoft.com', 'Adele', 'Vance',
        '$2a$10$YynSOPjxWBmhYQ.lIoViJ.NwXOJGa1p6S5zBUCuNn1zT6J.M.b7Xm'),
       ('DiegoS@p24q.onmicrosoft.com', 'Diego', 'Siciliani',
        '$2a$10$6N3tN9FRXYwyzSVU3Xr1AeY5uIWJBfxI/ytGk8YCx6YnikiH3WHCK'),
       ('GradyA@p24q.onmicrosoft.com', 'Grady', 'Archie',
        '$2a$10$GteI/Io5KTy8QirMaL8X...hjdBzDA5ksqjiBMI5vkS9A3SB6UNn2'),
       ('HenriettaM@p24q.onmicrosoft.com', 'Henrietta', 'Mueller',
        '$2a$10$qN.v/MukVioaROyzYxszU.28QbQm0awXwj.19RDi29L1fEtAAuRdG'),
       ('PattiF@p24q.onmicrosoft.com', 'Patti', 'Fernandez',
        '$2a$10$F1Fqvtx9mEJ6ycyK5ZIPXet.FwR1dsrYDKwLs71JPf21/PP4KF8he');

--changeset alex.hamilton:3-priority_enum_ddl
CREATE TYPE PRIORITY AS ENUM ('Low', 'Medium', 'High');

--changeset alex.hamilton:4-status_enum_ddl
CREATE TYPE STATUS AS ENUM ('New', 'Started', 'Blocked', 'Done');

--changeset alex.hamilton:5-tasks_table_ddl
CREATE TABLE tasks
(
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    comment        VARCHAR(255),
    due_date       DATE         NOT NULL,
    priority       PRIORITY     NOT NULL,
    status         STATUS       NOT NULL,
    owning_user_id BIGINT REFERENCES users (id)
);

--changeset alex.hamilton:6-insert_initial_tasks
INSERT INTO tasks (name, comment, due_date, priority, status, owning_user_id)
VALUES ('htmx frontend', 'No more React!', '2023-11-10', 'High', 'New', 1),
       ('Design database schema', 'We''re going with Postgres', '2023-11-11', 'High', 'Blocked', 3),
       ('Onboard new developers', NULL, '2023-11-12', 'Low', 'Done', 1),
       ('Authentication', 'Store passwords and roles in the database', '2023-11-12', 'Medium', 'Started', 2),
       ('Email integration', NULL, '2023-11-14', 'Low', 'New', 2),
       ('Implement backend logic', 'Use Spring Boot', '2023-11-15', 'Medium', 'Started', 4),
       ('Test and QA', NULL, '2023-11-16', 'High', 'Started', 5),
       ('Bug fixing and optimization', NULL, '2023-11-17', 'Low', 'Blocked', 6),
       ('Documentation', 'Create project documentation and user guides', '2023-11-18', 'Medium', 'Done', 3),
       ('Project deployment', 'We haven''t chosen a cloud provider', '2023-11-19', 'High', 'Blocked', 4);

--changeset alex.hamilton:7-tagged_users_table_ddl
CREATE TABLE tagged_users
(
    user_id BIGINT REFERENCES users (id),
    task_id BIGINT REFERENCES tasks (id),
    PRIMARY KEY (user_id, task_id)
);

--changeset alex.hamilton:8-prevent_tagging_owner_trigger_ddl splitStatements:false
CREATE FUNCTION fn_prevent_tagging_owning_user()
    RETURNS TRIGGER AS
$body$
BEGIN
    IF EXISTS (SELECT 1
               FROM tasks
               WHERE id = NEW.task_id
                 AND owning_user_id = NEW.user_id)
    THEN
        RAISE EXCEPTION 'Cannot tag owning user id=% to task id=%', NEW.user_id, NEW.task_id;
    END IF;
    RETURN NEW;
END;
$body$
    LANGUAGE plpgsql;

CREATE TRIGGER trg_prevent_tagging_owning_user
    BEFORE INSERT OR
        UPDATE
    ON tagged_users
    FOR EACH ROW
EXECUTE FUNCTION
    fn_prevent_tagging_owning_user();

--changeset alex.hamilton:9-insert_initial_tagged_users
INSERT INTO tagged_users (user_id, task_id)
VALUES (1, 2);
INSERT INTO tagged_users (user_id, task_id)
VALUES (1, 4);
INSERT INTO tagged_users (user_id, task_id)
VALUES (1, 6);
INSERT INTO tagged_users (user_id, task_id)
VALUES (2, 1);
INSERT INTO tagged_users (user_id, task_id)
VALUES (2, 3);
INSERT INTO tagged_users (user_id, task_id)
VALUES (2, 6);
INSERT INTO tagged_users (user_id, task_id)
VALUES (2, 7);
INSERT INTO tagged_users (user_id, task_id)
VALUES (3, 5);
INSERT INTO tagged_users (user_id, task_id)
VALUES (3, 6);
INSERT INTO tagged_users (user_id, task_id)
VALUES (3, 7);
INSERT INTO tagged_users (user_id, task_id)
VALUES (3, 8);
INSERT INTO tagged_users (user_id, task_id)
VALUES (4, 7);
INSERT INTO tagged_users (user_id, task_id)
VALUES (4, 8);
INSERT INTO tagged_users (user_id, task_id)
VALUES (4, 9);
INSERT INTO tagged_users (user_id, task_id)
VALUES (5, 1);
INSERT INTO tagged_users (user_id, task_id)
VALUES (6, 2);
