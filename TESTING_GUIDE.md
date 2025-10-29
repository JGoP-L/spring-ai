# 🧪 修改验证测试指南

**适用于**: 日志改进及异步工具调用功能  
**目标**: 全面验证代码修改的正确性和规范性

---

## 📋 快速测试清单

```bash
# 1. 格式验证 (必须)
./mvnw spring-javaformat:validate -pl spring-ai-model

# 2. 运行单元测试 (必须)
./mvnw test -Dtest=DefaultToolCallingManagerTests -pl spring-ai-model

# 3. 运行所有测试 (推荐)
./mvnw test -pl spring-ai-model

# 4. Checkstyle检查 (可选)
./mvnw checkstyle:check -pl spring-ai-model -Ddisable.checks=false

# 5. 构建整个模块 (推荐)
./mvnw clean install -pl spring-ai-model -DskipTests=false
```

**全部通过 = ✅ 验证成功！**

---

## 🎯 分步骤详细验证

### **第一步：格式验证** ⭐⭐⭐⭐⭐

这是**最重要**的验证，Spring AI项目以此为准。

```bash
cd /Users/shaojie/IdeaProjects/spring-ai

# 验证格式
./mvnw spring-javaformat:validate -pl spring-ai-model
```

**期望输出**:
```
[INFO] --- spring-javaformat-maven-plugin:0.0.43:validate (default-cli) @ spring-ai-model ---
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

**如果失败**:
```bash
# 自动修复格式
./mvnw spring-javaformat:apply -pl spring-ai-model

# 重新验证
./mvnw spring-javaformat:validate -pl spring-ai-model
```

---

### **第二步：运行单元测试** ⭐⭐⭐⭐⭐

验证功能正确性。

```bash
# 运行ToolCallingManager的测试
./mvnw test -Dtest=DefaultToolCallingManagerTests -pl spring-ai-model
```

**期望输出**:
```
[INFO] Tests run: 18, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

**查看详细日志**:
```bash
./mvnw test -Dtest=DefaultToolCallingManagerTests -pl spring-ai-model -X
```

**看到的日志应该包含**:
```
DEBUG o.s.a.m.t.DefaultToolCallingManager -- Executing tool call: toolA (synchronous mode)
DEBUG o.s.a.m.t.DefaultToolCallingManager -- Executing tool call: toolB (synchronous mode)
```

---

### **第三步：运行模块所有测试** ⭐⭐⭐⭐

确保没有破坏其他功能。

```bash
# 运行spring-ai-model模块的所有测试
./mvnw test -pl spring-ai-model
```

**期望输出**:
```
[INFO] Tests run: XXX, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

**如果测试很多，可以先跑快速测试**:
```bash
# 只运行单元测试，跳过集成测试
./mvnw test -pl spring-ai-model -Dtest=*Tests
```

---

### **第四步：Checkstyle检查** ⭐⭐ (可选)

检查代码风格细节。

```bash
# 启用checkstyle检查
./mvnw checkstyle:check -pl spring-ai-model -Ddisable.checks=false
```

**注意**:
- 这个检查默认是**禁用**的
- 可能会报告很多现有代码的问题
- 行长度超标是常见问题，非关键

**重点关注你修改的文件**:
```bash
./mvnw checkstyle:check -pl spring-ai-model -Ddisable.checks=false 2>&1 | \
  grep DefaultToolCallingManager
```

---

### **第五步：完整构建** ⭐⭐⭐⭐

验证完整的构建流程。

```bash
# 清理并重新构建
./mvnw clean install -pl spring-ai-model
```

**期望输出**:
```
[INFO] Installing .../spring-ai-model-1.1.0-SNAPSHOT.jar ...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 🔍 验证日志输出

### **创建测试代码**

在 `spring-ai-model/src/test/java/org/springframework/ai/model/tool/` 创建测试文件：

**LoggingVerificationTest.java**:
```java
package org.springframework.ai.model.tool;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.AsyncToolCallback;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class LoggingVerificationTest {

    private static final Logger logger = LoggerFactory.getLogger(LoggingVerificationTest.class);

    @Test
    void testSynchronousLogging() {
        logger.info("=== Testing Synchronous Logging ===");
        
        // 创建测试工具
        ToolCallback syncTool = createSyncTool("syncTool");
        AsyncToolCallback asyncTool = createAsyncTool("asyncTool");
        
        // 创建manager
        DefaultToolCallingManager manager = DefaultToolCallingManager.builder().build();
        
        // 创建包含工具调用的响应
        AssistantMessage assistantMessage = AssistantMessage.builder()
            .content("I'll use the tools")
            .toolCall("call1", "syncTool", "{}")
            .toolCall("call2", "asyncTool", "{}")
            .build();
        
        Generation generation = new Generation(assistantMessage);
        ChatResponse response = new ChatResponse(List.of(generation));
        
        // 创建Prompt
        Prompt prompt = new Prompt("test", 
            ToolCallingChatOptions.builder()
                .toolCallbacks(List.of(syncTool, asyncTool))
                .build());
        
        // 执行同步调用 - 应该看到日志
        logger.info("Executing synchronous tool calls...");
        ToolExecutionResult result = manager.executeToolCalls(prompt, response);
        
        assertNotNull(result);
        assertEquals(2, result.conversationHistory().size());
        
        logger.info("=== Synchronous Test Complete ===");
    }
    
    @Test
    void testAsynchronousLogging() {
        logger.info("=== Testing Asynchronous Logging ===");
        
        // 创建测试工具
        AsyncToolCallback asyncTool = createAsyncTool("asyncTool");
        ToolCallback syncTool = createSyncTool("syncTool");
        
        // 创建manager
        DefaultToolCallingManager manager = DefaultToolCallingManager.builder().build();
        
        // 创建包含工具调用的响应
        AssistantMessage assistantMessage = AssistantMessage.builder()
            .content("I'll use the tools")
            .toolCall("call1", "asyncTool", "{}")
            .toolCall("call2", "syncTool", "{}")
            .build();
        
        Generation generation = new Generation(assistantMessage);
        ChatResponse response = new ChatResponse(List.of(generation));
        
        // 创建Prompt
        Prompt prompt = new Prompt("test",
            ToolCallingChatOptions.builder()
                .toolCallbacks(List.of(asyncTool, syncTool))
                .build());
        
        // 执行异步调用 - 应该看到日志
        logger.info("Executing asynchronous tool calls...");
        ToolExecutionResult result = manager.executeToolCallsAsync(prompt, response).block();
        
        assertNotNull(result);
        assertEquals(2, result.conversationHistory().size());
        
        logger.info("=== Asynchronous Test Complete ===");
    }
    
    private ToolCallback createSyncTool(String name) {
        return new ToolCallback() {
            @Override
            public String getName() {
                return name;
            }
            
            @Override
            public ToolDefinition getToolDefinition() {
                return ToolDefinition.builder().name(name).build();
            }
            
            @Override
            public ToolMetadata getToolMetadata() {
                return ToolMetadata.builder().build();
            }
            
            @Override
            public String call(String functionArguments) {
                logger.debug("{} executed (sync)", name);
                return "sync result";
            }
        };
    }
    
    private AsyncToolCallback createAsyncTool(String name) {
        return new AsyncToolCallback() {
            @Override
            public String getName() {
                return name;
            }
            
            @Override
            public ToolDefinition getToolDefinition() {
                return ToolDefinition.builder().name(name).build();
            }
            
            @Override
            public ToolMetadata getToolMetadata() {
                return ToolMetadata.builder().build();
            }
            
            @Override
            public String call(String functionArguments) {
                logger.debug("{} executed (sync fallback)", name);
                return "sync result";
            }
            
            @Override
            public Mono<String> callAsync(String functionArguments) {
                logger.debug("{} executed (async)", name);
                return Mono.just("async result");
            }
            
            @Override
            public boolean supportsAsync() {
                return true;
            }
        };
    }
}
```

### **运行日志验证测试**

```bash
# 运行测试并查看日志
./mvnw test -Dtest=LoggingVerificationTest -pl spring-ai-model
```

**期望看到的日志**:
```
INFO  LoggingVerificationTest - === Testing Synchronous Logging ===
INFO  LoggingVerificationTest - Executing synchronous tool calls...
DEBUG DefaultToolCallingManager - Executing tool call: syncTool (synchronous mode)
DEBUG DefaultToolCallingManager - Executing tool call: asyncTool (synchronous mode)
DEBUG DefaultToolCallingManager - Tool 'asyncTool' implements AsyncToolCallback but executing in synchronous mode. Consider using executeToolCallsAsync() for better performance.
INFO  LoggingVerificationTest - === Synchronous Test Complete ===

INFO  LoggingVerificationTest - === Testing Asynchronous Logging ===
INFO  LoggingVerificationTest - Executing asynchronous tool calls...
DEBUG DefaultToolCallingManager - Executing async tool call: asyncTool
DEBUG DefaultToolCallingManager - Tool 'asyncTool' supports async execution, using callAsync()
DEBUG DefaultToolCallingManager - Executing async tool call: syncTool
DEBUG DefaultToolCallingManager - Tool 'syncTool' does not support async, using sync fallback on boundedElastic scheduler
INFO  LoggingVerificationTest - === Asynchronous Test Complete ===
```

---

## 📊 验证关键日志点

### **1. 同步方法日志**

**应该看到**:
```
DEBUG - Executing tool call: toolName (synchronous mode)
```

**如果工具支持异步，还应该看到**:
```
DEBUG - Tool 'toolName' implements AsyncToolCallback but executing in synchronous mode. 
        Consider using executeToolCallsAsync() for better performance.
```

### **2. 异步方法日志**

**对于异步工具**:
```
DEBUG - Executing async tool call: toolName
DEBUG - Tool 'toolName' supports async execution, using callAsync()
```

**对于同步工具回退**:
```
DEBUG - Executing async tool call: toolName
DEBUG - Tool 'toolName' does not support async, using sync fallback on boundedElastic scheduler
```

---

## 🧪 集成测试示例

### **创建完整的集成测试**

```bash
# 创建测试目录
mkdir -p spring-ai-integration-tests/src/test/java/org/springframework/ai/integration/tool
```

**IntegrationTest.java**:
```java
package org.springframework.ai.integration.tool;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.AsyncToolCallback;
import org.springframework.ai.tool.ToolCallback;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ToolCallingIntegrationTest {
    
    private static final Logger logger = LoggerFactory.getLogger(ToolCallingIntegrationTest.class);
    
    @Test
    void testRealWorldScenario() {
        logger.info("=== Real World Integration Test ===");
        
        // 模拟真实场景：混合同步和异步工具
        ToolCallback weatherTool = createWeatherTool();
        AsyncToolCallback databaseTool = createDatabaseTool();
        ToolCallback calculatorTool = createCalculatorTool();
        
        DefaultToolCallingManager manager = DefaultToolCallingManager.builder().build();
        
        // 模拟LLM响应
        AssistantMessage llmResponse = AssistantMessage.builder()
            .content("I'll check the weather, query the database, and do some calculations")
            .toolCall("call1", "weatherTool", "{\"city\":\"Beijing\"}")
            .toolCall("call2", "databaseTool", "{\"query\":\"SELECT * FROM users\"}")
            .toolCall("call3", "calculatorTool", "{\"expression\":\"2+2\"}")
            .build();
        
        ChatResponse response = new ChatResponse(List.of(new Generation(llmResponse)));
        Prompt prompt = new Prompt("test",
            ToolCallingChatOptions.builder()
                .toolCallbacks(List.of(weatherTool, databaseTool, calculatorTool))
                .build());
        
        // 测试同步执行
        logger.info("Testing synchronous execution...");
        long syncStart = System.currentTimeMillis();
        ToolExecutionResult syncResult = manager.executeToolCalls(prompt, response);
        long syncDuration = System.currentTimeMillis() - syncStart;
        
        assertNotNull(syncResult);
        logger.info("Synchronous execution took: {}ms", syncDuration);
        
        // 测试异步执行
        logger.info("Testing asynchronous execution...");
        long asyncStart = System.currentTimeMillis();
        ToolExecutionResult asyncResult = manager.executeToolCallsAsync(prompt, response).block();
        long asyncDuration = System.currentTimeMillis() - asyncStart;
        
        assertNotNull(asyncResult);
        logger.info("Asynchronous execution took: {}ms", asyncDuration);
        
        // 性能对比
        double improvement = ((double)(syncDuration - asyncDuration) / syncDuration) * 100;
        logger.info("Performance improvement: {:.2f}%", improvement);
        
        logger.info("=== Integration Test Complete ===");
    }
    
    private ToolCallback createWeatherTool() {
        return new ToolCallback() {
            @Override
            public String call(String args) {
                simulateDelay(100);
                return "Weather: Sunny, 25°C";
            }
            // ... 其他方法
        };
    }
    
    private AsyncToolCallback createDatabaseTool() {
        return new AsyncToolCallback() {
            @Override
            public Mono<String> callAsync(String args) {
                return Mono.delay(Duration.ofMillis(200))
                    .map(__ -> "Database result: 5 users");
            }
            
            @Override
            public boolean supportsAsync() {
                return true;
            }
            // ... 其他方法
        };
    }
    
    private ToolCallback createCalculatorTool() {
        return new ToolCallback() {
            @Override
            public String call(String args) {
                simulateDelay(50);
                return "Result: 4";
            }
            // ... 其他方法
        };
    }
    
    private void simulateDelay(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

---

## 📝 手动验证清单

### **检查点1: 代码格式** ✅

```bash
./mvnw spring-javaformat:validate -pl spring-ai-model
```
- [ ] BUILD SUCCESS
- [ ] 没有格式错误

### **检查点2: 单元测试** ✅

```bash
./mvnw test -Dtest=DefaultToolCallingManagerTests -pl spring-ai-model
```
- [ ] 18个测试全部通过
- [ ] 没有失败
- [ ] 没有错误

### **检查点3: 日志输出** ✅

运行测试并检查日志：
- [ ] 看到 "Executing tool call: ... (synchronous mode)"
- [ ] 看到 "Executing async tool call: ..."
- [ ] 看到性能提示（如果适用）

### **检查点4: 模块构建** ✅

```bash
./mvnw clean install -pl spring-ai-model
```
- [ ] BUILD SUCCESS
- [ ] JAR文件成功创建

### **检查点5: 代码审查** ✅

手动检查：
- [ ] 日志使用参数化 `logger.debug("...", param)`
- [ ] 没有使用字符串拼接 `logger.debug("..." + param)` ❌
- [ ] instanceof检查正确
- [ ] 注释清晰

---

## 🎯 快速验证脚本

创建一个一键验证脚本：

**verify.sh**:
```bash
#!/bin/bash

echo "🧪 开始验证修改..."
echo ""

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查函数
check_result() {
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ $1 通过${NC}"
        return 0
    else
        echo -e "${RED}❌ $1 失败${NC}"
        return 1
    fi
}

cd /Users/shaojie/IdeaProjects/spring-ai

# 1. 格式验证
echo "1️⃣ 验证代码格式..."
./mvnw spring-javaformat:validate -pl spring-ai-model -q
check_result "代码格式"
echo ""

# 2. 单元测试
echo "2️⃣ 运行单元测试..."
./mvnw test -Dtest=DefaultToolCallingManagerTests -pl spring-ai-model -q
check_result "单元测试"
echo ""

# 3. 模块测试
echo "3️⃣ 运行模块测试..."
./mvnw test -pl spring-ai-model -q
check_result "模块测试"
echo ""

# 4. 构建
echo "4️⃣ 构建模块..."
./mvnw clean install -pl spring-ai-model -DskipTests -q
check_result "模块构建"
echo ""

echo -e "${GREEN}🎉 所有验证通过！${NC}"
```

**使用方法**:
```bash
chmod +x verify.sh
./verify.sh
```

---

## 🔍 调试技巧

### **查看详细日志**

```bash
# 启用DEBUG日志
./mvnw test -Dtest=DefaultToolCallingManagerTests \
  -pl spring-ai-model \
  -Dlogging.level.org.springframework.ai.model.tool=DEBUG
```

### **只运行特定测试**

```bash
# 运行单个测试方法
./mvnw test -Dtest=DefaultToolCallingManagerTests#whenSingleToolCallInChatResponseThenExecute \
  -pl spring-ai-model
```

### **查看编译错误**

```bash
# 详细编译输出
./mvnw clean compile -pl spring-ai-model -X
```

### **检查依赖**

```bash
# 查看依赖树
./mvnw dependency:tree -pl spring-ai-model
```

---

## 📊 性能对比测试

### **创建性能测试**

```java
@Test
void performanceComparison() {
    // 创建耗时工具
    AsyncToolCallback slowAsyncTool = createSlowAsyncTool();
    
    // 同步执行
    long syncTime = measureExecution(() -> 
        manager.executeToolCalls(prompt, response)
    );
    
    // 异步执行  
    long asyncTime = measureExecution(() ->
        manager.executeToolCallsAsync(prompt, response).block()
    );
    
    logger.info("Sync: {}ms, Async: {}ms, Improvement: {}%",
        syncTime, asyncTime, 
        ((syncTime - asyncTime) * 100.0 / syncTime));
}
```

---

## 🎯 常见问题排查

### **问题1: 测试失败**

```bash
# 清理并重试
./mvnw clean
./mvnw test -Dtest=DefaultToolCallingManagerTests -pl spring-ai-model
```

### **问题2: 格式验证失败**

```bash
# 自动修复
./mvnw spring-javaformat:apply -pl spring-ai-model

# 重新验证
./mvnw spring-javaformat:validate -pl spring-ai-model
```

### **问题3: 看不到日志**

```bash
# 配置日志级别
export MAVEN_OPTS="-Dlogging.level.org.springframework.ai.model.tool=DEBUG"

./mvnw test -Dtest=DefaultToolCallingManagerTests -pl spring-ai-model
```

### **问题4: 编译错误**

```bash
# 检查Java版本
java -version  # 应该是 17+

# 重新导入Maven项目
./mvnw clean install -pl spring-ai-model -DskipTests
```

---

## ✅ 验证成功标准

当你看到以下所有结果时，验证成功：

1. ✅ `spring-javaformat:validate` 通过
2. ✅ 所有单元测试通过（18/18）
3. ✅ 模块构建成功
4. ✅ 日志输出正确
5. ✅ 没有编译错误

**恭喜！你的修改已验证通过！** 🎉

---

## 📚 更多资源

- **Spring AI文档**: https://docs.spring.io/spring-ai/reference/
- **贡献指南**: CONTRIBUTING.adoc
- **测试最佳实践**: spring-ai-test/README.md

---

**最后更新**: 2025-10-29  
**适用版本**: Spring AI 1.1.0-SNAPSHOT

