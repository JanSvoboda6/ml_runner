import React from 'react';
import '../App.css';
import ModelList from '../project/ModelList';
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
                <ModelList />
            </div>
        </div>
    );
}
export default Board;