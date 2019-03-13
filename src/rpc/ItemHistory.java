package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;

/**
 * Servlet implementation class ItemHistory
 */
@WebServlet("/history")
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ItemHistory() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    //给一个user id 返回之前所有她收藏的活动
    //we add an extra key-value pair to JSON output to indicate the corresponding item object is favorited by user. 
    //Our frontend code will make a solid heart based on this data.

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
    	
		String userId = request.getParameter("user_id");
		JSONArray array = new JSONArray();
		
		DBConnection conn = DBConnectionFactory.getConnection();
		
		try {
			Set<Item> items = conn.getFavoriteItems(userId);
			for (Item item : items) {
				JSONObject obj = item.toJSONObject();
				//告诉前端这个item已经被用户favorite应该加上实心
				obj.append("favorite", true);
				array.put(obj);
			}
			//结果返回到前端
			RpcHelper.writeJsonArray(response, array);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    //向数据库写数据
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 DBConnection connection = DBConnectionFactory.getConnection();
	  	 try {
	  		//根据用户设置的favorite通过http得到JASONObject
	  		 JSONObject input = RpcHelper.readJSONObject(request);
	  		//读取jason信息
	  		 String userId = input.getString("user_id");
	  		 JSONArray array = input.getJSONArray("favorite");
	  		//转化为list of string
	  		 List<String> itemIds = new ArrayList<>();
	  		 for(int i = 0; i < array.length(); ++i) {
	  			 itemIds.add(array.getString(i));
	  		 }
	  		 
	  		 connection.setFavoriteItems(userId, itemIds);
	  		 
	  		 RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
	  		
	  	 } catch (Exception e) {
	  		 e.printStackTrace();
	  	 } finally {
	  		 connection.close();
	  	 }

	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		 DBConnection connection = DBConnectionFactory.getConnection();
	  	 try {
	  		 JSONObject input = RpcHelper.readJSONObject(request);
	  		 String userId = input.getString("user_id");
	  		 JSONArray array = input.getJSONArray("favorite");
	  		 List<String> itemIds = new ArrayList<>();
	  		 for(int i = 0; i < array.length(); ++i) {
	  			 itemIds.add(array.getString(i));
	  		 }
	  		 connection.unsetFavoriteItems(userId, itemIds);
	  		 RpcHelper.writeJsonObject(response, new JSONObject().put("result", "SUCCESS"));
	  		
	  	 } catch (Exception e) {
	  		 e.printStackTrace();
	  	 } finally {
	  		 connection.close();
	  	 }
	}

}
