import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import axios from 'axios'

import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import { zhHans } from 'vuetify/locale'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import '@mdi/font/css/materialdesignicons.css'
import 'unfonts.css'

import App from './App.vue'
import router from './router'
import { useUserdataStore } from '@/stores/userdata'

// 让axios携带cookie进行跨域请求，以支持后端的会话管理和认证机制
axios.defaults.withCredentials = true

createApp(App)
  .use(createPinia().use(piniaPluginPersistedstate))
  .use(router)
  .use(createVuetify({
  components,
  directives,
  locale: {
    locale: 'zhHans',
    messages: {
      zhHans,
    },
  },
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
