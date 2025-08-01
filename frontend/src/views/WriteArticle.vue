<template>
  <div class="write-article">
    <div class="container">
      <div class="write-container">
        <el-form
          ref="articleFormRef"
          :model="articleForm"
          :rules="articleRules"
          label-width="80px"
        >
          <el-form-item label="标题" prop="title">
            <el-input
              v-model="articleForm.title"
              placeholder="请输入文章标题"
              maxlength="100"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item label="摘要" prop="summary">
            <el-input
              v-model="articleForm.summary"
              type="textarea"
              :rows="3"
              placeholder="请输入文章摘要"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item label="分类" prop="categoryId">
            <el-select
              v-model="articleForm.categoryId"
              placeholder="请选择分类"
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
          
          <el-form-item label="标签">
            <el-select
              v-model="articleForm.tagIds"
              multiple
              placeholder="请选择标签"
              style="width: 300px"
            >
              <el-option
                v-for="tag in tags"
                :key="tag.id"
                :label="tag.name"
                :value="tag.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="封面">
            <el-input
              v-model="articleForm.cover"
              placeholder="请输入封面图片URL（可选）"
            />
          </el-form-item>
          
          <el-form-item label="内容" prop="content">
            <div class="editor-container">
              <div class="editor-toolbar">
                <el-button-group>
                  <el-button size="small" @click="insertMarkdown('**', '**')">粗体</el-button>
                  <el-button size="small" @click="insertMarkdown('*', '*')">斜体</el-button>
                  <el-button size="small" @click="insertMarkdown('`', '`')">代码</el-button>
                  <el-button size="small" @click="insertMarkdown('\n```\n', '\n```\n')">代码块</el-button>
                  <el-button size="small" @click="insertMarkdown('[', '](url)')">链接</el-button>
                  <el-button size="small" @click="insertMarkdown('![', '](url)')">图片</el-button>
                </el-button-group>
              </div>
              
              <div class="editor-content">
                <div class="editor-input">
                  <el-input
                    ref="contentInputRef"
                    v-model="articleForm.content"
                    type="textarea"
                    :rows="20"
                    placeholder="请输入文章内容（支持Markdown语法）"
                    @input="updatePreview"
                  />
                </div>
                
                <div class="editor-preview">
                  <div class="preview-header">预览</div>
                  <div v-html="previewContent" class="markdown-body"></div>
                </div>
              </div>
            </div>
          </el-form-item>
          
          <el-form-item>
            <div class="form-actions">
              <el-button @click="saveDraft" :loading="draftLoading">保存草稿</el-button>
              <el-button type="primary" @click="publishArticle" :loading="publishLoading">
                发布文章
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { articleApi } from '../api/article'
import { categoryApi } from '../api/category'
import { tagApi } from '../api/tag'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'

const router = useRouter()
const userStore = useUserStore()
const articleFormRef = ref()
const contentInputRef = ref()

const draftLoading = ref(false)
const publishLoading = ref(false)
const categories = ref([])
const tags = ref([])

// 文章表单数据
const articleForm = reactive({
  title: '',
  summary: '',
  content: '',
  cover: '',
  categoryId: null,
  tagIds: []
})

// 表单验证规则
const articleRules = {
  title: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    { min: 5, max: 100, message: '标题长度在 5 到 100 个字符', trigger: 'blur' }
  ],
  summary: [
    { required: true, message: '请输入文章摘要', trigger: 'blur' },
    { min: 10, max: 200, message: '摘要长度在 10 到 200 个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入文章内容', trigger: 'blur' },
    { min: 50, message: '内容至少50个字符', trigger: 'blur' }
  ]
}

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

// 预览内容
const previewContent = computed(() => {
  if (!articleForm.content) return '<p>在左侧输入内容，这里将显示预览效果</p>'
  return md.render(articleForm.content)
})

// 获取分类列表
const getCategories = async () => {
  try {
    const response = await categoryApi.getCategoryList()
    if (response.code === 200) {
      categories.value = response.data
    }
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取标签列表
const getTags = async () => {
  try {
    const response = await tagApi.getTagList()
    if (response.code === 200) {
      tags.value = response.data
    }
  } catch (error) {
    console.error('获取标签失败:', error)
  }
}

// 插入Markdown语法
const insertMarkdown = (before, after) => {
  const textarea = contentInputRef.value?.textarea
  if (!textarea) return
  
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selectedText = articleForm.content.substring(start, end)
  
  const newText = before + selectedText + after
  articleForm.content = articleForm.content.substring(0, start) + newText + articleForm.content.substring(end)
  
  // 设置光标位置
  setTimeout(() => {
    textarea.focus()
    textarea.setSelectionRange(start + before.length, start + before.length + selectedText.length)
  }, 0)
}

// 更新预览（防抖）
let previewTimer = null
const updatePreview = () => {
  clearTimeout(previewTimer)
  previewTimer = setTimeout(() => {
    // 预览内容会自动更新，因为使用了computed
  }, 300)
}

// 保存草稿
const saveDraft = async () => {
  if (!articleFormRef.value) return
  
  try {
    const valid = await articleFormRef.value.validate()
    if (!valid) return
    
    draftLoading.value = true
    const response = await articleApi.createArticle({
      ...articleForm,
      status: 0 // 草稿状态
    })
    
    if (response.code === 200) {
      ElMessage.success('草稿保存成功')
      router.push('/profile')
    } else {
      ElMessage.error(response.message || '保存失败')
    }
  } catch (error) {
    ElMessage.error('保存失败')
  } finally {
    draftLoading.value = false
  }
}

// 发布文章
const publishArticle = async () => {
  if (!articleFormRef.value) return
  
  try {
    const valid = await articleFormRef.value.validate()
    if (!valid) return
    
    publishLoading.value = true
    const response = await articleApi.createArticle({
      ...articleForm,
      status: 1 // 发布状态
    })
    
    if (response.code === 200) {
      ElMessage.success('文章发布成功')
      router.push('/profile')
    } else {
      ElMessage.error(response.message || '发布失败')
    }
  } catch (error) {
    ElMessage.error('发布失败')
  } finally {
    publishLoading.value = false
  }
}

// 页面加载时获取数据
onMounted(() => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  getCategories()
  getTags()
})
</script>

<style scoped>
.write-article {
  padding: 20px 0;
}

.write-container {
  max-width: 1200px;
  margin: 0 auto;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 30px;
}

.editor-container {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.editor-toolbar {
  padding: 10px;
  background: #f5f7fa;
  border-bottom: 1px solid #dcdfe6;
}

.editor-content {
  display: flex;
  height: 500px;
}

.editor-input {
  flex: 1;
  border-right: 1px solid #dcdfe6;
}

.editor-input :deep(.el-textarea) {
  height: 100%;
}

.editor-input :deep(.el-textarea__inner) {
  height: 100%;
  border: none;
  border-radius: 0;
  resize: none;
  font-family: 'Courier New', monospace;
  font-size: 14px;
  line-height: 1.6;
}

.editor-preview {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.preview-header {
  padding: 10px 15px;
  background: #f5f7fa;
  border-bottom: 1px solid #dcdfe6;
  font-size: 14px;
  color: #666;
}

.markdown-body {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
  line-height: 1.8;
  color: #333;
}

.form-actions {
  display: flex;
  gap: 15px;
}

/* Markdown预览样式 */
.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4,
.markdown-body h5,
.markdown-body h6 {
  margin: 20px 0 10px 0;
  font-weight: 600;
  color: #333;
}

.markdown-body h1 {
  font-size: 20px;
}

.markdown-body h2 {
  font-size: 18px;
}

.markdown-body h3 {
  font-size: 16px;
}

.markdown-body p {
  margin: 10px 0;
}

.markdown-body code {
  background: #f6f8fa;
  padding: 2px 4px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
  font-size: 13px;
}

.markdown-body pre {
  background: #f6f8fa;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
  margin: 10px 0;
}

.markdown-body pre code {
  background: none;
  padding: 0;
}

.markdown-body blockquote {
  border-left: 3px solid #dfe2e5;
  padding-left: 10px;
  margin: 10px 0;
  color: #666;
}

.markdown-body ul,
.markdown-body ol {
  padding-left: 20px;
  margin: 10px 0;
}

.markdown-body li {
  margin: 3px 0;
}

.markdown-body img {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  margin: 10px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .write-container {
    padding: 20px 15px;
    margin: 0 15px;
  }
  
  .editor-content {
    flex-direction: column;
    height: auto;
  }
  
  .editor-input {
    border-right: none;
    border-bottom: 1px solid #dcdfe6;
  }
  
  .editor-input :deep(.el-textarea__inner) {
    height: 300px;
  }
  
  .editor-preview {
    height: 300px;
  }
}
</style>