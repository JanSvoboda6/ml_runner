import { combineReducers } from "redux";
import auth from "./AuthenticationReducer";
import message from "./MessageReducer";

export default combineReducers({
    auth,
    message,
});