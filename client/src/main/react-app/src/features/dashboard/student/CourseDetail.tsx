import {useHistory, useParams} from "react-router-dom";
import React, {MouseEventHandler, useEffect} from "react";
import {Button, Card, Col, Collapse, Radio, Row, Typography} from "antd";
import Title from "antd/es/typography/Title";
import {useState} from "reinspect";
import {httpGet} from "../../../httpClient";
import {BASE_URL, COURSE_ENDPOINT} from "../../../constants";
import {useSelector} from "react-redux";
import {RootState} from "../../../app/rootReducer";
import "./courseDetails.scss"
import {RadioChangeEvent} from "antd/es";

interface ICourseInfo {
    courseName: string;
    courseId: string;
    courseDescription: string;
    courseStatus: "LIVE" | "DRAFT";
    teacherName: string;
    teacherEmail: string;
    teacherId: string;
    lessonList: {
        id: string;
        lessonName: string;
        lessonContent: string;
    }[];
    quizzes: Record<string, {
        id: string;
        quizName: string;
        questions: Record<string, {
            id: string;
            title: string;
            answers: Record<string, {
                id: string;
                title: string;
                value: boolean;
            }>;
        }>
    }>
    studentList: {
        username: string;
        email: string;
        studentCompleteName: string
    }[];
}

interface QuizState {
    quizzes: Record<string, {
        id: string;
        quizName: string;
        questions: Record<string, {
            id: string;
            title: string;
            answers: Record<string, {
                id: string;
                title: string;
                value: boolean;
            }>;
        }>
    }>
}

const {Panel} = Collapse;

export const handleAnswerClick = ({
                                      quizId,
                                      questionId,
                                      answerId,
                                      setAnswerState,
                                      answerState
                                  }: {
    quizId: string,
    questionId: string,
    answerId: string,
    setAnswerState: Function,
    answerState: Record<string, any>
}) =>
    (evt: RadioChangeEvent) => {
        const newState = {
            ...answerState,
            quizzes: {
                ...answerState?.quizzes,
                [quizId]: {
                    ...answerState[quizId],
                    questions: {
                        ...answerState?.quizzes[quizId]?.questions,
                        [questionId]: {
                            ...answerState?.quizzes[quizId]?.questions[questionId],
                            answers: {
                                ...answerState?.quizzes[quizId]?.questions[questionId]?.answers,
                                [answerId]: {
                                    ...answerState?.quizzes[quizId]?.questions[questionId]?.answers[answerId],
                                    value: evt.target.value
                                }
                            }
                        }
                    }
                }
            }
        }
        setAnswerState(newState);
    };

const verifyQuiz = ({backendData, state, setState, quizId}: { backendData: QuizState, state: QuizState, setState: Function, quizId: string }) =>
    (evt: React.MouseEvent<HTMLElement>) => {

        const incorrect = [];

            for (const qs of Object.values(backendData.quizzes[quizId].questions)) {
                const {id: questionId} = qs;
                for (const ans of Object.values(qs.answers)) {
                    const {id: ansId} = ans;
                    if (backendData?.quizzes[quizId]?.questions[questionId]?.answers[ansId].value !==
                        state?.quizzes[quizId]?.questions[questionId]?.answers[ansId].value
                    ) {
                        incorrect.push(ansId);
                    }
                }
            }

        setState({
            ...state,
            errors: incorrect.length
        });
    }

export const CourseDetail = () => {

    const {id} = useParams<{ id: string }>();

    const [state, setState] = useState({} as ICourseInfo, 'course-details');

    const [answerState, setAnswerState] = useState({quizzes: {} as Record<string, any>, errors: 0}, 'set-answer');

    const user = useSelector((state: RootState) => state.user);
    const history = useHistory();

    const d = user.expiresAt && user.expiresAt * 1000;
    const expired = d && d <= Date.now();

    useEffect(() => {

        if (expired) {
            history.push("/login")
        }
        const init = async () => {
            const {status, body} = await httpGet<ICourseInfo>({
                url: `${BASE_URL}${COURSE_ENDPOINT}/${id}/getcourseinfo`,
                headers: {
                    "Authorization": `Bearer ${user.accessToken}`,
                }
            })
            if (status && body) {
                setState(body);
            }
        }
        init();
    }, [id]);


    if (!state.courseId) {
        return (<>
            <Col flex="auto">
                <Row justify="start">
                    <Col className="ant-col-xs-24--ripeti-card-row ant-col-md-22--ripeti-card-row"
                         lg={{span: 22, flex: "auto"}} md={{span: 22, flex: "auto", push: 2}}
                         xs={{flex: "auto", span: 24}} style={{}}>
                        <Typography>
                            <Title level={3}>
                                Loading...
                            </Title>
                        </Typography>
                    </Col>
                </Row>
            </Col>
        </>);
    }
    return (
        <>
            <Col flex="auto">
                <Row justify="start">
                    <Col className="ant-col-xs-24--ripeti-card-row ant-col-md-22--ripeti-card-row"
                         lg={{span: 22, flex: "auto"}} md={{span: 22, flex: "auto", push: 2}}
                         xs={{flex: "auto", span: 24}}>
                        <Typography>
                            <Title level={3}>
                                {state.courseName}
                            </Title>
                        </Typography>
                    </Col>
                </Row>
                {state.teacherId && <Row justify="start">
                    <Col className="ant-col-xs-24--ripeti-card-row ant-col-md-22--ripeti-card-row"
                         lg={{span: 22, flex: "auto"}} md={{span: 22, flex: "auto", push: 2}}
                         xs={{flex: "auto", span: 24}}>
                        <Card className="course-description" title="Informatile de contact">
                            Lector: {state.teacherName} <br/>
                            e-mail: {state.teacherEmail}
                        </Card>
                    </Col>
                </Row>}
                <br/>
                <br/>
                {state.courseDescription && <Row justify="start">
                    <Col className="ant-col-xs-24--ripeti-card-row ant-col-md-22--ripeti-card-row"
                         lg={{span: 22, flex: "auto"}} md={{span: 22, flex: "auto", push: 2}}
                         xs={{flex: "auto", span: 24}}>
                        <Card className="course-description" title="Descrierea cursului">
                            <div dangerouslySetInnerHTML={{__html: state.courseDescription}}></div>
                        </Card>
                    </Col>
                </Row>}
                <br/>
                <br/>
                {state?.studentList?.length > 0 && <Row justify="start">
                    <Col className="ant-col-xs-24--ripeti-card-row ant-col-md-22--ripeti-card-row"
                         lg={{span: 22, flex: "auto"}} md={{span: 22, flex: "auto", push: 2}}
                         xs={{flex: "auto", span: 24}}>
                        <Card className="enrolled-students" title="Studenti deja inscrisi">
                            {state.studentList.map(x => <> {x.studentCompleteName} <br/></>)}
                        </Card>
                    </Col>
                </Row>}
                <br/>
                <br/>
                {state?.lessonList?.length > 0 && <Row justify="start">
                    <Col className="ant-col-xs-24--ripeti-card-row ant-col-md-22--ripeti-card-row"
                         lg={{span: 22, flex: "auto"}} md={{span: 22, flex: "auto", push: 2}}
                         xs={{flex: "auto", span: 24}}>
                        <Card className="course-lessons" title="Transcrierea lectilor">
                            <Collapse accordion style={{width: "100%"}}>
                                {state.lessonList.map(l => {
                                    return (
                                        <Panel header={l.lessonName} key={l.id}>
                                            <div dangerouslySetInnerHTML={{__html: l.lessonContent}}></div>
                                        </Panel>
                                    )
                                })}
                            </Collapse>
                        </Card>
                    </Col>
                </Row>}
                <br/>
                <br/>
                {state.quizzes && Object.values(state.quizzes).length > 0 && <Row justify="start">
                    <Col className="ant-col-xs-24--ripeti-card-row ant-col-md-22--ripeti-card-row"
                         lg={{span: 22, flex: "auto"}} md={{span: 22, flex: "auto", push: 2}}
                         xs={{flex: "auto", span: 24}}>
                        <Card className="course-lessons" title="Teste de autoevaluare">
                            <Collapse accordion>
                                {Object.values(state.quizzes).map(qi => {
                                    return (
                                        <Panel header={qi.quizName} id={qi.id} key={qi.id}
                                               className="answer-card__body">
                                            {Object.values(qi.questions).length > 0 &&
                                            Object.values(qi.questions).map(qs => {
                                                return (
                                                    <Card className="quiz-questions" title={qs.title}>
                                                        {
                                                            Object.values(qs.answers).length > 0 &&
                                                            Object.values(qs.answers).map(a => {
                                                                return (
                                                                    <div className="answer-container">
                                                                        <div
                                                                            style={{marginRight: '1rem'}}>{a.title}</div>
                                                                        <div>
                                                                            <Radio.Group onChange={handleAnswerClick({
                                                                                quizId: qi.id,
                                                                                questionId: qs.id,
                                                                                answerId: a.id,
                                                                                answerState,
                                                                                setAnswerState
                                                                            })}>
                                                                                <Radio value={true}>corect</Radio>
                                                                                <Radio value={false}>incorect</Radio>
                                                                            </Radio.Group>
                                                                        </div>
                                                                    </div>
                                                                )
                                                            })
                                                        }
                                                        <br/>
                                                        <br/>
                                                        <div>
                                                            <Button type="primary" onClick={verifyQuiz({
                                                                backendData: {quizzes: state.quizzes},
                                                                quizId: qi.id,
                                                                state: answerState,
                                                                setState: setAnswerState
                                                            })}>
                                                                Verifica
                                                            </Button>
                                                        </div> <br />
                                                        Ai totalizat {answerState.errors + ""} eroari
                                                    </Card>
                                                )
                                            })}
                                        </Panel>)
                                })}
                            </Collapse>
                        </Card>
                    </Col>
                </Row>}
            </Col>
        </>
    );
}


