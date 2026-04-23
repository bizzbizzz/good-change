<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    int menuNo    = 0;
    String mName  = "";
    String mUrl   = "";
    int sortOrder = 0;
    int mUse      = 1;
    int parentNo  = 0;
    String parentName = "";

    String boardNumParam = request.getParameter("board_num");
    String commParam     = request.getParameter("comm");

    if(boardNumParam != null && !boardNumParam.trim().equals("")) {

        if("insert".equals(commParam)) {
            // ✅ insert일 때는 board_num이 parentNo
            parentNo = Integer.parseInt(boardNumParam);

            // 상위메뉴명 조회
            try {
                SQL = "SELECT m_name FROM menu WHERE m_no = ?";
                pstmt = con.prepareStatement(SQL);
                pstmt.setInt(1, parentNo);
                rs = pstmt.executeQuery();
                if(rs.next()) {
                    parentName = rs.getString("m_name");
                }
                rs.close();
                pstmt.close();
            } catch(Exception e) {
                out.print(e);
            }

        } else {
            // ✅ update일 때는 board_num이 menuNo
            try {
                menuNo = Integer.parseInt(boardNumParam);

                SQL = "SELECT m.m_no, m.m_name, m.m_url, m.sort_order, m.m_use, "
                    + "       m.parent_no, p.m_name AS parent_name "
                    + "FROM menu m "
                    + "LEFT JOIN menu p ON m.parent_no = p.m_no "
                    + "WHERE m.m_no = ?";

                pstmt = con.prepareStatement(SQL);
                pstmt.setInt(1, menuNo);
                rs = pstmt.executeQuery();

                if(rs.next()) {
                    mName      = rs.getString("m_name")     == null ? "" : rs.getString("m_name");
                    mUrl       = rs.getString("m_url")      == null ? "" : rs.getString("m_url");
                    sortOrder  = rs.getInt("sort_order");
                    mUse       = rs.getInt("m_use");
                    parentNo   = rs.getInt("parent_no");
                    parentName = rs.getString("parent_name") == null ? "없음" : rs.getString("parent_name");
                }

                rs.close();
                pstmt.close();

            } catch(Exception e) {
                out.print(e);
            }
        }
    }
%>