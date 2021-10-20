import React from "react";
import { Link, Redirect } from 'react-router-dom';
import Popup from "./Popup";
import { useDispatch } from "react-redux";
import logo from '../styles/logo_but_text.png'
import { LoginService } from '../services/LoginService';
import { useState } from "react";

function Login()
{
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    const [isPopupClosed, setPopupClosed] = useState(false);
    const [loading, setLoading] = useState(false);
    const [isLoggedIn, setLoggedIn] = useState(false);

    const search = window.location.search;
    const params = new URLSearchParams(search);
    const showPopup = params.get('popup');

    const dispatch = useDispatch();

    const onChangeUsername = (e: { target: { value: any; }; }) =>
    {
        setUsername(e.target.value);
    }

    const onChangePassword = (e: { target: { value: any; }; }) =>
    {
        setPassword(e.target.value);
    }

    const handleLogin = (e: { preventDefault: () => void; }) =>
    {
        e.preventDefault();

        setLoading(true);

        var isValidationSuccesfull = true;
        if (isValidationSuccesfull) //TODO Jan: implement proper validation
        {
            var user = { username: username, password: password, accessToken: "" };

            LoginService(dispatch, user)
                .then(
                    (user: any) =>
                    {
                        setLoggedIn(true);
                    },
                    (error: any) =>
                    {
                        console.log(error);
                        setLoggedIn(false);
                        setLoading(false);

                        var message = "";
                        if (error && error.response && error.response.data.message)
                        {
                            message = error.response.data.message;
                        }
                        else if (error.message)
                        {
                            message = error.message;
                        }
                        else if (error.toString())
                        {
                            message = error.toString();
                        }

                        setMessage(message);
                    });
        } else
        {
            setLoading(false);
        }
    }

    const closePopup = (e: { preventDefault: () => void; }) =>
    {
        e.preventDefault();
        setPopupClosed(true);
    }

    if (isLoggedIn)
    {
        return <Redirect to="/" />;
    }

    return (
        <div className="login-page">
            {showPopup == 't' && !isPopupClosed && (<Popup content="Thanks for registration.  Now you can login!" handleClose={closePopup} />)}
            <a className="register-item logo-register"><img className='logo' src={logo} alt="logo_but" /></a>
            <div className="login-page-content">

                <form onSubmit={handleLogin}>
                    <div className="login-item">
                        <label htmlFor="username">Username</label>
                        <div className="login-item">
                            <input
                                type="text"
                                className="input-text"
                                name="username"
                                value={username}
                                onChange={onChangeUsername}
                            />
                        </div>
                    </div>

                    <div className="login-item">
                        <label htmlFor="password">Password</label>
                        <div className="login-item">
                            <input
                                type="password"
                                className="input-text"
                                name="password"
                                value={password}
                                onChange={onChangePassword}
                            />
                        </div>
                    </div>

                    <div className="login-item">
                        <button className="submit-button" disabled={loading}>
                            <span>Login</span>
                        </button>
                    </div>

                    {message && (
                        <div className="login-item">
                            <div className="alert-text">
                                {message}
                            </div>
                        </div>
                    )}
                </form>
                <div className="login-link">
                    <p className="login-link-text" >Do not have an account?</p>
                    <Link className="login-link-reference" to="/register">Register</Link>
                </div>
            </div>
        </div>
    );
}

export default Login;
