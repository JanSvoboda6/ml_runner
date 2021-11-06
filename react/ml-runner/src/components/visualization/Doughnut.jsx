import React from 'react'
import { Doughnut as Dough } from 'react-chartjs-2'

const Doughnut = () =>
{
    return (
        <div>
            <Dough
                data={ {
                    datasets: [
                        {
                            data: [12, 19, 15],
                            backgroundColor: [
                                'rgba(113, 228, 170, 0.9)',
                                'rgba(133, 38, 201, 0.9)',
                                'rgba(201, 38, 76, 0.9)',
                            ],
                            borderColor: [
                                'rgba(255, 99, 132, 1)',
                                'rgba(54, 162, 235, 1)',
                            ],
                            borderWidth: 0,
                        },
                        // {
                        //   label: 'Quantity',
                        //   data: [47, 52, 67, 58, 9, 50],
                        //   backgroundColor: 'orange',
                        //   borderColor: 'red',
                        // },
                    ],
                } }
                height={ 200 }
                width={ 200 }
                options={ {
                    maintainAspectRatio: false,
                    scales: {
                        yAxes: [
                            {
                                ticks: {
                                    beginAtZero: true,
                                },
                            },
                        ],
                    },
                } }
            />
        </div>
    )
}

export default Doughnut;