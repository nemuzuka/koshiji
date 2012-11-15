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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.nemuzuka.koshiji.meta.ScheduleModelMeta;
import jp.co.nemuzuka.koshiji.model.ScheduleModel;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.FilterCriterion;
import org.slim3.datastore.InMemoryFilterCriterion;
import org.slim3.datastore.InMemorySortCriterion;
import org.slim3.datastore.ModelMeta;

import com.google.appengine.api.datastore.Key;

/**
 * ScheduleModelに対するDao.
 * @author kazumune
 */
public class ScheduleDao extends AbsDao {

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.dao.AbsDao#getModelMeta()
     */
    @SuppressWarnings("rawtypes")
    @Override
    ModelMeta getModelMeta() {
        return ScheduleModelMeta.get();
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.dao.AbsDao#getModelClass()
     */
    @SuppressWarnings("rawtypes")
    @Override
    Class getModelClass() {
        return ScheduleModel.class;
    }
    
    private static ScheduleDao dao = new ScheduleDao();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static ScheduleDao getInstance() {
        return dao;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private ScheduleDao(){}

    /**
     * 表示対象スケジュール情報取得.
     * 指定Memberがスケジュールに含まれ、かつ
     * １．Modelの開始日 >= 抽出開始日 and Modelの開始日 <= 抽出終了日
     * ２．Modelの終了日 >= 抽出開始日 and Modelの終了日 <= 抽出終了日
     * ３．Modelの開始日 < 抽出開始日 and Modelの終了日 > 抽出終了日
     * のいずれかを満たすスケジュールデータを取得します。
     * @param startDate 抽出開始日
     * @param endDate 抽出終了日
     * @param targetMembers 表示対象MemberKeyStringSet
     * @return 表示対象スケジュールList
     */
    public List<ScheduleModel> getList(Date startDate, Date endDate, Set<String> targetMembers) {
        ScheduleModelMeta e = (ScheduleModelMeta) getModelMeta();
        //Modelの開始日 >= 抽出開始日 and Modelの開始日 <= 抽出終了日のListを取得
        List<ScheduleModel> startList = getList(createStartFilterSet(startDate, endDate, e), targetMembers, e);

        //Modelの終了日 >= 抽出開始日 and Modelの終了日 <= 抽出終了日のListを取得
        List<ScheduleModel> endList = getList(createEndFilterSet(startDate, endDate, e), targetMembers, e);
        
        //Modelの開始日 < 抽出開始日 and Modelの終了日 > 抽出終了日のListを取得
        List<ScheduleModel> rangeList = getList(createRangeFilterSet(startDate, endDate, e), targetMembers, e);
        
        //マージし、ソートする
        List<ScheduleModel> mergedList = merge(startList, endList, rangeList);
        return Datastore.sortInMemory(mergedList, e.startDate.asc, e.startTime.asc, e.key.asc);
    }
    
    /**
     * Key一覧取得.
     * 終了日　＜＝　指定日付
     * の関係を持つScheduleのKeyListを取得します。
     * @param targetDate 指定日付
     * @return 該当レコード
     */
    public List<Key> getKeyList(Date targetDate) {
        ScheduleModelMeta e = (ScheduleModelMeta) getModelMeta();
        Set<FilterCriterion> filter = new HashSet<FilterCriterion>();
        filter.add(e.endDate.lessThanOrEqual(targetDate));
        return getKeyList(filter);
    }
    
    /**
     * マージ処理.
     * 3つのListをマージして、新しいListを作成します。
     * 重複しているレコードは1つにまとめられます。
     * @param startList 開始日に関する検索結果
     * @param endList 終了日に関する検索結果
     * @param rangeList 開始日〜終了日に関する検索結果
     * @return マージ後List
     */
    private List<ScheduleModel> merge(List<ScheduleModel> startList,
            List<ScheduleModel> endList, List<ScheduleModel> rangeList) {
        Map<Key, ScheduleModel> map = new HashMap<Key, ScheduleModel>();
        mergeMap(map, startList);
        mergeMap(map, endList);
        mergeMap(map, rangeList);
        
        List<ScheduleModel> retList = new ArrayList<ScheduleModel>();
        for(Map.Entry<Key, ScheduleModel> target : map.entrySet()) {
            retList.add(target.getValue());
        }
        return retList;
    }

    /**
     * Map設定.
     * Keyが存在しなければMapに追加します。
     * @param map 設定先Map
     * @param targetList 処理対象List
     */
    private void mergeMap(Map<Key, ScheduleModel> map, List<ScheduleModel> targetList) {
        for(ScheduleModel target : targetList) {
            if(map.containsKey(target.getKey()) == false) {
                map.put(target.getKey(), target);
            }
        }
    }
    
    /**
     * 検索実行.
     * 検索条件を元に検索を行います。
     * @param inMemoryFilterSet 検索条件
     * @param targetMembers 表示対象MemberKeyStringSet
     * @param e ModelMeta
     * @return スケジュールList
     */
    private List<ScheduleModel> getList(Set<InMemoryFilterCriterion> inMemoryFilterSet, 
        Set<String> targetMembers, ScheduleModelMeta e) {
        Set<FilterCriterion> filterSet = new HashSet<FilterCriterion>();
        filterSet.add(e.connMember.in(targetMembers));
        return getList(filterSet, inMemoryFilterSet, (InMemorySortCriterion[]) null);
    }
    
    /**
     * 開始日に関する検索条件設定.
     * Modelの開始日 >= 抽出開始日 and Modelの開始日 <= 抽出終了日
     * となる検索条件を返却します
     * @param startDate 抽出開始日
     * @param endDate 抽出終了日
     * @param e ModelMeta
     * @return 検索条件Set
     */
    private Set<InMemoryFilterCriterion> createStartFilterSet(Date startDate, Date endDate, 
        ScheduleModelMeta e) {
        //Modelの開始日に対する条件を作成する
        Set<InMemoryFilterCriterion> startFilterSet = new HashSet<InMemoryFilterCriterion>();
        startFilterSet.add(e.startDate.greaterThanOrEqual(startDate));
        startFilterSet.add(e.startDate.lessThanOrEqual(endDate));
        return startFilterSet;
    }

    /**
     * 終了日に関する検索条件設定.
     * Modelの終了日 >= 抽出開始日 and Modelの終了日 <= 抽出終了日
     * となる検索条件を返却します。
     * @param startDate 抽出開始日
     * @param endDate 抽出終了日
     * @param e ModelMeta
     * @return 検索条件Set
     */
    private Set<InMemoryFilterCriterion> createEndFilterSet(Date startDate, Date endDate, 
        ScheduleModelMeta e) {
        //Modelの終了日に対する条件を作成する
        Set<InMemoryFilterCriterion> endFilterSet = new HashSet<InMemoryFilterCriterion>();
        endFilterSet.add(e.endDate.greaterThanOrEqual(startDate));
        endFilterSet.add(e.endDate.lessThanOrEqual(endDate));
        return endFilterSet;
    }

    /**
     * 開始日〜終了日に関する検索条件設定.
     * Modelの開始日 < 抽出開始日 and Modelの終了日 > 抽出終了日
     * となる検索条件を返却します。
     * @param startDate 抽出開始日
     * @param endDate 抽出終了日
     * @param e ModelMeta
     * @return 検索条件Set
     */
    private Set<InMemoryFilterCriterion> createRangeFilterSet(Date startDate, Date endDate, 
        ScheduleModelMeta e) {
        //開始〜終了までの期間に対する条件を作成する
        Set<InMemoryFilterCriterion> rangeFilterSet = new HashSet<InMemoryFilterCriterion>();
        rangeFilterSet.add(e.startDate.lessThan(startDate));
        rangeFilterSet.add(e.endDate.greaterThan(endDate));
        return rangeFilterSet;
    }
}
