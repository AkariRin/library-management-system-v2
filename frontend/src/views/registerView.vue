<template>
  <v-container fluid class="fill-height">
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="6" lg="4">
        <v-card elevation="12" class="pa-4">
          <v-card-title class="text-h4 text-center mb-4">
            创建账户
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
                hint="用于登录（唯一标识）"
              ></v-text-field>
              <v-text-field
                v-model="name"
                label="昵称"
                prepend-inner-icon="mdi-card-account-details"
                type="text"
                variant="underlined"
                :rules="nameRules"
                hint="你的显示名称（1-12字符）"
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
              <v-text-field
                v-model="confirmPassword"
                label="确认密码"
                prepend-inner-icon="mdi-lock-check"
                :type="showConfirmPassword ? 'text' : 'password'"
                :append-inner-icon="showConfirmPassword ? 'mdi-eye' : 'mdi-eye-off'"
                @click:append-inner="showConfirmPassword = !showConfirmPassword"
                variant="underlined"
                :rules="confirmPasswordRules"
              ></v-text-field>
              <v-btn
                block
                color="primary"
                size="large"
                variant="elevated"
                @click="handleRegister"
                :loading="loading"
              >
                注册
              </v-btn>
              <v-divider class="my-4"></v-divider>
              <v-row>
                <v-col cols="12" class="text-center">
                  <span class="text-body-2">已有账户？</span>
                  <RouterLink to="/login" class="text-primary ms-1">登录</RouterLink>
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
import { useRouter, RouterLink } from 'vue-router'
import axios from 'axios'

interface AxiosError {
  response?: { data?: { message?: string } }
  request?: unknown
}

const router = useRouter()

const username = ref('')
const name = ref('')
const password = ref('')
const confirmPassword = ref('')
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const loading = ref(false)
const formRef = ref<HTMLFormElement | null>(null)

const snackbar = ref(false)
const snackbarText = ref('')
const snackbarColor = ref('success')

const usernameRules = [
  (v: string) => !!v || '用户名不能为空',
  (v: string) => (v && v.length >= 3 && v.length <= 50) || '用户名长度必须在3-50字符之间'
]
const nameRules = [
  (v: string) => !!v || '昵称不能为空',
  (v: string) => (v && v.length >= 1 && v.length <= 12) || '昵称长度必须在1-12字符之间'
]
const passwordRules = [
  (v: string) => !!v || '密码不能为空',
  (v: string) => (v && v.length >= 6 && v.length <= 50) || '密码长度必须在6-50字符之间'
]
const confirmPasswordRules = [
  (v: string) => v === password.value || '两次输入的密码不匹配'
]

const handleRegister = async () => {
  const validator = formRef.value as unknown as { validate: () => Promise<{ valid: boolean }> }
  const { valid } = validator ? await validator.validate() : { valid: false }
  if (!valid) {
    return
  }

  loading.value = true
  try {
    const response = await axios.post('/api/auth/register', {
      username: username.value,
      name: name.value,
      password: password.value
    })

    if (response.data.success) {
      snackbarText.value = '注册成功！跳转到登录...'
      snackbarColor.value = 'success'
      snackbar.value = true

      setTimeout(() => {
        router.push('/login')
      }, 2000)
    } else {
      snackbarText.value = response.data.message || '注册失败'
      snackbarColor.value = 'error'
      snackbar.value = true
    }
  } catch (error: unknown) {
    const axiosError = error as AxiosError
    if (axiosError.response) {
      snackbarText.value = axiosError.response.data?.message || '注册失败'
      snackbarColor.value = 'error'
    } else if (axiosError.request) {
      snackbarText.value = '无法连接到服务器'
      snackbarColor.value = 'error'
    } else {
      snackbarText.value = '注册失败，请重试'
      snackbarColor.value = 'error'
    }
    snackbar.value = true
  } finally {
    loading.value = false
  }
}
</script>
