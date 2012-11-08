<%-- 
/*
 * Copyright 2012 Kazumune Katagiri. (http://d.hatena.ne.jp/nemuzuka)
 * Licensed under the Apache License v2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
 --%>
	
<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!-- Navbar
================================================== -->
<div class="navbar navbar-inverse navbar-fixed-top">
	<div class="navbar-inner">
		<div class="container">
			<button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			</button>
			<a class="brand" href="/">Koshiji</a>
			<div class="nav-collapse collapse">
				<ul class="nav">

					<li class="">
						<div style="margin-top:10px">
							<select id="groupList">
								<c:forEach var="group" items="${userInfo.groupList}">
									<option value="${f:h(group.value)}">${f:h(group.label)}</option>
								</c:forEach>
							</select>
						</div>
					</li>

					<li class="dropdown" id="menu_message">
						<a id="menu1" href="#" role="button" class="dropdown-toggle">
						<span id="menu1_icon"></span>Message <b class="caret"></b>
						</a>
						<ul class="dropdown-menu" role="menu" aria-labelledby="menu1">

							<c:if test="${userInfo.groupManager == true}">
								<li id="menu_groupAdmin"><a tabindex="-1" href="javascript:void(0)">グループ管理</a></li>
								<li id="menu_addMember"><a tabindex="-1" href="javascript:void(0)">メンバー追加</a></li>
								<li class="divider"></li>
							</c:if>
							
							<c:choose>
								<c:when test="${userInfo.groupManager == true}">
									<li id="menu_groupRemove"><a tabindex="-1" href="javascript:void(0)">グループ削除</a></li>
								</c:when>
								<c:when test="${userInfo.groupManager == false && userInfo.selectedGroupKeyString != ''}">
									<li id="menu_groupRemove"><a tabindex="-1" href="javascript:void(0)">グループ脱退</a></li>
								</c:when>
							</c:choose>
							
							<li class="divider"></li>
							<li id="menu_groupAdd"><a tabindex="-1" href="javascript:void(0)">新規グループ作成</a></li>
						</ul>
					</li>
					
					<li class="" id="menu_schedule">
						<a href="javascript:void(0)">Schedule</a>
					</li>
					
					<li class="" id="menu_personalSettings">
						<a href="javascript:void(0)">Settings</a>
					</li>
					<li class="">
						<a href="/logout">Logout</a>
					</li>
				</ul>
			</div>

		</div>
	</div>
</div>

<%-- Modalダイアログ --%>
<div class="modal hide fade" id="groupNameDialog">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3>新規グループ作成</h3>
	</div>
	<div class="modal-body">
		<div class="form-horizontal">
			<div class="control-group">
				<label class="control-label" for="groupNameDialog_groupName">グループ名</label>
				<div class="controls">
					<input type="text" class="input-xlarge" id="groupNameDialog_groupName" placeholder="新しいグループ名は？">
				</div>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" id="groupNameDialog_close">Close</a>
		<a href="javascript:void(0)" class="btn btn-primary" id="groupNameDialog_create">グループを作成する</a>
	</div>
</div>

<div class="modal hide fade" id="groupAdminDialog">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3>グループ管理</h3>
	</div>
	<div class="modal-body">
		<div class="form-horizontal">
			<div class="control-group">
				<label class="control-label" for="groupAdminDialog_groupName">グループ名</label>
				<div class="controls">
					<input type="text" class="input-xlarge" id="groupAdminDialog_groupName" placeholder="新しいグループ名は？">
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">メンバー一覧</label>
				<div class="controls">
					<div class="message_address warning" id="groupAdminDialog_members"></div>
					<small class="text-warning">※このグループから外すメンバーをチェックして下さい</small>
				</div>
			</div>
		</div>
	
		<input type="hidden" id="groupAdminDialog_versionNo">
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" id="groupAdminDialog_close">Close</a>
		<a href="javascript:void(0)" class="btn btn-primary" id="groupAdminDialog_execute">変更する</a>
	</div>
</div>

<div class="modal hide fade" id="addMemberDialog">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3>メンバー追加</h3>
	</div>
	<div class="modal-body">
		<div class="form-horizontal">
			<div class="control-group">
				<label class="control-label" for="addMemberDialog_email">メールアドレス</label>
				<div class="controls">
					<input type="text" class="input-xlarge" id="addMemberDialog_email" placeholder="追加するメンバーのメールアドレスは？" />
					<p class="text-warning">※Gmailのアドレスでないとログインできません</p>
				</div>
			</div>
		</div>
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" id="addMemberDialog_close">Close</a>
		<a href="javascript:void(0)" class="btn btn-primary" id="addMemberDialog_execute">追加する</a>
	</div>
</div>

<div class="modal hide fade" id="personalSettingsDialog">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3>ユーザ情報変更</h3>
	</div>
	<div class="modal-body">

		<div class="form-horizontal">
			<div class="control-group">
				<label class="control-label" for="personalSettingsDialog_name">ニックネーム</label>
				<div class="controls">
					<input type="text" class="input-xlarge" id="personalSettingsDialog_name" placeholder="あなたの名前は？">
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="personalSettingsDialog_defaultGroup">デフォルト表示グループ</label>
				<div class="controls">
					<select id="personalSettingsDialog_defaultGroup"></select>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="personalSettingsDialog_defaultTimeZone">タイムゾーン</label>
				<div class="controls">
					<select id="personalSettingsDialog_defaultTimeZone"></select>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="personalSettingsDialog_memo">メモ</label>
				<div class="controls">
					<textarea id="personalSettingsDialog_memo" class="input-xlarge" rows="5"></textarea>
				</div>
			</div>
		</div>
		<input type="hidden" id="personalSettingsDialog_versionNo" />
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" id="personalSettingsDialog_close">Close</a>
		<a href="javascript:void(0)" class="btn btn-primary" id="personalSettingsDialog_execute">変更する</a>
	</div>
</div>

<script type="text/javascript">
<!--
isGroupManager = ${f:h(userInfo.groupManager)};
selectedGroup = "${f:h(userInfo.selectedGroupKeyString)}";
//-->
</script>
<script type="text/javascript" src="/js/header.js"></script>

<span id="dummy_ruler" style="visibility:hidden;position:absolute;white-space:nowrap;"></span>
<input type="hidden" id="token" />
