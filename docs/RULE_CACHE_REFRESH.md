# RuleCacheManager 定时刷新机制

## 概述

`RuleCacheManager` 现在包含一个定时任务，会定期刷新规则缓存，确保从数据库获取最新的规则配置。

## 实现细节

### 1. 定时任务配置

```java
@Scheduled(fixedDelayString = "${app.rule.cache-expire-millis:60000}")
public void refreshCache() {
    log.debug("Scheduled cache refresh triggered");
    invalidateAll();
    log.info("Cache refreshed successfully at {}", java.time.LocalDateTime.now());
}
```

### 2. 工作原理

- **触发方式**: `fixedDelay` - 在上一次执行完成后等待指定时间再执行下一次
- **默认周期**: 60000 毫秒（60秒）
- **可配置**: 通过 `app.rule.cache-expire-millis` 配置项自定义
- **执行动作**: 调用 `invalidateAll()` 清除所有缓存

### 3. 配置示例

在 `application.yaml` 中配置：

```yaml
app:
  rule:
    cache-expire-millis: 60000  # 60秒
```

### 4. 缓存刷新流程

```
┌─────────────┐
│ 定时任务触发 │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ 清除所有缓存 │ ◄── invalidateAll()
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ 记录日志     │
└──────┬──────┘
       │
       ▼
┌─────────────┐
│ 下次访问时   │
│ 从DB重新加载 │
└─────────────┘
```

### 5. 优势

1. **自动更新**: 无需手动干预，缓存会自动刷新
2. **配置灵活**: 可通过配置文件调整刷新频率
3. **日志追踪**: 每次刷新都会记录日志，便于监控
4. **一致性**: 确保所有实例的缓存最终一致

### 6. 注意事项

- **性能影响**: 频繁刷新会增加数据库查询次数，建议根据业务需求设置合理的刷新间隔
- **并发安全**: Caffeine Cache 本身是线程安全的，多个线程同时访问不会有问题
- **首次加载**: 应用启动时会初始化缓存，定时任务会在第一个周期后开始执行

### 7. 监控建议

可以通过以下日志监控缓存刷新情况：

```
INFO  c.j.t.cache.RuleCacheManager - Cache refreshed successfully at 2026-05-01T12:37:58.070
```

如果长时间没有看到此日志，可能表示：
- 定时任务未启用（检查 `@EnableScheduling`）
- 应用负载过高导致调度延迟
- 日志级别设置不当（需要 INFO 级别）
