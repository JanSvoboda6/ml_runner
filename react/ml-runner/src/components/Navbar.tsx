import React from "react";
import { useDispatch } from "react-redux";
import { Link, useHistory } from "react-router-dom";
import LogoutService from "../services/LogoutService";
// import logo from '../styles/logo_but_text.png'
import dots from '../styles/logo_dots_new.svg'

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

    return (
        <div className="wrapper">
            <nav className="upper-navbar">
                <a className="logo-container" href="/"><img className='logo-dots' src={dots} alt="logo_but" /></a>
                <a className="upper-navbar-item"><Link to="/projects">Projects</Link></a>
                <a className="upper-navbar-item"><Link to="/summary">Summary</Link></a>
                <a className="upper-navbar-item"><Link to="/newproject">Add New Project</Link></a>
                <a className="upper-navbar-item-logout"><button className="upper-navbar-logout-button" onClick={handleLogout}><Link to="/logout">Logout</Link></button></a>
                <div className="start-at-projects upper-navbar-animation"></div>
            </nav>
        </div>)
}

export default Navbar;