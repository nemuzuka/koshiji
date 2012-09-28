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
<c:import url="/message/layout.jsp">
<c:param name="title" value="Group Schedule Borad 【Koshiji】"/>

<c:param name="subMenu">
	<div class="controls" align="center">
		<textarea id="body" class="body" placeholder="伝えたいこと、ある？"></textarea>
	</div>
	<div id="message_address" class="controls message_address">
		<label class="checkbox"><input type="checkbox" name="send_target" checked="checked" value="all">全員</label>
        <c:forEach var="member" items="${members}">
          <label class="checkbox"><input type="checkbox" name="send_target" value="${f:h(member.value)}">${f:h(member.label)}</label>
        </c:forEach>
	</div>
	<div align="center" style="padding-top:10px;" class="controls">
		<button id="message_create" class="btn btn-primary">メッセージを送る</button>
	</div>

</c:param>
  
<c:param name="content">

<script type="text/javascript" src="/js/message/message.js"></script>

<div class="widget">
<form class="form-horizontal">
	<div id="result_area" class="message_list_top">
	</div>
	<div id="footer_area" class="message_list_footer">
	</div>
</form>
</div>

<input type="hidden" id="selected_menu" value="menu1" />

</c:param>

</c:import>