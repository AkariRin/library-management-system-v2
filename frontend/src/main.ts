import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import axios from 'axios'

import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'
import 'unfonts.css'

import App from './App.vue'
import router from './router'
import { useUserdataStore } from '@/stores/userdata'

// Configure axios to send cookies with requests
axios.defaults.withCredentials = true

createApp(App)
  .use(createPinia().use(piniaPluginPersistedstate))
  .use(router)
  .use(createVuetify({
  components,
  directives,
}))
  .mount('#app')

// 添加axios全局拦截器，用于拦截401返回
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      const userStore = useUserdataStore()
      userStore.logout()
      router.push({
        name: 'login',
        query: { redirect: router.currentRoute.value.fullPath }
      })
    }
    return Promise.reject(error)
  }
)
