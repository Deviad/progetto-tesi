

import { setLocale } from 'yup';

setLocale({
    mixed: {
        default: 'Nu este valid',
    },
    string: {
        min: '${path} trebuie sa aiba mai mult de ${min} caractere',
        max: '${path} trebuie sa aiba mai putin de ${max} caractere'
    },
    number: {
        min: '${path} trebuie sa aiba mai mult de ${min} caractere',
        max: '${path} trebuie sa aiba mai putin de ${max} caractere'
    },
});
