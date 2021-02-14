import * as Cookies from "js-cookie";
import {Nullable} from "../types";

const utils = (function() {

    const toFormUrlEncoded = <BODY extends NonNullable<object>>(object: BODY) => {
        return Object.entries(object)
            .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
            .join('&');
    }

    const getParameterByName = (name: string, url?: string): Nullable<string> => {
        if (!url) url = window.location.href;
        name = name.replace(/[\[\]]/g, '\\$&');
        const regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
            results = regex.exec(url);
        if (!results) return null;
        if (!results[2]) return '';
        return decodeURIComponent(results[2].replace(/\+/g, ' '));
    }

    const cookieStorage = (function cookieStorage() {

        const options = {
            secure: true
        };

        const setItem = (key: string, value:any) => Cookies.set(key, value, options);

        const getItem = (key: string) => Cookies.get(key);

        const removeItem = (key:string) => Cookies.remove(key, options);

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
        }
        catch (e) {
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
            if(process.env.NODE_ENV === 'production') {
                return 'http://localhost:5051/react/user-profile'
            } else {
                return 'http://localhost:3000/user-profile'
            }
        },

        get tokenRedirect() {
            if(process.env.NODE_ENV === 'production') {
                return 'http://localhost:5051/react/oauth/token'
            } else {
                return 'http://localhost:3000/oauth/token'
            }
        }})


    return {
        toFormUrlEncoded,
        getParameterByName,
        endpointFactory,
        get storage() {
            return storageFactory.getStorage();
        }
    }

})();

export {utils};
