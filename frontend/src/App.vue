<template>
  <v-app>
    <v-app-bar
      v-if="showAppBar"
      class="px-3"
      density="compact"
      flat
    >
      <v-tabs
        v-model="activeTab"
        align-tabs="center"
        color="grey-darken-2"
      >
        <v-tab
          :key="route.name"
          v-for="route in navTabs"
          :to="{ name: route.name }"
          replace
          style="min-width: 160px; padding: 0 20px"
        >
          <template v-if="route.name === 'Dashboard'">
            <v-icon icon="mdi-home" size="24" />
          </template>
          <template v-else>
            {{ route.name }}
          </template>
        </v-tab>
      </v-tabs>
      <v-spacer></v-spacer>
      <v-btn
        icon="mdi-logout"
        variant="text"
        @click="logout"
        title="Logout"
      ></v-btn>
    </v-app-bar>
    <v-main class="glass-effect">
      <v-container class="pt-16" fluid>
        <v-row v-if="!route.meta?.isPublic" class="pt-16" justify="center">
          <v-col cols="12" md="10">
            <v-card elevation="4" class="pa-16 glass-card">
              <router-view />
            </v-card>
          </v-col>
        </v-row>
        <router-view v-else />
      </v-container>
    </v-main>
    <v-dialog v-model="logoutDialog" max-width="400">
      <v-card>
        <v-card-title class="text-h5">
          Confirm logout
        </v-card-title>
        <v-card-text>
          Are you sure you want to log out?
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn
            color="grey"
            variant="text"
            @click="cancelLogout"
          >
            Cancel
          </v-btn>
          <v-btn
            color="primary"
            variant="text"
            @click="confirmLogout"
          >
            Confirm
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-snackbar
      v-model="snackbar"
      :timeout="3000"
      color="success"
      location="top"
    >
      {{ snackbarText }}
      <template v-slot:actions>
        <v-btn
          variant="text"
          @click="snackbar = false"
        >
          Close
        </v-btn>
      </template>
    </v-snackbar>
  </v-app>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserdataStore } from '@/stores/userdata'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const user = useUserdataStore()
const activeTab = ref<string | symbol | undefined | null>(null)
const logoutDialog = ref(false)
const snackbar = ref(false)
const snackbarText = ref('')
const showAppBar = computed(() => !route.meta?.isPublic)
const navTabs = computed(() => {
  return router.options.routes.filter(r => {
    // 过滤掉公开页面
    if (r.meta?.isPublic) return false
    // 如果路由需要管理员权限，只有管理员才能看到
    if (r.meta?.requireAdmin) {
      return user.admin
    }
    return true
  })
})

const logout = () => {
  logoutDialog.value = true
}

const confirmLogout = async () => {
  try {
    await axios.post('/api/auth/logout')
    user.logout()
    logoutDialog.value = false
    snackbarText.value = 'Successfully logged out'
    snackbar.value = true
    setTimeout(() => {
      router.push({ name: 'login' })
    }, 1000)
  } catch (error) {
    console.error('Logout failed:', error)
    // 登出失败也清理本地状态
    user.logout()
    logoutDialog.value = false
    router.push({ name: 'login' })
  }
}

const cancelLogout = () => {
  logoutDialog.value = false
}

watch(
  () => route.name,
  (name) => {
    activeTab.value = name
  },
  { immediate: true }
)
</script>

<style scoped>
.glass-effect {
  position: relative;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.15), rgba(255, 255, 255, 0.05));
  backdrop-filter: blur(25px);
  -webkit-backdrop-filter: blur(25px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.37);
  overflow: hidden;
}

.glass-effect::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  right: -50%;
  bottom: -50%;
  background:
    radial-gradient(circle at 20% 50%, rgba(120, 119, 198, 0.5), transparent 40%),
    radial-gradient(circle at 80% 80%, rgba(72, 219, 251, 0.5), transparent 40%),
    radial-gradient(circle at 40% 20%, rgba(254, 215, 226, 0.5), transparent 40%);
  z-index: -1;
  animation: gradientShift 8s ease-in-out infinite;
}

@keyframes gradientShift {
  0% {
    transform: translate(0, 0) scale(1);
    opacity: 0.9;
  }
  33% {
    transform: translate(10%, 10%) scale(1.1);
    opacity: 0.7;
  }
  66% {
    transform: translate(-5%, 5%) scale(0.95);
    opacity: 0.8;
  }
  100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.9;
  }
}

.glass-card {
  background: rgba(255, 255, 255, 0.8) !important;
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.3) !important;
}
</style>

