<template>
  <v-container fluid class="fill-height">
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="6" lg="4">
        <v-card elevation="12" class="pa-4">
          <v-card-title class="text-h4 text-center mb-4">
            登录
          </v-card-title>
          <v-card-text>
            <v-form ref="formRef">
              <v-text-field
                v-model="username"
                label="用户名"
                prepend-inner-icon="mdi-account"
                type="text"
                variant="underlined"
                :rules="usernameRules"
              ></v-text-field>
              <v-text-field
                v-model="password"
                label="密码"
                prepend-inner-icon="mdi-lock"
                :type="showPassword ? 'text' : 'password'"
                :append-inner-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                @click:append-inner="showPassword = !showPassword"
                variant="underlined"
                :rules="passwordRules"
              ></v-text-field>
              <v-checkbox
                v-model="rememberMe"
                label="记住我（保持登录）"
                color="primary"
                hide-details
                class="mb-4"
              ></v-checkbox>
              <v-btn
                block
                color="primary"
                size="large"
                variant="elevated"
                @click="handleLogin"
                :loading="loading"
              >
                登录
              </v-btn>
              <v-divider class="my-4"></v-divider>
              <v-row>
                <v-col cols="12" class="text-center">
                  <span class="text-body-2">还没有账户？</span>
                  <RouterLink to="/register" class="text-primary ms-1">注册</RouterLink>
                </v-col>
              </v-row>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
    <v-snackbar
      v-model="snackbar"
      :color="snackbarColor"
      :timeout="3000"
      location="top"
    >
      {{ snackbarText }}
      <template v-slot:actions>
        <v-btn
          variant="text"
          @click="snackbar = false"
        >
          关闭
        </v-btn>
      </template>
    </v-snackbar>
  </v-container>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute, RouterLink } from 'vue-router'
import { useUserdataStore } from '@/stores/userdata'
import axios from 'axios'

interface AxiosError {
  response?: { data?: { message?: string } }
  request?: unknown
}

const router = useRouter()
const route = useRoute()
const user = useUserdataStore()

const username = ref('')
const password = ref('')
const rememberMe = ref(false)
const showPassword = ref(false)
const loading = ref(false)
const formRef = ref<HTMLFormElement | null>(null)
const snackbar = ref(false)
const snackbarText = ref('')
const snackbarColor = ref('success')

const usernameRules = [
  (v: string) => !!v || '用户名不能为空',
  (v: string) => (v && v.length >= 3 && v.length <= 50) || '用户名长度必须在3-50字符之间'
]
const passwordRules = [
  (v: string) => !!v || '密码不能为空',
  (v: string) => (v && v.length >= 6 && v.length <= 50) || '密码长度必须在6-50字符之间'
]

const handleLogin = async () => {
  const validator = formRef.value as unknown as { validate: () => Promise<{ valid: boolean }> }
  const { valid } = validator ? await validator.validate() : { valid: false }
  if (!valid) {
    return
  }

  loading.value = true
  try {
    const response = await axios.post('/api/auth/login', {
      username: username.value,
      password: password.value,
      rememberMe: rememberMe.value
    })

    if (response.data.success && response.data.data) {
      const { uuid, username: userName, name, admin } = response.data.data

      user.login(uuid, userName, name, admin)

      snackbarText.value = '登录成功！'
      snackbarColor.value = 'success'
      snackbar.value = true

      const redirectPath = route.query.redirect || '/'
      setTimeout(() => {
        router.push(redirectPath as string)
      }, 1000)
    } else {
      snackbarText.value = response.data.message || '登录失败'
      snackbarColor.value = 'error'
      snackbar.value = true
    }
  } catch (error: unknown) {
    const axiosError = error as AxiosError
    console.error('Login error:', error)

    if (axiosError.response) {
      snackbarText.value = axiosError.response.data?.message || '用户名或密码错误'
      snackbarColor.value = 'error'
    } else if (axiosError.request) {
      snackbarText.value = '无法连接到服务器'
      snackbarColor.value = 'error'
    } else {
      snackbarText.value = '登录失败，请重试'
      snackbarColor.value = 'error'
    }
    snackbar.value = true
  } finally {
    loading.value = false
  }
}
</script>
