import React from "react";
import Datasets from "./Datasets";
import Navbar from "./Navbar";



function DatasetPage()
{
    return (
        <div>
            <Navbar start="start-at-datasets" />
            <Datasets />
        </div>
    )
}

export default DatasetPage;