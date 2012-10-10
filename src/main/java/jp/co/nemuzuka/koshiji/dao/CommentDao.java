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

import jp.co.nemuzuka.koshiji.meta.CommentModelMeta;
import jp.co.nemuzuka.koshiji.model.CommentModel;

import org.slim3.datastore.FilterCriterion;
import org.slim3.datastore.ModelMeta;

import com.google.appengine.api.datastore.Key;

/**
 * CommentModelに対するDao.
 * @author kazumune
 */
public class CommentDao extends AbsDao {

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.dao.AbsDao#getModelMeta()
     */
    @SuppressWarnings("rawtypes")
    @Override
    ModelMeta getModelMeta() {
        return CommentModelMeta.get();
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.dao.AbsDao#getModelClass()
     */
    @SuppressWarnings("rawtypes")
    @Override
    Class getModelClass() {
        return CommentModel.class;
    }
    
    private static CommentDao dao = new CommentDao();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static CommentDao getInstance() {
        return dao;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private CommentDao(){}
    
    /**
     * Comment一覧取得.
     * 指定した検索条件に紐付くComment一覧を取得します。
     * @param messageKey MessageKey
     * @return 該当レコード
     */
    public List<CommentModel> getList(Key messageKey) {
        CommentModelMeta e = (CommentModelMeta) getModelMeta();
        Set<FilterCriterion> filter = new HashSet<FilterCriterion>();
        filter.add(e.messageKey.equal(messageKey));
        return getList(filter, null, e.lastUpdate.desc, e.no.desc);
    }
    
    /**
     * Comment削除.
     * MessageKeyに紐付くCommentを削除します。
     * @param messageKey MessageKey
     */
    public void delete4MessageKey(Key messageKey) {
        List<CommentModel> list = getList(messageKey);
        for(CommentModel target : list) {
            delete(target.getKey());
        }
    }
}
