/*
 * Copyright 2012 Kazumune Katagiri. (http://d.hatena.ne.jp/nemuzuka)
 * Licensed under the Apache License v2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

//日次スケジュール初期表示
function initSchedule() {
	$("#schedule_area").empty();
	renderDayMenu();
	renderTodaySchedule();
}

//当日スケジュール描画
function renderTodaySchedule() {
	var params = {};
	params["viewType"] = "today";
	callAjaxAndRenderSchedule(params);
}

//最新データ取得
//この関数名は共通処理より呼ばれるので重要です。
function refresh() {
	var params = {};
	params["viewType"] = "refresh";
	callAjaxAndRenderSchedule(params);
}

//基準日を移動したスケジュール描画
function renderTargetSchedule(viewType, amountType) {
	var params = {};
	params["viewType"] = viewType;
	params["amountType"] = amountType;
	callAjaxAndRenderSchedule(params);
}

//日次スケジュール描画
function callAjaxAndRenderSchedule(params) {

	//サーバに問い合わせ
	setAjaxDefault();
	$.ajax({
		type: "GET",
		data: params,
		url: "/schedule/ajax/day"
	}).then(
		function(data){
			successRenderDay(data);
		}
	);
}

//成功時の描画
function successRenderDay(data) {
	//共通エラーチェック
	if(errorCheck(data) == false) {
		return;
	}
	//tokenの設定
	$("#token").val(data.token);

	var result = data.result;
	//再描画
	renderDaySchedule(result);
}

//日次スケジュール描画
function renderDaySchedule(result) {
	$("#schedule_table").remove();
	var $table = $("<table>").attr({"id":"schedule_table"}).addClass("schedule_table schedule_day_table");
	
	$.each(result.viewDate, function() {
		var target_date = executeFormatDateyyyyMMdd(this.targetDate, "yyyyMMdd", "yyyy/MM/dd");
		$("#schedule_target_date").val(target_date);
	});
	
	//body部分作成
	var $tbody = $("<tbody />");
	$.each(result.viewSchedule, function(){
		var $tr = $("<tr />");
		var targetMemberKey = this.memberKeyString;

		//リソース情報表示
		var $nameSpan = $("<span />").text(this.name);
		var resourceClass = "td1_schedule";
		var $td = $("<td />").addClass(resourceClass + " name").append($nameSpan).append("<br />");
		$tr.append($td);

		//日付情報表示
		var daySchedules = this.daySchedules;
		$.each(daySchedules, function(index){
			$td = $("<td />").addClass(resourceClass);

			//時刻指定無しListを表示
			setSchedule(this.noTimeList, $td);
			//時刻指定有りListを表示
			setSchedule(this.timeList, $td);

			var targetDate = formatDateyyyyMMdd(result.viewDate[index].targetDate);
			setAddImage($td, targetMemberKey, targetDate);

			$tr.append($td);
		});
		$tbody.append($tr);
	});
	$table.append($tbody);
	$("#schedule_area").append($table);
}

//日付Menu描画
function renderDayMenu() {
	var $schedule_daycrl = $("<div>").addClass("schedule_daycrl").attr({"id":"schedule_daycrl"});
	
	//スケジュール表示切替ボタンを押下された場合の処理
	var $today = $("<i />").addClass("icon-share-alt").css('cursor','pointer');
	var $todaySpan = $("<span />").css('cursor','pointer').append($today).append("今日");
	$todaySpan.on("click", function(){
		renderTodaySchedule();
	});
	var $prev_day = $("<i />").addClass("icon-chevron-left").css('cursor','pointer');
	$prev_day.on("click", function(){
		renderTargetSchedule("prev","day");
	});
	var $next_day = $("<i />").addClass("icon-chevron-right").css('cursor','pointer');
	$next_day.on("click", function(){
		renderTargetSchedule("next","day");
	});

	$.datepicker.setDefaults($.extend($.datepicker.regional['ja']));
	var $scheduleTargetDate = $("<input />").attr({"type":"text","readonly":"readonly","id":"schedule_target_date"}).addClass("input-small");
	$scheduleTargetDate.datepicker();
	$scheduleTargetDate.on("change", function(){
		var params = {};
		params["appointmentDate"] = unFormatDate($(this).val());
		callAjaxAndRenderSchedule(params);
	});
	
	$schedule_daycrl
		.append($prev_day)
		.append(" ")
		.append($scheduleTargetDate)
		.append(" ")
		.append($next_day)
		.append("　")
		.append($todaySpan);
	
	$("#schedule_area").append($schedule_daycrl);
}