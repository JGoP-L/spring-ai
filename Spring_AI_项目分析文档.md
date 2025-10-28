# Spring AI 项目分析文档

> 文档生成时间：2025-10-17  
> 项目版本：1.1.0-SNAPSHOT  
> 分析人员：AI Assistant

---

## 📋 目录

1. [项目概述](#项目概述)
2. [核心特性](#核心特性)
3. [项目架构](#项目架构)
4. [完整模块详解 (105个模块)](#spring-ai-完整模块详解) ⭐ 新增
   - [一、核心基础模块 (10个)](#一核心基础模块-10个)
   - [二、AI模型实现模块 (19个)](#二ai模型实现模块-19个)
   - [三、向量数据库模块 (21个)](#三向量数据库模块-21个)
   - [四、文档读取器模块 (4个)](#四文档读取器模块-4个)
   - [五、对话记忆模块 (4个)](#五对话记忆模块-4个)
   - [六、MCP模块 (2个)](#六mcp模块-2个)
   - [七、自动配置模块 (40+个)](#七自动配置模块-40个)
   - [八、Starter模块 (49个)](#八starter模块-49个)
   - [九、辅助工具模块 (4个)](#九辅助工具模块-4个)
   - [十、特色功能模块](#十特色功能模块)
   - [模块统计总览](#模块统计总览)
   - [模块选择指南](#模块选择指南)
5. [AI模型提供商支持](#ai模型提供商支持)
6. [向量数据库支持](#向量数据库支持)
7. [技术栈](#技术栈)
8. [构建与测试](#构建与测试)
9. [应用场景](#应用场景)
10. [总结](#总结)

---

## 🎯 项目概述

### 基本信息

- **项目名称**: Spring AI
- **仓库地址**: https://github.com/spring-projects/spring-ai
- **组织**: Spring Projects
- **许可证**: Apache License 2.0
- **当前版本**: 1.1.0-SNAPSHOT
- **开发语言**: Java 17+
- **构建工具**: Maven

### 项目定位

**Spring AI** 是一个为开发 AI 应用程序提供 Spring 友好 API 和抽象的框架。它的目标是将 Spring 生态系统的设计原则（如可移植性和模块化设计）应用到 AI 领域，并推广使用 POJO 作为 AI 应用程序的构建块。

> **核心使命**: 连接企业的 **数据** 和 **API** 与 **AI 模型**

### 设计理念

- **可移植性**: 统一的API支持多个AI提供商，避免供应商锁定
- **模块化**: 松耦合的模块设计，按需使用
- **Spring友好**: 深度集成Spring Boot、Spring Cloud等
- **类型安全**: 强类型的Java API，编译时检查
- **可观测性**: 内置监控和追踪支持

---

## ⭐ 核心特性

### 1. 多模型支持

#### 聊天模型 (Chat Completion)
- 支持对话式AI
- 流式响应支持
- 上下文管理
- 对话记忆功能

#### 嵌入模型 (Embedding)
- 文本向量化
- 语义搜索
- 相似度计算
- 支持多种维度

#### 图像生成 (Text to Image)
- DALL-E、Stable Diffusion等
- 支持图像生成参数配置
- 批量生成支持

#### 语音转文字 (Audio Transcription)
- Whisper等模型
- 多语言支持
- 时间戳提取

#### 文字转语音 (Text to Speech)
- 语音合成
- 多种语音选择
- 流式音频输出

#### 内容审核 (Moderation)
- 检测不当内容
- 分类评分
- 自动过滤

### 2. 结构化输出 (Structured Outputs)

自动将AI模型的JSON输出映射到Java POJO：

```java
// 定义返回类型
record WeatherInfo(String city, int temperature, String condition) {}

// AI自动返回结构化对象
WeatherInfo weather = chatClient.prompt()
    .user("北京今天天气如何？")
    .call()
    .entity(WeatherInfo.class);
```

### 3. 工具调用/函数调用 (Tools/Function Calling)

允许AI模型调用客户端的Java方法：

```java
@Tool("获取实时天气")
public String getWeather(String city) {
    return weatherAPI.query(city);
}
```

### 4. 向量数据库集成

支持20+个向量数据库，包括：
- PostgreSQL/PGVector
- Chroma
- Pinecone
- Qdrant
- Redis
- MongoDB Atlas
- 等等...

提供统一的API和SQL-like的元数据过滤器。

### 5. RAG支持 (Retrieval Augmented Generation)

- 文档加载和分割
- 向量化存储
- 相似度检索
- 上下文注入

### 6. 可观测性 (Observability)

基于Micrometer的监控：
- 请求追踪
- 性能指标
- Token使用统计
- 错误监控

### 7. ChatClient API

流式API设计，类似于WebClient和RestClient：

```java
ChatClient chatClient = ChatClient.builder(chatModel)
    .defaultSystem("你是一个友好的AI助手")
    .build();

String response = chatClient.prompt()
    .user("你好")
    .call()
    .content();
```

### 8. Advisors API

封装可重用的AI模式：
- Prompt增强
- 响应过滤
- 上下文管理
- 中间件模式

### 9. Spring Boot集成

- 自动配置
- Starters支持
- Properties配置
- 依赖注入

---

## 🏗️ 项目架构

### 模块组织结构（完整版）

```
spring-ai/ (根目录)
│
├─📦 核心层 (Core Layer)
│  ├── spring-ai-bom/                    # BOM依赖管理
│  ├── spring-ai-commons/                # 通用工具类、JSON处理、媒体类型
│  ├── spring-ai-model/                  # ⭐核心模型接口层
│  │   ├── chat/                         # 聊天模型接口、消息类型
│  │   ├── embedding/                    # 嵌入模型接口
│  │   ├── image/                        # 图像模型接口
│  │   ├── audio/                        # 音频模型接口
│  │   ├── converter/                    # 输出转换器
│  │   ├── tool/                         # 工具调用框架
│  │   └── moderation/                   # 内容审核接口
│  ├── spring-ai-client-chat/            # ChatClient流式API
│  ├── spring-ai-vector-store/           # 向量存储抽象层
│  ├── spring-ai-rag/                    # RAG框架
│  │   ├── preretrieval/                 # 查询预处理
│  │   ├── retrieval/                    # 文档检索
│  │   ├── postretrieval/                # 检索后处理
│  │   ├── generation/                   # 生成增强
│  │   └── advisor/                      # RAG Advisor
│  ├── spring-ai-template-st/            # StringTemplate模板引擎
│  ├── spring-ai-retry/                  # 重试机制
│  └── spring-ai-test/                   # 测试工具库
│
├─🤖 AI模型实现层 (Model Implementation Layer)
│  └── models/                           # 19个AI提供商实现
│      ├── spring-ai-openai/             # OpenAI (GPT-4, DALL-E, Whisper)
│      ├── spring-ai-anthropic/          # Anthropic (Claude)
│      ├── spring-ai-azure-openai/       # Azure OpenAI
│      ├── spring-ai-ollama/             # Ollama本地模型
│      ├── spring-ai-google-genai/       # Google Gemini
│      ├── spring-ai-google-genai-embedding/ # Google Embedding
│      ├── spring-ai-vertex-ai-gemini/   # Vertex AI Gemini
│      ├── spring-ai-vertex-ai-embedding/ # Vertex AI Embedding
│      ├── spring-ai-bedrock/            # AWS Bedrock
│      ├── spring-ai-bedrock-converse/   # Bedrock Converse API
│      ├── spring-ai-mistral-ai/         # Mistral AI
│      ├── spring-ai-zhipuai/            # 智谱AI 🇨🇳
│      ├── spring-ai-deepseek/           # DeepSeek 🇨🇳
│      ├── spring-ai-minimax/            # MiniMax 🇨🇳
│      ├── spring-ai-huggingface/        # Hugging Face
│      ├── spring-ai-oci-genai/          # Oracle Cloud
│      ├── spring-ai-elevenlabs/         # ElevenLabs TTS
│      ├── spring-ai-stability-ai/       # Stable Diffusion
│      ├── spring-ai-transformers/       # ONNX本地模型
│      └── spring-ai-postgresml/         # PostgresML
│
├─🗄️ 向量存储实现层 (Vector Store Implementation Layer)
│  └── vector-stores/                    # 21个向量数据库实现
│      ├── spring-ai-pgvector-store/     # PostgreSQL + pgvector ⭐
│      ├── spring-ai-chroma-store/       # Chroma
│      ├── spring-ai-pinecone-store/     # Pinecone
│      ├── spring-ai-qdrant-store/       # Qdrant
│      ├── spring-ai-redis-store/        # Redis
│      ├── spring-ai-milvus-store/       # Milvus
│      ├── spring-ai-weaviate-store/     # Weaviate
│      ├── spring-ai-elasticsearch-store/ # Elasticsearch
│      ├── spring-ai-mongodb-atlas-store/ # MongoDB Atlas
│      ├── spring-ai-neo4j-store/        # Neo4j
│      ├── spring-ai-cassandra-store/    # Cassandra
│      ├── spring-ai-oracle-store/       # Oracle 23c
│      ├── spring-ai-azure-store/        # Azure AI Search
│      ├── spring-ai-azure-cosmos-db-store/ # Azure Cosmos DB
│      ├── spring-ai-opensearch-store/   # OpenSearch
│      ├── spring-ai-mariadb-store/      # MariaDB
│      ├── spring-ai-couchbase-store/    # Couchbase
│      ├── spring-ai-gemfire-store/      # VMware GemFire
│      ├── spring-ai-hanadb-store/       # SAP HANA
│      ├── spring-ai-typesense-store/    # Typesense
│      └── spring-ai-coherence-store/    # Oracle Coherence
│
├─📄 数据处理层 (Data Processing Layer)
│  ├── document-readers/                 # 文档读取器
│  │   ├── pdf-reader/                   # PDF解析 (PDFBox)
│  │   ├── markdown-reader/              # Markdown解析
│  │   ├── jsoup-reader/                 # HTML解析 (jsoup)
│  │   └── tika-reader/                  # 通用文档解析 (Tika)
│  └── advisors/                         # Advisor模式
│      └── spring-ai-advisors-vector-store/ # 向量存储Advisor
│
├─💬 状态管理层 (State Management Layer)
│  └── memory/repository/                # 对话记忆存储
│      ├── spring-ai-model-chat-memory-repository-cassandra/
│      ├── spring-ai-model-chat-memory-repository-jdbc/
│      ├── spring-ai-model-chat-memory-repository-neo4j/
│      └── spring-ai-model-chat-memory-repository-cosmos-db/
│
├─🔗 协议层 (Protocol Layer)
│  └── mcp/                              # Model Context Protocol
│      ├── common/                       # MCP核心实现
│      └── mcp-annotations-spring/       # Spring注解支持
│
├─⚙️ 自动配置层 (Auto-Configuration Layer)
│  └── auto-configurations/              # 40+个自动配置模块
│      ├── common/
│      │   └── spring-ai-autoconfigure-retry/
│      ├── models/                       # AI模型自动配置
│      │   ├── tool/                     # 工具调用配置
│      │   ├── chat/
│      │   │   ├── client/               # ChatClient配置
│      │   │   ├── memory/               # 对话记忆配置
│      │   │   └── observation/          # 观测性配置
│      │   ├── embedding/observation/    # 嵌入观测配置
│      │   ├── image/observation/        # 图像观测配置
│      │   ├── spring-ai-autoconfigure-model-openai/
│      │   ├── spring-ai-autoconfigure-model-anthropic/
│      │   ├── spring-ai-autoconfigure-model-ollama/
│      │   └── ... (17个模型配置)
│      ├── mcp/                          # MCP自动配置
│      │   ├── client/ (common, httpclient, webflux)
│      │   └── server/ (common, webmvc, webflux)
│      └── vector-stores/                # 向量存储自动配置 (21个)
│
├─🚀 启动器层 (Starter Layer)
│  └── spring-ai-spring-boot-starters/   # 49个Starter模块
│      ├── 模型Starter (17个)
│      │   ├── spring-ai-starter-model-openai
│      │   ├── spring-ai-starter-model-anthropic
│      │   └── ...
│      ├── 向量存储Starter (21个)
│      │   ├── spring-ai-starter-vector-store-pgvector
│      │   ├── spring-ai-starter-vector-store-chroma
│      │   └── ...
│      ├── 对话记忆Starter (4个)
│      └── MCP Starter (5个)
│
├─🛠️ 开发工具层 (Development Tools Layer)
│  ├── spring-ai-spring-boot-testcontainers/ # Testcontainers集成
│  ├── spring-ai-spring-boot-docker-compose/ # Docker Compose集成
│  ├── spring-ai-spring-cloud-bindings/      # Cloud Bindings
│  └── spring-ai-integration-tests/          # 集成测试套件
│
└─📚 文档层 (Documentation Layer)
   └── spring-ai-docs/                   # Antora文档
```

---

### 🔄 完整依赖关系图

#### 层次结构

```
┌─────────────────────────────────────────────────────────────────┐
│                        🚀 应用层                                   │
│                  (用户的Spring Boot应用)                          │
└─────────────────────────────────────────────────────────────────┘
                              ↓ 依赖
┌─────────────────────────────────────────────────────────────────┐
│                    🚀 Starter层 (49个)                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ 模型Starter  │  │向量存储Starter│  │ 记忆Starter  │          │
│  │   (17个)     │  │   (21个)     │  │   (4个)      │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
└─────────────────────────────────────────────────────────────────┘
                              ↓ 包含
┌─────────────────────────────────────────────────────────────────┐
│                   ⚙️ 自动配置层 (40+个)                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ 模型配置     │  │向量存储配置  │  │ 观测性配置   │          │
│  │   (17个)     │  │   (21个)     │  │   (3个)      │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
└─────────────────────────────────────────────────────────────────┘
                              ↓ 配置
┌─────────────────────────────────────────────────────────────────┐
│                     🤖 实现层                                      │
│  ┌──────────────────────┐  ┌──────────────────────┐             │
│  │   AI模型实现层       │  │  向量存储实现层      │             │
│  │   ┌──────────────┐   │  │  ┌──────────────┐   │             │
│  │   │ OpenAI      │   │  │  │  PGVector    │   │             │
│  │   │ Anthropic   │   │  │  │  Chroma      │   │             │
│  │   │ Ollama      │   │  │  │  Pinecone    │   │             │
│  │   │ ...19个模型 │   │  │  │  ...21个存储 │   │             │
│  │   └──────────────┘   │  │  └──────────────┘   │             │
│  └──────────────────────┘  └──────────────────────┘             │
└─────────────────────────────────────────────────────────────────┘
                              ↓ 实现
┌─────────────────────────────────────────────────────────────────┐
│                     📦 核心接口层                                  │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │              spring-ai-model (核心) ⭐                      │  │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐         │  │
│  │  │ChatModel│ │Embedding│ │  Image  │ │  Audio  │         │  │
│  │  │  接口   │ │  Model  │ │  Model  │ │  Model  │         │  │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘         │  │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐                     │  │
│  │  │   Tool  │ │Converter│ │  Message│                     │  │
│  │  │  接口   │ │   接口  │ │   类型  │                     │  │
│  │  └─────────┘ └─────────┘ └─────────┘                     │  │
│  └───────────────────────────────────────────────────────────┘  │
│                                                                   │
│  ┌───────────────────┐  ┌───────────────────┐                   │
│  │ spring-ai-        │  │ spring-ai-client- │                   │
│  │ vector-store      │  │ chat              │                   │
│  │ (向量存储接口)     │  │ (ChatClient API)  │                   │
│  └───────────────────┘  └───────────────────┘                   │
│                                                                   │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │              spring-ai-rag (RAG框架)                       │  │
│  │  ┌─────────────┐ ┌──────────┐ ┌──────────────┐           │  │
│  │  │ Pre-Retrieval│ │Retrieval │ │Post-Retrieval│           │  │
│  │  └─────────────┘ └──────────┘ └──────────────┘           │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
                              ↓ 依赖
┌─────────────────────────────────────────────────────────────────┐
│                    🔧 基础设施层                                   │
│  ┌──────────────────────┐  ┌──────────────────────┐             │
│  │  spring-ai-commons   │  │  spring-ai-retry     │             │
│  │  (工具类、JSON处理)   │  │  (重试机制)          │             │
│  └──────────────────────┘  └──────────────────────┘             │
└─────────────────────────────────────────────────────────────────┘
```

---

### 📊 模块间依赖关系详解

#### 1️⃣ 核心依赖链

```
spring-ai-commons (最底层，无依赖)
    ↓
spring-ai-model (依赖 commons)
    ↓
├─→ models/* (19个AI模型实现，依赖 spring-ai-model)
├─→ spring-ai-vector-store (依赖 spring-ai-model)
│       ↓
│   └─→ vector-stores/* (21个实现，依赖 spring-ai-vector-store)
│
├─→ spring-ai-client-chat (依赖 spring-ai-model)
│
└─→ spring-ai-rag (依赖 spring-ai-model, vector-store, client-chat)
        ↓
    advisors/spring-ai-advisors-vector-store (依赖 rag)
```

#### 2️⃣ AI模型模块依赖

```
spring-ai-model (核心接口)
    ↓
┌──────────────────────┬──────────────────────┬──────────────────────┐
│                      │                      │                      │
models/spring-ai-      models/spring-ai-     models/spring-ai-      ...
openai                 anthropic             ollama                 (19个)
│                      │                      │
├─ ChatModel实现       ├─ ChatModel实现      ├─ ChatModel实现
├─ EmbeddingModel实现  │                      ├─ EmbeddingModel实现
├─ ImageModel实现      │                      │
└─ AudioModel实现      └─ ToolCallback实现   └─ StreamingModel实现
```

#### 3️⃣ 向量存储模块依赖

```
spring-ai-vector-store (统一接口)
    ↓
┌────────────────────┬────────────────────┬────────────────────┐
│                    │                    │                    │
vector-stores/       vector-stores/       vector-stores/       ...
spring-ai-           spring-ai-           spring-ai-           (21个)
pgvector-store       chroma-store         pinecone-store
│                    │                    │
└─ VectorStore实现   └─ VectorStore实现   └─ VectorStore实现
```

#### 4️⃣ RAG框架依赖

```
spring-ai-rag
    ↓
依赖于:
├─ spring-ai-model (核心接口)
├─ spring-ai-vector-store (向量存储)
├─ spring-ai-client-chat (ChatClient)
│
包含组件:
├─ preretrieval/
│   ├─ QueryExpander (查询扩展)
│   └─ QueryTransformer (查询转换)
├─ retrieval/
│   ├─ DocumentRetriever (文档检索)
│   └─ DocumentJoiner (文档合并)
├─ postretrieval/
│   └─ DocumentPostProcessor (后处理)
└─ generation/
    └─ QueryAugmenter (查询增强)
```

#### 5️⃣ 自动配置层依赖

```
auto-configurations/*
    ↓
每个自动配置模块依赖:
├─ 对应的实现模块 (如 spring-ai-openai)
├─ Spring Boot AutoConfiguration
└─ spring-ai-model (核心接口)

示例:
auto-configurations/models/spring-ai-autoconfigure-model-openai
    ↓
├─ 依赖 models/spring-ai-openai
├─ 创建 OpenAiChatModel Bean
├─ 创建 OpenAiEmbeddingModel Bean
└─ 读取配置 spring.ai.openai.*
```

#### 6️⃣ Starter层依赖

```
spring-boot-starters/*
    ↓
每个Starter包含:
├─ 对应的实现模块
├─ 对应的自动配置模块
└─ 必要的传递依赖

示例:
spring-ai-starter-model-openai
    ↓
├─ spring-ai-openai (实现)
├─ spring-ai-autoconfigure-model-openai (配置)
├─ spring-ai-model (核心接口)
└─ spring-ai-commons (工具类)
```

#### 7️⃣ 完整的数据流

```
用户应用 (Application)
    ↓ 注入
ChatClient Bean
    ↓ 使用
OpenAiChatModel Bean (由自动配置创建)
    ↓ 实现
ChatModel接口 (spring-ai-model定义)
    ↓ 调用
OpenAI API (外部服务)
    ↓ 返回
ChatResponse
    ↓ 转换 (可选)
BeanOutputConverter (spring-ai-model提供)
    ↓ 输出
Java POJO对象
```

---

### 🔗 跨层依赖关系

#### 水平依赖（同层模块间）

```
models/spring-ai-openai  ─独立→  不依赖其他AI模型
models/spring-ai-anthropic ─独立→ 不依赖其他AI模型
...
(AI模型实现之间相互独立)

vector-stores/spring-ai-pgvector-store ─独立→ 不依赖其他向量存储
vector-stores/spring-ai-chroma-store   ─独立→ 不依赖其他向量存储
...
(向量存储实现之间相互独立)
```

#### 垂直依赖（跨层）

```
应用层
  ↓
Starter层 (便捷依赖)
  ↓
自动配置层 (自动装配)
  ↓
实现层 (具体功能)
  ↓
接口层 (抽象定义)
  ↓
基础设施层 (工具支持)
```

#### 特殊依赖

```
spring-ai-rag
    ↓
跨越多层依赖:
├─ spring-ai-model (接口层)
├─ spring-ai-vector-store (接口层)
├─ spring-ai-client-chat (接口层)
└─ spring-ai-commons (基础设施层)

advisors/spring-ai-advisors-vector-store
    ↓
├─ spring-ai-rag (RAG框架)
├─ spring-ai-vector-store (向量存储)
└─ spring-ai-client-chat (ChatClient)
```

---

### 📐 模块依赖矩阵

| 模块 | 依赖的核心模块 | 作用 |
|------|----------------|------|
| **spring-ai-commons** | 无 | 基础工具类 |
| **spring-ai-model** | commons | 核心接口定义 |
| **spring-ai-client-chat** | model, commons | ChatClient API |
| **spring-ai-vector-store** | model, commons | 向量存储接口 |
| **spring-ai-rag** | model, vector-store, client-chat | RAG框架 |
| **models/*** | model, commons | AI模型实现 |
| **vector-stores/*** | vector-store, commons | 向量存储实现 |
| **auto-configurations/*** | 对应实现模块 | 自动配置 |
| **starters/*** | 对应配置模块 | 便捷依赖 |

---

### 🎯 设计原则

#### 1. 分层架构
- **接口层**：定义统一的抽象
- **实现层**：具体的技术实现
- **配置层**：Spring Boot集成
- **启动器层**：简化依赖管理

#### 2. 模块独立性
- 同一层的模块**相互独立**
- 可以**按需选择**不同的实现
- 实现了**供应商中立**

#### 3. 依赖倒置
- 高层模块**不依赖**低层模块
- 都依赖于**抽象接口**
- 符合**SOLID原则**

#### 4. 可扩展性
- 新增AI模型：实现`ChatModel`接口
- 新增向量存储：实现`VectorStore`接口
- 新增文档读取器：实现`DocumentReader`接口

---

## 📦 Spring AI 完整模块详解

> 项目共包含 **105个模块**，分为9大类别

---

## 🎯 一、核心基础模块 (10个)

这些是Spring AI框架的核心基础，定义了所有功能的基础接口和抽象。

### 1.1 spring-ai-model ⭐⭐⭐⭐⭐

**作用**: 核心模型接口层，定义所有AI模型的通用接口和抽象类

**主要内容**:

| 包名 | 文件数 | 功能描述 |
|------|--------|----------|
| `model/` | 39 | 核心模型接口(Model, Request, Response, Options) |
| `chat/` | 51 | 聊天模型、消息类型、提示词管理、对话记忆 |
| `embedding/` | 20 | 嵌入模型、向量化、文档嵌入 |
| `image/` | 16 | 图像生成模型、图像提示词 |
| `audio/` | 16 | 音频转录(STT)和文字转语音(TTS) |
| `converter/` | 8 | 输出转换器(JSON→POJO、List、Map) |
| `tool/` | 39 | 工具调用、Function Calling、方法工具 |
| `moderation/` | 13 | 内容审核、分类检测 |
| `util/` | 6 | JSON Schema生成、JSON解析 |
| `aot/` | 6 | GraalVM Native Image运行时提示 |

**关键类**:
```java
// 核心接口
Model.java                    // 所有模型的基础接口
ChatModel.java                // 聊天模型接口
EmbeddingModel.java           // 嵌入模型接口
ImageModel.java               // 图像模型接口
StreamingModel.java           // 流式模型支持

// 消息类型
UserMessage.java              // 用户消息
AssistantMessage.java         // AI助手回复
SystemMessage.java            // 系统提示消息
ToolResponseMessage.java      // 工具调用结果

// 输出转换
BeanOutputConverter.java      // JSON→Java对象
ListOutputConverter.java      // JSON数组→List
MapOutputConverter.java       // JSON→Map

// 工具调用
ToolCallback.java             // 工具回调接口
FunctionToolCallback.java     // 函数工具
MethodToolCallback.java       // 方法工具
```

**测试覆盖**: 698个单元测试

---

### 1.2 spring-ai-commons

**作用**: 通用工具类库，提供基础组件和工具方法

**功能**:
- JSON处理工具 (JsonUtils)
- 媒体类型处理 (Media, MimeType)
- 资源加载器 (ResourceLoader)
- 文档转换器 (DocumentTransformer)
- 文本分割器 (TextSplitter, TokenTextSplitter)
- 通用异常类

**使用场景**: 被所有其他模块依赖

---

### 1.3 spring-ai-client-chat

**作用**: 提供流式的ChatClient API，简化AI对话交互

**核心功能**:
- Fluent API设计 (类似WebClient)
- 支持系统提示、用户消息
- 支持工具调用
- 支持流式响应 (Flux)
- 支持结构化输出 (entity())
- 支持Advisor模式

**示例**:
```java
ChatClient client = ChatClient.builder(chatModel)
    .defaultSystem("你是AI助手")
    .build();

// 简单调用
String text = client.prompt().user("你好").call().content();

// 结构化输出
Person person = client.prompt()
    .user("返回一个人的信息")
    .call()
    .entity(Person.class);

// 流式响应
Flux<String> stream = client.prompt()
    .user("讲故事")
    .stream()
    .content();
```

---

### 1.4 spring-ai-vector-store

**作用**: 向量数据库的统一抽象层，提供统一的API

**核心接口**:
```java
public interface VectorStore {
    void add(List<Document> documents);              // 添加文档
    List<Document> similaritySearch(String query);   // 相似度搜索
    List<Document> similaritySearch(SearchRequest request); // 高级搜索
    void delete(List<String> idList);                // 删除文档
}
```

**特性**:
- 统一的向量存储接口
- SQL-like的元数据过滤器 (FilterExpressionBuilder)
- 支持相似度阈值、TopK等参数
- 支持ANTLR4解析过滤表达式

**过滤器示例**:
```java
FilterExpressionBuilder builder = new FilterExpressionBuilder();
Expression filter = builder.eq("country", "CN")
    .and(builder.gte("year", 2020))
    .build();
```

---

### 1.5 spring-ai-rag

**作用**: Retrieval Augmented Generation (RAG) 框架，实现模块化RAG架构

**核心组件**:

| 组件 | 说明 |
|------|------|
| **Query** | 查询表示（文本、历史、上下文） |
| **QueryExpander** | 查询扩展（多查询生成） |
| **QueryTransformer** | 查询转换（压缩、重写、翻译） |
| **DocumentRetriever** | 文档检索器 |
| **DocumentJoiner** | 文档合并器 |
| **QueryAugmenter** | 查询增强器 |
| **RetrievalAugmentationAdvisor** | RAG流程编排 |

**RAG流程**:
```
1. Pre-Retrieval (查询预处理)
   └─ QueryExpander: 扩展查询
   └─ QueryTransformer: 转换查询

2. Retrieval (检索)
   └─ DocumentRetriever: 从向量DB检索

3. Post-Retrieval (后处理)
   └─ DocumentJoiner: 合并文档
   └─ DocumentPostProcessor: 处理文档

4. Generation (生成)
   └─ QueryAugmenter: 增强查询
   └─ ChatModel: 生成答案
```

---

### 1.6 spring-ai-template-st

**作用**: StringTemplate模板引擎支持，用于动态生成提示词

**功能**:
- 提供 StTemplateRenderer
- 支持变量替换
- 支持条件渲染
- 用于Prompt模板

---

### 1.7 spring-ai-test

**作用**: 测试工具库，提供测试基础类和工具

**功能**:
- BaseVectorStoreTests: 向量存储测试基类
- BasicEvaluationTest: AI评估测试
- ObservationTestUtil: 观测性测试工具
- AudioPlayer: 音频播放工具
- SpringAiTestAutoConfigurations: 测试自动配置

**使用场景**: 为开发者编写测试提供工具

---

### 1.8 spring-ai-retry

**作用**: 重试机制支持，处理AI服务的瞬时故障

**功能**:
- RetryTemplate集成
- 指数退避策略
- 重试次数配置
- 异常分类处理

---

### 1.9 spring-ai-bom

**作用**: Bill of Materials，统一管理所有Spring AI模块的版本

**功能**:
- 版本统一管理
- 依赖版本冲突解决
- 简化依赖声明

**使用**:
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>1.1.0-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

---

### 1.10 spring-ai-docs

**作用**: 项目文档，使用Antora生成

**内容**:
- API文档
- 使用指南
- 示例代码
- 最佳实践

---

## 🤖 二、AI模型实现模块 (19个)

每个模块实现特定AI提供商的接口适配。

### 2.1 models/spring-ai-openai ⭐⭐⭐⭐⭐

**支持的模型**: GPT-4, GPT-3.5, DALL-E, Whisper, TTS
**功能**: Chat, Embedding, Image, Audio Transcription, TTS, Moderation
**特点**: 最完整的实现，支持所有OpenAI功能

---

### 2.2 models/spring-ai-anthropic (Claude)

**支持的模型**: Claude 3 Opus, Sonnet, Haiku
**功能**: Chat, Tool Calling
**特点**: 长上下文支持（200K tokens）

---

### 2.3 models/spring-ai-azure-openai

**支持的模型**: Azure托管的OpenAI模型
**功能**: 与OpenAI相同
**特点**: 企业级安全、合规性

---

### 2.4 models/spring-ai-ollama

**支持的模型**: Llama 2/3, Mistral, Codellama等
**功能**: Chat, Embedding（本地运行）
**特点**: 完全本地化、无需API密钥、免费

---

### 2.5 models/spring-ai-google-genai (Gemini)

**支持的模型**: Gemini Pro, Gemini Vision
**功能**: Chat, Vision, Tool Calling
**特点**: 多模态支持

---

### 2.6 models/spring-ai-google-genai-embedding

**支持的模型**: text-embedding-004
**功能**: Text Embedding
**特点**: Google的嵌入模型

---

### 2.7 models/spring-ai-vertex-ai-gemini

**支持的模型**: Google Cloud上的Gemini
**功能**: Chat, Vision
**特点**: 企业级Google Cloud集成

---

### 2.8 models/spring-ai-vertex-ai-embedding

**支持的模型**: Google Cloud Embedding
**功能**: Text/Image Embedding
**特点**: 多模态嵌入

---

### 2.9 models/spring-ai-bedrock

**支持的模型**: AWS Bedrock (Titan, Claude, Jurassic)
**功能**: Chat, Embedding, Image
**特点**: AWS集成

---

### 2.10 models/spring-ai-bedrock-converse

**支持的模型**: Bedrock Converse API
**功能**: 统一的对话接口
**特点**: 简化的API

---

### 2.11 models/spring-ai-mistral-ai

**支持的模型**: Mistral 7B, Mixtral 8x7B
**功能**: Chat, Embedding, Moderation
**特点**: 开源模型

---

### 2.12 models/spring-ai-zhipuai (智谱AI) 🇨🇳

**支持的模型**: GLM-4, ChatGLM
**功能**: Chat, Embedding
**特点**: 国内AI平台

---

### 2.13 models/spring-ai-deepseek 🇨🇳

**支持的模型**: DeepSeek
**功能**: Chat
**特点**: 国内开源模型

---

### 2.14 models/spring-ai-minimax 🇨🇳

**支持的模型**: MiniMax
**功能**: Chat
**特点**: 国内AI平台

---

### 2.15 models/spring-ai-huggingface

**支持的模型**: Hugging Face Inference API
**功能**: Chat
**特点**: 访问HuggingFace所有模型

---

### 2.16 models/spring-ai-oci-genai

**支持的模型**: Oracle Cloud AI
**功能**: Chat, Embedding
**特点**: Oracle企业云

---

### 2.17 models/spring-ai-elevenlabs

**支持的模型**: ElevenLabs
**功能**: Text to Speech
**特点**: 高质量语音合成

---

### 2.18 models/spring-ai-stability-ai

**支持的模型**: Stable Diffusion
**功能**: Text to Image
**特点**: 开源图像生成

---

### 2.19 models/spring-ai-transformers

**支持的模型**: ONNX Runtime
**功能**: 本地Embedding (all-MiniLM-L6-v2)
**特点**: 完全本地、离线运行

---

### 2.20 models/spring-ai-postgresml

**支持的模型**: PostgresML
**功能**: Database内AI
**特点**: 数据库内置AI能力

## 🗄️ 三、向量数据库模块 (21个)

每个模块实现特定向量数据库的VectorStore接口。

### 3.1 vector-stores/spring-ai-pgvector-store ⭐⭐⭐⭐⭐
**数据库**: PostgreSQL + pgvector扩展  
**特点**: 开源、稳定、SQL兼容、适合企业  
**支持**: 元数据过滤、相似度搜索、批量操作

### 3.2 vector-stores/spring-ai-chroma-store
**数据库**: Chroma  
**特点**: 易用、轻量级、开源  
**适用**: 开发测试、小型项目

### 3.3 vector-stores/spring-ai-pinecone-store
**数据库**: Pinecone (云服务)  
**特点**: 高性能、托管服务、可扩展  
**适用**: 生产环境、大规模应用

### 3.4 vector-stores/spring-ai-qdrant-store
**数据库**: Qdrant  
**特点**: Rust编写、高性能、开源  
**支持**: 多向量、过滤查询

### 3.5 vector-stores/spring-ai-redis-store
**数据库**: Redis  
**特点**: 内存数据库、极快速度  
**适用**: 缓存场景、实时搜索

### 3.6 vector-stores/spring-ai-milvus-store
**数据库**: Milvus  
**特点**: 云原生、分布式、高性能  
**适用**: 大规模向量搜索

### 3.7 vector-stores/spring-ai-weaviate-store
**数据库**: Weaviate  
**特点**: GraphQL API、模块化  
**支持**: 多模态搜索

### 3.8 vector-stores/spring-ai-elasticsearch-store
**数据库**: Elasticsearch  
**特点**: 全文搜索+向量搜索  
**适用**: 混合搜索场景

### 3.9 vector-stores/spring-ai-mongodb-atlas-store
**数据库**: MongoDB Atlas  
**特点**: 文档数据库+向量搜索  
**适用**: 已使用MongoDB的项目

### 3.10 vector-stores/spring-ai-neo4j-store
**数据库**: Neo4j  
**特点**: 图数据库+向量搜索  
**适用**: 知识图谱场景

### 3.11 vector-stores/spring-ai-cassandra-store
**数据库**: Apache Cassandra  
**特点**: 分布式、高可用  
**适用**: 大规模分布式系统

### 3.12 vector-stores/spring-ai-oracle-store
**数据库**: Oracle 23c  
**特点**: 企业级、AI Vector Search  
**适用**: Oracle用户

### 3.13 vector-stores/spring-ai-azure-store
**数据库**: Azure AI Search  
**特点**: Azure云服务、企业级  
**适用**: Azure生态

### 3.14 vector-stores/spring-ai-azure-cosmos-db-store
**数据库**: Azure Cosmos DB  
**特点**: 多模型数据库  
**适用**: Azure全球分布式应用

### 3.15 vector-stores/spring-ai-opensearch-store
**数据库**: OpenSearch (AWS)  
**特点**: Elasticsearch开源分支  
**适用**: AWS环境

### 3.16 vector-stores/spring-ai-mariadb-store
**数据库**: MariaDB  
**特点**: MySQL兼容  
**适用**: MariaDB用户

### 3.17 vector-stores/spring-ai-couchbase-store
**数据库**: Couchbase  
**特点**: NoSQL数据库  
**适用**: Couchbase用户

### 3.18 vector-stores/spring-ai-gemfire-store
**数据库**: VMware GemFire  
**特点**: 内存数据网格  
**适用**: 企业级缓存

### 3.19 vector-stores/spring-ai-hanadb-store
**数据库**: SAP HANA  
**特点**: 内存数据库、企业级  
**适用**: SAP环境

### 3.20 vector-stores/spring-ai-typesense-store
**数据库**: Typesense  
**特点**: 开源搜索引擎  
**适用**: 全文+向量搜索

### 3.21 vector-stores/spring-ai-coherence-store
**数据库**: Oracle Coherence  
**特点**: 内存数据网格  
**适用**: Oracle企业用户

---

## 📄 四、文档读取器模块 (4个)

用于读取和解析各种格式的文档。

### 4.1 document-readers/pdf-reader
**支持格式**: PDF  
**依赖**: Apache PDFBox  
**功能**: 提取文本、元数据  
**使用**:
```java
PdfDocumentReader reader = new PdfDocumentReader("document.pdf");
List<Document> documents = reader.get();
```

### 4.2 document-readers/markdown-reader
**支持格式**: Markdown (.md)  
**功能**: 解析Markdown、支持代码块  
**特点**: 保留格式信息

### 4.3 document-readers/jsoup-reader
**支持格式**: HTML  
**依赖**: jsoup  
**功能**: HTML解析、DOM操作  
**特点**: 支持CSS选择器

### 4.4 document-readers/tika-reader
**支持格式**: Word, Excel, PowerPoint等  
**依赖**: Apache Tika  
**功能**: 通用文档解析  
**特点**: 支持100+种格式

---

## 💬 五、对话记忆模块 (4个)

用于存储和管理聊天对话历史。

### 5.1 memory/repository/spring-ai-model-chat-memory-repository-cassandra
**存储**: Cassandra  
**特点**: 分布式、高可用  
**适用**: 大规模分布式应用

### 5.2 memory/repository/spring-ai-model-chat-memory-repository-jdbc
**存储**: 任何JDBC数据库 (MySQL, PostgreSQL等)  
**特点**: 通用、易用  
**适用**: 传统关系型数据库

### 5.3 memory/repository/spring-ai-model-chat-memory-repository-neo4j
**存储**: Neo4j  
**特点**: 图数据库、关系查询  
**适用**: 复杂对话关系

### 5.4 memory/repository/spring-ai-model-chat-memory-repository-cosmos-db
**存储**: Azure Cosmos DB  
**特点**: 全球分布式  
**适用**: Azure环境

---

## 🔗 六、MCP模块 (2个)

Model Context Protocol - 模型上下文协议实现。

### 6.1 mcp/common
**作用**: MCP协议的核心实现  
**功能**:
- 工具调用协议
- 上下文管理
- 异步/同步支持

### 6.2 mcp/mcp-annotations-spring
**作用**: Spring注解支持  
**功能**:
- @McpTool注解
- 自动注册工具
- Spring集成

---

## ⚙️ 七、自动配置模块 (40+个)

提供Spring Boot自动配置支持。

### 7.1 模型自动配置 (17个)

每个AI模型都有对应的自动配置模块：

- `spring-ai-autoconfigure-model-openai` - OpenAI自动配置
- `spring-ai-autoconfigure-model-anthropic` - Claude自动配置
- `spring-ai-autoconfigure-model-azure-openai` - Azure OpenAI
- `spring-ai-autoconfigure-model-ollama` - Ollama本地模型
- `spring-ai-autoconfigure-model-google-genai` - Gemini
- `spring-ai-autoconfigure-model-zhipuai` - 智谱AI 🇨🇳
- `spring-ai-autoconfigure-model-deepseek` - DeepSeek 🇨🇳
- `spring-ai-autoconfigure-model-minimax` - MiniMax 🇨🇳
- ...等17个

**功能**: 自动配置ChatModel, EmbeddingModel等Bean

**配置示例**:
```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4
          temperature: 0.7
```

### 7.2 向量存储自动配置 (21个)

每个向量数据库都有自动配置：

- `spring-ai-autoconfigure-vector-store-pgvector`
- `spring-ai-autoconfigure-vector-store-chroma`
- `spring-ai-autoconfigure-vector-store-pinecone`
- ...等21个

### 7.3 其他自动配置 (7个)

- `spring-ai-autoconfigure-retry` - 重试机制
- `spring-ai-autoconfigure-model-chat-client` - ChatClient
- `spring-ai-autoconfigure-model-chat-memory` - 对话记忆
- `spring-ai-autoconfigure-model-chat-observation` - 聊天观测
- `spring-ai-autoconfigure-model-embedding-observation` - 嵌入观测
- `spring-ai-autoconfigure-model-image-observation` - 图像观测
- `spring-ai-autoconfigure-model-tool` - 工具调用

### 7.4 MCP自动配置 (6个)

- `spring-ai-autoconfigure-mcp-client-common` - MCP客户端公共
- `spring-ai-autoconfigure-mcp-client-httpclient` - HTTP客户端
- `spring-ai-autoconfigure-mcp-client-webflux` - WebFlux客户端
- `spring-ai-autoconfigure-mcp-server-common` - MCP服务器公共
- `spring-ai-autoconfigure-mcp-server-webmvc` - WebMVC服务器
- `spring-ai-autoconfigure-mcp-server-webflux` - WebFlux服务器

---

## 🚀 八、Starter模块 (49个)

便捷的依赖管理，一键引入功能。

### 8.1 模型Starter (17个)

```xml
<!-- OpenAI -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>

<!-- Ollama本地模型 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-ollama</artifactId>
</dependency>

<!-- 智谱AI -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-zhipuai</artifactId>
</dependency>
```

**包含的Starter**:
- spring-ai-starter-model-openai
- spring-ai-starter-model-anthropic
- spring-ai-starter-model-azure-openai
- spring-ai-starter-model-ollama
- spring-ai-starter-model-google-genai
- spring-ai-starter-model-zhipuai 🇨🇳
- spring-ai-starter-model-deepseek 🇨🇳
- spring-ai-starter-model-minimax 🇨🇳
- ...等17个

### 8.2 向量存储Starter (21个)

```xml
<!-- PGVector -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-vector-store-pgvector</artifactId>
</dependency>

<!-- Chroma -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-vector-store-chroma</artifactId>
</dependency>
```

**包含的Starter**: 21个向量数据库的Starter

### 8.3 对话记忆Starter (4个)

- spring-ai-starter-model-chat-memory
- spring-ai-starter-model-chat-memory-repository-cassandra
- spring-ai-starter-model-chat-memory-repository-jdbc
- spring-ai-starter-model-chat-memory-repository-neo4j

### 8.4 MCP Starter (5个)

- spring-ai-starter-mcp-client
- spring-ai-starter-mcp-server
- spring-ai-starter-mcp-client-webflux
- spring-ai-starter-mcp-server-webflux
- spring-ai-starter-mcp-server-webmvc

---

## 🛠️ 九、辅助工具模块 (4个)

### 9.1 spring-ai-spring-boot-testcontainers

**作用**: Testcontainers集成  
**功能**: 测试时自动启动Docker容器  
**支持**: 向量数据库、数据库等

**使用**:
```java
@Testcontainers
@SpringBootTest
class MyTest {
    @Container
    static PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("pgvector/pgvector:pg16");
}
```

### 9.2 spring-ai-spring-boot-docker-compose

**作用**: Docker Compose集成  
**功能**: 开发时自动启动服务  
**支持**: 向量数据库、Redis等

**使用**: 在项目根目录添加 `compose.yaml`

### 9.3 spring-ai-spring-cloud-bindings

**作用**: Cloud Native Buildpacks绑定  
**功能**: 云平台服务绑定  
**适用**: Kubernetes、Cloud Foundry

### 9.4 spring-ai-integration-tests

**作用**: 集成测试套件  
**功能**: 端到端测试  
**内容**: RAG测试、工具调用测试、观测性测试

---

## 🎁 十、特色功能模块

### 10.1 advisors/spring-ai-advisors-vector-store

**作用**: 向量存储Advisor  
**功能**:
- QuestionAnswerAdvisor - 问答Advisor
- VectorStoreChatMemoryAdvisor - 向量存储对话记忆

**使用**:
```java
ChatClient client = ChatClient.builder(chatModel)
    .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
    .build();
```

---

## 📊 模块统计总览

| 类别 | 数量 | 说明 |
|------|------|------|
| **核心基础** | 10 | 框架核心、接口定义 |
| **AI模型** | 19 | 各AI提供商实现 |
| **向量数据库** | 21 | 向量存储实现 |
| **文档读取器** | 4 | 文档解析 |
| **对话记忆** | 4 | 历史存储 |
| **MCP** | 2 | 模型上下文协议 |
| **自动配置** | 40+ | Spring Boot集成 |
| **Starter** | 49 | 便捷依赖 |
| **辅助工具** | 4 | 测试、部署工具 |
| **特色功能** | 1 | Advisor等 |
| **总计** | **105+** | - |

---

## 🎯 模块选择指南

### 场景1：OpenAI聊天应用
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
```

### 场景2：本地AI + 文档问答 (RAG)
```xml
<!-- Ollama本地模型 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-ollama</artifactId>
</dependency>

<!-- PGVector向量存储 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-vector-store-pgvector</artifactId>
</dependency>
```

### 场景3：国内AI平台
```xml
<!-- 智谱AI -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-zhipuai</artifactId>
</dependency>
```

### 场景4：企业级完整方案
```xml
<!-- Claude (Anthropic) -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-anthropic</artifactId>
</dependency>

<!-- Pinecone向量数据库 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-vector-store-pinecone</artifactId>
</dependency>

<!-- 对话记忆(JDBC) -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-chat-memory-repository-jdbc</artifactId>
</dependency>
```


---

## 🤖 AI模型提供商支持

### 支持的模型列表

| 提供商 | 模块名 | 支持的功能 |
|--------|--------|------------|
| **OpenAI** | spring-ai-openai | Chat, Embedding, Image, TTS, Transcription |
| **Anthropic** | spring-ai-anthropic | Chat (Claude) |
| **Azure OpenAI** | spring-ai-azure-openai | 同OpenAI |
| **Ollama** | spring-ai-ollama | 本地模型 (Chat, Embedding) |
| **Google GenAI** | spring-ai-google-genai | Gemini (Chat, Vision) |
| **AWS Bedrock** | spring-ai-bedrock | Claude, Titan等 |
| **Mistral AI** | spring-ai-mistral-ai | Chat |
| **DeepSeek** | spring-ai-deepseek | Chat |
| **MiniMax** | spring-ai-minimax | Chat |
| **智谱AI** | spring-ai-zhipuai | Chat (国内) |
| **Hugging Face** | spring-ai-huggingface | Inference API |
| **ElevenLabs** | spring-ai-elevenlabs | TTS |
| **Stability AI** | spring-ai-stability-ai | 图像生成 |
| **Transformers** | spring-ai-transformers | ONNX本地模型 |
| **PostgresML** | spring-ai-postgresml | 数据库内AI |
| **OCI GenAI** | spring-ai-oci-genai | Oracle云 |
| **Vertex AI** | spring-ai-vertex-ai-* | Google云 |

### 使用示例

```java
// OpenAI
@Autowired
private OpenAiChatModel chatModel;

String response = chatModel.call("你好");

// Ollama (本地)
@Autowired
private OllamaChatModel ollamaModel;

String response = ollamaModel.call("解释量子计算");

// Anthropic (Claude)
@Autowired
private AnthropicChatModel claudeModel;

String response = claudeModel.call("写一段代码");
```

---

## 🗄️ 向量数据库支持

### 支持的向量数据库 (20+)

| 数据库 | 模块名 | 特点 |
|--------|--------|------|
| **PGVector** | spring-ai-pgvector-store | PostgreSQL扩展，开源 |
| **Chroma** | spring-ai-chroma-store | 开源，易用 |
| **Pinecone** | spring-ai-pinecone-store | 云服务，高性能 |
| **Qdrant** | spring-ai-qdrant-store | 开源，Rust编写 |
| **Redis** | spring-ai-redis-store | 内存数据库 |
| **Milvus** | spring-ai-milvus-store | 高性能，云原生 |
| **Weaviate** | spring-ai-weaviate-store | GraphQL查询 |
| **Elasticsearch** | spring-ai-elasticsearch-store | 全文搜索+向量 |
| **MongoDB Atlas** | spring-ai-mongodb-atlas-store | MongoDB向量搜索 |
| **Azure Vector Search** | spring-ai-azure-store | Azure云服务 |
| **Neo4j** | spring-ai-neo4j-store | 图数据库+向量 |
| **Oracle** | spring-ai-oracle-store | Oracle 23c |
| **MariaDB** | spring-ai-mariadb-store | MySQL兼容 |
| **Cassandra** | spring-ai-cassandra-store | 分布式数据库 |
| **Cosmos DB** | spring-ai-azure-cosmos-db-store | Azure多模型DB |
| **Couchbase** | spring-ai-couchbase-store | NoSQL数据库 |
| **GemFire** | spring-ai-gemfire-store | 分布式缓存 |
| **HanaDB** | spring-ai-hanadb-store | SAP数据库 |
| **OpenSearch** | spring-ai-opensearch-store | Elasticsearch分支 |
| **Typesense** | spring-ai-typesense-store | 搜索引擎 |
| **Coherence** | spring-ai-coherence-store | Oracle缓存 |

### 统一API

```java
@Autowired
private VectorStore vectorStore;

// 添加文档
Document doc = new Document("Spring AI很强大");
vectorStore.add(List.of(doc));

// 相似度搜索
List<Document> results = vectorStore
    .similaritySearch("Spring框架");

// 带过滤器的搜索
SearchRequest request = SearchRequest.query("AI")
    .withTopK(5)
    .withSimilarityThreshold(0.7)
    .withFilterExpression("country == 'CN'");
List<Document> filtered = vectorStore.similaritySearch(request);
```

---

## 🛠️ 技术栈

### 核心依赖

| 技术 | 版本 | 用途 |
|------|------|------|
| **Java** | 17+ | 编程语言 |
| **Spring Boot** | 3.5.6 | 应用框架 |
| **Spring Framework** | 6.x | 核心框架 |
| **Jackson** | 2.x | JSON处理 |
| **Project Reactor** | 3.x | 响应式编程 |
| **Micrometer** | 1.x | 可观测性 |
| **ANTLR** | 4.13.1 | 过滤器表达式解析 |
| **JsonSchema Generator** | 4.37.0 | JSON Schema生成 |
| **Kotlin** | 1.9.25 | Kotlin支持 |

### 测试框架

- JUnit 5
- Mockito
- AssertJ
- Testcontainers

### 构建工具

- Maven 3.x
- Maven Wrapper (mvnw)

---

## 🔨 构建与测试

### 环境要求

```bash
# Java版本
java -version  # 需要Java 17或更高

# Maven版本
./mvnw -v      # 使用内置的Maven Wrapper
```

### 构建命令

```bash
# 编译项目（跳过测试）
./mvnw clean compile -DskipTests

# 运行单元测试
./mvnw clean test

# 完整构建（包含测试）
./mvnw clean package

# 运行集成测试（需要API密钥）
export OPENAI_API_KEY=your-key
./mvnw clean verify -Pintegration-tests

# 快速集成测试（核心功能）
./mvnw verify -Pci-fast-integration-tests

# 构建文档
./mvnw -pl spring-ai-docs antora

# 格式化代码
./mvnw spring-javaformat:apply

# 启用Checkstyle
./mvnw clean package -Ddisable.checks=false
```

### 测试单个模块

```bash
# 测试spring-ai-model模块
./mvnw test -pl spring-ai-model

# 测试OpenAI模块
./mvnw verify -Pintegration-tests -pl models/spring-ai-openai

# 测试PGVector存储
./mvnw verify -Pintegration-tests -pl vector-stores/spring-ai-pgvector-store
```

### 项目统计

- **总模块数**: 100+ 个模块
- **核心模块测试**: spring-ai-model 698个单元测试
- **代码质量**: 
  - 高测试覆盖率
  - Checkstyle代码规范
  - 持续集成验证

---

## 💡 应用场景

### 1. 智能客服系统

```java
@Service
public class CustomerServiceBot {
    @Autowired
    private ChatClient chatClient;
    
    @Autowired
    private VectorStore knowledgeBase;
    
    public String answer(String question) {
        // RAG: 检索相关知识
        List<Document> docs = knowledgeBase
            .similaritySearch(question);
        
        String context = docs.stream()
            .map(Document::getContent)
            .collect(Collectors.joining("\n"));
        
        // 生成答案
        return chatClient.prompt()
            .system("基于以下知识回答: " + context)
            .user(question)
            .call()
            .content();
    }
}
```

### 2. 文档问答系统

```java
@Service
public class DocumentQA {
    public void indexDocuments(List<File> pdfs) {
        // 加载PDF
        pdfs.forEach(pdf -> {
            PdfDocumentReader reader = new PdfDocumentReader(pdf);
            List<Document> docs = reader.get();
            
            // 分割文档
            TokenTextSplitter splitter = new TokenTextSplitter();
            List<Document> chunks = splitter.split(docs);
            
            // 向量化并存储
            vectorStore.add(chunks);
        });
    }
    
    public String ask(String question) {
        // 检索 + 生成答案
        return ragService.query(question);
    }
}
```

### 3. 智能代码助手

```java
@Tool("执行数据库查询")
public String queryDatabase(String sql) {
    return jdbcTemplate.queryForList(sql).toString();
}

@Tool("查看日志")
public String viewLogs(String level) {
    return logService.getRecentLogs(level);
}

// AI可以自动调用这些工具
String response = chatClient.prompt()
    .user("查询最近的错误日志")
    .call()
    .content();
```

### 4. 内容生成系统

```java
// 生成营销文案
record MarketingCopy(
    String title,
    String body,
    List<String> keyPoints
) {}

MarketingCopy copy = chatClient.prompt()
    .user("为我们的AI产品生成营销文案")
    .call()
    .entity(MarketingCopy.class);
```

### 5. 多模态应用

```java
// 图像生成
ImageResponse imageResponse = imageModel.call(
    new ImagePrompt("未来城市景观"));

// 语音转文字
AudioTranscriptionResponse transcription = 
    transcriptionModel.call(audioFile);

// 文字转语音
Speech speech = ttsModel.call("欢迎使用Spring AI");
```

### 6. 智能搜索

```java
@Service
public class SemanticSearch {
    // 语义搜索而非关键词匹配
    public List<Product> search(String query) {
        // 用户输入转向量
        List<Double> queryVector = embeddingModel
            .embed(query);
        
        // 向量相似度搜索
        List<Document> results = vectorStore
            .similaritySearch(SearchRequest
                .query(query)
                .withTopK(10));
        
        return results.stream()
            .map(this::toProduct)
            .collect(Collectors.toList());
    }
}
```

---

## 📊 项目优势

### 1. **供应商中立**
- 统一API支持多个AI提供商
- 轻松切换模型（OpenAI → Ollama → Claude）
- 避免供应商锁定

### 2. **Spring生态深度集成**
- 依赖注入
- 自动配置
- AOP支持
- 事务管理
- Spring Security集成

### 3. **企业级特性**
- 可观测性（监控、追踪、指标）
- 重试机制
- 错误处理
- 安全性（API密钥管理）
- 测试支持

### 4. **开发体验**
- 类型安全
- 流式API
- 丰富的文档
- 大量示例
- 活跃社区

### 5. **性能优化**
- 响应式编程（Reactor）
- 流式响应
- 连接池管理
- 批量处理
- 缓存支持

### 6. **GraalVM支持**
- Native Image编译
- 快速启动
- 低内存占用
- 适合Serverless

---

## 🚀 快速开始

### 1. 添加依赖

```xml
<dependencies>
    <!-- OpenAI Starter -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-openai</artifactId>
    </dependency>
    
    <!-- PGVector Starter (可选) -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-pgvector</artifactId>
    </dependency>
</dependencies>
```

### 2. 配置

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4
          temperature: 0.7
```

### 3. 编写代码

```java
@RestController
@RequestMapping("/ai")
public class AiController {
    
    @Autowired
    private ChatClient chatClient;
    
    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
            .user(message)
            .call()
            .content();
    }
}
```

### 4. 运行

```bash
./mvnw spring-boot:run
```

---

## 📚 学习资源

### 官方资源
- [官方文档](https://docs.spring.io/spring-ai/reference/)
- [GitHub仓库](https://github.com/spring-projects/spring-ai)
- [示例项目](https://github.com/spring-projects/spring-ai-examples)
- [社区资源](https://github.com/spring-ai-community/awesome-spring-ai)

### 博客文章
- [Why Spring AI](https://spring.io/blog/2024/11/19/why-spring-ai)

### 参考项目
- LangChain (Python)
- LlamaIndex (Python)

---

## 🎯 总结

### Spring AI 是什么？

**Spring AI** 是一个为Java开发者打造的AI应用开发框架，它提供了：

1. ✅ **统一的AI模型接口** - 支持OpenAI、Claude、Gemini、Ollama等20+模型
2. ✅ **多模态支持** - 文本、图像、音频全覆盖
3. ✅ **向量数据库集成** - 支持20+向量数据库
4. ✅ **RAG能力** - 完整的检索增强生成框架
5. ✅ **Function Calling** - AI可以调用Java方法
6. ✅ **结构化输出** - 自动转换JSON为Java对象
7. ✅ **Spring Boot集成** - 自动配置、依赖注入
8. ✅ **企业级特性** - 可观测性、重试、安全性
9. ✅ **GraalVM支持** - 原生镜像编译

### 适用场景

- ✅ 智能客服机器人
- ✅ 文档问答系统
- ✅ 知识库搜索
- ✅ 内容生成
- ✅ 代码助手
- ✅ 数据分析
- ✅ 多模态应用

### 技术特点

- 🎯 **供应商中立** - 避免锁定
- 🎯 **类型安全** - 编译时检查
- 🎯 **Spring友好** - 深度集成
- 🎯 **高性能** - 响应式编程
- 🎯 **易测试** - 丰富的测试支持
- 🎯 **可扩展** - 模块化设计

### 项目成熟度

- ✅ 活跃开发（1.1.0-SNAPSHOT）
- ✅ 完善的CI/CD
- ✅ 高测试覆盖率
- ✅ 详细的文档
- ✅ Spring官方项目
- ✅ 活跃的社区

---

## 📝 分析结论

Spring AI 是一个**企业级、生产就绪**的AI应用开发框架。它不是Python项目的简单移植，而是一个为Java生态量身打造的AI框架。

**核心价值**：
1. 将Spring生态的优势带入AI开发
2. 提供统一、标准化的API
3. 支持多种AI提供商和向量数据库
4. 企业级特性（可观测性、安全性）
5. 优秀的开发体验

**适合谁？**
- Java/Spring开发者
- 企业级AI应用开发
- 需要供应商中立的团队
- 追求类型安全的团队
- 需要集成现有Spring应用

**竞争优势**：
- 对比LangChain：类型安全、Spring集成
- 对比纯SDK：统一API、高级抽象
- 对比其他Java框架：Spring生态、企业级

---

## 📝 文档更新记录

### v2.0 - 2025-10-24
- ✅ **新增**: 完整的105个模块详细说明
- ✅ **新增**: 10大类模块分类（核心基础、AI模型、向量数据库等）
- ✅ **新增**: 每个模块的功能说明、特点、适用场景
- ✅ **新增**: 模块统计总览表格
- ✅ **新增**: 模块选择指南（4个典型场景）
- ✅ **优化**: 更新目录结构
- ✅ **优化**: 删除重复内容

### v1.0 - 2025-10-17
- ✅ 初始版本
- ✅ 项目概述、核心特性
- ✅ 基础模块介绍
- ✅ 技术栈、构建测试

---

**文档版本**: 2.0  
**最后更新**: 2025-10-24  
**分析人员**: AI Assistant  
**项目地址**: https://github.com/spring-projects/spring-ai  
**总模块数**: 105+

---

## 💡 使用建议

### 如何选择合适的模块？

1. **选择AI模型**
   - 需要API服务 → OpenAI / Anthropic / Google Gemini
   - 本地运行 → Ollama / Transformers
   - 国内平台 → 智谱AI / DeepSeek / MiniMax 🇨🇳
   - 企业级 → Azure OpenAI / AWS Bedrock

2. **选择向量数据库**
   - 已有PostgreSQL → PGVector ⭐推荐
   - 开发测试 → Chroma
   - 云服务 → Pinecone / MongoDB Atlas
   - 企业级 → Oracle / SAP HANA

3. **选择文档读取器**
   - PDF → pdf-reader
   - Office文档 → tika-reader
   - HTML → jsoup-reader
   - Markdown → markdown-reader

4. **选择对话记忆存储**
   - 关系型数据库 → JDBC ⭐推荐
   - 分布式 → Cassandra
   - 图数据库 → Neo4j
   - Azure用户 → Cosmos DB

### 快速开始模板

#### 最简单的聊天应用
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
```

#### 完全本地化方案（无需API密钥）
```xml
<!-- Ollama本地模型 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-ollama</artifactId>
</dependency>

<!-- ONNX本地嵌入模型 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-transformers</artifactId>
</dependency>

<!-- PGVector向量存储 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-vector-store-pgvector</artifactId>
</dependency>
```

#### 企业级RAG系统
```xml
<!-- Claude AI -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-anthropic</artifactId>
</dependency>

<!-- Pinecone向量数据库 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-vector-store-pinecone</artifactId>
</dependency>

<!-- JDBC对话记忆 -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-chat-memory-repository-jdbc</artifactId>
</dependency>
```

---

**感谢阅读！如有问题，欢迎访问项目GitHub或官方文档。**

