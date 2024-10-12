package org.wpk.paddle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class PaddleProcessBuilder {
    private static final Robot ROBOT;
    private static final Rectangle SCREEN_RECT;
    // windows屏幕缩放比例
    private static final double WIN_SCREEN_RATE = 1.25;

    private static final ProcessBuilder PROCESS_BUILDER = new ProcessBuilder();
    // 创建一个单线程的ScheduledExecutorService
    private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    public static void main(String[] args) {
        PaddleProcessBuilder paddleProcessBuilder = new PaddleProcessBuilder();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    paddleProcessBuilder.task(args);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    // 在捕获 InterruptedException 时，需要恢复线程的中断状态，否则线程的中断标志会被清除，这可能会导致其他依赖于中断标志的地方出现问题
                    Thread.currentThread().interrupt();
                    System.err.println("Executor was interrupted");
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    e.printStackTrace();
                    executor.shutdown();
                }
            }
        };
        // 延迟1秒后首次执行，并且之后每3秒执行一次
        executor.scheduleAtFixedRate(task, 1, 3, TimeUnit.SECONDS);
    }

    /**
     * 具体执行逻辑
     * @param args
     */
    public void task(String[] args) throws IOException, InterruptedException {
        // key:要识别的文本
        String key = "接受";
        // keyIndex:选定截取图片范围, 识别文本重复, 通过 keyIndex 确定选定第几个, 从左上角开始, 逐行数, 注意长文本算一个元素
        int keyIndex = 0;
        int startX = 1505;
        int startY = 760;
        int width = 1845 - startX;
        int height = 1010 - startY;

        // 截屏
        BufferedImage bufferedImage = screenshotFull();
        writeImage(bufferedImage, "C:\\Users\\Apple\\Desktop\\temp\\screenshot\\");
        // 切割图片

        bufferedImage = subImage(bufferedImage, startX, startY, width, height);
        String imagePath = writeImage(bufferedImage, "C:\\Users\\Apple\\Desktop\\temp\\screenshot\\sub");
        // 定义exe路径及参数
        String exePath = "C:\\Users\\Apple\\Desktop\\temp\\Release\\ppocr.exe";
        String arg1 = "--det_model_dir=D:\\PaddleOCR\\model\\ch_PP-OCRv4_det_infer\\";
        String arg2 = "--rec_model_dir=D:\\PaddleOCR\\model\\ch_PP-OCRv4_rec_infer\\";
        String arg3 = "--rec_char_dict_path=D:\\PaddleOCR\\PaddleOCR-2.8.1\\ppocr\\utils\\ppocr_keys_v1.txt";
        String arg4 = "--image_dir=" + imagePath;
        String[] cmdParams = {exePath, arg1, arg2, arg3, arg4};
        System.out.println("ocr识别image_dir:" + imagePath);
        // 识别文本
        List<String> ocrResult = ocrProcess(cmdParams);
        List<String[]> textResultList = parseORCResult(ocrResult);
        for (String[] strings : textResultList) {
            System.out.println(Arrays.toString(strings));
        }
        //[[[69,12],[127,12],[127,37],[69,37]], 贾寒冰, 0.995404]
        //[[[70,40],[253,40],[253,56],[70,56]], 正在请求远程控制你的电脑, 0.998115]
        //[[[97,209],[137,209],[137,228],[97,228]], 接受, 0.980375]
        //[[[210,209],[249,209],[249,228],[210,228]], 拒绝, 0.998364]
        List<String[]> collectList = textResultList.stream()
                .filter(array -> Arrays.stream(array).anyMatch(s -> s.contains(key)))
                .collect(Collectors.toList());
        if (collectList.isEmpty()) {
            return;
        }
        String[] strings = collectList.get(keyIndex);
        // [[97,209],[137,209],[137,228],[97,228]], 接受, 0.980375
        if (strings.length >= 3) {
            // 去除左右括号, 解析为Map
            Map<String, int[]> locateMap = parseLocate(strings[0]);
            if (locateMap.size() > 0) {
                // 因为图片切割识别, 需要计算点击位置
                // 计算识别文本框宽和高,左上 右上 右下 左下
                int[] point0 = locateMap.get("point0");
                int[] point1 = locateMap.get("point1");
                int[] point2 = locateMap.get("point2");
                int[] point3 = locateMap.get("point3");
//                    System.out.println(Arrays.toString(point0));
//                    System.out.println(Arrays.toString(point2));
                int x0 = point0[0];
                int y0 = point0[1];

                int x2 = point2[0];
                int y2 = point2[1];
                int avgWidth = (x2 - x0) / 2;
                int avgLength = (y2 - y0) / 2;
//                    System.out.println("avgWidth=" + avgWidth);
//                    System.out.println("avgLength=" + avgLength);
                // 点击位置x=截取开始+定位开始+avgWidth
                // 点击位置y=截取开始+定位开始+avgWidth
                clickRight(startX + x0 + avgWidth, startY + y0 + avgLength);
            }
        }

    }

    public static Map<String, int[]> parseLocate(String locateStr) {
        HashMap<String, int[]> map = new HashMap<>();
        if (locateStr.length() == 0) {
            return map;
        }
        // 截取前后[], 结果:[97,209],[137,209],[137,228],[97,228]
        locateStr = locateStr.substring(2, locateStr.length() - 2);
        String[] split = locateStr.split("],\\[");
        if (split.length >= 4) {
            for (int i = 0; i < split.length; i++) {
                String[] xyStr = split[i].split(",");
                if (xyStr.length >= 2) {
                    int[] xy = {Integer.parseInt(xyStr[0]), Integer.parseInt(xyStr[1])};
                    map.put("point" + i, xy);
                }
            }
        }
        return map;
    }

    public static java.util.List<String> ocrProcess(String[] cmdParams) throws IOException, InterruptedException {
        // 使用ProcessBuilder类执行命令, command每次调用是覆盖操作
        PROCESS_BUILDER.command(cmdParams);
        PROCESS_BUILDER.redirectErrorStream(true); // 将错误流重定向到标准输出流

        Process process = PROCESS_BUILDER.start();

        // 获取进程的输出流
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()))
        ){
            java.util.List<String> lineList = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
                lineList.add(line);
            }

            // 获取错误流，如果有错误信息的话
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }

            // 等待程序执行完成
            int exitCode = process.waitFor();
//            System.out.println("Program exited with code: " + exitCode);
            return lineList;
        }
    }

    /**
     * 解析ocr识别结果
     * @return
     */
    public static java.util.List<String[]> parseORCResult(java.util.List<String> ocrResultList) {
        java.util.List<String[]> resultList = new ArrayList<>();
        int collectIndex = 0;
        for (String line : ocrResultList) {
            if (line.startsWith(collectIndex + "\tdet boxes: ")) {
                // 有效字符串:0	det boxes: [[316,134],[526,134],[526,223],[316,223]] rec text: 1202 rec score: 0.97366
                // 有效字符串:1	det boxes: [[263,247],[571,245],[571,327],[263,329]] rec text: 120228 rec score: 0.998371
                // 有效字符串:3	det boxes: [[475,472],[621,472],[621,563],[475,563]] rec text:  rec score: 0
                /*
                切割(索引:结果):
                0:1	det
                1:boxes:
                2:[[263,247],[571,245],[571,327],[263,329]]
                3:rec
                4:text:
                5:120228
                6:rec
                7:score:
                8:0.998371
                */
                String[] split = line.split(" ");
                if (split.length >= 8) {
                    // 索引2:坐标
                    // 索引5:识别结果
                    // 索引8:score
                    String[] res = {split[2], split[5], split[8]};
                    resultList.add(res);
                }
                collectIndex++;
            }
        }
        return resultList;
    }

    public static BufferedImage subImage(BufferedImage bufferedImage, int x, int y, int width, int height) {
        BufferedImage subImage = bufferedImage.getSubimage(x, y, width, height);
        return subImage;
    }

    public static String writeImage(BufferedImage bufferedImage, String destPath) {
        if (!destPath.endsWith(File.separator)) {
            destPath += File.separator;
        }
        new File(destPath).mkdirs();

        LocalDateTime now = LocalDateTime.now();
        String timeStr = now.format(FORMATTER);
        try {
            String fileName = destPath + "screenshot_" + timeStr + ".png";
            File outputFile = new File(fileName);
            // 保存截图为文件
            ImageIO.write(bufferedImage, "png", outputFile);
            return outputFile.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage screenshotFull() {
        // 捕获屏幕截图
        BufferedImage screenFullImage = ROBOT.createScreenCapture(SCREEN_RECT);
        return screenFullImage;
    }

    /**
     * 模拟鼠标左键单击
     * @param x
     * @param y
     */
    public static void clickLeft(Integer x, Integer y) {
        ROBOT.mouseMove(0, 0);
        if (Objects.isNull(x) || Objects.isNull(y)) {
            System.err.println("x:y=" + x + ":" + y + "| not click");
            return;
        }
        System.out.println("x:y=" + x + ":" + y + "| left click");
        x = (int) (x / WIN_SCREEN_RATE);
        y = (int) (y / WIN_SCREEN_RATE);
        // 移动鼠标
        ROBOT.mouseMove(x, y);
        // 按下左键
        ROBOT.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        // 松开左键
        ROBOT.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }


    public static void clickRight(Integer x, Integer y) {
        // 定位bug, 必须先移动到0, 0
        ROBOT.mouseMove(0, 0);
        if (Objects.isNull(x) || Objects.isNull(y)) {
            System.err.println("x:y=" + x + ":" + y + "| not click");
            return;
        }
        System.out.println("x:y=" + x + ":" + y + "| right click");
        x = (int) (x / WIN_SCREEN_RATE);
        y = (int) (y / WIN_SCREEN_RATE);
        // 移动鼠标
        ROBOT.mouseMove(x, y);
        // 按下左键
        ROBOT.mousePress(InputEvent.BUTTON3_DOWN_MASK);
        // 松开左键
        ROBOT.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
    }

    /**
     * 解析word数据, 模拟鼠标左键单击
     * @param word 获取到word数据
     */
    /*public void clickLeft(Word word) {
        if (Objects.isNull(word)) {
            System.err.println("word is null, not click");
            return;
        }
        Rectangle boundingBox = word.getBoundingBox();
        double x = boundingBox.getX();
        double y = boundingBox.getY();
        double width = boundingBox.getWidth();
        double height = boundingBox.getHeight();
        System.out.println(boundingBox);

        // 移动鼠标, 通过文本宽高计算位置
        int centerX = (int) (x + (width / 2));
        int centerY = (int) (y + (height / 2));
        ROBOT.mouseMove(centerX, centerY);
        // 按下左键
        ROBOT.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        // 松开左键
        ROBOT.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }*/

    static {
        try {
            // 创建 Robot 对象
            ROBOT = new Robot();
            // 获取屏幕尺寸
            SCREEN_RECT = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }
}
