import {HttpPostReqParams, MediaType, Nullable, Request} from "./types";
import {Log} from "./Log";

export enum ContentType {
    JSON_UTF8 = "application/json;charset=UTF-8",
}


export const addContentType = (contentType: ContentType, obj: Record<string, ContentType> = {}): Record<string, any> => {
    if (contentType === ContentType.JSON_UTF8) {
        obj['Content-Type'] = ContentType.JSON_UTF8;
    }
    return obj;
};

export const createReq = (postReqType: MediaType, bodyArg: Record<any, any>) => {
    const config: Record<keyof typeof MediaType, () => Request> = {
        JSON: () => ({
            method: 'POST',
            mode: 'cors',
            headers: addContentType(ContentType.JSON_UTF8),
            body: JSON.stringify(bodyArg),
        }),
        FORM: () => ({
            method: 'POST',
            body: bodyArg,
        }),
    };
    return config[(postReqType)]();
};


export const httpGet = async <T> (url: string): Promise<(boolean | Nullable<T>)[]> => {
    try {
        const response: Response = await fetch(url, {
            method: 'GET',
            mode: 'cors',
            headers: addContentType(ContentType.JSON_UTF8),
        });
        const responseOk = Math.floor(response.status / 100) === 2;
        const body: T = await response.json();
        return ([responseOk, body] as (boolean | Nullable<T>)[]);
    } catch (error) {
        Log.error(error);
        return [false, error];
    }
};

export const httpPost = async <T>  (params: HttpPostReqParams): Promise<(boolean | Nullable<T>)[]> => {

    const {url, bodyArg, postReqType} = params;
    try {
        let reqConfig: any;

        if (postReqType === MediaType.FORM) {
            reqConfig = createReq(MediaType.FORM, bodyArg);
        } else if (postReqType === MediaType.JSON) {
            reqConfig = createReq(MediaType.JSON, bodyArg);
        }
        const response: Response = await fetch(url, reqConfig);
        const responseOk = Math.floor(response.status / 100) === 2;
        const body: T = await response.json();
        return ([responseOk, body] as (boolean | Nullable<T>)[]);
    } catch (error) {
        Log.error(error);
        return [false, error];
    }
};

export const httpPut = async <T> (url: string, bodyArg: Record<any, any>): Promise<(boolean | Nullable<T>)[]> => {
    try {
        const response: Response = await fetch(url, {
            method: 'PUT',
            mode: 'cors',
            headers: addContentType(ContentType.JSON_UTF8),
            body: JSON.stringify(bodyArg),
        });
        const responseOk = Math.floor(response.status / 100) === 2;
        const body: T = await response.json();
        return ([responseOk, body] as (boolean | Nullable<T>)[]);
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
        return ([responseOk] as (boolean)[]);
    } catch (error) {
        Log.error(error);
        return [false, error];
    }
};
