import React from 'react';
import * as redux from "react-redux";
import {act, fireEvent, render, screen, waitFor} from "@testing-library/react";
import LoginPage from "../../components/pages/LoginPage";
import {Router} from "react-router";
import {createMemoryHistory} from 'history';
import LoginService from "../../services/LoginService";
import {User} from "../../types";
import Login from "../../components/pages/LoginPage";
import exp from "constants";
import userEvent from "@testing-library/user-event";
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
    test('When form is rendered then submit button is enabled', () => {
        const useDispatchSpy = jest.spyOn(redux, 'useDispatch');
        const mockDispatchFn = jest.fn();
        useDispatchSpy.mockReturnValue(mockDispatchFn);

        render(<Router history={createMemoryHistory()}><LoginPage/></Router>);
        const loginButton = screen.getByText(/login/i);
        expect(loginButton).toBeEnabled();
    });
    it.todo('When there is a login popup parameter then pop window with successful registration is displayed')
});

describe('Login', () => {
    test('When form is successfully submitted then user is redirected to the main page', async () => {
        const useDispatchSpy = jest.spyOn(redux, 'useDispatch');
        const mockDispatchFn = jest.fn();
        useDispatchSpy.mockReturnValue(mockDispatchFn);

        const history = createMemoryHistory();

        const user = {
            username: 'jan@jan.com',
            password: 'jan',
            accessToken: 'a random token'
        }

        jest.spyOn(LoginService, 'login').mockResolvedValue({user: user});

        history.replace = jest.fn();

        render(<Router history={history}><LoginPage/></Router>);

        const email = screen.getByPlaceholderText(/email/i);
        const password = screen.getByPlaceholderText(/password/i);
        const loginButton = screen.getByText(/login/i);

        fireEvent.change(email, {target: {value: 'jan@jan.com'}});
        fireEvent.change(password, {target: {value: 'jan'}});
        userEvent.click(loginButton);

        await waitFor(() => expect(loginButton).not.toBeInTheDocument());
        expect(history.replace).toHaveBeenCalledWith(expect.objectContaining({"pathname": "/preparing"}));
    });
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