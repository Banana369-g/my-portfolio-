package lesson09;

public class carowner {

	public static void main(String[] args) {
		Engine engine = new Engine();
		Car car = new Car(engine);
		car.start();
	}
}

class Car {
	private Engine engine;

	public Car(Engine engine) {
		this.engine = engine;
	}

	public void start() {
		engine.fire();
	}
}

class Engine {
	public void fire() {
		System.out.println("🚒💨💨💨💨💨💨");
	}
}