# HuHa-AI 项目介绍

## 项目概述

HuHa-AI 是一个多功能的人工智能交互平台，集成了多种AI服务和交互界面，旨在提供智能咨询、PDF文档交互以及课程预约等服务。该项目由三个核心模块组成：智能咨询后端(consultant)、多功能AI服务后端(huha-ai-three)以及前端界面(spring-ai-protal-second)。

### 核心特点

- 🤖 基于大型语言模型的智能对话服务
- 📄 PDF文档智能解析与问答
- 🎓 志愿填报咨询与预约系统
- 📚 课程查询与预约功能
- 🖼️ 支持多模态交互（文本、图片、音频、视频）
- 🌓 深色/浅色主题切换

## 项目结构

项目分为三个主要模块：

### 1. consultant - 志愿填报咨询服务后端

```
consultant/
  ├── src/main/java/com/huha/consultant/
  │   ├── aiservice/        - AI服务接口
  │   ├── config/           - 配置类
  │   ├── controller/       - 控制器
  │   ├── mapper/           - 数据库映射
  │   ├── pojo/             - 实体类
  │   ├── repository/       - 数据访问层
  │   ├── service/          - 业务逻辑层
  │   └── tools/            - AI工具类
  └── src/main/resources/
      ├── application.yml   - 应用配置
      ├── content/          - 内容资源
      └── system.txt        - AI系统提示
```

### 2. huha-ai-three - 多功能AI服务后端

```
huha-ai-three/
  ├── src/main/java/com/huha/ai/
  │   ├── config/           - 配置类
  │   ├── constants/        - 常量定义
  │   ├── controller/       - 控制器
  │   ├── entity/           - 实体类
  │   ├── mapper/           - 数据库映射
  │   ├── model/            - AI模型封装
  │   ├── repository/       - 数据访问层
  │   ├── service/          - 业务逻辑层
  │   ├── tools/            - AI工具类
  │   └── utils/            - 工具类
  └── src/main/resources/
      └── application.yaml  - 应用配置
```

### 3. spring-ai-protal-second - 前端界面

```
spring-ai-protal-second/
  ├── public/               - 静态资源
  ├── src/
  │   ├── assets/           - 静态资源
  │   ├── components/       - Vue组件
  │   ├── router/           - 路由配置
  │   ├── services/         - API服务
  │   ├── stores/           - 状态管理
  │   ├── utils/            - 工具函数
  │   └── views/            - 页面视图
  └── package.json          - 依赖配置
```

## 技术栈

### 后端技术

- **Spring Boot**: 应用框架
- **LangChain4j**: AI服务框架
- **Spring AI**: Spring生态的AI集成
- **MyBatis/MyBatis-Plus**: ORM框架
- **Redis**: 缓存和向量存储
- **MySQL**: 关系型数据库

### 前端技术

- **Vue 3**: 前端框架
- **TypeScript**: 类型系统
- **Vite**: 构建工具
- **Vue Router**: 路由管理
- **Pinia**: 状态管理
- **Heroicons**: 图标库
- **PDFTron WebViewer**: PDF查看组件

### AI模型集成

- **阿里云通义千问**(qwen-plus/qwen-max-latest): 大型语言模型
- **文本嵌入模型**(text-embedding-v3): 向量编码模型
- **Ollama**: 本地部署模型支持(deepseek-r1)

## 功能特性

### 智能咨询服务 (consultant)

- **志愿填报咨询**：提供院校信息、录取规则、专业推荐等咨询服务
- **预约管理**：支持用户预约一对一志愿填报指导服务
- **基于向量数据库的知识检索**：集成PDF文档知识库，支持精准回答院校相关问题
- **流式响应**：支持AI回复的流式输出，提升用户体验

### 多功能AI服务 (huha-ai-three)

- **多模态交互**：支持文本、图片、音频、视频等多种媒体格式的AI交互
- **课程服务**：提供课程查询和预约功能
- **学校信息查询**：查询各学校校区信息
- **会话记忆**：基于Redis的会话记忆功能，支持多轮对话

### 前端界面 (spring-ai-protal-second)

- **智能聊天界面**：支持与AI进行文本和多模态交互
- **ChatPDF功能**：上传PDF文件并与内容进行智能交互
- **聊天历史管理**：保存和加载历史对话
- **深色/浅色主题**：支持主题切换，提升用户体验
- **响应式设计**：适配不同设备屏幕大小

## 安装与部署

### 环境要求

- JDK 17+
- Maven 3.8+
- Node.js 16+
- MySQL 8.0+
- Redis 6.0+

### 后端部署 (consultant & huha-ai-three)

1. 克隆代码仓库：
```bash
git clone https://github.com/your-username/HuHa-AI.git
cd HuHa-AI
```

2. 配置数据库：
```sql
-- 创建数据库
CREATE DATABASE volunteer DEFAULT CHARACTER SET utf8mb4;
CREATE DATABASE huha DEFAULT CHARACTER SET utf8mb4;
```

3. 修改配置文件：
   - 修改 `consultant/src/main/resources/application.yml` 中的数据库和Redis配置
   - 修改 `huha-ai-three/src/main/resources/application.yaml` 中的数据库和Redis配置
   - 配置阿里云API密钥（替换 `${API-KEY}` 为实际的API密钥）

4. 编译构建：
```bash
# 构建consultant项目
cd consultant
mvn clean package -DskipTests

# 构建huha-ai-three项目
cd ../huha-ai-three
mvn clean package -DskipTests
```

5. 运行应用：
```bash
# 运行consultant项目
java -jar consultant/target/consultant-0.0.1-SNAPSHOT.jar

# 运行huha-ai-three项目
java -jar huha-ai-three/target/huha-ai-0.0.1-SNAPSHOT.jar
```

### 前端部署 (spring-ai-protal-second)

1. 安装依赖：
```bash
cd spring-ai-protal-second
npm install
```

2. 配置API地址：
   - 创建或修改 `.env.local` 文件，配置后端API地址：
   ```
   VITE_API_BASE_URL=http://localhost:8081
   ```

3. 开发模式运行：
```bash
npm run dev
```

4. 构建生产版本：
```bash
npm run build
```

5. 部署构建结果：
   - 将 `dist` 目录下的文件部署到Web服务器

## 使用指南

### 志愿填报咨询

1. 访问平台主页
2. 选择"志愿填报"服务
3. 输入问题，如查询院校信息、专业推荐等
4. 如需一对一指导，可通过AI预约填报服务

### PDF文档交互

1. 访问"ChatPDF"页面
2. 上传PDF文档（支持拖拽上传）
3. 等待文档处理完成
4. 在聊天框中提问PDF内容相关问题

### 课程查询与预约

1. 访问"课程服务"页面
2. 浏览可用课程或使用搜索功能
3. 选择感兴趣的课程，点击预约
4. 填写个人信息完成预约

## 系统架构图

```
┌─────────────────┐     ┌────────────────────┐     ┌───────────────────┐
│                 │     │                    │     │                   │
│   前端应用      │<───>│  API网关/负载均衡  │<───>│  微服务后端集群   │
│                 │     │                    │     │                   │
└─────────────────┘     └────────────────────┘     └───────────────────┘
                                                            │
                                                            ▼
                               ┌────────────────────────────┬────────────────────────────┐
                               │                            │                            │
                               ▼                            ▼                            ▼
                        ┌─────────────┐             ┌──────────────┐             ┌──────────────┐
                        │             │             │              │             │              │
                        │  MySQL数据库 │             │  Redis缓存   │             │  AI模型服务  │
                        │             │             │              │             │              │
                        └─────────────┘             └──────────────┘             └──────────────┘
```

## 开发计划

- [ ] 增加用户认证与授权系统
- [ ] 支持更多文档格式（Word、Excel等）
- [ ] 添加数据分析与报表功能
- [ ] 优化AI模型响应速度
- [ ] 增加更多专业领域知识库
- [ ] 开发移动端应用

## 贡献指南

欢迎贡献代码、报告问题或提出新功能建议！

1. Fork 项目仓库
2. 创建特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 提交Pull Request

## 许可证

本项目采用 MIT 许可证 - 详情请参阅 [LICENSE](LICENSE) 文件

## 联系方式

- 项目维护者: [您的名字]
- 电子邮箱: [您的邮箱]
- 项目仓库: [GitHub仓库链接]

---

© 2024 HuHa-AI 项目团队
