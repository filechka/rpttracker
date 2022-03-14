DROP TABLE IF EXISTS actions;
CREATE TABLE actions
(
    id varchar(36) not null primary key,
    name varchar(255) not null,
    description varchar(1023)
);