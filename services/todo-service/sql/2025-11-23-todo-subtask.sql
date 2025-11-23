drop table if exists todo_subtask;

CREATE TABLE if not exists todo_subtask
(
    id         bigserial PRIMARY KEY,
    todo_id    BIGINT    NOT NULL,
    title      VARCHAR(500)       default '' NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE INDEX idx_todo_subtask_todo_id ON todo_subtask (todo_id);

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

CREATE TRIGGER update_todo_subtask_updated_at
    BEFORE UPDATE
    ON todo_subtask
    FOR EACH ROW
EXECUTE FUNCTION update_updated_at_column();

-- Add comment to the table
COMMENT ON TABLE todo_subtask IS 'Table to store subtasks associated with todos';
COMMENT ON COLUMN todo_subtask.id IS 'Primary key of the subtask';
COMMENT ON COLUMN todo_subtask.todo_id IS 'Foreign key referencing the associated todo item';
COMMENT ON COLUMN todo_subtask.title IS 'Title of the subtask';
COMMENT ON COLUMN todo_subtask.created_at IS 'Timestamp when the subtask was created';
COMMENT ON COLUMN todo_subtask.updated_at IS 'Timestamp when the subtask was last updated';
COMMENT ON COLUMN todo_subtask.deleted_at IS 'Timestamp when the subtask was deleted (soft delete)';
