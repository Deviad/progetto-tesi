import React from 'react';
import {render} from '@testing-library/react';
import App from '../App';
import {Provider} from "react-redux";
import { MemoryRouter } from 'react-router-dom';
import { createStore } from '@reduxjs/toolkit';
import rootReducer from "../app/rootReducer";




describe('<App />', () => {
    test('renders app properly', () => {
        const store = createStore(rootReducer);

        const wrapper = render(
            <Provider store={store}>
                <MemoryRouter initialEntries={['/']}>
                    <App/>
                </MemoryRouter>
            </Provider>,
        );
        expect(wrapper.container.querySelector(".ant-layout")).toBeInTheDocument();
        wrapper.unmount();
    });
});
