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
    //��һ��user id ����֮ǰ�������ղصĻ
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
				//����ǰ�����item�Ѿ����û�favoriteӦ�ü���ʵ��
				obj.append("favorite", true);
				array.put(obj);
			}
			//������ص�ǰ��
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
    //�����ݿ�д����
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 DBConnection connection = DBConnectionFactory.getConnection();
	  	 try {
	  		//�����û����õ�favoriteͨ��http�õ�JASONObject
	  		 JSONObject input = RpcHelper.readJSONObject(request);
	  		//��ȡjason��Ϣ
	  		 String userId = input.getString("user_id");
	  		 JSONArray array = input.getJSONArray("favorite");
	  		//ת��Ϊlist of string
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
