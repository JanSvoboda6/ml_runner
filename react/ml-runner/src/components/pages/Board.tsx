import React from 'react';
import '../../App.css';
import ProjectList from '../project/ProjectList';
import Navbar from '../navigation/Navbar';

/**
 * Board page rendering a list of projects.
 */
function Board()
{
    return (
        <div className="App">
            <style>
                @import url('https://fonts.googleapis.com/css2?family=Lato&display=swap');
            </style>
            <Navbar/>
            <div className="main-body">
                <ProjectList />
            </div>
        </div>
    );
}
export default Board;