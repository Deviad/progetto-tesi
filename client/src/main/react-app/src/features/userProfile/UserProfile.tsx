import React, {RefObject, useCallback, useEffect, useRef, useState} from "react";
import {useDispatch, useSelector} from 'react-redux';
import {
    EroareDeLimita,
    EroareDePattern,
    EroareGeneric,
    MediaType,
    Nullable, PageSlug,
    UseProfileFormData,
    User,
    UserState
} from "../../types";
import {RootState} from "../../app/rootReducer";
import {Avatar, Button, Col, Row, Typography} from "antd";
import Title from "antd/es/typography/Title";
import {FormProps, withTheme} from "@rjsf/core";
import {httpGet, httpPut} from "../../httpClient";
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
import {utils} from "../../utils";
import {getAppFailure, getAppLoading, getStopLoading, getSetCurrentPage} from "../../app/appSharedSlice";
import {Dispatch} from "redux";

const Form = withTheme(AntDTheme);

const firstLetter = (arg: string) => {
    return arg.split(" ").map(str => str.substr(0, 1).toUpperCase())[0];
}

const schimbaMesajDeEroare = <T extends unknown>(key: string, error: EroareGeneric<T>) => {
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

const transformaEroarile = <T extends unknown>(errors: EroareGeneric<T>[]) => {
    return errors.map(error => {
        const ekey = error.name + ((error as any)?.params?.pattern ? (error as any).params.pattern : "");
        error.message = schimbaMesajDeEroare(ekey, error);
        return error;
    });
}

const profileSchema = (schema: any) => {
    const tmp: any = omit(schema, "properties.password", "properties.username", "required.password", "required.username")

    tmp.required = tmp.required.filter((x: string)=>x !== "password").filter((x: string)=> x !== "username")
    return tmp;
}

const profileUiSchema = (schema: any, uiSchema: any) => {

    return omit(uiSchema, "password", "username");
}

export const handleGetUserData = async (accessToken: Nullable<string>, username: Nullable<string>) => {

    //Todo: refactor when I create the error component

    if (!accessToken || !username) {
        throw new Error("you are not logged in");
    }

    return await httpGet<UseProfileFormData>({
        url: `${BASE_URL}${USER_ENDPOINT}/${username}`,
        headers: {
            "Authorization": `Bearer ${accessToken}`,
        }
    });
}

export const userProfileInitialState: UseProfileFormData = {
    username: "",
    lastName: "",
    firstName: "",
    email: "",
    address: {
        city: "",
        country: "",
        firstAddressLine: "",
        secondAddressLine: ""
    }
}
async function getData(dispatch: Dispatch<any>, setFData: Function, user: UserState) {
    const {body, status} = await handleGetUserData(user.accessToken, user.username);
    if(body && !status) {
        console.log(body);
        return;
    } else if(body) {
        if (utils.isTrue(body)) {
            setFData(body);
            dispatch(getStopLoading());
        }
    }
}

const onSubmit = (dispatch: Dispatch<any>, setFData: Function, setDisabled: Function, user: UserState) => (form: FormProps<any>) => {
    dispatch(getAppLoading());
    httpPut({
        url: `${BASE_URL}${USER_ENDPOINT}`,
        bodyArg: form.formData,
        postReqType: MediaType.JSON,
        headers: {
            "Authorization": `Bearer ${user.accessToken}`,
        }
    })
        .then(x=> getData(dispatch, setFData, user))
        .then(x=> setDisabled(true))
        .catch(err=>{
            dispatch(getAppFailure(err.toString()));
        })
    ;
}



const toggleSubmitButton = (formRef: RefObject<HTMLFormElement>, isDisabled: boolean) => {
    const button = Object.values(formRef?.current?.formElement)
        .find((x: any) => x.type === 'submit') as NonNullable<HTMLButtonElement>;
    button.disabled = isDisabled;
}


const UserProfile = () => {
    const user = useSelector((state: RootState) => state.user);
    const [isDisabled, setDisabled] = useState(true);

    const [fData, setFData] = useState(userProfileInitialState);

    const formRef: RefObject<HTMLFormElement> = useRef(null);

    const dispatch = useDispatch();

    const cSetEnabled = useCallback((bool: boolean) => (evt: React.MouseEvent<HTMLElement, MouseEvent>) => setDisabled(!bool), []);


    useEffect(() => {
        dispatch(getAppLoading());
        dispatch(getSetCurrentPage(PageSlug.USER_PROFILE))

        if(utils.isTrue(user.accessToken) && utils.isTrue(user.accessToken)) {
            getData(dispatch, setFData, user);
        }

    }, [fData.email, user.accessToken]);


    useEffect(()=> {
        toggleSubmitButton(formRef, isDisabled);
    }, [isDisabled])



    console.log("User", user);
    // @ts-ignore
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
                        {firstLetter(`${user.firstName}`) + firstLetter(`${user.lastName}`)}
                    </Avatar>
                </Col>
            </Row>
            {/*<Row>*/}
            {/*    <pre>{JSON.stringify(user, null, 2)}</pre>*/}
            {/*</Row>*/}
            <Row align="middle" justify="center">
                <Col span={12} flex="auto">
                    <Typography>
                        <Title>
                            Schimba datele de inregistrare
                        </Title>
                    </Typography>
                    <Button
                        type="primary"
                        icon={<PoweroffOutlined/>}
                        onClick={cSetEnabled(isDisabled)}
                    />
                </Col>
            </Row>
            <Row align="middle" justify="center">
                <Col span={12} flex="auto">
                    {  /* @ts-ignore */  }
                    {( !fData?.email) ? <Form schema={profileSchema(registrationSchema)} uiSchema={profileUiSchema(registrationUiSchema)}
                     /* @ts-ignore */
                          onSubmit={onSubmit(dispatch, setFData, user)}
                          noHtml5Validate={true}
                          liveValidate={true}
                          /* @ts-ignore */
                          ref={formRef}
                          showErrorList={false}
                          transformErrors={transformaEroarile}
                          disabled={isDisabled}

                    /> : /* @ts-ignore */
                        <Form schema={profileSchema(registrationSchema)} uiSchema={profileUiSchema(registrationUiSchema)}
                            /* @ts-ignore */
                              onSubmit={onSubmit(dispatch, setFData, setDisabled, user)}
                                 noHtml5Validate={true}
                                 liveValidate={true}
                                 /* @ts-ignore */
                                 ref={formRef}
                                 showErrorList={false}
                                 transformErrors={transformaEroarile}
                                 formData={fData}
                                 disabled={isDisabled}
                    />}
                </Col>
            </Row>
        </>
    );

}
export {UserProfile};
