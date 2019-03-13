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
//��������mysql��connection
public class MySQLConnection implements DBConnection {
	//��������
	private Connection conn;
	   
	   public MySQLConnection() {
	  	 try {
	  		 // ͨ��jdbc��java��database��������
	  		 Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
	  		 conn = DriverManager.getConnection(MySQLDBUtil.URL);
	  	 } catch (Exception e) {
	  		 e.printStackTrace();
	  	 }
	   }
	
	@Override
	//close ����
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
		// �������Ƿ�ɹ�
		   if (conn == null) {
		  		 System.err.println("DB connection failed");
		  		 return;
		  }
		  	
		  try {
		 //��history���������
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
	//����user id �õ�user����favorite��item id
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
//����user id ��item table�õ�item��Ϣ
	@Override
	public Set<Item> getFavoriteItems(String userId) {
		if (conn == null) {
			return new HashSet<>();
		}
		
		Set<Item> favoriteItems = new HashSet<>();
		//call����õ����userId��ϲ������item id,Ȼ�����itemid��������item
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
	//�Ѵ�TicketmasterAPI search������Ϣ�浽��db                  //term�Ǵ���Ĺؼ���
	public List<Item> searchItems(double lat, double lon, String term) {
		 TicketMasterAPI ticketMasterAPI = new TicketMasterAPI();
		   //���ظ�ǰ��
	        List<Item> items = ticketMasterAPI.search(lat, lon, term);
	        //�������Ľṹ��Ϣ���浽database
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
	  	 //�����ݼ��ص�database��ͨ��sql���prepareStatement
	  	 // sql injection
	  	 // select * from users where username = '' AND password = '';
	  	
	  	 // username: fakeuser ' OR 1 = 1; DROP  --
	  	 // select * from users where username = 'fakeuser ' OR 1 = 1 --' AND password = '';
	  	
	  	
	  	 try {
	  		//��items��table����µ�rule��һ����7��
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
	  		//��category��table���ϣ������ignore���row��һ�������ܼ�����һ��
	  		 sql = "INSERT IGNORE INTO categories VALUES(?, ?)";
	  		 ps = conn.prepareStatement(sql);
	  		 ps.setString(1, item.getItemId());
	  		 //category��setҪiterate��
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
			//iterator �����-1
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
