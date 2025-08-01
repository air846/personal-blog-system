<template>
  <div class="home">
    <Header />
    
    <!-- 英雄区域 -->
    <div class="hero-section">
      <div class="container">
        <div class="hero-content fade-in-up">
          <h1 class="hero-title gradient-text">欢迎来到我的博客</h1>
          <p class="hero-subtitle">分享技术见解，记录成长足迹</p>
          <div class="hero-stats">
            <div class="stat-item">
              <div class="stat-number">{{ total }}</div>
              <div class="stat-label">文章总数</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ categories.length }}</div>
              <div class="stat-label">分类数量</div>
            </div>
            <div class="stat-item">
              <div class="stat-number">{{ hotArticles.reduce((sum, article) => sum + article.viewCount, 0) }}</div>
              <div class="stat-label">总阅读量</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="container">
      <div class="main-content">
        <!-- 文章列表 -->
        <div class="article-list">
          <div class="section-header">
            <h2 class="section-title gradient-text">最新文章</h2>
            <div class="filter-tabs">
              <button 
                class="filter-tab" 
                :class="{ active: !selectedCategory }"
                @click="filterByCategory(null)"
              >
                全部
              </button>
              <button 
                v-for="category in categories.slice(0, 5)" 
                :key="category.id"
                class="filter-tab" 
                :class="{ active: selectedCategory === category.id }"
                @click="filterByCategory(category.id)"
              >
                {{ category.name }}
              </button>
            </div>
          </div>
          
          <div v-if="loading" class="loading">
            <div class="skeleton-cards">
              <div v-for="i in 3" :key="i" class="skeleton-card modern-card">
                <el-skeleton :rows="4" animated />
              </div>
            </div>
          </div>
          
          <div v-else-if="articles.length === 0" class="empty">
            <div class="empty-state modern-card">
              <div class="empty-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M9 12L11 14L15 10M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <h3>暂无文章</h3>
              <p>还没有发布任何文章，快去创作第一篇吧！</p>
            </div>
          </div>
          
          <div v-else class="articles-grid">
            <div
              v-for="(article, index) in articles"
              :key="article.id"
              class="article-card modern-card fade-in-up"
              :style="{ animationDelay: `${index * 0.1}s` }"
              @click="viewArticle(article.id)"
            >
              <div class="article-header">
                <div class="article-meta-top">
                  <div class="author-info">
                    <el-avatar :size="24" :src="article.authorAvatar">
                      {{ article.authorName?.charAt(0) }}
                    </el-avatar>
                    <span class="author-name">{{ article.authorName }}</span>
                  </div>
                  <span class="article-date">{{ formatDate(article.createTime) }}</span>
                </div>
                <h2 class="article-title">{{ article.title }}</h2>
              </div>
              
              <div class="article-summary">
                {{ article.summary || article.content.substring(0, 150) }}...
              </div>
              
              <div class="article-footer">
                <div class="article-tags" v-if="article.tags && article.tags.length">
                  <el-tag
                    v-for="tag in article.tags.slice(0, 3)"
                    :key="tag.id"
                    size="small"
                    class="modern-tag"
                  >
                    {{ tag.name }}
                  </el-tag>
                </div>
                <div class="article-stats">
                  <span class="stat-item">
                    <el-icon><View /></el-icon>
                    {{ article.viewCount }}
                  </span>
                  <span class="stat-item">
                    <el-icon><Star /></el-icon>
                    {{ article.likeCount }}
                  </span>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 分页 -->
          <div class="pagination" v-if="total > 0">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[9, 18, 36]"
              :total="total"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              class="modern-pagination"
            />
          </div>
        </div>
        
        <!-- 侧边栏 -->
        <div class="sidebar">
          <!-- 热门文章 -->
          <div class="sidebar-card modern-card">
            <div class="card-header">
              <h3 class="card-title">
                <el-icon class="title-icon"><TrendCharts /></el-icon>
                热门文章
              </h3>
            </div>
            <div v-if="hotArticles.length === 0" class="empty-sidebar">
              <div class="empty-icon">
                <el-icon><Document /></el-icon>
              </div>
              <p>暂无热门文章</p>
            </div>
            <div v-else class="hot-articles">
              <div
                v-for="(article, index) in hotArticles"
                :key="article.id"
                class="hot-article-item"
                @click="viewArticle(article.id)"
              >
                <div class="rank-badge" :class="`rank-${index + 1}`">
                  {{ index + 1 }}
                </div>
                <div class="article-info">
                  <h4 class="article-title">{{ article.title }}</h4>
                  <div class="article-meta">
                    <span class="views">
                      <el-icon><View /></el-icon>
                      {{ article.viewCount }}
                    </span>
                    <span class="likes">
                      <el-icon><Star /></el-icon>
                      {{ article.likeCount }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 分类云 -->
          <div class="sidebar-card modern-card">
            <div class="card-header">
              <h3 class="card-title">
                <el-icon class="title-icon"><Collection /></el-icon>
                文章分类
              </h3>
            </div>
            <div v-if="categories.length === 0" class="empty-sidebar">
              <div class="empty-icon">
                <el-icon><Folder /></el-icon>
              </div>
              <p>暂无分类</p>
            </div>
            <div v-else class="categories-cloud">
              <div
                v-for="category in categories"
                :key="category.id"
                class="category-item"
                @click="filterByCategory(category.id)"
              >
                <span class="category-name">{{ category.name }}</span>
                <span class="category-count">{{ category.articleCount || 0 }}</span>
              </div>
            </div>
          </div>
          
          <!-- 标签云 -->
          <div class="sidebar-card modern-card">
            <div class="card-header">
              <h3 class="card-title">
                <el-icon class="title-icon"><PriceTag /></el-icon>
                标签云
              </h3>
            </div>
            <div v-if="tags.length === 0" class="empty-sidebar">
              <div class="empty-icon">
                <el-icon><PriceTag /></el-icon>
              </div>
              <p>暂无标签</p>
            </div>
            <div v-else class="tags-cloud">
              <el-tag
                v-for="tag in tags"
                :key="tag.id"
                class="tag-item"
                @click="filterByTag(tag.id)"
              >
                {{ tag.name }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { articleApi } from '@/api/article'
import { categoryApi } from '@/api/category'
import { tagApi } from '@/api/tag'
import Header from '@/components/Header.vue'
import {
  View,
  Star,
  TrendCharts,
  Document,
  Collection,
  Folder,
  PriceTag
} from '@element-plus/icons-vue'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const articles = ref([])
const hotArticles = ref([])
const categories = ref([])
const tags = ref([])
const currentPage = ref(1)
const pageSize = ref(9)
const total = ref(0)
const selectedCategory = ref(null)

// 获取文章列表
const getArticles = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value,
      status: 'PUBLISHED'
    }
    
    if (selectedCategory.value) {
      params.categoryId = selectedCategory.value
    }
    
    const response = await articleApi.getArticles(params)
    articles.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('获取文章列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取热门文章
const getHotArticles = async () => {
  try {
    const response = await articleApi.getHotArticles()
    hotArticles.value = response.data
  } catch (error) {
    console.error('获取热门文章失败:', error)
  }
}

// 获取分类列表
const getCategories = async () => {
  try {
    const response = await categoryApi.getCategories()
    categories.value = response.data
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 获取标签列表
const getTags = async () => {
  try {
    const response = await tagApi.getTags()
    tags.value = response.data
  } catch (error) {
    console.error('获取标签列表失败:', error)
  }
}

// 按分类筛选
const filterByCategory = (categoryId) => {
  selectedCategory.value = categoryId
  currentPage.value = 1
  getArticles()
}

// 按标签筛选
const filterByTag = (tagId) => {
  // 这里可以实现按标签筛选的逻辑
  console.log('按标签筛选:', tagId)
}

// 查看文章
const viewArticle = (id) => {
  router.push(`/article/${id}`)
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  getArticles()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  getArticles()
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 组件挂载时获取数据
onMounted(() => {
  getArticles()
  getHotArticles()
  getCategories()
  getTags()
})
</script>

<style scoped>
/* 全局样式 */
.home {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 英雄区域 */
.hero-section {
  padding: 80px 0;
  color: white;
  text-align: center;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 700;
  margin-bottom: 20px;
  background: linear-gradient(45deg, #fff, #f0f0f0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-subtitle {
  font-size: 1.2rem;
  margin-bottom: 40px;
  opacity: 0.9;
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 60px;
  margin-top: 40px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 0.9rem;
  opacity: 0.8;
}

/* 主要内容区域 */
.main-content {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 30px;
  padding: 40px 0;
}

/* 文章列表 */
.article-list {
  background: white;
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.section-title {
  font-size: 1.8rem;
  font-weight: 600;
  margin: 0;
  background: linear-gradient(45deg, #667eea, #764ba2);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.filter-tabs {
  display: flex;
  gap: 10px;
}

.filter-tab {
  padding: 8px 16px;
  border: 1px solid #e0e0e0;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.filter-tab:hover {
  border-color: #667eea;
  color: #667eea;
}

.filter-tab.active {
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
  border-color: transparent;
}

/* 文章网格 */
.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 25px;
  margin-bottom: 30px;
}

.article-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
  height: fit-content;
}

.article-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  border-color: #667eea;
}

.article-header {
  margin-bottom: 15px;
}

.article-meta-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-name {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.article-date {
  font-size: 12px;
  color: #999;
}

.article-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #333;
  margin: 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-summary {
  color: #666;
  line-height: 1.6;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.article-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.modern-tag {
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 12px;
  font-size: 12px;
}

.article-stats {
  display: flex;
  gap: 15px;
  font-size: 14px;
  color: #999;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 侧边栏 */
.sidebar {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.sidebar-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.card-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.card-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
  color: #333;
}

.title-icon {
  color: #667eea;
}

/* 热门文章 */
.hot-article-item {
  display: flex;
  gap: 12px;
  padding: 12px 0;
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 8px;
}

.hot-article-item:hover {
  background: #f8f9ff;
  padding-left: 8px;
  padding-right: 8px;
}

.rank-badge {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  color: white;
  flex-shrink: 0;
}

.rank-1 { background: #ff6b6b; }
.rank-2 { background: #4ecdc4; }
.rank-3 { background: #45b7d1; }
.rank-badge:not(.rank-1):not(.rank-2):not(.rank-3) {
  background: #95a5a6;
}

.article-info {
  flex: 1;
  min-width: 0;
}

.hot-article-item .article-title {
  font-size: 14px;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.hot-article-item .article-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #999;
}

/* 分类云 */
.categories-cloud {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.category-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #f0f0f0;
}

.category-item:hover {
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
  border-color: transparent;
}

.category-name {
  font-weight: 500;
}

.category-count {
  font-size: 12px;
  opacity: 0.7;
}

/* 标签云 */
.tags-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  cursor: pointer;
  transition: all 0.3s;
  border-radius: 12px;
}

.tag-item:hover {
  background: linear-gradient(45deg, #667eea, #764ba2);
  color: white;
  border-color: transparent;
}

/* 空状态 */
.empty-sidebar {
  text-align: center;
  padding: 20px;
  color: #999;
}

.empty-icon {
  font-size: 2rem;
  margin-bottom: 10px;
  opacity: 0.5;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state .empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.empty-state h3 {
  margin: 0 0 10px 0;
  color: #666;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.modern-pagination {
  --el-pagination-button-color: #667eea;
  --el-pagination-hover-color: #764ba2;
}

/* 骨架屏 */
.skeleton-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 25px;
}

.skeleton-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  border: 1px solid #f0f0f0;
}

/* 动画 */
.fade-in-up {
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-stats {
    gap: 30px;
  }
  
  .section-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .articles-grid {
    grid-template-columns: 1fr;
  }
  
  .filter-tabs {
    flex-wrap: wrap;
  }
}
</style>