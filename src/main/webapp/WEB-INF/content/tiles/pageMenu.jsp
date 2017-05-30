<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<ul>
<c:forEach items="${requestScope.menuItems}" var="item">
<li><a href="${item.href}">${item.description}</a></li>
</c:forEach>
</ul>