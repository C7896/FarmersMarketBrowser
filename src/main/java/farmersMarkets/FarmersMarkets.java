package farmersMarkets;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The FarmersMarkets class represents the GUI for
 * a Farmer's Market forum.
 * @author Chev Kodama
 * @version 1.0
 */
public class FarmersMarkets extends Application {
	
	private static int NUM_THREADS = Runtime.getRuntime().availableProcessors();
	Thread setUpThread;
	Thread[] locationThreads;
	private DatabaseInitializer dbi;
	private DatabaseManager dbm;
	private Scene scene;
	private TabPane root;
	private BorderPane home;
	private ListView<MarketItem> markets;
	private VBox searchBox;
	
	private VBox loading;
	private StringProperty progressProperty;
	
	private int rating;
	
	private Preferences prefs;
	private boolean loaded_markets;
	private boolean loaded_locations;
	
	/**
	 * The main function of this package.
	 * @param args	command line arguments, should be none
	 */
	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * The start function for the GUI of this package.
	 * @param stage	the main stage of this GUI's window.
	 */
	public void start(Stage stage) throws Exception {
		dbi = new DatabaseInitializer();
		dbm = new DatabaseManager();
		root = new TabPane();
		home = new BorderPane();
		searchBox = new VBox();
		
		prefs = Preferences.userNodeForPackage(farmersMarkets.FarmersMarkets.class);
		loaded_markets = prefs.get("loaded_markets", "nope").equals("yup");
		loaded_locations = prefs.get("loaded_locations", "nope").equals("yup");
		
		loading = new VBox();
		loading.setAlignment(Pos.CENTER);
		loading.setStyle("-fx-background-color: white;");
		Label loadingLabel = new Label("Loading markets...\n");
		ScrollPane progressPane = new ScrollPane();
		Label progressLabel = new Label();
		progressProperty = new SimpleStringProperty();
		progressProperty.setValue("Application started.\nInitializing database...\n");
		progressLabel.textProperty().bind(progressProperty);
		progressPane.setContent(progressLabel);
		progressPane.setStyle("-fx-background-color: grey; -fx-border-color: brown");
		progressPane.setPrefWidth(500);
		progressPane.setPrefHeight(300);
		progressPane.setMaxWidth(500);
		progressPane.setMaxHeight(300);
		loadingLabel.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-text-fill: brown");
		
		loading.getChildren().addAll(loadingLabel, progressPane);
		
		scene = new Scene(loading, 800, 450);
		stage.setTitle("Farmers Markets");
		stage.setScene(scene);
		stage.setMinWidth(600);
		stage.setMinHeight(300);
		stage.show();
		
		setUpThread = new Thread(() -> {
			if ( !loaded_markets ) {
				dbi.initializeDatabase();
				Platform.runLater(() -> {
					progressProperty.set(progressProperty.get() + "Finished initializing database.\nParsing market data...\n");
				});
				parseMarketsCSV();
				prefs.put("loaded_markets", "yup");
				Platform.runLater(() -> {
					progressProperty.set(progressProperty.get() + "Finished storing all market data.\n");
				});
			}
			if ( !loaded_locations ) {
				if ( loaded_markets ) {
					dbi.initializeDatabase();
					Platform.runLater(() -> {
						progressProperty.set(progressProperty.get() + "Finished initializing database.\n");
					});
				}
				Platform.runLater(() -> {
					progressProperty.set(progressProperty.get() + "Parsing location data using " + NUM_THREADS + " threads... (this may take a while)\n");
				});
				parseLocationsCSV();
				Platform.runLater(() -> {
					progressProperty.set(progressProperty.get() + "Finished storing all location data.\n");
				});
				prefs.put("loaded_locations", "yup");
			}
			Platform.runLater(() -> {
				progressProperty.set(progressProperty.get() + "Loading markets...\n");
			});
			
			Tab marketsTab = new Tab("Browse Markets");
			ObservableList<MarketItem> list = dbm.getMarketItems(openMarketTab);
			if ( list.size() == 0 ) {
				prefs.put("loaded_markets", "nope");
			}
			Platform.runLater(() -> {
				markets = new ListView<>(list);
				progressProperty.set(progressProperty.get() + "Loaded all markets.\nSwitching screens...\n");
			});
			
			buildSearchBox();
			
			home.setCenter(markets);
			home.setRight(searchBox);
			
			marketsTab.setClosable(false);
			marketsTab.setContent(home);
			
			root.getTabs().add(marketsTab);
			root.getSelectionModel().select(0);
			root.setStyle("-fx-background-color: white;");
			
			Platform.runLater(() -> {
				scene = new Scene(root, 800, 450);
				stage.setScene(scene);
				stage.show();
			});
		});
		setUpThread.start();
		
		stage.setOnCloseRequest(e -> {
			if ( setUpThread != null && setUpThread.isAlive() ) {
            	setUpThread.interrupt();
			}
			if ( locationThreads != null ) {
				for ( int i = 0; i < NUM_THREADS; i++ ) {
					if ( locationThreads[i] != null && locationThreads[i].isAlive() ) {
						locationThreads[i].interrupt();
					}
				}
			}
        });
	}
	
	private ChangeListener<String> numberListener = new ChangeListener<String>() {
	    @Override
	    public void changed(ObservableValue<? extends String> observable, String old_value, String new_value) {
	    	StringProperty textProperty = (StringProperty) observable ;
	    	TextField textField = (TextField) textProperty.getBean();
	        if (!new_value.matches("\\d*")) {
	            textField.setText(new_value.replaceAll("[^\\d]", ""));
	        }
	    }
	};
	
	private EventHandler<MouseEvent> openMarketTab = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
        	/* open new tab */
        	MarketItem selected = ( MarketItem )event.getSource();
        	Tab marketTab = buildMarketTab(selected.getFMID(), selected.getIndex());
        	root.getTabs().add(marketTab);
        	root.getSelectionModel().select(marketTab);
        }
    };
    
    private void buildSearchBox() {
    	searchBox = new VBox();
    	
		Label searchLabel = new Label("Search by Location");
		HBox cityBox = new HBox();
		Label cityLabel = new Label("City: ");
		TextField cityField = new TextField("");
		cityBox.getChildren().addAll(cityLabel, cityField);
		HBox stateBox = new HBox();
		Label stateLabel = new Label("State (ex. NY): ");
		TextField stateField = new TextField("");
		stateBox.getChildren().addAll(stateLabel, stateField);
		HBox zipBox = new HBox();
		Label zipLabel = new Label("Zip code: ");
		TextField zipField = new TextField("");
		zipField.textProperty().addListener(numberListener);
		zipBox.getChildren().addAll(zipLabel, zipField);
		
		HBox distanceBox = new HBox();
		Label distanceLabel = new Label("Distance (miles): ");
		TextField distanceField = new TextField("");
		distanceBox.getChildren().addAll(distanceLabel, distanceField);
		HBox buttonBox = new HBox();
		Button cityStateButton = new Button("Search by\nCity and State");
		Button zipButton = new Button("Search by\nZip Code");
		
		Button resetButton = new Button("Reset Market Search");
		
		cityStateButton.setOnAction(e -> {
			String city = cityField.getText().toLowerCase();
			String state = stateField.getText().toLowerCase();
			if ( !isPositiveNumeric(distanceField.getText()) || distanceField.getText().equals("") ) {
				/* alert that distance must be a positive number */
				Alert alert = new Alert(AlertType.ERROR, "Distance is missing or invalid (must be positive).");
				Optional<ButtonType> error_result = alert.showAndWait();
				if ( error_result.isPresent() && error_result.get() == ButtonType.OK) {
					return;
				}
			}
			double distance = Double.parseDouble(distanceField.getText());
			ObservableList<MarketItem> sorted_list = dbm.cityStateMarketItems(city, state, distance, openMarketTab);
			if ( sorted_list == null || sorted_list.size() == 0 ) {
				/* alert that no markets matched their search */
				Alert alert = new Alert(AlertType.ERROR, "No markets with a valid location matched your search.");
				Optional<ButtonType> error_result = alert.showAndWait();
				if ( error_result.isPresent() && error_result.get() == ButtonType.OK) {
					return;
				}
			}
			Platform.runLater(() -> {
				markets.setItems(sorted_list);
			});
		});
		zipButton.setOnAction(e -> {
			if ( zipField.getText().equals("") ) {
				/* alert that zip_code is required */
				Alert alert = new Alert(AlertType.ERROR, "Zip code is required to search by zip code.");
				Optional<ButtonType> error_result = alert.showAndWait();
				if ( error_result.isPresent() && error_result.get() == ButtonType.OK) {
					return;
				}
			}
			int zip_code = Integer.parseInt(zipField.getText());
			if ( !isPositiveNumeric(distanceField.getText()) || distanceField.getText().equals("") ) {
				/* alert that distance must be a positive number and is required */
				Alert alert = new Alert(AlertType.ERROR, "Distance is missing or invalid (must be positive).");
				Optional<ButtonType> error_result = alert.showAndWait();
				if ( error_result.isPresent() && error_result.get() == ButtonType.OK) {
					return;
				}
			}
			double distance = Double.parseDouble(distanceField.getText());
			ObservableList<MarketItem> sorted_list = dbm.zipMarketItems(zip_code, distance, openMarketTab);
			if ( sorted_list == null || sorted_list.size() == 0 ) {
				/* alert that no markets matched their search */
				Alert alert = new Alert(AlertType.ERROR, "No markets with a valid location matched your search.");
				Optional<ButtonType> error_result = alert.showAndWait();
				if ( error_result.isPresent() && error_result.get() == ButtonType.OK) {
					return;
				}
			}
			Platform.runLater(() -> {
				markets.setItems(sorted_list);
			});
		});
		buttonBox.getChildren().addAll(cityStateButton, zipButton);
		
		resetButton.setOnAction(e -> {
			cityField.clear();
			stateField.clear();
			zipField.clear();
			distanceField.clear();
			ObservableList<MarketItem> reset_list = dbm.getMarketItems(openMarketTab);
			Platform.runLater(() -> {
				markets.setItems(reset_list);
			});
		});

		searchBox.setAlignment(Pos.CENTER);
		searchBox.setPadding(new Insets(0, 50, 0, 0));
		cityBox.setAlignment(Pos.CENTER_RIGHT);
		stateBox.setAlignment(Pos.CENTER_RIGHT);
		zipBox.setAlignment(Pos.CENTER_RIGHT);
		distanceBox.setAlignment(Pos.CENTER_RIGHT);
		buttonBox.setAlignment(Pos.CENTER_RIGHT);
		
		searchBox.getChildren().addAll(searchLabel, cityBox, stateBox, zipBox, distanceBox, buttonBox);
    }
    
    private void parseMarketsCSV() {
        File file = new File("src/main/resources/farmers_markets_csv.csv");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            /* skip the header line */
            br.readLine();

            while ((line = br.readLine()) != null) {
                List<String> values = parseCSVLine(line);

                for (int i = 0; i < values.size(); i++) {
                    values.set(i, values.get(i).strip());
                }

                /* assume fmid is okay */
                int fmid = Integer.parseInt(values.get(0));
                /* name can be anything */
                String name = values.get(1);

                /* links can be anything */
                ArrayList<String> links = new ArrayList<>(Arrays.asList(
                        values.get(2), values.get(3), values.get(4), values.get(5), values.get(6)
                ));

                /* street, city, county, and state can be anything */
                String street = values.get(7);
                String city = values.get(8);
                String county = values.get(9);
                String state = values.get(10);
                /* zip_code must be a natural number */
                String zip_code = values.get(11);
                if (!zip_code.matches("[1-9]\\d*")) {
                    zip_code = "unknown_zip_code";
                }

                /* schedule can be anything */
                ArrayList<String> schedule = new ArrayList<>(Arrays.asList(
                        values.get(12), values.get(13), values.get(14), values.get(15),
                        values.get(16), values.get(17), values.get(18), values.get(19)
                ));

                /* location must be numeric */
                String location = "";
                boolean long_valid = isNumeric(values.get(20));
                boolean lat_valid = isNumeric(values.get(21));
                if (long_valid && lat_valid) {
                    location = values.get(20) + "," + values.get(21);
                } else if (long_valid) {
                    location = "Longitude: " + values.get(20);
                } else if (lat_valid) {
                    location = "Latitude = " + values.get(21);
                } else {
                    location = "Unknown";
                }

                /* tags */
                ArrayList<String> tags = new ArrayList<>();
                String[] boolean_columns = {"credit", "WIC", "WICcash", "SFMNP", "SNAP", "Organic",
                        "Bakedgood", "Cheese", "Crafts", "Flowers", "Eggs",
                        "Seafood", "Herbs", "Vegtables", "Honey", "Jams",
                        "Maple", "Meat", "Nursery", "Nuts", "Plants", "Poultry",
                        "Prepared", "Soap", "Trees", "Wine", "Coffee", "Beans",
                        "Fruits", "Grains", "Juices", "Mushrooms", "PetFood",
                        "Tofu", "WildHarvested"};

                for (int i = 0; i < boolean_columns.length; i++) {
                    if (!values.get(22 + i).isEmpty() && values.get(22 + i).contains("Y")) {
                        tags.add(boolean_columns[i]);
                    }
                }
                /* updated can be anything */
                String updated = values.get(58);

                /* add market to database */
                dbm.addMarket(fmid, name, street, city, state, zip_code, county, links, schedule, tags, location, updated);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void parseLocationsCSV() {
    	locationThreads = new Thread[NUM_THREADS];
    	int num_per = 42750 / NUM_THREADS;
    	for ( int i = 0; i < NUM_THREADS; i++ ) {
    		locationThreads[i] = new Thread(new ArgRunnable(i, num_per));
    		locationThreads[i].start();
    		Platform.runLater(() -> {
				progressProperty.set(progressProperty.get() + "		Location thread started.\n");
    		});
    	}
    	for ( int i = 0; i < NUM_THREADS; i++ ) {
    		try {
				locationThreads[i].join();
				Platform.runLater(() -> {
					progressProperty.set(progressProperty.get() + "		Location thread joined.\n");
	    		});
			} catch (InterruptedException e) {
			}
    	}
    }
    
    private void threadParse(int start, int num) {
    	File file = new File("src/main/resources/locations_csv.csv");
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            for ( int i = start; i < start + num; i++ ) {
            	br.readLine();
            }
            
            int num_read = 0;
            while ((line = br.readLine()) != null && num_read < num) {
                List<String> values = parseCSVLine(line);

                for (int i = 0; i < values.size(); i++) {
                    values.set(i, values.get(i).strip());
                }
                
                int zip_code = Integer.parseInt(values.get(0));
                String city = values.get(3);
                String state = values.get(4);
                if ( !isNumeric(values.get(1)) || !isNumeric(values.get(2)) ) {
                	continue;
                }
                double longitude = Double.parseDouble(values.get(2));
                double latitude = Double.parseDouble(values.get(1));

                /* add location to database */
                dbm.addLocation(city, state, zip_code, longitude, latitude);
                num_read++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	private List<String> parseCSVLine(String line) {
	    List<String> values = new ArrayList<>();
	    StringBuilder current = new StringBuilder();
	    boolean in_quotes = false;
	
	    for (char c : line.toCharArray()) {
	        if (c == '\"') {
	        	in_quotes = !in_quotes;
	        } else if (c == ',' && !in_quotes) {
	            values.add(current.toString().strip());
	            current.setLength(0);
	        } else {
	            current.append(c);
	        }
	    }
	
	    values.add(current.toString().strip());
	
	    return values;
	}
	
	private static boolean isPositiveNumeric(String str) {
		return str.matches("\\d+(\\.\\d+)?");
	}

    private static boolean isNumeric(String str) {
    	  return str.matches("-?\\d+(\\.\\d+)?");
    }
	
	private Tab buildMarketTab(int fmid, int index) {
		/* get market item from database */
		Market market = dbm.getMarket(fmid);
		
		Tab marketTab = new Tab(market.getName());
		rating = 1;
		
		ScrollPane scrollPane = new ScrollPane();
		VBox content = new VBox();
		content.setPadding(new Insets(10, 20, 10, 20)); 
		Label titleLabel = new Label(market.getName());
		Label addressLabel = new Label(market.getStreet() + ", " + market.getCity() + ", " + market.getState() + " " + market.getZipCode() + "\n");
		Label countyLabel = new Label("County: " + market.getCounty());
		Label scheduleLabel = new Label("Schedule");
		Label s1DateLabel = new Label("Season 1 Date: " + market.date(1));
		Label s1TimeLabel = new Label("Season 1 Time: " + market.time(1));
		Label s2DateLabel = new Label("Season 2 Date: " + market.date(2));
		Label s2TimeLabel = new Label("Season 2 Time: " + market.time(2));
		Label s3DateLabel = new Label("Season 3 Date: " + market.date(3));
		Label s3TimeLabel = new Label("Season 3 Time: " + market.time(3));
		Label s4DateLabel = new Label("Season 4 Date: " + market.date(4));
		Label s4TimeLabel = new Label("Season 4 Time: " + market.time(4) + "\n");
		Hyperlink websiteLink = new Hyperlink();
		Hyperlink facebookLink = new Hyperlink();
		Hyperlink twitterLink = new Hyperlink();
		Hyperlink youtubeLink = new Hyperlink();
		Hyperlink otherMediaLink = new Hyperlink();
		Label locationLabel = new Label("Location: " + market.getLocation());
		Label tagsLabel = new Label("Tags: " + market.stringTags());
		Label updateTimeLabel = new Label("Update time: " + market.getUpdateTime());
		
		websiteLink.setText("Website: " + market.getWebsite());
		facebookLink.setText("Facebook: " + market.getFacebook());
		twitterLink.setText("Twitter: " + market.getTwitter());
		youtubeLink.setText("Youtube: " + market.getYoutube());
		otherMediaLink.setText("Other Media: " + market.getOtherMedia());

		websiteLink.setOnAction(e -> {
		    if(Desktop.isDesktopSupported())
		    {
		        try {
		            Desktop.getDesktop().browse(new URI(market.getWebsite()));
		        } catch (IOException e1) {
		            e1.printStackTrace();
		        } catch (URISyntaxException e1) {
		            e1.printStackTrace();
		        }
		    }
		});
		facebookLink.setOnAction(e -> {
		    if(Desktop.isDesktopSupported())
		    {
		        try {
		            Desktop.getDesktop().browse(new URI(market.getFacebook()));
		        } catch (IOException e1) {
		            e1.printStackTrace();
		        } catch (URISyntaxException e1) {
		            e1.printStackTrace();
		        }
		    }
		});
		twitterLink.setOnAction(e -> {
		    if(Desktop.isDesktopSupported())
		    {
		        try {
		            Desktop.getDesktop().browse(new URI(market.getTwitter()));
		        } catch (IOException e1) {
		            e1.printStackTrace();
		        } catch (URISyntaxException e1) {
		            e1.printStackTrace();
		        }
		    }
		});
		youtubeLink.setOnAction(e -> {
		    if(Desktop.isDesktopSupported())
		    {
		        try {
		            Desktop.getDesktop().browse(new URI(market.getYoutube()));
		        } catch (IOException e1) {
		            e1.printStackTrace();
		        } catch (URISyntaxException e1) {
		            e1.printStackTrace();
		        }
		    }
		});
		otherMediaLink.setOnAction(e -> {
		    if(Desktop.isDesktopSupported())
		    {
		        try {
		            Desktop.getDesktop().browse(new URI(market.getOtherMedia()));
		        } catch (IOException e1) {
		            e1.printStackTrace();
		        } catch (URISyntaxException e1) {
		            e1.printStackTrace();
		        }
		    }
		});
		
		titleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold");
		scheduleLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold");
		
		VBox reviewSection = createReviewSection(fmid, index);
		
		content.getChildren().addAll(titleLabel, addressLabel, countyLabel, scheduleLabel, s1DateLabel, 
				s1TimeLabel, s2DateLabel, s2TimeLabel, s3DateLabel, s3TimeLabel, s4DateLabel, 
				 s4TimeLabel, websiteLink, facebookLink, twitterLink, youtubeLink, otherMediaLink, 
				 locationLabel, tagsLabel, updateTimeLabel, reviewSection);
		scrollPane.setContent(content);
		
		marketTab.setContent(scrollPane);
		return marketTab;
	}
	
	private VBox createReviewSection(int fmid, int index) {
		VBox reviewSection = new VBox();
		
		ListView<Review> reviews = new ListView<>(dbm.getReviews(fmid));
		
		Label leaveReviewLabel = new Label("\n\nLeave a review for this market");
		Label ratingLabel = new Label("Rating:");
		HBox ratingBox = new HBox();
		ImageView star = new ImageView();
		try (FileInputStream inputstream = new FileInputStream("src/main/resources/star_filled.png")) {
			Image image = new Image(inputstream); 
			star.setImage(image);
			star.setFitWidth(40);
	        star.setFitHeight(40);
		}
		catch (Exception e) {
			System.err.println("Failed to load image");
		}
	    ratingBox.getChildren().add(star);
		for (int i = 1; i < 5; i++) {
			try (FileInputStream inputstream = new FileInputStream("src/main/resources/star_empty.png")) {
				Image image = new Image(inputstream); 
			    star = new ImageView(image);
				star.setFitWidth(40);
		        star.setFitHeight(40);
			}
			catch (Exception e) {
				System.err.println("Failed to load image");
			}
			star.setOnMouseClicked(event -> {
			    rating = ratingBox.getChildren().indexOf(event.getSource()) + 1;
			    updateStars(ratingBox, rating);
			});
		    ratingBox.getChildren().add(star);
		}
		Label usernameLabel = new Label("Display name:");
		TextField usernameField = new TextField("");
		Label commentLabel = new Label("Comment:");
		TextField commentField = new TextField("");
		Button postReviewButton = new Button("Post Review");
		
		Label reviewsLabel = new Label("\nReviews:");
		reviewsLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold");
		
		leaveReviewLabel.setStyle("-fx-font-weight: bold");
		postReviewButton.setOnAction(e -> {
			String username = usernameField.getText();
			if ( username.equals("") ) {
				/* alert user username is required */
				Alert alert = new Alert(AlertType.ERROR, "A display name is required to post a review.");
				Optional<ButtonType> error_result = alert.showAndWait();
				if ( error_result.isPresent() && error_result.get() == ButtonType.OK) {
					return;
				}
			}
			String comment = commentField.getText();
			/* add review to database */
			Review review = dbm.addReview(fmid, rating, username, comment, LocalDateTime.now());
			/* update this market's MarketItem in the browse markets tab */
			MarketItem market_item = markets.getItems().get(index);
			Platform.runLater(() -> {
				float average_rating = dbm.updateAverageRating(fmid);
			    market_item.setAverageRating(average_rating);
			});
			
			if ( review == null ) {
				/* alert user 1 review per market */
				Alert alert = new Alert(AlertType.ERROR, "Each display name may only post one review per market.");
				Optional<ButtonType> error_result = alert.showAndWait();
				if ( error_result.isPresent() && error_result.get() == ButtonType.OK) {
					return;
				}
			}
			else {
				/* update reviews */
				Platform.runLater(() -> {
					reviews.getItems().add(0, review);
				});
			}
		});
		
		reviewSection.getChildren().addAll(leaveReviewLabel, ratingLabel, ratingBox, usernameLabel, 
				usernameField, commentLabel, commentField, postReviewButton, reviewsLabel, reviews);
		return reviewSection;
	}
	
	private void updateStars(HBox box, int rating) {
	    for (int i = 0; i < box.getChildren().size(); i++) {
	    	
	        ImageView star = (ImageView) box.getChildren().get(i);
	        star.setFitWidth(40);
	        star.setFitHeight(40);
	        if (i < rating) {
				try (FileInputStream inputstream = new FileInputStream("src/main/resources/star_filled.png")) {
					Image image = new Image(inputstream); 
					star.setImage(image);
				}
				catch (Exception e) {
					System.err.println("Failed to load image");
				}
	            
	        } else {
	        	try (FileInputStream inputstream = new FileInputStream("src/main/resources/star_empty.png")) {
					Image image = new Image(inputstream); 
		            star.setImage(image);
				}
				catch (Exception e) {
					System.err.println("Failed to load image");
				}
	        }
	    }
	}
	
	private class ArgRunnable implements Runnable {
		
		private int index;
		private int num_per;
		
		public ArgRunnable(int i, int np) {
			this.index = i;
			this.num_per = np;
		}
	
		public void run() {
			threadParse(index * num_per, num_per);
		}
	}
}