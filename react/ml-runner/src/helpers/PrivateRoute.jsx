import React from 'react';
import { Route, Redirect } from 'react-router-dom';

const PrivateRoute = ({component: Component, ...rest}) => {
    return (
        <Route {...rest} render={props => (
            <Component {...props} /> === localStorage.user ? <Component {...props} /> : <Redirect to="/login" />
        )} />
    );
};

export default PrivateRoute;