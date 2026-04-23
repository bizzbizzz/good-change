<% 
try {} catch(Exception e) {} finally {
  if(rs != null) try{rs.close();}catch(Exception e){}
  if(pstmt != null) try{pstmt.close();}catch(Exception e){}
  if(con != null) try{con.close();}catch(Exception e){}
}
%>
<%--<%@ include file="mysql/JDBCClose.jsp" %>--%>