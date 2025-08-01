<template>
  <div class="edit-article">
    <Header />
    
    <div class="container">
      <div class="edit-content">
        <div class="edit-header">
          <h2>编辑文章</h2>
          <div class="header-actions">
            <el-button @click="saveDraft" :loading="saving">保存草稿</el-button>
            <el-button type="primary" @click="updateArticle" :loading="updating">更新文章</el-button>
          </div>
        </div>
        
        <div v-if="loading" class="loading">
          <el-skeleton :rows="10" animated />
        </div>
        
        <div v-else-if="!article" class="empty">
          <el-empty description="文章不存在" />
        </div>
        
        <div v-else>
          <el-form
            ref="articleFormRef"
            :model="articleForm"
            :rules="articleRules"
            class="article-form"
          >
            <el-form-item prop="title">
              <el-input
                v-model="articleForm.title"
                placeholder="请输入文章标题"
                size="large"
                class="title-input"
              />
            </el-form-item>
            
            <el-form-item prop="summary">
              <el-input
                v-model="articleForm.summary"
                type="textarea"
                :rows="3"
                placeholder="请输入文章摘要（可选）"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
            
            <el-form-item prop="categoryId">
              <el-select
                v-model="articleForm.categoryId"
                placeholder="选择分类"
                clearable
                style="width: 200px"
              >
                <el-option
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.name"
                  :value="category.id"
                />
              </el-select>
            </el-form-item>
            
            <el-form-item prop="tagIds">
              <el-select
                v-model="articleForm.tagIds"
                multiple
                placeholder="选择标签"
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="tag in tags"
                  :key="tag.id"
                  :label="tag.name"
                  :value="tag.id"
                />
              </el-select>
            </el-form-item>
            
            <el-form-item prop="content">
              <div class="editor-container">
                <div class="editor-toolbar">
                  <el-button-group>
                    <el-button size="small" @click="insertMarkdown('**', '**')">粗体</el-button>
                    <el-button size="small" @click="insertMarkdown('*', '*')">斜体</el-button>
                    <el-button size="small" @click="insertMarkdown('`', '`')">代码</el-button>
                    <el-button size="small" @click="insertMarkdown('[', '](url)')">链接</el-button>
                    <el-button size="small" @click="insertMarkdown('![', '](url)')">图片</el-button>
                  </el-button-group>
                  <el-button-group>
                    <el-button size="small" @click="insertMarkdown('# ', '')">H1</el-button>
                    <el-button size="small" @click="insertMarkdown('## ', '')">H2</el-button>
                    <el-button size="small" @click="insertMarkdown('### ', '')">H3</el-button>
                  </el-button-group>
                  <el-button-group>
                    <el-button size="small" @click="insertMarkdown('- ', '')">列表</el-button>
                    <el-button size="small" @click="insertMarkdown('1. ', '')">有序列表</el-button>
                    <el-button size="small" @click="insertMarkdown('> ', '')">引用</el-button>
                  </el-button-group>
                </div>
                
                <el-input
                  v-model="articleForm.content"
                  type="textarea"
                  :rows="20"
                  placeholder="请输入文章内容（支持Markdown格式）"
                  class="content-input"
                  @input="updatePreview"
                />
              </div>
            </el-form-item>
          </el-form>
          
          <!-- 预览区域 -->
          <div class="preview-section">
            <h3>预览</h3>
            <div class="preview-content">
              <div v-if="!articleForm.content" class="preview-empty">
                <el-empty description="开始输入内容查看预览" />
              </div>
              <div v-else class="markdown-preview" v-html="previewContent"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { articleApi } from '@/api/article'
import { categoryApi } from '@/api/category'
import { tagApi } from '@/api/tag'
import Header from '@/components/Header.vue'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const saving = ref(false)
const updating = ref(false)
const article = ref(null)
const categories = ref([])
const tags = ref([])
const previewContent = ref('')

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

const articleFormRef = ref()

const articleForm = reactive({
  title: '',
  summary: '',
  content: '',
  categoryId: null,
  tagIds: []
})

const articleRules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 1, max: 100, message: '标题长度在 1 到 100 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'blur' },
    { min: 10, message: '文章内容至少 10 个字符', trigger: 'blur' }
  ]
}

// 获取文章详情
const fetchArticleDetail = async () => {
  loading.value = true
  try {
    const response = await articleApi.getArticleDetail(route.params.id)
    article.value = response.data
    
    // 填充表单
    articleForm.title = article.value.title
    articleForm.summary = article.value.summary || ''
    articleForm.content = article.value.content
    articleForm.categoryId = article.value.categoryId
    articleForm.tagIds = article.value.tags?.map(tag => tag.id) || []
    
    // 更新预览
    updatePreview()
  } catch (error) {
    console.error('获取文章详情失败:', error)
    ElMessage.error('获取文章信息失败')
    router.push('/')
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const fetchCategories = async () => {
  try {
    const response = await categoryApi.getCategories()
    categories.value = response.data
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 获取标签列表
const fetchTags = async () => {
  try {
    const response = await tagApi.getTags()
    tags.value = response.data
  } catch (error) {
    console.error('获取标签列表失败:', error)
  }
}

// 插入Markdown语法
const insertMarkdown = (before, after) => {
  const textarea = document.querySelector('.content-input textarea')
  if (!textarea) return
  
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selectedText = articleForm.content.substring(start, end)
  
  const newText = before + selectedText + after
  articleForm.content = articleForm.content.substring(0, start) + newText + articleForm.content.substring(end)
  
  // 更新预览
  updatePreview()
  
  // 重新设置光标位置
  setTimeout(() => {
    textarea.focus()
    textarea.setSelectionRange(start + before.length, start + before.length + selectedText.length)
  }, 0)
}

// 更新预览
const updatePreview = () => {
  if (articleForm.content) {
    previewContent.value = md.render(articleForm.content)
  } else {
    previewContent.value = ''
  }
}

// 保存草稿
const saveDraft = async () => {
  if (!articleFormRef.value) return
  
  try {
    await articleFormRef.value.validate()
    saving.value = true
    
    const articleData = {
      title: articleForm.title,
      summary: articleForm.summary,
      content: articleForm.content,
      categoryId: articleForm.categoryId,
      tagIds: articleForm.tagIds,
      status: 'DRAFT'
    }
    
    await articleApi.updateArticle(route.params.id, articleData)
    ElMessage.success('草稿保存成功')
  } catch (error) {
    console.error('保存草稿失败:', error)
    ElMessage.error('保存失败，请重试')
  } finally {
    saving.value = false
  }
}

// 更新文章
const updateArticle = async () => {
  if (!articleFormRef.value) return
  
  try {
    await articleFormRef.value.validate()
    updating.value = true
    
    const articleData = {
      title: articleForm.title,
      summary: articleForm.summary,
      content: articleForm.content,
      categoryId: articleForm.categoryId,
      tagIds: articleForm.tagIds,
      status: 'PUBLISHED'
    }
    
    await articleApi.updateArticle(route.params.id, articleData)
    ElMessage.success('文章更新成功')
    router.push(`/article/${route.params.id}`)
  } catch (error) {
    console.error('更新文章失败:', error)
    ElMessage.error('更新失败，请重试')
  } finally {
    updating.value = false
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchArticleDetail()
  fetchCategories()
  fetchTags()
})
</script>

<style scoped>
.edit-article {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.edit-content {
  background: white;
  border-radius: 8px;
  padding: 30px;
  margin: 20px 0;
}

.edit-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.edit-header h2 {
  margin: 0;
  font-size: 24px;
  color: #333;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.article-form {
  margin-bottom: 30px;
}

.title-input {
  font-size: 18px;
  font-weight: 600;
}

.title-input :deep(.el-input__inner) {
  font-size: 18px;
  font-weight: 600;
  border: none;
  border-bottom: 2px solid #f0f0f0;
  border-radius: 0;
  padding: 15px 0;
}

.title-input :deep(.el-input__inner:focus) {
  border-bottom-color: #409eff;
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
  gap: 10px;
  flex-wrap: wrap;
}

.content-input :deep(.el-textarea__inner) {
  border: none;
  border-radius: 0;
  resize: none;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 14px;
  line-height: 1.6;
}

.preview-section {
  margin-top: 30px;
  border-top: 1px solid #f0f0f0;
  padding-top: 30px;
}

.preview-section h3 {
  margin: 0 0 20px 0;
  font-size: 18px;
  color: #333;
}

.preview-content {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  min-height: 300px;
  background: white;
}

.preview-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 300px;
}

.markdown-preview {
  padding: 20px;
  line-height: 1.8;
  color: #333;
}

.markdown-preview :deep(h1),
.markdown-preview :deep(h2),
.markdown-preview :deep(h3),
.markdown-preview :deep(h4),
.markdown-preview :deep(h5),
.markdown-preview :deep(h6) {
  margin: 20px 0 10px 0;
  font-weight: 600;
  color: #2c3e50;
}

.markdown-preview :deep(h1) {
  font-size: 2rem;
  border-bottom: 2px solid #eee;
  padding-bottom: 10px;
}

.markdown-preview :deep(h2) {
  font-size: 1.5rem;
}

.markdown-preview :deep(h3) {
  font-size: 1.25rem;
}

.markdown-preview :deep(p) {
  margin: 15px 0;
}

.markdown-preview :deep(code) {
  background: #f8f8f8;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 0.9em;
  color: #e74c3c;
}

.markdown-preview :deep(pre) {
  background: #f8f8f8;
  padding: 15px;
  border-radius: 5px;
  overflow-x: auto;
  margin: 20px 0;
}

.markdown-preview :deep(pre code) {
  background: none;
  padding: 0;
  color: inherit;
}

.markdown-preview :deep(blockquote) {
  border-left: 4px solid #ddd;
  padding-left: 15px;
  margin: 20px 0;
  color: #666;
  font-style: italic;
}

.markdown-preview :deep(ul),
.markdown-preview :deep(ol) {
  padding-left: 30px;
  margin: 15px 0;
}

.markdown-preview :deep(li) {
  margin: 5px 0;
}

.markdown-preview :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0;
}

.markdown-preview :deep(th),
.markdown-preview :deep(td) {
  border: 1px solid #ddd;
  padding: 8px 12px;
  text-align: left;
}

.markdown-preview :deep(th) {
  background: #f5f5f5;
  font-weight: 600;
}

.loading {
  padding: 40px;
}

.empty {
  text-align: center;
  padding: 40px;
}

@media (max-width: 768px) {
  .edit-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }
  
  .editor-toolbar {
    padding: 8px;
  }
  
  .editor-toolbar .el-button-group {
    margin-bottom: 5px;
  }
}
</style>