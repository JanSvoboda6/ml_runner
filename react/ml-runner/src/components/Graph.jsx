import React from 'react';
import { Line } from 'react-chartjs-2';



export default function Graph(props)
{
    const state = {
        labels: ['January', 'February', 'March',
            'April', 'May'],
        datasets: [
            {
                label: 'Red',
                fill: false,
                lineTension: 0.5,
                backgroundColor: props.backgroundColor,
                borderColor: props.backgroundColor,
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
