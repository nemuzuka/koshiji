<%-- 
/*
 * Copyright 2012 Kazumune Katagiri. (http://d.hatena.ne.jp/nemuzuka)
 * Licensed under the Apache License v2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
 --%>
<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<html lang="jp">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta charset="utf-8">
<title>Group Schedule Borad 【Koshiji】</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">
<link href="/css/bootstrap.min.css" rel="stylesheet">
<link href="/css/bootstrap-responsive.min.css" rel="stylesheet">
<link href="/css/docs.css" rel="stylesheet">

<script type="text/javascript" src="/js/jquery-1.7.2.min.js"></script>
<script type="text/javascript" src="/js/bootstrap.min.js"></script>
<script type="text/javascript" src="/js/jquery-common.js"></script>
<script type="text/javascript" src="/js/date.js"></script>
<script type="text/javascript" src="/js/dateformat.js"></script>
<script type="text/javascript" src="/js/jquery.toaster.min.js"></script>
<script type="text/javascript" src="/js/jquery.blockUI.js"></script>
<script type="text/javascript">
<!--
$(function(){
	$("#create").on("click", function(){
		var params = {};
		params["jp.co.nemuzuka.token"] = $("#token").val();
		setAjaxDefault();
		return $.ajax({
			type: "POST",
			url: "/ajax/createAdminMember",
			data: params
		}).then(
			function(data) {
				moveUrl("/");
			}
		);
	});
	
	$("#not_create").on("click", function(){
		moveUrl("/logout");
	});
});
//-->
</script>

</head>
<body data-spy="scroll" data-target=".subnav" data-offset="50" data-twttr-rendered="true">
<div class="container">
<div class="well">
	<p class="lead">
	あなたのアドレスは、まだグループ管理者の方に登録されていないようです。<br />
	グループ管理者にお問い合わせ下さい。<br /><br />
	それとも、あなたを新しいグループ管理者として登録しますか？
	</p>
	
	<button id="create" class="btn btn-large btn-primary" type="button">はい、私を新しいグループ管理者として登録します</button>
	<button id="not_create" class="btn btn-link" type="button">いいえ、私を誘ったグループ管理者の登録を待ちます</button>
	
	<input type="hidden" id="token" value="${f:h(token)}"/>
</div>
</div>

</body>
</html>