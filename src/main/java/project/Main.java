package project;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        loadNatives();

        // Reads image from the file, always BGR color space (blue, green, red)
        Mat mat = Imgcodecs.imread("image3.jpg");

        // convert color space from BGR to HSV color space
        // hsv mat will have the same image in
        Mat hsv = new Mat();
        Imgproc.cvtColor(
                mat, // source image
                hsv, // output matrix
                Imgproc.COLOR_BGR2HSV // code which indicates what color space to convert from and to
        );

        // Core operations
        // Core.*
        // add/subtract/multiply/divide/bitwise_or/bitwise_and/bitwise_xor
        // Core.add(src1, src2, dst)

        // removes all pixels which are not in a specific range of colors.
        // the output is a binary image, meaning 0, 1 values in a pixel.
        // remaining pixels are encoded as 1
        Mat onlyYellow = new Mat();
        Core.inRange(
                hsv, // source image
                // Hue, Sat, Val
                new Scalar(29, 123, 96), // minimum bound of color, matching the used color space
                new Scalar(50, 255, 255), // maximum bound of color, matching the used color space
                onlyYellow // output matrix
        );

        // locates contours in the image. contour = a set of close pixels which indicate
        // a single object.
        // theoretically, each contour would be a different object in the image
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(
                onlyYellow, // image to search in
                contours, // list to put found contours into
                new Mat(), // hierarchy of contours, will be discussed in the future
                Imgproc.RETR_CCOMP, // what part of the contour to store, CCOMP means store all the pixels of the contour in the result
                Imgproc.CHAIN_APPROX_SIMPLE // algorithm to use for finding contours. CHAIN_APPROX_SIMPLE will perform a simple approximation by proximity of pixels
        );

        // iterate of the contours and filter out contours which are too small
        List<MatOfPoint> filteredContours = new ArrayList<>();
        for (MatOfPoint contour : contours) {
            // if the contours' pixel count is too small, ignore it
            if (contour.total() < 30) {
                continue;
            }

            // the contour passed filtering, let's save it
            filteredContours.add(contour);

            /*
            // puts a text on the image
            Imgproc.putText(
                mat, // image to draw on
                String.valueOf(contour.total()), // text to draw (amount of pixels in the contours
                new Point(contour.get(0, 0)), // point (pixel) to start drawing from, the first pixel in the contour
                Core.FONT_HERSHEY_PLAIN, // font to use for the text
                5, // size of the font in pixels
                new Scalar(0, 255, 255), // color to draw in
                2 // thickness of the pixels
            );
             */
        }

        // draw contours on the image from a list of contours
        Imgproc.drawContours(
                mat, // image to draw on
                filteredContours, // list of contours to draw from
                -1, // index to draw from the list, or -1 to draw all contours
                new Scalar(255, 255, 0), // color to draw with, must match the color space in the image
                2 // thickness in pixels to use for drawing
        );

        // show the images
        new Window().showImage(mat);
        new Window().showImage(onlyYellow);
    }

    private static void loadNatives() {
        Natives.load(Natives.OPENCV_LIBNAME);
    }
}
