package lesson09;

public class Teacherr {

	public static void main(String[] args) {
		Student s1 = new Student("yukesh");
		Student s2 = new Student("bug");
		Student s3 = new Student("sudip");

		s1.introduce();
		s2.introduce();
		s3.introduce();
	}
}

class Student {
	private String name;

	public Student(String name) {
		this.name = name;
	}

	public void introduce() {
		System.out.println("my name is " + name);
	}
}