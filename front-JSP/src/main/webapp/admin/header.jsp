<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page import="java.io.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<% request.setCharacterEncoding("utf-8"); %>


<%
	String token = (String) session.getAttribute("token");
	if(token == null){
%>
<script>
	alert('세션이 만료되었습니다.\n로그인을 해주시기 바랍니다.');
	location.href = "admin.jsp";
</script>
<%
		return;
	}
%>


<c:set var="menuList" scope="request" value="${[
    {
        'name':'정보관리',
        'url':'index.jsp?type=info&comm=select',
        'children':[{
            'name':'회원정보',
            'url':'index.jsp?type=info&comm=member'
        },
        {
            'name':'시스템설정',
            'url':'index.jsp?type=info&comm=system'
        }]
    },
    {
        'name':'수혜자관리',
        'url':'index.jsp?type=member&comm=list',
        'children':[{
            'name':'수혜자 목록',
            'url':'index.jsp?type=member&comm=list'
        },
        {
            'name':'수혜자 등록',
            'url':'index.jsp?type=member&comm=join'
        }]
    },
    {
        'name':'가맹점관리',
        'url':'index.jsp?type=merchant&comm=list',
        'children':[{
            'name':'가맹점 목록',
            'url':'index.jsp?type=merchant&comm=list'
        },
        {
            'name':'가맹점 등록',
            'url':'index.jsp?type=merchant&comm=join'
        }]
    },
    {
        'name':'포인트지급',
        'url':'index.jsp?type=point&comm=select',
        'children':[{
            'name':'포인트 목록',
            'url':'index.jsp?type=point&comm=list'
        },
        {
            'name':'포인트 지급',
            'url':'index.jsp?type=point&comm=join'
        }]
    },
    {
        'name':'한도설정',
        'url':'index.jsp?type=limit&comm=select',
        'children':[{
            'name':'한도 목록',
            'url':'index.jsp?type=limit&comm=list'
        },
        {
            'name':'한도 설정',
            'url':'index.jsp?type=limit&comm=join'
        }]
    },
    {
        'name':'사용내역',
        'url':'index.jsp?type=payment&comm=list',
        'children':[{
            'name':'결제 내역',
            'url':'index.jsp?type=payment&comm=list'
        },
        {
            'name':'취소 내역',
            'url':'index.jsp?type=payment&comm=cancel'
        }]
    },
    {
        'name':'정산관리',
        'url':'index.jsp?type=settlement&comm=list',
        'children':[{
            'name':'정산 목록',
            'url':'index.jsp?type=settlement&comm=list'
        },
        {
            'name':'정산 등록',
            'url':'index.jsp?type=settlement&comm=join'
        }]
    },
    {
        'name':'자료센터관리',
        'url':'index.jsp?type=data&comm=list',
        'children':[{
            'name':'자료 목록',
            'url':'index.jsp?type=data&comm=list'
        },
        {
            'name':'자료 등록',
            'url':'index.jsp?type=data&comm=join'
        }]
    }
]}" />


<header class="cd-main-header">
<a href="#0" class="cd-logo">
	한국교육여행연구소 관리자
  <%-- <img src="image/logo.png" alt="Logo"> --%>
</a>



<a href="#0" class="cd-nav-trigger">Menu<span></span></a>


<!--회원정보 수정 S-->
<nav class="cd-nav">
  <ul class="cd-top-nav">
    <li class="has-children account">
      <a href="#0">
        <img src="image/cd-avatar.png" alt="avatar">
          ${sessionScope.ad_name}
      </a>
      <ul>
        <li><a href="adminReset.jsp">회원정보 수정</a></li>
        <li><a href="logoutProc.jsp">Logout</a></li>
      </ul>
    </li>
  </ul>
</nav>
<!--회원정보 수정 E-->

</header> <!-- .cd-main-header -->

<nav class="cd-side-nav">
    <ul class="gnb">
	    <li class="cd-label">Main</li>
	    
	    <c:forEach var="menu" items="${menuList}">
	        <li class="overview ${not empty menu.children ? 'has-children' : ''} ${not empty menu.cls ? menu.cls : ''}">
	            <a href="${menu.url}">
	                ${menu.name}
	                <c:if test="${not empty menu.children}"> ···</c:if>
	            </a>
	
	            <c:if test="${not empty menu.children}">
	                <ul>
	                    <c:forEach var="child" items="${menu.children}">
	                        <li><a href="${child.url}">${child.name}</a></li>
	                    </c:forEach>
	                </ul>
	            </c:if>
	        </li>
	    </c:forEach>
	    
	</ul>
	
</nav>
