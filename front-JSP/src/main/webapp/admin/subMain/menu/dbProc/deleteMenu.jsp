<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/db_conn.jsp" %>
<%
    request.setCharacterEncoding("UTF-8");

    int menuNo = request.getParameter("menuNo") == null || request.getParameter("menuNo").equals("")
                 ? 0 : Integer.parseInt(request.getParameter("menuNo"));

    String result = "fail";

    try {
        // 서브메뉴 먼저 삭제
        SQL = "DELETE FROM menu WHERE parent_no = ?";
        pstmt = con.prepareStatement(SQL);
        pstmt.setInt(1, menuNo);
        pstmt.executeUpdate();
        pstmt.close();

        // 해당 메뉴 삭제
        SQL = "DELETE FROM menu WHERE m_no = ?";
        pstmt = con.prepareStatement(SQL);
        pstmt.setInt(1, menuNo);
        int cnt = pstmt.executeUpdate();
        pstmt.close();

        if(cnt > 0) result = "success";

    } catch(Exception e) {
        result = "fail";
    }
%>
<%@ include file="/common/db_close.jsp" %>
{"result": "<%=result%>"}