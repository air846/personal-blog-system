#!/bin/bash

echo "🚀 个人博客系统启动脚本"
echo "================================"

# 检查后端是否已启动
echo "📋 检查后端服务状态..."
if curl -s http://localhost:8080/test/health > /dev/null; then
echo "✅ 后端服务已运行在 http://localhost:8080"
else
echo "❌ 后端服务未运行"
echo "请先启动后端服务："
echo "cd backend && mvn spring-boot:run"
echo ""
read -p "是否继续启动前端？(y/n): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
exit 1
fi
fi

# 启动前端
echo ""
echo "🌐 启动前端服务..."
cd frontend

# 检查 node_modules 是否存在
if [ ! -d "node_modules" ]; then
echo "📦 安装前端依赖..."
npm install
fi

echo "🚀 启动开发服务器..."
echo "前端地址: http://localhost:3000"
echo "后端地址: http://localhost:8080"
echo "API文档: http://localhost:8080/swagger-ui.html"
echo ""
echo "按 Ctrl+C 停止服务"
echo "================================"

npm run dev