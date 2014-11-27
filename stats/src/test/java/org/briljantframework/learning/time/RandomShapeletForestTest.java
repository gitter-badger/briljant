package org.briljantframework.learning.time;

import org.briljantframework.chart.Chartable;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.MixedDataFrame;
import org.briljantframework.io.CSVInputStream;
import org.briljantframework.matrix.DenseMatrix;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.MatrixLike;
import org.briljantframework.vector.Vector;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.xy.*;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.jfree.util.ShapeUtilities;
import org.junit.Test;

import java.awt.*;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RandomShapeletForestTest {
    @Test
    public void testName() throws Exception {
        //        try (CSVInputStream in = new CSVInputStream(new FileInputStream("/Users/isak/Projects/adeb/erlang/adeb-rr/deps/rr/data/iris.txt"))) {
        //            DenseDataFrame dataset = in.read(DenseDataFrame.copyTo());
        //            SupervisedDataset<DenseDataFrame, CategoricColumn> supervisedDataset = SupervisedDataset.createClassificationInput(dataset,
        //                    DenseDataFrame.copyTo(), DefaultCategoricColumn.copyTo());
        //
        //            ClassificationTree.Builder tree = ClassificationTree.withSplitter(RandomSplitter.withMaximumFeatures(2));
        //            Ensemble<Row, DataFrame<? extends Row>, CategoricColumn> ensemble = Ensemble.withMember(tree).withSize(100).withSampler(Bootstrap.create()).create();
        //
        //
        //            Result result = Evaluators.crossValidation(ensemble, supervisedDataset, 10);
        //            System.out.println(result);
        //
        //
        //        }
        //
    }

    //    @Test
    //    public void testFit() throws Exception {
    //        String name = "ECGFiveDays";
    //        String datasetPath = String.format("/Users/isak/Downloads/dataset2/%s/%s_", name, name);
    //
    //        Frame trainFrame = new DataSeriesInputStream(new FileInputStream(datasetPath + "TRAIN")).read(Frame
    //                .getFactory());
    //        Container<Frame, CategoricColumn> train = Container.extractTarget(trainFrame, 0, CategoricHeader::target,
    //                Frame.getFactory(), DenseCategoricColumn.getFactory());
    //
    //
    //        Frame testFrame = Datasets.randomize(
    //                new DataSeriesInputStream(new FileInputStream(datasetPath + "TEST")).read(Frame.getFactory())
    //        );
    //        Container<Frame, CategoricColumn> test = Container.extractTarget(testFrame, 0, CategoricHeader::target,
    //                Frame.getFactory(), DenseCategoricColumn.getFactory());
    //
    //        RandomShapeletForest.Builder forestBuilder = RandomShapeletForest
    //                .withSize(100)
    //                .withLowerLength(2)
    //                .withUpperLength(-1)
    //                .withInspectedShapelets(100)
    //                .withSampler(Bootstrap.create());
    //        RandomShapeletForest forest = forestBuilder.create();
    //        RandomShapeletForest.Model model = forest.fit(train.getDataset(), train.getTarget());
    //        HoldOutValidation<Frame, Column> validation = new HoldOutValidation<>(test.getDataset(), test.getTarget());
    //        Result result = validation.evaluate(model, train.getDataset(), train.getTarget());
    //        System.out.println(result);
    //
    //        Matrix x = Matrices.linspace(test.getDataset().columns() - 1, test.getDataset().columns(), 0);
    //        int width = 250;
    //        int height = 200;
    //
    //        Chartable.saveSVG(String.format("/Users/isak/Desktop/%s_timeSeries.svg", name), plotRows(x,
    //                trainFrame.getMatrix(),
    //                train.getTarget()), width, height);
    //
    //        long start = System.currentTimeMillis();
    //        System.out.println(test.getDataset().getShape());
    //        System.out.println(System.currentTimeMillis() - start);
    //        System.out.println(model.getLengthImportance());
    //        System.out.println(model.getPositionImportance());
    //
    //        JFreeChart lengthImportance = bar(x, "Shapelet length", model.getLengthImportance(),
    //                "Mean Decrease Impurity");
    //        JFreeChart positionImportance = area(x, "Position", model.getPositionImportance(), "Mean Decrease Impurity");
    //        Chartable.saveSVG(String.format("/Users/isak/Desktop/%s_lengthImportance.svg", name), lengthImportance, width,
    //                height);
    //        Chartable.saveSVG(String.format("/Users/isak/Desktop/%s_positionImportance.svg", name), positionImportance,
    //                width, height);
    ////
    ////
    //
    //
    //        System.exit(1);
    //
    //
    //        KNearestNeighbors knn = KNearestNeighbors
    //                .withNeighbors(1)
    //                .withDistance(DynamicTimeWarping.withDistance(Distance.EUCLIDEAN).withConstraint(-1).create())
    //                .create();
    //
    //        System.out.println(
    //                HoldOutValidation.withHoldout(test.getDataset(), test.getTarget())
    //                        .evaluate(knn, train.getDataset(), train.getTarget())
    //        );
    //
    //        Frame all = Datasets.randomize(
    //                Datasets.stack(Arrays.asList(trainFrame, testFrame))
    //        );
    //
    //        Container<Frame, CategoricColumn> cAll = Container.extractTarget(all, 0, CategoricHeader::target,
    //                Frame.getFactory(), DenseCategoricColumn.getFactory());
    //
    //        System.out.println(
    //                CrossValidation.withFolds(10, Frame.getFactory(), DenseCategoricColumn.getFactory())
    //                        .evaluate(knn, cAll.getDataset(), cAll.getTarget())
    //        );
    //
    ////        Result result = Evaluators.crossValidation(forest, test.getDataset(), test.getTarget(),
    ////                10, Frame.FACTORY, DefaultTarget.FACTORY);
    ////
    ////        long start = System.currentTimeMillis();
    ////        Result result = Evaluators.crossValidation(forest, train.getDataset(), train.getTarget(), 10,
    ////                Frame.FACTORY, DefaultTarget.FACTORY);
    //
    ////        Result result = Evaluators.holdOutValidation(forest, train.getDataset(), train.getTarget(),
    ////                test.getDataset(), test.getTarget());
    //
    ////        Chartable.saveSVG("/Users/isak/Desktop/test.svg", result.getAverageConfusionMatrix().getChart());
    ////
    ////        System.out.println(System.currentTimeMillis() - start);
    //
    //
    ////        KNearestNeighbors knn = KNearestNeighbors.withNeighbors(1).withDistance(DynamicTimeWarping.withConstraint
    // (1))
    ////                .create();
    //
    ////        Configurations<RandomShapeletForest> f = Tuners.split(forestBuilder, train, 0.5,
    ////                Updaters.range("Inspected Shapelets", RandomShapeletForest.Builder::setInspectedShapelets, 10, 100,
    //// 10),
    ////                Updaters.range("Upper Length", RandomShapeletForest.Builder::setUpperLength, 4, 20, 2)
    ////        );
    ////        System.out.println(f);
    //
    ////        Result result = Evaluators.crossValidation(knn, Storages.stack(train, test).permute(), 10);
    ////        System.out.println(result);
    ////        for (ConfusionMatrix confusionMatrix : result.getConfusionMatrices()) {
    ////            System.out.println(confusionMatrix);
    ////            System.out.println();
    ////        }
    //
    ////        System.out.println(new CrossValidationEvaluator<Frame, Target>().evaluate(forest, test));
    ////
    ////        long start = System.currentTimeMillis();
    ////        Model<Frame> model2 = forest.fit(train);
    ////        System.out.printf("Training on %s_TRAIN with %d rows and %d columns %n", name, train.rows(),
    // train.columns());
    ////        System.out.println("Fit: " + ((System.currentTimeMillis() - start) / 1000) + " seconds");
    ////
    ////        start = System.currentTimeMillis();
    ////        Predictions predictions = model2.predict(test.getDataset());
    ////        System.out.printf("Testing on %s_TEST with %d rows and %d columns %n", name, test.rows(), test.columns());
    ////
    ////        System.out.println("Predict: " + ((System.currentTimeMillis() - start) / 1000) + " seconds");
    ////        Target testTarget = test.getTarget();
    ////        int correct = 0;
    ////        for (int i = 0; i < predictions.size(); i++) {
    ////            Prediction p = predictions.get(i);
    ////            if (p.getValue().equals(testTarget.getValue(i))) {
    ////                correct += 1;
    ////            }
    ////        }
    ////        System.out.println(Evaluators.holdOutValidation(forest, train, test));
    //
    ////
    ////        System.out.println(Metrics.calculateError(predictions, test.getTarget()));
    ////        System.out.printf("Error: %.5f (i.e. %.5f)", (1 - ((double) correct) / test.rows()),
    //// ((double) correct) / test.rows());
    //
    //    }

    @Test
    public void testNoShapes() throws Exception {
        //        String name = "StarLightCurves";
        //        String datasetPath = String.format("/Users/isak/Downloads/dataset2/%s/%s_", name, name);
        //
        //        long start = System.currentTimeMillis();
        //        Frame trainFrame = new DataSeriesInputStream(new FileInputStream(datasetPath + "TEST"))
        //                .read(Frame.getFactory());
        //        System.out.println(System.currentTimeMillis() - start);
        //
        //        start = System.currentTimeMillis();
        //        Frame xTrain = trainFrame.dropColumns(Range.closed(1, trainFrame.columns()));
        //        System.out.println(System.currentTimeMillis() - start);
        //
        //        start = System.currentTimeMillis();
        //        CategoricColumn yTrain = Datasets.getColumnAs(trainFrame, 0, DefaultCategoricColumn.getFactory());
        //
        //        System.out.println(System.currentTimeMillis() - start);
        //        System.out.println(xTrain);
        //        System.out.println(yTrain);
        //
        //        String file = "/Users/isak/Projects/adeb/erlang/adeb-rr/deps/rr/data/connect-4.txt";
        //        start = System.currentTimeMillis();
        //        DenseDataset dataset = new CSVInputStream(new FileInputStream(file)).read(DenseDataset.getFactory());
        //        System.out.println(System.currentTimeMillis() - start);
        //
        //        System.out.println(dataset);


        //        Frame testFrame = Datasets.randomize(
        //                new DataSeriesInputStream(new FileInputStream(datasetPath + "TEST")).read(Frame.getFactory())
        //        );

        //        System.exit(0);


        //        int[] noShapelets = new int[]{1, 5, 10, 20, 30, 50, 75, 100};
        //        for (int i = 0; i < noShapelets.length; i++) {
        //            RandomShapeletForest forest = RandomShapeletForest
        //                    .withSize(100)
        //                    .withLowerLength(2)
        //                    .withUpperLength(-1)
        //                    .withInspectedShapelets(noShapelets[i])
        //                    .withSampler(Bootstrap.create())
        //                    .create();
        //            RandomShapeletForest.Model model = forest.fit(xTrain, yTrain);

        //            HoldOutValidation<Frame, Column> validation = new HoldOutValidation<>(test.getDataset(),
        // test.getTarget());
        //            Result result = validation.evaluate(model, train.getDataset(), train.getTarget());
        //            System.out.println(noShapelets[i] + "," + result.getAverage(ErrorRate.class, Metric.Sample.OUT));
        //        }


    }

    private JFreeChart bar(Matrix x, String xlabel, Matrix y, String ylabel) {
        XYSeriesCollection collection = new XYSeriesCollection();


        XYSeries series = new XYSeries("Series");
        for (int i = 0; i < x.size(); i++) {
            series.add(x.get(i), y.get(i));
        }

        collection.addSeries(series);

        JFreeChart chart = ChartFactory.createXYBarChart(null, xlabel, false, ylabel, new XYBarDataset(collection, 4));
        chart.removeLegend();
        return Chartable.applyTheme(chart);
    }

    public JFreeChart area(Matrix x, String xlabel, Matrix y, String ylabel) {
        XYSeriesCollection collection = new XYSeriesCollection();

        XYSeries series = new XYSeries("Series");
        for (int i = 0; i < x.size(); i++) {
            series.add(x.get(i), y.get(i));
        }

        collection.addSeries(series);

        JFreeChart chart = ChartFactory.createXYAreaChart(null, xlabel, ylabel, collection);
        XYPlot plot = (XYPlot) chart.getPlot();
        XYSplineRenderer r = new XYSplineRenderer(1, XYSplineRenderer.FillType.TO_ZERO);
        r.setSeriesFillPaint(0, new Color(0, 55, 255, 180));
        plot.setRenderer(r);


        chart.removeLegend();
        return Chartable.applyTheme(chart);
    }

    @Test
    public void testCreateSizePlot() throws Exception {
        Matrix error = DenseMatrix.columnVector(0.2352000996, 0.200126165, 0.1879845281, 0.176270231, 0.1738586474, 0.1724606892);
        Matrix errors = Matrices.parseMatrix("0.2352000996,0.200126165,0.1879845281,0.176270231,0.1738586474,0.1724606892;" +
                "0.249,0.249,0.249,0.249,0.249,0.249;" +
                "0.190,0.190,0.190,0.190,0.190,0.190;" +
                "0.210,0.210,0.210,0.210,0.210,0.210");
        System.out.println(errors);


        Matrix size = DenseMatrix.columnVector(10, 25, 50, 100, 250, 500);
        JFreeChart errorChart = plot(size, "No. trees", errors, "Average error", new String[]{"RSF", "1-NN", "1-NN DTW-best", "1-NN DTW-no"});
        ((XYPlot) errorChart.getPlot()).getRangeAxis().setRange(0.15, 0.26);
        errorChart.getLegend().setMargin(0, 45, -7, 0);
        Chartable.saveSVG("/Users/isak/Desktop/no_trees_error.svg", errorChart, 300, 140);

        Matrix auc = DenseMatrix.columnVector(0.9175961966, 0.9407329694, 0.9506284705, 0.9544230833, 0.9582902467, 0.959242855);
        JFreeChart aucChart = plot(size, "No. trees", auc, "Average AUC");
        ((XYPlot) aucChart.getPlot()).getRangeAxis().setRange(0.9, 1);
        Chartable.saveSVG("/Users/isak/Desktop/no_trees_auc.svg", aucChart, 250, 200);

    }

    public JFreeChart plot(MatrixLike x, String xlabel, MatrixLike y, String ylabel, String[] labels) {
        XYSeriesCollection collection = new XYSeriesCollection();

        for (int i = 0; i < y.rows(); i++) {
            String label = "unkown";
            if (labels != null && i < labels.length) {
                label = labels[i];
            }
            XYSeries series = new XYSeries(label);
            for (int j = 0; j < y.columns(); j++) {
                series.add(x.get(j), y.get(i, j));
            }
            collection.addSeries(series);
        }
        JFreeChart chart = Chartable.applyTheme(ChartFactory.createXYLineChart(null, xlabel, ylabel, collection));
        chart.getXYPlot().setRenderer(new XYLineAndShapeRenderer(true, false));
        chart.getXYPlot().setDomainGridlinesVisible(true);
        chart.getXYPlot().setRangeGridlinesVisible(false);
        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.getLegend().setItemLabelPadding(RectangleInsets.ZERO_INSETS);

        ((XYLineAndShapeRenderer) chart.getXYPlot().getRenderer()).setSeriesShapesVisible(0, true);
        chart.getLegend().setPosition(RectangleEdge.TOP);

        java.util.List<float[]> strokes = new ArrayList<>();
        strokes.add(new float[]{3f, 3f});
        strokes.add(new float[]{1f});
        strokes.add(new float[]{3f, 3f, 1f});

        for (int i = 1; i < y.rows(); i++) {
            chart.getXYPlot().getRenderer().setSeriesStroke(i,
                    new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, strokes.get((i - 1) % strokes.size()), 0f));
        }
        return chart;
    }

    public JFreeChart plot(MatrixLike x, String xlabel, MatrixLike y, String ylabel) {
        return plot(x, xlabel, y, ylabel, (String[]) null);
    }

    @Test
    public void testPlotData() throws Exception {
        String[] files = new String[]{"RSF-1NN", "RSF-1NNDTW-best", "RSF-1NNDTW-no"};
        Map<String, String> map = new HashMap<>();
        map.put("RSF-1NN", "1-nearest neighbor");
        map.put("RSF-1NNDTW-best", "1-NN DTW-best)");
        map.put("RSF-1NNDTW-no", "1-NN DTW-no)");
        for (String file : files) {
            try (CSVInputStream in = new CSVInputStream(new FileInputStream("/Users/isak/Desktop/" + file + ".csv"))) {
                DataFrame frame = new MixedDataFrame();//in.read(MatrixDataFrame.copyTo());

                XYLineAndShapeRenderer pointRenderer = new XYLineAndShapeRenderer(false, true);
                XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer(true, false);

                // Draw the baseline line of equal performance
                XYSeriesCollection baselineCollection = new XYSeriesCollection();
                XYSeries baseline = new XYSeries("Baseline");
                baseline.add(0, 0);
                baseline.add(1, 1);
                baselineCollection.addSeries(baseline);


                // Draw the points of actual pair-wise performances
                XYSeriesCollection rndknn = new XYSeriesCollection();
                for (int i = 0; i < frame.rows(); i++) {
                    XYSeries series = new XYSeries("" + i);
                    series.add(frame.getAsDouble(i, 0), frame.getAsDouble(i, 1));
                    rndknn.addSeries(series);
                }

                ValueAxis xBase = new NumberAxis("Shapelet Forest");
                ValueAxis yBase = new NumberAxis(map.get(file));

                // The x and y axis can only be between 0 and 1
                xBase.setRange(0, 1);
                yBase.setRange(0, 1);

                // First draw the scatter of pair-wise performances
                XYPlot combined = new XYPlot();
                combined.setDataset(0, rndknn);
                combined.setRenderer(0, pointRenderer);
                combined.setDomainAxis(0, xBase);
                combined.setRangeAxis(0, yBase);
                combined.mapDatasetToDomainAxis(0, 0);
                combined.mapDatasetToRangeAxis(0, 0);

                // Then draw the baseline curve
                combined.setDataset(1, baselineCollection);
                combined.setRenderer(1, lineRenderer);

                combined.mapDatasetToDomainAxis(1, 0);
                combined.mapDatasetToRangeAxis(1, 0);


                JFreeChart chart = Chartable.applyTheme(new JFreeChart(null, combined));
                chart.removeLegend();

                // Change the baseline to a black dashed curve
                chart.getXYPlot().getRenderer(1).setSeriesPaint(0, Color.black);
                chart.getXYPlot().getRenderer(1).setSeriesStroke(0, new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f, new float[]{5f, 3f}, 0f));

                // ... and the points to blue diagonal crosses
                Shape cross = ShapeUtilities.createDiagonalCross(2, 0.1f);
                for (int i = 0; i < frame.rows(); i++) {
                    chart.getXYPlot().getRenderer(0).setSeriesPaint(i, Chartable.theme.getColor(0));
                    chart.getXYPlot().getRenderer(0).setSeriesShape(i, cross);
                }

                Chartable.saveSVG("/Users/isak/Desktop/" + file + ".svg", chart, 250, 200);
            }
        }
    }

    @Test
    public void testRandomData() throws Exception {
        //        DataFrame.Builder dataset = new MixedDataFrame.Builder(DoubleValue.TYPE);
        //        StringVector.Builder targetBuilder = new StringVector.Builder(dataset.rows());
        //        for (int i = 0; i < dataset.rows(); i++) {
        //            if (new Random().nextDouble() > 0.5) {
        //                for (int j = 40; j < 55; j++) {
        //                    dataset.set(i, j, dataset.get(i, j) + j / 15);
        //                }
        //                targetBuilder.add(1);
        //            } else {
        //                for (int j = 10; j < 20; j++) {
        //                    dataset.put(i, j, dataset.get(i, j) - j / 10);
        //                }
        //                targetBuilder.add(0);
        //            }
        //        }
        //        CategoricColumn categoricColumn = targetBuilder.create();
        //
        //        Matrix x = Matrices.linspace(dataset.columns() - 1, dataset.columns(), 0);
        //        Chartable.saveSVG("/Users/isak/Desktop/timeSeries.svg", plotRows(x, dataset.asMatrix(), categoricColumn));
        //
        //        RandomShapeletForest.Builder forestBuilder = RandomShapeletForest.withSize(100).withLowerLength(2).withUpperLength(-1).withInspectedShapelets(3).withSampler(Bootstrap.create());
        //        RandomShapeletForest forest = forestBuilder.create();
        //
        //
        //        SupervisedDataset<MatrixDataFrame, CategoricColumn> supervisedDataset = new SupervisedDataset<>(dataset, categoricColumn,
        //                MatrixDataFrame.copyTo(), DefaultCategoricColumn.copyTo());
        //        Result result = Evaluators.splitValidation(forest, supervisedDataset, 0.33);
        //        System.out.println(result);
        //
        //        RandomShapeletForest.Model model = forest.fit(dataset, categoricColumn);
        //        System.out.println(model.getLengthImportance());
        //        System.out.println(model.getPositionImportance());
        //
        //        JFreeChart lengthImportance = plot(x, "Length", model.getLengthImportance(), "Importance");
        //        JFreeChart positionImportce = plot(x, "Position", model.getPositionImportance(), "Importance");
        //        Chartable.saveSVG("/Users/isak/Desktop/lengthImportance.svg", lengthImportance);
        //        Chartable.saveSVG("/Users/isak/Desktop/positionImportance.svg", positionImportce);
        //
    }

    public JFreeChart plotRows(Matrix x, Matrix ys, Vector targets) {
        XYSeriesCollection collection = new XYSeriesCollection();

        for (int i = 0; i < ys.rows(); i++) {
            XYSeries series = new XYSeries("" + i);
            Matrix y = ys.getRow(i);
            for (int j = 0; j < x.size(); j++) {
                series.add(x.get(j), y.get(j));
            }
            collection.addSeries(series);
        }
        JFreeChart chart = Chartable.applyTheme(ChartFactory.createXYLineChart(null, "Position", null, collection));
        XYPlot plot = (XYPlot) chart.getPlot();
        XYItemRenderer renderer = plot.getRenderer();
        for (int i = 0; i < targets.size(); i++) {
            renderer.setSeriesPaint(i, targets.get(i) == 1 ? Color.BLUE : Color.RED);
        }

        chart.removeLegend();
        return chart;
    }

    public JFreeChart plot(MatrixLike x, String xlabel, MatrixLike y, String ylabel, MatrixLike e) {
        XYIntervalSeriesCollection collection = new XYIntervalSeriesCollection();
        XYIntervalSeries series = new XYIntervalSeries("Series");
        for (int i = 0; i < x.size(); i++) {
            double xv = x.get(i);
            double yv = y.get(i);
            double ev = e.get(i);
            series.add(xv, xv, xv, yv, yv - ev, yv + ev);
        }
        collection.addSeries(series);

        NumberAxis xAxis = new NumberAxis(xlabel);
        NumberAxis yAxis = new NumberAxis(ylabel);
        XYErrorRenderer renderer = new XYErrorRenderer();
        renderer.setBaseLinesVisible(true);
        renderer.setBaseShapesVisible(false);
        XYPlot plot = new XYPlot(collection, xAxis, yAxis, renderer);

        JFreeChart chart = new JFreeChart(null, plot);
        return Chartable.applyTheme(chart);
    }
}