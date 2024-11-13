package farmersMarkets;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * The MarketItem class represents a market thumbnail.
 * @author Chev Kodama
 * @version 1.0
 */
public class MarketItem extends ScrollPane {
	
	private int fmid;
	private String name;
	private String address;
	private float average_rating;
	private StringProperty avg_rating_prop;
	private int index;
	private String location;
	private VBox content;
	
	/**
	 * MarketVBox constructor.
	 * @param fmid	the id
	 */
    public MarketItem(int fmid, String name, String address, float average_rating, int index, String location) {
        super();  /* Call the ScrollPane constructor */
        this.fmid = fmid;
        this.name = name;
        this.address = address;
        this.average_rating = average_rating;
        this.avg_rating_prop = new SimpleStringProperty();
        this.setAverageRating(this.average_rating);
        this.index = index;
        this.location = location;

        content = new VBox();
        content.setAlignment(Pos.CENTER_LEFT);
        content.setMaxWidth(Double.MAX_VALUE);
        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-weight: bold");
        Label addressLabel = new Label(address);
        Label avgRatingLabel = new Label();
        avgRatingLabel.textProperty().bind(this.avg_rating_prop);
        avgRatingLabel.setStyle("-fx-font-weight: bold");
        content.getChildren().addAll(nameLabel, addressLabel, avgRatingLabel);
        
        this.setHbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setVbarPolicy(ScrollBarPolicy.NEVER);
        this.setContent(content);
    }
    
    /**
     * Returns this MarketItem's fmid.
     * @return fmid
     */
    public int getFMID() {
    	return this.fmid;
    }
    
    /**
     * Returns this MarketItem's name.
     * @return name
     */
    public String getName() {
    	return this.name;
    }
    
    /**
     * Returns this MarketItem's address.
     * @return	address
     */
    public String getAddress() {
    	return this.address;
    }
    
    /**
     * Returns this MarketItem's average rating.
     * @return	average_rating
     */
    public float getAvgRating() {
    	return this.average_rating;
    }
    
    /**
     * Returns this MarketItem's index.
     * @return	index
     */
    public int getIndex() {
    	return this.index;
    }
    
    /**
     * Returns this MarketItem's location.
     * @return	location
     */
    public String getLocation() {
    	return this.location;
    }
    
    /**
     * Sets average_rating to the value input.
     * @param average_rating	the new average_rating.
     */
	public void setAverageRating(float average_rating) {
		this.average_rating = average_rating;
		if (average_rating == -1f) {
	        this.avg_rating_prop.set("Average Rating: N/A");
	    } else {
	        this.avg_rating_prop.set(String.format("Average Rating: %.1f", average_rating));
	    }
	}
}