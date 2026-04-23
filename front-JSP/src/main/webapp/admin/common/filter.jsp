<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<%
String placeHolder = (String) request.getAttribute("place_holder");
%>

<table class="popTable">
	<form id="search" method="get" action="" onsubmit="return false">
         <tr>
             <td>
                 <div class="filter">
                     
                     <!-- 처리상태 필터 (왼쪽) -->
                     <select name="process" id="processFilter" onchange="search('${currentType}', '<%=comm%>')">
                         <option value="">전체</option>
                         <option value="0" <%= "0".equals(request.getParameter("process")) ? "selected" : "" %>>접수대기</option>
                         <option value="1" <%= "1".equals(request.getParameter("process")) ? "selected" : "" %>>처리중</option>
                         <option value="2" <%= "2".equals(request.getParameter("process")) ? "selected" : "" %>>처리완료</option>
                     </select>
                     

                     <!-- 검색어 (오른쪽) -->
                     <div style="display:flex; align-items:center; gap:10px;">
                         <input type="text" name="keyword" value="<%=keyword%>"  placeholder="<%=placeHolder%>">
                         <div class="search" onclick="search('${currentType}','<%=comm%>')">
                             <button type="button">검색</button>
                         </div>
                     </div>

                 </div>
             </td>
         </tr>
     </form>
</table>