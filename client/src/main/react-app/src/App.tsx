import React from 'react';
import {FormProps, withTheme} from '@rjsf/core';
// @ts-ignore
import {Theme as AntDTheme} from '@rjsf/antd';

import './App.css'
import {Col, Layout, PageHeader, Row, Typography} from "antd";
import Title from "antd/es/typography/Title";

// Make modifications to the theme with your own fields and widgets

const Form = withTheme(AntDTheme);


const emailPattern = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
const passwordPattern = "(?=.*[a-z]+)(?=.*[0-9]+)(?=.*[A-Z]+)(?=.*[!@#$%^&*()_+\\[\\]{}:\";,.<>?|=-_]+).{8,20}"

const schema = {
    "type": "object",
    "required": [
        "firstName",
        "lastName",
        "email",
        "username",
        "role",
        "password"
    ],
    "properties": {
        "firstName": {
            "type": "string",
            "title": "Prenume",
            "minLength": 3,
            "pattern": "[a-zA-Z]+"
        },
        "lastName": {
            "type": "string",
            "title": "Nume",
            "minLength": 3,
            "pattern": "[a-zA-Z]+"
        },
        "username": {
            "type": "string",
            "title": "Numele utilizatorlui",
            "minLength": 3,
            "pattern": "[a-zA-Z]+"
        },
        "email": {
            "type": "string",
            "title": "Email",
            "pattern": emailPattern,
        },
        "password": {
            "type": "string",
            "title": "Parola",
            "pattern": passwordPattern,
        },
        "role": {
            "title": "Sunt un",
            "type": "string",
            "enum": [
                "STUDENT",
                "PROFESSOR",
            ],
            "enumNames": [
                "Student",
                "Profesor",
            ]
        },
        "address": {
            "required": [
                "firstAddressLine",
                "secondAddressLine",
                "city",
                "country"

            ],
            "type": "object",
            "title": "Adresa",
            "properties": {
                "firstAddressLine": {
                    "type": "string",
                    "title": "Strada si numar",
                    "minLength": 3,
                },
                "secondAddressLine": {
                    "type": "string",
                    "title": "Bloc, Scara, etc.",
                    "minLength": 3,
                },
                "city": {
                    "type": "string",
                    "title": "Oras",
                    "minLength": 3,
                    "pattern": "[a-zA-Z]+"
                },
                "country": {
                    "type": "string",
                    "enum": [
                        "ROMANIA"
                    ]
                }
            }
        }
    }
};

const uiSchema = {
    "firstName": {
        "ui:autofocus": true,
        "ui:emptyValue": "",
        "ui:autocomplete": "given-name"
    },
    "lastName": {
        "ui:emptyValue": "",
        "ui:autocomplete": "family-name"
    },
    "password": {
        "ui:widget": "password",
        "ui:help": `parola trebuia sa aiba o lungime de cel 
            putin 8 caractere, sa aiba cel putin o litera mare, 1 numar si 
            1 caracter special`
    },

    "role": {
        "ui:help": "Alege daca esti student sau profesor"
    }
};


interface EroareDeBaza {
    message: string,
    property: string,
}

interface EroareLipseste extends EroareDeBaza {
    name: "required"
}

interface EroareDeLimita extends EroareDeBaza {
    name: "minLength",
    params: {
        limit: number
    }
}

interface EroareDePattern extends EroareDeBaza {
    name: "pattern",
    params: {
        pattern: string;
    }
}

type EroareGeneric = EroareLipseste | EroareDeLimita | EroareDePattern


const proprietati: Record<string, string> = {
    ".firstName": "Prenume",
    ".lastName": "Nume de familie",
    ".username": "Nume utilizator",
    ".email": "Email",
    ".password": "Parola secreta",
    ".role": "Rol",
    ".address.firstAddressLine": "Strada si numar",
    ".address.secondAddressLine": "Bloc, Scara, etc.",
    ".address.city": "Oras",
    ".address.country": "Tara"
}

function schimbaMesajDeEroare(key: string, error: EroareLipseste | EroareDeLimita | EroareDePattern) {
    const map = {
        "required": `${proprietati[error.property]} este un camp obligator`,
        "minLength": `${proprietati[error.property]} trebuie sa contina cel putin ${(error as EroareDeLimita).params.limit} caractere`,
        "pattern[a-zA-Z]+": `${proprietati[(error as EroareDePattern).property]} poate contine doar litere`,
        [`pattern${emailPattern}`]: "Adresa de mail nu este valida",
        [`pattern${passwordPattern}`]:
            `parola trebuia sa aiba o lungime de cel 
            putin 8 caractere, sa aiba cel putin o litera mare, 1 numar si 
            1 caracter special`
    }
    if (!(key in map)) {
        return error.message;
    }
    return (map as Record<string, string>)[key]
}

const transformaEroarile = (errors: [
    EroareGeneric
]) => {
    return errors.map(error => {
        const ekey = error.name + ((error as any)?.params?.pattern ? (error as any).params.pattern : "");
        error.message = schimbaMesajDeEroare(ekey, error);
        return error;
    });
}


// const log = (type: any) => console.log.bind(console, type);
const onSubmit = (form: FormProps<any>) => {
    console.log("evt", form);
};

function App() {
    return (
        <Layout>
            <Layout.Header>
            </Layout.Header>
            <PageHeader>
            </PageHeader>
            <Layout.Content>
                <Row gutter={[16, 16]} align="middle" justify="center">
                    <Col span={10} flex="auto">
                        <Typography>
                            <Title>
                                Inregistreazate
                            </Title>
                        </Typography>
                    </Col>
                </Row>
                <Row gutter={[16, 16]} align="middle" justify="center">
                    <Col span={10} flex="auto">
                        {/* @ts-ignore */}
                        <Form schema={schema} uiSchema={uiSchema}
                              onSubmit={onSubmit}
                              showErrorList={false}
                              transformErrors={transformaEroarile}
                        />
                    </Col>
                </Row>
            </Layout.Content>
        </Layout>
    );
}

export default App;
