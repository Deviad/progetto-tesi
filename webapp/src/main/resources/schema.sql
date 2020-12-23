
create schema if not exists ripeti;
set search_path to ripeti;
create table if not exists addresses(id serial primary key, first_address_line varchar(255) not null, second_address_line varchar(255), city varchar(30) not null, country varchar(30) not null);
create table if not exists users(id serial primary key, first_name varchar(20) not null, last_name varchar(30) not null, username varchar(30) unique not null, password varchar(255) not null, address_id bigint, constraint fk_address foreign key(address_id) references addresses(id));
