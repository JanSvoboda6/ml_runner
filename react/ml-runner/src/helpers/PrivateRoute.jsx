import React, {useEffect, useState} from 'react';
import jwtDecode from "jwt-decode";
import {useDispatch} from "react-redux";
import { Route, Redirect } from 'react-router-dom';
import LogoutService from "../services/LogoutService";

/**
 * Used for redirecting to login page when user tries to access any page that require user to be logged in.
 * It also checks the expiration of JWT. If the token is expired user is logged out.
 */
const PrivateRoute = ({component: Component, ...rest}) => {
    const [shouldLogout, setShouldLogout] = useState(false);

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
                setShouldLogout(true);
            }
        }
    }

    if(shouldLogout)
    {
        return <Redirect to="/login" />;
    }

    return (
        <Route {...rest} render={props => (
            localStorage.user ? <Component {...props} /> : <Redirect to="/login" />
        )} />
    );
};

export default PrivateRoute;