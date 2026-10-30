package lesson09;

public class student {

	private String name;
	private int age;

	public student(String name, int age) {
		this.name = name;
		this.age = age;

	}

	public void introduce() {
		System.out.println("I'm " + name);
		System.out.println("I'm " + age + " year old");
		System.out.println("I'm " + name.length());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
