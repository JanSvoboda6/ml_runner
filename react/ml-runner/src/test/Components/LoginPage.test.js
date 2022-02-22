import React from 'react';
import * as redux from "react-redux";
import {fireEvent, render, screen, waitFor} from "@testing-library/react";
import LoginPage from "../../components/pages/LoginPage";
import {Router} from "react-router";
import {createMemoryHistory} from 'history';
import LoginService from "../../services/LoginService";
import Login from "../../components/pages/LoginPage";
import userEvent from "@testing-library/user-event";
import {act} from 'react-dom/test-utils';

describe('Rendering', () => {
    test('When form is rendered then submit button is enabled', () => {
        jest.spyOn(redux, 'useDispatch').mockReturnValue(jest.fn());
        render(<Router history={createMemoryHistory()}><LoginPage/></Router>);
        const loginButton = screen.getByText(/login/i);
        expect(loginButton).toBeEnabled();
    });

    test('When there is a login popup parameter then pop window with successful registration is displayed', () => {
        jest.spyOn(redux, 'useDispatch').mockReturnValue(jest.fn());
        const url = "http://random-url.com/?popup=t";
        Object.defineProperty(window, "location", {
            value: new URL(url)
        });

        render(<Router history={createMemoryHistory()}><LoginPage/></Router>);
        const popupMessage = screen.getByText("Thanks for registration. Now you can login!");
        expect(popupMessage).toBeInTheDocument();
    })
});

describe('Login', () => {
    test('When form is successfully submitted then user is redirected to the main page', async () => {
        jest.spyOn(redux, 'useDispatch').mockReturnValue(jest.fn());
        const history = createMemoryHistory();
        const user = {
            username: 'user@domain.com',
            password: 'password',
            accessToken: 'a random token'
        }
        jest.spyOn(LoginService, 'login').mockResolvedValue({user: user});
        history.replace = jest.fn();

        await act(async () => {
            render(<Router history={history}><LoginPage/></Router>);
        })

        const email = screen.getByPlaceholderText(/email/i);
        const password = screen.getByPlaceholderText(/password/i);
        const loginButton = screen.getByText("Login");

        await act(async () => {
            fireEvent.change(email, {target: {value: user.username}});
            fireEvent.change(password, {target: {value: user.password}});
            userEvent.click(loginButton);
        });
        expect(history.replace).toHaveBeenCalledWith(expect.objectContaining({"pathname": "/preparing"}));
    });

    test('When form is not successfully submitted then user is not redirected to the main page', async () => {
        jest.spyOn(redux, 'useDispatch').mockReturnValue(jest.fn());
        const history = createMemoryHistory();

        let message = 'A problem occurred';
        const error = {response: {data: {message: message}}}

        jest.spyOn(LoginService, 'login').mockRejectedValue(error);
        history.replace = jest.fn();

        await act(async () => {
            render(<Router history={history}><LoginPage/></Router>);
        });

        const email = screen.getByPlaceholderText(/email/i);
        const password = screen.getByPlaceholderText(/password/i);
        const loginButton = screen.getByText("Login");

        await act(async () => {
            fireEvent.change(email, {target: {value: 'user@user.com'}});
            fireEvent.change(password, {target: {value: 'password'}});
            fireEvent.click(loginButton);
        });

        const errorMessage = screen.getByText(message);
        expect(errorMessage).toBeInTheDocument();
    });
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