package gui.commonComponent.JPanelGraphique;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Classe en charge d'afficher des series de données
// dans un JfreeChart
//  https://stackoverflow.com/questions/70461035/jfreechart-control-ohlc-time-series-range-for-dynamic-display
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import global.EnumCrypto.enTypeQuotation;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleInsets;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.DefaultHighLowDataset;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

public class JGraphOHCL extends JAbstractGraph {

    public JGraphOHCL() {
        super();
    }

    @Override
    public void drawSeries(LocalDate datDeb, LocalDate datFin) {
        JFreeChart chart;
        ZoneId defaultZoneId = ZoneId.systemDefault();
        DefaultHighLowDataset dataSet = null;
        try {

            HLCOdata hlcOdata = new HLCOdata(super.getDataSet());
            chart = ChartFactory.createCandlestickChart("Performance", "Days", "Price", hlcOdata.getDataSet(datDeb, datFin), false);

            XYPlot plot = (XYPlot) chart.getPlot();
            //plot.setRenderer(renderer);
            plot.setBackgroundPaint(Color.lightGray);
            plot.setDomainGridlinePaint(Color.white);
            plot.setRangeGridlinePaint(Color.white);
            plot.setAxisOffset(new RectangleInsets(2.0, 2.0, 2.0, 2.0));
            plot.setDomainCrosshairVisible(true);
            plot.setRangeCrosshairVisible(true);

            // range des dates deb & fin
            DateAxis axis = (DateAxis) plot.getDomainAxis();
            Date dateDeb = Date.from(datDeb.atStartOfDay(defaultZoneId).toInstant());
            Date dateFin = Date.from(datFin.atStartOfDay(defaultZoneId).toInstant());
            axis.setRange(dateDeb, dateFin);
            axis.setDateFormatOverride(new SimpleDateFormat("dd/MM/yyyy"));
            // range en y récpération des valeurs minimum et maximale sur la pèriode
            plot.getRangeAxis().setRange(hlcOdata.minLow * 0.95, hlcOdata.maxHigh * 1.05);

            chart.setTitle(new TextTitle("Evolution ", new Font("Serif", Font.BOLD, 18)));
            chart.fireChartChanged();
            super.getChartPanel().setChart(chart);
        } catch (CloneNotSupportedException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


    private static class HLCOdata {

        private static final String dataKey = "data";
        TimeSeriesCollection timeSeriesCollection;
        private double minLow;
        private double maxHigh;

        public HLCOdata(TimeSeriesCollection timeSeriesCollection) {
            minLow = 0d;
            maxHigh = 0d;
            this.timeSeriesCollection = timeSeriesCollection;
        }

        public DefaultHighLowDataset getDataSet(LocalDate datDeb, LocalDate datFin) throws CloneNotSupportedException {

            TimeSeries timeSeries;
            Collection<RegularTimePeriod> regularTimePeriods;
            Collection<TimeSeriesDataItem> doubles;

            timeSeries = timeSeriesCollection.getSeries(0);
            regularTimePeriods = timeSeries.getTimePeriods();
            Date[] date = regularTimePeriods.stream().map(e -> Date.from(e.getStart().toInstant())).collect(Collectors.toList()).toArray(new Date[0]);
            // récupération des données entre les index Deb et Fin
            int indexDatDeb = Arrays.asList(date).indexOf(Date.from(datDeb.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            int indexDatFin = Arrays.asList(date).indexOf(Date.from(datFin.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            // DATE
            timeSeries = timeSeriesCollection.getSeries(0).createCopy(indexDatDeb, indexDatFin);
            regularTimePeriods = timeSeries.getTimePeriods();
            date = regularTimePeriods.stream().map(e -> Date.from(e.getStart().toInstant())).collect(Collectors.toList()).toArray(new Date[0]);

            //  HIGH
            timeSeries = timeSeriesCollection.getSeries(enTypeQuotation.high.toString()).createCopy(indexDatDeb, indexDatFin);
            doubles = timeSeries.getItems();
            double[] high = doubles.stream().mapToDouble(e -> e.getValue().doubleValue()).toArray();

            //  LOW
            timeSeries = timeSeriesCollection.getSeries(enTypeQuotation.low.toString()).createCopy(indexDatDeb, indexDatFin);
            doubles = timeSeries.getItems();
            double[] low = doubles.stream().mapToDouble(e -> e.getValue().doubleValue()).toArray();

            //  CLOSE
            timeSeries = timeSeriesCollection.getSeries(enTypeQuotation.close.toString()).createCopy(indexDatDeb, indexDatFin);
            doubles = timeSeries.getItems();
            double[] close = doubles.stream().mapToDouble(e -> e.getValue().doubleValue()).toArray();

            //  OPEN
            timeSeries = timeSeriesCollection.getSeries(enTypeQuotation.open.toString()).createCopy(indexDatDeb, indexDatFin);
            doubles = timeSeries.getItems();
            double[] open = doubles.stream().mapToDouble(e -> e.getValue().doubleValue()).toArray();

            //  VOLUME
            timeSeries = timeSeriesCollection.getSeries(enTypeQuotation.volume.toString()).createCopy(indexDatDeb, indexDatFin);
            doubles = timeSeries.getItems();
            double[] volume = doubles.stream().mapToDouble(e -> e.getValue().doubleValue()).toArray();

            // récupération des valeurs min et max
            minLow = Arrays.stream(low).min().getAsDouble();
            maxHigh = Arrays.stream(high).max().getAsDouble();

            return new DefaultHighLowDataset(dataKey, date, high, low, open, close, volume);

        }
    }


}
