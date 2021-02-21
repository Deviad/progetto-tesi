import {HttpGetRequestParams, HttpPostReqParams, MediaType, Nullable, Request} from "./types";
import {Log, utils} from "./utils";

export enum ContentType {
    JSON_UTF8 = "application/json;charset=UTF-8",
    URL_ENCODED = "application/x-www-form-urlencoded",
}


export const addContentType = (contentType: ContentType, obj: Record<string, string> = {}): Record<string, any> => {
    if (contentType === ContentType.JSON_UTF8) {
        obj['Content-Type'] = ContentType.JSON_UTF8;
    } else if (contentType === ContentType.URL_ENCODED) {
        obj['Content-Type'] = ContentType.URL_ENCODED;
    }
    return obj;
};

export const createReq = <T extends Nullable<'POST'> | Nullable<'PUT'>, FDATA extends NonNullable<string> | NonNullable<object>>
(postReqType: MediaType, bodyArg: FDATA, headers: Record<string, string> = {}, httpMethod?: T) => {
    const config: Record<keyof typeof MediaType, () => Request> = {
        JSON: () => ({
            method: httpMethod ?? "POST",
            mode: 'cors',
            headers: addContentType(ContentType.JSON_UTF8, headers),
            body: JSON.stringify(bodyArg),
        }),
        FORM: () => ({
            method: httpMethod ?? "POST",
            mode: 'cors',
            headers: addContentType(ContentType.JSON_UTF8, headers),
            body: utils.toFormUrlEncoded(bodyArg as NonNullable<object>),
        }),
    };
    return config[(postReqType)]();
};


export const httpGet = async <RESPONSE>(params: HttpGetRequestParams): Promise<{ body: RESPONSE | undefined | null; status: boolean, error: undefined | any }> => {

    const {url, headers = {}} = params;

    try {
        const response: Response = await fetch(url, {
            method: 'GET',
            mode: 'cors',
            headers: addContentType(ContentType.JSON_UTF8, headers),
        });
        const responseOk = Math.floor(response.status / 100) === 2;
        const body: Nullable<RESPONSE> = await response.json();
        return {
            status: responseOk,
            body,
            error: undefined
        };
    } catch (error) {
        Log.error(error);
        return {
            body: undefined,
            status: false,
            error
        };
    }
};


export const httpPost = async <RESPONSE>(params: HttpPostReqParams): Promise<{ body: RESPONSE | undefined | null; error: undefined; status: boolean }> => {

    const {url, bodyArg, postReqType, headers = {}} = params;
    try {
        let reqConfig = serializeDateAccordingToContentType<"POST", Record<string, any>>(postReqType, bodyArg, "POST", headers);
        const response: Response = await fetch(url, reqConfig);
        const responseOk = Math.floor(response.status / 100) === 2;
        const body: Nullable<RESPONSE> = await response.json();
        return {
            status: responseOk,
            body,
            error: undefined
        };
    } catch (error) {
        Log.error(error);
        return {
            body: undefined,
            status: false,
            error
        };
    }
};

export const httpPut = async <RESPONSE>(params: HttpPostReqParams): Promise<{ body: RESPONSE | undefined | null; error: undefined; status: boolean }> => {

    const {url, bodyArg, postReqType, headers = {}} = params;
    try {
        let reqConfig = serializeDateAccordingToContentType<"PUT", Record<string, any>>(postReqType, bodyArg, "PUT", headers);
        const response: Response = await fetch(url, reqConfig);
        const responseOk = Math.floor(response.status / 100) === 2;
        const body: Nullable<RESPONSE> = await response.json();
        return {
            status: responseOk,
            body,
            error: undefined
        };
    } catch (error) {
        Log.error(error);
        return {
            body: undefined,
            status: false,
            error
        };
    }
};

export const httpDelete = async (url: string): Promise<{ error: undefined; status: boolean }> => {
    try {
        const response: Response = await fetch(url, {
            method: 'DELETE',
            mode: 'cors',
            headers: addContentType(ContentType.JSON_UTF8),
        });
        const responseOk = Math.floor(response.status / 100) === 2;
        return {
            status: responseOk,
            error: undefined
        };
    } catch (error) {
        Log.error(error);
        return {
            status: false,
            error
        };
    }
};


export const serializeDateAccordingToContentType = <HTTPMETHOD extends Nullable<'POST'> | Nullable<'PUT'>,
    BODY extends NonNullable<string> | NonNullable<object>>(postReqType: MediaType, bodyArg: BODY, httpMethod?: HTTPMETHOD, headers: Record<string, string> = {}) => {
    let reqConfig: any;

    if (postReqType === MediaType.FORM) {
        reqConfig = createReq(MediaType.FORM, bodyArg, headers, httpMethod);
    } else if (postReqType === MediaType.JSON) {
        reqConfig = createReq(MediaType.JSON, bodyArg, headers, httpMethod);
    }
    return reqConfig;
}

