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
		$msgDiv.on({
			"mouseenter":function(){
				$(this).addClass("hover");
			},
			"mouseleave":function(){
				$(this).removeClass("hover");
			}
		});
		$msgDiv.on("click", function(event){
			var target = $(event.target);
			if(target.is("a") || target.is("span") || 
					target.is("i") || target.is("textarea")) {
				//指定タグのクリックの場合、divタグのクリックイベントは発生させない
				return;
			}
			var $msgCommentDiv = $(this).children(".message_comment");
			var keyToString = model.keyToString;
			toggleComment($msgCommentDiv, keyToString, create);
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
		var $createMemberSpan = $("<strong />").text(createMemberName);
		$msgHeader.append($small).append($bell_icon).append($createMemberSpan);
		
		//content部の生成
		var $msgContent = $("<div />").addClass("message_content");
		var $contentP = $("<p />").addClass("lead").html(escapeTextArea(model.bodyText));
		$msgContent.append($contentP);
		
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
	
	//コメント入力エリアを作成
	var $commentInputArea = $("<div />").addClass("massage_comment_input_area").css({"display":"none"});
	var $commentTextArea = $("<textarea />").addClass("body").attr({"rows":"4"}).attr({"placeholder":"コメント？"});
	var $commentExecuteButton = $("<a />").addClass("btn btn-primary").text("コメントを追加する").css({"margin-top":"5px"});
	$commentExecuteButton.on("click", function(){
		createComment($commentTextArea, keyToString);
	});
	$commentInputArea.append($commentTextArea).append($commentExecuteButton);
	
	var $btnToolbarDiv = $("<div />").addClass("btn-toolbar");
	var $btnGroupDiv = $("<div />").addClass("btn-group");
	var $addCommentA = $("<a />").addClass("btn").attr({"title":"メッセージにコメントを追加します"});
	var $commentSpan = $("<span />").text("コメント追加");
	$addCommentA.append($("<i />").addClass("icon-comment")).append($commentSpan);
	$addCommentA.on("click", function(){
		$commentInputArea.show('fast');
	});

	var $removeMessageA = $("<a />").addClass("btn");
	var $removeMessageSpan = $("<span />");
	var $removeMessageIcon = $("<i />")
	if(create) {
		$removeMessageSpan.text("削除").attr({"title":"メッセージを削除します"});
		$removeMessageIcon.addClass("icon-trash");
	} else {
		$removeMessageSpan.text("非表示").attr({"title":"メッセージを非表示にします"});
		$removeMessageIcon.addClass("icon-remove");
	}
	$removeMessageA.on("click", function(){
		deleteMessage($msgComment, keyToString);
	});
	$removeMessageA.append($removeMessageIcon).append($removeMessageSpan);
	$btnGroupDiv.append($addCommentA).append($removeMessageA);
	$btnToolbarDiv.append($btnGroupDiv);
	$msgComment.append($btnToolbarDiv);
	
	//コメント入力エリアを描画
	$msgComment.append($commentInputArea);

	//指定したメッセージに紐付くコメントを取得
	createCommentListArea($msgComment, keyToString).pipe(
		function(data) {
			$msgComment.slideToggle('fast');
		}
	);
}

//Message非表示
function deleteMessage($msgComment, keyToString) {
	if(window.confirm("メッセージを削除します。本当によろしいですか？") == false) {
		return;
	}

	var params = {};
	params["messageKeyString"] = keyToString;
	params["jp.co.nemuzuka.token"] = $("#token").val();
	
	setAjaxDefault();
	return $.ajax({
		type: "POST",
		url: "/message/ajax/delete",
		data: params
	}).pipe(
		function(data) {

			//共通エラーチェック
			if(errorCheck(data) == false) {
				return;
			}

			//tokenの設定
			$("#token").val(data.token);
			//Messageを非表示にする
			infoCheck(data);
			$msgComment.parent().remove();
		}
	);
}

//CommentListを描画します
function createCommentListArea($appendTarget, messageKeyToString) {

	var params = {};
	params["messageKeyString"] = messageKeyToString;
	
	setAjaxDefault();
	return $.ajax({
		type: "POST",
		url: "/message/ajax/searchComment",
		data: params
	}).pipe(
		function(data) {

			//共通エラーチェック
			if(errorCheck(data) == false) {
				return;
			}

			//一覧のdivを作成する
			var $commentDiv = createCommentDiv(data, messageKeyToString);
			$appendTarget.append($commentDiv);
		}
	);
}

//Comment一覧描画
function createCommentDiv(data, messageKeyToString) {
	//tokenの設定
	$("#token").val(data.token);

	var $retDiv = $("<div />").addClass("message_comment_list");
	var $addressDiv = $("<address />").append($("<strong />").text("宛先:"))
		.append($("<span />").text(data.result.address));
	var $table = $("<table />").addClass("table table-hover");
	var $tbody = $("<tbody />");
	var result = data.result.list;
	$.each(result, function(){
		var model = this.model;
		var createMemberName = this.createMemberName;
		var lastUpdate = this.lastUpdate;
		var canDelete = this.deleteAuth;
		var $delButton = $("<a />").attr({"href":"javascript:void(0)"}).addClass("btn btn-mini");
		$delButton.append($("<i />").addClass("icon-trash"));
		$delButton.on("click", function(){
			deleteComment($(this), messageKeyToString, model.keyToString)
		});
		if(canDelete == false) {
			$delButton = "";
		}
		
		var $tr = $("<tr />");
		$tr.append(
			$("<td />").attr({"width":"95%"})
				.append($("<p />").html(escapeTextArea(model.bodyText)))
				.append($("<p>").append($("<small />").text("( " + createViewTime(lastUpdate) + " " + createMemberName + " )")))
		).append(
			$("<td />").attr({"width":"5%", "align":"right"})
				.append($delButton)
		);
		$tbody.append($tr);
	});
	$table.append($tbody);

	$retDiv.append($addressDiv);
	if(result.length != 0) {
		$retDiv.append($table);
	}
	return $retDiv;
}

//コメント削除
function deleteComment($buttonObj, messageKeyString, commentKeyString) {
	if(window.confirm("コメントを削除します。本当によろしいですか？") == false) {
		return;
	}

	var params = {};
	params["messageKeyString"] = messageKeyString;
	params["commentKeyString"] = commentKeyString;
	params["jp.co.nemuzuka.token"] = $("#token").val();

	//validateチェック
	var v = new Validate();
	v.addRules({value:params["messageKeyString"],option:'required',error_args:"Message Key"});
	v.addRules({value:params["commentKeyString"],option:'required',error_args:"Comment Key"});
	if(v.execute() == false) {
		return;
	}

	setAjaxDefault();
	var task;
	task = $.ajax({
		type: "POST",
		url: "/message/ajax/deleteComment",
		data: params
	}).pipe(
		function(data) {

			//共通エラーチェック
			if(errorCheck(data) == false) {
				return;
			}
			
			//メッセージを表示して、一覧歳表示
			infoCheck(data);
			var $appendDiv = $buttonObj.parent().parent().parent().parent().parent().parent();
			$appendDiv.children(".message_comment_list").remove();
			createCommentListArea($appendDiv, messageKeyString);
		}
	);
}

//コメント登録
function createComment($textArea, messageKey) {
	var params = {};
	params["body"] = $textArea.val();
	params["messageKeyString"] = messageKey;
	params["jp.co.nemuzuka.token"] = $("#token").val();
	
	//validateチェック
	var v = new Validate();
	v.addRules({value:params["body"],option:'required',error_args:"コメント"});
	v.addRules({value:params["body"],option:'maxLength',error_args:"コメント", size:2048});
	v.addRules({value:params["messageKeyString"],option:'required',error_args:"Message Key"});
	if(v.execute() == false) {
		return;
	}

	setAjaxDefault();
	var task;
	task = $.ajax({
		type: "POST",
		url: "/message/ajax/createComment",
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
			$textArea.val("");
			$textArea.parent().hide("fast");
			var $appendDiv = $textArea.parent().parent();
			$appendDiv.children(".message_comment_list").remove();
			createCommentListArea($appendDiv, messageKey);
		}
	);
}


//メッセージ登録関連初期処理
function initMessage() {
	$("#message_create").on("click", function(){
		createMessage();
	});
	setAutoResize($("#body"));
	$("#body").trigger("change");
}

//autoResize設定
function setAutoResize($target) {
	$target.autoResize({
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
