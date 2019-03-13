package entity;
//����persistent��������entity

	import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//�ṩ���û�һ��get����Ϣ
//������Ͳ����û��޸���
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
		//In this case, we just need Getters because we don��t want to change an item instance once it��s constructed.
		
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

		//�û����һ��tab��nearby����call ticketmaster API�������ݱ��浽���ݿ⣬�����õ�ʱ���������
		//һ���淵�ظ�һ���ְѽ������ǰ��API����һ���洫�����ݿ⣬���ں����Ƽ�
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
		//��builder pattern��get set �ֿ�д��һ�����ڳ�ʼ�������Ƚ϶��ʱ��һ��builder�Ѷ���һ�����������ˣ�һ���治���û����������޸�
		//constructor�������ģ�����������չ��
			
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


