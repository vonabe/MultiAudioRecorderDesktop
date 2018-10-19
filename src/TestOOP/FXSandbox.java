/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TestOOP;

/**
 *
 * @author wenkael™
 */
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class FXSandbox extends Application {
    
    private static final int STAR_COUNT = 20_000;
    
    private final Rectangle[] nodes = new Rectangle[STAR_COUNT];
    private final double[] angles = new double[STAR_COUNT];
    private final long[] start = new long[STAR_COUNT];
    
    private final Random random = new Random();
    public static double rand(double max, double min)
    {
        return (Math.random()*(max-min)+min);
    }
    @Override
    public void start(final Stage primaryStage) {
        for (int i=0; i<STAR_COUNT; i++) {
            nodes[i] = new Rectangle(rand(5D, 0.5D), rand(5D, 0.5D), Color.color(rand(1, 0), rand(1, 0), rand(1, 0)));
            angles[i] = 2.0 * Math.PI * random.nextDouble();
            start[i] = random.nextInt(2_000_000_000);
        }
        final Scene scene = new Scene(new Group(nodes), 800, 600, Color.BLACK);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                final double width = 0.5 * primaryStage.getWidth();
                final double height = 0.5 * primaryStage.getHeight();
                final double radius = Math.sqrt(2) * Math.max(width, height);
                for (int i=0; i<STAR_COUNT; i++) {
                    final Node node = nodes[i];
                    final double angle = angles[i];
                    final long t = (now - start[i]) % 2_000_000_000;
                    final double d = t * radius / 2_000_000_000.0;
                    node.setTranslateX(Math.cos(angle) * d + width);
                    node.setTranslateY(Math.sin(angle) * d + height);
                    
                }
            }
        }.start();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    
}