set search_path to ripeti;

-- Get all quizzes by course id attaching questions, answers.

select qi.*, ripeti_questions.*, ripeti_answers.*
from unnest(array(select c.quiz_ids from courses c where c.id::text = '802ba907-f915-46ed-aaf9-751d8db8c9f2')) quiz_id
         join quizzes qi on qi.id = quiz_id
         join (select qs.* from questions qs) ripeti_questions on ripeti_questions.id = any(qi.question_ids)
         join (select ans.* from answers ans) ripeti_answers on ripeti_answers.id = any(ripeti_questions.answer_ids);


select u.* from unnest(array(select c.student_ids from courses c where c.id::text = '')) user_id
                    join users u on u.id=user_id;


-- GET ALL QUIZZES PER COURSE
select qi.* from unnest(array(select c.quiz_ids from courses c where c.id::text = '802ba907-f915-46ed-aaf9-751d8db8c9f2')) quiz_id
                     join quizzes qi on qi.id=quiz_id;




-- GET ALL QUESTIONS PER QUIZ
select qs.* from unnest(array(select qi.question_ids from quizzes qi where qi.id::text = '409a1c2b-b1e5-42a7-b2ae-58212aaf3b37')) question_id
                     join questions qs on qs.id=question_id;


-- GET ALL ANSWERS PER QUESTION
select a.* from unnest(array(select qs.answer_ids from questions qs where qs.id::text = 'c11ee15c-7792-41ce-b537-f9ef6d695b33')) answer_id
                    join answers a on a.id=answer_id;


-- drop type if exists user_role cascade;
-- create type user_role as enum ('STUDENT', 'PROFESSOR');


-- alter table if exists users
--     add role user_role;


-- GET ALL QUESTIONS PER QUIZ varianta 2
select qs.id from questions qs where qs.id = any(array((select qz.question_ids from quizzes qz where qz.id::text = '409a1c2b-b1e5-42a7-b2ae-58212aaf3b37')));
