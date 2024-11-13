package farmersMarkets;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Properties;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * The DatabaseManager class takes care of all sql queries and inserts
 * for the database.
 * @author Chev Kodama
 * @version 1.0
 */
public class DatabaseManager {
	private Connection connect() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/db.properties")) {
            properties.load(input);

            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");

            return DriverManager.getConnection(url, user, password);

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
	
	/**
	 * Adds a market to the database.
	 * @param fmid		id
	 * @param name		market name
	 * @param street	market street
	 * @param city		market city
	 * @param state		market state
	 * @param zip_code	market zip_code
	 * @param county	market county
	 * @param links		website, facebook, twitter, youtube, and other media links/usernames
	 * @param schedule	seasons 1-4 dates and times of opening
	 * @param tags		things available
	 * @param location	the longitude and latitude of this market
	 * @param updated	when this information is from
	 */
    public void addMarket(int fmid, String name, String street, String city,
            String state, String zip_code, String county, ArrayList<String> links,
            ArrayList<String> schedule, ArrayList<String> tags, String location,
            String updated) {
        
        /* check if the fmid already exists */
        String checkSql = "SELECT COUNT(*) FROM markets WHERE fmid = ?";
        
        try (Connection connection = connect();
             PreparedStatement check_statement = connection.prepareStatement(checkSql)) {
            
            check_statement.setInt(1, fmid);
            try (ResultSet rs = check_statement.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    /* fmid does not exist */
                    String sql = "INSERT INTO markets (fmid, name, street, city, state, zip_code, county, " +
                           "website, facebook, twitter, youtube, other_media, " +
                           "s1_date, s1_time, s2_date, s2_time, s3_date, s3_time, s4_date, s4_time, " +
                           "location, updated, credit, WIC, WICcash, SFMNP, SNAP, Organic, Bakedgood, " +
                           "Cheese, Crafts, Flowers, Eggs, Seafood, Herbs, Vegtables, Honey, Jams, " +
                           "Maple, Meat, Nursery, Nuts, Plants, Poultry, Prepared, Soap, Trees, Wine, " +
                           "Coffee, Beans, Fruits, Grains, Juices, Mushrooms, PetFood, Tofu, WildHarvested) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                           "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                           "?, ?, ?, ?, ?, ?, ?)";
                    
                    try (PreparedStatement insert_statement = connection.prepareStatement(sql)) {
                        
                        insert_statement.setInt(1, fmid);
                        insert_statement.setString(2, name);
                        insert_statement.setString(3, street);
                        insert_statement.setString(4, city);
                        insert_statement.setString(5, state);
                        insert_statement.setString(6, zip_code);
                        insert_statement.setString(7, county);
                        
                        /* set links */
                        for (int i = 0; i < 5; i++) {
                        	insert_statement.setString(8 + i, links.get(i));
                        }
                        
                        /* set schedule */
                        for (int i = 0; i < 8; i++) {
                        	insert_statement.setString(13 + i, schedule.get(i));
                        }
                        
                        insert_statement.setString(21, location);
                        insert_statement.setString(22, updated);
                        
                        /* set tags */
                        String[] tag_names = {"credit", "WIC", "WICcash", "SFMNP", "SNAP", "Organic", 
                                             "Bakedgood", "Cheese", "Crafts", "Flowers", "Eggs", "Seafood", 
                                             "Herbs", "Vegtables", "Honey", "Jams", "Maple", "Meat", "Nursery", 
                                             "Nuts", "Plants", "Poultry", "Prepared", "Soap", "Trees", "Wine", 
                                             "Coffee", "Beans", "Fruits", "Grains", "Juices", "Mushrooms", 
                                             "PetFood", "Tofu", "WildHarvested"};
                        
                        for (int i = 0; i < tag_names.length; i++) {
                            int value = tags.contains(tag_names[i]) ? 1 : 0;
                            insert_statement.setInt(23 + i, value);
                        }
                        
                        insert_statement.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Returns an observable list of market items using the markets stored in the database.
     * @param openMarketTab	the event handler for the market items
     * @return				an observable list of market items
     */
    public ObservableList<MarketItem> getMarketItems(EventHandler<MouseEvent> openMarketTab) {
    	String query = "SELECT fmid, name, street, city, state, zip_code, average_rating, location FROM markets";
    	ObservableList<MarketItem> market_items = FXCollections.observableArrayList();
    	try ( Connection connection = connect();
    			PreparedStatement statement = connection.prepareStatement(query)) {
    		ResultSet rs = statement.executeQuery();
    		
    		int index = 0;
    		while ( rs.next() ) {
    			int fmid = rs.getInt("fmid");
    			String name = rs.getString("name");
    			String address = rs.getString("street") + ", " + rs.getString("city") + ", " + rs.getString("state") + " " + rs.getString("zip_code");
    			float average_rating = rs.getFloat("average_rating");
    			String location = rs.getString("location");
    			MarketItem market_item = new MarketItem(fmid, name, address, average_rating, index, location);
    			index++;
    			market_item.setOnMouseClicked(openMarketTab);
    			
    			market_items.add(market_item);
    		}
    	}
    	catch ( SQLException e ) {
    		e.printStackTrace();
    	}
    	
    	return market_items;
    }
    
    /**
     * Returns an observable list of market items within distance miles of city and state.
     * @param city			the origin city
     * @param state			the origin state
     * @param distance		max distance from origin
     * @param openMarketTab	event handler for market items
     * @return				observable list of market items
     */
    public ObservableList<MarketItem> cityStateMarketItems(String city, String state, double distance, EventHandler<MouseEvent> openMarketTab) {
    	double longitude = 0.0;
    	double latitude = 0.0;
    	String query = "SELECT longitude, latitude FROM locations WHERE city = ? AND state = ?";
    	try ( Connection connection = connect();
    			PreparedStatement statement = connection.prepareStatement(query)) {
    		statement.setString(1, city.toLowerCase());
    		statement.setString(2, state.toLowerCase());
    		ResultSet rs = statement.executeQuery();
    		if ( rs.next() ) {
    			longitude = rs.getDouble("longitude");
    			latitude = rs.getDouble("latitude");
    		}
    		else {
    			return null;
    		}
    	}
    	catch ( SQLException e ) {
    		e.printStackTrace();
    	}
    	ObservableList<MarketItem> market_items = this.getMarketItems(openMarketTab);
    	ObservableList<MarketItem> sorted_market_items = FXCollections.observableArrayList();
    	for ( int i = 0; i < market_items.size(); i++ ) {
    		String string_location = market_items.get(i).getLocation();
    		String[] split_string_location = string_location.split(",");
    		if ( split_string_location.length != 2 ) {
    			/* skip markets with missing coordinates */
    			continue;
    		}
    		if ( !isNumeric(split_string_location[0]) || !isNumeric(split_string_location[1]) ) {
    			/* skip markets with invalid coordinates */
    			continue;
    		}
    		double[] location = new double[2];
    		location[0] = Double.parseDouble(split_string_location[0]);
    		location[1] = Double.parseDouble(split_string_location[1]);
    		
    		if ( calculateDistance(longitude, latitude, location[0], location[1]) <= distance ) {
    			sorted_market_items.add(market_items.get(i));
    		}
    	}
    	return sorted_market_items;
    }
    
    private static double calculateDistance(double start_lat, double start_long, double end_lat, double end_long) {

        double dLat = Math.toRadians((end_lat - start_lat));
        double dLong = Math.toRadians((end_long - start_long));

        start_lat = Math.toRadians(start_lat);
        end_lat = Math.toRadians(end_lat);

        double a = haversine(dLat) + Math.cos(start_lat) * Math.cos(end_lat) * haversine(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        /* 6371 = Approximate Earth radius in km */
        /* multiply by 0.621371 to convert to miles */
        return (6371 * c) * 0.621371;
    }
    
    private static double haversine(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
    
    private static boolean isNumeric(String str) {
    	return str.matches("-?\\d+(\\.\\d+)?");
    }
    
    /**
     * Returns an observable list of market items within distance miles of the zip code.
     * @param zip			the origin zip code
     * @param distance		max distance from origin
     * @param openMarketTab	event handler for market items
     * @return				an observable list of market items
     */
    public ObservableList<MarketItem> zipMarketItems(int zip, double distance, EventHandler<MouseEvent> openMarketTab) {
    	double longitude = 0.0;
    	double latitude = 0.0;
    	String query = "SELECT longitude, latitude FROM locations WHERE zip_code = ?";
    	try ( Connection connection = connect();
    			PreparedStatement statement = connection.prepareStatement(query)) {
    		statement.setInt(1, zip);
    		ResultSet rs = statement.executeQuery();
    		if ( rs.next() ) {
    			longitude = rs.getDouble("longitude");
    			latitude = rs.getDouble("latitude");
    		}
    		else {
    			return null;
    		}
    	}
    	catch ( SQLException e ) {
    		e.printStackTrace();
    	}
    	ObservableList<MarketItem> market_items = this.getMarketItems(openMarketTab);
    	ObservableList<MarketItem> sorted_market_items = FXCollections.observableArrayList();
    	for ( int i = 0; i < market_items.size(); i++ ) {
    		String string_location = market_items.get(i).getLocation();
    		String[] split_string_location = string_location.split(",");
    		if ( split_string_location.length != 2 ) {
    			/* skip markets with missing coordinates */
    			continue;
    		}
    		if ( !isNumeric(split_string_location[0]) || !isNumeric(split_string_location[1]) ) {
    			/* skip markets with invalid coordinates */
    			continue;
    		}
    		double[] location = new double[2];
    		location[0] = Double.parseDouble(split_string_location[0]);
    		location[1] = Double.parseDouble(split_string_location[1]);
    		
    		if ( calculateDistance(longitude, latitude, location[0], location[1]) <= distance ) {
    			sorted_market_items.add(market_items.get(i));
    		}
    	}
    	return sorted_market_items;
    }
    
    /**
     * Adds a review to the reviews table in the database.
     * @param fmid		market id
     * @param rating	user rating
     * @param username	user's unique display name
     * @param comment	user's comment
     * @param posted	date and time posted
     * @return			the Review
     */
    public Review addReview(int fmid, int rating, String username, String comment, LocalDateTime posted) {
    	String checkSql = "SELECT COUNT(*) FROM reviews WHERE fmid = ? AND username = ?";
    	
    	try ( Connection connection = connect();
    			PreparedStatement check_statement = connection.prepareStatement(checkSql)) {
    		
    		check_statement.setInt(1, fmid);
    		check_statement.setString(2, username);
            try (ResultSet rs = check_statement.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                	String sql = "INSERT INTO reviews (fmid, username, comment, rating, posted) VALUES (?, ?, ?, ?, ?)";
                	
                	try (PreparedStatement insert_statement = connection.prepareStatement(sql)) {
                		insert_statement.setInt(1, fmid);
                		insert_statement.setString(2, username);
                		insert_statement.setString(3, comment);
                		insert_statement.setInt(4, rating);
                		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String posted_string = posted.format(formatter);
                        insert_statement.setString(5, posted_string);
                		
                		insert_statement.executeUpdate();
                	} catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                else {
                	return null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    	
    	Review review = new Review(fmid, username, comment, rating, posted);
    	return review;
    }
    
    /**
     * Updates the average rating of a market in the
     * database based on the ratings in the review table.
     * @param fmid	target market id
     * @return		the new average rating
     */
    public float updateAverageRating(int fmid) {
    	String checkSql = "SELECT COUNT(*) FROM markets WHERE fmid = ?";
    	float average_rating = -1f;
    	try (Connection connection = connect();
                PreparedStatement check_statement = connection.prepareStatement(checkSql)) {
               
           check_statement.setInt(1, fmid);
           try (ResultSet rs = check_statement.executeQuery()) {
               if (rs.next() && rs.getInt(1) == 1) {
                   /* fmid exists */
            	   String sql = "UPDATE markets SET average_rating = ? WHERE fmid = ?";
                   try (PreparedStatement insert_statement = connection.prepareStatement(sql)) {
                	   
                	   String query = "SELECT AVG(rating) AS average_rating FROM reviews WHERE fmid = ?";
                       try (PreparedStatement query_statement = connection.prepareStatement(query)) {
                    	   query_statement.setInt(1, fmid);
                    	   ResultSet ratings_rs = query_statement.executeQuery();

                           if (ratings_rs.next()) {
                        	   average_rating = ratings_rs.getFloat("average_rating");
                           }
                       } catch (SQLException e) {
                           e.printStackTrace();
                       }
                	   
                	   insert_statement.setFloat(1, average_rating);
                       insert_statement.setInt(2, fmid);
                       
                       insert_statement.executeUpdate();
                   } catch (SQLException e) {
                       e.printStackTrace();
                   }
               }
           } catch (SQLException e) {
               e.printStackTrace();
           }
       } catch (SQLException e) {
           e.printStackTrace();
       }
    	
    	return average_rating;
    }
    
    /**
     * Returns an observable list of reviews for a market
     * @param fmid	the id of the market
     * @return		an observable list of reviews
     */
    public ObservableList<Review> getReviews(int fmid) {
    	String query = "SELECT * FROM reviews WHERE fmid = ?";
    	ObservableList<Review> reviews = FXCollections.observableArrayList();
    	try (Connection connection = connect();
    			PreparedStatement statement = connection.prepareStatement(query)) {
    		
    		statement.setInt(1, fmid);
    		
    		ResultSet rs = statement.executeQuery();
    		
    		while ( rs.next() ) {
	    		String username = rs.getString("username");
	    		String comment = rs.getString("comment");
	    		int rating = rs.getInt("rating");
	    		String posted_string = rs.getString("posted");
	    		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	            LocalDateTime posted = LocalDateTime.parse(posted_string, formatter);
	            Review review = new Review(fmid, username, comment, rating, posted);
	            
	            Platform.runLater(() -> {
	            	reviews.add(review);
	            });
    		}
    	}
    	catch ( SQLException e ) {
    		e.printStackTrace();
    	}
    	
    	Platform.runLater(() -> {
    		FXCollections.sort(reviews, Comparator.comparing(Review::getPosted).reversed());
    	});
    	
    	return reviews;
    }
    
    /**
     * Returns the market with id fmid.
     * @param fmid	the target market's id
     * @return		a market
     */
    public Market getMarket(int fmid) {
    	 String query = "SELECT * FROM markets WHERE fmid = ?";
    	 Market market = new Market();
         try (Connection connection = connect(); 
        		 PreparedStatement statement = connection.prepareStatement(query)) {
        	 /* set fmid */
        	 statement.setInt(1, fmid);

             /* execute query */
             ResultSet rs = statement.executeQuery();

             /* set up fields */
     		 String name = "";
     		 String street = "";
     		 String city = "";
     		 String state = "";
     		 String zip_code = "";
     		 String county = "";
     		 ArrayList<String> links = new ArrayList<String>();
     		 ArrayList<String> schedule = new ArrayList<String>();
     		 ArrayList<String> tags = new ArrayList<String>();
     		 String location = "";
     		 String updated = "";
             if (rs.next()) {
            	 /* get and store fields */
                 name = rs.getString("name");
                 street = rs.getString("street");
                 city = rs.getString("city");
                 state = rs.getString("state");
                 zip_code = rs.getString("zip_code");
                 county = rs.getString("county");
                 links.add(rs.getString("website"));
                 links.add(rs.getString("facebook"));
                 links.add(rs.getString("twitter"));
                 links.add(rs.getString("youtube"));
                 links.add(rs.getString("other_media"));
                 schedule.add(rs.getString("s1_date"));
                 schedule.add(rs.getString("s1_time"));
                 schedule.add(rs.getString("s2_date"));
                 schedule.add(rs.getString("s2_time"));
                 schedule.add(rs.getString("s3_date"));
                 schedule.add(rs.getString("s3_time"));
                 schedule.add(rs.getString("s4_date"));
                 schedule.add(rs.getString("s4_time"));
                 location = rs.getString("location");
                 updated = rs.getString("updated");
                 String[] column_names = {
                     "credit", "WIC", "WICcash", "SFMNP", "SNAP", "Organic", 
                     "Bakedgood", "Cheese", "Crafts", "Flowers", "Eggs", "Seafood", 
                     "Herbs", "Vegtables", "Honey", "Jams", "Maple", "Meat", "Nursery", 
                     "Nuts", "Plants", "Poultry", "Prepared", "Soap", "Trees", "Wine", 
                     "Coffee", "Beans", "Fruits", "Grains", "Juices", "Mushrooms", 
                     "PetFood", "Tofu", "WildHarvested"
                 };
                 for ( String column_name : column_names ) {
                	 boolean exists = rs.getBoolean(column_name);
                	 if ( exists ) {
                		 tags.add(column_name);
                	 }
                 }
             } else {
                 System.out.println("No market found with fmid = " + fmid);
             }
             
             /* create market */
             market = new Market(fmid, name, street, city, state, zip_code, county, links, schedule, tags, location, updated);
         } catch (SQLException e) {
             e.printStackTrace();
         }
         
         return market;
    }
    
    /**
     * Adds a location to the location table in the database.
     * @param city		location's city
     * @param state		location's state
     * @param zip_code	location's zip code
     * @param longitude	location's longitude
     * @param latitude	location's latitude
     */
    public void addLocation(String city, String state, int zip_code, double longitude, double latitude) {
    	String checkSql = "SELECT COUNT(*) FROM locations WHERE city = ? AND state = ? AND zip_code = ?";
    	
    	try ( Connection connection = connect();
    			PreparedStatement check_statement = connection.prepareStatement(checkSql)) {
    		
    		check_statement.setString(1, city);
    		check_statement.setString(2, state);
    		check_statement.setInt(3, zip_code);
            try (ResultSet rs = check_statement.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                	String sql = "INSERT INTO locations (city, state, zip_code, longitude, latitude) VALUES (?, ?, ?, ?, ?)";
                	
                	try (PreparedStatement insert_statement = connection.prepareStatement(sql)) {
                		insert_statement.setString(1, city.toLowerCase());
                		insert_statement.setString(2, state.toLowerCase());
                		insert_statement.setInt(3, zip_code);
                		insert_statement.setDouble(4, longitude);
                        insert_statement.setDouble(5, latitude);
                		
                		insert_statement.executeUpdate();
                	} catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}