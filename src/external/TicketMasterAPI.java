package external;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import entity.Item.ItemBuilder;


public class TicketMasterAPI {
	private static final String URL = "https://app.ticketmaster.com/discovery/v2/events.json";
	//��дkeyword Ĭ�Ϸ������е�events
	private static final String DEFAULT_KEYWORD = ""; // no restriction
	private static final String API_KEY = "z5uZEra74zMXU4EL0dgi5qyWXEZQuruz";
	//��������е����ݶ��������ݿ������һ��List<item>ȥ����õ��Ľ����array��Ӧlist
	public List<Item> search(double lat, double lon, String keyword) {
		if (keyword == null) {
			keyword = DEFAULT_KEYWORD;
		}
		try {
			//UTF-8 ת����taketmaster��ʶ���
			keyword = URLEncoder.encode(keyword, "UTF-8"); //"Rick Sun" => "Rick%20Sun" httpЭ���п����ո��������ַֿ�
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			//����д�����Ϣ����ӡ��������
			e.printStackTrace();
		}
		
		// "apikey=qqPuP6n3ivMUoT9fPgLepkRMreBcbrjV&latlong=37,-120&keyword=event&radius=50"
			// %sռλ������������Ӧ�Ķ������滻�� //50miles
		String query = String.format("apikey=%s&latlong=%s,%s&keyword=%s&radius=%s", API_KEY, lat, lon, keyword, 50);
		//����Ҫ���ͳ�ȥ�ĸ�ʽ
		String url = URL + "?" + query;
		//java io �Դ��Ŀ⣬��æ������ά����end point
		try {
			//����remote end point
			//Create a URLConnection instance that represents a connection to the remote object referred to by the URL. 
			//The HttpUrlConnection class allows us to perform basic HTTP requests without the use of any additional libraries.
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			//Tell what HTTP method to use. GET by default. 
			//The HttpUrlConnection class is used for all types of requests: GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE.
			connection.setRequestMethod("GET");
			//Get the status code from an HTTP response message.
			//To execute the request we can use the getResponseCode(), connect(), getInputStream() or getOutputStream() methods.
			int responseCode = connection.getResponseCode();
			System.out.println("Sending request to url: " + url);
			System.out.println("Response code: " + responseCode);
			
			if (responseCode != 200) {
				System.out.println("error status code is" + responseCode);
				return new ArrayList<>();//���ִ�����client���޷��������ؿյĸ����Ϳ�����
			}
			//�����200��˵���н���������ˣ�����������
			//returns an input stream that reads from this open connection
			//ͨ��getInputStream��ȡ��ticketmaster���ظ��ҵ���Ϣ����inputstream��ʽ�ģ���InputStreamReader��ȡ
			//Create a BufferedReader to help read text from a character-input stream. 
			//Provide for the efficient reading of characters, arrays, and lines.
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuilder response = new StringBuilder();
			//��reader����һ��һ�еĶ���ֱ��readerΪ��
			//Append response data to response StringBuilder instance line by line.
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			//Close the BufferedReader after reading the inputstream/response data.
			reader.close();
			
			// Extract events array only.Create a Json object out of the response string.
			JSONObject obj = new JSONObject(response.toString());

			if (!obj.isNull("_embedded")) {
				JSONObject embedded = obj.getJSONObject("_embedded");
				  return getItemList(embedded.getJSONArray("events"));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//���û�н����ticketmasterapi������
		return new ArrayList<>();
	}
	
	//add purify method in TicketMasterAPI.
	//java to convert JSONArray to a list of items.
	private List<Item> getItemList(JSONArray events) throws JSONException {
		List<Item> itemList = new ArrayList<>();
		
		for (int i = 0; i < events.length(); ++i) {
			JSONObject event = events.getJSONObject(i);
			ItemBuilder builder = new ItemBuilder();
			if (!event.isNull("id")) {
				builder.setItemId(event.getString("id"));
			}
			if (!event.isNull("name")) {
				builder.setName(event.getString("name"));
			}
			if (!event.isNull("url")) {
				builder.setUrl(event.getString("url"));
			}
			if (!event.isNull("distance")) {
				builder.setDistance(event.getDouble("distance"));
			}
			if (!event.isNull("rating")) {
				builder.setRating(event.getDouble("rating"));
			}
			builder.setAddress(getAddress(event));
			builder.setCategories(getCategories(event));
			builder.setImageUrl(getImageUrl(event));

			//��item����Ϣ�����ػ�ȥ
			itemList.add(builder.build());
		}
		return itemList;
	}

	
	/**
	 * Helper methods
	 */
	private String getAddress(JSONObject event) throws JSONException {
		if (!event.isNull("_embedded")) {
			JSONObject embedded = event.getJSONObject("_embedded");
			if (!embedded.isNull("venues")) {
				JSONArray venues = embedded.getJSONArray("venues");
				//ȡ����event����Ϊ�в�ͬ�ĵ�ַ����֤�ܷ���һ������
				for (int i = 0; i < venues.length(); ++i) {
					JSONObject venue = venues.getJSONObject(i);
					StringBuilder addressBuilder = new StringBuilder();
					if (!venue.isNull("address")) {
						JSONObject address = venue.getJSONObject("address");
						if (!address.isNull("line1")) {
							addressBuilder.append(address.getString("line1"));
						}
						if (!address.isNull("line2")) {
							addressBuilder.append(",");
							addressBuilder.append(address.getString("line2"));
						}
						if (!address.isNull("line3")) {
							addressBuilder.append(",");
							addressBuilder.append(address.getString("line3"));
						}
					}
					if (!venue.isNull("city")) {
						JSONObject city = venue.getJSONObject("city");
						if (!city.isNull("name")) {
							addressBuilder.append(",");
							addressBuilder.append(city.getString("name"));
						}
					}
					
					String addressStr = addressBuilder.toString();
					if (!addressStr.equals("")) {
						return addressStr;
					}
				}
			}
		}
		return "";
	}

	private String getImageUrl(JSONObject event) throws JSONException {
		if (!event.isNull("images")) {
			JSONArray array = event.getJSONArray("images");
			for (int i = 0; i < array.length(); i++) {
				JSONObject image = array.getJSONObject(i);
				if (!image.isNull("url")) {
					return image.getString("url");
				}
			}
		}
		return "";
	}

	private Set<String> getCategories(JSONObject event) throws JSONException {
		Set<String> categories = new HashSet<>();
		if (!event.isNull("classifications")) {
			JSONArray classifications = event.getJSONArray("classifications");
			for (int i = 0; i < classifications.length(); ++i) {
				JSONObject classification = classifications.getJSONObject(i);
				if (!classification.isNull("segment")) {
					JSONObject segment = classification.getJSONObject("segment");
					if (!segment.isNull("name")) {
						categories.add(segment.getString("name"));
					}
				}
			}
		}
		return categories;
	}
	private void queryAPI(double lat, double lon) {
		List<Item> events = search(lat, lon, null);

		for (Item event : events) {
			System.out.println(event.toJSONObject());
		}
	}
	/**
	 * Main entry for sample TicketMaster API requests.
	 */
	public static void main(String[] args) {
		TicketMasterAPI tmApi = new TicketMasterAPI();
		// Mountain View, CA
		// tmApi.queryAPI(37.38, -122.08);
		// London, UK
		// tmApi.queryAPI(51.503364, -0.12);
		// Houston, TX
		tmApi.queryAPI(29.682684, -95.295410);
	}

}






	
	
