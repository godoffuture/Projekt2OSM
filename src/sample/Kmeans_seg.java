package sample;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

import static java.lang.Math.*;

public class Kmeans_seg {

    private BufferedImage image;

    private int kNumber = 5;

    private int[][] intensityArray;

    private double[] centroids = new double[kNumber];

    private BufferedImage placeHolder;

    private int width;

    private int height;

    private int maxIterations = 100;

    private ColorHelper colorHelper;

    public Kmeans_seg(){
        colorHelper = new ColorHelper();
    }

    public void setImage(BufferedImage image){
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        getIntensityArray();
    }

    public void setK(int K){
        if(K>0){
            this.kNumber = K;
            this.centroids = new double[K];
        }
    }
    private void getIntensityArray(){
        this.intensityArray = new int[height][width];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                this.intensityArray[row][col] = image.getRGB(col, row);
            }
        }
    }

    private Double calculateDistance(double centroidIntensity, double pixelIntensity){
        return abs(pixelIntensity - centroidIntensity);
    }

    private void initiateCentroids(){
        Random generator = new Random();
        for(int i = 0; i<kNumber; i++ ){
            //random int in range <1, 255>
            centroids[i] = generator.nextInt(255) + 1;
        }
    }
    private void updateCentroids(int[][] centroidArray){
        int count;
        double sum;
        Color gray;
        for(int i = 0; i < kNumber; i++){
            sum = 0;
            count = 0;
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++)
                    if (centroidArray[row][col] == i){
                        gray = new Color(intensityArray[row][col]);
                        count++;
                        sum += gray.getRed();
                    }

            }

            centroids[i] = sum/count;
        }
    }

    private BufferedImage coloring(int[][] centroidArray){
        Color temp;
        Color[] colorArray = colorHelper.getArray();
        int centroid = 0;

        placeHolder = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                temp =  new Color(image.getRGB(col,row));
                //if ( temp.getRed() < 10){
                //  placeHolder.setRGB(row,col,Color.BLACK.getRGB());
                //}
                //else {
                centroid = centroidArray[row][col];
                placeHolder.setRGB(col, row, colorArray[centroid].getRGB());
                //}
            }
        }

        return placeHolder;
    }
    private double getGray(Color color){
        return (color.getRed()+color.getBlue()+color.getGreen())/3;
    }

    public BufferedImage segment(){
        double minDistance = 255;
        int minDistanceCentroid = 0;
        double distance;
        int[][] centroidArray = new int[height][width];
        Color gray;

        initiateCentroids();
        for (int i = 0; i < maxIterations; i++) {
            for (int row = 0; row < height; row++) {
                for (int col = 0; col < width; col++) {
                    minDistance = 255;
                    for (int c = 0; c < kNumber; c++) {
                        gray = new Color(intensityArray[row][col]);
                        distance = calculateDistance(centroids[c], getGray(gray));
                        if (distance < minDistance) {
                            minDistance = distance;
                            minDistanceCentroid = c;
                        }
                    }
                    centroidArray[row][col] = minDistanceCentroid;
                }
            }
            updateCentroids(centroidArray);
        }
        return coloring(centroidArray);
    }
}
