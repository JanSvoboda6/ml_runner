import React, { useState } from 'react';
import { Group } from '@visx/group';
import genBins, { Bin, Bins } from '@visx/mock-data/lib/generators/genBins';
import { scaleLinear } from '@visx/scale';
import { AxisBottom, AxisLeft, AxisTop } from '@visx/axis';
import { HeatmapCircle, HeatmapRect } from '@visx/heatmap';
import { getSeededRandom } from '@visx/mock-data';
import { Axis } from '@visx/axis';
import DefaultAxis from '../analysis/DefaultAxis';
import { FunctionTypeNode } from 'typescript';
import {Grid, GridColumns, GridRows} from "@visx/grid";


// const hot1 = '#77312f';
// const hot2 = '#f33d15';
const hot1 = '#122549';
const hot2 = '#b4fbde';
export const background = '#221c1c';

const binData = genBins(/* length = */ 10, /* height = */ 10);

function max<Datum>(data: Datum[], value: (d: Datum) => number): number
{
    return Math.max(...data.map(value));
}

function min<Datum>(data: Datum[], value: (d: Datum) => number): number
{
    return Math.min(...data.map(value));
}

const bins = (d: Bins) => d.bins;
const count = (d: Bin) => d.count;

const colorMax = max(binData, d => max(bins(d), count));
const bucketSizeMax = max(binData, d => bins(d).length);

const xScale = scaleLinear < number > ({
    domain: [0, binData.length],
});

const axisScale = scaleLinear < number > ({
    domain: [0, binData.length],
});

const yScale = scaleLinear < number > ({
    domain: [0, bucketSizeMax],
});
const circleColorScale = scaleLinear < string > ({
    range: [hot1, hot2],
    domain: [0, colorMax],
});
const rectColorScale = scaleLinear < string > ({
    range: [hot1, hot2],
    domain: [0, colorMax],
});
const opacityScale = scaleLinear < number > ({
    range: [1, 1],
    domain: [0, colorMax],
});

export type HeatmapProps = {
    width: number;
    height: number;
    margin?: { top: number; right: number; bottom: number; left: number };
    separation?: number;
    events?: boolean;
};

const defaultMargin = { top: 50, left: 50, right: 50, bottom: 50 };

export default ({
    width,
    height,
    events = false,
    margin = defaultMargin,
    separation = 50
}: HeatmapProps) =>
{
    const[valueOfSelectedBin, setValueOfSelectedBin] = useState(0);

    const onBinClick = (bin: any) =>
    {
        console.log(bin.bin.count);
        setValueOfSelectedBin(bin.bin.count);
    };
    // bounds
    const size = 500;
    const xMax = size - margin.bottom - margin.top;
    const yMax = size - margin.bottom - margin.top;

    const binWidth = xMax / binData.length;
    const binHeight = yMax / bucketSizeMax;
    const radius = min([binWidth, binHeight], d => d) / 2;

    xScale.range([0, xMax]);
    axisScale.range([0, xMax]);
    yScale.range([yMax, 0]);

    const formatXAxis = (tickItem) => {
        return tickItem.toString();
    }
    return width < 10 ? null : (
        <div className='heatmap-box'>
            <p className='selected-value'>Selected Value: {valueOfSelectedBin.toFixed(3)}&#37;</p>
        <svg width={ width } height={ height }>
           
            <g width={width/2} height={height/2} transform={'translate(220, -20)'}>
                <HeatmapRect
                    data={ binData }
                    xScale={ xScale }
                    yScale={ yScale }
                    colorScale={ rectColorScale }
                    opacityScale={ opacityScale }
                    binWidth={ binWidth }
                    binHeight={ binWidth }
                    gap={ 5 }
                >
                    { heatmap =>
                        heatmap.map(heatmapBins =>
                            heatmapBins.map(bin => (
                                <rect
                                    key={ `heatmap-rect-${ bin.row }-${ bin.column }` }
                                    className="vx-heatmap-rect heatmap-bin"
                                    width={ bin.width }
                                    height={ bin.height }
                                    x={ bin.x }
                                    y={ bin.y }
                                    fill={ bin.color }
                                    fillOpacity={ bin.opacity }
                                    onClick={ () =>
                                    {
                                        //if (!events) return;
                                        const { row, column } = bin;
                                        if(bin.bin)
                                        {                 
                                            onBinClick(bin);
                                        }            
                                        console.log(JSON.stringify({ row, column, bin: bin.bin }));
                                    } }
                                />
                            )),
                        )
                    }
                </HeatmapRect>
                {/*<Grid*/}
                {/*    top={margin.top}*/}
                {/*    left={margin.left}*/}
                {/*    xScale={xScale}*/}
                {/*    yScale={yScale}*/}
                {/*    width={xMax}*/}
                {/*    height={yMax}*/}
                {/*    stroke="black"*/}
                {/*    strokeOpacity={0.1}*/}
                {/*    xOffset={0}*/}
                {/*/>*/}

                {/*<GridRows scale={xScale} width={xMax} height={yMax} stroke="#e0e0e0" />*/}
                {/*<GridColumns scale={yScale} width={xMax} height={yMax} stroke="#e0e0e0" />*/}
            </g>
            <g transform={'translate(' + width/4 + ', 0)'}>
            <AxisTop     top={20}
                            scale={axisScale}
                            tickValues={[1,2,3,4,5,6,7,8,9,10]}
                            tickFormat={formatXAxis}
                            tickLabelProps={() => ({
                                fill: 'white',
                                fontSize: 11,
                                textAnchor: 'middle',
                            })}/>
            <AxisLeft
                scale={axisScale}
                tickFormat={formatXAxis}
                tickLabelProps={() => ({
                    fill: 'white',
                    fontSize: 11,
                    textAnchor: 'middle',
                })}/>
            </g>
            {/*<g transform={'translate(21, -110)'}>*/}
            {/*</g>*/}
            <g transform={'translate(50, -113)'}>
            </g>
        </svg>
        </div>
    );
};