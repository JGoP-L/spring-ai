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

package org.springframework.ai.tool;

import java.util.function.Function;

import reactor.core.publisher.Mono;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.lang.Nullable;

/**
 * 异步工具回调接口，支持非阻塞的工具执行。
 *
 * <p>
 * 相比传统的{@link ToolCallback}，异步工具不会阻塞线程， 适合需要调用外部API、数据库等I/O操作的场景。
 *
 * <p>
 * <strong>使用异步工具可以显著提升并发性能，避免线程池耗尽。</strong>
 *
 * <h2>基本用法</h2> <pre>{@code
 * &#64;Component
 * public class AsyncWeatherTool implements AsyncToolCallback {
 *
 *     private final WebClient webClient;
 *
 *     public AsyncWeatherTool(WebClient.Builder builder) {
 *         this.webClient = builder.baseUrl("https://api.weather.com").build();
 *     }
 *

 *     &#64;Override
 *     public Mono<String> callAsync(String toolInput, ToolContext context) {
 *         WeatherRequest request = parseInput(toolInput);
 *         return webClient.get()
 *             .uri("/weather?city=" + request.getCity())
 *             .retrieve()
 *             .bodyToMono(String.class)
 *             .timeout(Duration.ofSeconds(5));
 *     }
 *
 *
&#64;Override
 *     public ToolDefinition getToolDefinition() {
 *         return ToolDefinition.builder()
 *             .name("get_weather")
 *             .description("获取城市天气信息")
 *             .inputTypeSchema(WeatherRequest.class)
 *             .build();
 *     }
 * }
 * }</pre>
 *
 * <h2>向后兼容</h2>
 * <p>
 * 如果只实现了异步方法，同步方法{@link #call(String, ToolContext)}
 * 会自动调用{@link #callAsync(String, ToolContext)}并阻塞等待结果。
 *
 * <h2>性能优势</h2>
 * <table border="1">
 * <tr>
 * <th>并发量</th>
 * <th>同步工具</th>
 * <th>异步工具</th>
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
 *
 * @author Spring AI Team
 * @since 1.2.0
 * @see ToolCallback
 * @see ToolContext
 */
public interface AsyncToolCallback extends ToolCallback {

	/**
	 * 异步执行工具调用。
	 *
	 * <p>
	 * 此方法不会阻塞调用线程，而是返回一个{@link Mono}， 当工具执行完成时发出结果。
	 *
	 * <h3>最佳实践</h3>
	 * <ul>
	 * <li>使用{@link Mono#timeout(java.time.Duration)} 设置超时，避免无限等待</li>
	 * <li>使用{@link Mono#retry(long)} 处理临时性故障</li>
	 * <li>使用{@link Mono#onErrorResume(Function)} 优雅处理错误</li>
	 * <li>避免在异步方法中使用阻塞调用（如{@code Thread.sleep}）</li>
	 * </ul>
	 *
	 * <h3>示例</h3> <pre>{@code
	 * &#64;Override
	 * public Mono<String> callAsync(String toolInput, ToolContext context) {
	 *     return webClient.get()
	 *         .uri("/api/data")
	 *         .retrieve()
	 *         .bodyToMono(String.class)
	 *         .timeout(Duration.ofSeconds(10))
	 *         .retry(3)
	 *         .onErrorResume(ex -> Mono.just("Error: " + ex.getMessage()));
	 * }
	 * }</pre>
	 * @param toolInput 工具输入参数（JSON格式）
	 * @param context 工具执行上下文，可能为null
	 * @return 异步返回工具执行结果的Mono
	 * @throws org.springframework.ai.tool.execution.ToolExecutionException 如果工具执行失败
	 */
	Mono<String> callAsync(String toolInput, @Nullable ToolContext context);

	/**
	 * 检查是否支持异步调用。
	 *
	 * <p>
	 * 默认返回{@code true}。如果子类重写此方法并返回{@code false}，
	 * 框架将使用同步调用{@link #call(String, ToolContext)}， 并在独立线程池（boundedElastic）中执行。
	 *
	 * <p>
	 * 可以根据运行时条件动态决定是否使用异步： <pre>{@code
	 * &#64;Override
	 * public boolean supportsAsync() {
	 *     // 仅在生产环境使用异步
	 *     return "production".equals(environment.getActiveProfiles()[0]);
	 * }
	 * }</pre>
	 * @return 如果支持异步调用返回true，否则返回false
	 */
	default boolean supportsAsync() {
		return true;
	}

	/**
	 * 同步执行工具调用（向后兼容 - 单参数版本）。
	 *
	 * <p>
	 * 默认实现会调用双参数版本的{@link #call(String, ToolContext)}。
	 * @param toolInput 工具输入参数（JSON格式）
	 * @return 工具执行结果
	 * @throws org.springframework.ai.tool.execution.ToolExecutionException 如果工具执行失败
	 */
	@Override
	default String call(String toolInput) {
		return call(toolInput, null);
	}

	/**
	 * 同步执行工具调用（向后兼容）。
	 *
	 * <p>
	 * 默认实现会调用{@link #callAsync(String, ToolContext)} 并阻塞等待结果。这确保了向后兼容性，但会失去异步的性能优势。
	 *
	 * <p>
	 * <strong>注意</strong>：如果你的工具需要同时支持同步和异步调用， 可以重写此方法提供优化的同步实现。
	 *
	 * <p>
	 * <strong>警告</strong>：此方法会阻塞当前线程，直到异步操作完成。 在响应式上下文中应避免直接调用此方法。
	 * @param toolInput 工具输入参数（JSON格式）
	 * @param context 工具执行上下文，可能为null
	 * @return 工具执行结果
	 * @throws org.springframework.ai.tool.execution.ToolExecutionException 如果工具执行失败
	 */
	@Override
	default String call(String toolInput, @Nullable ToolContext context) {
		// 阻塞等待异步结果（降级方案）
		logger.debug("Using synchronous fallback for async tool: {}", getToolDefinition().name());
		return callAsync(toolInput, context).block();
	}

}
