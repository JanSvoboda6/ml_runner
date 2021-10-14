import React, { Component } from "react";
import { Redirect } from 'react-router-dom';

import Popup from "./Popup";

import { connect } from "react-redux";
import { login } from "../actions/Authentication";
import logo from '../styles/logo_but_text.png'

interface AppState
{
    dispatch: any,
    history: any,
    isLoggedIn: boolean,
    message: string
}

interface LoginState
{
    username: string,
    password: string,
    loading: boolean,
    isPopupClosed: boolean
}

class Login extends Component<AppState, LoginState>
{
    checkBtn: any;
    form: any;

    constructor(props: AppState)
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

    onChangeUsername(e: { target: { value: string; }; })
    {
        this.setState({
            username: e.target.value,
        });
    }

    onChangePassword(e: { target: { value: string; }; })
    {
        this.setState({
            password: e.target.value,
        });
    }

    handleLogin(e: { preventDefault: () => void; })
    {
        e.preventDefault();

        this.setState({
            loading: true,
        });

        const { dispatch, history } = this.props;

        if (this.checkBtn.context._errors.length === 0)
        {
            dispatch(login(this.state.username, this.state.password))
                .then(() =>
                {
                    history.push("/");
                    window.location.reload();
                })
                .catch(() =>
                {
                    this.setState({
                        loading: false
                    });
                });
        } else
        {
            this.setState({
                loading: false,
            });
        }
    }

    closePopup(e: { preventDefault: () => void; })
    {
        e.preventDefault();

        this.setState({
            isPopupClosed: true
        });
    }


    render()
    {
        const { isLoggedIn, message } = this.props;
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
                {showPopup == 't' && !isPopupClosed && (<Popup content="Thanks for registration.  Now you can login!" handleClose={this.closePopup} />)}
                <a className="register-item logo-register"><img className='logo' src={logo} alt="logo_but" /></a>
                <div className="login-page-content">

                    <form
                        onSubmit={this.handleLogin}
                        ref={(c) =>
                        {
                            this.form = c;
                        }}
                    >
                        <div className="login-item">
                            <label htmlFor="username">Username</label>
                            <input
                                type="text"
                                className="input-text"
                                name="username"
                                value={this.state.username}
                                onChange={this.onChangeUsername}
                            />
                        </div>

                        <div className="login-item">
                            <label htmlFor="password">Password</label>
                            <input
                                type="password"
                                className="input-text"
                                name="password"
                                value={this.state.password}
                                onChange={this.onChangePassword}
                            />
                        </div>

                        <div className="login-item">
                            <button
                                className="submit-button"
                                disabled={this.state.loading}
                            >
                                {this.state.loading && (
                                    <span className="spinner"></span>
                                )}
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
                        <button
                            style={{ display: "none" }}
                            ref={(c) =>
                            {
                                this.checkBtn = c;
                            }}
                        />
                    </form>
                </div>
            </div>
        );
    }
}

function mapStateToProps(state: { auth: { isLoggedIn: boolean; }; message: { message: string; }; })
{
    const { isLoggedIn } = state.auth;
    const { message } = state.message;
    return {
        isLoggedIn,
        message
    };
}

export default connect(mapStateToProps)(Login);
