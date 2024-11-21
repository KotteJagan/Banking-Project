package bank;

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
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/Transaction")
public class Transaction extends HttpServlet {
    private static final long serialVersionUID = 1L;
    String url = System.getenv("DATABASE_URL2");
    String un = System.getenv("DATABASE_USER");
    String pw = System.getenv("DATABASE_PASSWORD");
    Connection con = null;
    PreparedStatement pstmtDeposit = null;
    PreparedStatement pstmtWithdraw = null;
    PreparedStatement pstmtTransferFrom = null;
    PreparedStatement pstmtTransferTo = null;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(url, un, pw);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new ServletException("Error establishing database connection.", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountNo = req.getParameter("an");

        PrintWriter writer = resp.getWriter();
        resp.setContentType("text/html");

        // Start the HTML structure with header and navigation
        writer.println("<html>");
        writer.println("<head>");
        writer.println("<meta charset=\"UTF-8\">");
        writer.println("<title>Bank of Bengaluru - Transaction Details</title>");
        writer.println("<style>");
        writer.println("body { font-family: 'Roboto', sans-serif; margin: 0; padding: 0; background-color: #f4f4f4; }");
        writer.println("header { display: flex; justify-content: space-between; align-items: center; padding: 10px; background-color: #003366; color: white; }");
        writer.println(".logo img { width: 100px; height: auto; padding: 10px; }");
        writer.println("nav ul { list-style: none; display: flex; gap: 10px; }");
        writer.println("nav a { color: white; text-decoration: none; border-radius: 5px; }");
        writer.println("nav a:hover { background-color: #004080; }");
        writer.println("footer { text-align: center; padding: 20px; background-color: #003366; color: white; }");
        writer.println("table, th, td { border: 1px solid black; border-collapse: collapse; }");
        writer.println("th, td { padding: 5px; }");
        writer.println("h2 { margin-bottom: 15px; }");
        writer.println(".all { padding: 20px; }");
        writer.println(".trans {");
        writer.println("display: flex;");
        writer.println("justify-content: center;");
        writer.println("align-items: center;");
        writer.println("flex-direction: column;");
        writer.println("margin-top: 20px;");
        writer.println("}");

        // Media print CSS: Hide everything except the `.trans` class during printing
        writer.println("@media print {");
        writer.println("body * { visibility: hidden; }");
        writer.println(".trans, .trans * { visibility: visible; }");
        writer.println(".trans { position: absolute; top: 0; left: 0; width: 100%; }");
        writer.println("}");
        writer.println("</style>");
        writer.println("<script>");
        writer.println("function printContent() { window.print(); }");  // JavaScript function to print the content
        writer.println("</script>");
        writer.println("</head>");
        writer.println("<body>");

        // Header and navigation
        writer.println("<header>");
        writer.println("<div class=\"logo\"><img src=\"logo.png\" alt=\"Bank of Bengaluru Logo\"></div>");
        writer.println("<h1>Bank of Bengaluru</h1>");
        writer.println("<nav>");
        writer.println("<ul>");
        writer.println("<li><a href=\"mainpage.html\" class=\"home\">Home</a></li>");
        writer.println("<li><a href=\"create.html\">Create Account</a></li>");
        writer.println("<li><a href=\"Deposit.html\">Deposit Money</a></li>");
        writer.println("<li><a href=\"Withdraw.html\">Withdraw Money</a></li>");
        writer.println("<li><a href=\"Transfer.html\">Transfer Money</a></li>");
        writer.println("<li><a href=\"Transaction.html\">Transaction Details</a></li>");
        writer.println("<li><a href=\"About.html\">About Us</a></li>");
        writer.println("</ul>");
        writer.println("</nav>");
        writer.println("</header>");
        writer.println("<h1 class=\"all\"> All Transactions: </h1>");

        // Main content: Add the print button and transaction details
        writer.println("<div class='trans'>");

       
       

        try {
            // Fetch deposit transactions
            String queryDeposit = "SELECT * FROM deposit WHERE Account_Number = ?";
            pstmtDeposit = con.prepareStatement(queryDeposit);
            pstmtDeposit.setString(1, accountNo);
            ResultSet rsDeposit = pstmtDeposit.executeQuery();

            writer.println("<h2>Deposited Transactions</h2>");
            if (!rsDeposit.isBeforeFirst()) {
                writer.println("<p>No deposits found on this Account: " + accountNo + "</p>");
            } else {
                writer.println("<table border='1'><tr><th>Account Number</th><th>Amount</th><th>Deposited Time</th></tr>");
                while (rsDeposit.next()) {
                    writer.println("<tr><td>" + rsDeposit.getString("Account_Number") + "</td>");
                    writer.println("<td>" + rsDeposit.getString("Deposit_Money") + "</td>");
                    writer.println("<td>" + rsDeposit.getDate("Date_Time") + "</td></tr>");
                }
                writer.println("</table>");
            }

            // Fetch withdrawal transactions
            String queryWithdraw = "SELECT * FROM withdraw WHERE Account_Number = ?";
            pstmtWithdraw = con.prepareStatement(queryWithdraw);
            pstmtWithdraw.setString(1, accountNo);
            ResultSet rsWithdraw = pstmtWithdraw.executeQuery();

            writer.println("<h2>Withdrawal Transactions</h2>");
            if (!rsWithdraw.isBeforeFirst()) {
                writer.println("<p>No withdrawals found on this Account: " + accountNo + "</p>");
            } else {
                writer.println("<table border='1'><tr><th>Account Number</th><th>Amount</th><th>Withdrawal Time</th></tr>");
                while (rsWithdraw.next()) {
                    writer.println("<tr><td>" + rsWithdraw.getString("Account_Number") + "</td>");
                    writer.println("<td>" + rsWithdraw.getInt("Withdraw_Money") + "</td>");
                    writer.println("<td>" + rsWithdraw.getDate("Date_Time") + "</td></tr>");
                }
                writer.println("</table>");
            }

            // Fetch money transferred from this account
            String queryTransferFrom = "SELECT * FROM transfer WHERE To_Account_Number = ?";
            pstmtTransferFrom = con.prepareStatement(queryTransferFrom);
            pstmtTransferFrom.setString(1, accountNo);
            ResultSet rsTransferFrom = pstmtTransferFrom.executeQuery();

            writer.println("<h2>Money Transferred Transactions</h2>");
            if (!rsTransferFrom.isBeforeFirst()) {
                writer.println("<p>No money transfers found on this Account: " + accountNo + "</p>");
            } else {
                writer.println("<table border='1'><tr><th>From Account Number</th><th>To Account Number</th><th>Transferred Time</th><th>Transferred Amount</th></tr>");
                while (rsTransferFrom.next()) {
                    writer.println("<tr><td>" + rsTransferFrom.getString("From_Account_Number") + "</td>");
                    writer.println("<td>" + rsTransferFrom.getString("To_Account_Number") + "</td>");
                    writer.println("<td>" + rsTransferFrom.getDate("Date_Time") + "</td>");
                    writer.println("<td>" + rsTransferFrom.getInt("Transfered_amount") + "</td></tr>");
                }
                writer.println("</table>");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            writer.println("<h3>Error while fetching transaction details. Please try again later.</h3>");
        }
        writer.println("<button onclick='printContent()'>Print Transactions</button>");  // Print button
        writer.println("</div>"); // End of main content
        
        // Footer
        writer.println("<footer>");
        writer.println("<p>&copy; 2024 Bank of Bengaluru. All rights reserved.</p>");
        writer.println("</footer>");

        writer.println("</body>");
        writer.println("</html>");
    }

    @Override
    public void destroy() {
        try {
            if (con != null) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
