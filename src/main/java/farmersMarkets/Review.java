package farmersMarkets;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * The Review class represents a review for a specific market
 * and its visualization.
 * @author Chev Kodama
 * @version 1.0
 */
public class Review extends ScrollPane {
	int fmid;
	String username;
	String comment;
	int rating;
	LocalDateTime posted;
	
	/**
	 * Review's default constructor.
	 */
	public Review() {
		super();
		fmid = 0;
		username = "";
		comment = "";
		rating = 0;
		posted = LocalDateTime.of(1, 1, 1, 1, 1, 1);
	}
	
	/**
	 * Review's custom constructor.
	 * @param fmid		fmid of the market this review is for.
	 * @param username	the display name of the user.
	 * @param comment	the user's comment.
	 * @param rating	the user's rating.
	 * @param posted	the date and time posted.
	 */
	public Review(int fmid, String username, String comment, int rating, LocalDateTime posted) {
		super();
		this.fmid = fmid;
		this.username = username;
		this.comment = comment;
		this.rating = rating;
		this.posted = posted;
		
		VBox content = new VBox();
		Label usernameLabel = new Label(this.username);
		Label ratingLabel = new Label(String.format("%d/5", this.rating));
		Label commentLabel = new Label(this.comment);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String posted_string = this.posted.format(formatter);
		Label postedLabel = new Label(posted_string);
		
		usernameLabel.setStyle("-fx-font-weight: bold");
		ratingLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold");
		
		content.getChildren().addAll(usernameLabel, ratingLabel, commentLabel, postedLabel);
		this.setContent(content);
	}
	
	/**
	 * Returns the fmid of the market being reviewed.
	 * @return	fmid
	 */
	public int getFMID() {
		return this.fmid;
	}
	
	/**
	 * Returns the display name of the user who posted this review.
	 * @return	username
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * Returns the rating of this review.
	 * @return	rating
	 */
	public int getRating() {
		return this.rating;
	}
	
	/**
	 * Returns the comment of this review.
	 * @return	comment
	 */
	public String getComment() {
		return this.comment;
	}
	
	/**
	 * Returns the date and time this review was posted.
	 * @return	posted
	 */
	public LocalDateTime getPosted() {
		return this.posted;
	}
	
	/**
	 * The overridden equals method to compare by date posted.
	 * @param o	the object to compare this Review with.
	 */
	@Override
	public boolean equals(Object o) {
		if ( o == this ) {
			return true;
		}
		
		if (!(o instanceof Review)) {
			return false;
	    }
		
		Review other = ( Review ) o;
		if ( posted.equals(other.getPosted()) ) {
			return true;
		}
		return false;
	}
}