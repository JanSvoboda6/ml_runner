import React from "react";
import axios from "axios";
import AuthenticationService from "../../services/AuthenticationService";
import {act} from 'react-dom/test-utils';

describe('Login/logout', () =>
{
    const user = {
        username: 'user@domain.com',
        password: 'Thisisarandompassword_999',
        accessToken: 'a random token'
    }

    test('When user is successfully logged in then user information are stored in the local storage', async () => {
        const response = {data: user}
        jest.spyOn(axios, "post").mockResolvedValue(response);

        await act(async () => {
            await AuthenticationService.login(user.username, user.password);
        });

        const userFromLocalStorage = localStorage.getItem("user");
        expect(userFromLocalStorage).toBe(JSON.stringify(user));
    });

    test('When user is not successfully logged in then user information are not stored in the local storage', async () => {
        jest.spyOn(axios, "post").mockRejectedValue('Problem occurred!');

        try {
            await act(async () => {
                await AuthenticationService.login(user.username, user.password);
            });
        } catch (exception) {
        }

        const userFromLocalStorage = localStorage.getItem("user");
        expect(userFromLocalStorage).toBe(null);
    });

    test('After user is logged out then user information are not stored in the local storage', () => {
        localStorage.setItem("user", JSON.stringify(user));
        AuthenticationService.logout();
        expect(localStorage.getItem("user")).toBe(null);
    });
});