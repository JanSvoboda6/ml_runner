import React, { Component } from "react";
import { connect } from "react-redux";
import { Router, Switch, Route, Link } from "react-router-dom";

import "./App.css";

import Login from "./components/LoginPage";
import Register from "./components/RegisterPage";
import Board from "./components/Board";

import { logout } from "./actions/Authentication";
import { clearMessage } from "./actions/Message";

import { history } from './helpers/History';

class App extends Component
{
  constructor(props)
  {
    super(props);
    this.logOut = this.logOut.bind(this);

    this.state = {
      currentUser: undefined,
    };

    history.listen((location) =>
    {
      props.dispatch(clearMessage());
    });
  }

  componentDidMount()
  {
    const user = this.props.user;

    if (user)
    {
      this.setState({
        currentUser: user,
        showModeratorBoard: user.roles.includes("ROLE_MODERATOR"),
        showAdminBoard: user.roles.includes("ROLE_ADMIN"),
      });
    }
  }

  logOut()
  {
    this.props.dispatch(logout());
  }

  render()
  {
    const { currentUser, showModeratorBoard, showAdminBoard } = this.state;

    return (
      <div>
        <style>
          @import url('https://fonts.googleapis.com/css2?family=Lato&display=swap');
        </style>
        <Router history={ history }>
          <div className="navigation-page">
            <Switch>
              <Route exact path={ ["/", "/home"] } component={ Board } />
              <Route exact path="/login" component={ Login } />
              <Route exact path="/register" component={ Register } />
              <Route exact path="/logout" component={ Login } />
            </Switch>
          </div>
        </Router>
      </div>
    );
  }
}

function mapStateToProps(state)
{
  const { user } = state.auth;
  return {
    user,
  };
}

export default connect(mapStateToProps)(App);
