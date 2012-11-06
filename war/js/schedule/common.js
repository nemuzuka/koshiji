/*
 * Copyright 2012 Kazumune Katagiri. (http://d.hatena.ne.jp/nemuzuka)
 * Licensed under the Apache License v2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */

//スケジュール追加image追加
function setAddImage(td, targetMemberKey, targetDate) {
	var $img = $("<img />").attr({src:'/img/write20.gif'}).css('cursor','pointer');
	$img.attr({"alt":"スケジュールを追加します","title":"スケジュールを追加します"});
	$img.on("click", function(){
		openScheduleEditDialog("", targetMemberKey, targetDate);
	});
	td.append($img);
}

//スケジュール情報追加
function setSchedule(list, td) {
	$.each(list, function(){
		var $a = $("<a />").text(this.viewData).attr({href: "javascript:void(0)"});
		var scheduleKeyString = this.scheduleKeyString;
		$a.on("click", function(){
			openScheduleDetailDialog(scheduleKeyString);
		});
		var $duplicate = $("<span />").addClass("duplicate");
		if(this.duplicate) {
			$duplicate.text("×");
		}

		if(scheduleKeyString == null) {
			//スケジュールKeyがnullの場合(非公開で自分が含まれていない)
			$a = $("<span />").text(this.viewData);
		}

		var $span = $("<span />").append($duplicate).append($a);
		td.append($span).append($("<br />"));
	});
}
