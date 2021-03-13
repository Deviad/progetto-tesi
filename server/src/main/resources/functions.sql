set search_path to ripeti;
create or replace function f_users_1() returns trigger as
'
declare
begin
    raise notice $$THE STUDENT ID TO REMOVE FROM COURSES IS: %$$, old.id;
    update courses
    set student_ids = array_remove(courses.student_ids, old.id)
    where courses.id in (select courses.id from courses where old.id = any (courses.student_ids));

    update teams
    set student_ids = array_remove(teams.student_ids, old.id)
    where teams.id in (select teams.id from teams where old.id = any (teams.student_ids));

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
        where courses.id in (select courses.id from courses where old.id = any (courses.lesson_ids));
        return old;
    end;
'
    language plpgsql;

create or replace function f_quizzes_1() returns trigger as
'
    declare
    begin
        raise notice $$THE QUIZ ID TO REMOVE FROM COURSES IS: %$$, old.id;
        update courses
        set quiz_ids = array_remove(courses.quiz_ids, old.id)
        where courses.id in (select courses.id from courses where old.id = any (courses.quiz_ids));
        return old;
    end;
'
    language plpgsql;


create or replace function f_quizzes_2() returns trigger as
'
    declare
    begin
        raise notice $$THE QUIZ ID WHOSE QUESTIONS WILL BE DELETED IS: %$$, old.id;

                delete
                from questions
                where questions.id in (select qs.id
                   from unnest(array(select qi.question_ids
                                     from quizzes qi
                                     where qi.id::text = old.id::text)) question_id
                                        join questions qs on qs.id::text = question_id::text);
        return old;
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
        where quizzes.id in (select quizzes.id from quizzes where old.id = any (quizzes.question_ids));
        return old;
    end;
'
    language plpgsql;


--
-- -- select is_available('ccc');
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



drop trigger if exists questions_1_trg on questions;
create trigger questions_1_trg
    before delete
    on questions
    for each row
execute procedure f_questions_1();

