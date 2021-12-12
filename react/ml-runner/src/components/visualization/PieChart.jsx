import React from 'react'
import { Pie } from 'react-chartjs-2'

const PieChart = () =>
{
    return (
        <div>
            <Pie
                data={ {
                    labels: ['Blue', 'Mid Blue', 'Upper Mid Blue', 'Dark Blue'],
                    datasets: [
                        {
                            label: 'Random pie',
                            data: [3, 7, 5, 4],
                            backgroundColor: [
                                'rgba(250, 236, 160, 0.8)',
                                'rgba(156, 200, 190, 0.8)',
                                'rgba(100, 200, 200, 0.8)',
                                'rgba(255, 255, 255, 0.8)'
                            ],
                            borderColor: [
                                'rgba(18, 22, 25, 1)',
                            ],
                            borderWidth: 2,
                        },
                    ],
                } }
                height={ 250 }
                width={ 250 }
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

export default PieChart