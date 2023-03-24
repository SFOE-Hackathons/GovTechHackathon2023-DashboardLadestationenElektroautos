<script setup>
    import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js'
    import { onMounted, ref } from 'vue';
    import { Line } from 'vue-chartjs'

    ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);

    let chartData = ref(null);
    let chartOptions = ref(null);

    let fetchChartData = (async () => {
        let fetchLabels = await fetch('https://govtech-poc.test:31001/rest/histogram/month');
        let fetchedData = await fetchLabels.json();

        chartData.value = {
            labels: fetchedData.map(x => x.date),
            datasets: [
                {
                    label: 'Verbrauch in kwh',
                    backgroundColor: '#fff',
                    borderColor: '#fff',
                    data: fetchedData,
                    parsing: {
                        yAxisKey: 'value',
                        xAxisKey: 'date'
                    }
                }
            ]
        }
    })

    onMounted(async () => {
        await fetchChartData();

        chartOptions.value = {
            responsive: true,
            retainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            },
        }

        setInterval(fetchChartData, 4000)
    });
</script>


<template>
    <h1 class="mb-8 text-lg">Stromverbrauch der letzten 10 Tage in kwh</h1>
    <Line v-if="chartData" :data="chartData" :options="chartOptions" />
</template>