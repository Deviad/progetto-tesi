import {Col, Form, Input, Row, Typography} from "antd";
import Title from "antd/es/typography/Title";
import ReactQuill from "react-quill";
import React, {FC, useEffect} from "react";
import {useState} from "reinspect";


export const CourseAddition: FC = () => {
    const [dashboardState, setDashboardState] = useState({editor: {text: ""}}, "dashboard-state");

    const onSubmit = (data: any) => {
        console.log(data);
    };

    const [form] = Form.useForm();

    const handleEditorChange = (value: string) => {
        setDashboardState({
            editor: {
                text: value
            }
        })
    }

    useEffect(() => {
        form.setFieldsValue({
            content: dashboardState.editor.text,
        });
    }, [dashboardState.editor.text]);

    return (
        <Col flex="auto">
            <Row justify="center">
                <Col>
                    <Typography>
                        <Title level={3}>
                            Adauga un curs
                        </Title>
                    </Typography>
                </Col>
            </Row>
            <Row justify="start">
                <Col span={20} push={1} flex="auto">
                    <Form onFinish={onSubmit} form={form}>
                        <Typography>
                            <Title level={4}>
                                Denumire
                            </Title>
                        </Typography>
                        <Form.Item
                            name="name"
                            rules={[{
                                required: true,
                                min: 3, max: 100,
                                message: 'Introduci o denumire intre 3 si 100 de caractere',
                                type: 'string'
                            }]}>
                            <Input/>
                        </Form.Item>
                        <br/>
                        <br/>
                        <Typography>
                            <Title level={4}>
                                Descriere
                            </Title>
                        </Typography>
                        <ReactQuill style={{background: "#fff"}} value={dashboardState.editor.text}
                                    onChange={handleEditorChange}/>
                        <Form.Item name="content" hidden>
                            <Input/>
                        </Form.Item>
                        <br/>
                        <input type="submit"/>
                    </Form>
                </Col>
            </Row>
        </Col>
    );
}
