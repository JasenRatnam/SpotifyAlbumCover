
package albumcovers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;

/**
 * 2017-11-12
 * @author Jasen Ratnam 1622549
 */
public class FXMLDocumentController implements Initializable {
    
    // Used for debugging code.
    private static boolean debug = false;
    
    // Number of the current image shown on GUI.
    private int currentImage;
    
    // Array with album images.
    private ArrayList<Image> albumsImages;
    
    // Image view to place the album images on the GUI.
    @FXML
    private ImageView img;
    
    // Button on the GUI to go to the next image.
    @FXML
    private Button next;
    
    // Button on the GUI to go the previous image.
    @FXML
    private Button prev;
    
    // Button on the GUI to save the current image on the GUI.
    @FXML
    private Button saveImage;
    
    // Button on the GUI to search the album covers of the entered artist.
    @FXML
    private Button search;
    
    // Text field on the GUI for the user to enter an artist name.
    @FXML
    private TextField artistName;
    
    // Message label on the GUI to send message to the user.
    @FXML
    public Label Message_Label;
    
    private static FXMLDocumentController instance = null;
    
    // Method to set the message on the message label.
    public static void setMessage(String message)
    {
        if (instance != null)
        {
            instance.Message_Label.setText(message);
        }
    }
    
    // Method to save the current image on the GUI.
    @FXML
    private void saveButtonAction(ActionEvent event) 
    {
        FileChooser chooser = new FileChooser();
        // SEt the extension filters type.
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", ".jpg"),
                new FileChooser.ExtensionFilter("PNG", ".png")
                );
        
        chooser.setTitle("Save File");
        File file = chooser.showSaveDialog(saveImage.getScene().getWindow());
        
        if(file != null)
        {
            try
            {
                // Save the image.
                ImageIO.write(SwingFXUtils.fromFXImage(albumsImages.get(currentImage), null),"png", file);
            } 
            catch (IOException ex) 
            {
                 Message_Label.setText("Error saving the image!!");
                if(debug)
                {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println(ex.getMessage());
                }
            }  
            
            Message_Label.setText("Image has been saved");
        }   
    }
    
    // Method to quit the programme.
    @FXML
    private void quitFileButtonAction(ActionEvent event) 
    {
        System.exit(0);
    }
    
    // Method to go to the next image.
    @FXML
    private void nextButtonAction(ActionEvent event) 
    {
        // If image the last image.
        if(currentImage == albumsImages.size() || currentImage == 49 )
        {
            Message_Label.setText("This is the last image!");
        }
        
        // If image is not the last.
        else
        {
            // Go to the next image.
            img.setImage(albumsImages.get(currentImage + 1));
            currentImage += 1;
            Message_Label.setText("Cover " + currentImage + "/" + albumsImages.size());
        }
    }
    
    // Method to go to the previous image.
    @FXML
    private void prevButtonAction(ActionEvent event) 
    {
        // If image is the first image.
        if(currentImage == 0)
        {
            Message_Label.setText("This is the first image!");
        }
        
        // If image is not the first.
        else
        {
            // Go to the previous image.
            img.setImage(albumsImages.get(currentImage - 1));
            currentImage -= 1;
            Message_Label.setText("Cover " + currentImage + "/" + albumsImages.size());
        }
    }
    
    // Method to search for the albums images for the entered images.
    @FXML
    private void searchButtonAction(ActionEvent event) 
    {
        currentImage = 0;
        String artistID = SpotifyController.getArtistId(artistName.getText());
        
        // Get all images for the search
        albumsImages = SpotifyController.getAlbumCoversFromArtist(artistID);

        // Set the first image on the GUI.
        img.setImage(albumsImages.get(currentImage));
        

        Message_Label.setText("Cover " + currentImage + "/" + albumsImages.size());

        if(debug)
        {
            System.out.println("The artist id is: " + artistID); 
        }

        // Enable all other buttons.
        next.setDisable(false);
        prev.setDisable(false);
        saveImage.setDisable(false);
        
    }
    
    // Method to initialize th programme.
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        // Disable all buttons excpect the search button.
        next.setDisable(true);
        prev.setDisable(true);
        saveImage.setDisable(true);
        
        try
        {
            // Set the spotify image at the start of th image.
            File imgfile = new File("images/spotify.png");
            Image imgImage = new Image(imgfile.toURI().toString());
            img.setImage(imgImage);
        }
        catch(Exception e)
        {
            Message_Label.setText("Error uploading image!");
        }
    }    
    
}
