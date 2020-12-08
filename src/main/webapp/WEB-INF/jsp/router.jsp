<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:choose>
    <c:when test="${path == ''}">
        <%@ include file="component/sync/login.jsp" %>
    </c:when>
</c:choose>
