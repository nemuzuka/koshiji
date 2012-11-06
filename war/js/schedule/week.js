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
});

//週次スケジュール初期描画
function renderWeekSchedule() {
	$("#scheduleViewType").val("week");
	renderWeekMenu();
	refresh();
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

//最新データ取得
//この関数名は共通処理より呼ばれるので重要です。
function refresh() {
	var params = {};
	params["viewType"] = "refresh";
	callAjaxAndRenderSchedule(params);
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

//当日スケジュール描画
function renderTodaySchedule() {
	var params = {};
	params["viewType"] = "today";
	callAjaxAndRenderSchedule(params);
}

//基準日を移動したスケジュール描画
function renderTargetSchedule(viewType, amountType) {
	var params = {};
	params["viewType"] = viewType;
	params["amountType"] = amountType;
	callAjaxAndRenderSchedule(params);
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
	var $thead = $("<tr />").append($("<th />").text("").addClass("days"));
	$.each(result.viewDate, function() {
		var addClass = "";
		if(this.holiday) {
			addClass = "sche_week_holiday";
		} else if(this.saturday) {
			addClass = "sche_week_saturday";
		} else if(this.sunday) {
			addClass = "sche_week_sunday";
		}
		var todayClass = "";
		if(this.today) {
			todayClass = "sche_week_today";
		}
		var $th = $("<th />").addClass("days").addClass(addClass).addClass(todayClass);
		
		var target_date = executeFormatDateyyyyMMdd(this.targetDate, "yyyyMMdd", "M/d")
		
		var $dayOfTheWeekSpan = $("<span />");
		var thText = target_date + "(" + this.dayOfTheWeekName + ")";
		$dayOfTheWeekSpan.text(thText);
		var $holidayNameSpan = null;
		if(this.holidayName != null && this.holidayName != '') {
			$holidayNameSpan = $("<span />").text(this.holiday_memo);
		}

		if($holidayNameSpan == null) {
			$th.append($dayOfTheWeekSpan);
		} else {
			$th.append($dayOfTheWeekSpan).append("<br />").append($holidayNameSpan);
		}

		$thead.append($th);
	});
	$table.append($("<thead />").append($thead));

	//body部分作成
	var $tbody = $("<tbody />");
	$.each(result.viewSchedule, function(){
		var $tr = $("<tr />");
		var targetMemberKey = this.memberKeyString;

		//リソース情報表示
		var $nameSpan = $("<span />").text(this.name);
		var resourceClass = "td1_schedule";
		var $td = $("<td />").addClass(resourceClass).append($nameSpan).append("<br />");
		var $img = $("<img />").attr({src:"/img/calendar.png"}).css('cursor','pointer');
		$img.attr({"alt":"月の予定を表示します","title":"月の予定を表示します"});
		$img.on("click", function(){
			renderMonthScheduleInit(targetMemberKey);
		});
		$td.append($img);
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
}

//週次スケジュールのMenuを描画します
function renderWeekMenu() {
	$("#schedule_title").text("週次スケジュール");
	$("#schedule_daycrl").empty();

	//スケジュール表示切替ボタンを押下された場合の処理
	var $today = $("<a />").text("今日").attr({"id":"today", "href":"javascript:void(0);"}).addClass("btn");
	$today.on("click", function(){
		renderTodaySchedule();
	});
	var $prev_week = $("<a />").text("先週").attr({"id":"prev_week", "href":"javascript:void(0);"}).addClass("btn");
	$prev_week.on("click", function(){
		renderTargetSchedule("prev","week");
	});
	var $prev_day = $("<a />").text("先日").attr({"id":"prev_day", "href":"javascript:void(0);"}).addClass("btn");
	$prev_day.on("click", function(){
		renderTargetSchedule("prev","day");
	});
	var $next_day = $("<a />").text("翌日").attr({"id":"next_day", "href":"javascript:void(0);"}).addClass("btn");
	$next_day.on("click", function(){
		renderTargetSchedule("next","day");
	});
	var $next_week = $("<a />").text("翌週").attr({"id":"next_week", "href":"javascript:void(0);"}).addClass("btn");
	$next_week.on("click", function(){
		renderTargetSchedule("next","week");
	});

	$("#schedule_daycrl")
		.append($today)
		.append("　")
		.append($prev_week)
		.append(" ")
		.append($prev_day)
		.append(" ")
		.append($("<span>").attr({"id":"viewDateRange"}).addClass("view_title"))
		.append(" ")
		.append($next_day)
		.append(" ")
		.append($next_week);
}

