<template>
  <div class="profile-page">
    <Header />
    
    <div class="container">
      <div class="profile-content">
        <el-tabs v-model="activeTab" class="profile-tabs">
          <el-tab-pane label="个人信息" name="info">
            <div class="tab-content">
              <el-form
                ref="profileFormRef"
                :model="profileForm"
                :rules="profileRules"
                label-width="100px"
              >
                <el-form-item label="头像">
                  <el-avatar :size="80" :src="profileForm.avatar">
                    {{ userStore.nickname?.charAt(0) || userStore.username?.charAt(0) }}
                  </el-avatar>
                </el-form-item>
                
                <el-form-item label="用户名">
                  <el-input v-model="userStore.username" disabled />
                </el-form-item>
                
                <el-form-item label="昵称" prop="nickname">
                  <el-input v-model="profileForm.nickname" />
                </el-form-item>
                
                <el-form-item label="邮箱">
                  <el-input v-model="userStore.userInfo?.email" disabled />
                </el-form-item>
                
                <el-form-item>
                  <el-button type="primary" @click="updateProfile" :loading="updating">
                    保存修改
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="修改密码" name="password">
            <div class="tab-content">
              <el-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                label-width="100px"
              >
                <el-form-item label="原密码" prop="oldPassword">
                  <el-input
                    v-model="passwordForm.oldPassword"
                    type="password"
                    show-password
                  />
                </el-form-item>
                
                <el-form-item label="新密码" prop="newPassword">
                  <el-input
                    v-model="passwordForm.newPassword"
                    type="password"
                    show-password
                  />
                </el-form-item>
                
                <el-form-item label="确认密码" prop="confirmPassword">
                  <el-input
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    show-password
                  />
                </el-form-item>
                
                <el-form-item>
                  <el-button type="primary" @click="changePassword" :loading="changing">
                    修改密码
                  </el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-tab-pane>
          
          <el-tab-pane label="我的文章" name="articles">
            <div class="tab-content">
              <div v-if="loading" class="loading">
                <el-skeleton :rows="3" animated />
              </div>
              
              <div v-else-if="myArticles.length === 0" class="empty">
                <el-empty description="您还没有发布过文章" />
                <el-button type="primary" @click="$router.push('/write')">
                  写第一篇文章
                </el-button>
              </div>
              
              <div v-else class="my-articles">
                <div
                  v-for="article in myArticles"
                  :key="article.id"
                  class="article-item"
                >
                  <div class="article-info">
                    <h3 @click="viewArticle(article.id)">{{ article.title }}</h3>
                    <div class="article-meta">
                      <span class="status">
                        <el-tag :type="article.status === 'PUBLISHED' ? 'success' : 'warning'">
                          {{ article.status === 'PUBLISHED' ? '已发布' : '草稿' }}
                        </el-tag>
                      </span>
                      <span class="date">{{ formatDate(article.createTime) }}</span>
                      <span class="views">{{ article.viewCount }} 阅读</span>
                      <span class="likes">{{ article.likeCount }} 点赞</span>
                    </div>
                  </div>
                  
                  <div class="article-actions">
                    <el-button size="small" @click="editArticle(article.id)">编辑</el-button>
                    <el-button 
                      size="small" 
                      type="danger" 
                      @click="deleteArticle(article.id)"
                    >
                      删除
                    </el-button>
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
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { articleApi } from '@/api/article'
import Header from '@/components/Header.vue'

const router = useRouter()
const userStore = useUserStore()

// 响应式数据
const activeTab = ref('info')
const loading = ref(false)
const updating = ref(false)
const changing = ref(false)
const myArticles = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const profileFormRef = ref()
const passwordFormRef = ref()

const profileForm = reactive({
  nickname: userStore.nickname,
  avatar: userStore.avatar
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const profileRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ]
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const passwordRules = {
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

// 更新个人信息
const updateProfile = async () => {
  if (!profileFormRef.value) return
  
  try {
    await profileFormRef.value.validate()
    updating.value = true
    
    await userStore.updateUserInfo({
      nickname: profileForm.nickname,
      avatar: profileForm.avatar
    })
    
    ElMessage.success('个人信息更新成功')
  } catch (error) {
    console.error('更新个人信息失败:', error)
    ElMessage.error('更新失败，请重试')
  } finally {
    updating.value = false
  }
}

// 修改密码
const changePassword = async () => {
  if (!passwordFormRef.value) return
  
  try {
    await passwordFormRef.value.validate()
    changing.value = true
    
    await userStore.changePassword({
      oldPassword: passwordForm.oldPassword,
      newPassword: passwordForm.newPassword
    })
    
    ElMessage.success('密码修改成功')
    
    // 重置表单
    passwordForm.oldPassword = ''
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error('修改失败，请重试')
  } finally {
    changing.value = false
  }
}

// 获取我的文章
const getMyArticles = async () => {
  loading.value = true
  try {
    const response = await articleApi.getMyArticles({
      page: currentPage.value,
      size: pageSize.value
    })
    myArticles.value = response.data.records
    total.value = response.data.total
  } catch (error) {
    console.error('获取我的文章失败:', error)
  } finally {
    loading.value = false
  }
}

// 查看文章
const viewArticle = (id) => {
  router.push(`/article/${id}`)
}

// 编辑文章
const editArticle = (id) => {
  router.push(`/edit/${id}`)
}

// 删除文章
const deleteArticle = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这篇文章吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await articleApi.deleteArticle(id)
    ElMessage.success('删除成功')
    getMyArticles()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除文章失败:', error)
      ElMessage.error('删除失败，请重试')
    }
  }
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  getMyArticles()
}

const handleCurrentChange = (page) => {
  currentPage.value = page
  getMyArticles()
}

// 格式化日期
const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN')
}

// 监听标签页切换
watch(() => activeTab.value, (newTab) => {
  if (newTab === 'articles') {
    getMyArticles()
  }
})

// 组件挂载时获取用户信息
onMounted(() => {
  if (!userStore.isLoggedIn) {
    router.push('/login')
    return
  }
  
  // 同步用户信息到表单
  profileForm.nickname = userStore.nickname
  profileForm.avatar = userStore.avatar
})
</script>

<style scoped>
.profile-page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.profile-content {
  background: white;
  border-radius: 8px;
  padding: 30px;
  margin: 20px 0;
}

.profile-tabs {
  --el-tabs-header-height: 50px;
}

.tab-content {
  padding: 20px 0;
}

.my-articles {
  margin-bottom: 20px;
}

.article-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.article-info {
  flex: 1;
}

.article-info h3 {
  margin: 0 0 10px 0;
  font-size: 18px;
  color: #333;
  cursor: pointer;
  transition: color 0.3s;
}

.article-info h3:hover {
  color: #409eff;
}

.article-meta {
  display: flex;
  gap: 15px;
  font-size: 14px;
  color: #666;
}

.article-actions {
  display: flex;
  gap: 10px;
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
  .article-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .article-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>