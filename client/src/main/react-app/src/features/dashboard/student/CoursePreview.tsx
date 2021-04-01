import {message, Modal} from "antd";
import React from "react";
import Title from "antd/es/typography/Title";
import {httpPut} from "../../../httpClient";
import {MediaType} from "../../../types";
import {BASE_URL, COURSE_ENDPOINT} from "../../../constants";
import {useSelector} from "react-redux";
import {RootState} from "../../../app/rootReducer";
import {useHistory} from "react-router-dom";

export const renderModalContent = (description: string, teacherName: string) => {

    return (
        <>

            <div>
                <Title level={5}>
                    Professor <br/>
                </Title>
            </div>
            <div className="steps-content">
                {teacherName}
            </div>

            <div>
                <Title level={5}>
                    Descrierea cursului <br/>
                </Title>
            </div>
            <div className="steps-content" dangerouslySetInnerHTML={{__html: description}}>
            </div>
        </>)
}
export const CoursePreview = ({
                                  id,
                                  title,
                                  content: description,
                                  modalVisible,
                                  teacherName,
                                  toggleModal
                              }: {
    id: string,
    title: string,
    content: string,
    modalVisible: boolean,
    toggleModal: Function,
    teacherName: string,
}) => {

    const user = useSelector((state: RootState) => state.user)
    const history = useHistory();

    const ok = () => {

        const d = user.expiresAt && user.expiresAt * 1000;
        const expired = d && d <= Date.now();
        if (expired) {
            history.push("/login")
        }

        httpPut({
            postReqType: MediaType.JSON,
            url: `${BASE_URL}${COURSE_ENDPOINT}/${id}/assignstudent`,
            headers: {
                "Authorization": `Bearer ${user.accessToken}`,
            }
        })
        .then((res) => {
            if(!res.status) {
                message.error((res as any).body?.message)
                return;
            }
            toggleModal(false);
        })
        .catch(error =>  {
          console.log(error);
        })

    }
    const cancel = () => {
        toggleModal(false);
    }

    return (
        <>
            <Modal
                title={title}
                centered={true}
                visible={modalVisible}
                onOk={ok}
                cancelText="Inapoi"
                okText="Inscriete"
                onCancel={cancel}
                width={"60vw"}
            >
                {renderModalContent(description, teacherName)}
            </Modal>
        </>
    )
}


