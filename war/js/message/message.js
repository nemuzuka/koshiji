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
	initMessage();
	refreshList();
	
});

//初期メッセージ表示処理
function refreshList() {
	$("#result_area").empty();
	searchMessage(1);
}

//メッセージ検索・表示処理
function searchMessage(pageNo) {
	$("#footer_area").empty();

	var params = {};
	params["pageNo"] = pageNo;
	setAjaxDefault();
	var task;
	task = $.ajax({
		type: "POST",
		url: "/message/ajax/search",
		data: params
	}).pipe(
		function(data) {

			//共通エラーチェック
			if(errorCheck(data) == false) {
				return;
			}
			renderList(data, pageNo);
		}
	);
}

//一覧描画
function renderList(data, pageNo) {
	//tokenの設定
	$("#token").val(data.token);

	var result = data.result.list;
	$.each(result, function(){
		var model = this.model;
		var createMemberName = this.createMemberName;
		var unread = this.unread;
		var create = this.create;
		
		var $msgDiv = $("<div />").addClass("message_item");
		if(unread) {
			$msgDiv.addClass("unread");
		}
		$msgDiv.on("click", function(event){
			var target = $(event.target);
			if(target.is("a")) {
				//aタグのクリックの場合、divタグのクリックイベントは発生させない
				return;
			}
			var $msgComment = $(this).children(".message_comment");
			var keyToString = model.keyToString;
			toggleComment($msgComment, keyToString, create);
		});
		$msgDiv.on({
			"mouseenter":function(){
				$(this).addClass("hover");
			},
			"mouseleave":function(){
				$(this).removeClass("hover");
			}
		});

		//Header部の生成
		var $msgHeader = $("<div />").addClass("message_header");
		var time = " " + createViewTime(this.lastUpdate);
		var $timeSpan = $("<span />").text(time);
		var $comment_icon = "";
		if(model.comment) {
			$comment_icon = $("<i />").addClass("icon-comment");
		}
		var $small = $("<small />").addClass("incidental").append($comment_icon).append($timeSpan);

		var $bell_icon = "";
		if(unread) {
			$bell_icon = $("<i />").addClass("icon-bell");
		}
		var $createMemberSpan = $("<span />").text(createMemberName);
		$msgHeader.append($small).append($bell_icon).append($createMemberSpan);
		
		//content部の生成
		var $msgContent = $("<div />").addClass("message_content");
		var $contentSpan = $("<span />").addClass("lead").html(escapeTextArea(model.bodyText));
		$msgContent.append($contentSpan);
		
		//comment部の生成
		var $msgComment = $("<div />").addClass("message_comment").css({"display":"none"});

		$msgDiv.append($msgHeader).append($msgContent).append($msgComment);
		$("#result_area").append($msgDiv);
	});
	
	if(data.result.hasNextPage == false) {
		//メッセージが存在しない旨表示
		var msgSpan = $("<small />").text("表示するメッセージはありません。");
		var $info_icon = $("<i />").addClass("icon-info-sign");
		$("#footer_area").addClass("no_message").append($info_icon).append(msgSpan);
	} else {
		//次ページへのリンク表示
		var $a = $("<a />").attr({"href":"javascript:void(0)"}).text("さらに読み込む");
		$a.on("click", function(){
			var targetPage = pageNo + 1;
			searchMessage(targetPage);
		});
		$("#footer_area").removeClass("no_message").append($a);
	}
}

//コメント欄表示処理
//コメント欄が非表示の場合、コメントデータを取得し表示します。
//コメント欄が表示の場合、非表示にします。
function toggleComment($msgComment, keyToString, create) {
	if($msgComment.css("display") == "none") {
		//データを取得する
		renderCommentList($msgComment, keyToString, create);
	} else {
		$msgComment.slideToggle('fast');
	}
}

//
// コメントデータ取得・表示
//
function renderCommentList($msgComment, keyToString, create) {
	$msgComment.empty();
	alert("よんだよね" + create);
	$msgComment.slideToggle('fast');
}

//メッセージ登録関連初期処理
function initMessage() {
	$("#message_create").on("click", function(){
		createMessage();
	});
	$('#body').autoResize({
		// On resize:
		onResize : function() {
			$(this).css({opacity:1});
		},
		// After resize:
		animateCallback : function() {
			$(this).css({opacity:1});
		},
		// Quite slow animation:
		animateDuration : 300,
		// More extra space:
		extraSpace : 20
	});
	$("#body").trigger("change");
}

//メッセージ登録処理
function createMessage() {
	var params = {};
	params["body"] = $("#body").val();
	params["jp.co.nemuzuka.token"] = $("#token").val();
	params["memberKeyStrings[]"] = createArray4Checkbox("send_target");
	
	//validateチェック
	var v = new Validate();
	v.addRules({value:params["body"],option:'required',error_args:"メッセージ"});
	v.addRules({value:params["body"],option:'maxLength',error_args:"メッセージ", size:2048});
	if(v.execute() == false) {
		return;
	}

	setAjaxDefault();
	var task;
	task = $.ajax({
		type: "POST",
		url: "/message/ajax/create",
		data: params
	}).pipe(
		function(data) {

			//共通エラーチェック
			if(errorCheck(data) == false) {
				if(data.status == -1 ) {
					//validateの場合、tokenを再発行
					return reSetToken();
				}
				return;
			}
			
			//メッセージを表示して、一覧歳表示
			infoCheck(data);
			viewLoadingMsg();
			clearMessageInput();
			setTimeout(function(){ refreshList(); }, 1000);
		}
	);
}

//登録後入力欄初期化
function clearMessageInput() {
	$("#body").val("");
	$("#body").trigger("change");
	var cb = $("input:checkbox[@name='send_target']");
	$.each(cb, function() {
		$(this).prop('checked', false);
		if($(this).val() == 'all') {
			$(this).prop('checked', true);
		}
	});
}
