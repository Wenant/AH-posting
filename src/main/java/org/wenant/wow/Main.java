package org.wenant.wow;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import java.awt.*;
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

            // переводим изображения в mat
            Mat screen = convertBufferedImageToMat(screenshot);
            Mat juice = Imgcodecs.imread("juice.jpg");

            //создание таблицы результата и поиск шаблона на изображение
            Mat result = new Mat();
            Imgproc.matchTemplate(screen, juice, result, Imgproc.TM_CCOEFF_NORMED);

            // поиск совпадений и получение координат в matchLoc
            Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
            Point matchLoc = mmr.maxLoc;


            // проверка наличия совпадений
            if (Core.minMaxLoc(result).maxVal < 0.99) {
                System.out.println("Совпадений не найдено.");
            }else {
                System.out.println(matchLoc);
                robot.mouseMove((int) matchLoc.x, (int) matchLoc.y);
                //robot.mouseMove((int) matchLoc.x, (int) matchLoc.y - 100); // Перемещение курсора на небольшое расстояние
            }
            // рисунок квадрата
            Rect rect = new Rect(matchLoc, new Point(matchLoc.x + juice.cols(), matchLoc.y + juice.rows()));
            Imgproc.rectangle(screen, rect.tl(), rect.br(), new Scalar(0, 0, 255), 2);
            Imgcodecs.imwrite("output.jpg", screen);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

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