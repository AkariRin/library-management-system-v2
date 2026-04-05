<template>
  <v-card-title class="text-h4 mb-6 text-grey-darken-3">
    <v-icon icon="mdi-home" class="mr-8"></v-icon>
    首页
  </v-card-title>
  <v-divider></v-divider>

  <v-card-text class="my-8">
    <!-- 欢迎区域 -->
    <v-row class="mb-8">
      <v-col cols="12">
        <v-card color="primary" variant="elevated" class="pa-6">
          <div class="text-h3 text-white mb-2">
            欢迎回来，{{ userName }}! 👋
          </div>
          <div class="text-h6 text-white opacity-90">
            {{ greeting }}
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- 统计信息卡片 -->
    <v-row class="mb-8">
      <v-col cols="12" md="4">
        <v-card color="blue-lighten-5" variant="elevated" class="pa-6">
          <div class="d-flex align-center mb-2">
            <v-icon icon="mdi-book-multiple" size="40" color="blue-darken-2" class="mr-3"></v-icon>
            <div>
              <div class="text-h4 text-blue-darken-2">{{ totalBooks }}</div>
              <div class="text-body-1 text-grey-darken-1">图书总数</div>
            </div>
          </div>
          <v-progress-linear
            :model-value="100"
            color="blue-darken-2"
            height="4"
            class="mt-2"
          ></v-progress-linear>
        </v-card>
      </v-col>

      <v-col cols="12" md="4">
        <v-card color="green-lighten-5" variant="elevated" class="pa-6">
          <div class="d-flex align-center mb-2">
            <v-icon icon="mdi-book-check" size="40" color="green-darken-2" class="mr-3"></v-icon>
            <div>
              <div class="text-h4 text-green-darken-2">{{ currentBorrowedBooks }}</div>
              <div class="text-body-1 text-grey-darken-1">当前借出</div>
            </div>
          </div>
          <v-progress-linear
            :model-value="borrowProgress"
            color="green-darken-2"
            height="4"
            class="mt-2"
          ></v-progress-linear>
        </v-card>
      </v-col>

      <v-col cols="12" md="4">
        <v-card :color="overdueBooks > 0 ? 'red-lighten-5' : 'grey-lighten-4'" variant="elevated" class="pa-6">
          <div class="d-flex align-center mb-2">
            <v-icon
              icon="mdi-alert-circle"
              size="40"
              :color="overdueBooks > 0 ? 'red-darken-2' : 'grey-darken-1'"
              class="mr-3"
            ></v-icon>
            <div>
              <div class="text-h4" :class="overdueBooks > 0 ? 'text-red-darken-2' : 'text-grey-darken-1'">
                {{ overdueBooks }}
              </div>
              <div class="text-body-1 text-grey-darken-1">逾期书籍</div>
            </div>
          </div>
          <v-progress-linear
            :model-value="overdueBooks > 0 ? 100 : 0"
            :color="overdueBooks > 0 ? 'red-darken-2' : 'grey'"
            height="4"
            class="mt-2"
          ></v-progress-linear>
        </v-card>
      </v-col>
    </v-row>

    <!-- 快捷操作 -->
    <v-row class="mb-8">
      <v-col cols="12">
        <v-card variant="outlined" class="pa-6">
          <div class="text-h5 mb-4 text-grey-darken-3">
            <v-icon icon="mdi-lightning-bolt" class="mr-2"></v-icon>
            快速操作
          </div>
          <v-row>
            <v-col cols="12" sm="6" md="3">
              <v-btn
                block
                color="primary"
                variant="elevated"
                size="large"
                prepend-icon="mdi-book-search"
                @click="navigateTo('/books')"
              >
                书籍搜索
              </v-btn>
            </v-col>
            <v-col cols="12" sm="6" md="3">
              <v-btn
                block
                color="success"
                variant="elevated"
                size="large"
                prepend-icon="mdi-book-clock"
                @click="navigateTo('/borrow')"
              >
                我的借阅
              </v-btn>
            </v-col>
            <v-col cols="12" sm="6" md="3">
              <v-btn
                block
                color="info"
                variant="elevated"
                size="large"
                prepend-icon="mdi-cog"
                @click="navigateTo('/settings')"
              >
                设置
              </v-btn>
            </v-col>
            <v-col cols="12" sm="6" md="3" v-if="isAdmin">
              <v-btn
                block
                color="warning"
                variant="elevated"
                size="large"
                prepend-icon="mdi-shield-account"
                @click="navigateTo('/admin')"
              >
                管理面板
              </v-btn>
            </v-col>
          </v-row>
        </v-card>
      </v-col>
    </v-row>

    <!-- 系统信息 -->
    <v-row>
      <v-col cols="12">
        <v-card variant="outlined" class="pa-6">
          <div class="text-h5 mb-4 text-grey-darken-3">
            <v-icon icon="mdi-information" class="mr-2"></v-icon>
            系统信息
          </div>
          <v-row>
            <v-col cols="12" md="6">
              <v-list density="compact" bg-color="transparent">
                <v-list-item>
                  <template v-slot:prepend>
                    <v-icon icon="mdi-account" color="primary"></v-icon>
                  </template>
                  <v-list-item-title>用户名: {{ username }}</v-list-item-title>
                </v-list-item>
                <v-list-item>
                  <template v-slot:prepend>
                    <v-icon icon="mdi-shield-check" color="success"></v-icon>
                  </template>
                  <v-list-item-title>角色: {{ isAdmin ? '管理员' : '用户' }}</v-list-item-title>
                </v-list-item>
                <v-list-item>
                  <template v-slot:prepend>
                    <v-icon icon="mdi-calendar-clock" color="info"></v-icon>
                  </template>
                  <v-list-item-title>当前时间: {{ currentTime }}</v-list-item-title>
                </v-list-item>
              </v-list>
            </v-col>
            <v-col cols="12" md="6">
              <v-card color="blue-grey-lighten-5" variant="flat" class="pa-4">
                <div class="text-body-1 mb-2">
                  <v-icon icon="mdi-lightbulb-on" color="amber" class="mr-2"></v-icon>
                  <strong>今日提示</strong>
                </div>
                <div class="text-body-2 text-grey-darken-1">
                  {{ dailyTip }}
                </div>
              </v-card>
            </v-col>
          </v-row>
        </v-card>
      </v-col>
    </v-row>
  </v-card-text>

  <!-- 加载提示 -->
  <v-overlay :model-value="loading" class="align-center justify-center">
    <v-progress-circular indeterminate size="64" color="primary"></v-progress-circular>
  </v-overlay>

  <!-- 消息提示 -->
  <v-snackbar
    v-model="snackbar"
    :color="snackbarColor"
    :timeout="3000"
    location="top"
  >
    {{ snackbarText }}
    <template v-slot:actions>
      <v-btn variant="text" @click="snackbar = false">关闭</v-btn>
    </template>
  </v-snackbar>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserdataStore } from '@/stores/userdata'
import axios from 'axios'

// 类型定义
interface PageResponse<T> {
  content: T[]
  totalElements: number
}

interface Book {
  bookId: number
  title: string
  author: string
}

interface BorrowRecord {
  recordId: string
  status: string
  isOverdue: boolean
}

interface AxiosError {
  response?: { data?: { message?: string } }
}

// 路由和状态管理
const router = useRouter()
const userStore = useUserdataStore()

// UI 控制相关
const loading = ref(false)
const snackbar = ref(false)
const snackbarText = ref('')
const snackbarColor = ref('success')
const currentTime = ref('')
let timeInterval: number | null = null

// 用户信息
const userName = computed(() => userStore.name || '访客')
const username = computed(() => userStore.username || 'unknown')
const isAdmin = computed(() => userStore.admin)

// 统计数据
const totalBooks = ref(0)
const currentBorrowedBooks = ref(0)
const overdueBooks = ref(0)

// 计算属性
const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 12) return '早上好！祝你今天愉快！☀️'
  if (hour < 18) return '下午好！希望你工作顺利！🌤️'
  return '晚上好！该放松心情读点好书了！🌙'
})

const borrowProgress = computed(() => {
  if (totalBooks.value === 0) return 0
  return Math.min((currentBorrowedBooks.value / totalBooks.value) * 100, 100)
})

const dailyTip = computed(() => {
  const tips = [
    '记得按时归还图书，避免产生滞纳金！',
    '你可以一次借阅多本图书。',
    '查看我们目录中的新到图书！',
    '使用搜索过滤器按标题、作者或类别查找图书。',
    '在设置中更新你的个人资料以保持信息最新。',
    '逾期图书可能会产生罚款。请尽快归还！',
    '浏览我们的收藏以发现隐藏的珍宝！',
    '你可以在"借阅记录"部分查看所有的借阅历史。'
  ]
  const dayOfYear = Math.floor((Date.now() - new Date(new Date().getFullYear(), 0, 0).getTime()) / 86400000)
  return tips[dayOfYear % tips.length]
})

// 业务逻辑

// 加载图书总数
const loadTotalBooks = async () => {
  try {
    const response = await axios.get<{ success: boolean; data: PageResponse<Book> }>('/api/books', {
      params: { page: 0, size: 1 }
    })
    if (response.data.success && response.data.data) {
      totalBooks.value = response.data.data.totalElements
    }
  } catch (error) {
    console.error('Load total books error:', error)
  }
}

// 加载借阅统计
const loadBorrowStatistics = async () => {
  try {
    // 获取当前借阅的书籍
    const checkedOutResponse = await axios.get<{ success: boolean; data: PageResponse<BorrowRecord> }>(
      '/api/borrow-records/my',
      {
        params: { page: 0, size: 100, status: 'Checked_Out' }
      }
    )
    if (checkedOutResponse.data.success && checkedOutResponse.data.data) {
      const records = checkedOutResponse.data.data.content
      currentBorrowedBooks.value = records.length
      // 计算逾期数量
      overdueBooks.value = records.filter(record => record.isOverdue).length
    }
  } catch (error) {
    console.error('Load borrow statistics error:', error)
  }
}

// 加载所有数据
const loadDashboardData = async () => {
  loading.value = true
  try {
    await Promise.all([
      loadTotalBooks(),
      loadBorrowStatistics()
    ])
  } catch (error) {
    console.error('Load dashboard data error:', error)
    const axiosError = error as AxiosError
    showMessage(axiosError.response?.data?.message || '加载仪表板数据失败', 'error')
  } finally {
    loading.value = false
  }
}

// 导航到指定页面
const navigateTo = (path: string) => {
  router.push(path)
}

// 显示消息
const showMessage = (text: string, color: string) => {
  snackbarText.value = text
  snackbarColor.value = color
  snackbar.value = true
}

// 更新当前时间
const updateCurrentTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleString('en-US', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
}

onMounted(() => {
  loadDashboardData()
  updateCurrentTime()
  // 每秒更新时间
  timeInterval = window.setInterval(updateCurrentTime, 1000)
})

onUnmounted(() => {
  if (timeInterval !== null) {
    clearInterval(timeInterval)
  }
})
</script>

<style scoped>
.opacity-90 {
  opacity: 0.9;
}
</style>
