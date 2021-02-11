import React from 'react';
import {withTheme} from '@rjsf/core';
// @ts-ignore
import {Theme as AntDTheme} from '@rjsf/antd';

import './App.css'
import {Col, Layout, PageHeader, Row} from "antd";

// Make modifications to the theme with your own fields and widgets

const Form = withTheme(AntDTheme);


const schema = {
    "title": "A registration form",
    "description": "A simple form example.",
    "type": "object",
    "required": [
        "firstName",
        "lastName"
    ],
    "properties": {
        "firstName": {
            "type": "string",
            "title": "First name",
            "default": "Chuck"
        },
        "lastName": {
            "type": "string",
            "title": "Last name"
        },
        "telephone": {
            "type": "string",
            "title": "Telephone",
            "minLength": 10
        }
    }
};

const uiSchema = {
    "firstName": {
        "ui:autofocus": true,
        "ui:emptyValue": "",
        "ui:autocomplete": "family-name"
    },
    "lastName": {
        "ui:emptyValue": "",
        "ui:autocomplete": "given-name"
    },
    "age": {
        "ui:widget": "updown",
        "ui:title": "Age of person",
        "ui:description": "(earthian year)"
    },
    "bio": {
        "ui:widget": "textarea"
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

const log = (type: any) => console.log.bind(console, type);
const onSubmit = (data: any) => log(data);

function App() {
    return (

         <Layout>
             <Layout.Header>
             </Layout.Header>
             <Layout.Content>
                 <Row gutter={[16, 16]} align="middle">
                     <Col span={10} flex="auto" push="6">
                         {/* @ts-ignore */}
                         <Form schema={schema} uiSchema={uiSchema}
                               onChange={log("changed")}
                               onSubmit={onSubmit}
                               onError={log("errors")}/>
                     </Col>
                 </Row>
             </Layout.Content>
         </Layout>
    );
}

export default App;
