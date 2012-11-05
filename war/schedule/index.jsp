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
<c:import url="/schedule/layout.jsp">
<c:param name="title" value="Group Schedule Borad 【Koshiji】"/>

<c:param name="content">

<script type="text/javascript" src="/js/schedule/header.js"></script>
<script type="text/javascript" src="/js/schedule/common.js"></script>
<script type="text/javascript" src="/js/schedule/week.js"></script>
<script type="text/javascript" src="/js/schedule/month.js"></script>

<div class="widget">
<form class="form-horizontal">
	<div class="widget-header">
		<h3 class="title" id="schedule_title">週次スケジュール</h3>
	</div>
	<div class="widget-content">
	
	<div class="schedule_daycrl" id="schedule_daycrl" width="100%">
	</div>
	
	<table class="schedule_table" id="schedule_area">
	</table>
</form>
</div>

<input type="hidden" id="selected_menu" value="schedule" />

</c:param>

</c:import>