set search_path to ripeti;
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
        return new;
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

