/*
 * Copyright 2012 Kazumune Katagiri. (http://d.hatena.ne.jp/nemuzuka)
 * Licensed under the Apache License v2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

$(function(){
	initScheduleEditDialog();
	initScheduleDetailDialog();
});

//スケジュール詳細ダイアログ表示
function openScheduleDetailDialog(scheduleKey) {
	var params = {};
	params["scheduleKeyString"] = scheduleKey;
	
	//サーバに問い合わせ
	setAjaxDefault();
	$.ajax({
		type: "GET",
		data: params,
		url: "/schedule/ajax/detail"
	}).then(
		function(data){
			renderAndOpenScheduleDetailDialog(data);
		}
	);
}

//スケジュール詳細ダイアログ描画
function renderAndOpenScheduleDetailDialog(data) {
	//共通エラーチェック
	if(errorCheck(data) == false) {
		return;
	}
	
	var result = data.result;
	var model = result.model;

	$("#scheduleDetailDialog_scheduleKeyString").val(model.keyToString);
	$("#scheduleDetailDialog_versonNo").val(model.version);
	
	var title = model.title;
	if(model.closed == true) {
		title = title + "(非公開)";
	}
	$("#scheduleDetailDialog_title").text(title);
	$("#scheduleDetailDialog_viewDate").text(result.viewDate)
	$("#scheduleDetailDialog_memo").html(escapeTextArea(model.memoStr));
	$("#scheduleDetailDialog_connMember").text(result.connMemberNames);
	$("#scheduleDetailDialog_createMember").text(
			result.createMemberName + " (" + createViewTime(result.lastUpdate) + ")");
	
	//ボタンを表示
	$("#scheduleDetailDialog_modal-footer").empty();
	if(result.created == true) {
		//作成者の場合、ボタンを追加する
		var $editButton = $("<a />").text("変更する").attr({"href":"javascript:void(0);"}).addClass("btn");
		$editButton.on("click", function(){
			$("#scheduleDetailDialog").modal("hide");
			openScheduleEditDialog($("#scheduleDetailDialog_scheduleKeyString").val(), "", "");
		});
		$("#scheduleDetailDialog_modal-footer").append($editButton);
		
		var $deleteButton = $("<a />").text("削除する").attr({"href":"javascript:void(0);"}).addClass("btn btn-danger");
		$deleteButton.on("click", function(){
			deleteSchedule();
		});
		$("#scheduleDetailDialog_modal-footer").append($deleteButton);
	}
	var $closeButton = $("<a />").text("Close").attr({"href":"javascript:void(0);"}).addClass("btn");
	$closeButton.on("click", function(){
		$("#scheduleDetailDialog").modal("hide");
	});
	$("#scheduleDetailDialog_modal-footer").append($closeButton);
	
	$("#scheduleDetailDialog").modal("show");
	$("#scheduleDetailDialog_modal-body").scrollTop(0);
}

//スケジュール削除
function deleteSchedule() {
	if(window.confirm("本スケジュールを削除します。本当によろしいですか？") == false) {
		return;
	}

	var params = {};
	params["scheduleKeyString"] = $("#scheduleDetailDialog_scheduleKeyString").val();
	params["version"] = $("#scheduleDetailDialog_versonNo").val();
	params["jp.co.nemuzuka.token"] = $("#token").val();

	setAjaxDefault();
	var task;
	task = $.ajax({
		type: "POST",
		url: "/schedule/ajax/delete",
		data: params
	}).pipe(
		function(data) {

			//共通エラーチェック
			if(errorCheck(data) == false) {
				return;
			}
			
			//メッセージを表示して、自身をクローズし、
			//呼び出し元を再表示
			infoCheck(data);
			$('#scheduleDetailDialog').modal('hide');
			refresh();
		}
	);
}

//スケジュール登録・更新ダイアログ表示
function openScheduleEditDialog(scheduleKey, targetMemberKey, targetDate) {
	var params = {};
	params["scheduleKeyString"] = scheduleKey;
	params["memberKeyString"] = targetMemberKey;
	params["targetDate"] = unFormatDate(targetDate);
	
	//サーバに問い合わせ
	setAjaxDefault();
	$.ajax({
		type: "GET",
		data: params,
		url: "/schedule/ajax/edit"
	}).then(
		function(data){
			renderAndOpenScheduleEditDialog(data);
		}
	);
}

//スケジュール登録・更新ダイアログ描画
function renderAndOpenScheduleEditDialog(data) {
	//共通エラーチェック
	if(errorCheck(data) == false) {
		return;
	}
	
	var result = data.result;
	if(result.keyString == null || result.keyString == '') {
		//新規
		$("#scheduleEditDialog_view_title").text("登録");
		$("#scheduleEditDialog_button_action").text("登録");
	} else {
		//更新
		$("#scheduleEditDialog_view_title").text("変更");
		$("#scheduleEditDialog_button_action").text("変更");
	}
	$("#scheduleEditDialog_scheduleKeyString").val(result.keyString);
	$("#scheduleEditDialog_versonNo").val(result.versionNo);
	
	$("#scheduleEditDialog_title").val(result.title);
	$("#scheduleEditDialog_startDate").val(formatDateyyyyMMdd(result.startDate));
	$("#scheduleEditDialog_startTime").val(formatDateyyyyMMdd(formatTimehhmm(result.startTime)));
	$("#scheduleEditDialog_endDate").val(formatDateyyyyMMdd(result.endDate));
	$("#scheduleEditDialog_endTime").val(formatDateyyyyMMdd(formatTimehhmm(result.endTime)));
	$("#scheduleEditDialog_memo").val(result.memo);
	$("input[type='radio'][name='scheduleEditDialog_closedFlg']").val([result.closedFlg]);
	
	//スケジュールに紐づくMember
	$("#scheduleEditDialog_member_to").empty();
	$.each(result.selectedMemberList, function() {
		$("#scheduleEditDialog_member_to").append($('<option>').attr({ value: this.value }).text(this.label));
	});
	reWriteSelect("scheduleEditDialog_member_to", new Array());

	//選択可能なグループ
	$("#scheduleEditDialog_groupList").empty();
	$.each(result.groupList, function() {
		$("#scheduleEditDialog_groupList").append($('<option>').attr({ value: this.value }).text(this.label));
	});
	reWriteSelect("scheduleEditDialog_groupList", new Array());
	$("#scheduleEditDialog_groupList").val(result.selectedGroupKeyString);
	
	//選択グループのMember
	$("#scheduleEditDialog_member_from").empty();
	$.each(result.memberList, function() {
		$("#scheduleEditDialog_member_from").append($('<option>').attr({ value: this.value }).text(this.label));
	});
	reWriteSelect("scheduleEditDialog_member_from", new Array());
	
	$("#scheduleEditDialog").modal("show");
	$("#scheduleEditDialog_modal-body").scrollTop(0);
}

//スケジュール詳細ダイアログ初期処理
function initScheduleDetailDialog() {
	$("#scheduleDetailDialog").on({
		"show":function(){
			openModalDialog();
		},
		"shown":function(){
			return centerModalDialog(this);
		},
		"hide":function(){
			closeModalDialog();
		}
	})
}


//スケジュール登録・更新ダイアログ初期処理
function initScheduleEditDialog() {
	$("#scheduleEditDialog").on({
		"show":function(){
			openModalDialog();
		},
		"shown":function(){
			return centerModalDialog(this);
		},
		"hide":function(){
			closeModalDialog();
		}
	})

	$("#scheduleEditDialog_close").on("click", function(){
		$('#scheduleEditDialog').modal('hide');
	});
	$("#scheduleEditDialog_execute").on("click", function(){
		executeSchedule();
	});
	
	$.datepicker.setDefaults($.extend($.datepicker.regional['ja']));
	$.timepicker.setDefaults($.extend($.timepicker.regional['ja']));
	
	$("#scheduleEditDialog_startDate").datepicker();
	$("#scheduleEditDialog_startTime").timepicker();
	$("#scheduleEditDialog_endDate").datepicker();
	$("#scheduleEditDialog_endTime").timepicker();
	
	//開始日時が変更された時の振る舞いを登録
	$("#scheduleEditDialog_startDate").on("change", function(){
		changeStartDate();
	});

	//Member移動のイベント定義
	$("#scheduleEditDialog_member_up").on("click", function(){
		upItems("scheduleEditDialog_member_to");
	});
	$("#scheduleEditDialog_member_down").on("click", function(){
		downItems("scheduleEditDialog_member_to");
	});
	$("#scheduleEditDialog_member_add").on("click", function(){
		addItems("scheduleEditDialog_member_from","scheduleEditDialog_member_to");
	});
	$("#scheduleEditDialog_member_remove").on("click", function(){
		removeItems("scheduleEditDialog_member_to");
	});
	$("#scheduleEditDialog_groupList").on("change", function(){
		changeGroupList4Schedule();
	});
}

//スケジュール登録
function executeSchedule() {
	var params = createScheduleParams();

	if(validateScheduleParams(params) == false) {
		return;
	}

	setAjaxDefault();
	$.ajax({
		type: "POST",
		data: params,
		dataType: "json",
		url: '/schedule/ajax/save'
	}).then(
		function(data){

			//共通エラーチェック
			if(errorCheck(data) == false) {
				if(data.status == -1 ) {
					//validateの場合、tokenを再発行
					return reSetToken();
				}
				return;
			}

			//メッセージを表示して、自身をクローズし、
			//呼び出し元を再表示
			infoCheck(data);
			$('#scheduleEditDialog').modal('hide');
			refresh();
		}
	);
}

//リクエストパラメータ生成
function createScheduleParams() {
	var params={};
	params["title"] = $("#scheduleEditDialog_title").val();
	params["startDate"] = unFormatDate($("#scheduleEditDialog_startDate").val());
	params["startTime"] = unFormatTime($("#scheduleEditDialog_startTime").val());
	params["endDate"] = unFormatDate($("#scheduleEditDialog_endDate").val());
	params["endTime"] = unFormatTime($("#scheduleEditDialog_endTime").val());
	params["memo"] = $("#scheduleEditDialog_memo").val();
	params["closedFlg"] = $("input[type='radio'][name='scheduleEditDialog_closedFlg']:checked").val();
	params["connMemberKeyString[]"] = getSelectArray("scheduleEditDialog_member_to");

	params["keyString"] = $("#scheduleEditDialog_scheduleKeyString").val();
	params["versionNo"] = $("#scheduleEditDialog_versonNo").val();
	params["jp.co.nemuzuka.token"] = $("#token").val();

	return params;
}

//登録validate
function validateScheduleParams(params) {
	var v = new Validate();

	v.addRules({value:params["startDate"],option:'required',error_args:"スケジュール開始日"});
	v.addRules({value:params["startDate"],option:'date',error_args:"スケジュール開始日"});
	v.addRules({value:params["startTime"],option:'time',error_args:"スケジュール開始時刻"});
	v.addRules({value:params["endDate"],option:'date',error_args:"スケジュール終了日"});
	v.addRules({value:params["endTime"],option:'time',error_args:"スケジュール終了時刻"});
	
	v.addRules({value:params["title"],option:'required',error_args:"件名"});
	v.addRules({value:params["title"],option:'maxLength',error_args:"件名", size:64});
	v.addRules({value:params["memo"],option:'maxLength',error_args:"メモ", size:1024});

	if (v.execute() == false){
		return false;
	}
	
	//どちらかの時刻のみの入力はNG
	if ((params["startTime"] == '' && params["endTime"] != '') || 
		(params["startTime"] != '' && params["endTime"] == '')) {
		alert("時刻を設定する場合はスケジュール開始時刻とスケジュール終了時刻を入力してください。");
		return false;
	}
	
	//終了日が必須
	if(params["endDate"] == '') {
		alert("スケジュール終了日は必須です。");
		return false;
	}
	
	if(params["startDate"] > params["endDate"]) {
		alert("終了日は開始日以降の日付を指定して下さい。");
		return false;
	}
	
	//開始日＝終了日で、時刻の入力がある場合、開始時刻<=終了時刻でなければエラー
	if(params["startDate"] == params["endDate"] && params["startTime"] != '') {
		if(params["startTime"] > params["endTime"]) {
			alert("終了時刻は開始時刻以降の時間を指定して下さい。");
			return false;
		}
	}
	
	if(params["connMemberKeyString[]"] == null || params["connMemberKeyString[]"].length == 0) {
		alert("スケジュールに紐づくメンバーを1人以上設定して下さい。");
		return false;
	}
	return true;
}


//グループ変更時の処理
function changeGroupList4Schedule() {
	var groupKeyString = $("#scheduleEditDialog_groupList").val();
	var params = {};
	params["groupKeyString"] = groupKeyString;
	
	//サーバに問い合わせ
	setAjaxDefault();
	$.ajax({
		type: "GET",
		data: params,
		url: "/member/ajax/memberList"
	}).then(
		function(data){
			//共通エラーチェック
			if(errorCheck(data) == false) {
				return;
			}
			$("#scheduleEditDialog_member_from").empty();
			$.each(data.result, function() {
				$("#scheduleEditDialog_member_from").append($('<option>').attr({ value: this.value }).text(this.label));
			});
			reWriteSelect("scheduleEditDialog_member_from", new Array());
		}
	);
}

//開始日を変更された際の振る舞い
function changeStartDate() {
	var orgData = unFormatDate($("#scheduleEditDialog_startDate").val());
	$("#scheduleEditDialog_endDate").val(formatDateyyyyMMdd(orgData));
}
