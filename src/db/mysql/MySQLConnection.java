package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterAPI;
// This is a singleton pattern.
//真正建立mysql的connection
public class MySQLConnection implements DBConnection {
	//创建连接
	private Connection conn;
	   
	   public MySQLConnection() {
	  	 try {
	  		 // 通过jdbc把java和database连起来，
	  		 Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
	  		 conn = DriverManager.getConnection(MySQLDBUtil.URL);
	  	 } catch (Exception e) {
	  		 e.printStackTrace();
	  	 }
	   }
	
	@Override
	//close 连接
	public void close() {
		// TODO Auto-generated method stub
		 if (conn != null) {
	  		 try {
	  			 conn.close();
	  		 } catch (Exception e) {
	  			 e.printStackTrace();
	  		 }
	  	 }
    }


	@Override
	public void setFavoriteItems(String userId, List<String> itemIds) {
		// 看连接是否成功
		   if (conn == null) {
		  		 System.err.println("DB connection failed");
		  		 return;
		  }
		  	
		  try {
		 //向history里添加数据
		  String sql = "INSERT IGNORE INTO history(user_id, item_id) VALUES (?, ?)";
		  PreparedStatement ps = conn.prepareStatement(sql);
		  		 ps.setString(1, userId);
		  		 for (String itemId : itemIds) {
		  			 ps.setString(2, itemId);
		  			 ps.execute();
		  		 }
		  	       } catch (Exception e) {
		  		 e.printStackTrace();
		  	       }

	}

	@Override
	public void unsetFavoriteItems(String userId, List<String> itemIds) {
		// TODO Auto-generated method stub
		  if (conn == null) {
		  		 System.err.println("DB connection failed");
		  		 return;
		  }
		  	
		  try {
			  String sql = "DELETE FROM history WHERE user_id = ? AND item_id = ?";
			  PreparedStatement ps = conn.prepareStatement(sql);
			  ps.setString(1, userId);
			  for (String itemId : itemIds) {
		  			 ps.setString(2, itemId);
		  			 ps.execute();
			  }	      
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
	}

	@Override
	//根据user id 得到user所有favorite的item id
	public Set<String> getFavoriteItemIds(String userId) {
			if (conn == null) {
				return new HashSet<>();
			}
			
			Set<String> favoriteItems = new HashSet<>();
			
			try {
				String sql = "SELECT item_id FROM history WHERE user_id = ?";
				PreparedStatement stmt = conn.prepareStatement(sql);
				stmt.setString(1, userId);
				
				ResultSet rs = stmt.executeQuery();
				
				while (rs.next()) {
					String itemId = rs.getString("item_id");
					favoriteItems.add(itemId);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			return favoriteItems;

	}
//根据user id 从item table得到item信息
	@Override
	public Set<Item> getFavoriteItems(String userId) {
		if (conn == null) {
			return new HashSet<>();
		}
		
		Set<Item> favoriteItems = new HashSet<>();
		//call这个得到这个userId所喜欢过的item id,然后根据itemid搜索所有item
		Set<String> itemIds = getFavoriteItemIds(userId);
		
		try {
			String sql = "SELECT * FROM items WHERE item_id = ?";
			PreparedStatement stmt = conn.prepareStatement(sql);
			for (String itemId : itemIds) {
				stmt.setString(1, itemId);
				
				ResultSet rs = stmt.executeQuery();
				
				ItemBuilder builder = new ItemBuilder();
				
				while (rs.next()) {
					builder.setItemId(rs.getString("item_id"));
					builder.setName(rs.getString("name"));
					builder.setAddress(rs.getString("address"));
					builder.setImageUrl(rs.getString("image_url"));
					builder.setUrl(rs.getString("url"));
					builder.setCategories(getCategories(itemId));
					builder.setDistance(rs.getDouble("distance"));
					builder.setRating(rs.getDouble("rating"));
					favoriteItems.add(builder.build());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return favoriteItems;

	}
	

	@Override
	public Set<String> getCategories(String itemId) {
		// item_id, category
		// 1111,  music
		// 1111,  concert
		// 1111,  pop
	    
		if (conn == null) {
			return null;
		}
		Set<String> categories = new HashSet<>();
		try {
			String sql = "SELECT category from categories WHERE item_id = ? ";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, itemId);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String category = rs.getString("category");
				categories.add(category);
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return categories;

	}

	@Override
	//把从TicketmasterAPI search到的信息存到到db                  //term是传入的关键字
	public List<Item> searchItems(double lat, double lon, String term) {
		 TicketMasterAPI ticketMasterAPI = new TicketMasterAPI();
		   //返回给前端
	        List<Item> items = ticketMasterAPI.search(lat, lon, term);
	        //把搜索的结构信息保存到database
	        for(Item item : items) {
	        	saveItem(item);
	        }
	        return items;
	}

	@Override
	public void saveItem(Item item) {
		   if (conn == null) {
	  		   System.err.println("DB connection failed");
	  		   return;
	  	   }
	  	 //把数据加载到database，通过sql语句prepareStatement
	  	 // sql injection
	  	 // select * from users where username = '' AND password = '';
	  	
	  	 // username: fakeuser ' OR 1 = 1; DROP  --
	  	 // select * from users where username = 'fakeuser ' OR 1 = 1 --' AND password = '';
	  	
	  	
	  	 try {
	  		//把items的table里加新的rule，一共有7行
	  		 String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?)"; 
	  		 PreparedStatement ps = conn.prepareStatement(sql);
	  		 ps.setString(1, item.getItemId());
	  		 ps.setString(2, item.getName());
	  		 ps.setDouble(3, item.getRating());
	  		 ps.setString(4, item.getAddress());
	  		 ps.setString(5, item.getImageUrl());
	  		 ps.setString(6, item.getUrl());
	  		 ps.setDouble(7, item.getDistance());
	  		 ps.execute();
	  		//把category的table加上，如果有ignore多个row，一个出错还能继续下一个
	  		 sql = "INSERT IGNORE INTO categories VALUES(?, ?)";
	  		 ps = conn.prepareStatement(sql);
	  		 ps.setString(1, item.getItemId());
	  		 //category是set要iterate它
	  		 for(String category : item.getCategories()) {
	  			 ps.setString(2, category);
	  			 ps.execute();
	  		 }
	  		 
	  	 } catch (Exception e) {
	  		 e.printStackTrace();
	  	 }
	}

	@Override
	public String getFullname(String userId) {
		if (conn == null) {
			return "";
		}
		
		String name = "";
		
		try {
			String sql = "SELECT first_name, last_name FROM users WHERE user_id = ? ";
			PreparedStatement statement = conn.prepareStatement(sql);
			statement.setString(1, userId);
			ResultSet rs = statement.executeQuery();
			//iterator 起点是-1
			while (rs.next()) {
				name = rs.getString("first_name") + " " + rs.getString("last_name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return name;

	}

	@Override
	public boolean verifyLogin(String userId, String password) {
		if (conn == null) {
			return false;
		}
		try {
			String sql="SELECT * FROM users WHERE user_id = ? AND password = ? ";
			PreparedStatement statement=conn.prepareStatement(sql);
			statement.setString(1, userId);
			statement.setString(2, password);
			ResultSet rs=statement.executeQuery();
			while (rs.next()) {
				return true;
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean registerUser(String userId, String password, String firstname, String lastname) {
		if (conn == null) {
			System.err.println("DB connection failed");
			return false;
		}

		try {
			String sql = "INSERT IGNORE INTO users VALUES (?, ?, ?, ?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, userId);
			ps.setString(2, password);
			ps.setString(3, firstname);
			ps.setString(4, lastname);
			
			return ps.executeUpdate() == 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;	
	}

}
