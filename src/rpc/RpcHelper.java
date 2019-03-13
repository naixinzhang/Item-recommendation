package rpc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import entity.Item;
//�ⲿ�ִ�����ظ����֣�д��class�����ظ�
public class RpcHelper {
	// Parses a JSONObject from http request.
		public static JSONObject readJSONObject(HttpServletRequest request) {
	  	   StringBuilder sBuilder = new StringBuilder();
	  	   //��ǰ�˴�������Ϣת��jason object
	  	   try (BufferedReader reader = request.getReader()) {
	  		 String line = null;
	  		 while((line = reader.readLine()) != null) {
	  			 sBuilder.append(line);
	  		 }
	  		 return new JSONObject(sBuilder.toString());
	  		
	  	   } catch (Exception e) {
	  		 e.printStackTrace();
	  	   }
	  	//����������ⷵ�ؿ�
	  	  return new JSONObject();
	   }

	// Writes a JSONArray to http response.
		public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException{
			response.setContentType("application/jason");
			//����Ȩ�ޣ���ͬ�������ֻ����**������request�ѽ�����ظ�http  *��һ��ͨ���
			response.setHeader("Access-Control-Allow-Origin","*");
			//turns a printwriter object that can send character text to the client
			PrintWriter out = response.getWriter();
			//�����ظ�client��������ӵ�print��
			out.print(array);
			out.close();
		}

	              // Writes a JSONObject to http response.
		public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException {		
			response.setContentType("application/jason");
			response.setHeader("Access-Control-Allow-Origin","*");
			PrintWriter out = response.getWriter();
			//ֻҪ�������array�ĳ�object�Ϳ�����
			out.print(obj);
			out.close();
		}
		
		  // Converts a list of Item objects to JSONArray.
		  public static JSONArray getJSONArray(List<Item> items) {
		    JSONArray result = new JSONArray();
		    try {
		      for (Item item : items) {
		        result.put(item.toJSONObject());
		      }
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		    return result;
		  }


}



