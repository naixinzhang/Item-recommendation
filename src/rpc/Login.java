package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//��֤request����û��session
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
			//��seesion�ͷ��أ�û�оͲ���ɶ�����Լ���������Ϊ����post�Ĺ���
			HttpSession session = request.getSession(false);
			JSONObject obj = new JSONObject();
			if (session != null) {
				String userId = session.getAttribute("user_id").toString();
				obj.put("status", "OK").put("user_id", userId).put("name", connection.getFullname(userId));	
			} else {
				response.setStatus(403);
				obj.put("status", "Session Invalid");
			}
			RpcHelper.writeJsonObject(response, obj);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
		//����ǰ�˴�����������������Ƚϱ��ܣ�Ӧ�÷���body��	
			JSONObject input = RpcHelper.readJSONObject(request);
			String userId = input.getString("user_id");
			String password = input.getString("password");
			
			JSONObject obj = new JSONObject();
			//��¼�ɹ�
			if (connection.verifyLogin(userId, password)) {
				//����session
				HttpSession session = request.getSession();
				session.setAttribute("user_id", userId);
				//session��Ч�ڣ�600s
				//session�������tomcat������server���ڴ�����ò����տ��Դ������ݿ�
				session.setMaxInactiveInterval(600);
				//����debug�����Ե�ʱ����
				obj.put("status", "OK").put("user_id", userId).put("name", connection.getFullname(userId));
			} else {//��¼ʧ�ܣ��û�����������˷���401
				response.setStatus(401);
				obj.put("status", "User Doesn't Exists");
			}
			RpcHelper.writeJsonObject(response, obj);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			connection.close();
		}
	}

}
