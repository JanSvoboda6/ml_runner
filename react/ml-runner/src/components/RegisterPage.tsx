import React, { Component } from "react";
import { Redirect } from 'react-router-dom';

import { connect } from "react-redux";
import { register } from "../actions/Authentication";
import logo from '../styles/logo_but_text.png'
import { AppState, RegisterState } from "../types";

class Register extends Component<AppState, RegisterState>
{
    form: any;

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
            isRegistrationSuccessful: false
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

        var isValidationSuccesfull = true;
        if (isValidationSuccesfull) //TODO Jan: implement proper validation
        {
            this.props
                .dispatch(
                    register(this.state.username, this.state.email, this.state.password)
                )
                .then(() =>
                {
                    this.setState({
                        isRegistrationSuccessful: true,
                    });
                })
                .catch(() =>
                {
                    this.setState({
                        isRegistrationSuccessful: false,
                    });
                });
        }
    }

    render()
    {
        const { message } = this.props; //TODO Jan: Destruct in a way that message.message is not needed
        const { isRegistrationSuccessful: successful } = this.state;

        if (successful)
        {
            return <Redirect to="/login?popup=t" />;
        }

        return (
            <div>
                <div className="register-page">
                    <a className="register-item logo-register"><img className='logo' src={logo} alt="logo_but" /></a>
                    <form onSubmit={this.handleRegister}>
                        {!this.state.isRegistrationSuccessful && (
                            <div>
                                <div className="register-item username-text">
                                    <label htmlFor="username">Username</label>
                                    <input
                                        type="text"
                                        className="input-text"
                                        name="username"
                                        value={this.state.username}
                                        onChange={this.onChangeUsername}
                                    />
                                </div>

                                <div className="register-item email-text">
                                    <label htmlFor="email">Email</label>
                                    <input
                                        type="text"
                                        className="input-text"
                                        name="email"
                                        value={this.state.email}
                                        onChange={this.onChangeEmail}
                                    />
                                </div>

                                <div className="register-item password-text">
                                    <label htmlFor="password">Password</label>
                                    <input
                                        type="password"
                                        className="input-text"
                                        name="password"
                                        value={this.state.password}
                                        onChange={this.onChangePassword}
                                    />
                                </div>

                                <button className="register-item submit-button">Sign Up</button>

                            </div>
                        )}

                        {message.message !== "" && (
                            <div className="form-group">
                                <div className={this.state.isRegistrationSuccessful ? "alert alert-success" : "alert alert-danger"}>
                                    {message.message}
                                </div>
                            </div>
                        )}
                    </form>
                </div>
            </div >
        );
    }
}

function mapStateToProps(state: { message: any; })
{
    const { message } = state;
    return {
        message,
    };
}

export default connect(mapStateToProps)(Register);