package as;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Computes the Mandelbrot set.
 * http://en.wikipedia.org/wiki/Mandelbrot_set
 */
public class Mandelbrot {
    public static final int IMAGE_LENGTH = 1024;
    public static final int MAX_ITERATIONS = 512;

    public static final int COLOR_COUNT = 64;
    private static Color[] colors = generateColors(COLOR_COUNT);

    static Color getColor(int iterations) {
        return iterations == MAX_ITERATIONS ? Color.BLACK : colors[iterations % COLOR_COUNT];
    }

    private static Color[] generateColors(int n) {
        Color[] cols = new Color[n];
        for (int i = 0; i < n; i++) {
            cols[i] = Color.hsb(((float) i / (float) n) * 360, 0.85f, 1.0f);
        }
        return cols;
    }

    public static void computeSequential(PixelPainter painter, Plane plane, CancelSupport cancel) {
        double half = plane.length / 2;
        double reMin = plane.center.r - half;
        double imMax = plane.center.i + half;
        double step = plane.length / IMAGE_LENGTH;

        for (int x = 0; x < IMAGE_LENGTH && !cancel.isCancelled(); x++) { // x-axis
            double re = reMin + x * step; // map pixel to complex plane
            for (int y = 0; y < IMAGE_LENGTH; y++) { // y-axis
                double im = imMax - y * step; // map pixel to complex plane

                //int iterations = mandel(re, im);
                int iterations = mandel(new Complex(re, im));
                painter.paint(x, y, getColor(iterations));
            }
        }
    }


    public static void computeParallel(PixelPainter painter, Plane plane, CancelSupport cancel) {
        double half = plane.length / 2;
        double reMin = plane.center.r - half;
        double imMax = plane.center.i + half;
        double step = plane.length / IMAGE_LENGTH;
        List<Thread> threadList = new ArrayList<>();
        int numOfThreads = 8;
        int planeStep = IMAGE_LENGTH / numOfThreads;

        for (int i = 0; i < numOfThreads; i++){
            int finalI = i;
            threadList.add(new Thread(() -> new MandelSlice(finalI * planeStep, (finalI + 1) * planeStep,cancel, reMin,step,imMax,painter)));
           threadList.add(new Thread(() -> calculateMandelPart(finalI * planeStep, (finalI + 1) * planeStep,cancel, reMin, step, imMax, painter), "Mandel#" + i));
        }
        threadList.forEach(thread -> {
            thread.start();
        });
        threadList.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    private static void calculateMandelPart(int start, int end, CancelSupport cancel, double reMin, double step, double imMax, PixelPainter painter){
        System.out.println("Start: " + start + " , End: " + end);
        for (int x = start; x < end  && !cancel.isCancelled(); x++) { // x-axis
            double re = reMin + x * step; // map pixel to complex plane
            for (int y = 0; y < IMAGE_LENGTH; y++) { // y-axis
                double im = imMax - y * step; // map pixel to complex plane

                //int iterations = mandel(re, im);
                int iterations = mandel(new Complex(re, im));
                painter.paint(x, y, getColor(iterations));
            }
        }
    }

    /**
     * z_n+1 = z_n^2 + c starting with z_0 = 0
     * <p>
     * Checks whether c = re + i*im is a member of the Mandelbrot set.
     *
     * @return the number of iterations
     */
    public static int mandel(Complex c) {
        Complex z = Complex.ZERO;
        int iterations = 0;
        while (z.absSq() <= 4 && iterations < MAX_ITERATIONS) {
            z = z.pow(2).plus(c);
            iterations++;
        }
        return iterations;
    }

    /**
     * Same as {@code Mandelbrot#mandel(Complex)} but more efficient.
     */
    public static final int mandel(double cre, double cim) {
        double re = 0.0;
        double im = 0.0;
        int iterations = 0;
        while (re * re + im * im <= 4 && iterations < MAX_ITERATIONS) {
            double re1 = re * re - im * im + cre;
            double im1 = 2 * re * im + cim;
            re = re1;
            im = im1;
            iterations++;
        }
        return iterations;
    }
}
