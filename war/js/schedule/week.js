/*
 * Copyright 2012 Kazumune Katagiri. (http://d.hatena.ne.jp/nemuzuka)
 * Licensed under the Apache License v2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

$(function(){
	$(window).unload(function(){
		//画面を離れる場合
		unBlockLoadingMsg();
	});

	renderWeekSchedule();
	setCheckPollingFunction();
});

//週次スケジュール初期描画
function renderWeekSchedule() {
	$("#scheduleViewType").val("week");
	renderWeekMenu();
	renderTodaySchedule();
}

//週次スケジュール描画
//この関数名は共通処理より呼ばれるので重要です。
function callAjaxAndRenderSchedule(params) {

	var url = "";
	if($("#scheduleViewType").val() == "week") {
		url = "/schedule/ajax/week";
	} else {
		url = "/schedule/ajax/month";
	}
	
	//サーバに問い合わせ
	setAjaxDefault();
	$.ajax({
		type: "GET",
		data: params,
		url: url
	}).then(
		function(data){
			if($("#scheduleViewType").val() == "week") {
				successRender(data);
			} else {
				successRenderMonth(data);
			}
		}
	);
}

//成功時の描画
function successRender(data) {
	//共通エラーチェック
	if(errorCheck(data) == false) {
		return;
	}
	//tokenの設定
	$("#token").val(data.token);

	var result = data.result;
	//再描画
	renderSchedule(result);
}

//スケジュール一覧描画
function renderSchedule(result) {

	var $table = $("#schedule_area");
	$table.empty();
	$table.removeClass("schedule_table week_schedule_table");
	$table.addClass("schedule_table");

	//表示期間の設定
	$("#viewDateRange").text(result.viewDateRange);

	//ヘッダ部分作成
	$table.append($("<thead />").append(createWeekDayHeader(result.viewDate)));

	//body部分作成
	$table.append(createWeekBody(result, true));
}


