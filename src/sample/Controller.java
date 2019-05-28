package sample;

import ij.plugin.DICOM;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.File;

import java.io.IOException;
import java.net.URL;

import java.util.Optional;
import java.util.ResourceBundle;


public class Controller implements Initializable
{
    @FXML
    public ImageView image_ori_view, image_view;

    @FXML
    public TextField numberOfClusters;
    @FXML
    public TextField kernel_size_tf;
    @FXML
    public TextField sigma_tf;
    @FXML
    public TextField low_treshold_tf;
    @FXML
    public TextField high_treshold_tf;
    @FXML
    public Label timer_lbl;
    @FXML
    public Label k_timer_lbl;
    @FXML
    public ScrollPane scroll_original,scroll_changed;

    @FXML
    public Button k_start_btn;

    private int K=3;

    private DICOM dcm = new DICOM();

    private String extension,name;

    private Kmeans_seg kmeans_seg = new Kmeans_seg();

    private Canny_ed ce = new Canny_ed();

    private BufferedImage realSegmented;

    private BufferedImage buff_image;

    private File imageFile;

    final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);

    final DoubleProperty zoomProperty2 = new SimpleDoubleProperty(200);


    public void kmeans_start(ActionEvent actionEvent) {
        if(image_ori_view.getImage()!=null) {
            kmeans_seg.setImage(toGrayScale(buff_image));
            if (!numberOfClusters.getText().replaceAll("[^0-9?!\\.]", "").isEmpty())
                kmeans_seg.setK(Integer.parseInt(numberOfClusters.getText()));

            k_timer_lbl.setText("processing...");
            long start_time= System.currentTimeMillis();
            realSegmented = kmeans_seg.segment();
            long end_time = System.currentTimeMillis();
            float sec = (end_time - start_time) / 1000F;
            k_timer_lbl.setText("ended in "+sec+" sec");

            setImage_view(SwingFXUtils.toFXImage(realSegmented, null));
        }
    }

    private void setImage_view(Image image){
        image_view.setImage(image);
        image_view.preserveRatioProperty().set(true);


        zoomProperty.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable arg0) {
                image_ori_view.setFitWidth(zoomProperty.get());
                image_ori_view.setFitHeight(zoomProperty.get());
            }
        });

        scroll_original.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    zoomProperty.set(zoomProperty.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    zoomProperty.set(zoomProperty.get() / 1.1);
                }
            }
        });

        zoomProperty2.addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable arg0) {
                image_view.setFitWidth(zoomProperty2.get());
                image_view.setFitHeight(zoomProperty2.get());
            }
        });

        scroll_changed.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    zoomProperty2.set(zoomProperty2.get() * 1.1);
                } else if (event.getDeltaY() < 0) {
                    zoomProperty2.set(zoomProperty2.get() / 1.1);
                }
            }
        });

    }


    public void canny_start(ActionEvent actionEvent){

        if(image_ori_view.getImage()!=null) {
            ce.setOriginal_image(toGrayScale(buff_image));
            timer_lbl.setText("processing...");
            if (!kernel_size_tf.getText().replaceAll("[^0-9?!\\.]", "").isEmpty())
                ce.setGaussian_size(Integer.parseInt(kernel_size_tf.getText()));
            if (!sigma_tf.getText().replaceAll("[^0-9?!\\.]", "").isEmpty())
                ce.setGaussian_sigma(Float.parseFloat(sigma_tf.getText().replaceAll("[^0-9?!\\.]", "")));
            if (!low_treshold_tf.getText().replaceAll("[^0-9?!\\.]", "").isEmpty())
                ce.setLow_treshhold(Float.parseFloat(low_treshold_tf.getText().replaceAll("[^0-9?!\\.]", "")));
            if (!high_treshold_tf.getText().replaceAll("[^0-9?!\\.]", "").isEmpty())
                ce.setHigh_treshhold(Float.parseFloat(high_treshold_tf.getText().replaceAll("[^0-9?!\\.]", "")));

            timer_lbl.setText("processing...");
            long start_time= System.currentTimeMillis();
            ce.start_edge_detection();
            long end_time = System.currentTimeMillis();
            float sec = (end_time - start_time) / 1000F;
            timer_lbl.setText("ended in "+sec+" sec");

            setImage_view(SwingFXUtils.toFXImage(ce.getCedImage(), null));
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private BufferedImage toGrayScale(BufferedImage image){
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp op = new ColorConvertOp(cs, null);
        return op.filter(image, null);
    }


    public void new_image() throws IOException {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Segmentation Parameters");
        dialog.setHeaderText("Please enter DICOM FILEPATH");
        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()){
            imageFile = new File(result.get());
        }

        if(dcm_extension(imageFile)) {
            dcm.open(imageFile.getPath());
            buff_image = dcm.getBufferedImage();
        } else {
            buff_image = ImageIO.read(imageFile);
        }
        image_ori_view.setImage(SwingFXUtils.toFXImage(buff_image, null));


    }


    public void search_image_dcm(ActionEvent actionEvent) throws IOException {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File("C:\\Users"));
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Files","*.dcm", "*.jpg","*.png"));
        imageFile = fc.showOpenDialog(null);

        if(dcm_extension(imageFile)) {
            dcm.open(imageFile.getPath());
            buff_image = dcm.getBufferedImage();
        } else {
            buff_image = ImageIO.read(imageFile);
        }
        image_ori_view.setImage(SwingFXUtils.toFXImage(buff_image, null));

    }

    public void clear_image(ActionEvent actionEvent) {
        image_ori_view.setImage(null);
        image_view.setImage(null);
    }

    private boolean dcm_extension(File file){
        name = file.getName();
        extension = name.substring(name.lastIndexOf("."));
        return extension.equals(".dcm") ? true : false;
    }


}
