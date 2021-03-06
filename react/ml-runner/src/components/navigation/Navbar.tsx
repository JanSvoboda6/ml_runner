import React from "react";
import { useDispatch } from "react-redux";
import { Link } from "react-router-dom";
import LogoutService from "../../services/LogoutService";
import logo from '../../styles/vut_simple_logo.png'

/**
 * Navigation bar component.
 */
function Navbar()
{
    const dispatch = useDispatch();

    const handleLogout = () =>
    {
        LogoutService.logout(dispatch);
        window.location.reload();
    }
    return (
        <div className="wrapper">
            <nav className="upper-navbar">
                <a className="logo-container" href="/"><img className='logo-simple' src={logo} alt="logo_but" /></a>
                <Link to="/projects" className="upper-navbar-item" >Projects</Link>
                <Link to="/datasets" className="upper-navbar-item">Datasets</Link>
                <Link to="/newproject" className="upper-navbar-item">New Project</Link>
                <a className="upper-navbar-item-logout"><button className="upper-navbar-logout-button" onClick={handleLogout}><Link to="/logout">Logout</Link></button></a>
            </nav>
        </div >)
}

export default Navbar;