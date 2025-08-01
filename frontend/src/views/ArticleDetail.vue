<template>
  <div class="article-detail">
    <Header />
    
    <div class="container">
      <div class="main-content">
        <!-- 文章内容 -->
        <div class="article-content">
          <div v-if="loading" class="loading">
            <el-skeleton :rows="10" animated />
          </div>
          
          <div v-else-if="!article" class="empty">
            <el-empty description="文章不存在" />
          </div>
          
          <div v-else class="article">
            <!-- 文章头部 -->
            <div class="article-header">
              <h1 class="article-title">{{ article.title }}</h1>
              <div class="article-meta">
                <div class="meta-left">
                  <span class="author">
                    <el-avatar :size="24" :src="article.authorAvatar">
                      {{ article.authorName?.charAt(0) }}
                    </el-avatar>
                    {{ article.authorName }}
                  </span>
                  <span class="date">{{ formatDate(article.createTime) }}</span>
                  <span class="category" v-if="article.categoryName">
                    <el-tag size="small">{{ article.categoryName }}</el-tag>
                  </span>
                </div>
                <div class="meta-right">
                  <span class="views">{{ article.viewCount }} 阅读</span>
                  <span class="likes">
                    <el-button
                      :type="article.isLiked ? 'primary' : 'default'"
                      size="small"
                      @click="handleLike"
                      :loading="likeLoading"
                    >
                      <el-icon><ThumbsUp /></el-icon>
                      {{ article.likeCount }} 点赞
                    </el-button>
                  </span>
                </div>
              </div>
            </div>
            
            <!-- 文章标签 -->
            <div class="article-tags" v-if="article.tags && article.tags.length">
              <el-tag
                v-for="tag in article.tags"
                :key="tag.id"
                size="small"
                type="info"
              >
                {{ tag.name }}
              </el-tag>
            </div>
            
            <!-- 文章内容 -->
            <div class="article-body">
              <div class="markdown-content" v-html="renderedContent"></div>
            </div>
            
            <!-- 文章操作 -->
            <div class="article-actions" v-if="isAuthor">
              <el-button type="primary" @click="editArticle">编辑文章</el-button>
              <el-button type="danger" @click="deleteArticle">删除文章</el-button>
              <el-button 
                v-if="article.status === 'DRAFT'" 
                type="success" 
                @click="publishArticle"
              >
                发布文章
              </el-button>
            </div>
          </div>
        </div>
        
        <!-- 侧边栏 -->
        <div class="sidebar">
          <!-- 作者信息 -->
          <div class="sidebar-card">
            <h3>作者信息</h3>
            <div class="author-info" v-if="article">
              <el-avatar :size="60" :src="article.authorAvatar">
                {{ article.authorName?.charAt(0) }}
              </el-avatar>
              <h4>{{ article.authorName }}</h4>
              <p>{{ article.authorEmail }}</p>
            </div>
          </div>
          
          <!-- 相关文章 -->
          <div class="sidebar-card">
            <h3>相关文章</h3>
            <div v-if="relatedArticles.length === 0" class="empty">
              <el-empty description="暂无相关文章" :image-size="60" />
            </div>
            <div v-else class="related-articles">
              <div
                v-for="relatedArticle in relatedArticles"
                :key="relatedArticle.id"
                class="related-article-item"
                @click="viewArticle(relatedArticle.id)"
              >
                <h4>{{ relatedArticle.title }}</h4>
                <div class="meta">
                  <span>{{ formatDate(relatedArticle.createTime) }}</span>
                  <span>{{ relatedArticle.viewCount }}阅读</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { articleApi } from '@/api/article'
import Header from '@/components/Header.vue'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import { ThumbsUp } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const loading = ref(false)
const likeLoading = ref(false)
const article = ref(null)
const relatedArticles = ref([])

// Markdown渲染器
const md = new MarkdownIt({
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(str, { language: lang }).value
      } catch (__) {}
    }
    return '' // 使用默认的转义
  }
})

// 计算属性
const renderedContent = computed(() => {
  if (!article.value?.content) return ''
  return md.render(article.value.content)
})

const isAuthor = computed(() => {
  return userStore.isLoggedIn && article.value?.authorId === userStore.userInfo?.id
})

// 获取文章详情
const fetchArticleDetail = async () => {
  loading.value = true
  try {
    const response = await articleApi.getArticleDetail(route.params.id)
    article.value = response.data
  } catch (error) {
    console.error('获取文章详情失败:', error)
  } finally {
    loading.value = false
  }
}

// 获取相关文章
const fetchRelatedArticles = async () => {
  try {
    const response = await articleApi.getRecommendArticles(5)
    relatedArticles.value = response.data.filter(item => item.id !== parseInt(route.params.id))
  } catch (error) {
    console.error('获取相关文章失败:', error)
  }
}

// 点赞/取消点赞
const handleLike = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  
  likeLoading.value = true
  try {
    if (article.value.isLiked) {
      await articleApi.unlikeArticle(article.value.id)
      article.value.likeCount--
      article.value.isLiked = false
      ElMessage.success('取消点赞成功')
    } else {
      await articleApi.likeArticle(article.value.id)
      article.value.likeCount++
      article.value.isLiked = true
      ElMessage.success('点赞成功')
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    ElMessage.error('操作失败，请重试')
  } finally {
    likeLoading.value = false
  }
}

// 编辑文章
const editArticle = () => {
  router.push(`/edit/${article.value.id}`)
}

// 删除文章
const deleteArticle = async () => {
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await articleApi.deleteArticle(article.value.id)
    ElMessage.success('删除成功')
    router.push('/')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除文章失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

// 发布文章
const publishArticle = async () => {
  try {
    await articleApi.publishArticle(article.value.id)
    article.value.status = 'PUBLISHED'
    ElMessage.success('发布成功')
  } catch (error) {
    console.error('发布文章失败:', error)
    ElMessage.error('发布失败，请重试')
  }
}

// 查看文章
const viewArticle = (id) => {
  router.push(`/article/${id}`)
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 组件挂载时获取数据
onMounted(() => {
  fetchArticleDetail()
  fetchRelatedArticles()
})
</script>

<style scoped>
.article-detail {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.main-content {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 30px;
}

.article-content {
  background: white;
  border-radius: 8px;
  padding: 40px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.article-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.article-title {
  font-size: 2rem;
  font-weight: 700;
  color: #333;
  margin: 0 0 20px 0;
  line-height: 1.3;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.meta-left {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

.meta-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.author {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  color: #333;
}

.date {
  color: #666;
  font-size: 14px;
}

.views {
  color: #999;
  font-size: 14px;
}

.article-tags {
  margin-bottom: 30px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.article-body {
  margin-bottom: 30px;
}

.markdown-content {
  line-height: 1.8;
  color: #333;
  font-size: 16px;
}

.markdown-content h1,
.markdown-content h2,
.markdown-content h3,
.markdown-content h4,
.markdown-content h5,
.markdown-content h6 {
  margin: 30px 0 15px 0;
  font-weight: 600;
  color: #2c3e50;
}

.markdown-content h1 {
  font-size: 2rem;
  border-bottom: 2px solid #eee;
  padding-bottom: 10px;
}

.markdown-content h2 {
  font-size: 1.5rem;
}

.markdown-content h3 {
  font-size: 1.25rem;
}

.markdown-content p {
  margin: 15px 0;
}

.markdown-content code {
  background: #f8f8f8;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 0.9em;
  color: #e74c3c;
}

.markdown-content pre {
  background: #f8f8f8;
  padding: 15px;
  border-radius: 5px;
  overflow-x: auto;
  margin: 20px 0;
}

.markdown-content pre code {
  background: none;
  padding: 0;
  color: inherit;
}

.markdown-content blockquote {
  border-left: 4px solid #ddd;
  padding-left: 15px;
  margin: 20px 0;
  color: #666;
  font-style: italic;
}

.markdown-content ul,
.markdown-content ol {
  padding-left: 30px;
  margin: 15px 0;
}

.markdown-content li {
  margin: 5px 0;
}

.markdown-content table {
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0;
}

.markdown-content th,
.markdown-content td {
  border: 1px solid #ddd;
  padding: 8px 12px;
  text-align: left;
}

.markdown-content th {
  background: #f5f5f5;
  font-weight: 600;
}

.article-actions {
  display: flex;
  gap: 10px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.sidebar {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sidebar-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.sidebar-card h3 {
  margin: 0 0 15px 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: #333;
}

.author-info {
  text-align: center;
}

.author-info h4 {
  margin: 10px 0 5px 0;
  color: #333;
}

.author-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.related-article-item {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;
}

.related-article-item:hover {
  background: #f9f9f9;
  border-radius: 4px;
  padding-left: 10px;
  padding-right: 10px;
}

.related-article-item:last-child {
  border-bottom: none;
}

.related-article-item h4 {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #333;
  line-height: 1.4;
}

.related-article-item .meta {
  display: flex;
  gap: 10px;
  font-size: 12px;
  color: #999;
}

.loading {
  padding: 40px;
}

.empty {
  text-align: center;
  padding: 40px;
}

@media (max-width: 768px) {
  .main-content {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .article-content {
    padding: 20px;
  }
  
  .article-title {
    font-size: 1.5rem;
  }
  
  .article-meta {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .meta-left,
  .meta-right {
    width: 100%;
    justify-content: space-between;
  }
}
</style>