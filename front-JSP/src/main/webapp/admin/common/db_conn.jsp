<%@ page import="java.sql.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="java.text.*"%>
<%@ page import="org.apache.commons.lang.StringEscapeUtils"%>
<%
  request.setCharacterEncoding("utf-8");

  Class.forName("com.mysql.jdbc.Driver");
  String url = "jdbc:mysql://localhost:3306/eduTravel";
  String mysql_id = "root";
  String mysql_pw = "0816abcd";
    // ----------
  // ----------
  // ----------
  // ----------
  // ----------
  // ----------
  // ----------
  Connection con = DriverManager.getConnection(url,mysql_id,mysql_pw);

  ResultSet rs = null;
  PreparedStatement pstmt = null;
  String SQL = null;
%>
