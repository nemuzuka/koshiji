<%-- 
/*
 * Copyright 2012 Kazumune Katagiri. (http://d.hatena.ne.jp/nemuzuka)
 * Licensed under the Apache License v2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 */
 --%>
<%@page pageEncoding="UTF-8" isELIgnored="false"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="f" uri="http://www.slim3.org/functions"%>

<script type="text/javascript" src="/js/schedule/dialog.js"></script>

<div class="modal hide" id="scheduleEditDialog" style="width:750px;">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3>スケジュール<span id="scheduleEditDialog_view_title">登録</span></h3>
	</div>
	<div class="modal-body" id="scheduleEditDialog_modal-body">
		<div class="form-horizontal">
			<table class="edit_table">
				<tbody>
					<tr>
						<th width="100px">件名<span class="important-msg">(*)</span></th>
						<td>
							<input type="text" size="40" id="scheduleEditDialog_title" class="required-input input-xxlarge" />
						</td>
					</tr>
					<tr>
						<th>いつ<span class="important-msg">(*)</span></th>
						<td>
							<div>
								<input type="text" id="scheduleEditDialog_startDate" size="20" class="date required-input input-medium" placeholder="いつから" /> 
								<input type="text" size="6" id="scheduleEditDialog_startTime" class="input-mini" />　〜　
								<input type="text" id="scheduleEditDialog_endDate" size="20" class="date required-input input-medium"  placeholder="いつまで"/> 
								<input type="text" size="6" id="scheduleEditDialog_endTime" class="input-mini" />
							</div>
						</td>
					</tr>
					<tr>
						<th>メモ</th>
						<td>
							<textarea cols="45" rows="4" id="scheduleEditDialog_memo" class="memo input-xxlarge"></textarea>
						</td>
					</tr>
					<tr>
						<th>閲覧範囲</th>
						<td>
							<label for="scheduleEditDialog_ds_closedFlg1" class="radio"><input id="scheduleEditDialog_ds_closedFlg1" type="radio" name="scheduleEditDialog_closedFlg" value="0">公開する</label>
							<label for="scheduleEditDialog_ds_closedFlg2" class="radio"><input id="scheduleEditDialog_ds_closedFlg2" type="radio" name="scheduleEditDialog_closedFlg" value="1">非公開(参加者と登録者のみ閲覧できます)</label>
						</td>
					</tr>
					<tr>
						<th>参加者</th>
						<td>
							<div>
								<div class="sort_action_area" style="margin-right: 10px;">
									<div class="sort_up_area">
										<input type="button" class="btn sort_up_button" id="scheduleEditDialog_member_up" value="↑" />
									</div>
									<div>
										<input type="button" class="btn" id="scheduleEditDialog_member_down" value="↓" />
									</div>
								</div>
								<div class="sort_to">
									<div>
										<label for="scheduleEditDialog_member_to">参加メンバー</label>
										<select id="scheduleEditDialog_member_to" size="10" multiple class="required-input">
										</select>
									</div>
									<div align="right" class="resource_remove_area">
										<input id="scheduleEditDialog_member_remove" class="btn" type="button" value="ー" alt="参加メンバーから削除" title="参加メンバーから削除" />
									</div>
								</div>
								<div class="sort_to">
									<input id="scheduleEditDialog_member_add" class="btn resource_add_area" 
										type="button" value="＜" alt=" 参加メンバーに追加" title="参加メンバーに追加" />
								</div>
								<div>
									<label for="scheduleEditDialog_groupList">グループ</label>
									<select id="scheduleEditDialog_groupList">
									</select>
									<label for="scheduleEditDialog_member_from">メンバー</label>
									<select id="scheduleEditDialog_member_from" size="6" multiple>
									</select>
								</div>
							</div>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<input type="hidden" id="scheduleEditDialog_scheduleKeyString" />
		<input type="hidden" id="scheduleEditDialog_versonNo" />
	</div>
	<div class="modal-footer">
		<a href="javascript:void(0)" class="btn" id="scheduleEditDialog_close">Close</a>
		<a href="javascript:void(0)" class="btn btn-primary" id="scheduleEditDialog_execute"><span id="scheduleEditDialog_button_action">登録</span>する</a>
	</div>
</div>

<div class="modal hide" id="scheduleDetailDialog" style="width:750px;">
	<div class="modal-header">
		<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		<h3 id="scheduleDetailDialog_title"></h3>
	</div>
	<div class="modal-body" id="scheduleDetailDialog_modal-body">
		<div class="form-horizontal">
			<table class="edit_table">
				<tbody>
					<tr>
						<th width="100px">いつ</th>
						<td id="scheduleDetailDialog_viewDate"></td>
					</tr>
					<tr>
						<th>メモ</th>
						<td id="scheduleDetailDialog_memo"></td>
					</tr>
					<tr>
						<th>参加者</th>
						<td id="scheduleDetailDialog_connMember"></td>
					</tr>
					<tr>
						<th>登録者</th>
						<td id="scheduleDetailDialog_createMember"></td>
					</tr>
				</tbody>
			</table>
		</div>
		<input type="hidden" id="scheduleDetailDialog_scheduleKeyString" />
		<input type="hidden" id="scheduleDetailDialog_versonNo" />
	</div>
	<div class="modal-footer" id="scheduleDetailDialog_modal-footer">
		<a href="javascript:void(0)" class="btn" id="scheduleDetailDialog_close">Close</a>
	</div>
</div>
