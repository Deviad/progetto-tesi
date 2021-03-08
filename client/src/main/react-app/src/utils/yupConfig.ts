import {setLocale} from 'yup';

setLocale({
    mixed: {
        default: 'Nu este valid',
    },
    string: {
        // eslint-disable-next-line no-template-curly-in-string
        min: '${path} trebuie sa aiba mai mult de ${min} caractere',
        // eslint-disable-next-line no-template-curly-in-string
        max: '${path} trebuie sa aiba mai putin de ${max} caractere'
    },
    number: {
        // eslint-disable-next-line no-template-curly-in-string
        min: '${path} trebuie sa aiba mai mult de ${min} caractere',
        // eslint-disable-next-line no-template-curly-in-string
        max: '${path} trebuie sa aiba mai putin de ${max} caractere'
    },
});
