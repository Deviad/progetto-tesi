import {v4 as uuidv4} from "uuid";
import {produce} from "immer";
import {WizardStepsState} from "../../WizardSteps";

export function newDefaultQuizAdded(setState: Function) {
    return () => {
        const quizId = uuidv4();
        const questionId = uuidv4();
        const answerId = uuidv4();

        setState(produce((draft: WizardStepsState) => {
            draft
                .steps[2]
                .quizzes[quizId] = {
                id: quizId,
                questions: {
                    [questionId]: {
                        modified: false,
                        deleted: false,
                        id: questionId,
                        title: "Completeaza",
                        answers: {
                            [answerId]: {
                                value: true,
                                id: answerId,
                                title: "Completeaza",
                                modified: false,
                                deleted: false,
                                errors: {},
                            }
                        },
                        errors: {},
                    }
                },
                quizName: "Introduci o denumire",
                quizContent: "Introduci o descriere",
                type: "new",
                modified: false,
                deleted: false,
                errors: {},
            }
        }));
    };
}
