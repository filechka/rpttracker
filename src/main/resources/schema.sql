DROP TABLE IF EXISTS actions;
CREATE TABLE actions
(
    id varchar(36) not null primary key,
    name varchar(255) not null,
    description varchar(1023)
);
DROP TABLE IF EXISTS actions_history;
CREATE TABLE actions_history
(
    id varchar(36) not null primary key,
    action_id varchar(36) not null REFERENCES actions(id),
    created timestamp
);