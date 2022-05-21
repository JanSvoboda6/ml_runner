import {
    AnimatedAxis,
    AnimatedGrid,
    AnimatedLineSeries,
    XYChart,
    Tooltip, AreaSeries,
} from '@visx/xychart';
import React from 'react';

const data1 = [
    { x: '2020-01-01', y: 50 },
    { x: '2020-01-02', y: 10 },
    { x: '2020-01-03', y: 20 },
    { x: '2020-01-04', y: 55 },
    { x: '2020-01-05', y: 20 },
    { x: '2020-01-06', y: 25 },
    { x: '2020-01-07', y: 35 },
    { x: '2020-01-08', y: 40 },
];

const data2 = [
    { x: '1', y: 77 },
    { x: '2', y: 76 },
    { x: '3', y: 79 },
    { x: '4', y: 73 },
    { x: '5', y: 75 },
    { x: '6', y: 89 },
    { x: '7', y: 87 },
    { x: '8', y: 79 },
    { x: '9', y: 83 },
    { x: '10', y: 90 },
    { x: '11', y: 91 },
    { x: '13', y: 87 },
    { x: '14', y: 86 },
];


export interface Data {
    x: number;
    y: number;
}

interface Props {
    data: Data[]
}

const accessors = {
    xAccessor: d => d.x,
    yAccessor: d => d.y,
};

function Chart(props: Props)
{
    return (
        <XYChart height={400} width={1200} xScale={{type: 'band'}} yScale={{type: 'radial'}}>
            <AnimatedGrid columns={true} numTicks={10} rows={true} lineStyle={{stroke: "rgb(80,80,80)"}}/>
            <AnimatedAxis
                orientation="bottom"
                label={"Id [-]"}
                labelProps={{
                    y: 35,
                    fill: '#fff',
                    fontSize: 15,
                    strokeWidth: 1,
                    stroke: '#fff',
                    opacity: 0.9,
                    paintOrder: 'stroke',
                    textAnchor: 'middle',
                }}
                tickLabelProps={() => ({
                    fill: 'white',
                    fontSize: 13,
                    textAnchor: 'middle',
                })}
            />
            <AnimatedAxis
                orientation="left"
                label={"Accuracy [%]"}
                labelProps={{
                    y: 20,
                    fill: '#fff',
                    fontSize: 15,
                    strokeWidth: 1,
                    stroke: '#fff',
                    opacity: 0.9,
                    paintOrder: 'stroke',
                    textAnchor: 'middle',

                }}
                tickLabelProps={() => ({
                    fill: 'white',
                    fontSize: 13,
                    textAnchor: 'middle',
                    x: -15
                })}
            />
            <AnimatedLineSeries dataKey="Accuracy"
                                {...accessors}
                                data={ props.data }
                                fillOpacity={ 0.6 }
                                stroke={"rgb(250,210,150)"}
                                strokeWidth={2}
                                />

            <Tooltip
                snapTooltipToDatumX
                snapTooltipToDatumY
                showVerticalCrosshair
                showSeriesGlyphs
                renderTooltip={({tooltipData, colorScale}) => (
                    <div>
                        <div style={{color: colorScale ? colorScale(tooltipData?.nearestDatum?.key as string) : 'inherit'}}>
                            {tooltipData?.nearestDatum?.key}
                        </div>
                        <div>Id: {accessors.xAccessor(tooltipData?.nearestDatum?.datum)}</div>
                        <div>Accuracy: {accessors.yAccessor(tooltipData?.nearestDatum?.datum)}%</div>
                    </div>
                )}
            />
        </XYChart>
    );
}

export default Chart;