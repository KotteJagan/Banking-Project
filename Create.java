package bank;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;

@WebServlet("/Create")
public class Create extends HttpServlet {
    private static final long serialVersionUID = 1L;
    String url = System.getenv("DATABASE_URL2");
    String un = System.getenv("DATABASE_USER");
    String pw = System.getenv("DATABASE_PASSWORD");
    Connection con = null;
    PreparedStatement pstmt = null;
    String query  = "insert into create_account values (?, ?, ?, ?, ?, ?, ?);";
    
    public Create() {
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
        String fname = req.getParameter("fn");
        String mobile = req.getParameter("num");
        String email = req.getParameter("em");
        String address = req.getParameter("ad");
        String money = req.getParameter("dm");

        // Generate a unique 12-digit account number
        String accountnum = String.format("%012d", ThreadLocalRandom.current().nextLong(100000000000L, 1000000000000L));
        String mpin = req.getParameter("mp");
        try {
            pstmt = con.prepareStatement(query);
           
            pstmt.setString(1, fname);
            pstmt.setString(2, mobile);
            pstmt.setString(3, email);
            pstmt.setString(4, address);
            pstmt.setString(5, money);
            pstmt.setString(6, accountnum);
            pstmt.setString(7, mpin);

            int res = pstmt.executeUpdate();

            resp.setContentType("text/html");
            PrintWriter out = resp.getWriter();
            out.println("<html>");
            out.println("<head><title>Results</title>");
            out.println("<style>");
            out.println("body {font-family: Arial, sans-serif; background-color: #f4f4f4; color: #333; margin: 0; padding: 20px;}");
            out.println(".container {max-width: 600px; margin: 0 auto; background-color: #fff; padding: 20px; border-radius: 10px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);}");
            out.println("body {font-family: Arial, sans-serif; background-color: #f4f4f4; color: #333; margin: 0; padding: 20px;background-image: url('bank.jpg'); background-size: cover; background-repeat: no-repeat;background-position: center;min-height: 100vh;}");
            out.println("h2 {color: #2c3e50; margin-bottom: 20px;}");
            out.println("p {margin: 10px 0; font-size: 16px;}");
            out.println(".no-record {color: #e74c3c; font-weight: bold;}");
            out.println(".login-button {margin-top: 20px; padding: 10px 20px; background-color: #4CAF50; color: white; border: none; cursor: pointer; font-size: 16px; border-radius: 5px;}");
            out.println(".login-button:hover {background-color: #45a049;}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class=\"container\">");

            if (res > 0) {
                out.println("<h2>Account Created Successfully</h2>");
                out.println("<p><strong>Account Number:</strong> " + accountnum + "<p> (Note this Account Number) </p>");
                out.println("<p><strong>Account Number:</strong> " + mpin + "<p> (Remember this Mpin for Transactions) </p>");
                out.println("<p><strong>Full Name:</strong> " + fname + "</p>");
                out.println("<p><strong>Mobile Number:</strong> " + mobile + "</p>");
                out.println("<p><strong>Email Id:</strong> " + email + "</p>");
                out.println("<p><strong>Address:</strong> " + address + "</p>");
                
                out.println("<form action='mainpage.html'>");
                out.println("<input type='submit' value='Home' class='login-button'>");
                out.println("</form>");
            } else {
                out.println("<p class=\"no-record\">Account Creation failed. Please try again.</p>");
            }

            out.println("</div>");
            out.println("</body>");
            out.println("</html>");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Error executing SQL query", e);
        } finally {
            
        }
    }

    @Override
    public void destroy() {
        try {
            if (pstmt != null) pstmt.close();
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
