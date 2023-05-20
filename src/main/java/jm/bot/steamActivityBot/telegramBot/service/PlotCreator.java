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
import java.util.Map;

@Component
public class PlotCreator {


    public static File createPlotPng(Map<LocalDate,Integer> data) throws IOException {

        // create dataset for plot
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (LocalDate key : data.keySet()) {
            dataset.addValue(data.get(key), "Count", key);
        }

        // create plot
        JFreeChart chart = ChartFactory.createBarChart(
                "Steam Activity",
                "Date",
                "Count Achievements",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false);

        // color settings
        chart.setBackgroundPaint(Color.white);
        chart.getCategoryPlot().setBackgroundPaint(Color.lightGray);
        chart.getCategoryPlot().setRangeGridlinePaint(Color.white);

        // plot size
        int width = 3840; // width px
        int height = 2160; // height px

        // save plot in png file
        File file = new File("graph.png");
        ChartUtils.saveChartAsPNG(file, chart, width, height);

        return file;
    }
}
