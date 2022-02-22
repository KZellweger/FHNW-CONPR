package as;

import static as.Mandelbrot.*;

public class MandelSlice implements Runnable {

    private int start;
    private int end;
    private CancelSupport cancel;
    private double reMin;
    private double step;
    private double imMax;
    private PixelPainter painter;

    public MandelSlice(int start, int end, CancelSupport cancel, double reMin, double step, double imMax, PixelPainter painter) {
        this.start = start;
        this.end = end;
        this.cancel = cancel;
        this.reMin = reMin;
        this.step = step;
        this.imMax = imMax;
        this.painter = painter;
    }

    @Override
    public void run() {
        System.out.println("Start: " + start + " , End: " + end);
        for (int x = start; x < end && !cancel.isCancelled(); x++) { // x-axis
            double re = reMin + x * step; // map pixel to complex plane
            for (int y = 0; y < IMAGE_LENGTH; y++) { // y-axis
                double im = imMax - y * step; // map pixel to complex plane

                //int iterations = mandel(re, im);
                int iterations = mandel(new Complex(re, im));
                painter.paint(x, y, getColor(iterations));
            }
        }
    }
}
