import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        List<Integer> list1 = Collections.synchronizedList(new ArrayList<>());
        List<Integer> list2 = new CopyOnWriteArrayList<>();
        for (Integer i = 0; i < 1000; i++) {
            list1.add(i);
        }
        for (Integer i = 0; i < 1000; i++) {
            list2.add(i);
        }
        System.out.println("List synchronyzed: ");
        checkList(list1);
        System.out.println("CopyOnWriteArrayList: ");
        checkList(list2);
    }

    public static void checkList(List<Integer> list) throws ExecutionException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Future<Long> f1 = executorService.submit(new ListRunner(0, 500, list, countDownLatch));
        Future<Long> f2 = executorService.submit(new ListRunner(50, 1000, list, countDownLatch));
        countDownLatch.countDown();

        System.out.println("Thread1: " + f1.get()/1000);
        System.out.println("Thread2: " + f2.get()/1000);
    }
    static class ListRunner implements Callable<Long> {
        int start;
        int end;
        List<Integer> list;
        CountDownLatch countDownLatch;

        public ListRunner(int start, int end, List<Integer> list, CountDownLatch countDownLatch) {
            this.start = start;
            this.end = end;
            this.list = list;
            this.countDownLatch = countDownLatch;
        }

        @Override
        public Long call() throws Exception {

            countDownLatch.await();

            long startTime = System.nanoTime();
            for (int i = start; i < end; i++) {
                list.get(i);
            }
            return System.nanoTime() - startTime;
        }
    }
}

