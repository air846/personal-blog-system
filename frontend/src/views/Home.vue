<template>
  <div class="home">
    <div class="container">
      <div class="home-content">
        <!-- 主要内容区 -->
        <div class="main-content">
          <!-- 文章列表 -->
          <div class="article-list">
            <div class="section-header">
              <h2>最新文章</h2>
              <div class="category-filter">
                <el-button
                  :type="selectedCategory === null ? 'primary' : ''"
                  @click="filterByCategory(null)"
                  size="small"
                >
                  全部
                </el-button>
                <el-button
                  v-for="category in categories"
                  :key="category.id"
                  :type="selectedCategory === category.id ? 'primary' : ''"
                  @click="filterByCategory(category.id)"
                  size="small"
                >
                  {{ category.name }}
                </el-button>
              </div>
            </div>
            
            <div v-loading="loading" class="articles">
              <div v-if="articles.length === 0 && !loading" class="empty-state">
                <el-empty description="暂无文章" />
              </div>
              
              <article-card
                v-for="article in articles"
                :key="article.id"
                :article="article"
                @click="goToArticle(article.id)"
              />
            </div>
            
            <!-- 分页 -->
            <div class="pagination" v-if="total > 0">
              <el-pagination
                v-model:current-page="currentPage"
                v-model:page-size="pageSize"
                :total="total"
                :page-sizes="[10, 20, 50]"
                layout="total, sizes, prev, pager, next, jumper"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
              />
            </div>
          </div>
        </div>
        
        <!-- 侧边栏 -->
        <div class="sidebar">
          <!-- 热门文章 -->
          <div class="sidebar-section">
            <h3>热门文章</h3>
            <div v-loading="hotLoading" class="hot-articles">
              <div
                v-for="article in hotArticles"
                :key="article.id"
                class="hot-article-item"
                @click="goToArticle(article.id)"
              >
                <h4>{{ article.title }}</h4>
                <div class="article-meta">
                  <span>阅读 {{ article.viewCount }}</span>
                  <span>点赞 {{ article.likeCount }}</span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 分类列表 -->
          <div class="sidebar-section">
            <h3>文章分类</h3>
            <div class="category-list">
              <div
                v-for="category in categories"
                :key="category.id"
                class="category-item"
                @click="filterByCategory(category.id)"
              >
                <span>{{ category.name }}</span>
                <el-tag size="small">{{ category.articleCount || 0 }}</el-tag>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { articleApi } from '../api/article'
import { categoryApi } from '../api/category'
import ArticleCard from '../components/ArticleCard.vue'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const hotLoading = ref(false)
const articles = ref([])
const hotArticles = ref([])
const categories = ref([])
const selectedCategory = ref(null)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 获取文章列表
const getArticleList = async () => {
  loading.value = true
  try {
    const response = await articleApi.getArticleList({
      page: currentPage.value,
      size: pageSize.value,
      categoryId: selectedCategory.value
    })
    
    if (response.code === 200) {
      articles.value = response.data.records
      total.value = response.data.total
    }
  } catch (error) {
    ElMessage.error('获取文章列表失败')
  } finally {
    loading.value = false
  }
}

// 获取热门文章
const getHotArticles = async () => {
  hotLoading.value = true
  try {
    const response = await articleApi.getHotArticles(5)
    if (response.code === 200) {
      hotArticles.value = response.data
    }
  } catch (error) {
    console.error('获取热门文章失败:', error)
  } finally {
    hotLoading.value = false
  }
}

// 获取分类列表
const getCategoryList = async () => {
  try {
    const response = await categoryApi.getCategoryList()
    if (response.code === 200) {
      categories.value = response.data
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 按分类筛选
const filterByCategory = (categoryId) => {
  selectedCategory.value = categoryId
  currentPage.value = 1
  getArticleList()
}

// 跳转到文章详情
const goToArticle = (id) => {
  router.push(`/article/${id}`)
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  getArticleList()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  getArticleList()
}

// 监听分类变化
watch(selectedCategory, () => {
  getArticleList()
})

// 页面加载时获取数据
onMounted(() => {
  getArticleList()
  getHotArticles()
  getCategoryList()
})
</script>

<style scoped>
.home {
  padding: 20px 0;
}

.home-content {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 30px;
}

.main-content {
  min-height: 500px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
}

.section-header h2 {
  margin: 0;
  color: #333;
}

.category-filter {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.articles {
  min-height: 400px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

.pagination {
  margin-top: 30px;
  text-align: center;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sidebar-section {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.sidebar-section h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
  border-bottom: 2px solid #409eff;
  padding-bottom: 8px;
}

.hot-article-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;
}

.hot-article-item:last-child {
  border-bottom: none;
}

.hot-article-item:hover {
  background-color: #f8f9fa;
  padding-left: 8px;
}

.hot-article-item h4 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #999;
}

.category-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.category-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.category-item:hover {
  background-color: #f0f9ff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .home-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .category-filter {
    width: 100%;
  }
}
</style>