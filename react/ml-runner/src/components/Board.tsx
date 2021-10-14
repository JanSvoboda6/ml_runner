import React, { Component } from 'react';
import { connect } from 'react-redux';
import '../App.css';
import ModelList from '../ModelList';
import { logout } from "../actions/Authentication";
import logo from '../styles/logo_but_text.png'

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
        const { dispatch, history } = this.props;
        e.preventDefault();
        dispatch(logout());
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

export default connect(null, null)(Board)