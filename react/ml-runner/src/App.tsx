import React, { Component } from "react";
import { Router, Switch, Route, Link } from "react-router-dom";
import "./App.css";
import Login from "./components/LoginPage";
import Register from "./components/RegisterPage";
import Board from "./components/Board";
import { Logout as logout } from "./actions/Authentication";
import { history } from "./helpers/History";
import { User } from "./types";

interface AppProps
{
  dispatch: any,
  user: User
}

class App extends Component<AppProps, User>
{
  constructor(props: AppProps)
  {
    super(props);
    this.logOut = this.logOut.bind(this);
  }

  componentDidMount()
  {
    const user = this.props.user;

    if (user)
    {
      this.setState(user);
    }
  }

  logOut()
  {
    //this.props.dispatch(logout());
    logout();
  }

  render()
  {
    const user = this.state;

    return (
      <div>
        <style>
          @import url('https://fonts.googleapis.com/css2?family=Lato&display=swap');
        </style>
        < Router history={history} >
          <div className="navigation-page" >
            <Switch>
              <Route exact path={["/", "/home"]} component={Board} />
              <Route exact path="/login" component={Login} />
              <Route exact path="/register" component={Register} />
              <Route exact path="/logout" component={Login} />
            </Switch>
          </div>
        </Router>
      </div>
    );
  }
}
export default App;