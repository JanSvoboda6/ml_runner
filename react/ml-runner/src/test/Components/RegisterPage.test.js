import {createMemoryHistory} from "history";
import {act} from "react-dom/test-utils";
import {fireEvent, render, screen} from "@testing-library/react";
import {Router} from "react-router";
import React from "react";
import RegisterService from "../../services/RegisterService";
import RegisterPage from "../../components/pages/RegisterPage";

describe('Submitting', () => {
    test('When form is successfully submitted then user is redirected to the login page', async() => {
        const history = createMemoryHistory();

        const user = {
            username: 'user@domain.com',
            password: 'Thisisarandompassword_999'
        };

        jest.spyOn(RegisterService, 'register').mockResolvedValue("User successfully registered!");
        history.replace = jest.fn();

        await act(async () => {
            render(<Router history={history}><RegisterPage/></Router>);
        });

        const email = screen.getByPlaceholderText(/email/i);
        const password = screen.getByPlaceholderText(/password/i);
        const signUpButton = screen.getByText("Sign Up");

        await act(async () => {
            fireEvent.change(email, {target: {value: user.username}});
            fireEvent.change(password, {target: {value: user.password}});
        });

        await act(async () => {
            fireEvent.click(signUpButton);
        });

        expect(history.replace).toHaveBeenCalledWith(expect.objectContaining({"hash": "", "pathname": "/login", "search": "?popup=t", "state": undefined}));
    });

    test('When submitting the form fails then user is not redirected to the login page', async() => {
        const history = createMemoryHistory();

        const user = {
            username: 'user@domain.com',
            password: 'Thisisarandompassword_999'
        };

        jest.spyOn(RegisterService, 'register').mockRejectedValue("An error occurred!");
        history.replace = jest.fn();

        await act(async () => {
            render(<Router history={history}><RegisterPage/></Router>);
        });

        const email = screen.getByPlaceholderText(/email/i);
        const password = screen.getByPlaceholderText(/password/i);
        const signUpButton = screen.getByText("Sign Up");

        await act(async () => {
            fireEvent.change(email, {target: {value: user.username}});
            fireEvent.change(password, {target: {value: user.password}});
        });

        await act(async () => {
            fireEvent.click(signUpButton);
        });

        expect(history.replace).toHaveBeenCalledTimes(0);
    });

    test('When form is submitted and there is a problem on the server side then error message is displayed', async () => {
        const history = createMemoryHistory();

        const user = {
            username: 'user@domain.com',
            password: 'Thisisarandompassword_999'
        };

        let message = 'A problem occurred';
        const error = {response: {data: {message: message}}}

        jest.spyOn(RegisterService, 'register').mockRejectedValue(error);
        history.replace = jest.fn();

        await act(async () => {
            render(<Router history={history}><RegisterPage/></Router>);
        });

        const email = screen.getByPlaceholderText(/email/i);
        const password = screen.getByPlaceholderText(/password/i);
        const signUpButton = screen.getByText("Sign Up");

        await act(async () => {
            fireEvent.change(email, {target: {value: user.username}});
            fireEvent.change(password, {target: {value: user.password}});
        });

        await act(async () => {
            fireEvent.click(signUpButton);
        });

        const errorMessage = screen.getByText(message);
        expect(errorMessage).toBeInTheDocument();
    });

    test('When form has been submitted and there is an error then submit button becomes enabled', async() => {
        const history = createMemoryHistory();

        const user = {
            username: 'user@domain.com',
            password: 'Thisisarandompassword_999'
        };

        let message = 'A problem occurred';
        const error = {response: {data: {message: message}}}

        jest.spyOn(RegisterService, 'register').mockRejectedValue(error);
        history.replace = jest.fn();

        await act(async () => {
            render(<Router history={history}><RegisterPage/></Router>);
        });

        const email = screen.getByPlaceholderText(/email/i);
        const password = screen.getByPlaceholderText(/password/i);
        const signUpButton = screen.getByText("Sign Up");

        await act(async () => {
            fireEvent.change(email, {target: {value: user.username}});
            fireEvent.change(password, {target: {value: user.password}});
        });

        await act(async () => {
            fireEvent.click(signUpButton);
        });

        expect(signUpButton).toBeEnabled();
    });
});

describe('Validation', () =>
{
    it.todo('When form is submitted then all fields must be filled');
    it.todo('When fields are not properly filled then submit button is disabled');
    it.todo('When fields are properly filled then submit button is enabled');
    it.todo('When email is filled then email has correct format');
    it.todo('When email is filled then email must be shorter or equal to maximum email length');
    it.todo('When password is filled then password must be shorter or equal to maximum password length');
    it.todo('When email is not in a correct format then the warning message is displayed');
    it.todo('When password is not in a correct length then the warning message is displayed');
});