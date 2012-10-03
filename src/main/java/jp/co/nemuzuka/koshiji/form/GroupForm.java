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
package jp.co.nemuzuka.koshiji.form;

import java.io.Serializable;

/**
 * GroupForm.
 * @author kazumune
 */
public class GroupForm implements Serializable {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;

	/** 文字列化Key. */
	public String keyToString;
	
	/** グループ名. */
	public String groupName;
	
	/** バージョンNo. */
	public String versionNo;

    /**
     * @return keyToString
     */
    public String getKeyToString() {
        return keyToString;
    }

    /**
     * @param keyToString セットする keyToString
     */
    public void setKeyToString(String keyToString) {
        this.keyToString = keyToString;
    }

    /**
     * @return groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName セットする groupName
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return versionNo
     */
    public String getVersionNo() {
        return versionNo;
    }

    /**
     * @param versionNo セットする versionNo
     */
    public void setVersionNo(String versionNo) {
        this.versionNo = versionNo;
    }

}
