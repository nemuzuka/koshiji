/*
 * Copyright 2012 Kazumune Katagiri. (http://d.hatena.ne.jp/nemuzuka)
 * Licensed under the Apache License v2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

//月次スケジュール初期描画
function renderMonthScheduleInit(memberKey) {
	$("#scheduleViewType").val("month");
	
	var params = {};
	params["memberKeyString"] = memberKey;
	
	//サーバに問い合わせ
	setAjaxDefault();
	$.ajax({
		type: "GET",
		data: params,
		url: "/schedule/ajax/initMonth"
	}).then(
		function(data){
			//共通エラーチェック
			if(errorCheck(data) == false) {
				return;
			}
			renderMonthMenu();
			refresh();
		}
	);
}

//月次スケジュール描画
function successRenderMonth(data) {
	//共通エラーチェック
	if(errorCheck(data) == false) {
		return;
	}
	//tokenの設定
	$("#token").val(data.token);

	var result = data.result;
	//再描画
	renderMonthSchedule(result);
}

//月次スケジュール描画
function renderMonthSchedule(result) {
	$("#viewMonth").text(result.viewMonth);

	var $table = $("#schedule_area");
	$table.empty();
	$table.removeClass("schedule_table week_schedule_table");
	$table.addClass("week_schedule_table");

	//ヘッダ部分描画
	var $thead = $("<tr />")
		.append($("<th />").text("日").addClass("sche_week_sunday days"))
		.append($("<th />").text("月").addClass("sche_week_monday days"))
		.append($("<th />").text("火").addClass("sche_week_tuesday days"))
		.append($("<th />").text("水").addClass("sche_week_wednesday days"))
		.append($("<th />").text("木").addClass("sche_week_theseday days"))
		.append($("<th />").text("金").addClass("sche_week_friday days"))
		.append($("<th />").text("土").addClass("sche_week_saturday days"));
	$table.append($thead);

	//body部分作成
	var memberSchedule = result.viewSchedule[0];
	var targetMemberKey = memberSchedule.memberKeyString;
	$("#schedule_title").text("月次スケジュール (" + memberSchedule.name + ")");
	var tdCount = 0;
	var $tr = $("<tr />");
	$.each(result.viewDate, function(index){

		var holidayName = this.holidayName;
		if(holidayName == null) {
			holidayName = "";
		}
		var addClass = "";
		if(this.today) {
			addClass = "sche_month_today";
		} else if(this.holiday) {
			addClass = "sche_month_holiday";
		} else if(this.saturday) {
			addClass = "sche_month_saturday";
		} else if(this.sunday) {
			addClass = "sche_month_sunday";
		} else if(this.targetMonth) {
			addClass = "sche_terget_month";
		}

		//対象年月でない場合、class指定を初期値にする
		if(this.targetMonth == false) {
			addClass = "";
		}

		var target_date = executeFormatDateyyyyMMdd(this.targetDate, "yyyyMMdd", "d")

		var $span = $("<span />").text(target_date + " " + holidayName);
		var $td = $("<td />").append($span).append("<br />").addClass("td1_schedule").addClass(addClass);

		//時刻指定無しListを表示
		var noTimeList = memberSchedule.daySchedules[index].noTimeList;
		setSchedule(noTimeList, $td);

		//時刻指定有りListを表示
		var timeList = memberSchedule.daySchedules[index].timeList;
		setSchedule(timeList, $td);

		var targetDate = formatDateyyyyMMdd(this.targetDate);
		setAddImage($td, targetMemberKey, targetDate);

		$tr.append($td);
		tdCount++;
		if(tdCount >= 7) {
			//テーブルに追加し、新たにtrを作成する
			$table.append($tr);
			tdCount = 0;
			$tr = $("<tr />");
		}
	});

}

//月次スケジュールのMenuを表示します
function renderMonthMenu() {
	$("#schedule_daycrl").empty();

	//スケジュール表示切替ボタンを押下された場合の処理
	var $thisMonth = $("<a />").text("今月").attr({"href":"javascript:void(0);"}).addClass("btn");
	$thisMonth.on("click", function(){
		renderThisMonthSchedule();
	});
	var $prevMonth = $("<a />").text("先月").attr({"href":"javascript:void(0);"}).addClass("btn");
	$prevMonth.on("click", function(){
		renderTargetSchedule("prev","month");
	});
	var $nextMonth = $("<a />").text("翌月").attr({"href":"javascript:void(0);"}).addClass("btn");
	$nextMonth.on("click", function(){
		renderTargetSchedule("next","month");
	});
	var $backWeek = $("<a />").text("<<週次スケジュール表示").attr({"href":"javascript:void(0);"});
	$backWeek.on("click", function(){
		renderWeekSchedule();
	});
	var $div = $("<div />").css({"float":"right"}).addClass("back_title");
	$div.append($backWeek);

	$("#schedule_daycrl")
		.append($thisMonth)
		.append("　")
		.append($prevMonth)
		.append(" ")
		.append($("<span>").attr({"id":"viewMonth"}).addClass("view_title"))
		.append(" ")
		.append($nextMonth)
		.append(" ")
		.append($div);
}
//当月スケジュール描画
function renderThisMonthSchedule() {
	var params = {};
	params["viewType"] = "thisMonth";
	callAjaxAndRenderSchedule(params);
}
