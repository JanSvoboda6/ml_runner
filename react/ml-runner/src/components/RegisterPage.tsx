import React, { Component } from "react";
import { Redirect } from 'react-router-dom';

import { connect } from "react-redux";
import { Register as register } from "../actions/Authentication";
import logo from '../styles/logo_but_text.png'
import { AppState, RegisterState } from "../types";

class Register extends Component<AppState, RegisterState>
{
    constructor(props: AppState)
    {
        super(props);
        this.handleRegister = this.handleRegister.bind(this);
        this.onChangeUsername = this.onChangeUsername.bind(this);
        this.onChangeEmail = this.onChangeEmail.bind(this);
        this.onChangePassword = this.onChangePassword.bind(this);

        this.state = {
            username: "",
            email: "",
            password: "",
            isRegistrationSuccessful: false,
            message: ""
        };
    }

    onChangeUsername(e: { target: { value: string; }; })
    {
        this.setState({
            username: e.target.value,
        });
    }

    onChangeEmail(e: { target: { value: string; }; })
    {
        this.setState({
            email: e.target.value,
        });
    }

    onChangePassword(e: { target: { value: string; }; })
    {
        this.setState({
            password: e.target.value,
        });
    }

    handleRegister(e: { preventDefault: () => void; })
    {
        e.preventDefault();
        this.setState({
            isRegistrationSuccessful: false,
        });

        var isValidationSuccesful = true;
        if (isValidationSuccesful) //TODO Jan: implement proper validation
        {
            register(this.state.username, this.state.email, this.state.password)
                .then(() =>
                {
                    this.setState({ isRegistrationSuccessful: true });
                },
                    (error: any) =>
                    {
                        this.setState({ message: error.response.data.message });
                    });
        }
    }

    render()
    {
        const { isRegistrationSuccessful, message } = this.state;

        if (isRegistrationSuccessful)
        {
            return <Redirect to="/login?popup=t" />;
        }

        return (
            <div>
                <div className="register-page">
                    <a className="register-item logo-register"><img className='logo' src={logo} alt="logo_but" /></a>
                    <form onSubmit={this.handleRegister}>
                        <div>
                            <div className="register-item">
                                <label htmlFor="username">Username</label>
                                <div className="register-item username-text">
                                    <input
                                        type="text"
                                        className="input-text"
                                        name="username"
                                        value={this.state.username}
                                        onChange={this.onChangeUsername}
                                    />
                                </div>
                            </div>

                            <div className="register-item">
                                <label htmlFor="email">Email</label>
                                <div className="register-item email-text">
                                    <input
                                        type="text"
                                        className="input-text"
                                        name="email"
                                        value={this.state.email}
                                        onChange={this.onChangeEmail}
                                    />
                                </div>
                            </div>

                            <div className="register-item">
                                <label htmlFor="password">Password</label>
                                <div className="register-item password-text">
                                    <input
                                        type="password"
                                        className="input-text"
                                        name="password"
                                        value={this.state.password}
                                        onChange={this.onChangePassword}
                                    />
                                </div>
                            </div>

                            <button className="register-item submit-button">Sign Up</button>
                        </div>

                        {message !== "" && (
                            <div className="register-item">
                                {message}
                            </div>
                        )}
                    </form>
                </div>
            </div >
        );
    }
}

export default connect()(Register);