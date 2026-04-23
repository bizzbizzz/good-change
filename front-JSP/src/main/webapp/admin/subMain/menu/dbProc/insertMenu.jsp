<%@ page language="java" contentType="application/json; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/db_conn.jsp" %>
<%
    request.setCharacterEncoding("UTF-8");

    int parentNo  = request.getParameter("parentNo") == null || request.getParameter("parentNo").equals("")
                    ? 0 : Integer.parseInt(request.getParameter("parentNo"));
    String mName  = request.getParameter("mName");
    String mUrl   = request.getParameter("mUrl");
    int sortOrder = request.getParameter("sortOrder") == null || request.getParameter("sortOrder").equals("")
                    ? 0 : Integer.parseInt(request.getParameter("sortOrder"));
    int mUse      = request.getParameter("mUse") == null ? 1 : Integer.parseInt(request.getParameter("mUse"));

    String result = "fail";

    try {   	
        SQL = "INSERT INTO menu (parent_no, m_name, m_url, sort_order, m_use, created_at) "
            + "VALUES (?, ?, ?, ?, ?, NOW())";

        pstmt = con.prepareStatement(SQL);
    	if(parentNo == 0) {
    	    pstmt.setNull(1, java.sql.Types.INTEGER);  // ✅ 0이면 NULL로 저장
    	} else {
    	    pstmt.setInt(1, parentNo);
    	}
        pstmt.setString(2, mName);
        pstmt.setString(3, mUrl);
        pstmt.setInt(4, sortOrder);
        pstmt.setInt(5, mUse);

        int cnt = pstmt.executeUpdate();
        pstmt.close();

        if(cnt > 0) result = "success";

    } catch(Exception e) {
        result = "fail";
    }
%>
<%@ include file="/common/db_close.jsp" %>
{"result": "<%=result%>"}