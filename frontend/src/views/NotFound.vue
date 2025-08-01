<template>
  <div class="not-found">
    <div class="not-found-content">
      <div class="error-code">404</div>
      <h1>页面不存在</h1>
      <p>抱歉，您访问的页面不存在或已被删除</p>
      <div class="actions">
        <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
        <el-button @click="$router.go(-1)">返回上页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
// 404页面组件
</script>

<style scoped>
.not-found {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.not-found-content {
  text-align: center;
  color: white;
}

.error-code {
  font-size: 120px;
  font-weight: bold;
  margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.not-found-content h1 {
  font-size: 32px;
  margin: 0 0 15px 0;
  font-weight: 600;
}

.not-found-content p {
  font-size: 18px;
  margin: 0 0 30px 0;
  opacity: 0.9;
}

.actions {
  display: flex;
  gap: 15px;
  justify-content: center;
}

@media (max-width: 480px) {
  .error-code {
    font-size: 80px;
  }
  
  .not-found-content h1 {
    font-size: 24px;
  }
  
  .not-found-content p {
    font-size: 16px;
  }
  
  .actions {
    flex-direction: column;
    align-items: center;
  }
}
</style>