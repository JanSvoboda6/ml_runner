import {Bin, Bins} from "../Analysis";

/**
 * Provides constructed bin values for heatmap component.
 */
const constructBins = (values: number[][]) => {
    let bins = new Array<Bins>();
    for(let i = 0; i < values.length; i++)
    {
        let column = new Array<Bin>();
        for(let j = 0; j < values[i].length; j++)
        {
            column.push({"count": values[i][j]});
        }
        bins.push({"bins": column})
    }
    return bins;
}

export default {constructHeatMapBins: constructBins};