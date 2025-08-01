<template>
  <div class="search-page">
    <div class="container">
      <div class="search-header">
        <h2>搜索结果</h2>
        <p v-if="keyword">关键词："{{ keyword }}"</p>
        <p v-if="total > 0">找到 {{ total }} 篇相关文章</p>
      </div>
      
      <div v-loading="loading" class="search-results">
        <div v-if="articles.length === 0 && !loading" class="empty-state">
          <el-empty description="没有找到相关文章" />
          <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
        </div>
        
        <div v-else class="article-list">
          <ArticleCard
            v-for="article in articles"
            :key="article.id"
            :article="article"
            @click="viewArticle(article.id)"
          />
        </div>
        
        <!-- 分页 -->
        <div v-if="total > pageSize" class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="searchArticles"
            @current-change="searchArticles"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { articleApi } from '../api/article'
import ArticleCard from '../components/ArticleCard.vue'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const articles = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const keyword = ref('')

// 搜索文章
const searchArticles = async () => {
  if (!keyword.value) return
  
  loading.value = true
  try {
    const response = await articleApi.searchArticles({
      keyword: keyword.value,
      page: currentPage.value,
      size: pageSize.value
    })
    
    if (response.code === 200) {
      articles.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      ElMessage.error(response.message || '搜索失败')
    }
  } catch (error) {
    ElMessage.error('搜索失败')
  } finally {
    loading.value = false
  }
}

// 查看文章详情
const viewArticle = (id) => {
  router.push(`/article/${id}`)
}

// 监听路由参数变化
watch(
  () => route.query.q,
  (newKeyword) => {
    if (newKeyword) {
      keyword.value = newKeyword
      currentPage.value = 1
      searchArticles()
    }
  },
  { immediate: true }
)

// 页面加载时执行搜索
onMounted(() => {
  keyword.value = route.query.q || ''
  if (keyword.value) {
    searchArticles()
  }
})
</script>

<style scoped>
.search-page {
  padding: 20px 0;
}

.search-header {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 20px;
  text-align: center;
}

.search-header h2 {
  margin: 0 0 10px 0;
  color: #333;
}

.search-header p {
  margin: 5px 0;
  color: #666;
}

.search-results {
  min-height: 400px;
}

.empty-state {
  text-align: center;
  padding: 60px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.article-list {
  display: grid;
  gap: 20px;
}

.pagination {
  margin-top: 30px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-header {
    padding: 20px;
  }
  
  .empty-state {
    padding: 40px 20px;
  }
}
</style>