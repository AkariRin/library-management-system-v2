<template>
  <v-card-title class="text-h4 mb-6 text-grey-darken-3">
    <v-icon icon="mdi-book-clock" class="mr-8"></v-icon>
    我的借阅记录
  </v-card-title>
  <v-divider></v-divider>

  <v-card-text class="my-8">
    <!-- 过滤器区域 -->
    <v-row class="mb-6">
      <v-col cols="12" md="4">
        <v-select
          v-model="statusFilter"
          label="状态筛选"
          variant="underlined"
          prepend-inner-icon="mdi-filter"
          :items="statusOptions"
          item-title="text"
          item-value="value"
          clearable
          @update:modelValue="loadBorrowRecords"
        ></v-select>
      </v-col>
      <v-col cols="12" md="8" class="d-flex align-center ga-4">
        <v-btn color="primary" variant="elevated" prepend-icon="mdi-refresh" @click="loadBorrowRecords" :loading="loading">
          刷新
        </v-btn>
        <v-chip :color="overdueCount > 0 ? 'error' : 'success'" variant="elevated">
          <v-icon start icon="mdi-alert-circle"></v-icon>
          {{ overdueCount }} 逾期
        </v-chip>
      </v-col>
    </v-row>

    <!-- 借阅记录数据表格 -->
    <v-data-table
      :headers="headers"
      :items="borrowRecords"
      :loading="loading"
      :items-per-page="pageSize"
      hide-default-footer
      class="elevation-2"
    >
      <template v-slot:[`item.bookInfo`]="{ item }">
        <div>
          <div class="font-weight-bold">{{ item.bookTitle }}</div>
          <div class="text-caption text-grey-darken-1">{{ item.bookAuthor }}</div>
        </div>
      </template>
      <template v-slot:[`item.borrowDate`]="{ item }">
        {{ formatDate(item.borrowDate) }}
      </template>
      <template v-slot:[`item.dueDate`]="{ item }">
        <div :class="{ 'text-error font-weight-bold': item.isOverdue }">
          {{ formatDate(item.dueDate) }}
          <v-chip v-if="item.isOverdue" color="error" size="x-small" class="ml-1">逾期</v-chip>
        </div>
      </template>
      <template v-slot:[`item.returnDate`]="{ item }">
        {{ item.returnDate ? formatDate(item.returnDate) : '未归还' }}
      </template>
      <template v-slot:[`item.status`]="{ item }">
        <v-chip :color="getStatusColor(item.status)" size="small" variant="flat">
          {{ item.statusDescription }}
        </v-chip>
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <v-btn
          v-if="item.status === 'Checked_Out'"
          color="success"
          variant="text"
          size="small"
          @click="returnBook(item)"
          :loading="returningRecordId === item.recordId"
        >
          立即归还
        </v-btn>
        <v-btn
          color="info"
          variant="text"
          size="small"
          @click="viewRecordDetails(item)"
        >
          详情
        </v-btn>
      </template>
      <template v-slot:bottom>
        <div class="text-center pa-4">
          <v-pagination
            v-model="currentPage"
            :length="totalPages"
            :total-visible="7"
            @update:modelValue="onPageChange"
          ></v-pagination>
          <div class="text-caption text-grey-darken-1 mt-2">
            总计：{{ totalElements }} 条记录 | 第 {{ currentPage }} / {{ totalPages }} 页
          </div>
        </div>
      </template>
    </v-data-table>
  </v-card-text>

  <!-- 借阅记录详情对话框 -->
  <v-dialog v-model="detailDialog" max-width="700px">
    <v-card>
      <v-card-title class="text-h5 bg-info">
        <v-icon icon="mdi-information" class="mr-2"></v-icon>
        借阅记录详情
      </v-card-title>
      <v-card-text class="pa-6" v-if="selectedRecord">
        <v-row>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">记录 ID</div>
              <div class="text-body-1 font-weight-medium">{{ selectedRecord.recordId }}</div>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">状态</div>
              <v-chip :color="getStatusColor(selectedRecord.status)" variant="elevated">
                {{ selectedRecord.statusDescription }}
              </v-chip>
            </div>
          </v-col>
          <v-col cols="12">
            <v-divider></v-divider>
          </v-col>
          <v-col cols="12">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">书籍名称</div>
              <div class="text-h6">{{ selectedRecord.bookTitle }}</div>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">作者</div>
              <div class="text-body-1">{{ selectedRecord.bookAuthor }}</div>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">条形码</div>
              <div class="text-body-1">{{ selectedRecord.barcode || 'N/A' }}</div>
            </div>
          </v-col>
          <v-col cols="12">
            <v-divider></v-divider>
          </v-col>
          <v-col cols="12" md="4">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">借阅日期</div>
              <div class="text-body-1">{{ formatDate(selectedRecord.borrowDate) }}</div>
            </div>
          </v-col>
          <v-col cols="12" md="4">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">到期日期</div>
              <div class="text-body-1" :class="{ 'text-error font-weight-bold': selectedRecord.isOverdue }">
                {{ formatDate(selectedRecord.dueDate) }}
                <v-chip v-if="selectedRecord.isOverdue" color="error" size="small" class="ml-1">逾期</v-chip>
              </div>
            </div>
          </v-col>
          <v-col cols="12" md="4">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">归还日期</div>
              <div class="text-body-1">{{ selectedRecord.returnDate ? formatDate(selectedRecord.returnDate) : '未归还' }}</div>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">用户</div>
              <div class="text-body-1">{{ selectedRecord.userName }} (@{{ selectedRecord.username }})</div>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">副本 ID</div>
              <div class="text-body-1">{{ selectedRecord.itemId }}</div>
            </div>
          </v-col>
        </v-row>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn
          v-if="selectedRecord && selectedRecord.status === 'Checked_Out'"
          color="success"
          variant="elevated"
          @click="returnBookFromDialog"
          :loading="returningRecordId === selectedRecord.recordId"
        >
          立即归还
        </v-btn>
        <v-btn color="grey" variant="text" @click="closeDetailDialog">关闭</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <!-- 归还确认对话框 -->
  <v-dialog v-model="returnDialog" max-width="400px">
    <v-card>
      <v-card-title class="text-h5">确认归还</v-card-title>
      <v-card-text>
        确定要归还这本书吗？
        <div class="mt-4" v-if="recordToReturn">
          <div class="font-weight-bold">{{ recordToReturn.bookTitle }}</div>
          <div class="text-caption">作者：{{ recordToReturn.bookAuthor }}</div>
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn color="grey" variant="text" @click="cancelReturn" :disabled="returningRecordId !== null">取消</v-btn>
        <v-btn
          color="success"
          variant="elevated"
          @click="confirmReturn"
          :loading="returningRecordId !== null"
        >
          确认
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

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
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

// 类型定义
interface BorrowRecord {
  recordId: string
  userUuid: string
  username: string
  userName: string
  itemId: number
  barcode?: string
  bookId: number
  bookTitle: string
  bookAuthor: string
  borrowDate: string
  dueDate: string
  returnDate?: string
  status: string
  statusDescription: string
  isOverdue: boolean
}

interface PageResponse<T> {
  content: T[]
  pageNumber: number
  pageSize: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
  empty: boolean
}

interface AxiosError {
  response?: { data?: { message?: string } }
}

// UI 控制相关
const loading = ref(false)
const returningRecordId = ref<string | null>(null)
const detailDialog = ref(false)
const returnDialog = ref(false)
const snackbar = ref(false)
const snackbarText = ref('')
const snackbarColor = ref('success')

// 数据相关
const borrowRecords = ref<BorrowRecord[]>([])
const selectedRecord = ref<BorrowRecord | null>(null)
const recordToReturn = ref<BorrowRecord | null>(null)

const currentPage = ref(1)
const pageSize = ref(10)
const totalElements = ref(0)
const totalPages = ref(0)

const statusFilter = ref<string | null>(null)

const statusOptions = [
  { text: '全部', value: null },
  { text: '已借出', value: 'Checked_Out' },
  { text: '已归还', value: 'Returned' },
  { text: '逾期', value: 'Overdue' }
]

const headers = [
  { title: '书籍信息', key: 'bookInfo', sortable: false },
  { title: '条形码', key: 'barcode', sortable: false },
  { title: '借阅日期', key: 'borrowDate', sortable: false },
  { title: '到期日期', key: 'dueDate', sortable: false },
  { title: '归还日期', key: 'returnDate', sortable: false },
  { title: '状态', key: 'status', sortable: false },
  { title: '操作', key: 'actions', sortable: false }
]

// 计算属性
const overdueCount = computed(() => {
  return borrowRecords.value.filter(record => record.isOverdue && record.status === 'Checked_Out').length
})

// 业务逻辑相关

// 加载借阅记录
const loadBorrowRecords = async () => {
  loading.value = true
  try {
    const params: Record<string, string | number> = {
      page: currentPage.value - 1,
      size: pageSize.value
    }

    if (statusFilter.value) {
      params.status = statusFilter.value
    }

    const response = await axios.get<{ success: boolean; message: string; data: PageResponse<BorrowRecord> }>(
      '/api/borrow-records/my-records',
      { params }
    )

    if (response.data.success && response.data.data) {
      borrowRecords.value = response.data.data.content
      totalElements.value = response.data.data.totalElements
      totalPages.value = response.data.data.totalPages
    } else {
      showMessage(response.data.message || '加载借阅记录失败', 'error')
    }
  } catch (error) {
    console.error('Load borrow records error:', error)
    const axiosError = error as AxiosError
    showMessage(axiosError.response?.data?.message || '加载借阅记录失败', 'error')
  } finally {
    loading.value = false
  }
}

// 页码变化处理
const onPageChange = (page: number) => {
  currentPage.value = page
  loadBorrowRecords()
}

// 查看借阅记录详情
const viewRecordDetails = (record: BorrowRecord) => {
  selectedRecord.value = record
  detailDialog.value = true
}

// 关闭详情对话框
const closeDetailDialog = () => {
  detailDialog.value = false
  selectedRecord.value = null
}

// 归还图书（从列表）
const returnBook = (record: BorrowRecord) => {
  recordToReturn.value = record
  returnDialog.value = true
}

// 归还图书（从详情对话框）
const returnBookFromDialog = () => {
  if (selectedRecord.value) {
    recordToReturn.value = selectedRecord.value
    returnDialog.value = true
  }
}

// 取消归还
const cancelReturn = () => {
  returnDialog.value = false
  recordToReturn.value = null
}

// 确认归还
const confirmReturn = async () => {
  if (!recordToReturn.value) return

  returningRecordId.value = recordToReturn.value.recordId
  try {
    const response = await axios.post<{ success: boolean; message: string }>(
      `/api/borrow-records/${recordToReturn.value.recordId}/return`
    )

    if (response.data.success) {
      showMessage('图书归还成功！', 'success')
      returnDialog.value = false
      recordToReturn.value = null

      // 如果详情对话框是打开的，关闭它
      if (detailDialog.value) {
        detailDialog.value = false
        selectedRecord.value = null
      }

      // 重新加载借阅记录
      await loadBorrowRecords()
    } else {
      showMessage(response.data.message || '图书归还失败', 'error')
    }
  } catch (error) {
    console.error('Return book error:', error)
    const axiosError = error as AxiosError
    showMessage(axiosError.response?.data?.message || '图书归还失败', 'error')
  } finally {
    returningRecordId.value = null
  }
}

// 格式化日期
const formatDate = (dateString: string): string => {
  if (!dateString) return '无'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 获取状态颜色
const getStatusColor = (status: string): string => {
  const colorMap: Record<string, string> = {
    'Checked_Out': 'warning',
    'Returned': 'success',
    'Overdue': 'error'
  }
  return colorMap[status] || 'grey'
}

// 显示消息
const showMessage = (text: string, color: string) => {
  snackbarText.value = text
  snackbarColor.value = color
  snackbar.value = true
}

onMounted(() => {
  loadBorrowRecords()
})
</script>
