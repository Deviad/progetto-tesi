import {render} from "@testing-library/react";
import React from "react";
import {Login} from "../login";
import {createMemoryHistory} from 'history'
import { Router } from "react-router-dom";

describe('<Login />', () => {
    it('renders properly', () => {
        const history = createMemoryHistory()
        const pushSpy = jest.spyOn(history, 'push')
        render(
        // @ts-ignore
            <Router history={history}>
                <Login />
            </Router>,
        );
        expect(pushSpy).toHaveBeenCalled()
    });
});
