import React from 'react';
import * as redux from "react-redux";
import {render, screen} from "@testing-library/react";
import LoginPage from "../../components/pages/LoginPage";
import {Router} from "react-router";
import {createMemoryHistory} from 'history'

// const mockDispatch = jest.fn();
// jest.mock('react-redux', () => ({
//     useSelector: jest.fn(),
//     useDispatch: () => mockDispatch
// }));

const historyMock = {
    push: jest.fn(),
    listen: jest.fn()
};

describe('Rendering', () => {
    it('When form is rendered then submit button is enabled', () => {
        const useDispatchSpy = jest.spyOn(redux, 'useDispatch');
        const mockDispatchFn = jest.fn();
        useDispatchSpy.mockReturnValue(mockDispatchFn);

        render(<Router history={createMemoryHistory()}><LoginPage/></Router>);
        const loginButton = screen.getByText(/login/i);
        expect(loginButton).toBeEnabled();
    });
    it.todo('When there is a login popup parameter then pop window with successful registration is displayed')
});

describe('Logining in', () => {
    it.todo('When form is successfully submitted then user is redirected to the main page');
    it.todo('When form is not successfully submitted then user is not redirected to the main page');
    it.todo('When form is successfully submitted then user information are stored in the local storage');
    it.todo('When form is not successfully submitted then user information are not stored in the local storage');
    it.todo('When form is submitted then POST request is sent');
    it.todo('When form is submitted and there is an exception on server side then error message is displayed');
    it.todo('When form has been submitted then submit button becomes disabled');
    it.todo('When form has been submitted and there is an error then submit button becomes enabled');
});

describe('Validation', () => {
    it.todo('When form is submitted then all fields must be filled');
    it.todo('When fields are not properly filled then submit button is disabled');
    it.todo('When fields are properly filled then submit button is enabled');
    it.todo('When email is filled then email has correct format');
    it.todo('When email is filled then email must be shorter or equal to maximum email length');
    it.todo('When password is filled then password must be shorter or equal to maximum password length');
    it.todo('When email is not in a correct format then the warning message is displayed');
    it.todo('When password is not in a correct length then the warning message is displayed');
});