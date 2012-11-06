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
<c:import url="/schedule/dialog.jsp"/>

<body data-spy="scroll" data-target=".bs-docs-sidebar">

<c:import url="/header.jsp"/>
<!-- Contents -->
<div class="container-fluid">
	<div class="row-fluid">
		<div class="span4">
			<section>
				<div class="well sidebar-nav">
					${param.subMenu}
				</div>
				<div class="well">
					<div id="schedule_area" width="100%"></div>
				</div>
			</section>
		</div>
		<div class="span8">
			<section>
				<div class="well">
					${param.content}
				</div>
			</div>
		</div>
	</div>
</div>
</body>
</html>
