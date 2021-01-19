create schema if not exists ripeti;
set search_path to ripeti;


----- begin these are to be used only for the first time in order to create the full schema ---

-- drop type if exists user_role cascade;
-- create type user_role as enum ('STUDENT', 'TEACHER');

-- alter table if exists users
--     add role user_role;

-- drop type if exists course_status cascade;
-- create type course_status as enum ('DRAFT', 'LIVE');

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
create table if not exists teams
(
    id          uuid DEFAULT public.uuid_generate_v4(),
    team_name   varchar(255),
    course_id   uuid,
    score       int,
    student_ids uuid[],
    constraint fk_teams_course foreign key (course_id) references courses (id),
    primary key (id)
);
create table if not exists quizzes
(
    id           uuid DEFAULT public.uuid_generate_v4(),
    quiz_name    varchar(255),
    course_id    uuid,
    quiz_content text,
    primary key (id),
    constraint fk_quiz_course foreign key (course_id) references courses (id)

);
create table if not exists questions
(
    id      uuid DEFAULT public.uuid_generate_v4(),
    title   varchar(255),
    content text,
    quiz_id uuid,
    primary key (id)
);
create table if not exists quiz_run
(
    id            uuid DEFAULT public.uuid_generate_v4(),
    quiz_id       uuid,
    student_id    uuid,
    question_id   uuid,
    run_count     int,
    avg_run_score int,
    constraint fk_quiz_run_quiz foreign key (quiz_id) references quizzes (id),
    constraint fk_quiz_run_student foreign key (student_id) references users (id),
    constraint fk_quiz_run_question foreign key (question_id) references questions (id),
    primary key (id)
);


