<template>
  <v-card-title class="text-h4 mb-6 text-grey-darken-3">
    <v-icon icon="mdi-book-search" class="mr-8"></v-icon>
    书籍搜索
  </v-card-title>
  <v-divider></v-divider>

  <v-card-text class="my-8">
    <!-- 搜索过滤器区域 -->
    <v-expansion-panels class="mb-6">
      <v-expansion-panel>
        <v-expansion-panel-title>
          <v-icon icon="mdi-filter" class="mr-2"></v-icon>
          搜索筛选
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <v-row>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="searchFilters.title"
                label="书名"
                variant="underlined"
                prepend-inner-icon="mdi-book"
                clearable
                @keyup.enter="searchBooks"
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="searchFilters.author"
                label="作者"
                variant="underlined"
                prepend-inner-icon="mdi-account-edit"
                clearable
                @keyup.enter="searchBooks"
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="6">
              <v-text-field
                v-model="searchFilters.category"
                label="类別ID"
                variant="underlined"
                prepend-inner-icon="mdi-tag"
                type="number"
                clearable
                @keyup.enter="searchBooks"
              ></v-text-field>
            </v-col>
            <v-col cols="12" md="6">
              <v-select
                v-model="searchFilters.sortBy"
                label="排序方式"
                variant="underlined"
                prepend-inner-icon="mdi-sort"
                :items="sortOptions"
                item-title="text"
                item-value="value"
              ></v-select>
            </v-col>
          </v-row>
          <v-row>
            <v-col cols="12" class="d-flex ga-4">
              <v-btn color="primary" variant="elevated" prepend-icon="mdi-magnify" @click="searchBooks" :loading="loading">
                搜索
              </v-btn>
              <v-btn color="grey" variant="text" prepend-icon="mdi-refresh" @click="resetFilters">
                重置
              </v-btn>
            </v-col>
          </v-row>
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>

    <!-- 图书列表数据表格 -->
    <v-data-table
      :headers="headers"
      :items="books"
      :loading="loading"
      :items-per-page="pageSize"
      hide-default-footer
      class="elevation-2"
    >
      <template v-slot:[`item.price`]="{ item }">
        ¥{{ item.price ? item.price.toFixed(2) : '0.00' }}
      </template>
      <template v-slot:[`item.availability`]="{ item }">
        <v-chip :color="item.availableCopies > 0 ? 'success' : 'error'" variant="flat" size="small">
          {{ item.availableCopies }} / {{ item.totalCopies }}
        </v-chip>
      </template>
      <template v-slot:[`item.actions`]="{ item }">
        <v-btn color="info" variant="text" size="small" @click="viewBookDetails(item)">
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
            总计：{{ totalElements }} 本书 | 第 {{ currentPage }} / {{ totalPages }} 页
          </div>
        </div>
      </template>
    </v-data-table>
  </v-card-text>

  <!-- 图书详情对话框 -->
  <v-dialog v-model="detailDialog" max-width="800px">
    <v-card>
      <v-card-title class="text-h5 bg-primary">
        <v-icon icon="mdi-book-open-page-variant" class="mr-2"></v-icon>
        书籍触详情
      </v-card-title>
      <v-card-text class="pa-6" v-if="selectedBook">
        <v-row>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">书籍 ID</div>
              <div class="text-h6">{{ selectedBook.bookId }}</div>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">ISBN</div>
              <div class="text-h6">{{ selectedBook.isbn || 'N/A' }}</div>
            </div>
          </v-col>
          <v-col cols="12">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">书名</div>
              <div class="text-h5">{{ selectedBook.title }}</div>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">作者</div>
              <div class="text-h6">{{ selectedBook.author }}</div>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">出版社</div>
              <div class="text-h6">{{ selectedBook.publisher || 'N/A' }}</div>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">出版日期</div>
              <div class="text-h6">{{ selectedBook.publishDate || 'N/A' }}</div>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">类別</div>
              <div class="text-h6">{{ selectedBook.category || 'N/A' }}</div>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">价格</div>
              <div class="text-h6">¥{{ selectedBook.price ? selectedBook.price.toFixed(2) : '0.00' }}</div>
            </div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">库存</div>
              <div class="text-h6">
                <v-chip :color="selectedBook.availableCopies > 0 ? 'success' : 'error'" variant="elevated">
                  {{ selectedBook.availableCopies }} 可用 / {{ selectedBook.totalCopies }} 总计
                </v-chip>
              </div>
            </div>
          </v-col>
          <v-col cols="12">
            <div class="mb-4">
              <div class="text-caption text-grey-darken-1">摘要</div>
              <div class="text-body-1">{{ selectedBook.summary || '没有摘要信息' }}</div>
            </div>
          </v-col>
        </v-row>

        <v-divider class="my-4"></v-divider>

        <!-- 可用副本列表 -->
        <div class="mb-4">
          <div class="text-h6 mb-3">可用副本</div>
          <v-btn
            color="primary"
            variant="text"
            size="small"
            prepend-icon="mdi-refresh"
            @click="loadBookCopies"
            :loading="loadingCopies"
            class="mb-3"
          >
            加载副本
          </v-btn>

          <v-data-table
            v-if="bookCopies.length > 0"
            :headers="copiesHeaders"
            :items="bookCopies"
            :loading="loadingCopies"
            density="compact"
            class="elevation-1"
          >
            <template v-slot:[`item.status`]="{ item }">
              <v-chip :color="getStatusColor(item.status)" size="small" variant="flat">
                {{ item.statusDescription }}
              </v-chip>
            </template>
            <template v-slot:[`item.actions`]="{ item }">
              <v-btn
                v-if="item.status === 'Available'"
                color="success"
                variant="text"
                size="small"
                @click="borrowBook(item)"
                :disabled="borrowingItemId === item.itemId"
              >
                借阅
              </v-btn>
            </template>
          </v-data-table>
          <v-alert v-else-if="!loadingCopies" type="info" variant="tonal" class="mt-2">
            点击"加载副本"来查看可用副本
          </v-alert>
        </div>
      </v-card-text>
      <v-card-actions>
        <v-spacer></v-spacer>
      <v-btn color="grey" variant="text" @click="closeDetailDialog">关闽</v-btn>
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
      <v-btn variant="text" @click="snackbar = false">关闽</v-btn>
    </template>
  </v-snackbar>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

// 类型定义
interface Book {
  bookId: number
  isbn?: string
  title: string
  author: string
  publisher?: string
  publishDate?: string
  category?: number
  summary?: string
  price?: number
  totalCopies: number
  availableCopies: number
}

interface BookCopy {
  itemId: number
  bookId: number
  bookTitle: string
  bookAuthor: string
  barcode?: string
  location?: string
  status: string
  statusDescription: string
  acquisitionDate?: string
  acquisitionPrice?: number
  notes?: string
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
const loadingCopies = ref(false)
const borrowingItemId = ref<number | null>(null)
const detailDialog = ref(false)
const snackbar = ref(false)
const snackbarText = ref('')
const snackbarColor = ref('success')

// 数据相关
const books = ref<Book[]>([])
const selectedBook = ref<Book | null>(null)
const bookCopies = ref<BookCopy[]>([])

const currentPage = ref(1)
const pageSize = ref(10)
const totalElements = ref(0)
const totalPages = ref(0)

const searchFilters = ref({
  title: '',
  author: '',
  category: null as number | null,
  sortBy: 'bookId'
})

const sortOptions = [
  { text: '书籍 ID', value: 'bookId' },
  { text: '书名', value: 'title' },
  { text: '作者', value: 'author' },
  { text: '出版日期', value: 'publishDate' },
  { text: '价格', value: 'price' }
]

const headers = [
  { title: 'ID', key: 'bookId', sortable: false },
  { title: '书名', key: 'title', sortable: false },
  { title: '作者', key: 'author', sortable: false },
  { title: '出版社', key: 'publisher', sortable: false },
  { title: '价格', key: 'price', sortable: false },
  { title: '可用', key: 'availability', sortable: false },
  { title: '操作', key: 'actions', sortable: false }
]

const copiesHeaders = [
  { title: '副本ID', key: 'itemId' },
  { title: '条形码', key: 'barcode' },
  { title: '位置', key: 'location' },
  { title: '状态', key: 'status' },
  { title: '操作', key: 'actions' }
]

// 业务逻辑相关

// 搜索图书
const searchBooks = async () => {
  loading.value = true
  try {
    const params: Record<string, string | number> = {
      page: currentPage.value - 1,
      size: pageSize.value,
      sortBy: searchFilters.value.sortBy,
      sortDirection: 'desc'
    }

    if (searchFilters.value.title) {
      params.title = searchFilters.value.title
    }
    if (searchFilters.value.author) {
      params.author = searchFilters.value.author
    }
    if (searchFilters.value.category !== null) {
      params.category = searchFilters.value.category
    }

    const response = await axios.get<{ success: boolean; message: string; data: PageResponse<Book> }>('/api/books', { params })

    if (response.data.success && response.data.data) {
      books.value = response.data.data.content
      totalElements.value = response.data.data.totalElements
      totalPages.value = response.data.data.totalPages
    } else {
      showMessage(response.data.message || '获取书籍列表失败', 'error')
    }
  } catch (error) {
    console.error('Search books error:', error)
    const axiosError = error as AxiosError
    showMessage(axiosError.response?.data?.message || '获取书籍列表失败', 'error')
  } finally {
    loading.value = false
  }
}

// 重置过滤器
const resetFilters = () => {
  searchFilters.value = {
    title: '',
    author: '',
    category: null,
    sortBy: 'bookId'
  }
  currentPage.value = 1
  searchBooks()
}

// 页码变化处理
const onPageChange = (page: number) => {
  currentPage.value = page
  searchBooks()
}

// 查看图书详情
const viewBookDetails = (book: Book) => {
  selectedBook.value = book
  bookCopies.value = []
  detailDialog.value = true
}

// 关闭详情对话框
const closeDetailDialog = () => {
  detailDialog.value = false
  selectedBook.value = null
  bookCopies.value = []
}

// 加载图书副本列表
const loadBookCopies = async () => {
  if (!selectedBook.value) return

  loadingCopies.value = true
  try {
    const response = await axios.get<{ success: boolean; message: string; data: PageResponse<BookCopy> }>(
      `/api/book-items/book/${selectedBook.value.bookId}`,
      {
        params: {
          page: 0,
          size: 100,
          status: 'Available'
        }
      }
    )

    if (response.data.success && response.data.data) {
      bookCopies.value = response.data.data.content
    } else {
      showMessage(response.data.message || '加载书的副本失败', 'error')
    }
  } catch (error) {
    console.error('Load book copies error:', error)
    const axiosError = error as AxiosError
    showMessage(axiosError.response?.data?.message || '加载书的副本失败', 'error')
  } finally {
    loadingCopies.value = false
  }
}

// 借阅图书
const borrowBook = async (bookCopy: BookCopy) => {
  borrowingItemId.value = bookCopy.itemId
  try {
    const response = await axios.post<{ success: boolean; message: string }>(
      '/api/borrow-records/borrow',
      { itemId: bookCopy.itemId }
    )

    if (response.data.success) {
      showMessage('借阅成功！', 'success')
      // 重新加载副本列表和图书列表
      await loadBookCopies()
      await searchBooks()
    } else {
      showMessage(response.data.message || '借阅失败', 'error')
    }
  } catch (error) {
    console.error('Borrow book error:', error)
    const axiosError = error as AxiosError
    showMessage(axiosError.response?.data?.message || '借阅失败', 'error')
  } finally {
    borrowingItemId.value = null
  }
}

// 获取状态颜色
const getStatusColor = (status: string): string => {
  const colorMap: Record<string, string> = {
    'Available': 'success',
    'Checked_Out': 'warning',
    'Lost': 'error',
    'Damaged': 'error',
    'Withdrawn': 'grey'
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
  searchBooks()
})
</script>
