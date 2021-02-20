import React, {useCallback, useState} from "react";
import {useSelector} from 'react-redux';
import {EroareDeLimita, EroareDePattern, EroareGeneric, MediaType, User} from "../../types";
import {RootState} from "../../app/rootReducer";
import {Avatar, Button, Col, Row, Typography} from "antd";
import Title from "antd/es/typography/Title";
import {FormProps, withTheme} from "@rjsf/core";
import {httpPut} from "../../httpClient";
import {
    BASE_URL,
    emailPattern,
    passwordPattern,
    proprietati,
    registrationSchema,
    registrationUiSchema,
    USER_ENDPOINT
} from "../../constants";
import {omit} from "lodash";

// @ts-ignore
import {Theme as AntDTheme} from '@rjsf/antd';
import {PoweroffOutlined} from "@ant-design/icons";

const Form = withTheme(AntDTheme);

const firstLetter = (arg: string) => {
    return arg.split(" ").map(str => str.substr(0, 1).toUpperCase()).join("");
}

const onSubmit = async (form: FormProps<any>) => {
    await httpPut({
        url: `${BASE_URL}${USER_ENDPOINT}`,
        bodyArg: form.formData,
        postReqType: MediaType.JSON
    });
};

const schimbaMesajDeEroare = <T extends unknown> (key: string, error: EroareGeneric<T>) => {
    const map = {
        "required": `${proprietati[error.property]} este un camp obligator`,
        "minLength": `${proprietati[error.property]} trebuie sa contina cel putin ${(error as EroareDeLimita).params.limit} caractere`,
        "pattern[a-zA-Z]+": `${proprietati[(error as EroareDePattern).property]} poate contine doar litere`,
        [`pattern${emailPattern}`]: "Adresa de mail nu este valida",
        [`pattern${passwordPattern}`]:
            `parola trebuie sa aiba o lungime de cel 
            putin 8 caractere, sa aiba cel putin o litera mare, 1 numar si 
            1 caracter special`
    }
    if (!(key in map)) {
        return error.message;
    }
    return (map as Record<string, string>)[key]
}

const transformaEroarile = <T extends unknown> (errors: EroareGeneric<T>[]) => {
    return errors.map(error => {
        const ekey = error.name + ((error as any)?.params?.pattern ? (error as any).params.pattern : "");
        error.message = schimbaMesajDeEroare(ekey, error);
        return error;
    });
}

const profileSchema = (schema: any)=> {
    return omit(schema, "properties.password", "properties.username")
}

const profileUiSchema = (schema: any, uiSchema: any) => {

    return omit(uiSchema, "password")
}

const UserProfile = () => {
    const user = useSelector((state: RootState) => state.user);
    const [isDisabled, setDisabled] = useState(true);

    const cSetEnabled = useCallback((bool: boolean)=>(evt: React.MouseEvent<HTMLElement, MouseEvent>)=>setDisabled(!bool), []);

    console.log("User", user);
    return (
        <>
            <Row>
                <Col style={{display: "flex", alignSelf: "left", flexBasis: "20%"}}>
                    <Typography>
                        <Title level={3}>
                            Bine ai venit <br/>
                            {`${user.firstName} ${user.lastName}`}
                        </Title>
                    </Typography>
                </Col>
                <Col style={{
                    display: "flex",
                    flexBasis: "80%",
                    flexDirection: "row",
                    alignItems: "center",
                    justifyContent: "flex-end"
                }}>
                    <Avatar style={{backgroundColor: "#00a2ae", verticalAlign: 'middle', marginRight: "1rem"}}
                            size="large" gap={20}>
                        {firstLetter(`${user.firstName} ${user.lastName}`)}
                    </Avatar>
                </Col>
            </Row>
            <Row>
                <pre>{JSON.stringify(user, null, 2)}</pre>
            </Row>

            <Row align="middle" justify="center">
                <Col span={12} flex="auto">
                    <Typography>
                        <Title>
                            Schimba datele de inregistrare
                        </Title>
                    </Typography>
                    <Button
                        type="primary"
                        icon={<PoweroffOutlined />}
                        onClick={cSetEnabled(isDisabled)}
                    />
                </Col>
            </Row>
            <Row align="middle" justify="center">
                <Col span={12} flex="auto">
                    { /* @ts-ignore */}
                    <Form schema={profileSchema(registrationSchema)} uiSchema={profileUiSchema(registrationUiSchema)}
                          onSubmit={onSubmit}
                          noHtml5Validate={true}
                          liveValidate={true}
                          showErrorList={false}
                          transformErrors={transformaEroarile}
                          disabled={isDisabled}
                    />
                </Col>
            </Row>
        </>
    );

}
export {UserProfile};
