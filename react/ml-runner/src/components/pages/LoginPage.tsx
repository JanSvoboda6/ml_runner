import React, {useState} from "react";
import {Link, Redirect} from 'react-router-dom';
import {useDispatch} from "react-redux";
import logo from '../../styles/logo_but_text.png';
import cube from '../../styles/cube_animation.gif';
import LoginService from '../../services/LoginService';
import HelperBox from "../navigation/HelperBox";
import FadeIn from "react-fade-in";
import Validator from 'validator';

/**
 * Landing page that provides login form.
 */
function Login()
{
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [message, setMessage] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [isLoggedIn, setLoggedIn] = useState(false);

    const search = window.location.search;
    const params = new URLSearchParams(search);
    const showPopup = params.get("popup");

    const dispatch = useDispatch();

    const onChangeUsername = (e: { target: { value: string; }; }) =>
    {
        setUsername(e.target.value);
    }

    const onChangePassword = (e: { target: { value: string; }; }) =>
    {
        setPassword(e.target.value);
    }

    const handleLogin = (e: { preventDefault: () => void; }) =>
    {
        e.preventDefault();

        setIsLoading(true);

        if (validateForm())
        {
            const user = {username: username, password: password, accessToken: ""};

            LoginService.login(dispatch, user)
                .then(
                    (user: any) =>
                    {
                        setLoggedIn(true);
                    },
                    (error: any) =>
                    {
                        setLoggedIn(false);
                        setIsLoading(false);

                        setMessage(error.response.data);
                    });
        } else
        {
            setIsLoading(false);
        }
    }

    const validateForm = (): boolean => {
        if(username.length > 128)
        {
            setMessage("Email cannot have more than 128 characters!");
            return false;
        }
        if (!Validator.isEmail(username, {ignore_max_length: false}))
        {
            setMessage("Email format is not valid!");
            return false;
        }
        if(password.length === 0)
        {
            setMessage("Password cannot be empty!");
            return false;
        }
        if(password.length > 50)
        {
            setMessage("Password cannot have more than 50 character!");
            return false;
        }
        return  true;
    }

    if (isLoggedIn)
    {
        return <Redirect to="/preparing" />;
    }

    return (
            <>
            <div className="wrapper">
                {showPopup === 't' && <HelperBox content="Thank you for the registration. We have sent you an activation email." onClose={() => null}/>}
            </div>
            <div className="landing-page-wrapper">
                <div className="landing-page-content">
                    <FadeIn delay={250}>
                        <div className="landing-page">
                            <div className="landing-page-text">Machine Learning Runner</div>
                            <div className="landing-page-information-text-wrapper">
                                <div className='landing-page-information-text'>
                                    Upload data.
                                    Run model.
                                    Analyze.
                                </div>
                            </div>
                            <div className="login-form-wrapper">
                                <div className="login-form">
                                    <img className='logo' src={logo} alt="logo_but" />
                                    <div className="login-page-content">
                                        <form onSubmit={handleLogin}>
                                            <div className="login-item">
                                                <input
                                                    type="text"
                                                    className="input-text"
                                                    name="email"
                                                    placeholder="Email"
                                                    value={username}
                                                    onChange={onChangeUsername}
                                                />
                                            </div>
                                            <div className="login-item">
                                                <input
                                                    type="password"
                                                    className="input-text"
                                                    name="password"
                                                    placeholder="Password"
                                                    value={password}
                                                    onChange={onChangePassword}
                                                />
                                            </div>
                                            <div className="login-item">
                                                <button className="submit-button" disabled={isLoading}>
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
                            </div>
                            <div className="cube-image-wrapper">
                                <img className='cube-animation fade-in-image' src={cube} alt="cube_animation" />
                            </div>
                        </div>
                    </FadeIn>
                </div >
            </div>
            </>
    );
}

export default Login;
