package gameObjects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Represents a drawable object in the game.
 * Each drawable object has an image and a fallback option in case the image cannot be loaded. 
 */
public abstract class DrawableObject {

    private BufferedImage image;
    
    /**
     * Attempts to load an image from the given filename.
     * If the image cannot be loaded, prompts the user for a replacement file using JFileChooser.
     * 
     * @param filename the name of the image file to load
     */
    public void loadImage(String filename) {

        try {
            this.image = ImageIO.read(new File(filename));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Failed to load image: " + filename, "Error", JOptionPane.ERROR_MESSAGE);
            JFileChooser fileChooser = new JFileChooser("resources/images/");
            fileChooser.setDialogTitle("Select Replacement Image");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "png", "jpg", "jpeg", "gif"));
            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    this.image = ImageIO.read(fileChooser.getSelectedFile());
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, "Failed to load image: " + filename, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Draws a fallback shape if the image cannot be loaded.
     * 
     * @param g2d the Graphics2D object to draw on
     */
    public abstract void drawFallbackShape(Graphics2D g2d);

    /**
     * Returns the x-coordinate of the object (needed to draw the image)
     * 
     * @return the x-coordinate of the object
     */
    protected abstract int getX();

    /**
     * Returns the y-coordinate of the object (needed to draw the image)
     * 
     * @return the y-coordinate of the object
     */
    protected abstract int getY();

    /**
     * Draws the object on the given Graphics2D object. 
     * If the image is null, draws a fallback shape instead.
     * 
     * @param g2d the Graphics2D object to draw on
     */
    public void drawOn(Graphics2D g2d) {
        if (this.image == null) {
            this.drawFallbackShape(g2d);
        } else {
            g2d.drawImage(this.image, this.getX(), this.getY(), null);
        }
    }
}
