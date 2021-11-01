import React from 'react';
import { Line } from 'react-chartjs-2';

export default function Graph(props)
{
    let width, height, gradient;
    function getGradient(ctx, chartArea)
    {
        const chartWidth = chartArea.right - chartArea.left;
        const chartHeight = chartArea.bottom - chartArea.top;
        if (!gradient || width !== chartWidth || height !== chartHeight)
        {
            // Create the gradient because this is either the first render
            // or the size of the chart has changed
            width = chartWidth;
            height = chartHeight;
            gradient = ctx.createLinearGradient(0, chartArea.bottom, 0, chartArea.top);
            gradient.addColorStop(0, "rgba(58, 123, 213, 0.3)");
            gradient.addColorStop(1, "rgba(60, 120, 213, 0.7)");
        }

        return gradient;
    }

    const state = {
        labels: ['January', 'February', 'March',
            'April', 'May'],
        datasets: [
            {
                label: 'Random Chart',
                fill: true,
                lineTension: 0.5,
                // backgroundColor: props.backgroundColor,
                // borderColor: props.backgroundColor,
                backgroundColor: function (context)
                {
                    const chart = context.chart;
                    const { ctx, chartArea } = chart;

                    if (!chartArea)
                    {
                        return;
                    }
                    return getGradient(ctx, chartArea);
                },

                borderColor: function (context)
                {
                    const chart = context.chart;
                    const { ctx, chartArea } = chart;

                    if (!chartArea)
                    {
                        return;
                    }
                    return getGradient(ctx, chartArea);
                },
                borderWidth: 2,
                data: [1, 4, 3, 3, 5]
            }
        ]
    }

    return (
        <div>
            <Line
                data={ state }
                height={ 1 }
                width={ 2 }
                options={ {
                    responsive: true,
                    interaction: {
                        intersect: false,
                    },
                    title: {
                        display: true,
                        text: 'Average Rainfall per month',
                        fontSize: 10
                    },
                    legend: {
                        display: true,
                        position: 'right'
                    }
                } }
            />
        </div>
    );
}
