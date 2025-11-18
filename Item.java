import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Item {

  // Identity
  private String saveTo = "Items.txt"; // file path
  private String sku;
  private BarcodeType barcodeType;
  private String barcode;
  private String plu;
  private Optional<String> serialNumber = Optional.empty();

  // Description
  private String name;
  private BigDecimal price;
  private String department;
  private String category;
  private Optional<String> subCategory = Optional.empty();
  private String vendor;

  // Physical
  private String dimensionsUnit;
  private String weightUnit;
  private double weight;
  private double width;
  private double height;
  private double length;

  // Storage
  private List<String> warehouseLocations = new ArrayList<>();
  private List<String> floorLocations = new ArrayList<>();
  private String storageType;
  private Optional<String> storageNotes = Optional.empty();
  private boolean isHazardous = false;

  // Metadata
  private LocalDateTime dateAdded;
  private LocalDateTime lastUpdated;
  private String updatedById;

  private Filer filer = new Filer(); // use Filer instance

  // Constructor
  public Item(String sku, BarcodeType barcodeType, String barcode, String plu, String name,
              BigDecimal price, String department, String category, String vendor,
              boolean isHazardous, String updatedById) {
    this.sku = sku;
    this.barcodeType = barcodeType;
    this.barcode = barcode;
    this.plu = plu;
    this.name = name;
    this.price = price;
    this.department = department;
    this.category = category;
    this.vendor = vendor;
    this.isHazardous = isHazardous;
    this.updatedById = updatedById;

    this.serialNumber = Optional.empty();
    this.subCategory = Optional.empty();
    this.storageNotes = Optional.empty();

    this.warehouseLocations = new ArrayList<>();
    this.floorLocations = new ArrayList<>();

    this.dateAdded = LocalDateTime.now();
    this.lastUpdated = LocalDateTime.now();
  }

  // sku|barcodeType|barcode|plu|name|price|serialNumber|department|category|subCategory|vendor|dimensionsUnit|weightUnit|weight|width|height|length|warehouseLocations(comma-separated)|floorLocations(comma-separated)|storageType|storageNotes|isHazardous|dateAdded|lastUpdated|updatedById
  private String serializeItem() {
    return this.getSku() + "|" +
      this.getBarcodeType() + "|" +
      this.getBarcode() + "|" +
      this.getPlu() + "|" +
      this.getName() + "|" +
      this.getPrice() + "|" +
      this.getSerialNumber().orElse("") + "|" +
      this.getDepartment() + "|" +
      this.getCategory() + "|" +
      this.getSubCategory().orElse("") + "|" +
      this.getVendor() + "|" +
      this.getDimensionsUnit() + "|" +
      this.getWeightUnit() + "|" +
      this.getWeight() + "|" +
      this.getWidth() + "|" +
      this.getHeight() + "|" +
      this.getLength() + "|" +
      String.join(",", this.getWarehouseLocations()) + "|" +
      String.join(",", this.getFloorLocations()) + "|" +
      this.getStorageType() + "|" +
      this.getStorageNotes().orElse("") + "|" +
      this.isHazardous() + "|" +
      this.getDateAdded() + "|" +
      this.getLastUpdated() + "|" +
      this.getUpdatedById();
  }

  private Item deserializeItem(String line) {
    String[] parts = line.split("\\|", -1); // -1 keeps empty strings
    Item item = new Item(
      parts[0], // sku
      BarcodeType.valueOf(parts[1]),
      parts[2], // barcode
      parts[3], // plu
      parts[4], // name
      new BigDecimal(parts[5]), // price
      parts[7], // department
      parts[8], // category
      parts[10], // vendor
      Boolean.parseBoolean(parts[21]), // isHazardous
      parts[24] // updatedById
    );

    item.setSerialNumber(parts[6].isEmpty() ? Optional.empty() : Optional.of(parts[6]));
    item.setSubCategory(parts[9].isEmpty() ? Optional.empty() : Optional.of(parts[9]));
    item.setDimensionsUnit(parts[11]);
    item.setWeightUnit(parts[12]);
    item.setWeight(Double.parseDouble(parts[13]));
    item.setWidth(Double.parseDouble(parts[14]));
    item.setHeight(Double.parseDouble(parts[15]));
    item.setLength(Double.parseDouble(parts[16]));
    item.setWarehouseLocations(parts[17].isEmpty() ? new ArrayList<>() : new ArrayList<>(List.of(parts[17].split(","))));
    item.setFloorLocations(parts[18].isEmpty() ? new ArrayList<>() : new ArrayList<>(List.of(parts[18].split(","))));
    item.setStorageType(parts[19]);
    item.setStorageNotes(parts[20].isEmpty() ? Optional.empty() : Optional.of(parts[20]));
    item.setDateAdded(LocalDateTime.parse(parts[22]));
    item.setLastUpdated(LocalDateTime.parse(parts[23]));

    return item;
  }

  private void copyFrom(Item other) {
    this.barcodeType = other.barcodeType;
    this.barcode = other.barcode;
    this.plu = other.plu;
    this.name = other.name;
    this.price = other.price;
    this.serialNumber = other.serialNumber;
    this.department = other.department;
    this.category = other.category;
    this.subCategory = other.subCategory;
    this.vendor = other.vendor;
    this.dimensionsUnit = other.dimensionsUnit;
    this.weightUnit = other.weightUnit;
    this.weight = other.weight;
    this.width = other.width;
    this.height = other.height;
    this.length = other.length;
    this.warehouseLocations = other.warehouseLocations;
    this.floorLocations = other.floorLocations;
    this.storageType = other.storageType;
    this.storageNotes = other.storageNotes;
    this.isHazardous = other.isHazardous;
    this.dateAdded = other.dateAdded;
    this.lastUpdated = other.lastUpdated;
    this.updatedById = other.updatedById;
  }

  public void loadItem() {
    String content = filer.readFile(this.saveTo);
    String[] lines = content.split("\n");
    for (String line : lines) {
      if (line.startsWith(this.sku + "|")) {
        Item loaded = deserializeItem(line);
        copyFrom(loaded);
        break;
      }
    }
  }

  public void saveItem() {
    this.lastUpdated = LocalDateTime.now();
    String content = filer.readFile(this.saveTo);
    StringBuilder newContent = new StringBuilder();
    boolean found = false;

    for (String line : content.split("\n")) {
      if (line.startsWith(this.sku + "|")) {
        newContent.append(this.serializeItem()).append("\n");
        found = true;
      } else {
        newContent.append(line).append("\n");
      }
    }

    if (!found) {
      newContent.append(this.serializeItem()).append("\n");
    }

    filer.writeToFile(this.saveTo, newContent.toString());
  }

  // Getters
  public String getSaveToPath() { return saveTo; }
  public String getSku() { return sku; }
  public BarcodeType getBarcodeType() { return barcodeType; }
  public String getBarcode() { return barcode; }
  public String getPlu() { return plu; }
  public Optional<String> getSerialNumber() { return serialNumber; }

  public String getName() { return name; }
  public BigDecimal getPrice() { return price; }
  public String getDepartment() { return department; }
  public String getCategory() { return category; }
  public Optional<String> getSubCategory() { return subCategory; }
  public String getVendor() { return vendor; }

  public String getDimensionsUnit() { return dimensionsUnit; }
  public String getWeightUnit() { return weightUnit; }
  public double getWeight() { return weight; }
  public double getWidth() { return width; }
  public double getHeight() { return height; }
  public double getLength() { return length; }

  public List<String> getWarehouseLocations() { return warehouseLocations; }
  public List<String> getFloorLocations() { return floorLocations; }
  public String getStorageType() { return storageType; }
  public Optional<String> getStorageNotes() { return storageNotes; }
  public boolean isHazardous() { return isHazardous; }

  public LocalDateTime getDateAdded() { return dateAdded; }
  public LocalDateTime getLastUpdated() { return lastUpdated; }
  public String getUpdatedById() { return updatedById; }

  // Setters
  public void setSaveToPath(String saveTo) { this.saveTo = saveTo; }
  public void setSku(String sku) { this.sku = sku; }
  public void setBarcodeType(BarcodeType barcodeType) { this.barcodeType = barcodeType; }
  public void setBarcode(String barcode) { this.barcode = barcode; }
  public void setPlu(String plu) { this.plu = plu; }
  public void setSerialNumber(Optional<String> serialNumber) { this.serialNumber = serialNumber; }

  public void setName(String name) { this.name = name; }
  public void setPrice(BigDecimal price) { this.price = price; }
  public void setDepartment(String department) { this.department = department; }
  public void setCategory(String category) { this.category = category; }
  public void setSubCategory(Optional<String> subCategory) { this.subCategory = subCategory; }
  public void setVendor(String vendor) { this.vendor = vendor; }

  public void setDimensionsUnit(String dimensionsUnit) { this.dimensionsUnit = dimensionsUnit; }
  public void setWeightUnit(String weightUnit) { this.weightUnit = weightUnit; }
  public void setWeight(double weight) { this.weight = weight; }
  public void setWidth(double width) { this.width = width; }
  public void setHeight(double height) { this.height = height; }
  public void setLength(double length) { this.length = length; }

  public void setWarehouseLocations(List<String> warehouseLocations) { this.warehouseLocations = warehouseLocations; }
  public void setFloorLocations(List<String> floorLocations) { this.floorLocations = floorLocations; }
  public void setStorageType(String storageType) { this.storageType = storageType; }
  public void setStorageNotes(Optional<String> storageNotes) { this.storageNotes = storageNotes; }
  public void setHazardous(boolean isHazardous) { this.isHazardous = isHazardous; }

  public void setDateAdded(LocalDateTime dateAdded) { this.dateAdded = dateAdded; }
  public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
  public void setUpdatedById(String updatedById) { this.updatedById = updatedById; }

  public String toString() {
    return "";
  }
}
