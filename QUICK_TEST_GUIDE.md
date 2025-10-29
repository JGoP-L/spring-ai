# ⚡ 快速测试指南

**5分钟快速验证你的修改** 🚀

---

## 🎯 方法1: 一键自动验证 (推荐)

```bash
cd /Users/shaojie/IdeaProjects/spring-ai
./verify-changes.sh
```

**期望输出**:
```
🎉 恭喜！所有验证通过！
✨ 你的修改已准备就绪，可以提交PR
```

**测试覆盖**:
- ✅ Spring Java Format 验证
- ✅ 核心单元测试 (18个测试)
- ✅ 模块所有测试 (100+个测试)
- ✅ 模块构建

**耗时**: 约1-2分钟

---

## 📋 方法2: 手动分步验证

### **第1步: 格式验证** (必须)

```bash
./mvnw spring-javaformat:validate -pl spring-ai-model
```

**通过标准**: `BUILD SUCCESS`

---

### **第2步: 运行核心测试** (必须)

```bash
./mvnw test -Dtest=DefaultToolCallingManagerTests -pl spring-ai-model
```

**通过标准**: `Tests run: 18, Failures: 0, Errors: 0`

**期望看到的日志**:
```
DEBUG DefaultToolCallingManager -- Executing tool call: toolA (synchronous mode)
DEBUG DefaultToolCallingManager -- Executing async tool call: asyncTool
DEBUG DefaultToolCallingManager -- Tool 'asyncTool' supports async execution, using callAsync()
```

---

### **第3步: 运行所有测试** (推荐)

```bash
./mvnw test -pl spring-ai-model
```

**通过标准**: `BUILD SUCCESS`，无失败测试

---

### **第4步: 完整构建** (推荐)

```bash
./mvnw clean install -pl spring-ai-model
```

**通过标准**: JAR文件成功创建

---

## 🔍 验证关键日志

### **同步方法日志检查**

运行测试后，应该看到：

```
DEBUG - Executing tool call: toolName (synchronous mode)
```

**如果工具支持异步但在用同步方法**:
```
DEBUG - Tool 'toolName' implements AsyncToolCallback but executing in synchronous mode.
        Consider using executeToolCallsAsync() for better performance.
```

### **异步方法日志检查**

```
DEBUG - Executing async tool call: toolName
DEBUG - Tool 'toolName' supports async execution, using callAsync()
```

或者（同步工具回退）:
```
DEBUG - Tool 'toolName' does not support async, using sync fallback on boundedElastic scheduler
```

---

## 🧪 验证特定功能

### **只验证日志改进**

```bash
# 运行测试并过滤日志
./mvnw test -Dtest=DefaultToolCallingManagerTests -pl spring-ai-model | \
  grep "synchronous mode\|async tool call"
```

**期望输出**:
```
Executing tool call: toolA (synchronous mode)
Executing tool call: toolB (synchronous mode)
Executing async tool call: asyncTool
...
```

---

### **验证异步功能**

```bash
# 运行异步工具测试
./mvnw test -Dtest=DefaultToolCallingManagerTests#testAsync* -pl spring-ai-model
```

---

## ❌ 如果验证失败

### **格式错误**

```bash
# 自动修复
./mvnw spring-javaformat:apply -pl spring-ai-model

# 重新验证
./mvnw spring-javaformat:validate -pl spring-ai-model
```

### **测试失败**

```bash
# 查看详细错误
./mvnw test -Dtest=DefaultToolCallingManagerTests -pl spring-ai-model -X
```

### **构建失败**

```bash
# 清理并重建
./mvnw clean
./mvnw install -pl spring-ai-model
```

---

## ✅ 成功标准

**全部通过时，你会看到**:

```
✅ 代码格式 通过
✅ 单元测试 (DefaultToolCallingManagerTests) 通过  
✅ 模块测试 (spring-ai-model) 通过
✅ 模块构建 通过

🎉 恭喜！所有验证通过！
```

---

## 📚 详细文档

- **完整测试指南**: [TESTING_GUIDE.md](TESTING_GUIDE.md)
- **代码规范检查**: [CODE_STYLE_COMPLIANCE_REPORT.md](CODE_STYLE_COMPLIANCE_REPORT.md)
- **日志改进说明**: [LOGGING_IMPROVEMENTS.md](LOGGING_IMPROVEMENTS.md)

---

## 🎯 最常用命令

```bash
# 一键验证所有
./verify-changes.sh

# 只验证格式
./mvnw spring-javaformat:validate -pl spring-ai-model

# 只跑核心测试
./mvnw test -Dtest=DefaultToolCallingManagerTests -pl spring-ai-model

# 完整构建
./mvnw clean install -pl spring-ai-model
```

---

**最后更新**: 2025-10-29  
**预计耗时**: 1-2分钟（自动化）| 5-10分钟（手动）

