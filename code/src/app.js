//import general javascript
import './js/script.js';

//import tailwind css
import './scss/tailwind.css';
import './scss/_custom/font.scss';
import './scss/_custom/helpers.scss';

// Vuejs
import { createApp } from 'vue';
import staticData from './js/vue-components/staticData.vue';
import heatMap from './js/vue-components/heatMap.vue';
//import leftSide from './js/vue-components/leftSide.vue';
import rightSide from './js/vue-components/rightSide.vue';

createApp(staticData).mount('#staticData');
createApp(heatMap).mount('#heatMap');
//createApp(leftSide).mount('#leftSide');
createApp(rightSide).mount('#rightSide');

export default {
  components: { staticData, heatMap, rightSide }
}


// Accept HMR as per: https://vitejs.dev/guide/api-hmr.html
if (import.meta.hot) {
  import.meta.hot.accept(() => {
    console.log("HMR")
  });
}