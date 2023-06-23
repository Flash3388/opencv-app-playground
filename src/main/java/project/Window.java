package project;

import org.opencv.core.Mat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class Window {

    private final JFrame mWindow;
    private final JLabel mDisplayLbl;

    public Window() {
        mWindow = createWindow();
        mDisplayLbl = createImageHolder(mWindow);
    }

    public void showImage(Mat img) {
        SwingUtilities.invokeLater(()-> {
            java.awt.Image awtImage = matToAwtImage(img);
            mDisplayLbl.setIcon(new ImageIcon(awtImage));
            mWindow.setVisible(true);
        });
    }

    private static JFrame createWindow() {
        JFrame frame = new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        return frame;
    }

    private static JLabel createImageHolder(JFrame frame) {
        JLabel lbl = new JLabel();
        lbl.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        lbl.setSize(200, 200);
        lbl.setVisible(true);

        frame.add(lbl);

        return lbl;
    }

    private static java.awt.Image matToAwtImage(Mat mat) {
        // based on https://riptutorial.com/opencv/example/21963/converting-an-mat-object-to-an-bufferedimage-object
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] pixels = new byte[bufferSize];
        mat.get(0, 0, pixels); // get all the pixels

        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(pixels, 0, targetPixels, 0, pixels.length);

        return image;
    }
}
