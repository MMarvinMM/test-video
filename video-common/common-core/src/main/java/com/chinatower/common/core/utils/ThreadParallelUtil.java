package com.chinatower.common.core.utils;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import cn.hutool.extra.spring.SpringUtil;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings("all")
public abstract class ThreadParallelUtil {
    private static volatile Boolean useSingleThread;

    public static int timeout;
    private static final Logger LOGGER = LoggerFactory.getLogger("threadMonitor");

    static {
        try {
            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName("switch monitor");
                return thread;
            });
            String appName = ((AbstractApplicationContext) SpringUtil.getApplicationContext()).getBeanFactory().resolveEmbeddedValue("${spring.application.name}");
            String timeoutStr = ((AbstractApplicationContext) SpringUtil.getApplicationContext()).getBeanFactory().resolveEmbeddedValue("${thread.paralle.timeout:3}");
            if (!StringUtils.isEmpty(timeoutStr)) {
                timeout = Integer.parseInt(timeoutStr);
            } else {
                timeout = 3;
            }
            log.info("获取线程超时时间:{}", timeout);
            RedisTemplate redisTemplate = SpringUtil.getBean(RedisTemplate.class);
            String switchValue = (String) redisTemplate.opsForValue().get(appName + "-parallel-switch");
            if (useSingleThread == null) {
                useSingleThread = false;
            } else {
                if ("1".equals(switchValue)) {
                    useSingleThread = true;
                } else {
                    useSingleThread = false;
                }
            }

            executorService.scheduleAtFixedRate(() -> {
                String switchValueNew = (String) redisTemplate.opsForValue().get(appName + "-parallel-switch");
                if ("1".equals(switchValueNew)) {
                    useSingleThread = true;
                } else {
                    useSingleThread = false;
                }
            }, 1, 1, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error("并行开关失效", e);
            useSingleThread = false;
        }
    }

    public enum ConfigFactory {
        DEFAULT_SINGLE() {
            @Override
            public Config config() {
                return CONFIG;
            }

            @Override
            public synchronized Config create() {
                return super.create();
            }
        },
        WARNING_ORDER() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (this.config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    this.config = (new Config.ConfigBuilder()).threadPoolExecutor(
                            new ThreadPoolExecutor(15, 30, 300, TimeUnit.SECONDS, new SynchronousQueue(), factory, new ThreadPoolExecutor.AbortPolicy())
                    ).build();
                }
                config.timeUnit = TimeUnit.SECONDS;
                config.useTimeout = true;
                config.timeoutThreshold = timeout;
                return this.config;
            }
        },
        WARNING_ORDER_LIST() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            15, 30, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        WARNING_ORDER_COMPLETED() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            15, 30, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        WARNING_LEADER() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            3, 15, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        WARNING_INTERCEPT() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            3, 15, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        WARNING_CODE() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            10, 40, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        DOWNLOAD_IMG() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            50, 100, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        EVENT_EXPORT_CODE() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            34, 68, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        DEVICE_EXPORT_CODE() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            12, 24, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        USER_EXPORT_CODE() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            28, 56, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        ALGORITHM_CONFIG_STATUS() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            10, 100, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },

        GET_VIDEO_REALTIME_URLS() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            10, 100, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        QUERY_YG_DEVICE_AND_CHANNEL() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            10, 100, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        GET_DEVICE_INFO_DETAIL() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            10, 100, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        ADD_ALARM_LINKAGE_LIST() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            10, 100, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        UPDATE_ALARM_LINKAGE_LIST() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            10, 100, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        DEL_ALARM_LINKAGE_LIST() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            10, 100, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        PRESET_SNAPSHOT_JOB() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            10, 100, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        VIDEO_SNAPSHOT_JOB {
            private Config config = null;

            public synchronized Config config() {
                if (this.config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    this.config = (new Config.ConfigBuilder()).threadPoolExecutor(new ThreadPoolExecutor(10, 100, 300L, TimeUnit.SECONDS, new ArrayBlockingQueue(1000), factory, new ThreadPoolExecutor.AbortPolicy())).build();
                }

                return this.config;
            }
        },

        VIDEO_REQUEST_JOB {
            private Config config = null;

            public synchronized Config config() {
                if (this.config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    this.config = (new Config.ConfigBuilder()).threadPoolExecutor(
                            new ThreadPoolExecutor(10, 300, 3L, TimeUnit.SECONDS, new SynchronousQueue(), factory, new ThreadPoolExecutor.AbortPolicy())
                    ).build();
                }
                config.timeUnit = TimeUnit.SECONDS;
                config.useTimeout = true;
                config.timeoutThreshold = timeout;
                return this.config;
            }
        },

        BATCH_ORDER() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            30, 100, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        AROUND_ANALYSIS() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            10, 100, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        DOWNLOAD_VIDEO() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            10, 100, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },

        SECOND_DOWN_VIDEO() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            5, 5, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        FIRE_LEVEL_LAYER() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            5, 5, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },

        TAOHE_ANALYSIS() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            1, 100, 100L, TimeUnit.MILLISECONDS,
                            new LinkedBlockingQueue<>(1000),
                            r -> {
                                Thread thread = new Thread(r);
                                thread.setDaemon(true);
                                thread.setName("cal pg layer");
                                return thread;
                            }
                    )).build();
                }
                return config;
            }
        },
        TASK_ALGORITHM_STATISTICS() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            50, 100, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },
        HORN_START_TALK() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            10, 10, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },

        HORN_TASK() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            5, 10, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },

        FILE_ENCRYPT() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            10, 100, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.AbortPolicy()
                    )).useTimeout(true).timeoutThreshold(timeout).timeUnit(TimeUnit.SECONDS).build();
                }
                log.info("使用超时配置:{}", timeout);
                return config;
            }
        },

        TAG_MOVE_LIST {
            private Config config = null;

            public synchronized Config config() {
                if (this.config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    this.config = (new Config.ConfigBuilder()).threadPoolExecutor(new ThreadPoolExecutor(10, 20, 300L, TimeUnit.SECONDS, new SynchronousQueue(), factory, new ThreadPoolExecutor.CallerRunsPolicy())).build();
                }
                return this.config;
            }
        },

        TAG_MOVE_RESULT_LIST {
            private Config config = null;

            public synchronized Config config() {
                if (this.config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    this.config = (new Config.ConfigBuilder()).threadPoolExecutor(new ThreadPoolExecutor(20, 100, 300L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy())).build();
                }
                return this.config;
            }
        },

        SYNC_SNAP_SHOT {
            private Config config = null;

            public synchronized Config config() {
                if (this.config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    this.config = (new Config.ConfigBuilder()).threadPoolExecutor(
                            new ThreadPoolExecutor(10, 50, 3L, TimeUnit.SECONDS, new SynchronousQueue(), factory, new ThreadPoolExecutor.AbortPolicy())
                    ).build();
                }
                return this.config;
            }
        },

        USER_LOGIN_WEB() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            50, 200, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        },

        ORDER_DEVICE_DEAL() {
            private Config config = null;

            @Override
            public synchronized Config config() {
                if (config == null) {
                    ThreadFactory factory = ThreadFactoryBuilder.create().setNamePrefix(this.name()).build();
                    config = new Config.ConfigBuilder<>().threadPoolExecutor(new ThreadPoolExecutor(
                            2, 10, 300, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), factory, new ThreadPoolExecutor.CallerRunsPolicy()
                    )).build();
                }
                return config;
            }
        };

        private static final Map<ConfigFactory, Config> container = new ConcurrentHashMap<>();


        private static final ScheduledExecutorService THREAD_POOL_MONITOR = Executors.newSingleThreadScheduledExecutor(r -> new Thread(r, "threadMonitor:"));

        static {
            THREAD_POOL_MONITOR.scheduleAtFixedRate(() -> {
                for (ConfigFactory entry : container.keySet()) {
                    ThreadPoolExecutor executor = (ThreadPoolExecutor) container.get(entry).threadPoolExecutor;
                    int activeCount = executor.getActiveCount();
                    int poolSize = executor.getPoolSize();
                    int corePoolSize = executor.getCorePoolSize();
                    long completedTaskCount = executor.getCompletedTaskCount();
                    int largestPoolSize = executor.getLargestPoolSize();
                    int maximumPoolSize = executor.getMaximumPoolSize();
                    int size = executor.getQueue().size();
                    long taskCount = executor.getTaskCount();
                    LOGGER.info("ThreadPool = {} , useSingleThread = {}, activeCount = {} , poolSize = {} , corePoolSize = {}, completedTaskCount = {} , largestPoolSize = {} , maximumPoolSize = {} , queueSize = {} , taskCount = {} ",
                            entry.name(), useSingleThread, activeCount, poolSize, corePoolSize, completedTaskCount, largestPoolSize, maximumPoolSize, size, taskCount);
                }
            }, 0, 1, TimeUnit.SECONDS);
        }

        ConfigFactory() {
        }

        public abstract Config config();

        public Config create() {
            Config config = config();
            container.putIfAbsent(this, config);
            return container.get(this);
        }

    }

    /**
     * <pre>
     *    public static void main(String[] args) {
     *         System.out.println(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + " start");
     *         List<Integer> list = run(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), it -> {
     *             try {
     *                 TimeUnit.SECONDS.sleep(it);
     *             } catch (InterruptedException e) {
     *                 throw new RuntimeException(e);
     *             }
     *             return it * 10;
     *         }, new Config
     *                 .ConfigBuilder()
     *                 .useTimeout(false)
     *                 .threadPoolExecutor(new ThreadPoolExecutor(10, 10, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(199)))
     *                 .build());
     *         System.out.println(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + " end list = " + list);
     *
     *
     *         System.out.println(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + " start");
     *         List<Integer> list1 = run(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), it -> {
     *             try {
     *                 TimeUnit.SECONDS.sleep(it);
     *             } catch (InterruptedException e) {
     *                 throw new RuntimeException(e);
     *             }
     *             return it * 10;
     *         }, new Config
     *                 .ConfigBuilder()
     *                 .useTimeout(true)
     *                 .timeUnit(TimeUnit.SECONDS)
     *                 .timeoutThreshold(5)
     *                 .threadPoolExecutor(new ThreadPoolExecutor(10, 10, 1, TimeUnit.MINUTES, new ArrayBlockingQueue<>(199)))
     *                 .build());
     *         System.out.println(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + " end list = " + list1);
     *
     *         System.out.println(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + " start");
     *         List<Integer> list2 = run(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), it -> {
     *             try {
     *                 TimeUnit.SECONDS.sleep(it);
     *             } catch (InterruptedException e) {
     *                 throw new RuntimeException(e);
     *             }
     *             return it * 10;
     *         }, CONFIG);
     *         System.out.println(DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + " end list = " + list2);
     *
     *     }
     * </pre>
     *
     * @param sourceList    并行数据源
     * @param eachHandler   这个参数代表，对数据源中的每一个数据进行什么样的处理
     * @param configFactory 并行配置 ： builder 、工厂  com.chinatower.common.core.utils.ThreadParallelUtil.ConfigFactory
     * @param <T>           数据源类型
     * @param <R>           返回类型
     */
    public static <T, R> List<R> run(final List<T> sourceList,
                                     final Function<T, R> eachHandler,
                                     ConfigFactory configFactory) {
        Config config;
        if (useSingleThread || configFactory == null || (config = configFactory.create()) == null) {
            config = ConfigFactory.DEFAULT_SINGLE.create();
        }
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        Executor executor = config.threadPoolExecutor;
        boolean useTimeout = config.useTimeout;
        TimeUnit timeUnit = config.timeUnit;
        int timeoutThreshold = config.timeoutThreshold;
        Predicate<R> timeOutFilter = config.timeOutFilter;

        List<CompletableFuture<R>> completableFutureList = sourceList.stream().map(it -> {
            try {
                return CompletableFuture.supplyAsync(() -> {
                    RequestContextHolder.setRequestAttributes(requestAttributes, true);
                    return eachHandler.apply(it);
                }, executor);
            } catch (RejectedExecutionException e) {
                log.error("任务被线程池拒绝执行，当前任务: {}, 线程池信息: 核心线程数={}, 最大线程数={}, 活跃线程数={}, 队列大小={}",
                        it.toString(),

                        ((ThreadPoolExecutor) executor).getCorePoolSize(),
                        ((ThreadPoolExecutor) executor).getMaximumPoolSize(),
                        ((ThreadPoolExecutor) executor).getActiveCount(),
                        ((ThreadPoolExecutor) executor).getQueue().size(),
                        e);

                return CompletableFuture.<R>completedFuture(null);
            } catch (Exception e) {
                log.error("任务执行异常 ", e);
                return CompletableFuture.<R>completedFuture(null);
            }
        }).collect(Collectors.toList());

        if (useTimeout) {
            if (timeUnit == null || timeoutThreshold == 0) {
                throw new IllegalArgumentException("超时参数存在问题");
            }
            Config finalConfig = config;
            completableFutureList = completableFutureList.stream()
                    .map(it -> {
                        Supplier<R> timeoutHandler = finalConfig.timeoutHandler;
                        if (timeoutHandler != null) {
                            return configWithTimeout(it, timeoutThreshold, timeUnit)
                                    .exceptionally(ex -> {
                                        log.error("任务执行失败 ", ex);
                                        return timeoutHandler.get();
                                    });
                        }
                        return configWithTimeout(it, timeoutThreshold, timeUnit);
                    })
                    .collect(Collectors.toList());
            return completableFutureList.stream()
                    .map(it -> {
                        try {
                            return it.get();
                        } catch (InterruptedException | ExecutionException e) {
                            return null;
                        }
                    })
                    .filter(it -> timeOutFilter == null || timeOutFilter.test(it))
                    .collect(Collectors.toList());

        } else {
            CompletableFuture<R>[] array = new CompletableFuture[completableFutureList.size()];
            System.arraycopy(completableFutureList.toArray(), 0, array, 0, array.length);
            CompletableFuture.allOf(array).join();

            return Arrays.stream(array).map(it -> {
                try {
                    return it.get();
                } catch (InterruptedException | ExecutionException e) {
                    log.error("并发异常", e);
                }
                return null;
            }).collect(Collectors.toList());
        }
    }

    @Builder
    public static class Config<T, R> {
        private boolean useTimeout;
        private TimeUnit timeUnit;
        private int timeoutThreshold;
        private Executor threadPoolExecutor;
        private Predicate<R> timeOutFilter;
        private Supplier<R> timeoutHandler;
    }

    public static <T> CompletableFuture<T> configWithTimeout(CompletableFuture<T> future, long timeout, TimeUnit unit) {
        if (null == unit || null == future) {
            throw new IllegalArgumentException("参数为空");
        }
        if (future.isDone()) {
            return future;
        }

        return future.whenComplete(new Canceller(Delayer.delay(new Timeout(future), timeout, unit)));
    }

    static final class Timeout implements Runnable {
        final CompletableFuture<?> future;

        Timeout(CompletableFuture<?> future) {
            this.future = future;
        }

        public void run() {
            if (null != future && !future.isDone()) {
                future.completeExceptionally(new TimeoutException());
            }
        }
    }

    static final class Canceller implements BiConsumer<Object, Throwable> {
        final Future<?> future;

        Canceller(Future<?> future) {
            this.future = future;
        }

        public void accept(Object ignore, Throwable ex) {
            if (null == ex && null != future && !future.isDone()) {
                future.cancel(false);
            }
        }
    }

    static final class Delayer {
        static ScheduledFuture<?> delay(Runnable command, long delay, TimeUnit unit) {
            return delayer.schedule(command, delay, unit);
        }

        static final class DaemonThreadFactory implements ThreadFactory {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setName("canceller monitor ");
                return t;
            }
        }

        static final ScheduledThreadPoolExecutor delayer;

        static {
            delayer = new ScheduledThreadPoolExecutor(1, new DaemonThreadFactory());
            delayer.setRemoveOnCancelPolicy(true);
        }
    }

    static class CallerExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }

    public static final Config CONFIG = Config
            .builder()
            .useTimeout(false)
            .threadPoolExecutor(new CallerExecutor())
            .build();

}
