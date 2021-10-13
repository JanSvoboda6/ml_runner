import React, { Component } from "react";
import { Redirect } from 'react-router-dom';
import Form from "react-validation/build/form";
import Input from "react-validation/build/input";
import CheckButton from "react-validation/build/button";
import { isEmail } from "validator";

import { connect } from "react-redux";
import { register } from "../actions/Authentication";
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

const email = (value) =>
{
    if (!isEmail(value))
    {
        return (
            <div className="alert-text">
                This is not a valid email.
            </div>
        );
    }
};

const vusername = (value) =>
{
    if (value.length < 3 || value.length > 20)
    {
        return (
            <div className="alert-text">
                The username must be between 3 and 20 characters.
            </div>
        );
    }
};

const vpassword = (value) =>
{
    if (value.length < 6 || value.length > 40)
    {
        return (
            <div className="alert-text">
                The password must be between 6 and 40 characters.
            </div>
        );
    }
};

class Register extends Component
{
    constructor(props)
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
            successful: false,
        };
    }

    onChangeUsername(e)
    {
        this.setState({
            username: e.target.value,
        });
    }

    onChangeEmail(e)
    {
        this.setState({
            email: e.target.value,
        });
    }

    onChangePassword(e)
    {
        this.setState({
            password: e.target.value,
        });
    }

    handleRegister(e)
    {
        e.preventDefault();

        this.setState({
            successful: false,
        });

        this.form.validateAll();

        if (this.checkBtn.context._errors.length === 0)
        {
            this.props
                .dispatch(
                    register(this.state.username, this.state.email, this.state.password)
                )
                .then(() =>
                {
                    this.setState({
                        successful: true,
                    });
                })
                .catch(() =>
                {
                    this.setState({
                        successful: false,
                    });
                });
        }
    }

    render()
    {
        const { message } = this.props;
        const { successful } = this.state;
        if (successful)
        {
            return <Redirect to="/login?popup=t" />;
        }

        return (
            <div>
                <div className="register-page">
                    <a classname="register-item logo-register"><img className='image-logo' src={ logo } alt="logo_but" /></a>
                    <Form
                        onSubmit={ this.handleRegister }
                        ref={ (c) =>
                        {
                            this.form = c;
                        } }
                    >
                        { !this.state.successful && (
                            <div>
                                <div className="register-item username-text">
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

                                <div className="register-item email-text">
                                    <label htmlFor="email">Email</label>
                                    <Input
                                        type="text"
                                        className="input-text"
                                        name="email"
                                        value={ this.state.email }
                                        onChange={ this.onChangeEmail }
                                        validations={ [required, email] }
                                    />
                                </div>

                                <div className="register-item password-text">
                                    <label htmlFor="password">Password</label>
                                    <Input
                                        type="password"
                                        className="input-text"
                                        name="password"
                                        value={ this.state.password }
                                        onChange={ this.onChangePassword }
                                        validations={ [required, vpassword] }
                                    />
                                </div>

                                <button className="register-item submit-button">Sign Up</button>

                            </div>
                        ) }

                        { message && (
                            <div className="form-group">
                                <div className={ this.state.successful ? "alert alert-success" : "alert alert-danger" } role="alert">
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
    const { message } = state.message;
    return {
        message,
    };
}

export default connect(mapStateToProps)(Register);