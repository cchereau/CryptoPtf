package gui.commonComponent.JPanelGraphique;

import org.jetbrains.annotations.NotNull;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

abstract class JAbstractGraph extends JPanel {
    private final ChartPanel chartPanel;
    private final TimeSeriesCollection timeSeriesCollection;
    private JFreeChart chart;

    public JAbstractGraph() {
        super(new BorderLayout());
        timeSeriesCollection = new TimeSeriesCollection();
        chartPanel = new ChartPanel(chart);
        this.add(chartPanel);

    }

    protected ChartPanel getChartPanel() {
        return chartPanel;
    }


    public abstract void drawSeries(LocalDate datDeb, LocalDate datFin);


    public void addSeries(String name, TreeMap<LocalDate, Double> data) {
        TimeSeries series = new TimeSeries(name);
        for (Map.Entry<LocalDate, Double> entry : data.entrySet()) {
            Day day = new Day(Date.from(entry.getKey().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            series.add(day, entry.getValue());
        }
        timeSeriesCollection.addSeries(series);
    }

    public void removeSerie(@NotNull Boolean all, String name) {
        if (all) timeSeriesCollection.removeAllSeries();
        else timeSeriesCollection.removeSeries(timeSeriesCollection.getSeries(name));
    }


    protected TimeSeriesCollection getDataSet() {
        return timeSeriesCollection;
    }
}
