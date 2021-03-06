<%-- 
/*
 * Copyright 2012 Kazumune Katagiri. (http://d.hatena.ne.jp/nemuzuka)
 * Licensed under the Apache License v2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
 --%>
<!DOCTYPE html>
<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<html lang="jp">
<c:import url="/import.jsp"/>

<body data-spy="scroll" data-target=".bs-docs-sidebar">

<c:import url="/header.jsp"/>
<c:import url="/schedule/dialog.jsp"/>
<!-- Contents -->
<div class="container-fluid">
	<div class="row-fluid">
		<div class="span">
			<section>
				<div class="well">
					${param.content}
					<input type="hidden" id="scheduleViewType" />
				</div>
			</div>
		</div>
	</div>
</div>
</body>
</html>
