import {act} from "react-dom/test-utils";
import {fireEvent, render, screen} from "@testing-library/react";
import {Router} from "react-router";
import LoginPage from "../../components/pages/LoginPage";
import React from "react";
import Navbar from "../../components/navigation/Navbar";
import {createMemoryHistory} from "history";
import LogoutService from "../../services/LogoutService";
import * as redux from "react-redux";

describe('Logout handling', () =>
{
    test('When logout button is clicked then user is redirected to the login page', async() => {
        jest.spyOn(redux, 'useDispatch').mockReturnValue(jest.fn());
        LogoutService.logout = jest.fn();
        const history = createMemoryHistory();
        history.push = jest.fn();
        await act(async () => {
            render(<Router history={history}><Navbar/></Router>);
        });
        await act(async () => {
            fireEvent.click(screen.getByText(/logout/i));
        });

        expect(history.push).toHaveBeenCalledTimes(1);
    });
});