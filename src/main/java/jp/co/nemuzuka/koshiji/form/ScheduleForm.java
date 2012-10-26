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
import java.util.List;

import jp.co.nemuzuka.entity.LabelValueBean;

/**
 * Schedule登録Form.
 * @author kazumune
 */
public class ScheduleForm implements Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /** Key文字列. */
    private String keyString;
    /** 件名. */
    private String title;
    /** Memo. */
    private String memo;
    /** 開始日. */
    private String startDate;
    /** 開始時刻. */
    private String startTime;
    /** 終了日. */
    private String endDate;
    /** 終了時刻. */
    private String endTime;
    /** 
     * 非公開フラグ.
     * 非公開の場合、"1"
     */
    private String closedFlg;
    /** 関連MemberKeyString配列. */
    private String[] connMemberKeyString;

    /** バージョンNo. */
    private String versionNo;
    
    //表示用
    /** 選択MemberList. */
    private List<LabelValueBean> selectedMemberList;
    
    /** 参加グループList. */
    private List<LabelValueBean> groupList;
    /** 選択グループMemberList. */
    private List<LabelValueBean> memberList;
    /** 選択グループKey文字列. */
    private String selectedGroupKeyString;
    
    /**
     * @return keyString
     */
    public String getKeyString() {
        return keyString;
    }
    /**
     * @param keyString セットする keyString
     */
    public void setKeyString(String keyString) {
        this.keyString = keyString;
    }
    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }
    /**
     * @param title セットする title
     */
    public void setTitle(String title) {
        this.title = title;
    }
    /**
     * @return memo
     */
    public String getMemo() {
        return memo;
    }
    /**
     * @param memo セットする memo
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }
    /**
     * @return startDate
     */
    public String getStartDate() {
        return startDate;
    }
    /**
     * @param startDate セットする startDate
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    /**
     * @return startTime
     */
    public String getStartTime() {
        return startTime;
    }
    /**
     * @param startTime セットする startTime
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    /**
     * @return endDate
     */
    public String getEndDate() {
        return endDate;
    }
    /**
     * @param endDate セットする endDate
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
    /**
     * @return endTime
     */
    public String getEndTime() {
        return endTime;
    }
    /**
     * @param endTime セットする endTime
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    /**
     * @return closedFlg
     */
    public String getClosedFlg() {
        return closedFlg;
    }
    /**
     * @param closedFlg セットする closedFlg
     */
    public void setClosedFlg(String closedFlg) {
        this.closedFlg = closedFlg;
    }
    /**
     * @return connMemberKeyString
     */
    public String[] getConnMemberKeyString() {
        return connMemberKeyString;
    }
    /**
     * @param connMemberKeyString セットする connMemberKeyString
     */
    public void setConnMemberKeyString(String[] connMemberKeyString) {
        this.connMemberKeyString = connMemberKeyString;
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
    /**
     * @return selectedMemberList
     */
    public List<LabelValueBean> getSelectedMemberList() {
        return selectedMemberList;
    }
    /**
     * @param selectedMemberList セットする selectedMemberList
     */
    public void setSelectedMemberList(List<LabelValueBean> selectedMemberList) {
        this.selectedMemberList = selectedMemberList;
    }
    /**
     * @return groupList
     */
    public List<LabelValueBean> getGroupList() {
        return groupList;
    }
    /**
     * @param groupList セットする groupList
     */
    public void setGroupList(List<LabelValueBean> groupList) {
        this.groupList = groupList;
    }
    /**
     * @return memberList
     */
    public List<LabelValueBean> getMemberList() {
        return memberList;
    }
    /**
     * @param memberList セットする memberList
     */
    public void setMemberList(List<LabelValueBean> memberList) {
        this.memberList = memberList;
    }
    /**
     * @return selectedGroupKeyString
     */
    public String getSelectedGroupKeyString() {
        return selectedGroupKeyString;
    }
    /**
     * @param selectedGroupKeyString セットする selectedGroupKeyString
     */
    public void setSelectedGroupKeyString(String selectedGroupKeyString) {
        this.selectedGroupKeyString = selectedGroupKeyString;
    }
}
