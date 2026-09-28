package com.complaint;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/SubmitComplaintServlet")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 50)
public class SubmitComplaintServlet extends HttpServlet {

    private static final String DB_HOST = System.getenv("DB_HOST") != null
            ? System.getenv("DB_HOST")
            : "complaint_mysql";

    private static final String DB_USER = System.getenv("DB_USER") != null
            ? System.getenv("DB_USER")
            : "root";

    private static final String DB_PASS = System.getenv("DB_PASSWORD");

    private static final String DB_URL = "jdbc:mysql://" + DB_HOST
            + ":3306/complaint_db?useSSL=false&allowPublicKeyRetrieval=true";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Safety check: stop execution if environment variable is missing
        if (DB_PASS == null || DB_PASS.isEmpty()) {
            out.println(
                    "<h3 style='color:red;'>Configuration Error: DB_PASSWORD environment variable is not set.</h3>");
            return;
        }

        String name = request.getParameter("name");
        String mail = request.getParameter("mail");
        String dept = request.getParameter("dept");
        String roomNo = request.getParameter("room_no");
        String subject = request.getParameter("sub");
        String complaint = request.getParameter("complnt");

        Part filePart = request.getPart("proof");
        String fileName = (filePart != null && filePart.getSize() > 0) ? filePart.getSubmittedFileName() : null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                String sql = "INSERT INTO complaints (name, email, department, room_number, subject, complaint_text, file_name) VALUES (?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, name);
                    stmt.setString(2, mail);
                    stmt.setString(3, dept);
                    stmt.setString(4, roomNo);
                    stmt.setString(5, subject);
                    stmt.setString(6, complaint);
                    stmt.setString(7, fileName);

                    int rowsInserted = stmt.executeUpdate();
                    if (rowsInserted > 0) {
                        out.println("<h2 style='color:green;'>Complaint Submitted Successfully!</h2>");
                        out.println("<a href='index.html'>Submit Another Complaint</a>");
                    } else {
                        out.println("<h2 style='color:red;'>Failed to submit complaint.</h2>");
                    }
                }
            }
        } catch (Exception e) {
            out.println("<h3 style='color:red;'>Error: " + e.getMessage() + "</h3>");
            e.printStackTrace(out);
        }
    }
}