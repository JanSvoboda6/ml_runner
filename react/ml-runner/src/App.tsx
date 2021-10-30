import React, { Component } from "react";
import { Router, Switch, Route, Link } from "react-router-dom";
import "./App.css";
import Login from "./components/LoginPage";
import Register from "./components/RegisterPage";
import Board from "./components/Board";
//import { Logout as logout } from "./actions/Authentication";
import { history } from "./helpers/History";
import { User } from "./types";
import Project from "./components/Project";
import Navbar from "./components/Navbar";
import Summary from "./components/Summary";

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
  }

  componentDidMount()
  {
    const user = this.props.user;

    if (user)
    {
      this.setState(user);
    }
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
              <Route exact path="/projects" component={Board} />
              <Route exact path="/summary" component={Summary} />
              <Route exact path="/login" component={Login} />
              <Route exact path="/register" component={Register} />
              <Route exact path="/logout" component={Login} />
              <Route exact path="/newproject" component={Project} />
            </Switch>
          </div>
        </Router>
        <footer>
          <p>made with ♥️ by jan</p>
        </footer>
      </div>
    );
  }
}
export default App;