import * as Cookies from "js-cookie";
import {AccessToken, AuthorizationResponse, ErrorsMap, FormError, Nullable, PageSlug, UserState} from "../types";
import jwtDecode from "jwt-decode";
import {SchemaOf, string, ValidationError} from "yup";
import {cloneDeep, omit, trim} from "lodash";
import {SecondStepSchema} from "../features/coursemanagement/steps/second/secondStepCallbacks";
import React from "react";
import {message} from "antd";

const utils = (function () {

    const toFormUrlEncoded = <BODY extends NonNullable<object>>(object: BODY) => {
        return Object.entries(object)
            .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
            .join('&');
    }

    const getParameterByName = (name: string, url?: string): Nullable<string> => {
        if (!url) {
            url = window.location.href;
        }

        /*
            "[" "]" reprezinta simbolele proprie al limbajului Javascript,
            de aceea trebuie sa le escape-uim, astfel incat sa fie interpretate
            doar ca un sir de caractere.
         */
        name = name.replace(/[\[\]]/g, '\\$&');

        const parser = document.createElement('a');
        parser.href = url;

        /*
            Aici folosesc un espedient fiindca .search
            permite direct de a extrage bucata de query string din un url.
         */

        const query = parser.search.substring(1);
        const vars = query.split('&');
        for (let i = 0; i < vars.length; i++) {
            const pair = vars[i].split('=');
            if (pair[0] === name) {
                return decodeURIComponent(pair[1]);
            }
        }
        return null;
    };

    const cookieStorage = (function cookieStorage() {

        const options = {
            secure: true
        };

        const setItem = (key: string, value: any) => Cookies.set(key, value, options);

        const getItem = (key: string) => Cookies.get(key);

        const removeItem = (key: string) => Cookies.remove(key, options);

        const key = (index: number) => {
            let allKeys = Object.keys(Cookies.getJSON());
            return index > -1 && index <= allKeys.length
                ? allKeys[index]
                : "";
        };

        return {
            setItem,
            getItem,
            removeItem,
            key
        }
    })();

    const storageAvailable = <STORAGETYPE extends 'localStorage' | 'sessionStorage'>(type: STORAGETYPE) => {

        let storage: Nullable<Storage> = null;

        try {
            const storage: Storage = window[type] as Storage;
            const x = "__storage_test__";
            storage.setItem(x, x);
            storage.removeItem(x);
            return true;
        } catch (e) {
            return e instanceof DOMException && (
                    // everything except Firefox
                e.code === 22 ||
                // Firefox
                e.code === 1014 ||
                // test name field too, because code might not be present
                // everything except Firefox
                e.name === "QuotaExceededError" ||
                // Firefox
                e.name === "NS_ERROR_DOM_QUOTA_REACHED") &&
                // acknowledge QuotaExceededError only if there's something already stored
                (storage as Nullable<Record<any, any>>)?.length !== 0;
        }
    };

    const storageFactory = (function storageFactory() {
        const getStorage = () => storageAvailable("sessionStorage")
            ? sessionStorage
            : storageAvailable("localStorage")
                ? localStorage
                : cookieStorage;
        return {
            getStorage
        }
    })();

    const endpointFactory = () => ({
        get resource() {
            if (process.env.NODE_ENV === 'production') {
                return `http://localhost:5051/react/${PageSlug.USER_PROFILE}`
            } else {
                return `http://localhost:3000/${PageSlug.USER_PROFILE}`
            }
        },

        get tokenRedirect() {
            if (process.env.NODE_ENV === 'production') {
                return 'http://localhost:5051/react/oauth/token'
            } else {
                return 'http://localhost:3000/oauth/token'
            }
        }
    })

    const getUserStateFromAuthResponse = (auth: AuthorizationResponse) => {
        const access: AccessToken = jwtDecode(auth.access_token);
        const result: UserState = {
            email: access.email,
            expirationTime: auth.expires_in,
            expiresAt: access.exp,
            issuedAt: access.iat,
            refreshExpirationTime: auth.refresh_expires_in,
            refreshToken: auth.refresh_token,
            accessToken: auth.access_token,
            username: access.preferred_username,
            firstName: access.given_name,
            lastName: access.family_name,
            roles: access?.resource_access["ripeti-web"]?.roles
        }
        return result;
    }

    const decodeAuthResp = (serAuthResp: string): AuthorizationResponse => {
        return JSON.parse(serAuthResp) as AuthorizationResponse;
    }

    const decodeAccessToken = (serAuthResp: string): AccessToken => {
        const deserAuthResp = decodeAuthResp(serAuthResp);

        return jwtDecode(deserAuthResp.access_token);
    }

    const isValidStoredToken = () => {
        const serAuthResp = utils.storage.getItem("auth_res");

        if (utils.isNotTrue(serAuthResp)) {
            return false
        } else {
            const accessToken = decodeAccessToken(serAuthResp as string);

            // Date.now() intorce timpul curent in millisecunde, de aceea
            // trebuie sa facem o conversie din ms la s.
            return Math.floor(Date.now() / 1000) < accessToken.exp;

        }
    }

    const isTrue = (obj: any): boolean => {
        return !(obj === null ||
            obj === undefined ||
            obj === "" ||
            (obj.constructor === Object && Object.keys(obj).length === 0) ||
            (Object.prototype.toString.call(obj) === "[object Array]" && obj.length === 0));
    }

    const isNotTrue = (obj: any) => !isTrue(obj)


    /*
        cuvant-ul "is"  este folosit pentru a defini un type guard
        si ii permite lui Typescript sa stie sa faca type inference in maniera correcta
        cand ne gasim in o selectie (if=then-else).
    */

    const isString = (value: any): value is string => {
        return typeof value === 'string';
    }


    /*
       cuvant-ul "is"  este folosit pentru a defini un type guard
       si ii permite lui Typescript sa stie sa faca type inference in maniera correcta
       cand ne gasim in o selectie (if=then-else).
   */


    const hasText = (value: any): value is string => {
        return isString(value) && trim(value) !== '';
    }

    const stripHtmlTags = (text: string): string => {

        //^ in pozitia curenta in interiorul lui regex insemna toate caractere posibile in afara de,
        // deci in afara de > fiind ca este folosit ca token de inchidere unui tag HTML.
        // /g in replace insemna sa inlocuiesca toate aparitile, nu doar prima.

        return text.replace(/(<([^>]+)>)/gi, "");
    }

    const validateBySchema =
        <T extends unknown>(values: T, schema: SchemaOf<T>, validationPath?: string, context?: object) => {
            const errors: ErrorsMap = {};

            try {
                if (hasText(validationPath)) {
                    schema.validateSyncAt(validationPath, values, {
                        abortEarly: false,
                        strict: true,
                        context,
                    });
                } else {
                    schema.validateSync(values, {
                        abortEarly: false,
                        strict: true,
                        context,
                    });
                }
            } catch (e) {
                //o eroare care nu este aruncata din yup ar trebui sa fie aruncata pentru a fi managiuita din un handler
                // diferit
                if (Object.prototype.toString.call(e.inner) !== "[object Array]") {
                    throw e;
                }

                e.inner.forEach(({path, message}: ValidationError) => {
                    errors[path as string] = message;
                });
            }

            return errors;
        }

    const stripHtmlFromAttributes = (obj: Record<string, any>) => {

        const acc = {} as Record<string, any>;

        for (const [k, v] of Object.entries(obj)) {

            if( typeof v === "string") {
                acc[k] = utils.stripHtmlTags(v);
            }
            else {
                acc[k] = v;
            }
        }
        return acc;
    }

    type ValidateFormInputType<T> =  { objectToValidate: { [key: string]: any, errors?: FormError }, schema: any, value: T, path: string }

    function validateFormInput<T extends unknown>({objectToValidate, schema, value, path}: ValidateFormInputType<T>) {
        const copy = cloneDeep(objectToValidate);

        if ((value as React.ChangeEvent<HTMLInputElement>).target && typeof (value as React.ChangeEvent<HTMLInputElement>).target.value !== undefined) {
            copy[path] = utils.stripHtmlTags( (value as React.ChangeEvent<HTMLInputElement>).target.value)
        } else {
            copy[path] = utils.stripHtmlTags( (value as string));
        }

        let errorsMap;

        try {
            errorsMap = utils.validateBySchema(copy, schema, path);
        } catch (error) {
            console.log(error);
            message.error(error.message);
            return;
        }
        if (Object.keys(errorsMap).length === 0) {

            if (objectToValidate.errors && objectToValidate.errors[path]) {

                //Daca acum user-ul a introdus un sir fara erori atunci scoatem eroarile din stare lui step2.

                objectToValidate.errors = omit(objectToValidate.errors, path);
            }

        } else {

            if (!objectToValidate.errors) {
                objectToValidate.errors = {};
            }

            objectToValidate.errors = {...objectToValidate.errors, [path]: errorsMap[path]};
        }
    }

    const validateFormBlock = (objectToValidate: { [key: string]: any, errors?: FormError }, schema: any) => {
        const copy = cloneDeep(objectToValidate);

        return utils.validateBySchema(stripHtmlFromAttributes(copy), schema);
    }



    return {
        toFormUrlEncoded,
        getParameterByName,
        endpointFactory,
        isValidStoredToken,
        decodeAuthResp,
        decodeAccessToken,
        getUserStateFromAuthResponse,
        isTrue,
        hasText,
        isString,
        validateBySchema,
        isNotTrue,
        stripHtmlTags,
        validateFormBlock,
        validateFormInput,
        get storage() {
            return storageFactory.getStorage();
        }
    }

})();

export {utils};

