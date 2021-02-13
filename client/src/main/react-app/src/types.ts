export type Nullable<T> = T | undefined | null;

export enum MediaType {
    JSON = 'JSON',
    FORM = 'FORM',
}

export type RequestType = 'POST' | 'GET' | 'DELETE' | 'PUT';

export type Request = {
    method: RequestType;
    mode?: 'cors';
    headers?: Record<string, any>;
    body?: string | object;
}

export type HttpPostReqParams = {
    url: string;
    bodyArg: Record<any, any>;
    postReqType: MediaType;
}
