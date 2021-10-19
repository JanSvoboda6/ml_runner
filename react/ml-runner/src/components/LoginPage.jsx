import React, { Component } from "react";
import { Redirect } from 'react-router-dom';

import { useSelector, useDispatch } from 'react-redux'

import Popup from "./Popup";

import { connect } from "react-redux";
import { Login as login } from "../actions/Authentication";
import logo from '../styles/logo_but_text.png'
import { login as loginAction } from '../redux/UserSlice';

import { RootState } from '../redux/store'

class Login extends Component
{
    constructor(props) //TODO Jan: It si possible to desctruct props {dispatch, history}
    {
        super(props);
        this.handleLogin = this.handleLogin.bind(this);
        this.onChangeUsername = this.onChangeUsername.bind(this);
        this.onChangePassword = this.onChangePassword.bind(this);
        this.closePopup = this.closePopup.bind(this);

        this.state = {
            username: "",
            password: "",
            loading: false,
            isPopupClosed: false
        };
    }

    onChangeUsername(e)
    {
        this.setState({
            username: e.target.value,
        });
    }

    onChangePassword(e)
    {
        this.setState({
            password: e.target.value,
        });
    }

    handleLogin(e)
    {
        e.preventDefault();

        this.setState({
            loading: true,
        });

        var isValidationSuccesfull = true;
        if (isValidationSuccesfull) //TODO Jan: implement proper validation
        {
            var user = { username: this.state.username, password: this.state.password, accessToken: "" };

            login(user)
                .then((user) =>
                {
                    this.props.loginAction(user);

                    this.setState({ isLoggedIn: true });
                },
                    (error) =>
                    {
                        this.setState({ isLoggedIn: false });
                    });
        } else
        {
            this.setState({
                loading: false,
            });
        }


    }

    closePopup(e)
    {
        e.preventDefault();

        this.setState({
            isPopupClosed: true
        });
    }


    render()
    {
        const { isLoggedIn, message } = this.state;
        const { isPopupClosed } = this.state;
        if (isLoggedIn)
        {
            return <Redirect to="/" />;
        }

        const search = window.location.search;
        const params = new URLSearchParams(search);
        const showPopup = params.get('popup');

        return (
            <div className="login-page">
                { showPopup == 't' && !isPopupClosed && (<Popup content="Thanks for registration.  Now you can login!" handleClose={ this.closePopup } />) }
                <a className="register-item logo-register"><img className='logo' src={ logo } alt="logo_but" /></a>
                <div className="login-page-content">

                    <form onSubmit={ this.handleLogin }>
                        <div className="login-item">
                            <label htmlFor="username">Username</label>
                            <div className="login-item">
                                <input
                                    type="text"
                                    className="input-text"
                                    name="username"
                                    value={ this.state.username }
                                    onChange={ this.onChangeUsername }
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
                                    value={ this.state.password }
                                    onChange={ this.onChangePassword }
                                />
                            </div>
                        </div>

                        <div className="login-item">
                            <button className="submit-button" disabled={ this.state.loading }>
                                <span>Login</span>
                            </button>
                        </div>

                        { message && (
                            <div className="login-item">
                                <div className="alert-text">
                                    { message }
                                </div>
                            </div>
                        ) }
                    </form>
                </div>
            </div>
        );
    }
}

const mapDispatchToProps = dispatch =>
{
    return {
        loginAction: (user) => { dispatch(loginAction(user)); }
    }
}

const mapStateToProps = (state) =>
{
    return {
        stateValue: state
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(Login);
