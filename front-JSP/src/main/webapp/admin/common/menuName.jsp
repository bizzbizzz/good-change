<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="currentType" value="${param.type}" />
<c:set var="currentComm" value="${param.comm}" />


<c:forEach var="menu" items="${menuList}">
    <c:set var="menuType" value="${fn:substringAfter(fn:substringBefore(menu.url, '&'), 'type=')}" />
    <c:if test="${menuType eq currentType}">
        <c:set var="currentMenuName" value="${menu.name}" />
    </c:if>
    <c:if test="${not empty menu.children}">
        <c:forEach var="child" items="${menu.children}">
            <c:set var="childType" value="${fn:substringAfter(fn:substringBefore(child.url, '&'), 'type=')}" />
            <c:if test="${childType eq currentType}">
                <c:set var="currentMenuName" value="${child.name}" />
            </c:if>
        </c:forEach>
    </c:if>
</c:forEach>