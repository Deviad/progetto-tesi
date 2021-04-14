import React from "react";
import {Login} from "../login";
import {render} from "@testing-library/react";
import {createMemoryHistory} from "history";
import {Router} from "react-router-dom";

const mockPush = jest.fn();
describe('<Login />', () => {

    test('redirects to component', () => {
        const history = createMemoryHistory()
        const pushSpy = jest.spyOn(history, 'push')
        render(
            // @ts-ignore
            <Router history={history}>
                <Login/>
            </Router>,
        );
        expect(pushSpy).toHaveBeenCalled()
    });

    test('renders text before redirection', async () => {
        const history = createMemoryHistory();
        history.push = (...args: any) => mockPush(args);
        jest.spyOn(history, 'push')
        const wrapper = render(
            // @ts-ignore
            <Router history={history}>
                <Login/>
            </Router>,
        );
        expect(history.push).toHaveBeenCalled();
        const result = await wrapper.findByText("This is a showcase of OAuth2 integration in React");
        expect(result).toBeInTheDocument();
    });
});
