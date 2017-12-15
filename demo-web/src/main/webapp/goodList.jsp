<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
	String path = request.getContextPath();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	登录成功
	<hr />
	商品列表：
	<ol>
	<c:forEach items="${goodList}" var="good">
		<li>${good.name}(${good.price})</li>		
	</c:forEach>
	</ol>
</body>
</html>