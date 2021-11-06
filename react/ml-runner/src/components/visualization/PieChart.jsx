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
                                'rgba(60, 120, 213, 0.4)',
                                'rgba(60, 120, 213, 0.6)',
                                'rgba(60, 120, 213, 0.8)',
                                'rgba(60, 120, 213, 0.9)'
                            ],
                            borderColor: [
                                'rgba(255, 255, 255, 1)',
                            ],
                            borderWidth: 5,
                        },
                    ],
                } }
                height={ 300 }
                width={ 300 }
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