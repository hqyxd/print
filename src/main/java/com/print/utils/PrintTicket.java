package com.print.utils;

import com.alibaba.fastjson.JSON;
import com.print.utils.analysis.PrintBook;
import com.print.utils.analysis.PrintObject;
import com.print.utils.goods.ShoppingOrderDetailEntity;
import com.print.utils.utils.PropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.PrinterName;
import java.awt.*;
import java.awt.print.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 销售票据实体类
 *
 * @author hanqy
 * @version 2018年4月24日
 * @see PrintTicket
 * @since
 */
@Slf4j
public class PrintTicket implements Printable {

    private Map<String, Object> dataMap = new LinkedHashMap<String, Object>();

    private PrintBook printBook;

    public PrintTicket() {

    }

    public PrintTicket(PrintBook printBook, Map<String, Object> dataMap) {
        this.dataMap = dataMap;
        this.printBook = printBook;
    }

    private static final Pattern pattern = Pattern.compile("\\{(.*?)\\}");
    private static Matcher matcher;

    private Matcher matcher(String tagerStr) {
        matcher = pattern.matcher(tagerStr);
        while (matcher.find()) {
            return matcher;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        // 此 Graphics2D 类扩展 Graphics 类，以提供对几何形状、坐标转换、颜色管理和文本布局更为复杂的控制。
        // 它是用于在 Java(tm) 平台上呈现二维形状、文本和图像的基础类。
        Graphics2D g2 = (Graphics2D) graphics;
        g2.setColor(Color.black);// 设置打印颜色为黑色

        // 打印起点坐标
        double x = pageFormat.getImageableX(); // 返回与此 PageFormat相关的 Paper对象的可成像区域左上方点的 x坐标。
        double y = pageFormat.getImageableY(); // 返回与此 PageFormat相关的 Paper对象的可成像区域左上方点的 y坐标。

        List<PrintObject> headerList = printBook.getHeader();
        float line = 0f; // 行高
        line = commonPrint(headerList, g2, x, y, line, true, false); // 解析打印header

        line += (g2.getFont().getSize() + 3);
        List<PrintObject> goodsList = printBook.getGooods();
        line = commonPrint(goodsList, g2, x, y, line, false, false); // 解析商品头
        line += 3;
        // 虚线设置
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 4.0f, new float[]{4.0f}, 0.0f));
        // 在此图形上下文的坐标系中使用当前颜色在点 (x1, y1) 和 (x2, y2) 之间画一条线。 即绘制虚线
        g2.drawLine((int) x, (int) (y + line), (int) x + 158, (int) (y + line));
        float heigth = g2.getFont().getSize2D();// 获取字体的高度
        line += heigth + 3;
        int i = 0;
        List<ShoppingOrderDetailEntity> goodsInfoList = JSON.parseArray(dataMap.get("details").toString(), ShoppingOrderDetailEntity.class);
//        List<ShoppingOrderDetailEntity> goodsInfoList = (List<ShoppingOrderDetailEntity>) dataMap.get("details");
        if (goodsInfoList != null && goodsInfoList.size() > 0) {
            for (ShoppingOrderDetailEntity goodInfo : goodsInfoList) {
                i++;
                if (i >= 1) {
                    line += 3;
                }
                if (i == goodsInfoList.size() - 1) {
                    line -= 3;
                }
                // 商品文字超过五个字换行处理
                if (goodInfo.getName().length() > 5) {
                    int lineHeigt = drawStringWithFontStyleLineFeed(g2, goodInfo.getName(), (float) x, (float) y + line);
//                    g2.drawString(goodInfo.getName(), (float) x + 15, (float) y + line);
                    g2.drawString(goodInfo.getPrice().toString(), (float) x + 60, (float) y + line);
                    g2.drawString(goodInfo.getNum().toString(), (float) x + 95, (float) y + line);
                    g2.drawString(goodInfo.getSum().toString(), (float) x + 120, (float) y + line);
                    line += heigth + 3 + lineHeigt;
                } else {
                    g2.drawString(goodInfo.getName(), (float) x + 15, (float) y + line);
                    g2.drawString(goodInfo.getPrice().toString(), (float) x + 60, (float) y + line);
                    g2.drawString(goodInfo.getNum().toString(), (float) x + 95, (float) y + line);
                    g2.drawString(goodInfo.getSum().toString(), (float) x + 120, (float) y + line);
                    line += heigth + 3;
                }

//				g2.drawString(goodInfo.getName(), (float) x + 15, (float) y + line);

            }
        }
        // 结束虚线
        g2.drawLine((int) x, (int) (y + line), (int) x + 158, (int) (y + line));
        line += (g2.getFont().getSize() + 3);

        List<PrintObject> footList = printBook.getFooter();
        line = commonPrint(footList, g2, x, y, line, false, true); // 解析打印footer
        line += 3;

        switch (pageIndex) {
            case 0:
                return PAGE_EXISTS;
            default:
                return NO_SUCH_PAGE;
        }
    }

    /**
     * 公共解析
     *
     * @param printObjectList
     * @param g2
     * @param x
     * @param y
     * @return
     * @see
     */
    public float commonPrint(List<PrintObject> printObjectList, Graphics2D g2, double x, double y, float line, boolean header, boolean footer) {
        int index = 0;
        for (PrintObject print : printObjectList) {
            Font font = null; // 创建字体
            if (print != null && print.getBold()) { // 是否加粗字体
                font = new Font(print.getFont(), Font.BOLD, print.getSize());
            } else {
                font = new Font(print.getFont(), Font.PLAIN, print.getSize());
            }
            g2.setFont(font);// 设置打印字体

            float heigth = font.getSize2D();

            if (header && index == 0) {
                line += g2.getFont().getSize();
            }

            Matcher matcher = matcher(print.getText()); // 匹配字符
            String text = "";
            if (matcher == null) {
                text = print.getText();
            } else {
                String matherKey = matcher.group(0);
                String mapKey = matcher.group(1);
                text = print.getText().replace(matherKey, String.valueOf(dataMap.get(mapKey)));
            }

            g2.drawString(text, (float) x + new Float(print.getX()), (float) y + (StringUtils.isNotBlank(print.getY()) ? new Float(print.getY()) : line));

            if (index == printObjectList.size() - 1) {
                break;
            }
            if (print.getLine() != null && print.getLine()) {
                line += (heigth + (print.getLinewidth() != null ? print.getLinewidth() : 3)); // 下一行开始打印的高度
            }

            index++;
        }
        return line;
    }

    public void printer() {
        try {
            // Book 类提供文档的表示形式，该文档的页面可以使用不同的页面格式和页面 painter
            Book book = new Book(); // 要打印的文档

            // PageFormat类描述要打印的页面大小和方向
            PageFormat pf = new PageFormat(); // 初始化一个页面打印对象
            pf.setOrientation(PageFormat.PORTRAIT); // 设置页面打印方向，从上往下，从左往右

            // 设置打印纸页面信息。通过Paper设置页面的空白边距和可打印区域。必须与实际打印纸张大小相符。
            Paper paper = new Paper();
            double width = new Double(PropertiesUtils.getString("paper.width"));
            double height = new Double(PropertiesUtils.getString("paper.height"));
            paper.setSize(width, height); // A4纸张大小
            paper.setImageableArea(10, 10, width - 10, height - 10); // 打印区域
            pf.setPaper(paper);

            book.append(this, pf);

            PrinterJob job = PrinterJob.getPrinterJob(); // 获取打印服务对象

            job.setPageable(book); // 设置打印类
            //  获取可用的打印机服务
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            HashAttributeSet hs = new HashAttributeSet();
            hs.add(new PrinterName("GP-L80160 Series", null));

            PrintService[] pss = PrintServiceLookup.lookupPrintServices(null, hs);
            job.setPrintService(pss[0]);
            job.print();
//            int isCommit = Integer.parseInt(PropertiesUtils.getString("print.commit"));
//            if (isCommit == 1) {
//                job.print();
//            } else {
//                // 可以用printDialog显示打印对话框,在用户确认后打印
//                boolean a = job.printDialog();
//                if (a) {
//                    job.print();
//                } else {
//                    job.cancel();
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getStringHeight(Graphics g) {
        int height = g.getFontMetrics().getHeight();
        log.info("字符高度:" + height);
        return height;
    }

    private int getRows(int strWidth, int rowWidth) {
        int rows = 0;
        if (strWidth % rowWidth > 0) {
            rows = strWidth / rowWidth + 1;
        } else {
            rows = strWidth / rowWidth;
        }
        log.info("行数:" + rows);
        return rows;
    }

    private int getStringLength(Graphics g, String str) {
        char[] strcha = str.toCharArray();
        int strWidth = g.getFontMetrics().charsWidth(strcha, 0, str.length());
        log.info("字符总宽度:" + strWidth);
        return strWidth;
    }

    private int getRowStrNum(int strnum, int rowWidth, int strWidth) {
        int rowstrnum = 0;
        rowstrnum = (rowWidth * strnum) / strWidth;
        log.info("每行的字符数:" + rowstrnum);
        return rowstrnum;
    }

    private int drawStringWithFontStyleLineFeed(Graphics2D g, String strContent, float loc_X, float loc_Y) {
        //获取字符串 字符的总宽度
        int strWidth = getStringLength(g, strContent);
        //每一行字符串宽度
        int rowWidth = 60;
        log.info("每行字符宽度:" + rowWidth);
        //获取字符高度
        int strHeight = getStringHeight(g);
        //字符串总个数
        log.info("字符串总个数:" + strContent.length());
        int lineHeigt = 0;
        if (strWidth > rowWidth) {
            int rowstrnum = getRowStrNum(strContent.length(), rowWidth, strWidth);
            int rows = getRows(strWidth, rowWidth);
            String temp = "";
            for (int i = 0; i < rows; i++) {
                //获取各行的String
                if (i == rows - 1) {
                    //最后一行
                    temp = strContent.substring(i * rowstrnum, strContent.length());
                } else {
                    temp = strContent.substring(i * rowstrnum, i * rowstrnum + rowstrnum);
                }
                if (i > 0) {
                    //第一行不需要增加字符高度，以后的每一行在换行的时候都需要增加字符高度
                    loc_Y = loc_Y + strHeight;
                }
                lineHeigt += 8;
                g.drawString(temp, loc_X, loc_Y);

            }
        } else {
            //直接绘制
            g.drawString(strContent, loc_X, loc_Y);
        }
        return lineHeigt;
    }
}
