drop table if exists todo;

CREATE TABLE if not exists todo
(
    id          bigserial PRIMARY KEY,
    user_id     bigint      NOT NULL,
    title       VARCHAR(500)         default '' NOT NULL,
    description TEXT                 default '' NOT NULL,
    state       smallint NOT NULL,
    priority    VARCHAR(50) NOT NULL,
    assignee    bigint,
    due_date    date,
    created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at  TIMESTAMP
);

ALTER SEQUENCE todo_id_seq RESTART WITH 10000;

-- Create indexes for query performance optimization
-- Index for querying by user ID
CREATE INDEX idx_todo_user_id ON todo (user_id);

-- Create trigger for automatic updated_at column update
CREATE
    OR REPLACE FUNCTION update_updated_at_column()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at
        = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$
    LANGUAGE plpgsql;

CREATE TRIGGER update_todo_updated_at
    BEFORE UPDATE
    ON todo
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Add table comments
COMMENT
    ON TABLE todo IS 'Todo tasks table';
COMMENT
    ON COLUMN todo.id IS 'Unique identifier';
COMMENT
    ON COLUMN todo.user_id IS 'User ID - owner/tenant of this todo';
COMMENT
    ON COLUMN todo.title IS 'Todo title';
COMMENT
    ON COLUMN todo.description IS 'Todo detailed description';
COMMENT
    ON COLUMN todo.state IS 'Todo state';
COMMENT
    ON COLUMN todo.priority IS 'Priority level';
COMMENT
    ON COLUMN todo.assignee IS 'Assignee user ID';
COMMENT
    ON COLUMN todo.due_date IS 'Due date';
COMMENT
    ON COLUMN todo.created_at IS 'Creation timestamp';
COMMENT
    ON COLUMN todo.updated_at IS 'Last update timestamp';
COMMENT
    ON COLUMN todo.deleted_at IS 'Deletion timestamp for soft deletes';