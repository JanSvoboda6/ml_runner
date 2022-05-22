import {AnimatedAxis, AnimatedGrid, AnimatedLineSeries, Tooltip, XYChart,} from '@visx/xychart';
import React from 'react';

export interface Data {
    x: number;
    y: number;
}

interface ChartProps {
    data: Data[]
}

const accessors = {
    xAccessor: d => d.x,
    yAccessor: d => d.y,
};

/**
 * Component used for rendering a chart.
 */
function Chart(props: ChartProps)
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