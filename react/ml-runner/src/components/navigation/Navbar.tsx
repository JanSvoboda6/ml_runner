import React from "react";
import { useDispatch } from "react-redux";
import { Link, useHistory } from "react-router-dom";
import LogoutService from "../../services/LogoutService";
import logo from '../../styles/vut_simple_logo.png'

function Navbar(props: any)
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
                <a className="logo-container" href="/"><img className='logo-simple' src={logo} alt="logo_but" /></a>
                <Link to="/projects" className="upper-navbar-item" >Projects</Link>
                <Link to="/datasets" className="upper-navbar-item">Datasets</Link>
                <Link to="/summary" className="upper-navbar-item">Summary</Link>
                <Link to="/newproject" className="upper-navbar-item">Add New Project</Link>
                <a className="upper-navbar-item-logout"><button className="upper-navbar-logout-button" onClick={handleLogout}><Link to="/logout">Logout</Link></button></a>
                {/* <div className={"upper-navbar-animation" + " " + props.start}></div> */}
            </nav>
        </div >)
}

export default Navbar;