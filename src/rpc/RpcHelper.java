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
//这部分代码会重复出现，写个class避免重复
public class RpcHelper {
	// Parses a JSONObject from http request.
		public static JSONObject readJSONObject(HttpServletRequest request) {
	  	   StringBuilder sBuilder = new StringBuilder();
	  	   //从前端传来的信息转成jason object
	  	   try (BufferedReader reader = request.getReader()) {
	  		 String line = null;
	  		 while((line = reader.readLine()) != null) {
	  			 sBuilder.append(line);
	  		 }
	  		 return new JSONObject(sBuilder.toString());
	  		
	  	   } catch (Exception e) {
	  		 e.printStackTrace();
	  	   }
	  	//如果出现问题返回空
	  	  return new JSONObject();
	   }

	// Writes a JSONArray to http response.
		public static void writeJsonArray(HttpServletResponse response, JSONArray array) throws IOException{
			response.setContentType("application/jason");
			//控制权限，不同浏览器，只允许**发来的request把结果返回给http  *是一个通配符
			response.setHeader("Access-Control-Allow-Origin","*");
			//turns a printwriter object that can send character text to the client
			PrintWriter out = response.getWriter();
			//将返回给client的内容添加到print里
			out.print(array);
			out.close();
		}

	              // Writes a JSONObject to http response.
		public static void writeJsonObject(HttpServletResponse response, JSONObject obj) throws IOException {		
			response.setContentType("application/jason");
			response.setHeader("Access-Control-Allow-Origin","*");
			PrintWriter out = response.getWriter();
			//只要把上面的array改成object就可以了
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



