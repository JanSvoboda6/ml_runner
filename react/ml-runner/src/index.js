import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import { Provider } from 'react-redux';
import { combineReducers } from "redux";

import AuthenticationReducer from "./reducers/AuthenticationReducer";
import MessageReducer from "./reducers/MessageReducer";
import { BrowserRouter } from 'react-router-dom';
import store from './Store';

//export default combineReducers({ AuthenticationReducer, MessageReducer });

ReactDOM.render(
  <Provider store={ store }>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </Provider>,
  document.getElementById('root')
);
