<template>
  <div class="write-article">
    <!-- 页面头部 -->
    <div class="write-header">
      <div class="header-content">
        <h1 class="page-title">
          <i class="el-icon-edit"></i>
          {{ isEdit ? '编辑文章' : '写文章' }}
        </h1>
        <div class="header-actions">
          <el-button @click="saveDraft" :loading="saving" type="info" plain>
            <i class="el-icon-document"></i>
            保存草稿
          </el-button>
          <el-button @click="publishArticle" :loading="publishing" type="primary">
            <i class="el-icon-upload"></i>
            {{ isEdit ? '更新文章' : '发布文章' }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 文章编辑表单 -->
    <div class="write-content">
      <el-form :model="articleForm" :rules="rules" ref="articleFormRef" label-position="top">
        <!-- 文章标题 -->
        <el-form-item label="文章标题" prop="title">
          <el-input
            v-model="articleForm.title"
            placeholder="请输入文章标题"
            size="large"
            maxlength="100"
            show-word-limit
            clearable
          >
            <template #prefix>
              <i class="el-icon-edit-outline"></i>
            </template>
          </el-input>
        </el-form-item>

        <!-- 文章摘要 -->
        <el-form-item label="文章摘要" prop="summary">
          <el-input
            v-model="articleForm.summary"
            type="textarea"
            placeholder="请输入文章摘要（可选）"
            :rows="3"
            maxlength="200"
            show-word-limit
            resize="none"
          />
        </el-form-item>

        <!-- 文章分类和标签 -->
        <div class="form-row">
          <el-form-item label="文章分类" prop="categoryId" class="form-col">
            <el-select
              v-model="articleForm.categoryId"
              placeholder="请选择分类"
              size="large"
              style="width: 100%"
              clearable
            >
              <el-option
                v-for="category in categories"
                :key="category.id"
                :label="category.name"
                :value="category.id"
              />
            </el-select>
          </el-form-item>

          <el-form-item label="文章标签" class="form-col">
            <el-select
              v-model="articleForm.tags"
              multiple
              filterable
              allow-create
              default-first-option
              placeholder="请选择或输入标签"
              size="large"
              style="width: 100%"
            >
              <el-option
                v-for="tag in popularTags"
                :key="tag"
                :label="tag"
                :value="tag"
              />
            </el-select>
          </el-form-item>
        </div>

        <!-- 文章内容编辑器 -->
        <el-form-item label="文章内容" prop="content">
          <div class="editor-container">
            <div class="editor-toolbar">
              <el-button-group>
                <el-button
                  :type="editorMode === 'edit' ? 'primary' : 'default'"
                  @click="editorMode = 'edit'"
                  size="small"
                >
                  <i class="el-icon-edit"></i>
                  编辑
                </el-button>
                <el-button
                  :type="editorMode === 'preview' ? 'primary' : 'default'"
                  @click="editorMode = 'preview'"
                  size="small"
                >
                  <i class="el-icon-view"></i>
                  预览
                </el-button>
                <el-button
                  :type="editorMode === 'split' ? 'primary' : 'default'"
                  @click="editorMode = 'split'"
                  size="small"
                >
                  <i class="el-icon-s-grid"></i>
                  分屏
                </el-button>
              </el-button-group>
            </div>

            <div class="editor-content" :class="`editor-${editorMode}`">
              <!-- 编辑区域 -->
              <div v-show="editorMode === 'edit' || editorMode === 'split'" class="editor-input">
                <el-input
                  v-model="articleForm.content"
                  type="textarea"
                  placeholder="请输入文章内容，支持 Markdown 语法"
                  :rows="20"
                  resize="none"
                  @input="handleContentChange"
                />
              </div>

              <!-- 预览区域 -->
              <div v-show="editorMode === 'preview' || editorMode === 'split'" class="editor-preview">
                <div class="preview-content" v-html="previewContent"></div>
              </div>
            </div>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { marked } from 'marked'
import { articleApi } from '@/api/article'
import { categoryApi } from '@/api/category'
import { tagApi } from '@/api/tag'

const router = useRouter()
const route = useRoute()

// 响应式数据
const articleFormRef = ref(null)
const saving = ref(false)
const publishing = ref(false)
const editorMode = ref('edit') // edit, preview, split
const categories = ref([])
const popularTags = ref(['Vue', 'JavaScript', 'CSS', 'HTML', 'Node.js', 'React', 'TypeScript', '前端', '后端', '全栈'])

// 判断是否为编辑模式
const isEdit = computed(() => !!route.params.id)

// 文章表单数据
const articleForm = reactive({
  title: '',
  summary: '',
  content: '',
  categoryId: null,
  tags: []
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 1, max: 100, message: '标题长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'blur' },
    { min: 10, message: '文章内容至少需要 10 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择文章分类', trigger: 'change' }
  ]
}

// 预览内容
const previewContent = computed(() => {
  if (!articleForm.content) return '<p class="empty-preview">开始输入内容查看预览...</p>'
  return marked(articleForm.content)
})

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await categoryApi.getCategories()
    categories.value = response.data
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 获取文章详情（编辑模式）
const fetchArticleDetail = async () => {
  if (!isEdit.value) return
  
  try {
    const response = await articleApi.getArticleDetail(route.params.id)
    const article = response.data
    
    // 填充表单
    articleForm.title = article.title
    articleForm.summary = article.summary || ''
    articleForm.content = article.content
    articleForm.categoryId = article.categoryId
    articleForm.tags = article.tags?.map(tag => tag.name) || []
  } catch (error) {
    console.error('获取文章详情失败:', error)
    ElMessage.error('获取文章信息失败')
    router.push('/')
  }
}

// 处理内容变化
const handleContentChange = () => {
  // 可以在这里添加自动保存逻辑
}

// 保存草稿
const saveDraft = async () => {
  if (!articleFormRef.value) return
  
  try {
    await articleFormRef.value.validate()
    saving.value = true
    
    const articleData = {
      ...articleForm,
      status: 'DRAFT'
    }
    
    if (isEdit.value) {
      await articleApi.updateArticle(route.params.id, articleData)
      ElMessage.success('草稿保存成功')
    } else {
      const response = await articleApi.createArticle(articleData)
      ElMessage.success('草稿保存成功')
      // 跳转到编辑页面
      router.replace(`/edit/${response.data.id}`)
    }
  } catch (error) {
    console.error('保存草稿失败:', error)
    ElMessage.error('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

// 发布文章
const publishArticle = async () => {
  if (!articleFormRef.value) return
  
  try {
    await articleFormRef.value.validate()
    publishing.value = true
    
    const articleData = {
      ...articleForm,
      status: 'PUBLISHED'
    }
    
    let articleId
    if (isEdit.value) {
      await articleApi.updateArticle(route.params.id, articleData)
      articleId = route.params.id
      ElMessage.success('文章更新成功')
    } else {
      const response = await articleApi.createArticle(articleData)
      articleId = response.data.id
      ElMessage.success('文章发布成功')
    }
    
    // 跳转到文章详情页
    router.push(`/article/${articleId}`)
  } catch (error) {
    console.error('发布文章失败:', error)
    ElMessage.error('发布失败，请重试')
  } finally {
    publishing.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchCategories()
  fetchArticleDetail()
})
</script>

<style scoped>
.write-article {
  min-height: 100vh;
  background: #f5f7fa;
}

.write-header {
  background: white;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}

.page-title {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.write-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.form-col {
  margin-bottom: 0;
}

.editor-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.editor-toolbar {
  background: #f5f7fa;
  padding: 10px 15px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.editor-content {
  min-height: 500px;
  display: flex;
}

.editor-edit .editor-input {
  width: 100%;
}

.editor-preview .editor-preview {
  width: 100%;
}

.editor-split .editor-input,
.editor-split .editor-preview {
  width: 50%;
}

.editor-input {
  border-right: 1px solid #e4e7ed;
}

.editor-input :deep(.el-textarea__inner) {
  border: none;
  border-radius: 0;
  resize: none;
  min-height: 500px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 14px;
  line-height: 1.6;
}

.editor-preview {
  background: white;
  overflow-y: auto;
}

.preview-content {
  padding: 20px;
  line-height: 1.8;
  color: #333;
}

.preview-content :deep(h1),
.preview-content :deep(h2),
.preview-content :deep(h3),
.preview-content :deep(h4),
.preview-content :deep(h5),
.preview-content :deep(h6) {
  margin: 20px 0 10px 0;
  font-weight: 600;
  color: #2c3e50;
}

.preview-content :deep(h1) {
  font-size: 2rem;
  border-bottom: 2px solid #eee;
  padding-bottom: 10px;
}

.preview-content :deep(h2) {
  font-size: 1.5rem;
}

.preview-content :deep(h3) {
  font-size: 1.25rem;
}

.preview-content :deep(p) {
  margin: 15px 0;
}

.preview-content :deep(code) {
  background: #f8f8f8;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 0.9em;
  color: #e74c3c;
}

.preview-content :deep(pre) {
  background: #f8f8f8;
  padding: 15px;
  border-radius: 5px;
  overflow-x: auto;
  margin: 20px 0;
}

.preview-content :deep(pre code) {
  background: none;
  padding: 0;
  color: inherit;
}

.preview-content :deep(blockquote) {
  border-left: 4px solid #ddd;
  padding-left: 15px;
  margin: 20px 0;
  color: #666;
  font-style: italic;
}

.preview-content :deep(ul),
.preview-content :deep(ol) {
  padding-left: 30px;
  margin: 15px 0;
}

.preview-content :deep(li) {
  margin: 5px 0;
}

.preview-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0;
}

.preview-content :deep(th),
.preview-content :deep(td) {
  border: 1px solid #ddd;
  padding: 8px 12px;
  text-align: left;
}

.preview-content :deep(th) {
  background: #f5f5f5;
  font-weight: 600;
}

.empty-preview {
  color: #999;
  text-align: center;
  padding: 50px 20px;
  font-style: italic;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    height: auto;
    padding: 15px 0;
    gap: 15px;
  }
  
  .form-row {
    grid-template-columns: 1fr;
    gap: 0;
  }
  
  .editor-split .editor-input,
  .editor-split .editor-preview {
    width: 100%;
  }
  
  .editor-split .editor-content {
    flex-direction: column;
  }
  
  .editor-input {
    border-right: none;
    border-bottom: 1px solid #e4e7ed;
  }
}
</style>