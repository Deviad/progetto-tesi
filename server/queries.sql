
-- Get all quizzes by course id attaching questions, answers.

select qi.*, ripeti_questions.*, ripeti_answers.*
from unnest(array(select c.quiz_ids from courses c where c.id::text = '802ba907-f915-46ed-aaf9-751d8db8c9f2')) quiz_id
         join quizzes qi on qi.id = quiz_id
         join (select qs.* from questions qs) ripeti_questions on ripeti_questions.id = any(qi.question_ids)
         join (select ans.* from answers ans) ripeti_answers on ripeti_answers.id = any(ripeti_questions.answer_ids)


