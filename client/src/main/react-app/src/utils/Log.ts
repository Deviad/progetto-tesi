export class Log {
    static trace(...args: any[]) {
        // eslint-disable-next-line no-console
        console.trace(...args);
    }

    static info(...args: any[]) {
        // eslint-disable-next-line no-console
        console.info(...args);
    }

    static warn(...args: any[]) {
        // eslint-disable-next-line no-console
        console.warn(...args);
    }

    static error(...args: any[]) {
        // eslint-disable-next-line no-console
        console.error(...args);
    }
}
