// import React from 'react';
// import { Route, Redirect } from 'react-router-dom';
// import store from '../redux/store';

// const PrivateRoute = ({component: Component, ...rest}) => {
//     return (
//         const state = store.getState();
//         // Show the component only when the user is logged in
//         // Otherwise, redirect the user to /signin page
//         <Route {...rest} render={props => (
//             state.isLoggedIn ?
//                 <Component {...props} />
//             : <Redirect to="/signin" />
//         )} />
//     );
// };

// export default PrivateRoute;