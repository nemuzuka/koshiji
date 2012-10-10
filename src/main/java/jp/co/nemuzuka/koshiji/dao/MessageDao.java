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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.co.nemuzuka.koshiji.meta.MessageModelMeta;
import jp.co.nemuzuka.koshiji.model.MessageModel;

import org.slim3.datastore.FilterCriterion;
import org.slim3.datastore.ModelMeta;

import com.google.appengine.api.datastore.Key;

/**
 * MessageModelに対するDao.
 * @author kazumune
 */
public class MessageDao extends AbsDao {

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.dao.AbsDao#getModelMeta()
     */
    @SuppressWarnings("rawtypes")
    @Override
    ModelMeta getModelMeta() {
        return MessageModelMeta.get();
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.dao.AbsDao#getModelClass()
     */
    @SuppressWarnings("rawtypes")
    @Override
    Class getModelClass() {
        return MessageModel.class;
    }
    
    private static MessageDao dao = new MessageDao();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static MessageDao getInstance() {
        return dao;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private MessageDao(){}

    /**
     * Message一覧取得.
     * 指定したKeyに紐付くMessage一覧を取得します。
     * Keyが未指定の場合、size0のListを返却します
     * @param keys Key情報
     * @return 該当レコード
     */
    public List<MessageModel> getList(Key...keys) {
        MessageModelMeta e = (MessageModelMeta) getModelMeta();
        Set<FilterCriterion> filter = new HashSet<FilterCriterion>();
        if(keys != null && keys.length != 0) {
            filter.add(e.key.in(keys));
        } else {
            return new ArrayList<MessageModel>();
        }
        return getList(filter, null, e.lastUpdate.desc, e.no.desc);
    }
    
    /**
     * Key一覧取得.
     * 最終更新日付 <= 指定日付
     * の関係を持つMessageのKeyListを取得します。
     * @param targetDate 指定日付
     * @return 該当レコード
     */
    public List<Key> getKeyList(Date targetDate) {
        MessageModelMeta e = (MessageModelMeta) getModelMeta();
        Set<FilterCriterion> filter = new HashSet<FilterCriterion>();
        filter.add(e.lastUpdate.lessThanOrEqual(targetDate));
        return getKeyList(filter);
    }
}
