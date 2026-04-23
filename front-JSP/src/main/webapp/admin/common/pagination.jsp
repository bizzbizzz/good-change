<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<c:set var="type" value="${param.type}" />

<% if(total != 0) { %>
<ul class="center pageing">
    <% if(pg > block){ %>
        <li class="num">
            <a href="<%=pageUrl%>?pg=1&keyword=<%=keyword%>&type=${currentType }>&comm=<%=comm%>">《</a>
        </li>
        <li class="num">
            <a href="<%=pageUrl%>?pg=<%=startPage-1%>&keyword=<%=keyword%>&type=${currentType }&comm=<%=comm%>">〈</a>
        </li>
    <% } %>

    <% for(int i = startPage; i <= endPage; i++){ %>
        <% if(i == pg){ %>
            <li class="num_on"><a><%=i%></a></li>
        <% } else { %>
            <li class="num">
                <a href="<%=pageUrl%>?pg=<%=i%>&keyword=<%=keyword%>&type=${currentType }&comm=<%=comm%>"><%=i%></a>
            </li>
        <% } %>
    <% } %>

    <% if(endPage < allPage){ %>
        <li class="num">
            <a href="<%=pageUrl%>?pg=<%=endPage+1%>&keyword=<%=keyword%>&type=${currentType }&comm=<%=comm%>">〉</a>
        </li>
        <li class="num">
            <a href="<%=pageUrl%>?pg=<%=allPage%>&keyword=<%=keyword%>&type=${currentType }&comm=<%=comm%>">》</a>
        </li>
    <% } %>
</ul>
<% } %>