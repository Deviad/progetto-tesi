export const BASE_URL = "http://localhost:5050/api";

export const USER_ENDPOINT = "/user"
export const COURSE_ENDPOINT = "/course"

export const emailPattern = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
export const passwordPattern = "^(?=.*[a-z])(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*()_+{}:\";,.<>?|=_])[a-zA-Z0-9!@#$%^&*()_+{}:\";,.<>?|=-_]{8,20}$"


export const registrationSchema = {
    "type": "object",
    "required": [
        "firstName",
        "lastName",
        "email",
        "username",
        "role",
        "password"
    ],
    "properties": {
        "firstName": {
            "type": "string",
            "title": "Prenume",
            "minLength": 3,
            "pattern": "^[a-zA-Z ]+$"
        },
        "lastName": {
            "type": "string",
            "title": "Nume",
            "minLength": 3,
            "pattern": "^[a-zA-Z ]+$"
        },
        "username": {
            "type": "string",
            "title": "Numele utilizatorlui",
            "minLength": 3,
            "pattern": "^[a-z]+$"
        },
        "email": {
            "type": "string",
            "title": "Email",
            "pattern": emailPattern,
        },
        "password": {
            "type": "string",
            "title": "Parola",
            "pattern": passwordPattern,
        },
        "role": {
            "title": "Sunt un",
            "type": "string",
            "enum": [
                "STUDENT",
                "PROFESSOR",
            ],
            "enumNames": [
                "Student",
                "Profesor",
            ]
        },
        "address": {
            "required": [
                "firstAddressLine",
                "secondAddressLine",
                "city",
                "country"

            ],
            "type": "object",
            "title": "Adresa",
            "properties": {
                "firstAddressLine": {
                    "type": "string",
                    "title": "Strada si numar",
                    "minLength": 3,
                    "maxLength": 100,
                },
                "secondAddressLine": {
                    "type": "string",
                    "title": "Bloc, Scara, etc.",
                    "minLength": 3,
                    "maxLength": 100,
                },
                "city": {
                    "type": "string",
                    "title": "Oras",
                    "minLength": 3,
                    "maxLength": 20,
                    "pattern": "^[a-zA-Z ]+$"
                },
                "country": {
                    "type": "string",
                    "title": "Tara",
                    "enum": [
                        "ROMANIA"
                    ]
                }
            }
        }
    }
};
export const registrationUiSchema = {
    "firstName": {
        "ui:emptyValue": "",
        "ui:autocomplete": "given-name"
    },
    "lastName": {
        "ui:emptyValue": "",
        "ui:autocomplete": "family-name"
    },
    "password": {
        "ui:widget": "password",
        // "ui:help": `parola trebuia sa aiba o lungime de cel
        //     putin 8 caractere, sa aiba cel putin o litera mare, 1 numar si
        //     1 caracter special`
    },
    "role": {
        "ui:help": "Alege daca esti student sau profesor"
    }
};
export const proprietati: Record<string, string> = {
    ".firstName": "Prenume",
    ".lastName": "Nume de familie",
    ".username": "Nume utilizator",
    ".email": "Email",
    ".password": "Parola secreta",
    ".role": "Rol",
    ".address.firstAddressLine": "Strada si numar",
    ".address.secondAddressLine": "Bloc, Scara, etc.",
    ".address.city": "Oras",
    ".address.country": "Tara"
}
