<template>
  <div class="search-page">
    <Header />
    
    <div class="container">
      <div class="search-content">
        <div class="search-header">
          <h2>搜索结果</h2>
          <p v-if="keyword">关键词：{{ keyword }}</p>
          <p v-if="total > 0">共找到 {{ total }} 篇文章</p>
        </div>
        
        <div v-if="loading" class="loading">
          <el-skeleton :rows="3" animated />
        </div>
        
        <div v-else-if="articles.length === 0" class="empty">
          <el-empty description="没有找到相关文章" />
        </div>
        
        <div v-else class="search-results">
          <div
            v-for="article in articles"
            :key="article.id"
            class="search-item"
            @click="viewArticle(article.id)"
          >
            <h3 class="article-title">{{ article.title }}</h3>
            <p class="article-summary">{{ article.summary || article.content.substring(0, 200) }}...</p>
            <div class="article-meta">
              <span class="author">{{ article.authorName }}</span>
              <span class="date">{{ formatDate(article.createTime) }}</span>
              <span class="views">{{ article.viewCount }} 阅读</span>
              <span class="likes">{{ article.likeCount }} 点赞</span>
            </div>
          </div>
        </div>
        
        <!-- 分页 -->
        <div class="pagination" v-if="total > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { articleApi } from '@/api/article'
import Header from '@/components/Header.vue'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const articles = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 计算属性
const keyword = computed(() => route.query.keyword || '')

// 搜索文章
const searchArticles = async () => {
  if (!keyword.value) return
  
  loading.value = true
  try {
    const response = await articleApi.searchArticles(
      keyword.value,
      currentPage.value,
      pageSize.value
    )
    articles.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('搜索失败:', error)
  } finally {
    loading.value = false
  }
}

// 查看文章
const viewArticle = (id) => {
  router.push(`/article/${id}`)
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  searchArticles()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  searchArticles()
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 监听路由变化
watch(() => route.query.keyword, () => {
  currentPage.value = 1
  searchArticles()
})

// 组件挂载时搜索
onMounted(() => {
  searchArticles()
})
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.search-content {
  background: white;
  border-radius: 8px;
  padding: 30px;
  margin: 20px 0;
}

.search-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.search-header h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #333;
}

.search-header p {
  margin: 5px 0;
  color: #666;
  font-size: 14px;
}

.search-results {
  margin-bottom: 30px;
}

.search-item {
  padding: 20px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;
}

.search-item:hover {
  background-color: #f9f9f9;
  border-radius: 4px;
  padding-left: 10px;
  padding-right: 10px;
}

.search-item:last-child {
  border-bottom: none;
}

.article-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 10px 0;
  line-height: 1.4;
}

.article-summary {
  color: #666;
  line-height: 1.6;
  margin-bottom: 10px;
}

.article-meta {
  display: flex;
  gap: 15px;
  font-size: 14px;
  color: #999;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.empty {
  text-align: center;
  padding: 40px;
}

.loading {
  padding: 20px;
}

@media (max-width: 768px) {
  .article-meta {
    flex-wrap: wrap;
    gap: 10px;
  }
}
</style>