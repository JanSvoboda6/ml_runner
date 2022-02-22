import {createMemoryHistory} from "history";
import {act} from "react-dom/test-utils";
import {fireEvent, render, screen} from "@testing-library/react";
import {Router} from "react-router";
import React from "react";
import RegisterService from "../../services/RegisterService";
import RegisterPage from "../../components/pages/RegisterPage";
import Validator from "validator";

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
    test('When form is submitted then fields are validated', async() => {
        let isEmailSpy = jest.spyOn(Validator, "isEmail").mockReturnValue(true);
        let isLengthSpy = jest.spyOn(Validator, "isLength").mockReturnValue(true);
        let isStrongPasswordSpy = jest.spyOn(Validator, "isStrongPassword").mockReturnValue(true);

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

        expect(isEmailSpy).toHaveBeenCalledTimes(1);
        expect(isLengthSpy).toHaveBeenCalledTimes(1);
        expect(isStrongPasswordSpy).toHaveBeenCalledTimes(1);
    })

    test('When email is not in a correct format then the warning message is displayed', async() =>{
        jest.spyOn(Validator, "isEmail").mockReturnValue(false);
        jest.spyOn(Validator, "isLength").mockReturnValue(true);
        jest.spyOn(Validator, "isStrongPassword").mockReturnValue(true);

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

        const errorMessage = screen.getByText("Email format is not valid!");
        expect(errorMessage).toBeInTheDocument()
    });

    test('When password is longer than maximum length then the warning message is displayed', async() =>{
        jest.spyOn(Validator, "isEmail").mockReturnValue(true);
        jest.spyOn(Validator, "isLength").mockReturnValue(false);
        jest.spyOn(Validator, "isStrongPassword").mockReturnValue(true);

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

        const errorMessage = screen.getByText("Password is not valid!");
        expect(errorMessage).toBeInTheDocument();
    });

    test('When password is not a strong password then the warning message is displayed', async() =>{
        jest.spyOn(Validator, "isEmail").mockReturnValue(true);
        jest.spyOn(Validator, "isLength").mockReturnValue(true);
        jest.spyOn(Validator, "isStrongPassword").mockReturnValue(false);

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

        const errorMessage = screen.getByText("Password is not valid!");
        expect(errorMessage).toBeInTheDocument();
    });
});