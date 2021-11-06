import React from "react";
import Datasets from "../dataset/Datasets";
import Navbar from "../navigation/Navbar";

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