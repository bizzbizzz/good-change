<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href='https://fonts.googleapis.com/css?family=Open+Sans:300,400,700' rel='stylesheet' type='text/css'>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <link rel="stylesheet" href="css/reset.css">
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/admin.css">
    <link rel="stylesheet" href="css/adminHeader.css">
    <link rel="stylesheet" href="css/popUp.css">
    <link rel="stylesheet" href="css/common.css">
    <script src="js/jquery-2.1.4.js"></script>
    <script type="text/javascript" src="../se2/js/HuskyEZCreator.js"></script>
    <script src="js/jquery.menu-aim.js"></script>
    <script src="js/main.js"></script>
    <script src="js/common.js"></script>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <title>관리자페이지</title>
</head>
<c:set var="type" value="${param.type}" />
<c:set var="comm" value="${param.comm}" />
<body>

    <c:choose>
        <c:when test="${type eq 'member'}">
            <jsp:include page="subMain/memberRouter.jsp" />
        </c:when>
    </c:choose>

</body>
</html>