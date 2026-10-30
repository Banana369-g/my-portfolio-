package computer.entity;

public class computer {
	private int computerId;
	private String name;
	private String brand;
	private String cpu;
	private String gpu;
	private int ram;
	private int storage;
	private String category;
	private String description;
	private String image;
	private long price;
	private int quantity;
	private String seller;

	public computer() {
	}

	public computer(int computerId, String name, String brand, String cpu, String gpu,
			int ram, int storage, String category, String description, String image,
			long price, int quantity, String seller) {
		this.computerId = computerId;
		this.name = name;
		this.brand = brand;
		this.cpu = cpu;
		this.gpu = gpu;
		this.ram = ram;
		this.storage = storage;
		this.category = category;
		this.description = description;
		this.image = image;
		this.price = price;
		this.quantity = quantity;
		this.seller = seller;
	}

	public int getComputerId() {
		return computerId;
	}

	public void setComputerId(int computerId) {
		this.computerId = computerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getCpu() {
		return cpu;
	}

	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

	public String getGpu() {
		return gpu;
	}

	public void setGpu(String gpu) {
		this.gpu = gpu;
	}

	public int getRam() {
		return ram;
	}

	public void setRam(int ram) {
		this.ram = ram;
	}

	public int getStorage() {
		return storage;
	}

	public void setStorage(int storage) {
		this.storage = storage;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public long getPrice() {
		return price;
	}

	public void setPrice(long price) {
		this.price = price;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getSeller() {
		return seller;
	}

	public void setSeller(String seller) {
		this.seller = seller;
	}

	@Override
	public String toString() {
		return computerId + " | " + name + " | " + brand + " | " + cpu
				+ " | RAM:" + ram + "GB | 保存:" + storage + "GB | " + category
				+ " | 価格:" + price + "円 | 在庫:" + quantity + " | 出品者:" + seller;
	}
}