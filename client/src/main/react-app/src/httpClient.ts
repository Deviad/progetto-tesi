import {HttpPostReqParams, MediaType, Nullable, Request} from "./types";
import {Log} from "./utils";
import {utils} from "./utils";

export enum ContentType {
    JSON_UTF8 = "application/json;charset=UTF-8",
    URL_ENCODED = "application/x-www-form-urlencoded",
}


export const addContentType = (contentType: ContentType, obj: Record<string, ContentType> = {}): Record<string, any> => {
    if (contentType === ContentType.JSON_UTF8) {
        obj['Content-Type'] = ContentType.JSON_UTF8;
    } else if (contentType === ContentType.URL_ENCODED) {
        obj['Content-Type'] = ContentType.URL_ENCODED;
    }
    return obj;
};

export const createReq = <T extends Nullable<'POST'> | Nullable<'PUT'>, FDATA extends NonNullable<string> | NonNullable<object>>
(postReqType: MediaType, bodyArg: FDATA, httpMethod?: T) => {
    const config: Record<keyof typeof MediaType, () => Request> = {
        JSON: () => ({
            method: httpMethod ?? "POST",
            mode: 'cors',
            headers: addContentType(ContentType.JSON_UTF8),
            body: JSON.stringify(bodyArg),
        }),
        FORM: () => ({
            method: httpMethod ?? "POST",
            mode: 'cors',
            headers: addContentType(ContentType.JSON_UTF8),
            body: utils.toFormUrlEncoded(bodyArg as NonNullable<object>),
        }),
    };
    return config[(postReqType)]();
};


export const httpGet = async <RESPONSE>(url: string): Promise<(boolean | Nullable<RESPONSE>)[]> => {
    try {
        const response: Response = await fetch(url, {
            method: 'GET',
            mode: 'cors',
            headers: addContentType(ContentType.JSON_UTF8),
        });
        const responseOk = Math.floor(response.status / 100) === 2;
        const body: Nullable<RESPONSE> = await response.json();
        return ([responseOk, body] as (boolean | Nullable<RESPONSE>)[]);
    } catch (error) {
        Log.error(error);
        return [false, error];
    }
};


export const httpPost = async <RESPONSE>(params: HttpPostReqParams): Promise<(boolean | Nullable<RESPONSE>)[]> => {

    const {url, bodyArg, postReqType} = params;
    try {
        let reqConfig = serializeDateAccordingToContentType<"POST", Record<string, any>>(postReqType, bodyArg, "POST");
        const response: Response = await fetch(url, reqConfig);
        const responseOk = Math.floor(response.status / 100) === 2;
        const body: Nullable<RESPONSE> = await response.json();
        return ([responseOk, body] as (boolean | Nullable<RESPONSE>)[]);
    } catch (error) {
        Log.error(error);
        return [false, error];
    }
};

export const httpPut = async <RESPONSE>(params: HttpPostReqParams): Promise<(boolean | Nullable<RESPONSE>)[]> => {

    const {url, bodyArg, postReqType} = params;
    try {
        let reqConfig = serializeDateAccordingToContentType<"PUT", Record<string, any>>(postReqType, bodyArg, "PUT");
        const response: Response = await fetch(url, reqConfig);
        const responseOk = Math.floor(response.status / 100) === 2;
        const body: Nullable<RESPONSE> = await response.json();
        return ([responseOk, body] as (boolean | Nullable<RESPONSE>)[]);
    } catch (error) {
        Log.error(error);
        return [false, error];
    }
};

export const httpDelete = async (url: string): Promise<(boolean)[]> => {
    try {
        const response: Response = await fetch(url, {
            method: 'DELETE',
            mode: 'cors',
            headers: addContentType(ContentType.JSON_UTF8),
        });
        const responseOk = Math.floor(response.status / 100) === 2;
        return ([responseOk] as boolean[]);
    } catch (error) {
        Log.error(error);
        return [false, error];
    }
};


export const serializeDateAccordingToContentType = <HTTPMETHOD extends Nullable<'POST'> | Nullable<'PUT'>,
    BODY extends NonNullable<string> | NonNullable<object>>(postReqType: MediaType, bodyArg: BODY, httpMethod?: HTTPMETHOD) => {
    let reqConfig: any;

    if (postReqType === MediaType.FORM) {
        reqConfig = createReq(MediaType.FORM, bodyArg, httpMethod);
    } else if (postReqType === MediaType.JSON) {
        reqConfig = createReq(MediaType.JSON, bodyArg, httpMethod);
    }
    return reqConfig;
}

