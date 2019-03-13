package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Item;
import recommendation.GeoRecommendation;
//servlet是连接前端和中间的桥梁
/**
 * Servlet implementation class RecommendItem
 */
@WebServlet("/recommendation")
//content based recommendation
// 通过物品”本身的特点”, 而不是“购买这个物品的用户的信息”, 来计算物品之间的相似度.
//Given item profiles (category, price, etc.) of your favorite, recommend items that are similar to what you liked before
//在系统没有足够数据的时候, 一般先使用content-based method.
public class RecommendItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RecommendItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    //check exception 
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    	      throws ServletException, IOException {
    		
    			String userId = request.getParameter("user_id");

    			double lat = Double.parseDouble(request.getParameter("lat"));
    			double lon = Double.parseDouble(request.getParameter("lon"));

    			GeoRecommendation recommendation = new GeoRecommendation();
    			List<Item> items = recommendation.recommendItems(userId, lat, lon);
    			JSONArray array = new JSONArray();
    			for (Item item : items) {
    				array.put(item.toJSONObject());
    			}
    			RpcHelper.writeJsonArray(response, array);		
    	 }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
}
