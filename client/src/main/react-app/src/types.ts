export type Nullable<T> = T | undefined | null;

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

export type HttpPostReqParams = {
    url: string;
    bodyArg: Record<any, any>;
    postReqType: MediaType;
}

export type HttpPutReqParams = HttpPostReqParams;


export interface UserState extends User {
    isLoading: boolean
    error: Nullable<string>
}

export interface User {
    username: Nullable<string>,
    email: Nullable<string>,
}

export const userInitialState: UserState = {
    username: null,
    email: null,
    isLoading: false,
    error: null,

}
