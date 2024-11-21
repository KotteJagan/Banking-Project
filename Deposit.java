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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@WebServlet("/Deposit")
public class Deposit extends HttpServlet {
	
	 private static final long serialVersionUID = 1L;
	    String url = System.getenv("DATABASE_URL2");
	    String un = System.getenv("DATABASE_USER");
	    String pw = System.getenv("DATABASE_PASSWORD");
	    Connection con = null;
	    PreparedStatement pstmt = null;
	    String query  = "insert into deposit values (?, ?, ?);";
	    
	    public Deposit() {
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
	        String acnumber = req.getParameter("an");
	        String amount = req.getParameter("am");
	        LocalDateTime currentDateTime = LocalDateTime.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	        String date = currentDateTime.format(formatter);

	        
	        try {
	            pstmt = con.prepareStatement(query);
	           
	            pstmt.setString(1, acnumber);
	            pstmt.setString(2, amount);
	            pstmt.setString(3, date);
	            

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
	                out.println("<h2>Amount Deposited Successfully</h2>");
	                out.println("<p><strong>Account Number:</strong> " + acnumber + "</p>");
	                out.println("<p><strong>Amount:</strong> " + amount + "</p>");
	                out.println("<p><strong>Date & Time:</strong> " + date + "</p>");
	                out.println("<form action='Deposit.html'>");
	                out.println("<input type='submit' value='Deposit Again' class='login-button'>");
	                out.println("</form>");
	            } else {
	                out.println("<p class=\"no-record\">Money Depositing failed. Please try again.</p>");
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
