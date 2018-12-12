package link.mgiannone.githubchallenge.data.model;

import com.google.gson.annotations.SerializedName;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import link.mgiannone.githubchallenge.data.Config;

@Entity(tableName = Config.REPO_TABLE_NAME)
public class Repo {


	@SerializedName("propertyID")
	@PrimaryKey
	private int id;  //since is nullable we use an Integer

	@SerializedName("title") //json name
	private String title;

	@SerializedName("snippet")
	private String snippet;

	@SerializedName("lat")
	private double lat;

	@SerializedName("lng")
	private double lng;

	@SerializedName("city")
	private String city;

	@SerializedName("typology")
	private int typology;

	@SerializedName("category")
	private int category;

	@SerializedName("code")
	private String code;

	@SerializedName("shortDesc")
	private String shortDescription;

	@SerializedName("longDesc")
	private String longDescription;

	@SerializedName("price")
	private double price;

	@SerializedName("priceCategory")
	private int priceCategory;

	@SerializedName("roomsNO")
	private int roomsNO;

	@SerializedName("bedroomsNO")
	private int bedroomsNO;

	@SerializedName("bathroomsNO")
	private int bathroomsNO;

	@SerializedName("hasParking")
	private boolean hasParking;

	@SerializedName("area")
	private double area;

	@SerializedName("energyClass")
	private String energyClass;

	@SerializedName("showIntoWeb")
	private boolean showIntoWeb;

	@SerializedName("highlightIntoWeb")
	private boolean highlightIntoWeb;

	@SerializedName("dateFirstAdded")
	private String dateFirstAdded;

	@SerializedName("dateLastUpdated")
	private String dateLastUpdated;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSnippet() {
		return snippet;
	}

	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getTypology() {
		return typology;
	}

	public void setTypology(int typology) {
		this.typology = typology;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getLongDescription() {
		return longDescription;
	}

	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getPriceCategory() {
		return priceCategory;
	}

	public void setPriceCategory(int priceCategory) {
		this.priceCategory = priceCategory;
	}

	public int getRoomsNO() {
		return roomsNO;
	}

	public void setRoomsNO(int roomsNO) {
		this.roomsNO = roomsNO;
	}

	public int getBedroomsNO() {
		return bedroomsNO;
	}

	public void setBedroomsNO(int bedroomsNO) {
		this.bedroomsNO = bedroomsNO;
	}

	public int getBathroomsNO() {
		return bathroomsNO;
	}

	public void setBathroomsNO(int bathroomsNO) {
		this.bathroomsNO = bathroomsNO;
	}

	public boolean isHasParking() {
		return hasParking;
	}

	public void setHasParking(boolean hasParking) {
		this.hasParking = hasParking;
	}

	public double getArea() {
		return area;
	}

	public void setArea(double area) {
		this.area = area;
	}

	public String getEnergyClass() {
		return energyClass;
	}

	public void setEnergyClass(String energyClass) {
		this.energyClass = energyClass;
	}

	public boolean isShowIntoWeb() {
		return showIntoWeb;
	}

	public void setShowIntoWeb(boolean showIntoWeb) {
		this.showIntoWeb = showIntoWeb;
	}

	public boolean isHighlightIntoWeb() {
		return highlightIntoWeb;
	}

	public void setHighlightIntoWeb(boolean highlightIntoWeb) {
		this.highlightIntoWeb = highlightIntoWeb;
	}

	public String getDateFirstAdded() {
		return dateFirstAdded;
	}

	public void setDateFirstAdded(String dateFirstAdded) {
		this.dateFirstAdded = dateFirstAdded;
	}

	public String getDateLastUpdated() {
		return dateLastUpdated;
	}

	public void setDateLastUpdated(String dateLastUpdated) {
		this.dateLastUpdated = dateLastUpdated;
	}
}

