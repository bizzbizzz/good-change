<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="comm" value="${param.comm}" />
<c:choose>
    <c:when test="${comm eq 'list'}">
        <jsp:include page="member/list.jsp" />
    </c:when>
</c:choose>