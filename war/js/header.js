$(function(){
	initPersonalSettingsDialog();
	
	$("#groupList").val(selectedGroup);
	$("#groupList").on("change", function(){
		changeGroup($(this).val());
	});
	
	//Menu TOP
	$("#menu_dashboard").on("click", function(){
		moveUrl('/dashboard');
	});

	$("#menu_schedule").on("click", function(){
		moveUrl('/schedule');
	});
	
	$("#menu_personalSettings").on("click", function(){
		showPersonalSettings();
	});
	
	var targetName = $("#selected_menu").val();
	$("#menu_" + targetName).addClass("active");
	
	if(targetName == 'message') {
		//Messageを選択した際、ドロップダウンメニューを表示する
		$("#menu1").attr({"data-toggle":"dropdown"});
	} else {
		//Message未選択の場合、Message機能表示
		$("#menu1 b.caret").hide();
		$("#menu_message").on("click", function(){
			moveUrl('/message');
		});
	}
	
});

//ユーザ情報変更ダイアログ初期処理
function initPersonalSettingsDialog() {
	$("#personalSettingsDialog").on({
		"show":function(){
			openModalDialog();
		},
		"hide":function(){
			closeModalDialog();
		}
	})

	$("#personalSettingsDialog_close").on("click", function(){
		$('#personalSettingsDialog').modal('hide');
	});
	$("#personalSettingsDialog_execute").on("click", function(){
		executePersonalSettings();
	});
}

//ユーザ情報変更ダイアログOpen
function showPersonalSettings() {
	setAjaxDefault();
	return $.ajax({
		type: "POST",
		url: "/message/ajax/getMember"
	}).pipe(
		function(data) {
			//共通エラーチェック
			if(errorCheck(data) == false) {
				return;
			}

			//tokenの設定
			$("#token").val(data.token);
			var result = data.result;
			$("#personalSettingsDialog_name").val(result.name);
			$("#personalSettingsDialog_versionNo").val(result.versionNo);
			$("#personalSettingsDialog_defaultGroup").empty();
			$.each(result.groupList, function(){
				$("#personalSettingsDialog_defaultGroup").append($("<option />").attr({value:this.value}).text(this.label));
			});
			$("#personalSettingsDialog_defaultGroup").val(result.defaultGroup);

			$("#personalSettingsDialog_defaultTimeZone").empty();
			$.each(result.timeZoneList, function(){
				$("#personalSettingsDialog_defaultTimeZone").append($("<option />").attr({value:this.value}).text(this.label));
			});
			$("#personalSettingsDialog_defaultTimeZone").val(result.timeZone);
			
			$("#personalSettingsDialog_memo").val(result.memo);
			
			$("#personalSettingsDialog").modal("show");
		}
	);
}

//ユーザ情報変更
function executePersonalSettings() {
	var params = {};
	params["jp.co.nemuzuka.token"] = $("#token").val();
	params["name"] = $("#personalSettingsDialog_name").val();
	params["timeZone"] = $("#personalSettingsDialog_defaultTimeZone").val();
	params["defaultGroup"] = "";
	var $groupList = $("#personalSettingsDialog_defaultGroup");
	if($groupList != null) {
		params["defaultGroup"] = $groupList.val();
	}
	params["memo"] = $("#personalSettingsDialog_memo").val();
	params["versionNo"] = $("#personalSettingsDialog_versionNo").val();

	//validateチェック
	var v = new Validate();
	v.addRules({value:params["name"],option:'required',error_args:"ニックネーム"});
	v.addRules({value:params["name"],option:'maxLength',error_args:"ニックネーム", size:128});
	v.addRules({value:params["memo"],option:'maxLength',error_args:"メモ", size:1024});
	if(v.execute() == false) {
		return;
	}

	setAjaxDefault();
	return $.ajax({
		type: "POST",
		url: "/message/ajax/updateMember",
		data: params
	}).pipe(
		function(data) {
			//共通エラーチェック
			if(errorCheck(data) == false) {
				if(data.status == -1 ) {
					//validateの場合、tokenを再発行
					return reSetToken();
				}
				//ダイアログをクローズ
				$("#personalSettingsDialog").modal("hide");
				return;
			}
			infoCheck(data);

			//tokenの設定
			$("#token").val(data.token);
			
			//ダイアログをクローズ
			setTimeout(function(){ $("#personalSettingsDialog").modal("hide"); }, 1000);
		}
	);
}
