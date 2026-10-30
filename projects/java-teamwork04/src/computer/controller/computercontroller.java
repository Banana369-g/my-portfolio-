package computer.controller;

import computer.entity.computer;
import computer.model.computermodel;
import computer.view.computerview;

public class computercontroller {

	public static void main(String[] args) {
		computermodel model = new computermodel();
		computerview view = new computerview();

		model.init();

		boolean loop = true;
		while (loop) {
			view.showMenu();
			int menu = view.readMenu();

			switch (menu) {
			case 1:
				view.showList(model.findAll());
				break;

			case 2: {
				System.out.print("IDを入力: ");
				int id = view.readInt();
				view.showDetail(model.findByAll(id));
				break;
			}

			case 3: {
				System.out.print("キーワードを入力: ");
				String word = view.readString();
				view.showList(model.findByKeyword(word));
				break;
			}

			case 4: {
				System.out.print("価格(最小)を入力: ");
				long min = view.readLong();
				System.out.print("価格(最大)を入力: ");
				long max = view.readLong();
				view.showList(model.findbyprice(min, max));
				break;
			}

			case 5: {
				System.out.print("購入したいパソコンのIDを入力: ");
				int buyId = view.readInt();
				System.out.print("購入数量を入力: ");
				int qty = view.readInt();
				boolean ok = model.buyComputer(buyId, qty);
				view.showMessage(ok ? "購入が完了しました。" : "在庫不足、またはIDが存在しません。");
				break;
			}

			case 6: {
				System.out.print("商品名: ");
				String name = view.readString();
				System.out.print("ブランド: ");
				String brand = view.readString();
				System.out.print("CPU: ");
				String cpu = view.readString();
				System.out.print("GPU: ");
				String gpu = view.readString();
				System.out.print("RAM(GB): ");
				int ram = view.readInt();
				System.out.print("ストレージ(GB): ");
				int storage = view.readInt();
				System.out.print("カテゴリ: ");
				String category = view.readString();
				System.out.print("説明: ");
				String description = view.readString();
				System.out.print("画像URL: ");
				String image = view.readString();
				System.out.print("価格: ");
				long price = view.readLong();
				System.out.print("在庫数: ");
				int quantity = view.readInt();
				System.out.print("出品者名: ");
				String seller = view.readString();

				computer c = new computer(0, name, brand, cpu, gpu, ram, storage,
						category, description, image, price, quantity, seller);
				boolean ok = model.sellComputer(c);
				view.showMessage(ok ? "出品が完了しました。" : "出品に失敗しました。");
				break;
			}

			case 0:
				loop = false;
				view.showMessage("終了します。");
				break;

			default:
				view.showMessage("無効な選択です。");
			}
		}
	}
}