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

import java.util.List;

import jp.co.nemuzuka.common.UniqueKey;
import jp.co.nemuzuka.koshiji.dao.MemberGroupConnDao;
import jp.co.nemuzuka.koshiji.model.MemberGroupConnModel;
import jp.co.nemuzuka.koshiji.service.MemberGroupConnService;

import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;

/**
 * MemberGroupConnServiceの実装クラス.
 * @author kazumune
 */
public class MemberGroupConnServiceImpl implements MemberGroupConnService {

    MemberGroupConnDao memberGroupConnDao = MemberGroupConnDao.getInstance();

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
                memberGroupConnDao.delete(target.getKey());
            }
        } else {
            //自分とグループの関連を削除
            memberGroupConnDao.delete(targetModel.getKey());
        }
    }

}
