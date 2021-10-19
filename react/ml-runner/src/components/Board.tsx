import React, { Component } from 'react';
import { connect, useDispatch } from 'react-redux';
import '../App.css';
import ModelList from '../ModelList';
import logo from '../styles/logo_but_text.png'
import AuthenticationService from '../services/AuthenticationService';
import { logout as logoutAction } from '../redux/UserSlice';
import { ActionCreatorWithoutPayload } from '@reduxjs/toolkit';

interface AppState
{
    dispatch: any,
    history: any
}

class Board extends Component<AppState>
{
    constructor(props: AppState)
    {
        super(props);
        this.handleLogout = this.handleLogout.bind(this);
    }

    handleLogout(e: { preventDefault: () => void; })
    {
        const { history } = this.props;
        e.preventDefault();
        AuthenticationService.logout();
        logoutAction();

        history.push("/login");
        window.location.reload();
    }
    render()
    {
        return (
            <div className="App">
                <style>
                    @import url('https://fonts.googleapis.com/css2?family=Lato&display=swap');
                </style>
                <div className="upper-navbar">
                    <a className="logo-container" href="/"><img className='logo' src={logo} alt="logo_but" /></a>
                    <li className="upper-navbar-item"><a href="projects">Projects</a></li>
                    <li className="upper-navbar-item"><a href="detais">Details</a></li>
                    <li className="upper-navbar-item"><a href="summary">Summary</a></li>
                    <li className="upper-navbar-item-logout"><button onClick={this.handleLogout}><a href="/logout">Logout</a></button></li>
                </div>
                <hr></hr>
                <div className="main-body">
                    <ModelList />
                </div>
            </div>
        );
    }
}

const mapDispatchToProps = (dispatch: (arg0: ActionCreatorWithoutPayload<string>) => void) =>
{
    return {
        logoutAction: () => { dispatch(logoutAction); }
    }
}

const mapStateToProps = (state: any) =>
{
    return {
        stateValue: state
    };
};

export default connect(mapStateToProps, mapDispatchToProps)(Board);