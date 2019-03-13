package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;
import external.TicketMasterAPI;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
   // In SearchItem.doGet(), we need to have the same logic with itemHistory
    //so that frontend code can decide whether to show an empty or solid heart.
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		HttpSession session = request.getSession(false);
//		if (session == null) {
//			response.setStatus(403);
//			return;
//		}
//		//ͨ��session���userid
//		String userId = session.getAttribute("user_id").toString();
		String userId = request.getParameter("user_id");

		

		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		//��ticketmasterAPI����ѡ�����ݽ�items��Ȼ�󷵻�array����ʽ
		//TicketMasterAPI tmAPI = new TicketMasterAPI();
		//List<Item> items = tmAPI.search(lat, lon, null);	
		String term = request.getParameter("term");
		//�������ݿ�,����user idȥ���ݿ�����history table
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			//ͨ������γ��ȥTicketmasterAPI����
			List<Item> items = connection.searchItems(lat, lon, term);
			//ͬʱȥ���ݿ��������ղع���itemid
			Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);

			JSONArray array = new JSONArray();
			//����ʱ����û�б��ղع�
			for (Item item : items) {
				JSONObject obj = item.toJSONObject();
				obj.put("favorite", favoritedItemIds.contains(item.getItemId()));
				array.put(obj);
			}
			RpcHelper.writeJsonArray(response, array);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}

		
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
