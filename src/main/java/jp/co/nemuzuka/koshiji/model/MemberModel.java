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
package jp.co.nemuzuka.koshiji.model;

import net.arnx.jsonic.JSONHint;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

/**
 * アプリケーションに登録されているユーザを管理するModel.
 * @author kazumune
 */
@Model(schemaVersion = 1)
public class MemberModel extends AbsModel {

	/** メンバーKey. */
	//自動採番
	@Attribute(primaryKey=true)
	private Key key;

	/** メールアドレス. */
	private String mail;
	
	/** 氏名. */
	private String name;
	
	/** メモ. */
	@Attribute(unindexed=true)
	private Text memo;
	
	/** タイムゾーン. */
	@Attribute(unindexed=true)
	private String timeZone;

    /** デフォルト表示グループ. */
    @Attribute(unindexed=true)
    private String defaultGroup;
	
	/**
	 * @return key
	 */
	@JSONHint(ignore=true)
	public Key getKey() {
		return key;
	}
	/**
	 * @return the memo
	 */
	@JSONHint(ignore=true)
	public Text getMemo() {
		return memo;
	}

	/**
	 * @param key セットする key
	 */
	public void setKey(Key key) {
		this.key = key;
	}

	/**
	 * @return mail
	 */
	public String getMail() {
		return mail;
	}

	/**
	 * @param mail セットする mail
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name セットする name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param memo the memo to set
	 */
	public void setMemo(Text memo) {
		this.memo = memo;
	}

	/**
	 * @return the timeZone
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * @param timeZone the timeZone to set
	 */
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
    /**
     * @return defaultGroup
     */
    public String getDefaultGroup() {
        return defaultGroup;
    }
    /**
     * @param defaultGroup セットする defaultGroup
     */
    public void setDefaultGroup(String defaultGroup) {
        this.defaultGroup = defaultGroup;
    }
}
