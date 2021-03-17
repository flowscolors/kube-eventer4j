drop database if exists kube_event4j ;
create database kube_event4j;
use kube_event4j;

create table kube_event4j
(
    id               bigint(20)   not null auto_increment primary key comment 'event primary key',
    cluster_name     varchar(64)  not null default '' comment 'cluster name',
    event_name       varchar(64)  not null default '' comment 'event name',
    event_namespace  varchar(64)  not null default '' comment 'event namespace',
    event_id         varchar(64)  not null default '' comment 'event_id',
    type             varchar(64)  not null default '' comment 'event type Warning or Normal',
    reason           varchar(64)  not null default '' comment 'event reason',
    message          text  not null  comment 'event message' ,
    kind             varchar(64)  not null default '' comment 'event kind' ,
    first_occurrence_time   varchar(64)    not null default '' comment 'event first occurrence time',
    last_occurrence_time    varchar(64)    not null default '' comment 'event last occurrence time',
    unique index id_index (id)
) ENGINE = InnoDB default CHARSET = utf8mb4 comment ='Event4j info tables';