<template>
  <div class="profile">
    <div class="container">
      <!-- 用户信息 -->
      <div class="user-info">
        <div class="user-avatar">
          <el-avatar :size="80" :src="userStore.user?.avatar">
            {{ userStore.user?.nickname?.charAt(0) || 'U' }}
          </el-avatar>
        </div>
        <div class="user-details">
          <h2>{{ userStore.user?.nickname || '未设置昵称' }}</h2>
          <p>@{{ userStore.user?.username }}</p>
          <p>{{ userStore.user?.email }}</p>
        </div>
        <div class="user-actions">
          <el-button type="primary" @click="showEditDialog = true">编辑资料</el-button>
          <el-button @click="$router.push('/write')">写文章</el-button>
        </div>
      </div>
      
      <!-- 文章列表 -->
      <div class="article-section">
        <div class="section-header">
          <h3>我的文章</h3>
          <div class="article-stats">
            <span>共 {{ total }} 篇文章</span>
          </div>
        </div>
        
        <div class="article-filters">
          <el-radio-group v-model="statusFilter" @change="getArticleList">
            <el-radio-button :label="-1">全部</el-radio-button>
            <el-radio-button :label="0">草稿</el-radio-button>
            <el-radio-button :label="1">已发布</el-radio-button>
          </el-radio-group>
        </div>
        
        <div v-loading="loading" class="article-list">
          <div v-if="articles.length === 0 && !loading" class="empty-state">
            <el-empty description="暂无文章" />
            <el-button type="primary" @click="$router.push('/write')">写第一篇文章</el-button>
          </div>
          
          <div v-else>
            <div
              v-for="article in articles"
              :key="article.id"
              class="article-item"
            >
              <div class="article-content" @click="viewArticle(article)">
                <h4 class="article-title">{{ article.title }}</h4>
                <p class="article-summary">{{ article.summary }}</p>
                <div class="article-meta">
                  <span class="status" :class="getStatusClass(article.status)">
                    {{ getStatusText(article.status) }}
                  </span>
                  <span class="date">{{ formatDate(article.createTime) }}</span>
                  <span class="stats">
                    <el-icon><View /></el-icon>
                    {{ article.viewCount || 0 }}
                  </span>
                  <span class="stats">
                    <el-icon><Star /></el-icon>
                    {{ article.likeCount || 0 }}
                  </span>
                </div>
              </div>
              
              <div class="article-actions">
                <el-button size="small" @click="editArticle(article)">编辑</el-button>
                <el-button
                  v-if="article.status === 0"
                  size="small"
                  type="primary"
                  @click="publishArticle(article)"
                >
                  发布
                </el-button>
                <el-button
                  size="small"
                  type="danger"
                  @click="deleteArticle(article)"
                >
                  删除
                </el-button>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 分页 -->
        <div v-if="total > pageSize" class="pagination">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="getArticleList"
            @current-change="getArticleList"
          />
        </div>
      </div>
    </div>
    
    <!-- 编辑用户信息对话框 -->
    <el-dialog
      v-model="showEditDialog"
      title="编辑资料"
      width="500px"
    >
      <el-form
        ref="userFormRef"
        :model="userForm"
        :rules="userRules"
        label-width="80px"
      >
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
        </el-form-item>
        
        <el-form-item label="头像">
          <el-input v-model="userForm.avatar" placeholder="请输入头像URL" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="updateUserInfo" :loading="updateLoading">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { articleApi } from '../api/article'
import { View, Star } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const userFormRef = ref()

const loading = ref(false)
const updateLoading = ref(false)
const showEditDialog = ref(false)
const articles = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const statusFilter = ref(-1)

// 用户表单数据
const userForm = reactive({
  nickname: '',
  avatar: ''
})

// 表单验证规则
const userRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ]
}

// 获取文章列表
const getArticleList = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      size: pageSize.value
    }
    
    if (statusFilter.value !== -1) {
      params.status = statusFilter.value
    }
    
    const response = await articleApi.getUserArticles(params)
    if (response.code === 200) {
      articles.value = response.data.records || []
      total.value = response.data.total || 0
    } else {
      ElMessage.error(response.message || '获取文章列表失败')
    }
  } catch (error) {
    ElMessage.error('获取文章列表失败')
  } finally {
    loading.value = false
  }
}

// 查看文章
const viewArticle = (article) => {
  if (article.status === 1) {
    router.push(`/article/${article.id}`)
  } else {
    ElMessage.warning('草稿状态的文章无法查看')
  }
}

// 编辑文章
const editArticle = (article) => {
  router.push(`/edit/${article.id}`)
}

// 发布文章
const publishArticle = async (article) => {
  try {
    const response = await articleApi.publishArticle(article.id)
    if (response.code === 200) {
      ElMessage.success('文章发布成功')
      getArticleList()
    } else {
      ElMessage.error(response.message || '发布失败')
    }
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

// 删除文章
const deleteArticle = async (article) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除文章「${article.title}」吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    const response = await articleApi.deleteArticle(article.id)
    if (response.code === 200) {
      ElMessage.success('删除成功')
      getArticleList()
    } else {
      ElMessage.error(response.message || '删除失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 更新用户信息
const updateUserInfo = async () => {
  if (!userFormRef.value) return
  
  try {
    const valid = await userFormRef.value.validate()
    if (!valid) return
    
    updateLoading.value = true
    await userStore.updateUserInfo(userForm)
    showEditDialog.value = false
    ElMessage.success('资料更新成功')
  } catch (error) {
    ElMessage.error('更新失败')
  } finally {
    updateLoading.value = false
  }
}

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 0:
      return '草稿'
    case 1:
      return '已发布'
    default:
      return '未知'
  }
}

// 获取状态样式类
const getStatusClass = (status) => {
  switch (status) {
    case 0:
      return 'draft'
    case 1:
      return 'published'
    default:
      return ''
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 页面加载时获取数据
onMounted(() => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  // 初始化用户表单数据
  userForm.nickname = userStore.user?.nickname || ''
  userForm.avatar = userStore.user?.avatar || ''
  
  getArticleList()
})
</script>

<style scoped>
.profile {
  padding: 20px 0;
}

.user-info {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-details {
  flex: 1;
}

.user-details h2 {
  margin: 0 0 5px 0;
  color: #333;
}

.user-details p {
  margin: 5px 0;
  color: #666;
}

.user-actions {
  display: flex;
  gap: 10px;
}

.article-section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  color: #333;
}

.article-stats {
  color: #666;
  font-size: 14px;
}

.article-filters {
  margin-bottom: 20px;
}

.article-list {
  min-height: 200px;
}

.empty-state {
  text-align: center;
  padding: 40px;
}

.article-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  margin-bottom: 15px;
  transition: all 0.3s;
}

.article-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
}

.article-content {
  flex: 1;
  cursor: pointer;
}

.article-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.article-summary {
  color: #666;
  line-height: 1.6;
  margin: 0 0 10px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-meta {
  display: flex;
  gap: 15px;
  align-items: center;
  font-size: 12px;
  color: #999;
}

.status {
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
}

.status.draft {
  background: #f0f9ff;
  color: #0369a1;
}

.status.published {
  background: #f0fdf4;
  color: #166534;
}

.stats {
  display: flex;
  align-items: center;
  gap: 4px;
}

.article-actions {
  display: flex;
  gap: 8px;
  margin-left: 20px;
}

.pagination {
  margin-top: 30px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-info {
    flex-direction: column;
    text-align: center;
    padding: 20px;
  }
  
  .user-actions {
    justify-content: center;
  }
  
  .article-section {
    padding: 20px;
  }
  
  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .article-item {
    flex-direction: column;
    gap: 15px;
  }
  
  .article-actions {
    margin-left: 0;
    justify-content: flex-start;
  }
  
  .article-meta {
    flex-wrap: wrap;
    gap: 10px;
  }
}
</style>