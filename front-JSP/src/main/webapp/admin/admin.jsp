<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<% request.setCharacterEncoding("utf-8");%>
<!doctype html>
<html>
<head>
	<meta charset="utf-8">
	<title>좋은변화 관리자페이지</title>
	<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0" />
	<meta name="format-detection" content="telephone=no, address=no, email=no" />
	<link rel="stylesheet" type="text/css" href="css/adminHeader.css">
	<link rel="stylesheet" type="text/css" href="css/normalize.css">
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@xpressengine/xeicon@2.3.3/xeicon.min.css">
</head>
<body>
<section class="login_sec">
	<div class="wrap">
		<div class="RLogin">
			<div class="LoginTitle">
				좋은변화 <br>
				관리자페이지
			</div>
			<input type="text" class="login_input" id="ad_id" placeholder="ID" onkeydown="EnterFilter()"/>
			<input type="password" class="pw_input" id="ad_pw" placeholder="PASSWORD" onkeydown="EnterFilter()"/>
			<button class="login_btn" onclick="login()"><i class="xi-arrow-right"></i></button>
			<button class="join_btn" onclick="location.href='join.jsp'">회원가입</button>
		</div>
	</div>
</section>

<script src="js/config.js"></script>
<script src="js/member/member.js"></script>
<script>
	function EnterFilter() {
		if (event.key == "Enter") login();
	}
</script>
</body>
</html>