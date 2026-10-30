package lesson09;

public class dog {
	private String type;
	private String color;
	private double height;
	private double weight;

	public dog(String type, String color, double height, double weight) {
		this.type = type;
		this.color = color;
		this.height = height;
		this.weight = weight;
	}

	public void introduce() {
		System.out.println("Dog Type = " + type);
		System.out.println("Dog color = " + color);
		System.out.println("Dog height = " + height + "C.M");
		System.out.println("Dog weight = " + weight + "K.G");
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public double isHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double isWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

}
