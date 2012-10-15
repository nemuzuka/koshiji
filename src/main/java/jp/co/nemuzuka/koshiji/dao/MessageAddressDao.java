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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.co.nemuzuka.koshiji.meta.MessageAddressModelMeta;
import jp.co.nemuzuka.koshiji.model.MessageAddressModel;

import org.slim3.datastore.FilterCriterion;
import org.slim3.datastore.InMemorySortCriterion;
import org.slim3.datastore.ModelMeta;

import com.google.appengine.api.datastore.Key;

/**
 * MessageAddressModelに対するDao.
 * @author kazumune
 */
public class MessageAddressDao extends AbsDao {

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.dao.AbsDao#getModelMeta()
     */
    @SuppressWarnings("rawtypes")
    @Override
    ModelMeta getModelMeta() {
        return MessageAddressModelMeta.get();
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.dao.AbsDao#getModelClass()
     */
    @SuppressWarnings("rawtypes")
    @Override
    Class getModelClass() {
        return MessageAddressModel.class;
    }
    
    private static MessageAddressDao dao = new MessageAddressDao();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static MessageAddressDao getInstance() {
        return dao;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private MessageAddressDao(){}

    /**
     * MessageAddress一覧取得.
     * 指定した検索条件に紐付くMessageAddress一覧を取得します。
     * @param memberKey 取得対象MemberKey
     * @param groupKey 取得対象GroupKey
     * @return 該当レコード
     */
    public List<MessageAddressModel> getList(Key memberKey, Key groupKey) {
        MessageAddressModelMeta e = (MessageAddressModelMeta) getModelMeta();
        Set<FilterCriterion> filter = new HashSet<FilterCriterion>();
        filter.add(e.memberKey.equal(memberKey));
        filter.add(e.groupKey.equal(groupKey));
        return getList(filter, null, (InMemorySortCriterion[]) null);
    }
    
    /**
     * MessageAddress一覧取得.
     * Messageに紐付くMessageAddress一覧を取得します。
     * @param messageKey 取得対象MessageKey
     * @param groupKey 取得対象GroupKey
     * @return 該当レコード
     */
    public List<MessageAddressModel> getList4Message(Key messageKey, Key groupKey) {
        MessageAddressModelMeta e = (MessageAddressModelMeta) getModelMeta();
        Set<FilterCriterion> filter = new HashSet<FilterCriterion>();
        filter.add(e.messageKey.equal(messageKey));
        filter.add(e.groupKey.equal(groupKey));
        return getList(filter, null, (InMemorySortCriterion[]) null);
    }
    
    /**
     * MessageAddress存在チェック.
     * 該当メッセージの宛先に指定したユーザが存在するかチェックします。
     * @param messageKey MessageKey
     * @param groupKey GroupKey
     * @param memberKey MemberKey
     * @return 存在する場合、true
     */
    public boolean isExistsMember(Key messageKey, Key groupKey, Key memberKey) {
        if(getList4Member(messageKey, groupKey, memberKey).size() != 1) {
            return false;
        }
        return true;
    }
    
    /**
     * MessageAddress一覧取得存在チェック.
     * Message、Memberに紐付くデータを取得します。
     * @param messageKey MessageKey
     * @param groupKey GroupKey
     * @param memberKey MemberKey
     * @return 該当データ
     */
    public List<MessageAddressModel> getList4Member(Key messageKey, Key groupKey, Key memberKey) {
        MessageAddressModelMeta e = (MessageAddressModelMeta) getModelMeta();
        Set<FilterCriterion> filter = new HashSet<FilterCriterion>();
        filter.add(e.messageKey.equal(messageKey));
        filter.add(e.groupKey.equal(groupKey));
        filter.add(e.memberKey.equal(memberKey));
        return getList(filter, null, (InMemorySortCriterion[]) null);
    }
    
    /**
     * MessageAddress削除.
     * Messageに紐付くデータを削除します。
     * @param messageKey 削除対象MessageKey
     * @param groupKey 削除対象GroupKey
     */
    public void delete4MessageKey(Key messageKey, Key groupKey) {
        List<MessageAddressModel> list = getList4Message(messageKey, groupKey);
        for(MessageAddressModel target : list) {
            delete(target.getKey());
        }
    }
}
