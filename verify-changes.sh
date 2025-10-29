#!/bin/bash

echo "🧪 开始验证Spring AI修改..."
echo ""

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
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

FAILED=0

# 进入项目目录
cd "$(dirname "$0")"

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}1️⃣  验证代码格式 (Spring Java Format)${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
./mvnw spring-javaformat:validate -pl spring-ai-model -q
if check_result "代码格式"; then
    echo ""
else
    echo -e "${YELLOW}💡 提示: 运行 ./mvnw spring-javaformat:apply -pl spring-ai-model 自动修复${NC}"
    echo ""
    FAILED=1
fi

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}2️⃣  运行核心单元测试${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
./mvnw test -Dtest=DefaultToolCallingManagerTests -pl spring-ai-model -q
check_result "单元测试 (DefaultToolCallingManagerTests)"
if [ $? -ne 0 ]; then FAILED=1; fi
echo ""

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}3️⃣  运行模块所有测试${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}⏳ 这可能需要几分钟...${NC}"
./mvnw test -pl spring-ai-model -q
check_result "模块测试 (spring-ai-model)"
if [ $? -ne 0 ]; then FAILED=1; fi
echo ""

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}4️⃣  构建模块${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
./mvnw clean install -pl spring-ai-model -DskipTests -q
check_result "模块构建"
if [ $? -ne 0 ]; then FAILED=1; fi
echo ""

echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${BLUE}📊 验证总结${NC}"
echo -e "${BLUE}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}🎉 恭喜！所有验证通过！${NC}"
    echo -e "${GREEN}✨ 你的修改已准备就绪，可以提交PR${NC}"
    exit 0
else
    echo -e "${RED}⚠️  部分验证失败，请检查上述错误${NC}"
    echo -e "${YELLOW}💡 常见解决方法：${NC}"
    echo -e "   1. 格式问题: ./mvnw spring-javaformat:apply -pl spring-ai-model"
    echo -e "   2. 测试失败: 查看具体错误信息，修复代码"
    echo -e "   3. 构建失败: ./mvnw clean install -pl spring-ai-model"
    exit 1
fi
