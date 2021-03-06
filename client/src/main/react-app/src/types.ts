import {AjvError} from "@rjsf/core";

export type Nullable<T> = T | undefined | null;

export interface IEroareDeBaza extends AjvError {
    message: string,
    property: string,
}


export interface IEroareLipseste extends IEroareDeBaza {
    name: "required"
}

export interface IEroareDeLimita extends IEroareDeBaza {
    name: "minLength",
    params: {
        limit: number
    }
}

export interface IEroareDePattern extends IEroareDeBaza {
    name: "pattern",
    params: {
        pattern: string;
    }
}

export type EroareGeneric<T> = T extends IEroareLipseste | IEroareDeLimita | IEroareDePattern ? AjvError : AjvError;


export enum MediaType {
    JSON = 'JSON',
    FORM = 'FORM',
}

export type RequestType = 'POST' | 'GET' | 'DELETE' | 'PUT';

export type Request = {
    method: Nullable<RequestType>;
    mode?: 'cors';
    headers?: Record<string, any>;
    body: string | object;
}


export type HttpGetRequestParams = {
    url: string;
    headers?: Record<string, string>;
}

export type HttpPostReqParams = {
    url: string;
    bodyArg?: Record<any, any>;
    postReqType: MediaType;
    headers?: Record<string, string>
}

export type HttpPutReqParams = HttpPostReqParams;

export interface IUserState extends IUser {
    issuedAt: Nullable<number>;
    expiresAt: Nullable<number>;
    refreshExpirationTime: Nullable<number>;
    expirationTime: Nullable<number>;
    accessToken: Nullable<string>;
    refreshToken: Nullable<string>;
    firstName: Nullable<string>;
    lastName: Nullable<string>;
    roles: Nullable<string[]>;
}

type Address = {
    firstAddressLine: string;
    secondAddressLine: string;
    city: string;
    country: string;
}

export interface IUseProfileFormData {
    firstName: string;
    lastName: string;
    username: string;
    email: string;
    address: Address;
}


export interface ISharedState {
    isLoading: boolean,
    error: Nullable<string>;
    currentPage: Nullable<string>;
}

export interface IUser {
    username: Nullable<string>,
    email: Nullable<string>,
}

export interface IAuthorizationResponse {
    access_token: string;
    expires_in: number;
    id_token: string;
    "not-before-policy": number;
    refresh_expires_in: number;
    refresh_token: string;
    scope: string;
    session_state: string;
    token_type: string;
}

/*
    Here is an example with all the fields completed.

    acr: "0"
    allowed-origins: (2) ["*", "/!*"]
    aud: "account"
    auth_time: 1613382663
    azp: "ripeti-web"
    email: "izzio@11gmail233322.com"
    email_verified: true
    exp: 1613383035
    family_name: "Pugaasliese"
    given_name: "Davidae"
    iat: 1613382735
    iss: "http://localhost:8884/auth/realms/ripeti"
    jti: "8c1b9b8b-7c0b-4829-a695-45f16103de45"
    locale: "en"
    name: "Davidae Pugaasliese"
    nbf: 0
    preferred_username: "izzio"
    resource_access:
        account:
    roles: (3) ["manage-account", "manage-account-links", "view-profile"]
    __proto__: Object
    ripeti-web:
        roles: ["PROFESOR"]
    __proto__: Object
    __proto__: Object
    scope: "openid email profile"
    session_state: "2b08d01e-5792-4867-8fd7-2f3a2c566bbb"
    sub: "fafd791f-ec63-4250-8659-8be9221b088d"
    typ: "Bearer"
*/
export interface IAccessToken {
    acr: number;
    "allow-origins": string[],
    aud: string;
    auth_time: number;
    azp: string;
    email: string;
    email_verified: string;
    exp: number;
    family_name: string;
    given_name: string;
    iat: number;
    iss: string;
    jti: string;
    locale: string;
    name: string;
    preferred_username: string
    resource_access: {
        account: {
            roles: string[] //["manage-account", "manage-account-links", "view-profile"]
        },
        [key: string]: {
            roles: string[]; //professor or student
        }
    }
    scope: string;
    session_state: string;
    sub: string;
    typ: string; //Bearer
}


export enum PageSlug {
    REGISTER = "register",
    HOME = "home",
    USER_PROFILE = "user-profile",
    ERROR = "error",
    LOGOUT = "logout",
    DASHBOARD = "dashboard"
}

export enum PagePathName {
    REGISTER = "/register",
    HOME = "/home",
    USER_PROFILE = "/user-profile",
    ERROR = "/error",
    LOGOUT = "/logout",
    DASHBOARD = "/dashboard"
}

export type IFormError = Record<string, any>;

export interface ILesson {
    id: string,
    lessonName: string,
    lessonContent: string,
    type: "new" | "existing"
    modified: Nullable<boolean>,
    deleted: Nullable<boolean>,
    errors: Nullable<IFormError>;
}

export interface IQuestion {
    id: string,
    title: string,
    modified: Nullable<boolean>,
    deleted: Nullable<boolean>,
    answers: Record<string, IAnswer>
    errors: IFormError,
    type: "new" | "existing"
}

export interface IQuiz {
    id: string,
    quizName: string,
    quizContent: string,
    type: "new" | "existing"
    modified: Nullable<boolean>,
    deleted: Nullable<boolean>,
    questions: Record<string, IQuestion>
    errors: IFormError,
}

export interface IAnswer {
    id: string;
    title: string;
    value: boolean;
    type: "new" | "existing"
    modified: Nullable<boolean>,
    deleted: Nullable<boolean>,
    errors: IFormError,
}



export type ErrorsMap = Record<string, string>;
