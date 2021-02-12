import React from 'react';
import {FormProps, withTheme} from '@rjsf/core';
// @ts-ignore
import {Theme as AntDTheme} from '@rjsf/antd';

import './App.css'
import {Col, Layout, PageHeader, Row, Typography} from "antd";
import Title from "antd/es/typography/Title";

// Make modifications to the theme with your own fields and widgets

const Form = withTheme(AntDTheme);


const schema = {
    "type": "object",
    "required": [
        "firstName",
        "lastName"
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
        "telephone": {
            "type": "string",
            "title": "Telefon",
            "minLength": 10,
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
    "age": {
        "ui:widget": "updown",
        "ui:title": "Age of person",
        "ui:description": "(earthian year)"
    },
    "password": {
        "ui:widget": "password",
        "ui:help": "Hint: Make it strong!"
    },
    "date": {
        "ui:widget": "alt-datetime"
    },
    "telephone": {
        "ui:options": {
            "inputType": "tel"
        }
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

function schimbaMesajDeEroare(key: string, error: EroareLipseste | EroareDeLimita | EroareDePattern) {
    const map = {
        "required": `${error.property} este un camp obligator`,
        "minLength": `${error.property} trebuie sa contina cel putin ${(error as EroareDeLimita).params.limit} caractere`,
        "pattern[a-zA-Z]+": `${(error as EroareDePattern).property} poate contine doar litere`
    }
    if (!(key in map)) {
        throw new Error(`Campul error name nu este corect`);
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
