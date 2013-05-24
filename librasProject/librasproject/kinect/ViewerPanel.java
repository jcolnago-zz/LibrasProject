package kinect;

import java.awt.*;
import javax.swing.*;
import java.awt.image.*;

import org.OpenNI.*;

import java.nio.ByteBuffer;
import learning.LearningController;

/**
 * Based on OpenNI's SimpleViewer example.
 * Display a colour Kinect webcam image.
 * Some modifications where made!
 * 
 * @author Andrew Davison, ad@fivedots.psu.ac.th
 * @since August 2011
 * @version 3.0
*/
public class ViewerPanel extends JPanel implements Runnable {
    private BufferedImage image = null;
    private int imWidth, imHeight;
    private LearningController controller;

    private volatile boolean isRunning;

    /* OpenNI */
    private Context context;
    private ImageGenerator imageGen;

    /**
     * Sets the necessary variables.
     * 
     * @param controller the controller that owns this viewerPanel
     */
    public ViewerPanel(LearningController controller) {
        setBackground(Color.WHITE);
        this.controller= controller;

        configOpenNI();
    }

    
    /** 
     * Create context and image generator.
     */
    private void configOpenNI() {
        try {
            
            context = new Context();
      
            // add the NITE Licence 
            License licence = new License("PrimeSense", "0KOIk2JeIBYClPWVnMoRKn5cdY4=");   // vendor, key
            context.addLicense(licence); 

            imageGen = ImageGenerator.create(context);

            // xRes, yRes, FPS
            MapOutputMode mapMode = new MapOutputMode(640, 480, 30);   
            imageGen.setMapOutputMode(mapMode); 
            imageGen.setPixelFormat(PixelFormat.RGB24);

            // set Mirror mode for all 
            context.setGlobalMirror(true);

            context.startGeneratingAll(); 
            System.out.println("Started context generating..."); 

            ImageMetaData imageMD = imageGen.getMetaData();
            imWidth = imageMD.getXRes();
            imHeight = imageMD.getYRes();
        } 
        catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    
    /**
     * Gets the preferred size.
     * 
     * @return new Dimension object
     */
    @Override
    public Dimension getPreferredSize(){
        return new Dimension(imWidth, imHeight);
    }

    
    /**
     * Update and display the webcam image whenever the context is updated.
     */
    @Override
    public void run() {
        isRunning = true;
        while (isRunning) {
            try {
               context.waitAnyUpdateAll();
            }
            catch(StatusException e) {
                System.out.println(e); 
                  System.exit(1);
            }
            updateImage();
            controller.setFeed(image);
        }

        // close down
        try {
            context.stopGeneratingAll();
        }
        catch (StatusException e) {}
        
        context.release();
    } 

    
    /**
     * Stops the thread from updating the image.
     */
    public void closeDown() {
        isRunning = false;
    } 

    
    /**
     * Get image data as bytes; convert to an image.
     */
    private void updateImage() {
        try {
            ByteBuffer imageBB = imageGen.getImageMap().createByteBuffer();
            image = bufToImage(imageBB);
        }
        catch (GeneralException e) {
            System.out.println(e);
        }
    }

    
    /**
     * Transform the ByteBuffer of pixel data into a BufferedImage.
     * Converts RGB bytes to ARGB ints with no transparency. 
     * 
     * @param pixelsRGB byteBuffer
     * @return BufferedImage the converted buffered image
    */
    private BufferedImage bufToImage(ByteBuffer pixelsRGB) {
        int[] pixelInts = new int[imWidth * imHeight];

        int rowStart = 0;
            // rowStart will index the first byte (red) in each row;
            // starts with first row, and moves down

        int bbIdx;               // index into ByteBuffer
        int i = 0;               // index into pixels int[]
        int rowLen = imWidth * 3;    // number of bytes in each row
        for (int row = 0; row < imHeight; row++) {
            bbIdx = rowStart;
            // System.out.println("bbIdx: " + bbIdx);
            for (int col = 0; col < imWidth; col++) {
              int pixR = pixelsRGB.get( bbIdx++ );
              int pixG = pixelsRGB.get( bbIdx++ );
              int pixB = pixelsRGB.get( bbIdx++ );
              pixelInts[i++] = 
                 0xFF000000 | ((pixR & 0xFF) << 16) | 
                 ((pixG & 0xFF) << 8) | (pixB & 0xFF);
            }
            rowStart += rowLen;   // move to next row
        }

        // create a BufferedImage from the pixel data
        BufferedImage im = 
           new BufferedImage( imWidth, imHeight, BufferedImage.TYPE_INT_ARGB);
        im.setRGB( 0, 0, imWidth, imHeight, pixelInts, 0, imWidth );
        return im;
    }
}
