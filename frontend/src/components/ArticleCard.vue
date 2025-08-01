<template>
  <div class="article-card" @click="$emit('click')">
    <div class="article-cover" v-if="article.cover">
      <img :src="article.cover" :alt="article.title" />
    </div>
    
    <div class="article-content">
      <h3 class="article-title">{{ article.title }}</h3>
      
      <p class="article-summary">{{ article.summary }}</p>
      
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
    </div>
  </div>
</template>

<script setup>
import { View, Star } from '@element-plus/icons-vue'

defineProps({
  article: {
    type: Object,
    required: true
  }
})

defineEmits(['click'])

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.article-card {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
  overflow: hidden;
}

.article-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.article-cover {
  width: 100%;
  height: 200px;
  overflow: hidden;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.article-card:hover .article-cover img {
  transform: scale(1.05);
}

.article-content {
  padding: 20px;
}

.article-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 10px 0;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-summary {
  color: #666;
  line-height: 1.6;
  margin: 0 0 15px 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 12px;
  color: #999;
}

.meta-left {
  display: flex;
  gap: 15px;
}

.meta-right {
  display: flex;
  gap: 15px;
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

/* 响应式设计 */
@media (max-width: 768px) {
  .article-cover {
    height: 150px;
  }
  
  .article-content {
    padding: 15px;
  }
  
  .article-title {
    font-size: 16px;
  }
  
  .meta-left {
    gap: 10px;
  }
  
  .meta-right {
    gap: 10px;
  }
}
</style>