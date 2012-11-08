/*
 * Copyright 2012 Kazumune Katagiri. (http://d.hatena.ne.jp/nemuzuka)
 * Licensed under the Apache License v2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

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
			
			//ScheduleTopに遷移する
			moveUrl("/schedule");
		}
	);
}

//一定時間経過後の関数呼び出し設定
function setCheckPollingFunction() {
	setTimeout(function() { unreadMessageCheck(); }, 60000);
}

//未読Messageの存在チェック
function unreadMessageCheck() {
	setAjaxNoLoadingMsg();
	var task;
	task = $.ajax({
		type: "POST",
		url: "/message/ajax/searchUnreadMessage"
	}).pipe(
		function(data) {

			//共通エラーチェック
			if(errorCheck(data) == false) {
				return;
			}
			$("#menu1_icon").empty();
			if(data.result != 0) {
				var $icon = $("<i />").addClass("icon-info-sign icon-white");
				$("#menu1_icon").append($icon);
			}
			
			setCheckPollingFunction();
		}
	);
}
