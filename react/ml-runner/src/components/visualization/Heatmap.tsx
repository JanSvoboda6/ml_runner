import React, {useState} from 'react';
import {scaleLinear} from '@visx/scale';
import {AxisLeft, AxisTop} from '@visx/axis';
import {HeatmapRect} from '@visx/heatmap';
import {Bins} from "../analysis/Analysis"
import Gradient from "javascript-color-gradient";


export type HeatmapProps = {
    bins: Bins[];
    tickValuesX: Array<string>;
    tickValuesY: Array<string>;
    width: number;
    height: number;
    margin?: { top: number; right: number; bottom: number; left: number };
    xAxisLabel: string;
    yAxisLabel: string;
    separation?: number;
    events?: boolean;
};

const defaultMargin = { top: 50, left: 50, right: 50, bottom: 50 };

const gradientArray = new Gradient().setColorGradient("#122549", "#b4fbde").setMidpoint(51).getColors();

export default ({
    bins,
    tickValuesX,
    tickValuesY,
    width,
    height,
    margin = defaultMargin,
    xAxisLabel,
    yAxisLabel,
}: HeatmapProps) =>
{
    const[valueOfSelectedBin, setValueOfSelectedBin] = useState("-");

    const onBinClick = (bin: any) =>
    {
        if(bin.bin.count === undefined)
        {
            setValueOfSelectedBin("-");
            return;
        }
        const percentageValue = bin.bin.count * 100;
        setValueOfSelectedBin(percentageValue.toFixed(2).toString() + "%");
    };

    const size = 510;
    const xMax = size - margin.bottom - margin.top;
    const yMax = size - margin.bottom - margin.top;
    const gap = 5;

    const binWidth = xMax / bins.length;
    const binHeight = yMax / bins[0].bins.length;

    const xScale = scaleLinear < number > ({
        domain: [0, bins.length],
    });

    const yScale = scaleLinear < number > ({
        domain: [0,  bins[0].bins.length],
    });

    const xAxisScale = scaleLinear < number > ({
        domain: [0, bins.length - 1],
    });

    const yAxisScale = scaleLinear < number > ({
        domain: [0, bins[0].bins.length - 1],
    });

    const xTicksIndexes = [...Array(bins.length)].map((_,i) => i);
    const yTicksIndexes = [...Array(bins[0].bins.length)].map((_,i) => i);

    xScale.range([0, xMax]);
    yScale.range([0, yMax]);
    xAxisScale.range([0, xMax - binWidth]);
    yAxisScale.range([0, yMax - binHeight]);

    const formatXAxis = (tickItem) => {
        return tickValuesX[tickItem];
    }

    const offset = 90;

    const formatYAxis = (tickItem) => {
    return tickValuesY[tickItem];
    }

    const getColorBasedOnCountValue = (count: number | null | undefined) => {
        if(count === undefined || count === null || count <= 0.5)
        {
            return gradientArray[0];
        }
        return gradientArray[((count*100) - 50).toFixed()];
    }

    return width < 10 ? null : (
        <div className='heatmap-box'>
        <svg width={ width } height={ height }>
            <g width={width/2} height={height/2} transform={'translate(' + width/4 + ',' + offset + ')'}>
                <HeatmapRect
                    data={ bins }
                    xScale={ xScale }
                    yScale={ yScale }
                    binWidth={ binWidth }
                    binHeight={ binHeight }
                    gap={ gap } >
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
                                    fill={getColorBasedOnCountValue(bin.count)}
                                    fillOpacity={ 0.8}
                                    onClick={ () =>
                                    {
                                        if(bin.bin)
                                        {                 
                                            onBinClick(bin);
                                        }
                                    } }
                                />
                            )),
                        )
                    }
                </HeatmapRect>
            </g>
            <g transform={'translate(' + (width/4 + binWidth/2) +', -5)'} >
            <AxisTop
                top={offset}
                scale={xAxisScale}
                tickValues={xTicksIndexes}
                tickFormat={formatXAxis}
                strokeWidth={1}
                stroke={'#fff'}
                tickStroke={'#fff'}
                label={xAxisLabel}
                labelProps={{
                    y: -60,
                    fill: '#fff',
                    fontSize: 15,
                    strokeWidth: 1,
                    stroke: '#fff',
                    paintOrder: 'stroke',
                    textAnchor: 'middle',
                }}
                tickLabelProps={() => ({
                    fill: 'white',
                    fontSize: 13,
                    textAnchor: 'start',
                    y: -15,
                    angle: -90
                })}/>
            </g>

            <g transform={'translate('+ (width/4) + ',' + (binHeight/2 + offset) + ')'}>
            <AxisLeft
                left={-10}
                scale={yAxisScale}
                tickValues={yTicksIndexes}
                tickFormat={formatYAxis}
                strokeWidth={1}
                stroke={'#fff'}
                label={yAxisLabel}
                labelProps={{
                    fill: '#fff',
                    fontSize: 15,
                    stroke: '#fff',
                    strokeWidth: 1,
                    textAnchor: 'middle',
                    y: -60
                }}
                tickStroke={'#fff'}
                tickLabelProps={() => ({
                    fill: 'white',
                    fontSize: 13,
                    textAnchor: 'middle',
                    x: -30
                })}/>
            </g>
        </svg>
            <p className='selected-value'>Selected Accuracy: {valueOfSelectedBin}</p>
        </div>
    );
};