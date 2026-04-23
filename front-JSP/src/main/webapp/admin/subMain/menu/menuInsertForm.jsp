<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/common/db_conn.jsp" %>
<%@ include file="dbProc/selectOneMenu.jsp" %>
<%@ include file="/common/menuName.jsp" %>

<c:set var="isUpdate" value="${param.comm eq 'update'}" />
<c:set var="type" value="${param.type}" />

<main class="cd-main-content">
    <div class="content-wrapper">
        <div class="titleNav">
            ${currentMenuName} 수정
        </div>
        
        <c:if test="${param.comm eq 'insert'}">
		    <input type="hidden" id="parentNo" value="<%=parentNo%>">
		</c:if>

        <div class="wrap">
            <div class="over_hi" style="margin-top:60px;">
                <form id="updateFrm" method="post">
                    <input type="hidden" id="board_num" value="<%=menuNo%>">

                    <table class="adminTable">
                        <tr>
                            <th style="width:200px">상위메뉴</th>
                            <td class="left"><%=parentName%></td>
                        </tr>
                        <tr>
                            <th style="width:200px">메뉴명</th>
                            <td class="left">
                                <input type="text" name="mName" id="mName" value="<%=mName%>">
                            </td>
                        </tr>
                        <tr>
                            <th style="width:200px">URL</th>
                            <td class="left">
                                <input type="text" name="mUrl" id="mUrl" value="<%=mUrl%>">
                            </td>
                        </tr>
                        <tr>
                            <th style="width:200px">정렬순서</th>
                            <td class="left">
                                <input type="number" name="sortOrder" id="sortOrder" value="<%=sortOrder%>">
                            </td>
                        </tr>
                        <tr>
                            <th style="width:200px">사용여부</th>
                            <td class="left">
                                <select name="mUse" id="mUse">
                                    <option value="1" <%=mUse == 1 ? "selected" : ""%>>사용</option>
                                    <option value="0" <%=mUse == 0 ? "selected" : ""%>>미사용</option>
                                </select>
                            </td>
                        </tr>
                    </table>
                </form>

                <div class="right">
                    <input type="button" value="목록으로" class="common-del" onclick="goToURL('${type}', 'select');" />
                    <c:if test="${param.comm eq 'update'}">
                        <input type="button" value="수정하기" class="common-del" onclick="updateMenu();" />
                    </c:if>
                    
  
					<c:if test="${param.comm eq 'insert'}">
					    <input type="button" value="등록하기" class="common-del" onclick="insertMenu();" />
					</c:if>
                   
                </div>
            </div>
        </div>
    </div>
</main>
<script src="js/board/menuFetch.js"></script>
<%@ include file="/common/db_close.jsp" %>