<template>
    <v-card-title class="text-h4 mb-6 text-grey-darken-3">
      <v-icon icon="mdi-cog" class="mr-8"></v-icon>
      用户设置
    </v-card-title>
    <v-divider></v-divider>
    <v-card-text class="my-8">
      <div class="d-flex align-center mb-10">
        <div class="text-subtitle-1 text-grey-darken-2" style="width: 120px;">UUID</div>
        <div class="text-h5 text-grey-darken-3">{{ uuid }}</div>
      </div>
      <div class="d-flex align-center mb-10">
        <div class="text-subtitle-1 text-grey-darken-2" style="width: 120px;">用户名</div>
        <div class="text-h5 text-grey-darken-3">{{ username }}</div>
      </div>
      <div class="d-flex align-center mb-10">
        <div class="text-subtitle-1 text-grey-darken-2" style="width: 120px;">昵称</div>
        <div class="text-h5 text-grey-darken-3">{{ displayName }}</div>
      </div>
      <div class="d-flex align-center">
        <div class="text-subtitle-1 text-grey-darken-2" style="width: 120px;">角色</div>
        <div class="text-h5 text-grey-darken-3">
          <v-chip :color="isAdmin ? 'error' : 'primary'" variant="elevated">
            {{ isAdmin ? '管理员' : '用户' }}
          </v-chip>
        </div>
      </div>
    </v-card-text>
    <v-divider></v-divider>
    <v-card-actions class="d-flex my-8 ga-6">
      <v-btn color="primary" variant="elevated" prepend-icon="mdi-account-edit" @click="openUsernameDialog">
        修改用户名
      </v-btn>
      <v-btn color="info" variant="elevated" prepend-icon="mdi-card-account-details-outline" @click="openDisplayNameDialog">
        修改昵称
      </v-btn>
      <v-btn color="warning" variant="elevated" prepend-icon="mdi-lock-reset" @click="openPasswordDialog">
        修改密码
      </v-btn>
      <v-btn color="error" variant="elevated" prepend-icon="mdi-logout" @click="handleLogout">
        退出登录
      </v-btn>
    </v-card-actions>

    <v-dialog v-model="usernameDialog" max-width="500px">
      <v-card>
        <v-card-title class="text-h5">修改用户名</v-card-title>
        <v-card-text>
          <v-form ref="usernameFormRef">
            <v-text-field
              v-model="newUsername"
              label="新用户名"
              variant="underlined"
              prepend-icon="mdi-account"
              :rules="nameRules"
              :disabled="loading"
            ></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" variant="text" @click="closeUsernameDialog" :disabled="loading">取消</v-btn>
          <v-btn color="primary" variant="elevated" @click="changeUsername" :loading="loading">确认</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="displayNameDialog" max-width="500px">
      <v-card>
        <v-card-title class="text-h5">修改昵称</v-card-title>
        <v-card-text>
          <v-form ref="displayNameFormRef">
            <v-text-field
              v-model="newDisplayName"
              label="新昵称"
              variant="underlined"
              prepend-icon="mdi-card-account-details-outline"
              :rules="displayNameRules"
              :disabled="loading"
            ></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" variant="text" @click="closeDisplayNameDialog" :disabled="loading">取消</v-btn>
          <v-btn color="info" variant="elevated" @click="changeDisplayName" :loading="loading">确认</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="passwordDialog" max-width="500px">
      <v-card>
        <v-card-title class="text-h5">修改密码</v-card-title>
        <v-card-text>
          <v-form ref="passwordFormRef">
            <v-text-field
              v-model="currentPassword"
              label="当前密码"
              type="password"
              variant="underlined"
              prepend-icon="mdi-lock"
              :rules="passwordRequiredRules"
              :disabled="loading"
              class="mb-2"
            ></v-text-field>
            <v-text-field
              v-model="newPassword"
              label="新密码"
              type="password"
              variant="underlined"
              prepend-icon="mdi-lock-reset"
              :rules="passwordRules"
              :disabled="loading"
              class="mb-2"
            ></v-text-field>
            <v-text-field
              v-model="confirmPassword"
              label="确认新密码"
              type="password"
              variant="underlined"
              prepend-icon="mdi-lock-check"
              :rules="confirmPasswordRules"
              :disabled="loading"
            ></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="grey" variant="text" @click="closePasswordDialog" :disabled="loading">取消</v-btn>
          <v-btn color="warning" variant="elevated" @click="changePassword" :loading="loading">确认</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

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
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useUserdataStore } from '@/stores/userdata'
import { useRouter } from 'vue-router'
import axios from 'axios'

interface AxiosError {
  response?: { data?: { message?: string } }
  request?: unknown
}

interface FormElement {
  validate: () => Promise<{ valid: boolean }>
  reset: () => void
}

const user = useUserdataStore()
const router = useRouter()

const uuid = computed(() => user.uuid)
const username = computed(() => user.username)
const displayName = computed(() => user.name)
const isAdmin = computed(() => user.admin)

const usernameDialog = ref(false)
const displayNameDialog = ref(false)
const passwordDialog = ref(false)

const newUsername = ref('')
const newDisplayName = ref('')
const currentPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')

const loading = ref(false)
const snackbar = ref(false)
const snackbarText = ref('')
const snackbarColor = ref('success')

const usernameFormRef = ref<FormElement | null>(null)
const displayNameFormRef = ref<FormElement | null>(null)
const passwordFormRef = ref<FormElement | null>(null)

const nameRules = [
  (v: string) => !!v || '用户名不能为空',
  (v: string) => (v && v.length >= 3 && v.length <= 50) || '用户名长度必须在3-50个字符之间'
]

const displayNameRules = [
  (v: string) => !!v || '昵称不能为空',
  (v: string) => (v && v.length >= 1 && v.length <= 12) || '昵称长度必须在1-12个字符之间'
]

const passwordRequiredRules = [
  (v: string) => !!v || '当前密码不能为空'
]

const passwordRules = [
  (v: string) => !!v || '新密码不能为空',
  (v: string) => (v && v.length >= 6 && v.length <= 50) || '密码长度必须在6-50个字符之间'
]

const confirmPasswordRules = [
  (v: string) => v === newPassword.value || '两次输入的密码不一致'
]

const openUsernameDialog = () => {
  newUsername.value = username.value || ''
  usernameDialog.value = true
}

const closeUsernameDialog = () => {
  usernameDialog.value = false
  newUsername.value = ''
  if (usernameFormRef.value) {
    usernameFormRef.value.reset()
  }
}

const openDisplayNameDialog = () => {
  newDisplayName.value = displayName.value || ''
  displayNameDialog.value = true
}

const closeDisplayNameDialog = () => {
  displayNameDialog.value = false
  newDisplayName.value = ''
  if (displayNameFormRef.value) {
    displayNameFormRef.value.reset()
  }
}

const openPasswordDialog = () => {
  passwordDialog.value = true
}

const closePasswordDialog = () => {
  passwordDialog.value = false
  currentPassword.value = ''
  newPassword.value = ''
  confirmPassword.value = ''
  if (passwordFormRef.value) {
    passwordFormRef.value.reset()
  }
}

const changeUsername = async () => {
  const validator = usernameFormRef.value as FormElement | null
  const { valid } = validator ? await validator.validate() : { valid: false }
  if (!valid) {
    return
  }

  loading.value = true

  try {
    const response = await axios.post(
      '/api/user/change-username',
      {
        userUuid: user.uuid,
        newUsername: newUsername.value
      }
    )

    if (response.data.success && response.data.data) {
      user.updateUsername(response.data.data.username)
      snackbarText.value = '用户名更新成功'
      snackbarColor.value = 'success'
      snackbar.value = true
      setTimeout(() => {
        closeUsernameDialog()
      }, 1000)
    } else {
      snackbarText.value = response.data.message || '更新失败'
      snackbarColor.value = 'error'
      snackbar.value = true
    }
  } catch (error: unknown) {
    const axiosError = error as AxiosError
    console.error('Change username error:', error)
    snackbarText.value = axiosError.response?.data?.message || '更新失败，请重试'
    snackbarColor.value = 'error'
    snackbar.value = true
  } finally {
    loading.value = false
  }
}

const changeDisplayName = async () => {
  const validator = displayNameFormRef.value as FormElement | null
  const { valid } = validator ? await validator.validate() : { valid: false }
  if (!valid) {
    return
  }

  loading.value = true

  try {
    const response = await axios.post(
      '/api/user/change-name',
      {
        userUuid: user.uuid,
        newName: newDisplayName.value
      }
    )

    if (response.data.success && response.data.data) {
      user.updateName(response.data.data.name)
      snackbarText.value = '昵称更新成功'
      snackbarColor.value = 'success'
      snackbar.value = true
      setTimeout(() => {
        closeDisplayNameDialog()
      }, 1000)
    } else {
      snackbarText.value = response.data.message || '更新失败'
      snackbarColor.value = 'error'
      snackbar.value = true
    }
  } catch (error: unknown) {
    const axiosError = error as AxiosError
    console.error('Change display name error:', error)
    snackbarText.value = axiosError.response?.data?.message || '更新失败，请重试'
    snackbarColor.value = 'error'
    snackbar.value = true
  } finally {
    loading.value = false
  }
}

const changePassword = async () => {
  const validator = passwordFormRef.value as FormElement | null
  const { valid } = validator ? await validator.validate() : { valid: false }
  if (!valid) {
    return
  }

  loading.value = true

  try {
    const response = await axios.post(
      '/api/user/change-pass',
      {
        userUuid: user.uuid,
        oldPassword: currentPassword.value,
        newPassword: newPassword.value
      }
    )

    if (response.data.success) {
      snackbarText.value = '密码更新成功'
      snackbarColor.value = 'success'
      snackbar.value = true
      setTimeout(() => {
        closePasswordDialog()
      }, 1000)
    } else {
      snackbarText.value = response.data.message || '更新失败'
      snackbarColor.value = 'error'
      snackbar.value = true
    }
  } catch (error: unknown) {
    const axiosError = error as AxiosError
    console.error('Change password error:', error)
    snackbarText.value = axiosError.response?.data?.message || '更新失败，请重试'
    snackbarColor.value = 'error'
    snackbar.value = true
  } finally {
    loading.value = false
  }
}

const handleLogout = async () => {
  loading.value = true
  try {
    await axios.post('/api/auth/logout')

    user.logout()
    snackbarText.value = '退出登录成功'
    snackbarColor.value = 'success'
    snackbar.value = true

    setTimeout(() => {
      router.push('/login')
    }, 1000)
  } catch (error: unknown) {
    console.error('Logout error:', error)
    // 即使服务器端登出失败也清除状态
    user.logout()
    router.push('/login')
  } finally {
    loading.value = false
  }
}
</script>
