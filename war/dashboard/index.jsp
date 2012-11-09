<%-- 
/*
 * Copyright 2012 Kazumune Katagiri. (http://d.hatena.ne.jp/nemuzuka)
 * Licensed under the Apache License v2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
 --%>
<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>
<c:import url="/dashboard/layout.jsp">
<c:param name="title" value="Group Schedule Borad 【Koshiji】"/>

<c:param name="content">

<script type="text/javascript" src="/js/dashboard/header.js"></script>
<script type="text/javascript" src="/js/dashboard/dashboard.js"></script>
<script type="text/javascript" src="/js/schedule/common.js"></script>
<script type="text/javascript" src="/js/schedule/weekCommon.js"></script>
<script type="text/javascript" src="/js/schedule/week4LoginMember.js"></script>

<div class="widget">
<form class="form-horizontal">
	<div class="schedule_daycrl" id="schedule_daycrl" width="100%">
	</div>

	<table class="schedule_table" id="schedule_area">
	</table>

	<br />

	<h3 id="unread_message_title"></h3>
	<div id="unread_message_list_area">
	</div>
</form>
</div>

<input type="hidden" id="selected_menu" value="dashboard" />

</c:param>

</c:import>