package as.conbench;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class BenchmarkRunnerImpl implements BenchmarkRunner {

    private Object testObject;
    private List<BenchmarkMethodDescriptor> methodDescriptorList;
    private static final int WARM_UP = 100_000;

    @Override
    public void runBenchmark(BenchmarkDescriptor desc) {

        try {
            System.out.println("Instantiate TestClass " + desc.testClass.getName());
            testObject = desc.testClass.getDeclaredConstructor().newInstance();
            for (BenchmarkMethodDescriptor methodDescriptor : desc.testMethods) {
                System.out.println("Warm up Method " + methodDescriptor.method.getName());
                methodDescriptor.method.invoke(testObject, WARM_UP);
                List<Thread> threads = new ArrayList<>();
                for (int nThreads : methodDescriptor.nThreads) {
                    threads.add(buildTestThread(testObject, methodDescriptor.method, desc.nTimes));
                }
            }


        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException ex) {
            System.err.println("Schade");
            ex.printStackTrace();
        }
    }


    private Thread buildTestThread(Object testObject, Method runnable, int nTimes) {
        return new Thread(() -> {
            try {
                runnable.invoke(testObject, nTimes);
            } catch (IllegalAccessException | InvocationTargetException e) {
                System.err.println("Auch schade");
                e.printStackTrace();
            }
        });
    }

}
