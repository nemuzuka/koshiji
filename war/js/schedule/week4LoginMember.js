/*
 * Copyright 2012 Kazumune Katagiri. (http://d.hatena.ne.jp/nemuzuka)
 * Licensed under the Apache License v2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

//日次スケジュール初期表示
function initSchedule() {
	$("#schedule_area").empty();
	renderWeekMenu();
	renderTodaySchedule();
}

//週次スケジュール描画
function callAjaxAndRenderSchedule(params) {

	//サーバに問い合わせ
	setAjaxDefault();
	$.ajax({
		type: "GET",
		data: params,
		url: "/schedule/ajax/week4Login"
	}).then(
		function(data){
			successRender(data);
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
	renderSchedule(result, false);
}

//週次スケジュール描画
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
	$table.append(createWeekBody(result));
}
