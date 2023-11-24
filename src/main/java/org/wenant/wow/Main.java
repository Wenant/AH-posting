package org.wenant.wow;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class Main {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        try {
            Robot robot = new Robot();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle screenRect = new Rectangle(screenSize);
            BufferedImage screenshot = robot.createScreenCapture(screenRect);
            Mat screen = convertBufferedImageToMat(screenshot);
            Mat postButtonImg = Imgcodecs.imread("post.jpg");
            Mat cancelButtonImg = Imgcodecs.imread("cancel.jpg");

            //цикл for для проверки, потом переделаю на кнопку
            for(int i=0; i<5; i++) {
                compareAndClick(robot, screen, cancelButtonImg);
                compareAndClick(robot, screen, postButtonImg);
            }

        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

    }
    private static void compareAndClick(Robot robot, Mat screen, Mat buttonImg ){
        //создание таблицы результата и поиск шаблона на изображение
        Mat result = new Mat();
        Imgproc.matchTemplate(screen, buttonImg, result, Imgproc.TM_CCOEFF_NORMED);
        // поиск совпадений и получение координат в matchLocation
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        Point matchLocation = mmr.maxLoc;
        // проверка наличия совпадений
        if (Core.minMaxLoc(result).maxVal >= 0.99) {
            clickToButton(robot, matchLocation);
        }
    }

    private static void clickToButton(Robot robot, Point matchLocation){
        robot.mouseMove((int) matchLocation.x, (int) matchLocation.y);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(200);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
        // Перемещение курсора на небольшое расстояние
        robot.mouseMove((int) matchLocation.x, (int) matchLocation.y - 100);
    }

    // Перевод BufferedImage в Mat
    private static Mat convertBufferedImageToMat(BufferedImage imageIn) {
        BufferedImage image = new BufferedImage(imageIn.getWidth(), imageIn.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = image.createGraphics();
        g.drawImage(imageIn, 0, 0, null);
        g.dispose();
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, pixels);
        return mat;
    }
}