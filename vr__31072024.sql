CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TYPE task_status AS ENUM ('NEW', 'IN_PROGRESS', 'DONE');
CREATE TYPE task_priority AS ENUM ('low', 'medium', 'high');

CREATE TABLE users (
   id       UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
   email    VARCHAR(255) NOT NULL,
   password VARCHAR(2048) NOT NULL
);

CREATE TABLE tasks (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title         VARCHAR(255) NOT NULL,
    description   VARCHAR NOT NULL,
    status        task_status NOT NULL,
    priority      task_priority NOT NULL
);

CREATE TABLE comments (
    id            UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    text          VARCHAR NOT NULL,
    author_id     UUID NOT NULL REFERENCES users(id)
);

CREATE TABLE task_comments (
    comment_id    UUID NOT NULL REFERENCES comments(id),
    task_id       UUID NOT NULL REFERENCES tasks(id),
    PRIMARY KEY (comment_id, task_id)
);

CREATE TABLE user_author_tasks (
    task_id       UUID NOT NULL REFERENCES tasks(id),
    user_id       UUID NOT NULL REFERENCES users(id),
    PRIMARY KEY (task_id, user_id)
);

CREATE TABLE user_executor_tasks (
    task_id       UUID NOT NULL REFERENCES tasks(id),
    user_id       UUID NOT NULL REFERENCES users(id),
    PRIMARY KEY (task_id, user_id)
);
