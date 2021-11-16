import React from 'react';
import { ReactBurgerMenu } from 'react-burger-menu';
import { Scatter } from 'react-chartjs-2';

export default function ScatterGraph(props)
{

    // function computeDataPoints(ctx, chartArea)
    // {
    //     let dataPoints = [];
    //     props.runner.forEach(runner =>
    //     {
    //         dataPoints.push({ x: runner.gamma, y: runner.c });
    //     })
    //     console.log(dataPoints);
    //     chartArea.data(dataPoints);
    //     return dataPoints;
    // }

    const state = {

        datasets: [
            {
                label: 'Gamma/C Parameter Plot',
                backgroundColor: 'rgba(60, 120, 213, 0.9)',
                borderColor: 'rgba(60, 120, 213, 0.9)',
                borderWidth: 2,

                data: [{
                    x: 1,
                    y: 0.3
                }, {
                    x: 0.9,
                    y: 0.8
                }, {
                    x: 0.7,
                    y: 0.5
                }],

                // data: function (context)
                // {
                //     const chart = context.chart;
                //     const { ctx, chartArea } = chart;

                //     if (!chartArea)
                //     {
                //         return;
                //     }
                //     return computeDataPoints(ctx, chartArea)

                // }
            }
        ]
    }

    return (
        <div>
            <Scatter
                data={ state }
                type='scatter'
                height={ 1 }
                width={ 2 }
                options={ {
                    plugins: {
                        legend: {
                            position: 'top',
                        },
                        title: {
                            display: true,
                            text: 'GAMMA/C PARAMETER SCATTER PLOT',
                            font: {
                                size: 25
                            }
                        }
                    },
                    scales:
                    {
                        x:
                        {
                            title:
                            {
                                display: true,
                                text: 'Gamma',
                                font: {
                                    size: 20
                                },
                            },

                        },
                        y:
                        {
                            title:
                            {
                                display: true,
                                text: 'C',
                                font: {
                                    size: 20
                                },
                            }
                        }
                    }
                } }

            />
        </div>
    );
}
