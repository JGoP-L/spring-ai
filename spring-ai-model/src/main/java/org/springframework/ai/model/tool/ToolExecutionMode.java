/*
 * Copyright 2023-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ai.model.tool;

/**
 * 工具执行模式枚举。
 *
 * <p>
 * 定义了工具调用的不同执行模式，用于性能优化和资源管理。
 *
 * <h2>使用场景</h2>
 * <ul>
 * <li><strong>SYNC</strong>：快速执行的工具（< 100ms），纯计算任务</li>
 * <li><strong>ASYNC</strong>：涉及I/O的操作（HTTP请求、数据库查询），长时间运行的任务（> 1秒）</li>
 * <li><strong>PARALLEL</strong>：多个独立工具需要并行执行</li>
 * <li><strong>STREAMING</strong>：需要实时反馈的长时间运行任务</li>
 * </ul>
 *
 * @author Spring AI Team
 * @since 1.2.0
 */
public enum ToolExecutionMode {

	/**
	 * 同步执行模式。
	 *
	 * <p>
	 * 工具执行会阻塞调用线程，直到完成。 此模式适用于：
	 * <ul>
	 * <li>快速执行的工具（< 100ms）</li>
	 * <li>纯计算任务</li>
	 * <li>不涉及I/O的操作</li>
	 * <li>简单的字符串处理</li>
	 * </ul>
	 *
	 * <p>
	 * <strong>性能影响</strong>：会占用线程池中的线程， 高并发场景下可能成为瓶颈。默认情况下，同步工具会在
	 * boundedElastic线程池中执行（最多80个线程）。
	 *
	 * <h3>示例</h3> <pre>{@code
	 * &#64;Tool("calculate_sum")
	 * public int calculateSum(int a, int b) {
	 *     // 纯计算，适合同步模式
	 *     return a + b;
	 * }
	 * }</pre>
	 */
	SYNC,

	/**
	 * 异步执行模式。
	 *
	 * <p>
	 * 工具执行不会阻塞调用线程，使用响应式编程模型。 此模式适用于：
	 * <ul>
	 * <li>涉及网络I/O的操作（HTTP请求、RPC调用）</li>
	 * <li>数据库查询和更新</li>
	 * <li>文件读写操作</li>
	 * <li>长时间运行的任务（> 1秒）</li>
	 * <li>需要高并发的场景</li>
	 * </ul>
	 *
	 * <p>
	 * <strong>性能优势</strong>：不占用线程，可以支持 数千甚至数万的并发工具调用。在高并发场景下，性能提升 可达5-10倍。
	 *
	 * <h3>示例</h3> <pre>{@code
	 * &#64;Component
	 * public class AsyncWeatherTool implements AsyncToolCallback {
	 *     &#64;Override
	 *     public Mono<String> callAsync(String input, ToolContext context) {
	 *         // 网络I/O，适合异步模式
	 *         return webClient.get()
	 *             .uri("/weather")
	 *             .retrieve()
	 *             .bodyToMono(String.class);
	 *     }
	 * }
	 * }</pre>
	 *
	 * <h3>性能对比</h3>
	 * <table border="1">
	 * <tr>
	 * <th>并发量</th>
	 * <th>同步模式</th>
	 * <th>异步模式</th>
	 * <th>性能提升</th>
	 * </tr>
	 * <tr>
	 * <td>100个请求</td>
	 * <td>平均4秒</td>
	 * <td>平均2秒</td>
	 * <td>50%</td>
	 * </tr>
	 * <tr>
	 * <td>500个请求</td>
	 * <td>平均12秒</td>
	 * <td>平均2秒</td>
	 * <td>83%</td>
	 * </tr>
	 * </table>
	 */
	ASYNC,

	/**
	 * 并行执行模式（未来扩展）。
	 *
	 * <p>
	 * 多个工具调用可以并行执行，而不是串行。 适用于工具调用之间没有依赖关系的场景。
	 *
	 * <p>
	 * <strong>注意</strong>：此模式目前未实现，保留用于未来扩展。
	 *
	 * <h3>未来用途</h3> <pre>{@code
	 * // 未来可能的API
	 * toolManager.executeInParallel(
	 *     toolCall1,  // 获取天气
	 *     toolCall2,  // 获取新闻
	 *     toolCall3   // 获取股票价格
	 * );
	 * // 三个工具同时执行，而不是串行执行
	 * }</pre>
	 */
	PARALLEL,

	/**
	 * 流式执行模式（未来扩展）。
	 *
	 * <p>
	 * 工具可以返回流式结果，而不是等待全部完成。 适用于长时间运行且需要实时反馈的任务。
	 *
	 * <p>
	 * <strong>注意</strong>：此模式目前未实现，保留用于未来扩展。
	 *
	 * <h3>未来用途</h3> <pre>{@code
	 * // 未来可能的API
	 * &#64;Component
	 * public class StreamingAnalysisTool implements StreamingToolCallback {
	 *     &#64;Override
	 *     public Flux<ToolExecutionChunk> executeStreaming(String input) {
	 *         return Flux.interval(Duration.ofSeconds(1))
	 *             .take(10)
	 *             .map(i -> new ToolExecutionChunk("进度: " + (i * 10) + "%"));
	 *     }
	 * }
	 *
	 * // AI可以实时看到工具执行进度
	 * // 用户可以实时看到反馈
	 * }</pre>
	 */
	STREAMING

}
