<script setup>
    import { Bar } from 'vue-chartjs';
    import { Chart as ChartJS, Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale } from 'chart.js';
  
    ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale)
  
    const chartOptions = {
        indexAxis: 'y',
        responsive: true,
        plugins: {
            legend: {
                position: 'bottom'
            },
            title: {
                font: {
                    size: 18
                },
                display: true,
                text: 'Verbrauch nach Kanton',
                color: '#fff',
            }
        },
    }

    const chartData = {
        labels: ['AG', 'AI', 'AR', 'BE', 'BL', 'BS', 'FR', 'GE', 'GL', 'GR', 'JU', 'LU', 'NE', 'NW', 'OW', 'SG', 'SH', 'SO', 'SZ', 'TG', 'TI', 'UR', 'VD', 'VS', 'ZG', 'ZH'],
        datasets: [
            {
                label: 'Verbrauch in kw/h',
                backgroundColor: ['#71C76F'],
                data: [30, 20, 10, 40, 50, 30, 20, 10, 40, 50, 30, 20, 10, 40, 50, 30, 20, 10, 40, 50, 30, 20, 10, 40, 50, 30, 22]
            }
        ]
    }
  
    const height = 500;
</script>

<template>
    <Bar :height="height" :data="chartData" :options="chartOptions" />
</template>
  
<style scoped></style>
  