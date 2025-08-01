<template>
  <div class="article-detail">
    <div class="container">
      <div v-loading="loading" class="article-container">
        <div v-if="article" class="article">
          <!-- 文章头部 -->
          <header class="article-header">
            <h1 class="article-title">{{ article.title }}</h1>
            
            <div class="article-meta">
              <div class="meta-left">
                <span class="author">{{ article.authorName || '匿名' }}</span>
                <span class="date">{{ formatDate(article.publishTime) }}</span>
                <span class="category" v-if="article.categoryName">
                  {{ article.categoryName }}
                </span>
              </div>
              
              <div class="meta-right">
                <span class="stat">
                  <el-icon><View /></el-icon>
                  {{ article.viewCount || 0 }}
                </span>
                <span class="stat">
                  <el-icon><Star /></el-icon>
                  {{ article.likeCount || 0 }}
                </span>
              </div>
            </div>
            
            <div class="article-tags" v-if="article.tags && article.tags.length">
              <el-tag
                v-for="tag in article.tags"
                :key="tag.id"
                size="small"
                :color="tag.color"
              >
                {{ tag.name }}
              </el-tag>
            </div>
          </header>
          
          <!-- 文章内容 -->
          <div class="article-content">
            <div v-html="renderedContent" class="markdown-body"></div>
          </div>
          
          <!-- 文章操作 -->
          <div class="article-actions">
            <el-button
              :type="isLiked ? 'primary' : ''"
              :icon="Star"
              @click="toggleLike"
              :loading="likeLoading"
            >
              {{ isLiked ? '已点赞' : '点赞' }} ({{ article.likeCount || 0 }})
            </el-button>
          </div>
        </div>
        
        <div v-else-if="!loading" class="not-found">
          <el-empty description="文章不存在" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { articleApi } from '../api/article'
import { useUserStore } from '../stores/user'
import { View, Star } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

const route = useRoute()
const userStore = useUserStore()

const loading = ref(false)
const likeLoading = ref(false)
const article = ref(null)
const isLiked = ref(false)

// Markdown渲染器
const md = new MarkdownIt({
  highlight: function (str, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(str, { language: lang }).value
      } catch (__) {}
    }
    return ''
  }
})

// 渲染后的内容
const renderedContent = computed(() => {
  if (!article.value?.content) return ''
  return md.render(article.value.content)
})

// 获取文章详情
const getArticleDetail = async () => {
  loading.value = true
  try {
    const response = await articleApi.getArticleDetail(route.params.id)
    if (response.code === 200) {
      article.value = response.data
    } else {
      ElMessage.error(response.message || '获取文章失败')
    }
  } catch (error) {
    ElMessage.error('获取文章失败')
  } finally {
    loading.value = false
  }
}

// 切换点赞状态
const toggleLike = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  
  likeLoading.value = true
  try {
    if (isLiked.value) {
      await articleApi.unlikeArticle(article.value.id)
      article.value.likeCount--
      isLiked.value = false
      ElMessage.success('取消点赞成功')
    } else {
      await articleApi.likeArticle(article.value.id)
      article.value.likeCount++
      isLiked.value = true
      ElMessage.success('点赞成功')
    }
  } catch (error) {
    ElMessage.error('操作失败')
  } finally {
    likeLoading.value = false
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

// 页面加载时获取文章
onMounted(() => {
  getArticleDetail()
})
</script>

<style scoped>
.article-detail {
  padding: 20px 0;
}

.article-container {
  max-width: 800px;
  margin: 0 auto;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.article {
  padding: 40px;
}

.article-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.article-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin: 0 0 20px 0;
  line-height: 1.3;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  font-size: 14px;
  color: #666;
}

.meta-left {
  display: flex;
  gap: 20px;
}

.meta-right {
  display: flex;
  gap: 20px;
}

.stat {
  display: flex;
  align-items: center;
  gap: 4px;
}

.article-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.article-content {
  margin-bottom: 30px;
}

.article-actions {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.not-found {
  padding: 60px;
  text-align: center;
}

/* Markdown样式 */
.markdown-body {
  line-height: 1.8;
  color: #333;
}

.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4,
.markdown-body h5,
.markdown-body h6 {
  margin: 30px 0 15px 0;
  font-weight: 600;
  color: #333;
}

.markdown-body h1 {
  font-size: 24px;
}

.markdown-body h2 {
  font-size: 20px;
}

.markdown-body h3 {
  font-size: 18px;
}

.markdown-body p {
  margin: 15px 0;
}

.markdown-body code {
  background: #f6f8fa;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
  font-size: 14px;
}

.markdown-body pre {
  background: #f6f8fa;
  padding: 15px;
  border-radius: 6px;
  overflow-x: auto;
  margin: 15px 0;
}

.markdown-body pre code {
  background: none;
  padding: 0;
}

.markdown-body blockquote {
  border-left: 4px solid #dfe2e5;
  padding-left: 15px;
  margin: 15px 0;
  color: #666;
}

.markdown-body ul,
.markdown-body ol {
  padding-left: 20px;
  margin: 15px 0;
}

.markdown-body li {
  margin: 5px 0;
}

.markdown-body img {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  margin: 15px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .article {
    padding: 20px;
  }
  
  .article-title {
    font-size: 24px;
  }
  
  .article-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .meta-left {
    gap: 15px;
  }
  
  .meta-right {
    gap: 15px;
  }
}
</style>