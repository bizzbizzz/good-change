<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/db_conn.jsp" %>
<%
    int menuAdNo      = (Integer)session.getAttribute("ad_no");
    final int menuRowSize = 20;
    final int menuBlock   = 5;
    int menuPg = 1;
    String menuKeyword = request.getParameter("keyword") != null ? request.getParameter("keyword").trim() : "";

    if(request.getParameter("pg") != null && !request.getParameter("pg").trim().equals("")) {
        menuPg = Integer.parseInt(request.getParameter("pg"));
    }

    int menuStart     = (menuPg - 1) * menuRowSize;
    int menuTotal     = 0;
    int menuAllPage   = 0;
    int menuStartPage = ((menuPg - 1) / menuBlock) * menuBlock + 1;
    int menuEndPage   = menuStartPage + menuBlock - 1;

    try {
        String menuCountSQL = "SELECT COUNT(*) FROM menu WHERE parent_no IS NULL ";
        if(!menuKeyword.equals("")) menuCountSQL += "AND m_name LIKE ? ";

        pstmt = con.prepareStatement(menuCountSQL);
        int menuIdx = 1;
        if(!menuKeyword.equals("")) pstmt.setString(menuIdx++, "%" + menuKeyword + "%");

        rs = pstmt.executeQuery();
        if(rs.next()) menuTotal = rs.getInt(1);
        rs.close();
        pstmt.close();

        menuAllPage = (int)Math.ceil(menuTotal / (double)menuRowSize);
        if(menuEndPage > menuAllPage) menuEndPage = menuAllPage;

        SQL = "SELECT p.m_no, p.m_name, p.m_url, p.m_use, p.sort_order, "
        	    + "       c.m_no AS c_m_no, c.m_name AS c_m_name, c.m_url AS c_m_url, "
        	    + "       c.m_use AS c_m_use, c.sort_order AS c_sort_order "
        	    + "FROM menu p "
        	    + "LEFT JOIN menu c ON p.m_no = c.parent_no "
        	    + "WHERE p.parent_no IS NULL OR p.parent_no = 0 ";  // ✅ 0도 포함
        	if(!menuKeyword.equals("")) SQL += "AND p.m_name LIKE ? ";
        	SQL += "ORDER BY p.sort_order ASC, c.sort_order ASC";

        pstmt = con.prepareStatement(SQL);
        menuIdx = 1;
        if(!menuKeyword.equals("")) pstmt.setString(menuIdx++, "%" + menuKeyword + "%");

        rs = pstmt.executeQuery();

    } catch(Exception e) {
        out.print(e);
    }
%>