import React from "react";
import { useDispatch } from "react-redux";
import { Link, useHistory } from "react-router-dom";
import LogoutService from "../services/LogoutService";
import logo from '../styles/logo_but_text.png'

function Navbar()
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

    return (<div className="navbar-wrapper">
        <div className="upper-navbar">
            <a className="logo-container" href="/"><img className='logo' src={logo} alt="logo_but" /></a>
            <li className="upper-navbar-item"><Link to="/projects">Projects</Link></li>
            <li className="upper-navbar-item"><Link to="/details">Details</Link></li>
            <li className="upper-navbar-item"><Link to="/summary">Summary</Link></li>
            <li className="upper-navbar-item"><Link to="/newproject">Add New Project</Link></li>
            <li className="upper-navbar-item-logout"><button className="upper-navbar-logout-button" onClick={handleLogout}><Link to="/logout">Logout</Link></button></li>
        </div>
    </div>)
}

export default Navbar;