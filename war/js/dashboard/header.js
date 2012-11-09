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
			
			//MessageTopに遷移する
			moveUrl("/message");
		}
	);
}
