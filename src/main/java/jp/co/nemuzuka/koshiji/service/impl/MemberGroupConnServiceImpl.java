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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.nemuzuka.common.UniqueKey;
import jp.co.nemuzuka.entity.LabelValueBean;
import jp.co.nemuzuka.koshiji.dao.GroupDao;
import jp.co.nemuzuka.koshiji.dao.MemberDao;
import jp.co.nemuzuka.koshiji.dao.MemberGroupConnDao;
import jp.co.nemuzuka.koshiji.model.GroupModel;
import jp.co.nemuzuka.koshiji.model.MemberGroupConnModel;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.koshiji.service.MemberGroupConnService;

import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;

/**
 * MemberGroupConnServiceの実装クラス.
 * @author kazumune
 */
public class MemberGroupConnServiceImpl implements MemberGroupConnService {

    MemberGroupConnDao memberGroupConnDao = MemberGroupConnDao.getInstance();
    GroupDao groupDao = GroupDao.getInstance();
    MemberDao memberDao = MemberDao.getInstance();

    /** インスタンス. */
	private static MemberGroupConnServiceImpl impl = new MemberGroupConnServiceImpl();
	
	/**
	 * インスタンス取得.
	 * @return インスタンス
	 */
	public static MemberGroupConnServiceImpl getInstance() {
		return impl;
	}
	
	/**
	 * デフォルトコンストラクタ.
	 */
	private MemberGroupConnServiceImpl(){}

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.MemberGroupConnService#put(com.google.appengine.api.datastore.Key, com.google.appengine.api.datastore.Key, boolean)
     */
    @Override
    public Key put(Key memberKey, Key groupKey, boolean admin) {
        
        String memberKeyString = Datastore.keyToString(memberKey);
        String groupKeyString = Datastore.keyToString(groupKey);
        String uniqueKey = memberKeyString + ":" + groupKeyString;
        if (Datastore.putUniqueValue(UniqueKey.memberGroupConn.name(), uniqueKey) == false) {
            //既に登録されている組み合わせの場合、処理終了
            return null;
        }
        
        MemberGroupConnModel model = new MemberGroupConnModel();
        model.setAdmin(admin);
        model.setGroupKey(groupKey);
        model.setMemberKey(memberKey);
        model.setSortNum(Long.MAX_VALUE);
        memberGroupConnDao.put(model);
        return model.getKey();
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.MemberGroupConnService#getList(com.google.appengine.api.datastore.Key)
     */
    @Override
    public List<MemberGroupConnModel> getList(Key memberKey) {
        return memberGroupConnDao.getList(memberKey);
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.MemberGroupConnService#getMemberList(com.google.appengine.api.datastore.Key)
     */
    @Override
    public List<MemberGroupConnModel> getMemberList(Key groupKey) {
        return memberGroupConnDao.getMemberList(groupKey);
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.MemberGroupConnService#deleteMemberGroupConn(com.google.appengine.api.datastore.Key, com.google.appengine.api.datastore.Key)
     */
    @Override
    public void deleteMemberGroupConn(Key memberKey, Key groupKey) {
        List<MemberGroupConnModel> list = getMemberList(groupKey);
        MemberGroupConnModel targetModel = null;

        for(MemberGroupConnModel target : list) {
            if(target.getMemberKey().equals(memberKey)) {
                targetModel = target;
                break;
            }
        }
        if(targetModel == null) {
            return;
        }
        
        if(targetModel.isAdmin()) {
            //指定グループに対する全ての関連を削除
            for(MemberGroupConnModel target : list) {
                memberGroupConnDao.deleteMember(target.getKey(), target.getMemberKey(), target.getGroupKey());
            }
            //グループも削除
            groupDao.delete(groupKey);
        } else {
            //自分とグループの関連を削除
            memberGroupConnDao.deleteMember(targetModel.getKey(), memberKey, groupKey);
        }
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.MemberGroupConnService#getGroupList(com.google.appengine.api.datastore.Key)
     */
    @Override
    public List<LabelValueBean> getGroupList(Key memberKey) {
        
        List<MemberGroupConnModel> list = getList(memberKey);
        Set<Key> groupKeySet = new HashSet<Key>();
        for(MemberGroupConnModel target : list) {
            groupKeySet.add(target.getGroupKey());
        }
        
        Map<Key, GroupModel> groupMap = groupDao.getMap(groupKeySet.toArray(new Key[0]));
        List<LabelValueBean> retList = new ArrayList<LabelValueBean>();
        for(MemberGroupConnModel target : list) {
            GroupModel groupModel = groupMap.get(target.getGroupKey());
            if(groupModel == null) {
                continue;
            }
            retList.add(new LabelValueBean(groupModel.getGroupName(), groupModel.getKeyToString()));
        }
        return retList;
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.MemberGroupConnService#getMemberLabelValueList(com.google.appengine.api.datastore.Key)
     */
    @Override
    public List<LabelValueBean> getMemberLabelValueList(Key groupKey) {
        
        List<LabelValueBean> retList = new ArrayList<LabelValueBean>();
        
        List<MemberGroupConnModel> list = memberGroupConnDao.getMemberList(groupKey);
        Set<Key> memberKeySet = new HashSet<Key>();
        for(MemberGroupConnModel target: list) {
            memberKeySet.add(target.getMemberKey());
        }
        Map<Key, MemberModel> memberMap = memberDao.getMap(memberKeySet.toArray(new Key[0]));
        for(MemberGroupConnModel target: list) {
            MemberModel member = memberMap.get(target.getMemberKey());
            if(member == null) {
                continue;
            }
            retList.add(new LabelValueBean(member.getName(), member.getKeyToString()));
        }
        return retList;
    }
}
