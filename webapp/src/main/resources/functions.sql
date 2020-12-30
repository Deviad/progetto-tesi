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
--
-- -- select is_available('ccc');
drop trigger if exists users_1_trg on users;
create trigger users_1_trg
    before delete
    on users
    for each row
execute procedure f_users_1();
