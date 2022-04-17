package gui.commonComponent.JPanelGraphique;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Classe en charge d'afficher des series de données
// dans un JfreeChart
//  https://stackoverflow.com/questions/70461035/jfreechart-control-ohlc-time-series-range-for-dynamic-display
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.chart.ui.RectangleInsets;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class JGraphChart extends JAbstractGraph {
    public JGraphChart() {
        super();
    }

    @Override
    public void drawSeries(LocalDate datDeb, LocalDate datFin) {
        JFreeChart chart;

        chart = ChartFactory.createTimeSeriesChart(
                "Evolution de la performance",
                "Jour",
                "Value (€)",
                super.getDataSet(),
                true,
                true,
                false
        );

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(1.0f));
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesStroke(1, new BasicStroke(1.0f));
        renderer.setSeriesPaint(1, Color.CYAN);
        renderer.setSeriesStroke(1, new BasicStroke(1.0f));

        chart.setBackgroundPaint(Color.white);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(2.0, 2.0, 2.0, 2.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        // range des dates deb & fin
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        ZoneId defaultZoneId = ZoneId.systemDefault();
        Date dateDeb = Date.from(datDeb.atStartOfDay(defaultZoneId).toInstant());
        Date dateFin = Date.from(datFin.atStartOfDay(defaultZoneId).toInstant());
        axis.setRange(dateDeb, dateFin);
        axis.setDateFormatOverride(new SimpleDateFormat("dd/MM/yyyy"));
        // range en y
        plot.getRangeAxis().setAutoRange(true);

        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.setTitle(new TextTitle("Evolution ", new Font("Serif", Font.BOLD, 18)));
        chart.fireChartChanged();

        super.getChartPanel().setChart(chart);

    }


}
