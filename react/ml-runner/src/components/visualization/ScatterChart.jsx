import React from 'react';
import { ReactBurgerMenu } from 'react-burger-menu';
import { Scatter } from 'react-chartjs-2';

export default function ScatterGraph(props)
{
    const state = {
        labels: ['January', 'February', 'March',
            'April', 'May'],
        datasets: [
            {
                label: 'Random Chart',
                backgroundColor: 'rgba(60, 120, 213, 0.4)',
                borderColor: 'rgba(60, 120, 213, 0.4)',
                borderWidth: 2,


                // data: [{
                //     x: -10,
                //     y: 0
                // }, {
                //     x: 0,
                //     y: 10
                // }, {
                //     x: 10,
                //     y: 5
                // }, {
                //     x: 0.5,
                //     y: 5.5
                // }],

                data: () =>
                {
                    let dataPoints = [];
                    props.runner.forEach(runner =>
                    {
                        dataPoints.push({ x: runner.gamma, y: runner.c });
                    })

                    console.log(dataPoints);
                    return dataPoints;
                }
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
                    responsive: true,
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
