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
package jp.co.nemuzuka.koshiji.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.nemuzuka.koshiji.meta.UnreadMessageModelMeta;
import jp.co.nemuzuka.koshiji.model.UnreadMessageModel;

import org.slim3.datastore.FilterCriterion;
import org.slim3.datastore.ModelMeta;

import com.google.appengine.api.datastore.Key;

/**
 * UnreadMessageModelに対するDao.
 * @author kazumune
 */
public class UnreadMessageDao extends AbsDao {

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.dao.AbsDao#getModelMeta()
     */
    @SuppressWarnings("rawtypes")
    @Override
    ModelMeta getModelMeta() {
        return UnreadMessageModelMeta.get();
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.dao.AbsDao#getModelClass()
     */
    @SuppressWarnings("rawtypes")
    @Override
    Class getModelClass() {
        return UnreadMessageModel.class;
    }
    
    private static UnreadMessageDao dao = new UnreadMessageDao();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static UnreadMessageDao getInstance() {
        return dao;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private UnreadMessageDao(){}

    /**
     * Map取得.
     * 指定したKey配列に合致するデータを取得します。
     * Keyが未指定の場合、size0のListを返却します。
     * KeyはMessageKeyを設定します。
     * @param memberKey MemberKey
     * @param messageKeys MessageKey配列
     * @return 該当Map
     */
    public Map<Key, UnreadMessageModel> getMap(Key memberKey, Key...messageKeys) {
        List<UnreadMessageModel> list = getList(memberKey, messageKeys);
        Map<Key, UnreadMessageModel> map = new LinkedHashMap<Key, UnreadMessageModel>();
        for(UnreadMessageModel target : list) {
            map.put(target.getMessageKey(), target);
        }
        return map;
    }
    
    /**
     * 未読Message一覧取得.
     * 指定したMemberのMessageKeyに紐付く未読Message一覧を取得します。
     * MessageKeyが未指定の場合、size0のListを返却します
     * @param memberKey MemberKey
     * @param messageKeys MessageKey配列
     * @return 該当レコード
     */
    public List<UnreadMessageModel> getList(Key memberKey, Key...messageKeys) {
        UnreadMessageModelMeta e = (UnreadMessageModelMeta) getModelMeta();
        Set<FilterCriterion> filter = new HashSet<FilterCriterion>();
        if(messageKeys != null && messageKeys.length != 0) {
            filter.add(e.messageKey.in(messageKeys));
        } else {
            return new ArrayList<UnreadMessageModel>();
        }
        filter.add(e.memberKey.equal(memberKey));
        return getList(filter, null, e.key.asc);
    }
    
    /**
     * 未読データ一覧取得.
     * 指定したMessageの未読データを取得します。
     * @param messageKey MessageKey
     * @return 未読データ一覧
     */
    public List<UnreadMessageModel> getList(Key messageKey) {
        UnreadMessageModelMeta e = (UnreadMessageModelMeta) getModelMeta();
        Set<FilterCriterion> filter = new HashSet<FilterCriterion>();
        filter.add(e.messageKey.equal(messageKey));
        return getList(filter, null, e.key.asc);
    }
    
    /**
     * 未読データ削除.
     * 指定したMessageの未読データを削除します。
     * @param messageKey MessageKey
     */
    public void delete4MessageKey(Key messageKey) {
        List<UnreadMessageModel> list = getList(messageKey);
        for(UnreadMessageModel target : list) {
            delete(target.getKey());
        }
    }
}
