package Kimmy;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class GestureRecognition extends JFrame {
    private static final long serialVersionUID = 1L;
    private JLabel label;
    private VideoCapture capture;
    private Mat frame;
    private Mat gray;
    private Mat diff_red_mat;
    private Mat bin_red;
    private Robot robot;

    public GestureRecognition() {
        super("Gesture Recognition");
        setLayout(new FlowLayout());
        label = new JLabel();
        add(label);
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        // Load OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Initialize camera capture
        capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.out.println("Camera not found");
            System.exit(1);
        }

        // Create a robot instance
        robot = new Robot();

        // Start gesture recognition thread
        new Thread(() -> {
            while (true) {
                // Capture frame
                capture.read(frame);

                // Convert to grayscale
                Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);

                // Extract red color channel
                Mat red = new Mat();
                Core.extractChannel(frame, red, 2);

                // Calculate difference between grayscale and red channel
                Core.absdiff(gray, red, diff_red_mat);

                // Apply Gaussian blur and median filter
                Imgproc.GaussianBlur(diff_red_mat, diff_red_mat, new Size(5, 5), 0);
                Imgproc.medianBlur(diff_red_mat, diff_red_mat, 75);

                // Apply binary threshold
                Imgproc.threshold(diff_red_mat, bin_red, 20, 200, Imgproc.THRESH_BINARY);

                // Find contours
                Mat contours = new Mat();
                Imgproc.findContours(bin_red, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

                // Iterate through contours
                for (int i = 0; i < contours.size(); i++) {
                    // Calculate contour area
                    double area = Imgproc.contourArea(contours.get(i));

                    // Filter out small contours
                    if (area > 1000) {
                        // Calculate contour bounding rectangle
                        Rect rect = Imgproc.boundingRect(contours.get(i));

                        // Draw rectangle on original frame
                        Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 2);

                        // Move mouse cursor to center of rectangle
                        Point center = new Point(rect.x + rect.width / 2, rect.y + rect.height / 2);
                        robot.mouseMove(center.x, center.y);
                    }
                }

                // Display output
                BufferedImage image = HighGui.toBufferedImage(frame);
                label.setIcon(new ImageIcon(image));
            }
        }).start();
    }

    public static void main(String[] args) {
        new GestureRecognition();
    }
}