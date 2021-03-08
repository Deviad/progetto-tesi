import {setLocale} from 'yup';

setLocale({
    mixed: {
        default: 'Nu este valid',
    },
    string: {
        // eslint-disable-next-line no-template-curly-in-string
        min: '${path} trebuie sa aiba cel putin ${min} caractere',
        // eslint-disable-next-line no-template-curly-in-string
        max: '${path} trebuie sa aiba cel mult ${max} caractere'
    },
    number: {
        // eslint-disable-next-line no-template-curly-in-string
        min: '${path} trebuie sa aiba cel putin ${min} caractere',
        // eslint-disable-next-line no-template-curly-in-string
        max: '${path} trebuie sa aiba cel mult ${max} caractere'
    },
});
