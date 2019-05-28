package sample;

import java.awt.image.BufferedImage;
import java.util.Arrays;

public class Canny_ed {

    private BufferedImage original_image,ced_image;

    private int height;
    private int width;

    private float gaussian_sigma = 2f;
    private int gaussian_size = 8;//musi byc wieksze od 2
    private float low_treshhold = 1;
    private float high_treshhold = 2;

    private int[] image_data;
    private int[] image_mag;

    private int image_size;

    //convolution of original_image by gaussain coefficent funkcja conv2 matlab
    private float[] conv_x;
    private float[] conv_y;

    private float[] gradient_x;
    private float[] gradient_y;


    public void start_edge_detection(){

        width = original_image.getWidth();
        height = original_image.getHeight();
        image_size = width*height;


        image_data = new int[image_size];
        image_mag = new int[image_size];

        conv_x = new float[image_size];
        conv_y = new float[image_size];
        gradient_x = new float[image_size];
        gradient_y = new float[image_size];

        byte[] image_pixels = (byte[]) original_image.getData().getDataElements(0, 0, width, height, null);
        for (int i = 0; i < image_size; i++){
            image_data[i] = (image_pixels[i] & 0xff);
        }
        gaussian_grad();

        hysteresis(Math.round(low_treshhold * 100F),Math.round( high_treshhold * 100F));
        threshold_edges();
    }

    private float gaussian(float k) {
        return (float) Math.exp(-(k * k) / (2f * gaussian_sigma * gaussian_sigma));
    }

    private void gaussian_grad() {
        int gaussian_width;
        float gaussian_kernel[] = new float[gaussian_size];
        float difference_gaussian_kernel[] = new float[gaussian_size];

        for (gaussian_width = 0; gaussian_width < gaussian_size; gaussian_width++) {
            if (gaussian(gaussian_width) <= 0.005f) break;
            gaussian_kernel[gaussian_width] = (gaussian(gaussian_width - 0.5f) + gaussian(gaussian_width) + gaussian(gaussian_width + 0.5f)) / 6f / ((float) Math.PI * (float) Math.pow(gaussian_sigma, 2));
            difference_gaussian_kernel[gaussian_width] = gaussian(gaussian_width + 0.5f) - gaussian(gaussian_width - 0.5f);
        }



        for (int x = gaussian_width - 1; x < width - (gaussian_width - 1); x++) {
            for (int y = width * (gaussian_width - 1); y < width * (height - (gaussian_width - 1)); y += width) {
                int index = x + y;
                float x_sum = image_data[index] * gaussian_kernel[0];
                float y_sum = x_sum;
                int xOffset = 1;
                int yOffset = width;
                for (; xOffset < gaussian_width; ) {
                    y_sum += gaussian_kernel[xOffset] * (image_data[index - yOffset] + image_data[index + yOffset]);
                    x_sum += gaussian_kernel[xOffset] * (image_data[index - xOffset] + image_data[index + xOffset]);
                    yOffset += width;
                    xOffset++;
                }
                conv_y[index] = y_sum;
                conv_x[index] = x_sum;

            }
        }

        for (int gw = gaussian_width; gw < width - gaussian_width; gw++) {
                for (int y = width * (gaussian_width - 1); y < width * (height - (gaussian_width - 1)); y += width) {
                    float x_sum = 0.0f;
                    int index_gw = gw + y;
                    int yOffset = width;
                    for (int i = 1; i < gaussian_width; i++) {
                        x_sum += difference_gaussian_kernel[i] * (conv_x[index_gw - yOffset] - conv_x[index_gw + yOffset]);
                        yOffset += width;
                    }

                    gradient_y[index_gw] = x_sum;
                }

        }

        for (int x = gaussian_width - 1; x < width - (gaussian_width - 1); x++) {
            for (int y = width * (gaussian_width - 1); y < width * (height - (gaussian_width - 1)); y += width) {
                float x_sum = 0.0f;
                int index = x + y;
                float y_sum = 0.0f;
                int yOffset = width;
                for (int i = 1; i < gaussian_width; i++) {
                    y_sum += difference_gaussian_kernel[i] * (conv_y[index - i] - conv_y[index + i]);
                }
                gradient_x[index] = y_sum;
            }
        }



        // non-maximal supression
        for (int x = gaussian_width; x < width - gaussian_width; x++) {
            for (int y = width * gaussian_width; y < width * (height - gaussian_width); y += width) {
                int index = x + y;

                float gr_x = gradient_x[index];
                float gr_y = gradient_y[index];
                float gradMag = (float) Math.hypot(gr_x, gr_y);

                float mag_n = (float) Math.hypot(gradient_x[index - width], gradient_y[index - width]);
                float mag_s = (float) Math.hypot(gradient_x[index + width], gradient_y[index + width]);
                float mag_w = (float) Math.hypot(gradient_x[index - 1], gradient_y[index - 1]);
                float mag_e = (float) Math.hypot(gradient_x[index + 1], gradient_y[index + 1]);
                float mag_ne = (float) Math.hypot(gradient_x[index - width - 1], gradient_y[index - width - 1]);
                float mag_se = (float) Math.hypot(gradient_x[index + width + 1], gradient_y[index + width+ 1]);
                float mag_sw = (float) Math.hypot(gradient_x[index - width - 1], gradient_y[index - width - 1]);
                float mag_nw = (float) Math.hypot(gradient_x[index + width+ 1], gradient_y[index + width+ 1]);

                float tmp;

                if (gr_x * gr_y <= (float) 0
                        ? Math.abs(gr_x) >= Math.abs(gr_y)
                        ? (tmp = Math.abs(gr_x * gradMag)) >= Math.abs(gr_y * mag_ne - (gr_x + gr_y) * mag_e)
                        && tmp > Math.abs(gr_y * mag_sw - (gr_x + gr_y) * mag_w)
                        : (tmp = Math.abs(gr_y * gradMag)) >= Math.abs(gr_x * mag_ne - (gr_y + gr_x) * mag_n)
                        && tmp > Math.abs(gr_x * mag_sw - (gr_y + gr_x) * mag_s)
                        : Math.abs(gr_x) >= Math.abs(gr_y)
                        ? (tmp = Math.abs(gr_x * gradMag)) >= Math.abs(gr_y * mag_se + (gr_x - gr_y) * mag_e)
                        && tmp > Math.abs(gr_y * mag_nw + (gr_x - gr_y) * mag_w)
                        : (tmp = Math.abs(gr_y * gradMag)) >= Math.abs(gr_x * mag_se + (gr_y - gr_x) * mag_s)
                        && tmp > Math.abs(gr_x * mag_nw + (gr_y - gr_x) * mag_n)
                ) {

                    image_mag[index] = gradMag >= 1000F ? 100000 : (int) (100F * gradMag);

                } else {
                    image_mag[index] = 0;
                }
            }
        }
        }

        private void hysteresis(int low, int high) {
            Arrays.fill(image_data, 0);

            int offset = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (image_data[offset] == 0 && image_mag[offset] >= high) {
                        follow(x, y, offset, low);
                    }
                    offset++;
                }
            }
        }


        private void follow(int x1, int y1, int i1, int threshold) {
            int x0 = x1 == 0 ? x1 : x1 - 1;
            int x2 = x1 == width - 1 ? x1 : x1 + 1;
            int y0 = y1 == 0 ? y1 : y1 - 1;
            int y2 = y1 == height -1 ? y1 : y1 + 1;

            image_data[i1] = image_mag[i1];
            for (int x = x0; x <= x2; x++) {
                for (int y = y0; y <= y2; y++) {
                    int i2 = x + y * width;
                    if ((y != y1 || x != x1)
                            && image_data[i2] == 0
                            && image_mag[i2] >= threshold) {
                        follow(x, y, i2, threshold);
                        return;
                    }
                }
            }
        }


    private void threshold_edges() {
        for (int i = 0; i < image_size; i++) {
            image_data[i] = image_data[i] > 0 ? -1 : 0xff000000;
        }
    }

    public BufferedImage getCedImage(){
        ced_image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        ced_image.getWritableTile(0,0).setDataElements(0,0,width, height, image_data);
        return ced_image;
    }

    public void setOriginal_image(BufferedImage image){
        this.original_image = image;
    }

    public void setGaussian_sigma(float gaussian_sigma) {
        this.gaussian_sigma = gaussian_sigma;
    }

    public void setGaussian_size(int gaussian_size) {
        this.gaussian_size = gaussian_size;
    }

    public void setLow_treshhold(float low_treshhold) {
        this.low_treshhold = low_treshhold;
    }

    public void setHigh_treshhold(float high_treshhold) {
        this.high_treshhold = high_treshhold;
    }
}
