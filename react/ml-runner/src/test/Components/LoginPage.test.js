import React from 'react';
import * as redux from "react-redux";
import {fireEvent, render, screen} from "@testing-library/react";
import LoginPage from "../../components/pages/LoginPage";
import {Router} from "react-router";
import {createMemoryHistory} from 'history';
import LoginService from "../../services/LoginService";
import Login from "../../components/pages/LoginPage";
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
            password: 'Thisisarandompassword_999',
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
        });

        await act(async () => {
            fireEvent.click(loginButton);
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
            fireEvent.change(password, {target: {value: 'Thisisarandompassword_999'}});
            fireEvent.click(loginButton);
        });

        expect(history.replace).toHaveBeenCalledTimes(0);
    });

    test('When form is submitted and there is an exception on server side then error message is displayed', async () => {
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
            fireEvent.change(password, {target: {value: 'Thisisarandompassword_999'}});
            fireEvent.click(loginButton);
        });

        await act(async () => {
            fireEvent.click(loginButton);
        });

        const errorMessage = screen.getByText(message);
        expect(errorMessage).toBeInTheDocument();
    });

    test('When form has been submitted and there is an error then submit button becomes enabled', async () => {
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
            fireEvent.change(password, {target: {value: 'Thisisarandompassword_999'}});
            fireEvent.click(loginButton);
        });

        expect(loginButton).toBeEnabled();
    });

    it.todo('When form has been submitted then submit button becomes disabled');
});

describe('Validation', () => {
    test('When email field is blank and login button is clicked then validation message is displayed', async () => {
        jest.spyOn(redux, 'useDispatch').mockReturnValue(jest.fn());
        const history = createMemoryHistory();

        history.replace = jest.fn();

        await act(async () => {
            render(<Router history={history}><LoginPage/></Router>);
        });

        const email = screen.getByPlaceholderText(/email/i);
        const password = screen.getByPlaceholderText(/password/i);
        const loginButton = screen.getByText("Login");

        await act(async () => {
            fireEvent.change(email, {target: {value: ''}});
            fireEvent.change(password, {target: {value: 'Thisisarandompassword_999'}});
            fireEvent.click(loginButton);
        });

        const errorMessage = screen.getByText("Email format is not valid!");
        expect(errorMessage).toBeInTheDocument();
    });

    test('When email field is not in proper format and login button is clicked then validation message is displayed', async () => {
        jest.spyOn(redux, 'useDispatch').mockReturnValue(jest.fn());
        const history = createMemoryHistory();

        history.replace = jest.fn();

        await act(async () => {
            render(<Router history={history}><LoginPage/></Router>);
        });

        const email = screen.getByPlaceholderText(/email/i);
        const password = screen.getByPlaceholderText(/password/i);
        const loginButton = screen.getByText("Login");

        await act(async () => {
            fireEvent.change(email, {target: {value: 'an invalid email@ format'}});
            fireEvent.change(password, {target: {value: 'Thisisarandompassword_999'}});
            fireEvent.click(loginButton);
        });

        const errorMessage = screen.getByText("Email format is not valid!");
        expect(errorMessage).toBeInTheDocument();
    });

    test('When password is not properly filled and login button is clicked then validation message is displayed', async () => {
        jest.spyOn(redux, 'useDispatch').mockReturnValue(jest.fn());
        const history = createMemoryHistory();

        history.replace = jest.fn();

        await act(async () => {
            render(<Router history={history}><LoginPage/></Router>);
        });

        const email = screen.getByPlaceholderText(/email/i);
        const password = screen.getByPlaceholderText(/password/i);
        const loginButton = screen.getByText("Login");

        await act(async () => {
            fireEvent.change(email, {target: {value: 'user@user.com'}});
            fireEvent.change(password, {target: {value: ''}});
        });

        await act(async () => {
            fireEvent.click(loginButton);
        });

        const errorMessage = screen.getByText("Password is not valid!");
        expect(errorMessage).toBeInTheDocument();
    });

    test('When email has maximum length of 128 characters then validation is successful', async () => {
        jest.spyOn(redux, 'useDispatch').mockReturnValue(jest.fn());
        const history = createMemoryHistory();

        history.replace = jest.fn();

        await act(async () => {
            render(<Router history={history}><LoginPage/></Router>);
        });

        let name = 'n'.repeat(64);
        let domain = 'd'.repeat(63);
        let emailAddressWithNameAndDomainLengthOf127 = name + '@' + domain + '.com';

        const email = screen.getByPlaceholderText(/email/i);
        const password = screen.getByPlaceholderText(/password/i);
        const loginButton = screen.getByText("Login");

        await act(async () => {
            fireEvent.change(email, {target: {value: emailAddressWithNameAndDomainLengthOf127}});
            fireEvent.change(password, {target: {value: 'Thisisarandompassword_999'}});
        });

        await act(async () => {
            fireEvent.click(loginButton);
        });

        const errorMessage = screen.queryByText("Email format is not valid!");
        expect(errorMessage).not.toBeInTheDocument();

    });

    test('When email is longer than 128 characters then validation is not successful', async () => {
        jest.spyOn(redux, 'useDispatch').mockReturnValue(jest.fn());
        const history = createMemoryHistory();

        history.replace = jest.fn();

        await act(async () => {
            render(<Router history={history}><LoginPage/></Router>);
        });

        let name = 'n'.repeat(64);
        let domain = 'd'.repeat(64);
        let emailAddressWithNameAndDomainLengthOf128 = name + '@' + domain + '.com';

        const email = screen.getByPlaceholderText(/email/i);
        const password = screen.getByPlaceholderText(/password/i);
        const loginButton = screen.getByText("Login");

        await act(async () => {
            fireEvent.change(email, {target: {value: emailAddressWithNameAndDomainLengthOf128}});
            fireEvent.change(password, {target: {value: 'Thisisarandompassword_999'}});
        });

        await act(async () => {
            fireEvent.click(loginButton);
        });

        const errorMessage = screen.getByText("Email format is not valid!");
        expect(errorMessage).toBeInTheDocument();

    });

    test('When password is shorter or equal to maximum password length of 50 characters then validation is successful', async() =>{
        jest.spyOn(redux, 'useDispatch').mockReturnValue(jest.fn());
        const history = createMemoryHistory();

        history.replace = jest.fn();

        await act(async () => {
            render(<Router history={history}><LoginPage/></Router>);
        });

        const email = screen.getByPlaceholderText(/email/i);
        const password = screen.getByPlaceholderText(/password/i);
        const loginButton = screen.getByText("Login");

        let strongPasswordOfLength10 = 'Strong_999'
        let passwordWithMaximalLength = strongPasswordOfLength10.repeat(5);

        await act(async () => {
            fireEvent.change(email, {target: {value: 'user@user.com'}});
            fireEvent.change(password, {target: {value: passwordWithMaximalLength}});
        });

        await act(async () => {
            fireEvent.click(loginButton);
        });

        const errorMessage = screen.queryByText("Password format is not valid!");
        expect(errorMessage).not.toBeInTheDocument()
    });

    test('When password is longer than maximum password length of 50 characters then validation is not successful', async() =>{
        jest.spyOn(redux, 'useDispatch').mockReturnValue(jest.fn());
        const history = createMemoryHistory();

        history.replace = jest.fn();

        await act(async () => {
            render(<Router history={history}><LoginPage/></Router>);
        });

        const email = screen.getByPlaceholderText(/email/i);
        const password = screen.getByPlaceholderText(/password/i);
        const loginButton = screen.getByText("Login");

        let tooLongPassword = 'p'.repeat(51);
        await act(async () => {
            fireEvent.change(email, {target: {value: 'user@user.com'}});
            fireEvent.change(password, {target: {value: tooLongPassword}});
        });

        await act(async () => {
            fireEvent.click(loginButton);
        });

        const errorMessage = screen.getByText("Password is not valid!");
        expect(errorMessage).toBeInTheDocument()
    });
});