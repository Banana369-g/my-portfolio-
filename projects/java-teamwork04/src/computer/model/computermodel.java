package computer.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import computer.entity.computer;

public class computermodel {

	private static final String URL = "jdbc:sqlite:computer.db";

	public void init() {
		String sql = "CREATE TABLE IF NOT EXISTS computer ("
				+ "computerId INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "name TEXT NOT NULL,"
				+ "brand TEXT,"
				+ "cpu TEXT,"
				+ "gpu TEXT,"
				+ "ram INTEGER,"
				+ "storage INTEGER,"
				+ "category TEXT,"
				+ "description TEXT,"
				+ "image TEXT,"
				+ "price INTEGER,"
				+ "quantity INTEGER,"
				+ "seller TEXT)";
		try (Connection Y = DriverManager.getConnection(URL);
				PreparedStatement X = Y.prepareStatement(sql)) {
			X.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<computer> findAll() {
		List<computer> list = new ArrayList<>();
		String sql = "SELECT * FROM computer ORDER BY computerId";
		try (Connection Y = DriverManager.getConnection(URL);
				PreparedStatement X = Y.prepareStatement(sql);
				ResultSet P = X.executeQuery()) {
			while (P.next()) {
				list.add(mapRow(P));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public computer findByAll(int id) {
		computer c = null;
		String sql = "SELECT * FROM computer WHERE computerId = ?";
		try (Connection Y = DriverManager.getConnection(URL);
				PreparedStatement X = Y.prepareStatement(sql)) {
			X.setInt(1, id);
			try (ResultSet P = X.executeQuery()) {
				if (P.next()) {
					c = mapRow(P);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return c;
	}

	public List<computer> findByKeyword(String word) {
		List<computer> list = new ArrayList<>();
		String sql = "SELECT * FROM computer WHERE name LIKE ? OR brand LIKE ? OR category LIKE ?";
		String w = "%" + word + "%";
		try (Connection Y = DriverManager.getConnection(URL);
				PreparedStatement X = Y.prepareStatement(sql)) {
			X.setString(1, w);
			X.setString(2, w);
			X.setString(3, w);
			try (ResultSet P = X.executeQuery()) {
				while (P.next()) {
					list.add(mapRow(P));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<computer> findbyprice(long min, long max) {
		List<computer> list = new ArrayList<>();
		String sql = "SELECT * FROM computer WHERE price BETWEEN ? AND ? ORDER BY price";
		try (Connection Y = DriverManager.getConnection(URL);
				PreparedStatement X = Y.prepareStatement(sql)) {
			X.setLong(1, min);
			X.setLong(2, max);
			try (ResultSet P = X.executeQuery()) {
				while (P.next()) {
					list.add(mapRow(P));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public boolean buyComputer(int id, int qty) {
		String sql = "UPDATE computer SET quantity = quantity - ? WHERE computerId = ? AND quantity >= ?";
		try (Connection Y = DriverManager.getConnection(URL);
				PreparedStatement X = Y.prepareStatement(sql)) {
			X.setInt(1, qty);
			X.setInt(2, id);
			X.setInt(3, qty);
			return X.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean sellComputer(computer c) {
		String sql = "INSERT INTO computer (name,brand,cpu,gpu,ram,storage,category,description,image,price,quantity,seller) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
		try (Connection Y = DriverManager.getConnection(URL);
				PreparedStatement X = Y.prepareStatement(sql)) {
			X.setString(1, c.getName());
			X.setString(2, c.getBrand());
			X.setString(3, c.getCpu());
			X.setString(4, c.getGpu());
			X.setInt(5, c.getRam());
			X.setInt(6, c.getStorage());
			X.setString(7, c.getCategory());
			X.setString(8, c.getDescription());
			X.setString(9, c.getImage());
			X.setLong(10, c.getPrice());
			X.setInt(11, c.getQuantity());
			X.setString(12, c.getSeller());
			return X.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean update(computer c) {
		String sql = "UPDATE computer SET name=?,brand=?,cpu=?,gpu=?,ram=?,storage=?,category=?,description=?,image=?,price=?,quantity=?,seller=? WHERE computerId=?";
		try (Connection Y = DriverManager.getConnection(URL);
				PreparedStatement X = Y.prepareStatement(sql)) {
			X.setString(1, c.getName());
			X.setString(2, c.getBrand());
			X.setString(3, c.getCpu());
			X.setString(4, c.getGpu());
			X.setInt(5, c.getRam());
			X.setInt(6, c.getStorage());
			X.setString(7, c.getCategory());
			X.setString(8, c.getDescription());
			X.setString(9, c.getImage());
			X.setLong(10, c.getPrice());
			X.setInt(11, c.getQuantity());
			X.setString(12, c.getSeller());
			X.setInt(13, c.getComputerId());
			return X.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean delete(int id) {
		String sql = "DELETE FROM computer WHERE computerId=?";
		try (Connection Y = DriverManager.getConnection(URL);
				PreparedStatement X = Y.prepareStatement(sql)) {
			X.setInt(1, id);
			return X.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	private computer mapRow(ResultSet P) throws SQLException {
		computer c = new computer();
		c.setComputerId(P.getInt("computerId"));
		c.setName(P.getString("name"));
		c.setBrand(P.getString("brand"));
		c.setCpu(P.getString("cpu"));
		c.setGpu(P.getString("gpu"));
		c.setRam(P.getInt("ram"));
		c.setStorage(P.getInt("storage"));
		c.setCategory(P.getString("category"));
		c.setDescription(P.getString("description"));
		c.setImage(P.getString("image"));
		c.setPrice(P.getLong("price"));
		c.setQuantity(P.getInt("quantity"));
		c.setSeller(P.getString("seller"));
		return c;
	}
}