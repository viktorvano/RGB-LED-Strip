package com.viktor.vano.led.strip.scheduler.Classes;

import com.sun.istack.internal.NotNull;
import javafx.scene.chart.XYChart;

import java.util.LinkedList;

public class GradientListRGB {
    LinkedList<Integer> red, green, blue, time;
    LinkedList<Double> redGradient, greenGradient, blueGradient;
    private XYChart.Series<Number, Number> redSeries, greenSeries, blueSeries;
    public GradientListRGB(@NotNull XYChart.Series red, @NotNull XYChart.Series green, @NotNull XYChart.Series blue)
    {
        this.redSeries = red;
        this.greenSeries = green;
        this.blueSeries = blue;
        sortData();
        calculateGradients();
    }

    private void sortData()
    {
        red = new LinkedList<>();
        green = new LinkedList<>();
        blue = new LinkedList<>();
        time = new LinkedList<>();
        for(int i=0; i<redSeries.getData().size(); i++)
        {
            if(i==0)
            {
                time.add(redSeries.getData().get(i).getXValue().intValue());
                red.add(redSeries.getData().get(i).getYValue().intValue());
                green.add(greenSeries.getData().get(i).getYValue().intValue());
                blue.add(blueSeries.getData().get(i).getYValue().intValue());
            }else
            {
                for(int a=0; a<time.size(); a++)
                {
                    if(a+1 < time.size())
                    {
                        if(redSeries.getData().get(i).getXValue().intValue() > time.get(a)
                                && redSeries.getData().get(i).getXValue().intValue() < time.get(a+1))
                        {
                            time.add(a+1, redSeries.getData().get(i).getXValue().intValue());
                            red.add(a+1, redSeries.getData().get(i).getYValue().intValue());
                            green.add(a+1, greenSeries.getData().get(i).getYValue().intValue());
                            blue.add(a+1, blueSeries.getData().get(i).getYValue().intValue());
                            break;
                        }
                    }else
                    {
                        if(redSeries.getData().get(i).getXValue().intValue() > time.get(a))
                        {
                            time.add(a+1, redSeries.getData().get(i).getXValue().intValue());
                            red.add(a+1, redSeries.getData().get(i).getYValue().intValue());
                            green.add(a+1, greenSeries.getData().get(i).getYValue().intValue());
                            blue.add(a+1, blueSeries.getData().get(i).getYValue().intValue());
                            break;
                        }
                    }
                }
            }
        }
    }

    private void calculateGradients()
    {
        redGradient = new LinkedList<>();
        greenGradient = new LinkedList<>();
        blueGradient = new LinkedList<>();
        for(int i = 0; i < time.size()-1; i++)
        {
            redGradient.add((double)(red.get(i+1) - red.get(i)) / (double)(time.get(i+1) - time.get(i)));
            greenGradient.add((double)(green.get(i+1) - green.get(i)) / (double)(time.get(i+1) - time.get(i)));
            blueGradient.add((double)(blue.get(i+1) - blue.get(i)) / (double)(time.get(i+1) - time.get(i)));
        }
    }

    public int getRed(int timeInSeconds)
    {
        int gradientIndex = 0;
        for (int i=0; i<time.size() - 1; i++)
        {
            if(timeInSeconds > time.get(i) && timeInSeconds <= time.get(i+1))
            {
                gradientIndex = i;
                break;
            }
        }

        double color = ((double) (timeInSeconds - time.get(gradientIndex))) * redGradient.get(gradientIndex)
                + red.get(gradientIndex);

        return (int)color;
    }

    public int getGreen(int timeInSeconds)
    {
        int gradientIndex = 0;
        for (int i=0; i<time.size() - 1; i++)
        {
            if(timeInSeconds > time.get(i) && timeInSeconds <= time.get(i+1))
            {
                gradientIndex = i;
                break;
            }
        }

        double color = ((double) (timeInSeconds - time.get(gradientIndex))) * greenGradient.get(gradientIndex)
                + green.get(gradientIndex);

        return (int)color;
    }

    public int getBlue(int timeInSeconds)
    {
        int gradientIndex = 0;
        for (int i=0; i<time.size() - 1; i++)
        {
            if(timeInSeconds > time.get(i) && timeInSeconds <= time.get(i+1))
            {
                gradientIndex = i;
                break;
            }
        }

        double color = ((double) (timeInSeconds - time.get(gradientIndex))) * blueGradient.get(gradientIndex)
                + blue.get(gradientIndex);

        return (int)color;
    }
}
