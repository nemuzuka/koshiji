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
package jp.co.nemuzuka.koshiji.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jp.co.nemuzuka.entity.LabelValueBean;
import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.koshiji.dao.MemberDao;
import jp.co.nemuzuka.koshiji.dao.ScheduleDao;
import jp.co.nemuzuka.koshiji.form.ScheduleForm;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.koshiji.model.ScheduleModel;
import jp.co.nemuzuka.koshiji.service.ScheduleEditService;
import jp.co.nemuzuka.utils.ConvertUtils;
import jp.co.nemuzuka.utils.CurrentDateUtils;
import jp.co.nemuzuka.utils.DateTimeUtils;

import org.apache.commons.lang.StringUtils;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

/**
 * MessageSearchServiceの実装クラス.
 * @author kazumune
 */
public class ScheduleEditServiceImpl implements ScheduleEditService {

    ScheduleDao scheduleDao = ScheduleDao.getInstance();
    MemberDao memberDao = MemberDao.getInstance();
    
    private static ScheduleEditServiceImpl impl = new ScheduleEditServiceImpl();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static ScheduleEditServiceImpl getInstance() {
        return impl;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private ScheduleEditServiceImpl(){}

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.ScheduleEditService#put(jp.co.nemuzuka.koshiji.form.ScheduleForm, com.google.appengine.api.datastore.Key)
     */
    @Override
    public Key put(ScheduleForm form, Key loginMemberKey) {
        ScheduleModel model = createSchedule(form, loginMemberKey);
        scheduleDao.put(model);
        return model.getKey();
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.ScheduleEditService#getForm(java.lang.String, java.lang.String, java.lang.String, jp.co.nemuzuka.entity.UserInfo)
     */
    @Override
    public ScheduleForm getForm(String scheduleKeyString,
            String memberKeyString, String targetDate, UserInfo userInfo) {
        
        ScheduleModel schedule = null;
        if(StringUtils.isEmpty(scheduleKeyString)) {
            //新規の場合
            schedule = new ScheduleModel();
            SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
            Date date = ConvertUtils.toDate(targetDate, sdf);
            schedule.setStartDate(date);
            schedule.setEndDate(date);
            Set<String> connMember = new LinkedHashSet<String>();
            connMember.add(memberKeyString);
            schedule.setConnMember(connMember);
        } else {
            //更新の場合
            schedule = scheduleDao.get(Datastore.stringToKey(scheduleKeyString));
            if(schedule == null || 
                    schedule.getCreateMemberKey().equals(Datastore.stringToKey(userInfo.keyToString)) == false ) {
                //データが存在しない、データの作成者がログインMemberでない場合、処理終了
                return null;
            }
        }
        
        return createForm(schedule, userInfo);
    }
    
    /**
     * Form情報作成.
     * @param schedule 設定元Schedule
     * @param userInfo ログイン情報
     * @return ScheduleForm
     */
    private ScheduleForm createForm(ScheduleModel schedule, UserInfo userInfo) {
        
        ScheduleForm form = new ScheduleForm();
        if(schedule.getKey() != null) {
            form.setKeyString(schedule.getKeyToString());
            form.setVersionNo(ConvertUtils.toString(schedule.getVersion()));
            form.setMemo(schedule.getMemo().getValue());
        }
        form.setTitle(schedule.getTitle());
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        form.setStartDate(ConvertUtils.toString(schedule.getStartDate(), sdf));
        form.setStartTime(schedule.getStartTime());
        form.setEndDate(ConvertUtils.toString(schedule.getEndDate(), sdf));
        form.setEndTime(schedule.getEndTime());
        
        if(schedule.isClosed()) {
            form.setClosedFlg("1");
        } else {
            form.setClosedFlg("0");
        }
        
        //選択済みのMemberListを作成する
        form.setSelectedMemberList(createMemberList(schedule.getConnMember()));
        
        //参加グループのList、選択済みのグループのMemberListを設定する
        form.setGroupList(userInfo.getGroupList());
        form.setMemberList(userInfo.getMemberList());
        form.setSelectedGroupKeyString(userInfo.selectedGroupKeyString);
        return form;
    }

    /**
     * 選択済みMemberList作成.
     * @param connMember 関連MemberKeySet
     * @return 選択済みMemberList
     */
    private List<LabelValueBean> createMemberList(Set<String> connMember) {
        Set<Key> keySet = new LinkedHashSet<Key>();
        for(String keyString : connMember) {
            keySet.add(Datastore.stringToKey(keyString));
        }
        List<LabelValueBean> retList = new ArrayList<LabelValueBean>();
        List<MemberModel> list = memberDao.getList(keySet.toArray(new Key[0]));
        for(MemberModel target : list) {
            retList.add(new LabelValueBean(target.getName(), target.getKeyToString()));
        }
        return retList;
    }

    /**
     * ScheduleModel作成.
     * 新規登録時、Modelをnewします。
     * 更新時、Modelを取得し、Form情報を元に更新します。
     * 更新データが存在しない or 作成者≠ログインユーザの場合、バージョン不正のExceptionをThrowします
     * @param form 入力Form
     * @param loginMemberKey ログインユーザのMemberKey
     * @return 更新対象ScheduleModel
     */
    private ScheduleModel createSchedule(ScheduleForm form, Key loginMemberKey) {
        
        ScheduleModel model = null;
        Date currentDateTime = CurrentDateUtils.getInstance().getCurrentDateTime();
        if(StringUtils.isEmpty(form.getKeyString())) {
            //新規の場合
            model = new ScheduleModel();
            model.setCreateMemberKey(loginMemberKey);
            model.setCreateDateTime(currentDateTime);
        } else{
            Key key = Datastore.stringToKey(form.getKeyString());
            Long version = ConvertUtils.toLong(form.getVersionNo());
            //更新の場合
            model = scheduleDao.get(key, version);
            if(model == null || model.getCreateMemberKey().equals(loginMemberKey) == false) {
                //該当データが存在しない or 作成者＝ログインMemberでない場合、不正処理
                throw new ConcurrentModificationException();
            }
        }
        model.setTitle(form.getTitle());
        model.setMemo(new Text(form.getMemo()));
        
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        Date startDate = ConvertUtils.toDate(form.getStartDate(), sdf);
        model.setStartDate(startDate);
        model.setStartTime(form.getStartTime());
        Date endDate = ConvertUtils.toDate(form.getEndDate(), sdf);
        model.setEndDate(endDate);
        model.setEndTime(form.getEndTime());
        
        if("1".equals(form.getClosedFlg())) {
            model.setClosed(true);
        } else {
            model.setClosed(false);
        }
        model.setLastUpdate(currentDateTime);
        
        Set<String> connMember = new LinkedHashSet<String>();
        for(String targetMemberKey : form.getConnMemberKeyString()) {
            connMember.add(targetMemberKey);
        }
        model.setConnMember(connMember);
        
        return model;
    }

}
