package jm.bot.steamActivityBot.telegramBot.service;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class PlotCreator {


    public static File createPlotPng(Map<LocalDate,Integer> data) throws IOException {
//        Map<String, Integer> data = new HashMap<>();
//        data.put("A", 5);
//        data.put("B", 3);
//        data.put("C", 7);
//        data.put("D", 1);

        // Создаем dataset для графика
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (LocalDate key : data.keySet()) {
            dataset.addValue(data.get(key), "Count", key);
        }

        // Создаем график
        JFreeChart chart = ChartFactory.createBarChart(
                "Steam Activity",
                "Date",
                "Count Achievements",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);

        // Настраиваем цвета
        chart.setBackgroundPaint(Color.white);
        chart.getCategoryPlot().setBackgroundPaint(Color.lightGray);
        chart.getCategoryPlot().setRangeGridlinePaint(Color.white);

        // Размер графика
        int width = 3840; /* ширина в пикселях */
        int height = 2160; /* высота в пикселях */
        Dimension dim = new Dimension(width, height);
//        chart.draw(, new Rectangle(0,0,1200,600));

        // Сохраняем график в файл
        File file = new File("graph.png");
        ChartUtils.saveChartAsPNG(file, chart, width, height);

        return file;
    }
}
