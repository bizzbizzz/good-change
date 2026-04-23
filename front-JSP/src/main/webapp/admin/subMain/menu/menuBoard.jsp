<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="dbProc/selectMenu.jsp" %>
<%@ include file="/common/menuName.jsp" %>

<c:set var="currentType" value="${param.type}" />

<main class="cd-main-content">
    <div class="content-wrapper">
        <div class="titleNav">
            ${currentMenuName}
        </div>

        <div class="wrap">
            <div class="over_hi" style="margin-top:60px;">
                <table class="adminTable">
                    <colgroup>
                        <col style="width:15%;"/>
                        <col style="width:20%;"/>
                        <col style="width:20%;"/>
                        <col style="width:10%;"/>
                        <col style="width:10%;"/>
                        <col style="width:15%;"/>
                        <col style="width:10%;"/>
                    </colgroup>
                    <tr>
                        <th>1depth</th>
                        <th>서브메뉴명</th>
                        <th>URL</th>
                        <th>정렬순서</th>
                        <th>사용여부</th>
                        <th>관리</th>
                        <th>삭제</th>
                    </tr>

                    <% if(menuTotal == 0) { %>
                    <tr>
                        <td colspan="7">등록된 메뉴가 없습니다.</td>
                    </tr>
                    <% } else {
                        java.util.LinkedHashMap<Integer, String> parentMap = new java.util.LinkedHashMap<Integer, String>();
                        java.util.LinkedHashMap<Integer, java.util.List<Object[]>> childMap = new java.util.LinkedHashMap<Integer, java.util.List<Object[]>>();

                        while(rs.next()) {
                            int    pNo        = rs.getInt("m_no");
                            String pName      = rs.getString("m_name");
                            int    cMNo       = rs.getInt("c_m_no");
                            String cMName     = rs.getString("c_m_name");
                            String cMUrl      = rs.getString("c_m_url") == null ? "" : rs.getString("c_m_url");
                            int    cSortOrder = rs.getInt("c_sort_order");
                            int    cMUse      = rs.getInt("c_m_use");

                            if(!parentMap.containsKey(pNo)) {
                                parentMap.put(pNo, pName);
                                childMap.put(pNo, new java.util.ArrayList<Object[]>());
                            }
                            if(cMNo > 0) {
                                childMap.get(pNo).add(new Object[]{cMNo, cMName, cMUrl, cSortOrder, cMUse});
                            }
                        }

                        for(java.util.Map.Entry<Integer, String> pEntry : parentMap.entrySet()) {
                            int    pNo      = pEntry.getKey();
                            String pName    = pEntry.getValue();
                            java.util.List<Object[]> children = childMap.get(pNo);
                            int rowspan = children.size() == 0 ? 1 : children.size() + 1;
                    %>
                    <tr>
                        <td rowspan="<%=rowspan%>" class="center"><%=pName%></td>
                        <td colspan="4" class="center">-</td>
                        <td class="center">
                            <button type="button" onclick="goToURL('menu', 'insert', <%=pNo%>)">서브추가</button>
                        </td>
                        <td class="center">
                            <button type="button" class="common-del" onclick="deleteMenu(<%=pNo%>)">삭제</button>
                        </td>
                    </tr>
                    <%
                        for(int i = 0; i < children.size(); i++) {
                            Object[] child    = children.get(i);
                            int    cMNo       = (int)child[0];
                            String cMName     = (String)child[1];
                            String cMUrl      = (String)child[2];
                            int    cSortOrder = (int)child[3];
                            int    cMUse      = (int)child[4];
                    %>
                    <tr>
                        <td class="center"><%=cMName%></td>
                        <td class="center"><%=cMUrl%></td>
                        <td class="center"><%=cSortOrder%></td>
                        <td class="center"><%=cMUse == 1 ? "사용" : "미사용"%></td>
                        <td class="center">
                            <button type="button" onclick="goToURL('menu', 'update', <%=cMNo%>)">수정</button>
                        </td>
                        <td class="center">
                            <button type="button" class="common-del" onclick="deleteMenu(<%=cMNo%>)">삭제</button>
                        </td>
                    </tr>
                    <% } %>
                    <% } %>
                    <% } %>
                </table>
            </div>

            <div class="right" style="margin-top:20px;">
                <input type="button" value="1depth 추가" class="common-del" onclick="goToURL('menu', 'insert');" />
            </div>
        </div>
    </div>
</main>
<%@ include file="/common/db_close.jsp" %>
<script src="js/board/menuFetch.js"></script>