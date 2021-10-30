import React from "react";
import Navbar from "./Navbar";
import BarChart from "./BarChart";
import Graph from "./Graph";

function Summary()
{
    return (
        <div>
            <Navbar />
            <div className="summary-list">
                <div className="summary-list-item"> <BarChart /></div>
                <div className="summary-list-item"> <BarChart /></div>
                <div className="summary-list-item"> <Graph backgroundColor='rgba(229, 81, 116, 0.9)' /></div>
                <div className="summary-list-item"> <Graph backgroundColor='rgba(112, 200, 116, 0.9)' /></div>
            </div>
        </div>
    )
}

export default Summary;