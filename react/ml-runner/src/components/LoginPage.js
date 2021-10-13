import React, { Component } from "react";
import { Redirect } from 'react-router-dom';

import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";
import Popup from "../components/Popup";

import { connect } from "react-redux";
import { login } from "../actions/Authentication";
import logo from '../styles/logo_but_text.png'

const required = (value) =>
{
    if (!value)
    {
        return (
            <div className="alert-text">
                This field is required!
            </div>
        );
    }
};

class Login extends Component
{
    constructor(props)
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

        this.form.validateAll();

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

    closePopup(e)
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
                { showPopup == 't' && !isPopupClosed && (<Popup content="Thanks for registration.  Now you can login!" handleClose={ this.closePopup } />) }
                <a classname="register-item logo-register"><img className='image-logo' src={ logo } alt="logo_but" /></a>
                <div className="login-page-content">

                    <Form
                        onSubmit={ this.handleLogin }
                        ref={ (c) =>
                        {
                            this.form = c;
                        } }
                    >
                        <div className="login-item">
                            <label htmlFor="username">Username</label>
                            <Input
                                type="text"
                                className="input-text"
                                name="username"
                                value={ this.state.username }
                                onChange={ this.onChangeUsername }
                                validations={ [required] }
                            />
                        </div>

                        <div className="login-item">
                            <label htmlFor="password">Password</label>
                            <Input
                                type="password"
                                className="input-text"
                                name="password"
                                value={ this.state.password }
                                onChange={ this.onChangePassword }
                                validations={ [required] }
                            />
                        </div>

                        <div className="login-item">
                            <button
                                className="submit-button"
                                disabled={ this.state.loading }
                            >
                                { this.state.loading && (
                                    <span className="spinner"></span>
                                ) }
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
                        <CheckButton
                            style={ { display: "none" } }
                            ref={ (c) =>
                            {
                                this.checkBtn = c;
                            } }
                        />
                    </Form>
                </div>
            </div>
        );
    }
}

function mapStateToProps(state)
{
    const { isLoggedIn } = state.auth;
    const { message } = state.message;
    return {
        isLoggedIn,
        message
    };
}

export default connect(mapStateToProps)(Login);
