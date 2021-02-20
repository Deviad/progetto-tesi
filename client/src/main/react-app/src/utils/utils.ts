import * as Cookies from "js-cookie";
import {AccessToken, AuthorizationResponse, Nullable, UserState} from "../types";
import {isEmpty, isNil} from "lodash";
import jwtDecode from "jwt-decode";

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
                return 'http://localhost:5051/react/user-profile'
            } else {
                return 'http://localhost:3000/user-profile'
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

        if (isNil(serAuthResp) || isEmpty(serAuthResp)) {
            return false
        } else {
            const accessToken = decodeAccessToken(serAuthResp);

            // Date.now() intorce timpul curent in millisecunde, de aceea
            // trebuie sa facem o conversie din ms la s.
            return Math.floor(Date.now() / 1000) < accessToken.exp;

        }
    }

    return {
        toFormUrlEncoded,
        getParameterByName,
        endpointFactory,
        isValidStoredToken,
        decodeAuthResp,
        decodeAccessToken,
        getUserStateFromAuthResponse,
        get storage() {
            return storageFactory.getStorage();
        }
    }

})();

export {utils};

