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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.nemuzuka.koshiji.dao.CommentDao;
import jp.co.nemuzuka.koshiji.dao.MemberDao;
import jp.co.nemuzuka.koshiji.dao.MemberGroupConnDao;
import jp.co.nemuzuka.koshiji.dao.MessageAddressDao;
import jp.co.nemuzuka.koshiji.dao.MessageDao;
import jp.co.nemuzuka.koshiji.dao.UnreadMessageDao;
import jp.co.nemuzuka.koshiji.entity.CommentModelEx;
import jp.co.nemuzuka.koshiji.entity.MessageModelEx;
import jp.co.nemuzuka.koshiji.model.AbsModel;
import jp.co.nemuzuka.koshiji.model.CommentModel;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.koshiji.model.MessageAddressModel;
import jp.co.nemuzuka.koshiji.model.MessageModel;
import jp.co.nemuzuka.koshiji.model.UnreadMessageModel;
import jp.co.nemuzuka.koshiji.service.MessageSearchService;
import jp.co.nemuzuka.utils.ConvertUtils;
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
    CommentDao commentDao = CommentDao.getInstance();
    
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
        
        //1ページ目を表示する場合、表示する可能性のあるMessageKeyのListを取得し、DBに格納する
        //2ページ以降の場合、DBからMessageKeyのListを取得する
        //MessageKeyのListを元に表示対象のページに合致するデータを取得する
        
        List<MessageModel> targetMessageList = null;
        if(param.pageNo == 1) {
            //表示する可能性のあるMessageのKeyListを作成する
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
            targetMessageList = (List<MessageModel>) PagerUtils.createTargetList(allMessageList, 
                param.pageNo, param.limit);
            
            //表示対象のMessageKeyを戻り値として設定
            param.messageKeyStrings = toKeyStringList(allMessageList);
            
        } else {
            List<String> targetMessageKeyList = 
                    (List<String>) PagerUtils.createTargetList(param.messageKeyStrings, 
                        param.pageNo, param.limit);
            Key[] targetMessageKeys = ConvertUtils.toKeyArray(targetMessageKeyList);
            targetMessageList = messageDao.getList(targetMessageKeys);
        }
        
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
        
        Result result =  createResult(targetMessageList, createMemberMap, unreadMessageMap, 
            param.memberKey, param.limit);
        result.messageKeyStrings = param.messageKeyStrings;
        return result;
        
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.MessageSearchService#getCommentList(com.google.appengine.api.datastore.Key, com.google.appengine.api.datastore.Key, com.google.appengine.api.datastore.Key)
     */
    @Override
    public CommentResult getCommentList(Key messageKey, Key memberKey,
            Key groupKey) {
        CommentResult result = new CommentResult();
        
        //MemberがGroupと関連づいていない場合、処理終了
        if(memberGroupConnDao.isJoinMember(memberKey, groupKey) == false) {
            return result;
        }
        if(messageAddressDao.isExistsMember(messageKey, groupKey, memberKey) == false) {
            return result;
        }
        MessageModel message = messageDao.get(messageKey);
        if(message == null || message.getGroupKey().equals(groupKey) == false) {
            return result;
        }
        
        //Commentを取得
        List<CommentModel> commentList = commentDao.getList(messageKey);
        
        //Messageに紐付く全ての宛先を取得
        List<MessageAddressModel> addressList = messageAddressDao.getList4Message(messageKey, groupKey);
        
        //MemberSetを作成
        Set<Key> createMemberKey = new HashSet<Key>();
        //コメント作成者分
        for(CommentModel target : commentList) {
            if(target.getCreateMemberKey() != null) {
                createMemberKey.add(target.getCreateMemberKey());
            }
        }
        //Message宛先分
        for(MessageAddressModel target : addressList) {
            createMemberKey.add(target.getMemberKey());
        }
        
        //作成者名を取得する為にMemberを取得
        Map<Key, MemberModel> createMemberMap = 
                memberDao.getMap(createMemberKey.toArray(new Key[0]));
        
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd HHmm");
        for(CommentModel target : commentList) {
            CommentModelEx ex = new CommentModelEx();
            ex.setModel(target);
            ex.setLastUpdate(sdf.format(target.getLastUpdate()));
            MemberModel member = createMemberMap.get(target.getCreateMemberKey());
            ex.setCreateMemberName("");
            if(member != null) {
                ex.setCreateMemberName(member.getName());
            }
            //コメントの作成者 or メッセージ作成者の場合、コメント削除可
            if(target.getCreateMemberKey() != null) {
                if(target.getCreateMemberKey().equals(memberKey) || 
                        message.getCreateMemberKey().equals(memberKey)) {
                    ex.setDeleteAuth(true);
                }
            }
            result.list.add(ex);
        }
        
        //宛先文字列を作成
        result.address = createAddressStr(addressList, createMemberMap);
        
        return result;
    }
    
    /**
     * 宛先文字列作成.
     * Member名を文字連結したものを返却します。
     * @param addressList 宛先List
     * @param createMemberMap Member設定Map
     * @return 宛先文字列
     */
    private String createAddressStr(List<MessageAddressModel> addressList,
            Map<Key, MemberModel> createMemberMap) {
        StringBuilder sb = new StringBuilder();
        for(MessageAddressModel target : addressList) {
            MemberModel member = createMemberMap.get(target.getMemberKey());
            if(member == null) {
                continue;
            }
            
            if(sb.length() != 0) {
                sb.append(",");
            }
            sb.append(member.getName());
        }
        return sb.toString();
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

    /**
     * ModelKeyStringList作成.
     * @param list 作成対象ModelList
     * @return 変換後ModelKeyStringList
     */
    private static List<String> toKeyStringList(List<MessageModel> list) {
        List<String> retList = new ArrayList<String>();
        for(AbsModel target : list) {
            retList.add(target.getKeyToString());
        }
        return retList;
    }
}
