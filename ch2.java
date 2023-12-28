import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class ch2 extends Application {

    public int MAX_POINTS = 16;
    public int RADIUS = 5;
    public Pane root;
    public List<Circle> points;
    public List<Line> lines;
    public List<List<Point2D>> convexHullSteps;
    public int currentStep;

    @Override
    public void start(Stage stage) {
        root = new Pane();
        points = new ArrayList<>();
        lines = new ArrayList<>();
        convexHullSteps = new ArrayList<>();
        currentStep = -1;

        for (int i = 0; i < MAX_POINTS; i++) {
            double x = Math.random() * (800 - RADIUS * 2) + RADIUS;
            double y = Math.random() * (600 - RADIUS * 2) + RADIUS;

            Circle point = new Circle(x, y, RADIUS, Color.PURPLE);
            points.add(point);
            root.getChildren().add(point);
            System.out.println((int)x + " " +  (int)y);

        }
        root.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                if (currentStep >= convexHullSteps.size()) {
                    return;  
                }
                if (currentStep == -1) {
                    executeConvexHull();
                    currentStep = 0;
                }
            }
        });
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Convex Hull");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public void executeConvexHull() {
        root.getChildren().removeAll(lines);
        lines.clear();
        convexHullSteps.clear();

        List<Point2D> pointList = new ArrayList<>();
        for (Circle point : points) {
            double x = point.getCenterX();
            double y = point.getCenterY();
            pointList.add(new Point2D(x, y));
        }
        calcConvexHullSteps(pointList);
    }

    public void calcConvexHullSteps(List<Point2D> points) {
        if (points.size() <= 3) {
            convexHullSteps.add(new ArrayList<>(points));
            return;
        }
        int left = 0;
        Point2D leftPoint = points.get(left);
        for (int i = 1; i < points.size(); i++) {
            Point2D currentPoint = points.get(i);
            if (currentPoint.getX() < leftPoint.getX()) {
                left = i;
                leftPoint = currentPoint;
            }
        }
        List<Point2D> convexHull = new ArrayList<>();
        int currentPointIndex = left;
        int next;
        do {
            convexHull.add(points.get(currentPointIndex));
            next = (currentPointIndex + 1) % points.size();

            for (int i = 0; i < points.size(); i++) {
                if (i == currentPointIndex) {
                    continue;
                }

                Point2D current = points.get(currentPointIndex);
                Point2D nextPoint = points.get(next);
                Point2D fodder = points.get(i);

                double crossProduct = crossProduct(current, nextPoint, fodder);

                if (crossProduct > 0) {
                    next = i;
                convexHullSteps.add(convexHull);
                }
            }
            currentPointIndex = next;
        } while (currentPointIndex != left);

        convexHullSteps.add(convexHull);
    }

    public double crossProduct(Point2D a, Point2D b, Point2D c) {
        return (b.getX() - a.getX()) * (c.getY() - a.getY()) - (b.getY() - a.getY()) * (c.getX() - a.getX());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
