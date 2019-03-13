package entity;
//保存persistent的数据在entity

	import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//提供给用户一个get的信息
//创建完就不让用户修改了
	public class Item {
		private String itemId;
		private String name;
		private double rating;
		private String address;
		private Set<String> categories;
		private String imageUrl;
		private String url;
		private double distance;

		/**
		 * This is a builder pattern in Java.
		 */
		//Builder pattern builds a complex object using simple objects and using a step by step approach. 
		//It separates the construction of a complex object from its representation so that the same construction process can create 
		//different representations. We can also make the object to build immutable. 

//
		private Item(ItemBuilder builder) {
			this.itemId = builder.itemId;
			this.name = builder.name;
			this.rating = builder.rating;
			this.address = builder.address;
			this.categories = builder.categories;
			this.imageUrl = builder.imageUrl;
			this.url = builder.url;
			this.distance = builder.distance;


		}
		// to make data fields can be accessed by others, normally we need Getters and Setters for each of them. 
		//In this case, we just need Getters because we don’t want to change an item instance once it’s constructed.
		
		public String getItemId() {
			return itemId;
		}
		public String getName() {
			return name;
		}
		public double getRating() {
			return rating;
		}
		public String getAddress() {
			return address;
		}
		public Set<String> getCategories() {
			return categories;
		}
		public String getImageUrl() {
			return imageUrl;
		}
		public String getUrl() {
			return url;
		}
		public double getDistance() {
			return distance;
		}

		//To convert an Item object a JSONObject instance because in our application
		//frontend code cannot understand Java class, it can only understand JSON.

		//用户点击一个tab“nearby”，call ticketmaster API，把数据保存到数据库，将来用的时候调出来用
		//一方面返回给一部分把结果返回前端API，另一方面传入数据库，用于后面推荐
		public JSONObject toJSONObject() {
			JSONObject obj = new JSONObject();
			try {
				obj.put("item_id", itemId);
				obj.put("name", name);
				obj.put("rating", rating);
				obj.put("address", address);
				obj.put("categories", new JSONArray(categories));
				obj.put("image_url", imageUrl);
				obj.put("url", url);
				obj.put("distance", distance);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return obj;
		}
		
		public static class ItemBuilder {
			private String itemId;
			private String name;
			private double rating;
			private String address;
			private Set<String> categories;
			private String imageUrl;
			private String url;
			private double distance;
		//用builder pattern把get set 分开写，一方面在初始化变量比较多的时候，一个builder把对象一次性生成完了，一方面不让用户创建完再修改
		//constructor不能随便改，这样更有延展性
			
			public Item build() {
				return new Item(this);
			}


			public void setItemId(String itemId) {
				this.itemId = itemId;
			}

			public void setName(String name) {
				this.name = name;
			}

			public void setRating(double rating) {
				this.rating = rating;
			}

			public void setAddress(String address) {
				this.address = address;
			}

			public void setCategories(Set<String> categories) {
				this.categories = categories;
			}

			public void setImageUrl(String imageUrl) {
				this.imageUrl = imageUrl;
			}

			public void setUrl(String url) {
				this.url = url;
			}

			public void setDistance(double distance) {
				this.distance = distance;
			}
			
		}
		
	}


