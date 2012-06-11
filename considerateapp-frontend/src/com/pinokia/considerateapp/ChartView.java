package com.pinokia.considerateapp;

import org.achartengine.chart.AbstractChart;
import org.achartengine.chart.TimeChart;
import org.achartengine.chart.PieChart;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ChartView extends View {
    // Abstracted class to create Pie Chart Views and Line Chart Views
    // Pretty self-explanatory code.

    public static final int numDays = 5; // num days to collect info for
    private static final String[] colors = { "#58D9FC", "#EE58FC", "#A15CCC",
        "#E20AFA", "#5CA1CC" };

    private int type;

    enum chartType {
        PIE, LINE, AREA
    }

    private AbstractChart chart;
    private DefaultRenderer renderer;

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ChartView);
        type = a.getInteger(R.styleable.ChartView_type, 0);
        initChartView();
    }

    public void setType(chartType type) {
        switch (type) {
            case PIE:
                this.type = 1;
                break;
            case LINE:
                this.type = 2;
                break;
            case AREA:
                this.type = 3;
        }
        initChartView();
    }

    private void initChartView() {
        if (type == 1) {
            renderer = new DefaultRenderer();
            renderer.setShowLegend(false);
        } else {
            renderer = new XYMultipleSeriesRenderer();
            ((XYMultipleSeriesRenderer) renderer).setXLabels(numDays);
            ((XYMultipleSeriesRenderer) renderer).setXLabelsColor(Color.BLACK);
            ((XYMultipleSeriesRenderer) renderer).setYLabelsColor(0, Color.BLACK);	
        }
        renderer.setLabelsColor(Color.BLACK);
        renderer.setAxesColor(Color.BLACK);
        renderer.setClickEnabled(false);
        renderer.setZoomEnabled(false);
        renderer.setPanEnabled(false);
        int[] margins = { 10, 50, 10, 50 };
        renderer.setMargins(margins);
    }

    private void clearRenderer() {
        for (SimpleSeriesRenderer series : renderer.getSeriesRenderers()) {
            renderer.removeSeriesRenderer(series);
        }
    }

    public void createChart(CategorySeries data) {
        //This should fail if we call it on a Line Chart
        assert (type == 1);
        clearRenderer();
        for (int i = 0; i < data.getItemCount(); i++) {
            SimpleSeriesRenderer series = new SimpleSeriesRenderer();
            series.setColor(Color.parseColor(colors[i]));
            renderer.addSeriesRenderer(series);
        }
        chart = new PieChart(data, renderer);
    }

    public void createChart(XYMultipleSeriesDataset data) {
        //This should fail if called on a Pie Chart.
        assert (type == 2 || type == 3);
        clearRenderer();
        for (int i = 0; i < data.getSeriesCount(); i++) {
            XYSeriesRenderer series = new XYSeriesRenderer();
            series.setLineWidth(2);
            series.setColor(Color.parseColor(colors[i]));
            if (type == 3) {
                series.setFillBelowLine(true);
                series.setFillBelowLineColor(Color.parseColor("#36"
                            + colors[i].substring(1)));
            }
            renderer.addSeriesRenderer(series);
        }
        chart = new TimeChart(data, (XYMultipleSeriesRenderer) renderer);
        ((TimeChart) chart).setDateFormat("MM/dd/yyyy");
    }

    @Override
        public void onDraw(Canvas canvas) {
            if (chart != null) {
                RectF bounds = new RectF(canvas.getClipBounds());
                chart.draw(canvas, 0, 0, (int) canvas.getWidth(),
                        (int) bounds.height(), new Paint());
            }
        }

}
