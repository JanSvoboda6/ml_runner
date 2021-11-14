import React from 'react';
import '../../App.css';
import ProjectList from '../project/ProjectList';
import Navbar from '../navigation/Navbar';

function Board()
{
    return (
        <div className="App">
            <style>
                @import url('https://fonts.googleapis.com/css2?family=Lato&display=swap');
            </style>
            <Navbar start="start-at-projects" />
            <div className="main-body">
                <ProjectList />
            </div>
        </div>
    );
}
export default Board;