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
	unreadMessageCheckWithGroup();
	initSchedule();
});

//一定時間経過後の関数呼び出し設定
function setCheckPollingFunction() {
	setTimeout(function() { unreadMessageCheckWithGroup(); }, 60000);
}

//未読Messageの存在チェック
function unreadMessageCheckWithGroup() {
	setAjaxNoLoadingMsg();
	var task;
	task = $.ajax({
		type: "POST",
		url: "/message/ajax/unreadMessage4Login"
	}).pipe(
		function(data) {

			//共通エラーチェック
			if(errorCheck(data) == false) {
				return;
			}
			
			$("#unread_message_title").text("参加グループ");
			$("#unread_message_list_area").empty();
			var result = data.result;
			$.each(result, function(){
				var groupKeyString = this.groupKeyString;
				var groupName = this.groupName;
				var count = this.unreadMessageCnt;
				
				var $groupDiv = $("<div />").addClass("group_item");
				$groupDiv.on({
					"mouseenter":function(){
						$(this).addClass("hover");
					},
					"mouseleave":function(){
						$(this).removeClass("hover");
					}
				});
				$groupDiv.on("click", function(){
					changeGroup(groupKeyString);
				});

				//Header部の生成
				var $groupName = $("<span />").text(groupName + " ");
				var $count = "";
				if(count != null) {
					$count = $("<span />").addClass("badge badge-warning").text("未読 " + count + "件");
				}
				$groupDiv.append($groupName).append($count);
				$("#unread_message_list_area").append($groupDiv);
			});
			
			setCheckPollingFunction();
		}
	);
}

