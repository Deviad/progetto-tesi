
create schema if not exists ripeti;
set search_path to ripeti;
create table if not exists addresses(id uuid DEFAULT public.uuid_generate_v4() , first_address_line varchar(255) not null, second_address_line varchar(255), city varchar(30) not null, country varchar(30) not null, primary key(id));
create table if not exists users(id uuid DEFAULT public.uuid_generate_v4(), first_name varchar(30) not null, last_name varchar(30) not null, username varchar(30) unique not null, password varchar(255) not null, email varchar(150) unique not null, address_id uuid, primary key (id), constraint fk_users_address foreign key(address_id) references addresses(id));
create table if not exists courses(id uuid DEFAULT public.uuid_generate_v4(), course_name varchar(255), teacher_id uuid, student_id uuid, primary key(id), constraint fk_course_teacher foreign key(teacher_id) references users(id), constraint fk_course_student foreign key(student_id) references users(id));
