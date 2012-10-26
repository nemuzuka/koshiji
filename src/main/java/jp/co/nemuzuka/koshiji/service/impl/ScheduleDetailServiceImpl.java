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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.koshiji.dao.MemberDao;
import jp.co.nemuzuka.koshiji.dao.MemberGroupConnDao;
import jp.co.nemuzuka.koshiji.dao.ScheduleDao;
import jp.co.nemuzuka.koshiji.model.MemberGroupConnModel;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.koshiji.model.ScheduleModel;
import jp.co.nemuzuka.koshiji.service.ScheduleDetailService;
import jp.co.nemuzuka.utils.DateTimeUtils;

import org.apache.commons.lang.StringUtils;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;

/**
 * ScheduleDetailServiceの実装クラス.
 * @author kazumune
 */
public class ScheduleDetailServiceImpl implements ScheduleDetailService {

    ScheduleDao scheduleDao = ScheduleDao.getInstance();
    MemberDao memberDao = MemberDao.getInstance();
    MemberGroupConnDao memberGroupConnDao = MemberGroupConnDao.getInstance();
    
    private static ScheduleDetailServiceImpl impl = new ScheduleDetailServiceImpl();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static ScheduleDetailServiceImpl getInstance() {
        return impl;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private ScheduleDetailServiceImpl(){}

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.ScheduleDetailService#get(com.google.appengine.api.datastore.Key, jp.co.nemuzuka.entity.UserInfo)
     */
    @Override
    public Detail get(Key scheduleKeyString, UserInfo userInfo) {
        String loginMemberKeyString = userInfo.keyToString;
        Key loginMemberKey = Datastore.stringToKey(loginMemberKeyString);
        
        ScheduleModel model = scheduleDao.get(scheduleKeyString);
        if(model == null) {
            return null;
        }
        boolean connMember = true;
        boolean created = model.getCreateMemberKey().equals(loginMemberKey);
        if(created == false && model.getConnMember().contains(loginMemberKeyString) == false) {
            connMember = false;
        }
        if(model.isClosed() && connMember == false) {
            //非公開のスケジュールで関係ないユーザの場合
            return null;
        }
        
        Detail ret = new Detail();
        ret.model = model;
        ret.created = created;
        
        //各種Member名の設定
        setNames(ret, connMember, userInfo.selectedGroupKeyString);
        
        //表示日付の設定
        ret.viewDate = createViewDate(model);
        
        //最終更新日時の設定
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd HHmm");
        ret.lastUpdate = sdf.format(model.getLastUpdate());
        
        return ret;
    }

    /**
     * 表示日付作成.
     * @param model ScheduleModel
     */
    private String createViewDate(ScheduleModel model) {
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyy年M月d日");
        StringBuilder sb = new StringBuilder();
        if(model.getStartDate().getTime() == model.getEndDate().getTime()) {
            //開始日＝終了日の場合
            sb.append(sdf.format(model.getStartDate()));
            
            if(StringUtils.isNotEmpty(model.getStartTime())) {
                //時間が設定されている場合
                sb.append(" ").append(DateTimeUtils.formatTime(model.getStartTime()))
                    .append(" 〜 ").append(DateTimeUtils.formatTime(model.getEndTime()));
            }
            
        } else {
            //開始日≠終了日の場合
            if(StringUtils.isNotEmpty(model.getStartTime())) {
                //時間が設定されている場合
                sb.append(sdf.format(model.getStartDate()))
                    .append(" ").append(DateTimeUtils.formatTime(model.getStartTime()))
                    .append(" 〜 ").append(sdf.format(model.getEndDate()))
                    .append(" ").append(DateTimeUtils.formatTime(model.getEndTime()));
            } else {
                //時間が設定されていない場合
                sb.append(sdf.format(model.getStartDate()))
                    .append(" 〜 ").append(sdf.format(model.getEndDate()));
            }
        }
        return sb.toString();
    }

    /**
     * 各種Member名設定.
     * @param detail 設定対象オブジェクト
     * @param connMember ログインユーザがスケジュールに関係するMemberの場合、true
     * @param groupKeyString 選択グループKey文字列
     */
    private void setNames(Detail detail, boolean connMember, String groupKeyString) {
        ScheduleModel model = detail.model;
        Map<Key, MemberModel> memberMap = createTargetMemberMap(model, connMember, groupKeyString);
        
        //スケジュール参加者名の設定
        int otherCnt = 0;
        StringBuilder sb = new StringBuilder();
        for(String target : model.getConnMember()) {
            MemberModel member = memberMap.get(Datastore.stringToKey(target));
            if(member == null) {
                otherCnt++;
                continue;
            }
            if(sb.length() != 0) {
                sb.append(",");
            }
            sb.append(member.getName());
        }
        if(otherCnt != 0) {
            sb.append(" (他").append(otherCnt).append("名)");
        }
        detail.connMemberNames = sb.toString();
        
        //スケジュール作成者の設定
        MemberModel memberModel = memberMap.get(model.getCreateMemberKey());
        if(memberModel == null) {
            detail.createMemberName = "※他グループのメンバー";
        } else {
            detail.createMemberName = memberModel.getName();
        }
    }

    /**
     * 表示対象MemberMap取得.
     * スケジュールの関連者の場合、スケジュール参加者、作成者を含むMemberMapを作成します。
     * 関連者でない場合、現在選択しているグループのMemberのMemberMapを作成します。
     * @param model ScheduleModel
     * @param connMemberスケジュールの関連者の場合、true
     * @param groupKeyString 選択しているグループのKey文字列
     * @return 表示対象MemberMap
     */
    private Map<Key, MemberModel> createTargetMemberMap(ScheduleModel model,
        boolean connMember, String groupKeyString) {
        
        Set<Key> memberSet = new LinkedHashSet<Key>();
        if(connMember) {
            //登録者、関連Memberの名称を全て表示
            memberSet.add(model.getCreateMemberKey());
            for(String targetMember : model.getConnMember()) {
                memberSet.add(Datastore.stringToKey(targetMember));
            }
        } else {
            //現在表示しているグループに紐付くMemberのみ設定
            Key groupKey = Datastore.stringToKey(groupKeyString);
            List<MemberGroupConnModel> memberList = memberGroupConnDao.getMemberList(groupKey);          
            for(MemberGroupConnModel target : memberList) {
                memberSet.add(target.getMemberKey());
            }
        }
        return memberDao.getMap(memberSet.toArray(new Key[0]));
    }

}
