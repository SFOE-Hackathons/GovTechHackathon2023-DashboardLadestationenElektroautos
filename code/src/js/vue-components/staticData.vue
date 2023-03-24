<script setup>
    import { ref, onMounted, computed } from 'vue';

    let powerYesterday = ref('0');
    let powerToday = ref('0');
    let occupiedChargers = ref('0');
    let total_chargers = ref('0');

    let fetchStats = (async () => {
        let fetchedData = await fetch('https://govtech-poc.test:31001/rest/dashboard');
        let data = await fetchedData.json();

        powerYesterday.value = data.total_energy_yesterday.toFixed();
        powerToday.value = data.total_power.toFixed();
        occupiedChargers.value = data.occupied_chargers.toFixed();
        total_chargers.value = data.total_chargers.toFixed();
    })

    onMounted(async () => {
        await fetchStats();
        setInterval(fetchStats, 4000);
    });
</script>

<template>
    <div class="py-12 bg-neutral-800">
        <div class="px-6 mx-auto max-w-7xl lg:px-8">
            <dl class="grid grid-cols-1 text-center gap-y-16 gap-x-8 lg:grid-cols-3">
                <div class="flex flex-col max-w-xs mx-auto gap-y-4">
                    <dt class="text-base leading-7 text-neutral-400">Stromverbrauch vorheriger Tag</dt>
                    <dd class="order-first text-3xl font-semibold tracking-tight text-white sm:text-5xl"><strong v-text="(powerYesterday / 1000).toFixed(1)"></strong> MWh</dd>
                </div>

                <div class="flex flex-col max-w-xs mx-auto gap-y-4">
                    <dt class="text-base leading-7 text-neutral-400">Aktuelle Ladeleistung</dt>
                    <dd class="order-first text-3xl font-semibold tracking-tight text-white sm:text-5xl"><strong v-text="(powerToday / 1000).toFixed(1)"></strong> MW</dd>
                </div>

                <div class="flex flex-col max-w-xs mx-auto gap-y-4">
                    <dt class="text-base leading-7 text-neutral-400">Belegte Ladestationen</dt>
                    <dd class="order-first text-3xl font-semibold tracking-tight text-white sm:text-5xl"><strong v-text="(total_chargers / occupiedChargers).toFixed()"></strong>%</dd>
                </div>
            </dl>
        </div>
    </div>
</template>

<style scoped></style>