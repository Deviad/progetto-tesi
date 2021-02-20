import React, {FC, RefObject, useEffect, useRef} from "react";
import {Col, Row, Typography} from "antd";
import Title from "antd/es/typography/Title";
import {FormProps, withTheme} from "@rjsf/core";
import {httpPost} from "../../httpClient";
import {
    BASE_URL,
    emailPattern,
    passwordPattern,
    proprietati,
    registrationSchema,
    registrationUiSchema,
    USER_ENDPOINT
} from "../../constants";
import {EroareDeLimita, EroareDePattern, EroareGeneric, MediaType} from "../../types";
// @ts-ignore
import {Theme as AntDTheme} from '@rjsf/antd';

const Form = withTheme(AntDTheme);


// Aici folosesc un escamotage pentru ca sintaxa nativ nu supoarta:
//
// const ceva = <T extends unknown> (key: string, error: EroareGeneric<T>) {}
// arunca eroare unclosed `T` tag
// de aceea folosesc <T extends unknown>
// deci dupa primul T neaparat trebuie sa folosim extend
// este o constrangere al limbajului

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


const autofocusPePrimulCamp = (formRef: RefObject<HTMLFormElement>) => {
    const input = Object.values(formRef?.current?.formElement)
        .find((x: any) => x.id === 'root_firstName') as NonNullable<HTMLInputElement>;
    input.focus();
}

const onSubmit = async (form: FormProps<any>) => {
    await httpPost({
        url: `${BASE_URL}${USER_ENDPOINT}`,
        bodyArg: form.formData,
        postReqType: MediaType.JSON
    });
};

const transformaEroarile = <T extends unknown> (errors: EroareGeneric<T>[]) => {
    return errors.map(error => {
        const ekey = error.name + ((error as any)?.params?.pattern ? (error as any).params.pattern : "");
        error.message = schimbaMesajDeEroare(ekey, error);
        return error;
    });
}

export const Registration: FC = () => {
    const formRef: RefObject<HTMLFormElement> = useRef(null);
    useEffect(() => {
        autofocusPePrimulCamp(formRef);
    }, []);

    return (<>
        <Row align="middle" justify="center">
            <Col span={12} flex="auto">
                <Typography>
                    <Title>
                        Inregistreazate
                    </Title>
                </Typography>
            </Col>
        </Row>
        <Row align="middle" justify="center">
            <Col span={12} flex="auto">
                { /* @ts-ignore */}
                <Form schema={registrationSchema} uiSchema={registrationUiSchema}
                      ref={formRef}
                      onSubmit={onSubmit}
                      showErrorList={false}
                      transformErrors={transformaEroarile}
                />
            </Col>
        </Row>
    </>)
}

