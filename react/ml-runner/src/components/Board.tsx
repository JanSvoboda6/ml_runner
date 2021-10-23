import React from 'react';
import '../App.css';
import ModelList from './ModelList';
import Navbar from './Navbar';

function Board()
{
    return (
        <div className="App">
            <style>
                @import url('https://fonts.googleapis.com/css2?family=Lato&display=swap');
            </style>
            <Navbar />
            <div className="main-body">
                <ModelList />
            </div>
        </div>
    );
}

export default Board;