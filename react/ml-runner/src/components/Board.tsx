import React from 'react';
import '../App.css';
import ModelList from './ModelList';
import logo from '../styles/logo_but_text.png'
import LogoutService from '../services/LogoutService';
import { useHistory } from 'react-router';
import { useDispatch } from 'react-redux';
import { Link } from 'react-router-dom';

function Board()
{
    const history = useHistory();
    const dispatch = useDispatch();

    const handleLogout = (e: { preventDefault: () => void; }) =>
    {
        e.preventDefault();

        LogoutService(dispatch);

        history.push("/login");
        window.location.reload();
    }

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
                <li className="upper-navbar-item-logout"><button className="upper-navbar-logout-button" onClick={handleLogout}><Link to="/logout">Logout</Link></button></li>
            </div>
            <hr></hr>
            <div className="main-body">
                <ModelList />
            </div>
        </div>
    );
}

export default Board;