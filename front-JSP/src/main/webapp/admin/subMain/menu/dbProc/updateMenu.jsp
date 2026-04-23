<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/db_conn.jsp" %>
<%
    request.setCharacterEncoding("UTF-8");

    int menuNo    = Integer.parseInt(request.getParameter("menuNo"));
    String mName  = request.getParameter("mName");
    String mUrl   = request.getParameter("mUrl");
    int sortOrder = request.getParameter("sortOrder") == null || request.getParameter("sortOrder").equals("")
                    ? 0 : Integer.parseInt(request.getParameter("sortOrder"));
    int mUse      = Integer.parseInt(request.getParameter("mUse"));

    String result = "fail";

    try {
        SQL = "UPDATE menu "
            + "SET m_name = ?, m_url = ?, sort_order = ?, m_use = ? "
            + "WHERE m_no = ?";

        pstmt = con.prepareStatement(SQL);
        pstmt.setString(1, mName);
        pstmt.setString(2, mUrl);
        pstmt.setInt(3, sortOrder);
        pstmt.setInt(4, mUse);
        pstmt.setInt(5, menuNo);

        int cnt = pstmt.executeUpdate();
        pstmt.close();

        if(cnt > 0) result = "success";

    } catch(Exception e) {
        result = "fail";
    }
%>
<%@ include file="/common/db_close.jsp" %>
{"result": "<%=result%>"}