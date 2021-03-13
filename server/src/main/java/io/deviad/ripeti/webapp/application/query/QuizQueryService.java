package io.deviad.ripeti.webapp.application.query;

import io.deviad.ripeti.webapp.adapter.QuizAdapters;
import io.deviad.ripeti.webapp.ui.queries.AnswerQuery;
import io.deviad.ripeti.webapp.ui.queries.QuestionResponseDto;
import io.deviad.ripeti.webapp.ui.queries.QuizWithoutResults;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@Lazy
@AllArgsConstructor
public class QuizQueryService {

    R2dbcEntityOperations client;


 @Transactional
  public Mono<Map<UUID, Collection<QuizWithoutResults>>> getAllQuizzes(@Parameter(in = ParameterIn.PATH, required = true) String courseId) {

    return getAllQuizEntitiesByCourseId(courseId)
            .onErrorResume(Flux::error)
            .switchIfEmpty(Flux.error( new ResponseStatusException(HttpStatus.BAD_REQUEST, "No quiz found with provided courseId")))
        .flatMap(
             y -> {
                 final Flux<QuestionResponseDto> questions =
                         getQuestionWithoutAnswer(y.id().toString()).onErrorResume(Mono::error);
                 return Flux.zip(Mono.defer(()->Mono.just(y)).repeat(), questions.collect(Collectors.toSet()));
             })
        .flatMap(
            tuple -> {
              var quiz = tuple.getT1();
              quiz = quiz.withQuestions(tuple.getT2());
              return Flux.just(quiz);
            })
        .flatMap(quiz-> Flux.zip(Mono.defer(()->Mono.just(quiz)).repeat(),  combineQuestionsWithAnswers(quiz).collect(Collectors.toSet())))
        .flatMap(tuple-> {
             var q = tuple.getT1();
             q = q.withQuestions(tuple.getT2());
             return Flux.just(q);
         })
        .collectMultimap(QuizWithoutResults::id);
 }

    private Flux<QuestionResponseDto> combineQuestionsWithAnswers(QuizWithoutResults quiz) {
        return Flux.fromIterable(quiz.questions())
                .flatMap(question-> Flux.zip(Mono.defer(()->Mono.just(question)).repeat(), getAnswers(question.id().toString()).collect(Collectors.toSet())))
                .flatMap(x-> {
                    var question = x.getT1();

                    question = question.withAnswers(x.getT2());
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
    Flux<AnswerQuery> getAnswers(String questionId) {
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



