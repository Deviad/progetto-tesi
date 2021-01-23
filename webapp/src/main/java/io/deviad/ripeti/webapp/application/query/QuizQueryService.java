package io.deviad.ripeti.webapp.application.query;

import io.deviad.ripeti.webapp.adapter.QuizAdapters;
import io.deviad.ripeti.webapp.api.command.AnswerDto;
import io.deviad.ripeti.webapp.api.queries.QuestionResponseDto;
import io.deviad.ripeti.webapp.api.queries.QuizWithoutResults;
import io.micrometer.core.annotation.Timed;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@Lazy
@AllArgsConstructor
public class QuizQueryService {

    R2dbcEntityOperations client;


 @Transactional(readOnly = true, propagation = Propagation.NESTED)
  public Flux<QuizWithoutResults> getAllQuizzes(String courseId) {


    return getAllQuizEntitiesByCourseId(courseId)
            .onErrorResume(Flux::error)
            .switchIfEmpty(Flux.error(new RuntimeException("No quiz found with provided courseId")))
        .flatMap(
            y -> {
              final Flux<QuestionResponseDto> questions =
                      getQuestionWithoutAnswer(y.id().toString()).onErrorResume(Mono::error);
              return Flux.zip(Mono.just(y), questions);
            })
        .map(
            tuple -> {
              var r = QuestionResponseDto.builder()
                      .id(tuple.getT2().id())
                      .title(tuple.getT2().title())
                      .answers(tuple.getT2().answers())
                      .build();
              final Set<QuestionResponseDto> questions =
                  Stream.of(
                          tuple.getT1().questions(),
                          Set.of(r))
                      .flatMap(Stream::ofNullable)
                      .flatMap(Collection::stream)
                      .collect(Collectors.toSet());

              var quiz = tuple.getT1();
              quiz = quiz.withQuestions(questions);
              return quiz;
            })
        .flatMap(quiz-> Flux.zip(Flux.just(quiz),  combineQuestionsWithAnswers(quiz)))
         .flatMap(tuple-> {
             var q = tuple.getT1();
             q = q.withQuestions(Set.of(tuple.getT2()));
             return Flux.just(q);
         });
 }

    private Flux<QuestionResponseDto> combineQuestionsWithAnswers(QuizWithoutResults quiz) {
        return Flux.fromIterable(quiz.questions())
                .flatMap(question-> Flux.zip(Mono.just(question), getAnswers(question.id().toString())))
                .flatMap(x-> {
                    var question = x.getT1();

                    question = question.withAnswers(Stream.concat(x.getT1().answers().stream(), Set.of(x.getT2()).stream())
                            .flatMap(Stream::ofNullable)
                            .collect(Collectors.toSet()));
                    return Flux.just(question);
                });
    }



    @Timed("getAllQuizEntitiesByCourseId")
     Flux<QuizWithoutResults> getAllQuizEntitiesByCourseId(String courseId) {
        //language=PostgreSQL
        String query =
                """
                select qi.* from unnest(array(select c.quiz_ids from courses c where c.id::text = $1)) quiz_id
                join quizzes qi on qi.id=quiz_id
                """;
        return client.getDatabaseClient().sql(query)
                .bind("$1", courseId)
                .map(QuizAdapters.QUIZ_FROM_ROW_MAP::apply)
                .all();
    }

    @Timed("getAllQuestionEntitiesByQuizId")
    Flux<QuestionResponseDto> getQuestionWithoutAnswer(String quizId) {
        //language=PostgreSQL
        String query = """
             select qs.* from unnest(array(select qi.question_ids from quizzes qi where qi.id::text = $1)) question_id
             join questions qs on qs.id=question_id
            """;

        return client.getDatabaseClient().sql(query)
                .bind("$1", quizId)
                .map(QuizAdapters.QUESTION_FROM_ROW_MAP::apply)
                .all();

    }

    @Timed("getAnswers")
    Flux<AnswerDto> getAnswers(String questionId) {
        //language=PostgreSQL
        String query = """
            select a.* from unnest(array(select qs.answer_ids from questions qs where qs.id::text = $1)) answer_id
            join answers a on a.id=answer_id
            """;

        return client.getDatabaseClient().sql(query)
                .bind("$1", questionId)
                .map(QuizAdapters.ANSWER_FROM_ROW_MAP::apply)
                .all();

    }


}



