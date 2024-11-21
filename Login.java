package bank;

import jakarta.servlet.ServletConfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/signin")
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Database connection parameters
    String url = System.getenv("DATABASE_URL2");
    String un = System.getenv("DATABASE_USER");
    String pw = System.getenv("DATABASE_PASSWORD");

    Connection con = null;
    PreparedStatement pstmt = null;
    String query = "select First_Name,Last_Name from signup where User_Id = ? and Passwords = ? ;";

    public Login() {
        super();
    }

    public void init(ServletConfig config) throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, un, pw);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new ServletException("Initialization failed", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userid = req.getParameter("ui");
        String password = req.getParameter("pw");

        try {
            pstmt = con.prepareStatement(query);
            pstmt.setString(1, userid);
            pstmt.setString(2, password);

            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
                // Fetch the first name of the user (assuming you have a column 'FirstName' in your DB)
            	String firstName = res.getString("First_Name");
                String lastName = res.getString("Last_Name");

                // Store the names in session
                HttpSession sess = req.getSession();
                sess.setAttribute("firstName", firstName);
                sess.setAttribute("lastName", lastName);

                resp.sendRedirect("mainpage.html");
            } else {
               
                resp.setContentType("text/html");
                PrintWriter out = resp.getWriter();
                out.println("<html>");
                out.println("<head><title>Login Failed</title></head>");
                out.println("<body>");
                out.println("<p>Login failed. Incorrect User ID or Password.</p>");
                out.println("</body>");
                out.println("</html>");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Error executing SQL query", e);
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

   
    }

