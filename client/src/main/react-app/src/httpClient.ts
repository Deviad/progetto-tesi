import {HttpGetRequestParams, HttpPostReqParams, MediaType, Nullable, Request} from "./types";
import {Log, utils} from "./utils";
import {notification} from "antd";

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

export const createReq = <T extends Nullable<'POST'> | Nullable<'PUT'> |  Nullable<'DELETE'>, FDATA extends NonNullable<string> | NonNullable<object>>
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
            headers: addContentType(ContentType.URL_ENCODED, headers),
            body: utils.toFormUrlEncoded(bodyArg as NonNullable<object>),
        }),
    };
    return config[(postReqType)]();
};


export const httpGet = async <RESPONSE>(params: HttpGetRequestParams): Promise<{ body: RESPONSE | undefined | null; status: boolean }> => {

    const {url, headers = {}} = params;

    try {
        const response: Response = await fetch(url, {
            method: 'GET',
            mode: 'cors',
            headers: addContentType(ContentType.JSON_UTF8, headers),
        });
        const responseOk = Math.floor(response.status / 100) === 2;

        let body: Nullable<RESPONSE> = null;
        try {
            body = await response.json();
        } catch (error) {
            console.log(error);
            return {
                status: responseOk,
                body
            }
        }
        return {
            status: responseOk,
            body,
        };
    } catch (error) {
        Log.error(error);
        notification["error"]({
            message: 'Http client error',
            description: error.message,
        });
        throw error;
    }
};


export function showHttpServerError<RESPONSE>(responseOk: boolean, body: RESPONSE | undefined | null) {
    if (!responseOk) {
        notification["error"]({
            message: 'Http server error',
            description: (body as any).message,
        });
    }
}

export const httpPost = async <RESPONSE>(params: HttpPostReqParams): Promise<{ body: RESPONSE | undefined | null; status: boolean }> => {

    const {url, bodyArg={}, postReqType, headers = {}} = params;
    try {
        let reqConfig = serializeDateAccordingToContentType<"POST", Record<string, any>>(postReqType, bodyArg, "POST", headers);
        const response: Response = await fetch(url, reqConfig);
        const responseOk = Math.floor(response.status / 100) === 2;
        let body: Nullable<RESPONSE> = null;
        try {
            body = await response.json();
        } catch (error) {
            console.log(error);
            return {
                status: responseOk,
                body
            }
        }
        showHttpServerError(responseOk, body);
        return {
            status: responseOk,
            body
        };
    } catch (error) {
        Log.error(error);
        notification["error"]({
            message: 'Http client error',
            description: error.message,
        });
        throw error;

    }
};

export const httpPut = async <RESPONSE>(params: HttpPostReqParams): Promise<{ body: RESPONSE | undefined | null; status: boolean }> => {

    const {url, bodyArg={}, postReqType, headers = {}} = params;
    try {
        let reqConfig = serializeDateAccordingToContentType<"PUT", Record<string, any>>(postReqType, bodyArg, "PUT", headers);
        const response: Response = await fetch(url, reqConfig);
        const responseOk = Math.floor(response.status / 100) === 2;
        let body: Nullable<RESPONSE> = null;
        try {
            body = await response.json();
        } catch (error) {
            console.log(error);
            return {
                status: responseOk,
                body
            }
        }
        return {
            status: responseOk,
            body,
        };
    } catch (error) {
        Log.error(error);
        notification["error"]({
            message: 'Http client error',
            description: error.message,
        });
        throw error;
    }
};

// export const httpDelete = async (url: string, toDelete: any): Promise<{ status: boolean }> => {
//
//     try {
//
//         let reqConfig = serializeDateAccordingToContentType<"DELETE", Record<string, any>>(MediaType.FORM, toDelete, "DELETE", addContentType(ContentType.URL_ENCODED));
//
//         const response: Response = await fetch(url, reqConfig);
//         const responseOk = Math.floor(response.status / 100) === 2;
//         return {
//             status: responseOk,
//         };
//     } catch (error) {
//         Log.error(error);
//         notification["error"]({
//             message: 'Http client error',
//             description: error.message,
//         });
//         throw error;
//     }
// };


export const httpDeleteAll = async <RESPONSE>(params: HttpPostReqParams): Promise<boolean> => {

    const {url, bodyArg={}, postReqType, headers = {}} = params;
    try {
        let reqConfig = serializeDateAccordingToContentType<"DELETE", Record<string, any>>(postReqType, bodyArg, "DELETE", headers);
        const response: Response = await fetch(url, reqConfig);
        return Math.floor(response.status / 100) === 2;
    } catch (error) {
        Log.error(error);
        notification["error"]({
            message: 'Http client error',
            description: error.message,
        });
        throw error;
    }
};


export const serializeDateAccordingToContentType = <HTTPMETHOD extends Nullable<'POST'> | Nullable<'PUT'> | Nullable<'DELETE'>,
    BODY extends NonNullable<string> | NonNullable<object>>(postReqType: MediaType, bodyArg: BODY, httpMethod?: HTTPMETHOD, headers: Record<string, string> = {}) => {
    let reqConfig: any;

    if (postReqType === MediaType.FORM) {
        reqConfig = createReq(MediaType.FORM, bodyArg, headers, httpMethod);
    } else if (postReqType === MediaType.JSON) {
        reqConfig = createReq(MediaType.JSON, bodyArg, headers, httpMethod);
    }
    return reqConfig;
}

