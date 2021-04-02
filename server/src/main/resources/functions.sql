set search_path to ripeti;

create or replace function f_get_complete_course_info(in courseId text) returns setof record language plpgsql as
'
begin
    drop table if exists t_qi_left;
    drop table if exists t_qi_right;
    drop table if exists courses_with_q_id;
    drop table if exists courses_with_quiz_info;
    drop table if exists courses_with_quiz_info_teacher_info;
    drop table if exists courses_with_quiz_info_teacher_info_student_info;
    drop table if exists courses_with_quiz_info_teacher_info_student_info_lesson_info;
    drop table if exists cqilqst;
    drop table if exists cqilqsta;


    create temp table t_qi_left as

    select quiz_id, cs.id as course_id,
           cs.course_name as course_name,
           cs.description as course_description,
           cs.status as course_status,
           cs.teacher_id as course_teacher_id,
           cs.student_ids as course_student_ids,
           cs.lesson_ids as course_lesson_ids,
           cs.quiz_ids as course_quiz_ids
    from unnest(array(select c.quiz_ids from courses c where c.id::text = courseId)) quiz_id
        left join courses cs on quiz_id  = any(cs.quiz_ids);

    create temp table t_qi_right as
    select quiz_id, cs.id as course_id,
           cs.course_name as course_name,
           cs.description as course_description,
           cs.status as course_status,
           cs.teacher_id as course_teacher_id,
           cs.student_ids as course_student_ids,
           cs.lesson_ids as course_lesson_ids,
           cs.quiz_ids as course_quiz_ids
    from unnest(array(select c.quiz_ids from courses c where c.id::text = courseId)) quiz_id
        right join courses cs on quiz_id  = any(cs.quiz_ids);

    create temp table courses_with_q_id as
    select *  from ( select * from t_qi_left WHERE t_qi_left.quiz_id IS NULL
        union all
        select *  from t_qi_right) alias1
    where true;


    create temp table courses_with_quiz_info as
    select *
    from (   select
        courses_with_q_id.course_id,
        courses_with_q_id.course_name,
        courses_with_q_id.course_description,
        courses_with_q_id.course_status,
        courses_with_q_id.course_teacher_id,
        courses_with_q_id.course_student_ids,
        courses_with_q_id.course_lesson_ids,
        q.quiz_name as quiz_name,
        q.quiz_content as quiz_content,
        q.question_ids as quiz_question_ids,
        quiz_id

        from courses_with_q_id
        left join quizzes q on courses_with_q_id.quiz_id = q.id
        union all
        select
        courses_with_q_id.course_id,
        courses_with_q_id.course_name,
        courses_with_q_id.course_description,
        courses_with_q_id.course_status,
        courses_with_q_id.course_teacher_id,
        courses_with_q_id.course_student_ids,
        courses_with_q_id.course_lesson_ids,
        q.quiz_name as quiz_name,
        q.quiz_content as quiz_content,
        q.question_ids as quiz_question_ids,
        quiz_id
        from courses_with_q_id
        right join quizzes q on courses_with_q_id.quiz_id = q.id
        where courses_with_q_id.quiz_id is null) alias2
    where alias2.course_id::text = courseId;

    create temp table courses_with_quiz_info_teacher_info as
    select *
    from (select
        courses_with_quiz_info.course_id,
        courses_with_quiz_info.course_name,
        courses_with_quiz_info.course_description,
        courses_with_quiz_info.course_status,
        courses_with_quiz_info.course_teacher_id,
        courses_with_quiz_info.course_student_ids,
        courses_with_quiz_info.course_lesson_ids,
        courses_with_quiz_info.quiz_name as quiz_name,
        courses_with_quiz_info.quiz_content as quiz_content,
        courses_with_quiz_info.quiz_id,
        courses_with_quiz_info.quiz_question_ids,
        concat(u.last_name, '', '', u.first_name) as teacher_full_name,
        u.email as teacher_email
        from courses_with_quiz_info join users u on u.id = courses_with_quiz_info.course_teacher_id) alias3;


    create temp table courses_with_quiz_info_teacher_info_student_info as
    select *
    from (select
        courses_with_quiz_info_teacher_info.course_id,
        courses_with_quiz_info_teacher_info.course_name,
        courses_with_quiz_info_teacher_info.course_description,
        courses_with_quiz_info_teacher_info.course_status,
        courses_with_quiz_info_teacher_info.course_teacher_id,
        courses_with_quiz_info_teacher_info.course_student_ids,
        courses_with_quiz_info_teacher_info.course_lesson_ids,
        courses_with_quiz_info_teacher_info.quiz_name as quiz_name,
        courses_with_quiz_info_teacher_info.quiz_content as quiz_content,
        courses_with_quiz_info_teacher_info.quiz_id,
        courses_with_quiz_info_teacher_info.quiz_question_ids,
        courses_with_quiz_info_teacher_info.teacher_full_name,
        courses_with_quiz_info_teacher_info.teacher_email,
        students.email as student_email,

        case
        when students.last_name is not null
            then concat(students.last_name, '', '', students.first_name)
        end as student_full_name,

        students.username as student_username
        from courses_with_quiz_info_teacher_info
        left join users students on students.id = any(courses_with_quiz_info_teacher_info.course_student_ids)
        union all
        select
        courses_with_quiz_info_teacher_info.course_id,
        courses_with_quiz_info_teacher_info.course_name,
        courses_with_quiz_info_teacher_info.course_description,
        courses_with_quiz_info_teacher_info.course_status,
        courses_with_quiz_info_teacher_info.course_teacher_id,
        courses_with_quiz_info_teacher_info.course_student_ids,
        courses_with_quiz_info_teacher_info.course_lesson_ids,
        courses_with_quiz_info_teacher_info.quiz_name as quiz_name,
        courses_with_quiz_info_teacher_info.quiz_content as quiz_content,
        courses_with_quiz_info_teacher_info.quiz_id,
        courses_with_quiz_info_teacher_info.quiz_question_ids,
        courses_with_quiz_info_teacher_info.teacher_full_name,
        courses_with_quiz_info_teacher_info.teacher_email,
        students.email as student_email,
        concat(students.last_name, '', '', students.first_name) as student_full_name,
        students.username as student_username
        from courses_with_quiz_info_teacher_info
        right join users students on students.id = any(courses_with_quiz_info_teacher_info.course_student_ids)
        where course_student_ids = ''{}'') alias4;


    create temp table courses_with_quiz_info_teacher_info_student_info_lesson_info as
    select *
    from (select
        courses_with_quiz_info_teacher_info_student_info.course_id,
        courses_with_quiz_info_teacher_info_student_info.course_name,
        courses_with_quiz_info_teacher_info_student_info.course_description,
        courses_with_quiz_info_teacher_info_student_info.course_status,
        courses_with_quiz_info_teacher_info_student_info.course_teacher_id,
        courses_with_quiz_info_teacher_info_student_info.course_student_ids,
        courses_with_quiz_info_teacher_info_student_info.course_lesson_ids,
        courses_with_quiz_info_teacher_info_student_info.quiz_name as quiz_name,
        courses_with_quiz_info_teacher_info_student_info.quiz_content as quiz_content,
        courses_with_quiz_info_teacher_info_student_info.quiz_id,
        courses_with_quiz_info_teacher_info_student_info.quiz_question_ids,
        courses_with_quiz_info_teacher_info_student_info.teacher_full_name,
        courses_with_quiz_info_teacher_info_student_info.teacher_email,
        courses_with_quiz_info_teacher_info_student_info.student_email,
        courses_with_quiz_info_teacher_info_student_info.student_full_name,
        courses_with_quiz_info_teacher_info_student_info.student_username,
        l.lesson_name as lesson_name,
        l.lesson_content as lesson_content,
        l.id as lesson_id
        from courses_with_quiz_info_teacher_info_student_info
        left join lessons l on l.id = any(courses_with_quiz_info_teacher_info_student_info.course_lesson_ids)
        union all
        select
        courses_with_quiz_info_teacher_info_student_info.course_id,
        courses_with_quiz_info_teacher_info_student_info.course_name,
        courses_with_quiz_info_teacher_info_student_info.course_description,
        courses_with_quiz_info_teacher_info_student_info.course_status,
        courses_with_quiz_info_teacher_info_student_info.course_teacher_id,
        courses_with_quiz_info_teacher_info_student_info.course_student_ids,
        courses_with_quiz_info_teacher_info_student_info.course_lesson_ids,
        courses_with_quiz_info_teacher_info_student_info.quiz_name as quiz_name,
        courses_with_quiz_info_teacher_info_student_info.quiz_content as quiz_content,
        courses_with_quiz_info_teacher_info_student_info.quiz_id,
        courses_with_quiz_info_teacher_info_student_info.quiz_question_ids,
        courses_with_quiz_info_teacher_info_student_info.teacher_full_name,
        courses_with_quiz_info_teacher_info_student_info.teacher_email,
        courses_with_quiz_info_teacher_info_student_info.student_email,
        courses_with_quiz_info_teacher_info_student_info.student_full_name,
        courses_with_quiz_info_teacher_info_student_info.student_username,
        l.lesson_name as lesson_name,
        l.lesson_content as lesson_content,
        l.id as lesson_id
        from courses_with_quiz_info_teacher_info_student_info
        right join lessons l on l.id = any(courses_with_quiz_info_teacher_info_student_info.course_lesson_ids)
        where course_lesson_ids = ''{}'') alias5;


    create table cqilqst as
    select *  from (
        select
        t.course_id,
        t.course_name,
        t.course_description,
        t.course_status,
        t.course_teacher_id,
        t.quiz_name,
        t.quiz_content,
        t.quiz_id,
        t.teacher_full_name,
        t.teacher_email,
        t.student_email,
        t.student_full_name,
        t.student_username,
        t.lesson_name,
        t.lesson_content,
        t.lesson_id,
        qs.title as question_title,
        qs.id as question_id,
        qs.answer_ids as answer_ids
        from courses_with_quiz_info_teacher_info_student_info_lesson_info t
        left join questions qs on  qs.id = any(t.quiz_question_ids)
        union all
        select
        t.course_id,
        t.course_name,
        t.course_description,
        t.course_status,
        t.course_teacher_id,
        t.quiz_name,
        t.quiz_content,
        t.quiz_id,
        t.teacher_full_name,
        t.teacher_email,
        t.student_email,
        t.student_full_name,
        t.student_username,
        t.lesson_name,
        t.lesson_content,
        t.lesson_id,
        qs.title as question_title,
        qs.id as question_id,
        qs.answer_ids as answer_ids
        from courses_with_quiz_info_teacher_info_student_info_lesson_info t
        right join questions qs on  qs.id = any(t.quiz_question_ids)
        where quiz_question_ids = ''{}'') alias6;


   return query
    select
        cqilqst.course_id,
        cqilqst.course_name,
        cqilqst.course_description,
        cqilqst.course_status,
        cqilqst.course_teacher_id,
        cqilqst.quiz_name,
        cqilqst.quiz_content,
        cqilqst.quiz_id,
        cqilqst.teacher_full_name,
        cqilqst.teacher_email,
        cqilqst.student_email,
        cqilqst.student_full_name,
        cqilqst.student_username,
        cqilqst.lesson_name,
        cqilqst.lesson_content,
        cqilqst.lesson_id,
        cqilqst.question_title,
        cqilqst.question_id,
        answers.id as answer_id,
        answers.title as answer_title,
        answers.value as answer_value
    from cqilqst
             left join answers on answers.id = any (cqilqst.answer_ids)
    union all
    select
        cqilqst.course_id,
        cqilqst.course_name,
        cqilqst.course_description,
        cqilqst.course_status,
        cqilqst.course_teacher_id,
        cqilqst.quiz_name,
        cqilqst.quiz_content,
        cqilqst.quiz_id,
        cqilqst.teacher_full_name,
        cqilqst.teacher_email,
        cqilqst.student_email,
        cqilqst.student_full_name,
        cqilqst.student_username,
        cqilqst.lesson_name,
        cqilqst.lesson_content,
        cqilqst.lesson_id,
        cqilqst.question_title,
        cqilqst.question_id,
        answers.id as answer_id,
        answers.title as answer_title,
        answers.value as answer_value
    from cqilqst
             right join answers on answers.id = any (cqilqst.answer_ids)
    where answer_ids = ''{}'';
end
';




create or replace function f_users_1() returns trigger as
'
    declare
    begin
        raise notice $$THE STUDENT ID TO REMOVE FROM COURSES IS: %$$, old.id;
        update courses
        set student_ids = array_remove(courses.student_ids, old.id)
        where courses.id in (select courses.id
                             from courses
                             where old.id = any (courses.student_ids));

        update teams
        set student_ids = array_remove(teams.student_ids, old.id)
        where teams.id in (select teams.id
                           from teams
                           where old.id = any (teams.student_ids));

        return old;
    end;
'
    language plpgsql;

create or replace function f_lessons_1() returns trigger as
'
    declare
    begin
        raise notice $$THE LESSON ID TO REMOVE FROM COURSES IS: %$$, old.id;
        update courses
        set lesson_ids = array_remove(courses.lesson_ids, old.id)
        where courses.id in (select courses.id
                             from courses
                             where old.id = any (courses.lesson_ids));
        return old;
    end;
'
    language plpgsql;


-- Remove quiz's id from courses.quiz_ids when a quiz is removed
create or replace function f_quizzes_1() returns trigger as
'
    begin
        raise notice $$THE QUIZ ID TO REMOVE FROM COURSES IS: %$$, old.id;
        update courses
        set quiz_ids = array_remove(courses.quiz_ids, old.id)
        where courses.id in (select courses.id
                             from courses
                             where old.id = any (courses.quiz_ids));

        return old;
    end;
'
    language plpgsql;


-- Delete questions when a quiz is removed
create or replace function f_quizzes_2() returns trigger as
'
    #variable_conflict use_column begin
    raise notice $$$THE QUESTION IDS TO REMOVE: %$$, old.question_ids;
    delete
    from questions
    where questions.id in (select question_id
                           from unnest(array(select old.question_ids)) question_id);
    return null;
end;
'
    language plpgsql;


-- Remove questions when their ids are removed from quiz.question_ids
create or replace function f_quizzes_3() returns trigger as
'
    begin
        raise notice $$$THE QUESTION IDS TO REMOVE: %$$, old.question_ids;
        delete
        from questions
        where questions.id in (SELECT *
                               FROM unnest(old.question_ids) unnested
--                                We want the values that do not belong to the intersection
                               WHERE unnested <> any (new.question_ids));
        return new;
    end;
'
    language plpgsql;

create or replace function f_questions_1() returns trigger as
'
    declare
    begin
        raise notice $$THE QUESTION ID TO REMOVE FROM QUIZZES IS: %$$, old.id;
        update quizzes
        set question_ids = array_remove(quizzes.question_ids, old.id)
        where quizzes.id in (select quizzes.id
                             from quizzes
                             where old.id = any (quizzes.question_ids));
        return old;
    end;
'
    language plpgsql;

-- Delete answers when a question is removed
create or replace function f_questions_2() returns trigger as
'
    #variable_conflict use_column begin
    raise notice $$THE ANSWERS IDS TO BE REMOVED: %$$, old.answer_ids;
    delete
    from answers
    where answers.id in (select ids
                         from unnest(array(select old.answer_ids)) ids);
    return null;
end;
'
    language plpgsql;


drop trigger if exists users_1_trg on users;
create trigger users_1_trg
    before delete
    on users
    for each row
execute procedure f_users_1();


drop trigger if exists lessons_1_trg on lessons;
create trigger lessons_1_trg
    before delete
    on lessons
    for each row
execute procedure f_lessons_1();


drop trigger if exists quizzes_1_trg on quizzes;
create trigger quizzes_1_trg
    before delete
    on quizzes
    for each row
execute procedure f_quizzes_1();


drop trigger if exists quizzes_2_trg on quizzes;
create trigger quizzes_2_trg
    after delete
    on quizzes
    for each row
execute procedure f_quizzes_2();

drop trigger if exists quizzes_3_trg on quizzes;
create trigger quizzes_3_trg
    after update
    on quizzes
    for each row
execute procedure f_quizzes_3();



drop trigger if exists questions_1_trg on questions;
create trigger questions_1_trg
    after delete
    on questions
    for each row
execute procedure f_questions_1();

drop trigger if exists questions_2_trg on questions;
create trigger questions_2_trg
    after delete
    on questions
    for each row
execute procedure f_questions_2();

