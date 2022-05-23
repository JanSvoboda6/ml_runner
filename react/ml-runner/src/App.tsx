import React, {Component} from "react";
import {Route, Router, Switch} from "react-router-dom";
import "./App.css";
import Login from "./components/pages/LoginPage";
import Register from "./components/pages/RegisterPage";
import Board from "./components/pages/Board";
import {history} from "./helpers/History";
import {User} from "./helpers/types";
import Project from "./components/pages/Project";
import DatasetPage from "./components/pages/DatasetPage";
import EnvironmentPreparation from "./components/pages/EnvironmentPreparation";
import PrivateRoute from "./helpers/PrivateRoute";
import Analysis from "./components/analysis/Analysis";
import RunnerResultPage from "./components/project/RunnerResultPage";

interface AppProps
{
  dispatch: any,
  user: User
}

/**
 * Main component.
 */
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
    return (
      <div>
        <style>
          @import url('https://fonts.googleapis.com/css2?family=Lato&display=swap');
        </style>
        <Router history={history} >
          <div className="navigation-page" >
            <Switch>
              <PrivateRoute exact path={["/", "/home"]} component={Board} />
              <Route exact path="/login" component={Login} />
              <Route exact path="/register" component={Register} />
              <Route exact path="/logout" component={Login} />
              <PrivateRoute exact path="/projects" component={Board} />
              <PrivateRoute exact path="/datasets" component={DatasetPage} />
              <PrivateRoute exact path="/newproject" component={Project} />
              <PrivateRoute exact path="/preparing" component={EnvironmentPreparation} />
              <PrivateRoute exact path="/analysis" component={Analysis} />
              <PrivateRoute exact path="/runner/result" component={RunnerResultPage} />
            </Switch>
          </div>
        </Router>
        <footer>
          <p>made by jan</p>
        </footer>
      </div>
    );
  }
}
export default App;