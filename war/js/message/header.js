/*
 * Copyright 2012 Kazumune Katagiri. (http://d.hatena.ne.jp/nemuzuka)
 * Licensed under the Apache License v2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

$(function(){
	initGroupNameDialog();
	initGroupAdminDialog();
	initAddMemberDialog();

	$("#menu_groupAdmin").on("click", function(){
		showGroupAdminDialog();
	});
	$("#menu_addMember").on("click", function(){
		showAddMemberDialog();
	});
	$("#menu_groupRemove").on("click", function(){
		var action = "から脱退";
		if(isGroupManager == true) {
			action = "を削除";
		}
		var groupName = $('#groupList option:selected').text();
		if(window.confirm("グループ「" + groupName + "」" + action + "します。本当によろしいですか？") == false) {
			return;
		}
		removeGroup();
	});
	$("#menu_groupAdd").on("click", function(){
		$("#groupNameDialog").modal('show');
	});
});


//メンバー追加ダイアログ初期処理
function initAddMemberDialog() {
	$("#addMemberDialog").on({
		"show":function(){
			openModalDialog();
		},
		"hide":function(){
			closeModalDialog();
		}
	})

	$("#addMemberDialog_close").on("click", function(){
		$('#addMemberDialog').modal('hide');
	});
	$("#addMemberDialog_execute").on("click", function(){
		executeAddMember();
	});
}

//Memberをグループに追加します
function executeAddMember() {
	var params = {};
	params["jp.co.nemuzuka.token"] = $("#token").val();
	params["mail"] = $("#addMemberDialog_email").val();

	//validateチェック
	var v = new Validate();
	v.addRules({value:params["mail"],option:'required',error_args:"メールアドレス"});
	v.addRules({value:params["mail"],option:'maxLength',error_args:"メールアドレス", size:128});
	if(v.execute() == false) {
		return;
	}

	setAjaxDefault();
	return $.ajax({
		type: "POST",
		url: "/message/ajax/addGroupMember",
		data: params
	}).pipe(
		function(data) {
			//共通エラーチェック
			if(errorCheck(data) == false) {
				if(data.status == -1 ) {
					//validateの場合、tokenを再発行
					return reSetToken();
				}
				return;
			}
			infoCheck(data);
			
			//Messageを再表示
			var selectedGroup = $("#groupList").val();
			setTimeout(function(){ changeGroup(selectedGroup); }, 1000);
		}
	);
}


//グループ管理ダイアログOpen
function showGroupAdminDialog(){
	
	setAjaxDefault();
	return $.ajax({
		type: "POST",
		url: "/message/ajax/getGroup"
	}).pipe(
		function(data) {
			//共通エラーチェック
			if(errorCheck(data) == false) {
				return;
			}

			//tokenの設定
			$("#token").val(data.token);
			var result = data.result;
			$("#groupAdminDialog_groupName").val(result.groupName);
			$("#groupAdminDialog_versionNo").val(result.versionNo);
			$("#groupAdminDialog_members").empty();
			$.each(result.memberList, function(){
				var $label = $("<label />").addClass("checkbox");
				var $checkBox = $("<input />").attr({"type":"checkbox", "name":"deleteMembers"}).val(this.value);
				var $viewSpan = $("<span />").text(this.label);
				$label.append($checkBox).append($viewSpan);
				$("#groupAdminDialog_members").append($label);
			});
			$("#groupAdminDialog").modal('show');
		}
	);
}

//グループ管理ダイアログ初期処理
function initGroupAdminDialog() {
	$("#groupAdminDialog").on({
		"show":function(){
			openModalDialog();
		},
		"hide":function(){
			closeModalDialog();
		}
	})

	$("#groupAdminDialog_close").on("click", function(){
		$('#groupAdminDialog').modal('hide');
	});
	$("#groupAdminDialog_execute").on("click", function(){
		executeGroup();
	});
}

//グループ情報を永続化します
function executeGroup() {
	var params = {};
	params["jp.co.nemuzuka.token"] = $("#token").val();
	params["groupName"] = $("#groupAdminDialog_groupName").val();
	params["versionNo"] = $("#groupAdminDialog_versionNo").val();
	params["deleteMembers[]"] = createArray4Checkbox("deleteMembers");
	
	setAjaxDefault();
	return $.ajax({
		type: "POST",
		url: "/message/ajax/updateGroup",
		data: params
	}).pipe(
		function(data) {
			//共通エラーチェック
			if(errorCheck(data) == false) {
				if(data.status == -1 ) {
					//validateの場合、tokenを再発行
					return reSetToken();
				}
				return;
			}
			infoCheck(data);
			
			//Messageを再表示
			var selectedGroup = $("#groupList").val();
			setTimeout(function(){ changeGroup(selectedGroup); }, 1000);
		}
	);
	
}

//メンバー追加ダイアログ表示
function showAddMemberDialog() {
	$("#addMemberDialog_email").val("");
	$('#addMemberDialog').modal('show');	
}

//Memberとグループの関連を削除します。
function removeGroup() {
	var params = {};
	params["jp.co.nemuzuka.token"] = $("#token").val();

	setAjaxDefault();
	return $.ajax({
		type: "POST",
		url: "/message/ajax/deleteGroup",
		data: params
	}).pipe(
		function(data) {
			//共通エラーチェック
			if(errorCheck(data) == false) {
				return;
			}
			infoCheck(data);
			
			//先頭に遷移する
			setTimeout(function(){ moveUrl("/"); }, 1000);
		}
	);
}

//新規グループ作成ダイアログ初期処理
function initGroupNameDialog() {
	$("#groupNameDialog").on({
		"show":function(){
			openModalDialog();
		},
		"hide":function(){
			closeModalDialog();
		}
	})

	$("#groupNameDialog_close").on("click", function(){
		$('#groupNameDialog').modal('hide');
	});
	$("#groupNameDialog_create").on("click", function(){
		createGroup();
	});
}

//新規グループ作成
function createGroup() {
	var params = {};
	params["groupName"] = $("#groupNameDialog_groupName").val();
	params["jp.co.nemuzuka.token"] = $("#token").val();

	//validateチェック
	var v = new Validate();
	v.addRules({value:params["groupName"],option:'required',error_args:"グループ名"});
	v.addRules({value:params["groupName"],option:'maxLength',error_args:"グループ名", size:128});
	if(v.execute() == false) {
		return;
	}

	setAjaxDefault();
	var task;
	task = $.ajax({
		type: "POST",
		url: "/message/ajax/createGroup",
		data: params
	}).pipe(
		function(data) {

			//共通エラーチェック
			if(errorCheck(data) == false) {
				if(data.status == -1 ) {
					//validateの場合、tokenを再発行
					return reSetToken();
				}
				return;
			}
			
			//メッセージを表示して、グループ変更
			infoCheck(data);
			
			var groupKeyString = data.result;
			setTimeout(function(){ changeGroup(groupKeyString); }, 1000);
		}
	);
}

//表示グループ変更時処理
function changeGroup(groupKeyString) {
	var params = {};
	params["selectedGroupKeyString"] = groupKeyString;
	
	setAjaxDefault();
	var task;
	task = $.ajax({
		type: "POST",
		url: "/ajax/changeGroup",
		data: params
	}).pipe(
		function(data) {

			//共通エラーチェック
			if(errorCheck(data) == false) {
				return;
			}
			
			//MessageTopに遷移する
			moveUrl("/message");
		}
	);
}
