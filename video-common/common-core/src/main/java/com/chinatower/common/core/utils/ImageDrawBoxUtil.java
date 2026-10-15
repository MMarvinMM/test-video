package com.chinatower.common.core.utils;

import com.chinatower.common.core.dto.AlarmPoint;
import com.chinatower.common.core.dto.BoxInfo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * 图片画框工具类
 * 使用优化的直接像素操作方案
 * 接收告警点列表，按逆时针顺序画框
 *
 * @author chinatower
 */
public class ImageDrawBoxUtil {
    /**
     * 将百分比坐标转换为像素坐标
     * 按照逆时针顺序排序：1(左上) -> 2(左下) -> 3(右下) -> 4(右上)
     *
     * @param alarmPointList 告警点列表
     * @param imageWidth     图片宽度
     * @param imageHeight    图片高度
     * @return 返回一个包含x坐标数组和y坐标数组的对象数组 [xPoints, yPoints]
     */
    private static int[][] convertToPixelCoordinates(List<AlarmPoint> alarmPointList, int imageWidth, int imageHeight) {
        // 按照alarmPointId排序：1, 2, 3, 4
        List<AlarmPoint> sortedPoints = new ArrayList<>(alarmPointList);
        sortedPoints.sort(Comparator.comparingInt(AlarmPoint::getAlarmPointId));

        int[] xPoints = new int[sortedPoints.size()];
        int[] yPoints = new int[sortedPoints.size()];

        for (int i = 0; i < sortedPoints.size(); i++) {
            AlarmPoint point = sortedPoints.get(i);
            xPoints[i] = (int) (point.getPercentX() * imageWidth);
            yPoints[i] = (int) (point.getPercentY() * imageHeight);
        }

        return new int[][]{xPoints, yPoints};
    }

    /**
     * 优化的直接像素操作（批量设置像素）
     * 优点：性能最优，减少方法调用开销
     * 性能：10-30ms (1920x1080)
     *
     * @param picUrl         图片URL
     * @param alarmPointList 告警点列表，按逆时针顺序：1(左上) -> 2(左下) -> 3(右下) -> 4(右上)
     * @param color          框线颜色
     * @param strokeWidth    框线宽度
     * @return 图片字节数组
     * @throws IOException IO异常
     */
    public static byte[] drawBox(String picUrl, List<AlarmPoint> alarmPointList,
                                 Color color, int strokeWidth) throws IOException {
        BufferedImage image = ImageIO.read(new URL(picUrl));
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        BufferedImage copyImage = new BufferedImage(
                imageWidth,
                imageHeight,
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g = copyImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        // 直接操作像素数组，性能最优
        int[] pixels = ((java.awt.image.DataBufferInt) copyImage.getRaster()
                .getDataBuffer()).getData();

        int imgWidth = imageWidth;
        int imgHeight = imageHeight;
        int colorValue = color.getRGB();

        // 转换百分比坐标为像素坐标
        int[][] coords = convertToPixelCoordinates(alarmPointList, imageWidth, imageHeight);
        int[] xPoints = coords[0];
        int[] yPoints = coords[1];
        int pointCount = xPoints.length;

        // 按逆时针顺序连接点，绘制多边形边框（优化版本）
        for (int i = 0; i < pointCount; i++) {
            int x1 = xPoints[i];
            int y1 = yPoints[i];
            int x2 = xPoints[(i + 1) % pointCount];
            int y2 = yPoints[(i + 1) % pointCount];

            // 优化的线段绘制（直接操作像素数组）
            drawLineOptimized(pixels, x1, y1, x2, y2, strokeWidth,
                    colorValue, imgWidth, imgHeight);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(copyImage, "png", baos);
        return baos.toByteArray();
    }

    /**
     * 优化的线段绘制方法（直接操作像素数组）
     *
     * @param pixels      像素数组
     * @param x1          起点X坐标
     * @param y1          起点Y坐标
     * @param x2          终点X坐标
     * @param y2          终点Y坐标
     * @param strokeWidth 线宽
     * @param colorValue  颜色值
     * @param imgWidth    图片宽度
     * @param imgHeight   图片高度
     */
    private static void drawLineOptimized(int[] pixels, int x1, int y1, int x2, int y2,
                                          int strokeWidth, int colorValue, int imgWidth, int imgHeight) {
        // Bresenham算法
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        int x = x1;
        int y = y1;
        int halfWidth = strokeWidth / 2;

        while (true) {
            // 绘制当前点及其周围的像素（实现线宽）
            for (int offsetX = -halfWidth; offsetX <= halfWidth; offsetX++) {
                for (int offsetY = -halfWidth; offsetY <= halfWidth; offsetY++) {
                    int px = x + offsetX;
                    int py = y + offsetY;
                    if (px >= 0 && px < imgWidth && py >= 0 && py < imgHeight) {
                        // 简单的距离判断
                        double distance = Math.sqrt(offsetX * offsetX + offsetY * offsetY);
                        if (distance <= halfWidth) {
                            pixels[py * imgWidth + px] = colorValue;
                        }
                    }
                }
            }

            if (x == x2 && y == y2) {
                break;
            }

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x += sx;
            }
            if (e2 < dx) {
                err += dx;
                y += sy;
            }
        }
    }

    /**
     * 绘制多个框（支持一张图片画多个框，优化像素操作）
     *
     * @param picUrl             图片URL
     * @param boxList            框列表，每个框包含4个点
     * @return 图片字节数组
     * @throws IOException IO异常
     */
    public static byte[] drawMultipleBoxes(String picUrl, List<BoxInfo> boxList) throws IOException {
        return drawMultipleBoxes(picUrl, boxList, Color.red, 3);
    }

    public static byte[] drawMultipleBoxes(String picUrl, List<BoxInfo> boxList,
                                           Color defaultColor, int defaultStrokeWidth) throws IOException {
        BufferedImage image = ImageIO.read(new URL(picUrl));
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        BufferedImage copyImage = new BufferedImage(
                imageWidth,
                imageHeight,
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g = copyImage.createGraphics();
        g.drawImage(image, 0, 0, null);

        // 直接操作像素数组，性能最优
        int[] pixels = ((java.awt.image.DataBufferInt) copyImage.getRaster()
                .getDataBuffer()).getData();

        int imgWidth = imageWidth;
        int imgHeight = imageHeight;

        // 绘制每个框/绊线
        for (BoxInfo boxInfo : boxList) {
            if (BoxInfo.DRAW_TYPE_TRIP_THREAD.equals(boxInfo.getDrawType())) {
                drawTripThread(g, boxInfo, imageWidth, imageHeight, defaultColor, defaultStrokeWidth);
                continue;
            }

            Color boxColor = boxInfo.getColor() != null ? boxInfo.getColor() : defaultColor;
            int boxStrokeWidth = boxInfo.getStrokeWidth() > 0 ? boxInfo.getStrokeWidth() : defaultStrokeWidth;
            int colorValue = boxColor.getRGB();

            if (boxInfo.getAlarmPointList() == null || boxInfo.getAlarmPointList().isEmpty()) {
                continue;
            }

            // 转换百分比坐标为像素坐标
            int[][] coords = convertToPixelCoordinates(boxInfo.getAlarmPointList(), imageWidth, imageHeight);
            int[] xPoints = coords[0];
            int[] yPoints = coords[1];
            int pointCount = xPoints.length;

            // 按逆时针顺序连接点，绘制多边形边框（优化版本）
            for (int i = 0; i < pointCount; i++) {
                int x1 = xPoints[i];
                int y1 = yPoints[i];
                int x2 = xPoints[(i + 1) % pointCount];
                int y2 = yPoints[(i + 1) % pointCount];

                // 优化的线段绘制（直接操作像素数组）
                drawLineOptimized(pixels, x1, y1, x2, y2, boxStrokeWidth,
                        colorValue, imgWidth, imgHeight);
            }

            // 在框上方绘制文字信息（默认在框左上角上方，若空间不足则回退到框内）
            if (boxInfo.getLabel() != null && !boxInfo.getLabel().isEmpty()) {
                int minX = Arrays.stream(xPoints).min().orElse(0);
                int minY = Arrays.stream(yPoints).min().orElse(0);
                g.setColor(boxColor);
                Font font = new Font("SansSerif", Font.BOLD, 16);
                g.setFont(font);

                FontMetrics fontMetrics = g.getFontMetrics(font);
                int textHeight = fontMetrics.getHeight();

                // x 轻微右移避免贴边；y 优先放到框上方，放不下则回退到框内顶部
                int textX = Math.max(minX + 2, 0);
                int preferTextY = minY - 4; // 文本基线在框上方一点
                int textY = preferTextY - textHeight >= 0 ? preferTextY : Math.max(minY + textHeight, textHeight);

                g.drawString(boxInfo.getLabel(), textX, textY);
            }
        }

        g.dispose();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(copyImage, "png", baos);
        return baos.toByteArray();
    }

    /**
     * 绘制绊线：threadLine 为绊线，direction 为方向箭头
     */
    private static void drawTripThread(Graphics2D g, BoxInfo boxInfo, int imageWidth, int imageHeight,
                                       Color defaultColor, int defaultStrokeWidth) {
        Color color = boxInfo.getColor() != null ? boxInfo.getColor() : defaultColor;
        int strokeWidth = boxInfo.getStrokeWidth() > 0 ? boxInfo.getStrokeWidth() : defaultStrokeWidth;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(color);
        g.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // 绘制绊线（threadLine 两个端点连线）
        List<AlarmPoint> threadLine = boxInfo.getAlarmPointList();
        if (threadLine != null && threadLine.size() >= 2) {
            List<AlarmPoint> sortedLine = new ArrayList<>(threadLine);
            sortedLine.sort(Comparator.comparingInt(AlarmPoint::getAlarmPointId));
            int x1 = (int) (sortedLine.get(0).getPercentX() * imageWidth);
            int y1 = (int) (sortedLine.get(0).getPercentY() * imageHeight);
            int x2 = (int) (sortedLine.get(1).getPercentX() * imageWidth);
            int y2 = (int) (sortedLine.get(1).getPercentY() * imageHeight);
            g.drawLine(x1, y1, x2, y2);
        }

        // 绘制方向箭头（direction 两个端点连线 + 箭头三角）
        List<AlarmPoint> direction = boxInfo.getDirectionPointList();
        if (direction == null || direction.size() < 2) {
            return;
        }
        List<AlarmPoint> sortedDirection = new ArrayList<>(direction);
        sortedDirection.sort(Comparator.comparingInt(AlarmPoint::getAlarmPointId));
        int[] px = new int[sortedDirection.size()];
        int[] py = new int[sortedDirection.size()];
        for (int i = 0; i < sortedDirection.size(); i++) {
            px[i] = (int) (sortedDirection.get(i).getPercentX() * imageWidth);
            py[i] = (int) (sortedDirection.get(i).getPercentY() * imageHeight);
        }
        g.drawPolyline(px, py, px.length);

        int arrowSize = Math.max(strokeWidth * 4, 12);
        int tailX = px[px.length - 2];
        int tailY = py[py.length - 2];
        int tipX = px[px.length - 1];
        int tipY = py[px.length - 1];
        double dx = tailX - tipX;
        double dy = tailY - tipY;
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length <= 0) {
            return;
        }
        double angle = Math.atan2(tipY - tailY, tipX - tailX);
        int wingX1 = (int) (tipX - arrowSize * Math.cos(angle - Math.PI / 6));
        int wingY1 = (int) (tipY - arrowSize * Math.sin(angle - Math.PI / 6));
        int wingX2 = (int) (tipX - arrowSize * Math.cos(angle + Math.PI / 6));
        int wingY2 = (int) (tipY - arrowSize * Math.sin(angle + Math.PI / 6));
        g.fillPolygon(new int[]{tipX, wingX1, wingX2}, new int[]{tipY, wingY1, wingY2}, 3);
    }
}

