import java.io.FileInputStream;
import java.util.Random;
import java.util.Scanner;

import javazoom.jl.player.Player;

public class Project1 {

	static final int MAX_HP = 100;
	static final int MAX_HEAL = 3;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		Random r = new Random();

		int[] hp = { MAX_HP, MAX_HP };
		boolean[] defending = { false, false };
		int[] healcount = { 0, 0 };
		String[] p = new String[2];

		int current = 0;

		// Player names
		System.out.print("Enter Player 1 Name: ");
		p[0] = sc.nextLine();
		playSound("rizz-sound-effect.mp3");

		System.out.print("Enter Player 2 Name: ");
		p[1] = sc.nextLine();
		playSound("rizz-sound-effect.mp3");

		int total = getBet(sc, p[0]) + getBet(sc, p[1]);

		while (hp[0] > 0 && hp[1] > 0) {
			int opponent = 1 - current;

			displayStatus(p, hp, defending, healcount);

			System.out.println("\n" + p[current] + "'s Turn");
			System.out.println("1. Attack  2. Defend  3. Heal");

			int choice = sc.nextInt();

			switch (choice) {
			case 1:
				attack(r, p, hp, defending, current, opponent);
				break;

			case 2:
				defending[current] = true;
				System.out.println(p[current] + " is defending!");
				break;

			case 3:
				heal(r, p, hp, healcount, current);
				break;

			default:
				System.out.println("Invalid choice!");
				playSound("vine-boom.mp3");
				continue;
			}

			if (hp[opponent] <= 0)
				break;

			current = opponent;
		}

		System.out.println("\n===== GAME OVER =====");
		if (hp[0] <= 0) {
			System.out.println(p[1] + " Wins!");
		} else {
			System.out.println(p[0] + " Wins!");
		}
		playSound("rizz-sound-effect.mp3");
		System.out.println("CONGRALUTIONS YOU WIN ￥" + total);

	}

	static void attack(Random r, String[] p, int[] hp, boolean[] defending, int current, int opponent) {
		int damage = r.nextInt(11) + 10;
		if ((r.nextInt(2) == 0)) {
			playSound("重いキック1.mp3");
		} else {
			playSound("punch-gaming-sound-effect-hd_RzlG1GE.mp3");
		}

		if (r.nextInt(100) < 20) {
			damage *= 2;
			System.out.println("🔥 CRITICAL HIT!");
			playSound("bone-crack.mp3");
		}

		if (defending[opponent]) {
			damage /= 2;
			defending[opponent] = false;
			playSound("rizz-sound-effect.mp3");
		}

		hp[opponent] -= damage;
		System.out.println(p[current] + " attacked for " + damage + " damage!");
	}

	static void heal(Random r, String[] p, int[] hp, int[] healcount, int current) {
		if (healcount[current] >= MAX_HEAL) {
			System.out.println("❌ No heals left!");
			playSound("fahhhhhhhhhhhhhh.mp3");
			return;
		}

		int heal = r.nextInt(20) + 5;
		hp[current] = Math.min(MAX_HP, hp[current] + heal);
		healcount[current]++;

		playSound("anime-wow-sound-effect.mp3");
		System.out.println(p[current] + " healed " + heal + " HP!");
	}

	static int getBet(Scanner sc, String player) {
		System.out.print(player + ", enter your bet: ");
		int bet = sc.nextInt();

		if (bet < 1000) {
			playSound("999-social-credit-siren.mp3");
		} else {
			playSound("anime-wow-sound-effect.mp3");
		}

		return bet;
	}

	static void displayStatus(String[] p, int[] hp, boolean[] defending, int[] healcount) {
		for (int i = 0; i < 2; i++) {
			System.out.println(p[i] + " HP: " + hp[i] +
					(defending[i] ? " (Defending)" : "") +
					" | Heals left: " + (MAX_HEAL - healcount[i]));
		}
	}

	static void playSound(String fileName) {
		try {
			FileInputStream file = new FileInputStream(fileName);
			Player sound = new Player(file);
			sound.play();
		} catch (Exception e) {
			System.out.println("⚠ Sound error: " + fileName);
		}
	}
}