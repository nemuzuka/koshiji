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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jp.co.nemuzuka.common.UniqueKey;
import jp.co.nemuzuka.koshiji.meta.MemberGroupConnModelMeta;
import jp.co.nemuzuka.koshiji.model.MemberGroupConnModel;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.FilterCriterion;
import org.slim3.datastore.InMemorySortCriterion;
import org.slim3.datastore.ModelMeta;

import com.google.appengine.api.datastore.Key;

/**
 * MemberGroupConnModelに対するDao.
 * @author kazumune
 */
public class MemberGroupConnDao extends AbsDao {

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.dao.AbsDao#getModelMeta()
     */
    @SuppressWarnings("rawtypes")
    @Override
    ModelMeta getModelMeta() {
        return MemberGroupConnModelMeta.get();
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.dao.AbsDao#getModelClass()
     */
    @SuppressWarnings("rawtypes")
    @Override
    Class getModelClass() {
        return MemberGroupConnModel.class;
    }
    
    private static MemberGroupConnDao dao = new MemberGroupConnDao();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static MemberGroupConnDao getInstance() {
        return dao;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private MemberGroupConnDao(){}

    /**
     * 一覧取得.
     * 指定したMemberKeyに紐付く一覧を取得します。
     * @param key memberKey
     * @return 該当レコード
     */
    public List<MemberGroupConnModel> getList(Key memberKey) {
        MemberGroupConnModelMeta e = (MemberGroupConnModelMeta) getModelMeta();
        Set<FilterCriterion> filter = new HashSet<FilterCriterion>();
        filter.add(e.memberKey.equal(memberKey));
        return getList(filter, null, e.key.asc);
    }

    /**
     * 一覧取得.
     * 指定したGroupKeyに紐付く一覧を取得します。
     * @param groupKey groupKey
     * @return 該当レコード
     */
    public List<MemberGroupConnModel> getMemberList(Key groupKey) {
        return getMemberList(new Key[]{groupKey});
    }
    
    /**
     * 一覧取得.
     * 指定したGroupKeyに紐付く一覧を取得します。
     * @param groupKeys groupKey配列
     * @return 該当レコード
     */
    public List<MemberGroupConnModel> getMemberList(Key...groupKeys) {
        
        if(groupKeys == null || groupKeys.length == 0) {
            return new ArrayList<MemberGroupConnModel>();
        }
        
        MemberGroupConnModelMeta e = (MemberGroupConnModelMeta) getModelMeta();
        Set<FilterCriterion> filter = new HashSet<FilterCriterion>();
        filter.add(e.groupKey.in(groupKeys));
        return getList(filter, null, e.admin.desc, e.key.asc);
    }
    
    
    /**
     * Set取得.
     * 指定したGroupKeyに紐付くMemberのKeyのSetを取得します。
     * @param groupKey groupKey
     * @return 紐付くMemberのKey
     */
    public Set<Key> getMemberSet(Key groupKey) {
        Set<Key> set = new LinkedHashSet<Key>();
        List<MemberGroupConnModel> list = getMemberList(groupKey);
        for(MemberGroupConnModel target : list) {
            set.add(target.getMemberKey());
        }
        return set;
    }
    
    
    /**
     * 存在チェック.
     * 指定したMember、Groupの組み合わせが登録されているかチェックします。
     * @param memberKey チェック対象MemberKey
     * @param groupKey チェック対象GroupKey
     * @return 登録されている場合、true
     */
    public boolean isJoinMember(Key memberKey, Key groupKey) {
        MemberGroupConnModelMeta e = (MemberGroupConnModelMeta) getModelMeta();
        Set<FilterCriterion> filter = new HashSet<FilterCriterion>();
        filter.add(e.memberKey.equal(memberKey));
        filter.add(e.groupKey.equal(groupKey));
        List<MemberGroupConnModel> list = getList(filter, null, new InMemorySortCriterion[0]);
        if(list.size() != 1) {
            return false;
        }
        return true;
    }
    
    /**
     * 関連削除.
     * 指定した関連を削除します。その際、一意制約データも削除します。
     * @param key MemberGroupConnModelのKey
     * @param memberKey MemberKey
     * @param groupKey GroupKey
     */
    public void deleteMember(Key key, Key memberKey, Key groupKey) {
        String memberKeyString = Datastore.keyToString(memberKey);
        String groupKeyString = Datastore.keyToString(groupKey);
        String uniqueKey = memberKeyString + ":" + groupKeyString;
        Datastore.deleteUniqueValue(UniqueKey.memberGroupConn.name(), uniqueKey);
        delete(key);
    }
}
