package farmersMarkets;

import java.util.ArrayList;

public class Market {
	
	private int fmid;
	private String name;
	private String street;
	private String city;
	private String state;
	private String zip_code;
	private String county;
	private String website;
	private String facebook;
	private String twitter;
	private String youtube;
	private String other_media;
	private String[] schedule;
	private ArrayList<String> tags;
	private String location;
	private String updated;
	
	/**
	 * Market's default constructor.
	 */
	public Market() {
		fmid = 0;
		name = "Default market";
		street = "";
		city = "";
		state = "";
		zip_code = "";
		county = "";
		website = "";
		facebook = "";
		twitter = "";
		youtube = "";
		other_media = "";
		schedule = new String[]{"", "", "", "", "", "", "", ""};
		tags = new ArrayList<String>();
		location = "";
		updated = "";
	}
	
	/**
	 * Market's comprehensive contructor;
	 * @param fmid		fmid
	 * @param name		name
	 * @param street	street
	 * @param city		city
	 * @param state		state
	 * @param zip_code	zip_code
	 * @param county	county
	 * @param links		all social media links
	 * @param schedule	4-season schedule
	 * @param tags		all tags
	 * @param location	this Market's location
	 * @param updated	when this information was recorded
	 */
	public Market(int fmid, String name, String street, String city,
			String state, String zip_code, String county, ArrayList<String> links,
			ArrayList<String> schedule, ArrayList<String> tags, String location,
			String updated) {
		if ( links.size() != 5 || schedule.size() != 8 ) {
			return;
		}
		
		this.fmid = fmid;
		this.name = name;
		this.street = street;
		this.city = city;
		this.state = state;
		this.zip_code = zip_code;
		this.county = county;
		/* verify and clean up links */
		this.website = links.get(0);
		this.facebook = links.get(1);
		this.twitter = links.get(2);
		this.youtube = links.get(3);
		this.other_media = links.get(4);
		this.schedule = new String[8];
		for ( int i = 0; i < 8; i++ ) {
			this.schedule[i] = schedule.get(i);
		}
		this.tags = new ArrayList<String>();
		for ( String tag : tags ) {
			this.tags.add(tag);
		}
		this.location = location;
		this.updated = updated;
	}
	
	/**
	 * Returns this market's id.
	 * @return	fmid
	 */
	public int getFMID() {
		return this.fmid;
	}
	
	/**
	 * Returns this market's name.
	 * @return	name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the street this market is on.
	 * @return	street
	 */
	public String getStreet() {
		return this.street;
	}
	
	/**
	 * Returns the city this market is in.
	 * @return	city
	 */
	public String getCity() {
		return this.city;
	}
	
	/**
	 * Returns the state this market is in.
	 * @return	state
	 */
	public String getState() {
		return this.state;
	}
	
	/**
	 * Returns this market's zip code.
	 * @return	zip_code
	 */
	public String getZipCode() {
		return this.zip_code;
	}
	
	/**
	 * Returns the county this market is in.
	 * @return	county
	 */
	public String getCounty() {
		return this.county;
	}
	
	/**
	 * Returns the range of dates this market is
	 * open during the season input.
	 * @param season	the season to get the dates for.
	 * @return			the dates for the season input.
	 */
	public String date(int season) {
		return this.schedule[2 * (season - 1)];
	}
	
	/**
	 * Returns the hours as a string.
	 * @param season	the season to return the hours for.
	 * @return			the hours for the season input.
	 */
	public String time(int season) {
		return this.schedule[2 * (season - 1) + 1];
	}
	
	/**
	 * Returns the location string.
	 * @return	location
	 */
	public String getLocation() {
		return this.location;
	}
	
	/**
	 * Returns the tags ArrayList as a ", " delimited string.
	 * @return	tags as a ", " delimited string.
	 */
	public String stringTags() {
		String string_tags = "";
		if ( tags.size() > 0 ) {
			string_tags = tags.get(0);
		}
		for ( int i = 1; i < tags.size(); i++ ) {
			string_tags = string_tags + ", " + tags.get(i);
		}
		
		return string_tags;
	}
	
	/**
	 * Returns the time this information was updated as a string.
	 * @return	updated
	 */
	public String getUpdateTime() {
		return this.updated;
	}
	
	/**
	 * Returns the website string.
	 * @return	website
	 */
	public String getWebsite() {
		return this.website;
	}
	
	/**
	 * Returns the Facebook string.
	 * @return	facebook
	 */
	public String getFacebook() {
		return this.facebook;
	}
	
	/**
	 * Returns the Twitter string.
	 * @return	twitter
	 */
	public String getTwitter() {
		return this.twitter;
	}
	
	/**
	 * Returns the Youtube string.
	 * @return	youtube
	 */
	public String getYoutube() {
		return this.youtube;
	}
	
	/**
	 * Returns the other media string.
	 * @return	other_media
	 */
	public String getOtherMedia() {
		return this.other_media;
	}
}