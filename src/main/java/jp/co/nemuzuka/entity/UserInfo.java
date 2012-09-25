/*
 * Copyright 2012 Kazumune Katagiri. (http://d.hatena.ne.jp/nemuzuka)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.co.nemuzuka.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jp.co.nemuzuka.common.TimeZone;

/**
 * Sessionに格納するUser情報.
 * @author kazumune
 */
public class UserInfo implements Serializable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	
	/** ログインユーザのIDのKey文字列. */
	public String keyToString;
	
	/** ログインユーザのTimeZone. */
	public TimeZone timeZone = TimeZone.GMT_P_9;
	
	/** 選択したグループのKey文字列. */
	public String selectedGroupKeyString = "";
	
	/** 選択したグループの管理者であれば、true */
	public boolean groupManager;
	
    /** グループMemberList. */
    public List<LabelValueBean> memberList = new ArrayList<LabelValueBean>();
	
	/** 参加グループList. */
	public List<LabelValueBean> groupList = new ArrayList<LabelValueBean>();
	
}
