import {
    AnimatedAxis, // any of these can be non-animated equivalents
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
    { x: '2020-01-01', y: 30 },
    { x: '2020-01-02', y: 40 },
    { x: '2020-01-03', y: 80 },
    { x: '2020-01-04', y: 60 },
    { x: '2020-01-05', y: 75 },
    { x: '2020-01-06', y: 50 },
    { x: '2020-01-07', y: 40 },
    { x: '2020-01-08', y: 45 },
];

const data3 = [
    { x: '2020-01-01', y: 60 },
    { x: '2020-01-02', y: 90 },
    { x: '2020-01-03', y: 95 },
    { x: '2020-01-04', y: 90 },
    { x: '2020-01-05', y: 55 },
    { x: '2020-01-06', y: 65 },
    { x: '2020-01-07', y: 75 },
    { x: '2020-01-08', y: 80 },
];

const accessors = {
    xAccessor: d => d.x,
    yAccessor: d => d.y,
};

function SimpleChart()
{
    return (
        <XYChart height={400} width={1200} xScale={{type: 'band'}} yScale={{type: 'radial'}}>
            <AnimatedAxis orientation="bottom" />
            <AnimatedAxis orientation="left"/>
            <AnimatedGrid columns={true} numTicks={4}/>
            {/*<AnimatedLineSeries dataKey="Line 1" data={data1} {...accessors} />*/}
            {/*<AnimatedLineSeries dataKey="Line 2" data={data2} {...accessors} />*/}
            {/*<AnimatedLineSeries dataKey="Line 3" data={data3} {...accessors} />*/}
            <AreaSeries
                dataKey="Data 1"
                data={ data1 }
                fillOpacity={ 0.3 }
                fill="#d6e0f0"
                {...accessors}
            />
            <AreaSeries
                dataKey="Data 2"
                data={ data2 }
                {...accessors}
                fillOpacity={ 0.3}
            />
            <AreaSeries
                dataKey="Data 3"
                data={ data3 }
                {...accessors}
                fillOpacity={ 0.3 }
            />

            <Tooltip
                snapTooltipToDatumX
                snapTooltipToDatumY
                showVerticalCrosshair
                showSeriesGlyphs
                renderTooltip={({tooltipData, colorScale}) => (
                    <div>
                        <div style={{color: colorScale(tooltipData.nearestDatum.key)}}>
                            {tooltipData.nearestDatum.key}
                        </div>
                        {accessors.xAccessor(tooltipData.nearestDatum.datum)}
                        {', '}
                        {accessors.yAccessor(tooltipData.nearestDatum.datum)}
                    </div>
                )}
            />
        </XYChart>
    );
}

export default SimpleChart;