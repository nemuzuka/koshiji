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
