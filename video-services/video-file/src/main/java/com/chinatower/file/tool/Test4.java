//package com.chinatower.file.tool;
//
//import cn.hutool.core.date.DateUtil;
//
//import java.util.List;
//import java.util.concurrent.*;
//import java.util.function.Supplier;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//public class Test4 {
//
//    private static final ScheduledExecutorService scheduler =
//            Executors
//                    .newScheduledThreadPool(1,
//                            r -> {
//                                Thread thread = new Thread(r);
//                                thread.setName("failAfter-%d");
//                                thread.setDaemon(true);
//                                return thread;
//                            });
//
//    public static void main(String[] args) {
////        System.out.println("-------开始------");
//        final CompletableFuture<Object> oneSecondTimeout = failAfter()
//                .exceptionally(Throwable::getMessage);
//        List<Object> collect = Stream.of("1", "2", "3", "4", "5", "6", "7")
//                .map(x -> CompletableFuture.anyOf(createTaskSupplier(x), oneSecondTimeout))
//                .collect(Collectors.toList())
//                .stream()
//                .map(CompletableFuture::join)
//                .collect(Collectors.toList());
//        System.out.println("-------结束------");
//        System.out.println(collect);
//
//
//
////        CompletableFuture<Object> objectCompletableFuture = CompletableFuture.anyOf(CompletableFuture.supplyAsync(() -> {
////            try {
////                TimeUnit.SECONDS.sleep(10);
////            } catch (InterruptedException e) {
////                throw new RuntimeException(e);
////            }
////            return 1;
////        }), CompletableFuture.supplyAsync(() -> {
////            try {
////                TimeUnit.SECONDS.sleep(5);
////            } catch (InterruptedException e) {
////                throw new RuntimeException(e);
////            }
////            return 2;
////        }));
////        try {
////            Object o = objectCompletableFuture.get();
////            System.out.println(o);
////        } catch (InterruptedException | ExecutionException e) {
////            throw new RuntimeException(e);
////        }
//
//    }
//
//    public static CompletableFuture<String> createTaskSupplier(String x) {
//        return CompletableFuture.supplyAsync(getStringSupplier(x))
//                .exceptionally(Throwable::getMessage);
//    }
//
//
//    public static Supplier<String> getStringSupplier(String text) {
//        return () -> {
//            System.out.println(DateUtil.now() + Thread.currentThread().getName() + "开始 " + text);
////            if ("1".equals(text)) {
////                throw new RuntimeException("运行时错误");
////            }
//            try {
//                setSleepTime(Integer.parseInt(text));
////                if ("5".equals(text))
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println(DateUtil.now() + Thread.currentThread().getName() + "停止 " + text);
//            return text + "号";
//        };
//    }
//
//    static void setSleepTime(int SleepTime) throws InterruptedException {
//        TimeUnit.SECONDS.sleep(SleepTime * 1000L);
//    }
//
//    public static <T> CompletableFuture<T> failAfter() {
//        final CompletableFuture<T> promise = new CompletableFuture<>();
//        scheduler.schedule(() -> {
//            final TimeoutException ex = new TimeoutException("Timeout after " + 2);
//            return promise.completeExceptionally(ex);
//        }, 2, TimeUnit.SECONDS);
//        return promise;
//    }
//
//}