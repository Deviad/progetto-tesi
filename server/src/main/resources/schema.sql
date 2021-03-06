create schema if not exists ripeti;
set search_path to ripeti;


----- begin these are to be used only for the first time in order to create the full schema ---

-- drop type if exists user_role cascade;
-- create type user_role as enum ('STUDENT', 'PROFESSOR');
--
-- alter table if exists users
--     add role user_role;
--
-- drop type if exists course_status cascade;
-- create type course_status as enum ('DRAFT', 'LIVE');
--
-- alter table if exists courses
--     add status course_status;

----- end ----

-- create table if not exists addresses
-- (
--     id                  uuid DEFAULT public.uuid_generate_v4(),
--     first_address_line  varchar(255) not null,
--     second_address_line varchar(255),
--     city                varchar(30)  not null,
--     country             varchar(30)  not null,
--     primary key (id)
-- );


create table if not exists users
(
    id         uuid DEFAULT public.uuid_generate_v4(),
    first_name varchar(30)         not null,
    last_name  varchar(30)         not null,
    username   varchar(30) unique  not null,
    password   varchar(255)        not null,
    email      varchar(150) unique not null,
    address    varchar             not null,
    role       user_role,
    primary key (id)
--     constraint fk_users_address foreign key (address_id) references addresses (id)
);
create index concurrently if not exists idx_users_username ON users (username);
create index concurrently if not exists idx_users_email ON users (email);
create table if not exists courses
(
    id          uuid DEFAULT public.uuid_generate_v4(),
    course_name varchar(255),
    description text,
    status      course_status,
    teacher_id  uuid,
    student_ids uuid[],
    lesson_ids  uuid[],
    quiz_ids uuid[],
    primary key (id),
    constraint fk_course_teacher foreign key (teacher_id) references users (id)
);
create table if not exists lessons
(
    id             uuid DEFAULT public.uuid_generate_v4(),
    lesson_name    varchar(255),
    lesson_content text,
    primary key (id)
);
create table if not exists quizzes
(
    id           uuid DEFAULT public.uuid_generate_v4(),
    quiz_name    varchar(255),
    quiz_content text,
    question_ids uuid[],
    primary key (id)

);
create table if not exists questions
(
    id      uuid DEFAULT public.uuid_generate_v4(),
    title   varchar(255),
    answer_ids uuid[],
    primary key (id)
);

create table if not exists answers
(
    id      uuid DEFAULT public.uuid_generate_v4(),
    title   varchar(255),
    value   bool,
    primary key (id)
);


