import React, {useEffect} from 'react';
import jwtDecode from "jwt-decode";
import {useDispatch} from "react-redux";
import { Route, Redirect } from 'react-router-dom';
import LogoutService from "../services/LogoutService";

const PrivateRoute = ({component: Component, ...rest}) => {

    const dispatch = useDispatch();

    useEffect(() => {
        logoutUserIfAccessTokenIsExpired();
    });

    const logoutUserIfAccessTokenIsExpired = () => {
        const user = JSON.parse(localStorage.getItem("user"));
        if (user)
        {
            const {exp} = jwtDecode(user.accessToken);
            if (Date.now() > exp * 1000)
            {
                LogoutService.logout(dispatch);
            }
        }
    }

    return (
        <Route {...rest} render={props => (
            localStorage.user ? <Component {...props} /> : <Redirect to="/login" />
        )} />
    );
};

export default PrivateRoute;