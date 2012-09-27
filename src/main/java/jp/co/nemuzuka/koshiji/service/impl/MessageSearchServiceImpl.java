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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.nemuzuka.koshiji.dao.MemberDao;
import jp.co.nemuzuka.koshiji.dao.MemberGroupConnDao;
import jp.co.nemuzuka.koshiji.dao.MessageAddressDao;
import jp.co.nemuzuka.koshiji.dao.MessageDao;
import jp.co.nemuzuka.koshiji.dao.UnreadMessageDao;
import jp.co.nemuzuka.koshiji.entity.MessageModelEx;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.koshiji.model.MessageAddressModel;
import jp.co.nemuzuka.koshiji.model.MessageModel;
import jp.co.nemuzuka.koshiji.model.UnreadMessageModel;
import jp.co.nemuzuka.koshiji.service.MessageSearchService;
import jp.co.nemuzuka.utils.DateTimeUtils;
import jp.co.nemuzuka.utils.PagerUtils;

import com.google.appengine.api.datastore.Key;

/**
 * MessageSearchServiceの実装クラス.
 * @author kazumune
 */
public class MessageSearchServiceImpl implements MessageSearchService {

    MessageDao messageDao = MessageDao.getInstance();
    MessageAddressDao messageAddressDao = MessageAddressDao.getInstance();
    MemberGroupConnDao memberGroupConnDao = MemberGroupConnDao.getInstance();
    UnreadMessageDao unreadMessageDao = UnreadMessageDao.getInstance();
    MemberDao memberDao = MemberDao.getInstance();
    
    private static MessageSearchServiceImpl impl = new MessageSearchServiceImpl();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static MessageSearchServiceImpl getInstance() {
        return impl;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private MessageSearchServiceImpl(){}

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.MessageSearchService#getList(jp.co.nemuzuka.koshiji.service.MessageSearchService.SearchParam)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Result getList(SearchParam param) {
        
        //MemberがGroupと関連づいていない場合、処理終了
        if(memberGroupConnDao.isJoinMember(param.memberKey, param.groupKey) == false) {
            return new Result();
        }
        //表示する可能性のあるListを取得
        List<MessageAddressModel> messageAddressList = 
                messageAddressDao.getList(param.memberKey, param.groupKey);
        Set<Key> messageKeySet = new HashSet<Key>();
        for(MessageAddressModel target : messageAddressList) {
            messageKeySet.add(target.getMessageKey());
        }

        //MessageModelの情報を取得
        //※本当は、ソート順を意識してKeyだけ取得し、Keyに紐付くMessageを取得するようにしたかったが、
        //　slim3ではasKeyListの場合、ソート順は指定できないので全件メモリ上に保持するようにした
        List<MessageModel> allMessageList = 
                messageDao.getList(messageKeySet.toArray(new Key[0]));

        //表示するページ番号、ページあたりの表示件数を参照し、表示するMessageを指定して取得
        List<MessageModel> targetMessageList = (List<MessageModel>) PagerUtils.createTargetList(allMessageList, 
            param.pageNo, param.limit);
        
        Set<Key> targetMessageKey = new HashSet<Key>();
        Set<Key> createMemberKey = new HashSet<Key>();
        for(MessageModel target : targetMessageList) {
            targetMessageKey.add(target.getKey());
            createMemberKey.add(target.getCreateMemberKey());
        }
        
        //作成者名を取得する為にMemberを取得
        Map<Key, MemberModel> createMemberMap = 
                memberDao.getMap(createMemberKey.toArray(new Key[0]));
        
        //未読Message情報を取得
        Map<Key, UnreadMessageModel> unreadMessageMap = 
                unreadMessageDao.getMap(param.memberKey, targetMessageKey.toArray(new Key[0]));
        
        return createResult(targetMessageList, createMemberMap, unreadMessageMap, 
            param.memberKey, param.limit);
    }
    
    /**
     * Result作成.
     * 表示対象MessageListを元にResultを生成します。
     * その際未読Messageは、既読状態にします。
     * @param targetMessageList 表示対象MessageList
     * @param createMemberMap MemberMap
     * @param unreadMessageMap 未読MessageMap
     * @param memberKey ログインユーザのMemberKey
     * @param limit 一覧表示件数
     * @return 戻り値
     */
    private Result createResult(List<MessageModel> targetMessageList, 
            Map<Key, MemberModel> createMemberMap, Map<Key, UnreadMessageModel> unreadMessageMap,
            Key memberKey, int limit) {
        Result result = new Result();
        Set<Key> deleteUnreadMessageKey = new HashSet<Key>();
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd HHmm");
        int size = targetMessageList.size();
        for(int i = 0; i < size; i++) {
            
            if((i + 1) > limit) {
                //次ページの存在チェック用のレコードが存在する場合、
                //次ページあり、として戻り値のListに含めない
                result.hasNextPage = true;
                continue;
            }
            
            MessageModel target = targetMessageList.get(i);
            MessageModelEx model = new MessageModelEx();
            model.setModel(target);
            MemberModel member = createMemberMap.get(target.getCreateMemberKey());
            model.setCreateMemberName("");
            if(member != null) {
                model.setCreateMemberName(member.getName());
            }
            model.setLastUpdate(sdf.format(target.getLastUpdate()));
            //未読の設定
            UnreadMessageModel unread = unreadMessageMap.get(target.getKey());
            if(unread != null) {
                model.setUnread(true);
                deleteUnreadMessageKey.add(unread.getKey());
            }
            //作成者がログインユーザの場合、作成者である旨設定
            if(target.getCreateMemberKey().equals(memberKey)) {
                model.setCreate(true);
            }
            result.list.add(model);
        }
        //未読Messageを削除することで、既読状態にする
        unreadMessageDao.delete(deleteUnreadMessageKey.toArray(new Key[0]));

        return result;
    }
}
