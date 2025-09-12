CREATE TABLE todo
(
    id          UUID         NOT NULL,
    label       VARCHAR(50)  NOT NULL,
    state       VARCHAR(255) NOT NULL,
    color       VARCHAR(7)   NOT NULL,
    created     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    modified    TIMESTAMP WITHOUT TIME ZONE,
    due_date    TIMESTAMP WITHOUT TIME ZONE,
    description TEXT,
    parent_id   UUID,
    CONSTRAINT pk_todo PRIMARY KEY (id)
);

ALTER TABLE todo
    ADD CONSTRAINT FK_TODO_ON_PARENT FOREIGN KEY (parent_id) REFERENCES todo (id);