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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slim3.datastore.Datastore;

import jp.co.nemuzuka.koshiji.dao.CommentDao;
import jp.co.nemuzuka.koshiji.dao.MemberGroupConnDao;
import jp.co.nemuzuka.koshiji.dao.MessageAddressDao;
import jp.co.nemuzuka.koshiji.dao.MessageDao;
import jp.co.nemuzuka.koshiji.dao.MessageSeqDao;
import jp.co.nemuzuka.koshiji.dao.UnreadMessageDao;
import jp.co.nemuzuka.koshiji.model.CommentModel;
import jp.co.nemuzuka.koshiji.model.MessageAddressModel;
import jp.co.nemuzuka.koshiji.model.MessageModel;
import jp.co.nemuzuka.koshiji.model.UnreadMessageModel;
import jp.co.nemuzuka.koshiji.service.MessageEditService;
import jp.co.nemuzuka.utils.CurrentDateUtils;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

/**
 * MessageSearchServiceの実装クラス.
 * @author kazumune
 */
public class MessageEditServiceImpl implements MessageEditService {

    MessageDao messageDao = MessageDao.getInstance();
    MessageAddressDao messageAddressDao = MessageAddressDao.getInstance();
    MemberGroupConnDao memberGroupConnDao = MemberGroupConnDao.getInstance();
    UnreadMessageDao unreadMessageDao = UnreadMessageDao.getInstance();
    MessageSeqDao messageSeqDao = MessageSeqDao.getInstance();
    CommentDao commentDao = CommentDao.getInstance();
    
    private static MessageEditServiceImpl impl = new MessageEditServiceImpl();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static MessageEditServiceImpl getInstance() {
        return impl;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private MessageEditServiceImpl(){}

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.MessageEditService#createMessage(jp.co.nemuzuka.koshiji.service.MessageEditService.CreateParam)
     */
    @Override
    public void createMessage(CreateParam param) {
        //MemberがGroupと関連づいていない場合、処理終了
        if(memberGroupConnDao.isJoinMember(param.createMemberKey, param.groupKey) == false) {
            return;
        }
        Key[] memberKeys = createAddressMemberKeys(param);
        if(memberKeys.length == 0) {
            return;
        }
        
        //メッセージの作成
        Key messageKey = putMessage(param);
        
        //宛先・未読の作成
        createUnreadMessageAddress(messageKey, memberKeys, param.groupKey);
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.MessageEditService#createComment(jp.co.nemuzuka.koshiji.service.MessageEditService.CreateCommentParam)
     */
    @Override
    public void createComment(CreateCommentParam param) {
        //MemberがGroupと関連づいていない場合、処理終了
        if(memberGroupConnDao.isJoinMember(param.createMemberKey, param.groupKey) == false) {
            return;
        }
        
        //Messageを取得
        MessageModel message = messageDao.get(param.messageKey);
        if(message == null || message.getGroupKey().equals(param.groupKey) == false) {
            //存在しない、またはグループが異なる場合、処理終了
            return;
        }
        
        //Messageに紐付く宛先を取得
        Set<Key> targetMemberSet = createMemberSet(
            messageAddressDao.getList4Message(param.messageKey, param.groupKey));
        if(targetMemberSet.contains(param.createMemberKey) == false) {
            //コメント登録者が宛先に含まれていない場合、処理終了
            return;
        }
        
        //Messageを更新
        message.setLastUpdate(CurrentDateUtils.getInstance().getCurrentDateTime());
        message.setComment(true);
        messageDao.put(message);
        
        //登録されている未読情報を削除し、再度put(ただし、自分は除く)
        createComment(param, targetMemberSet);
    }
    
    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.MessageEditService#deleteComment(com.google.appengine.api.datastore.Key, com.google.appengine.api.datastore.Key, com.google.appengine.api.datastore.Key)
     */
    @Override
    public void deleteComment(Key messageKey, Key commentKey, Key memberKey) {
        MessageModel message = messageDao.get(messageKey);
        CommentModel comment = commentDao.get(commentKey);
        if(message == null || comment == null) {
            return;
        }
        if(message.getCreateMemberKey().equals(memberKey) == false &&
                comment.getCreateMemberKey().equals(memberKey) == false) {
            return;
        }

        //固定文言で上書き
        String msg = System.getProperty("jp.co.nemuzuka.message.delete.comment", "deleted.");
        comment.setBody(new Text(msg));
        comment.setCreateMemberKey(null);
        commentDao.put(comment);
    }

    /**
     * Commentの作成、未読状態の設定.
     * @param param 登録情報
     * @param targetMemberSet 宛先MemberSet
     */
    private void createComment(CreateCommentParam param,
            Set<Key> targetMemberSet) {
        //Comment登録
        putComment(param);
        
        //該当Messageの未読情報を削除
        List<UnreadMessageModel> unreadMessageList = unreadMessageDao.getList(param.messageKey);
        for(UnreadMessageModel target : unreadMessageList) {
            unreadMessageDao.delete(target.getKey());
        }
        
        //未読情報を追加
        for(Key memberKey : targetMemberSet) {
            if(memberKey.equals(param.createMemberKey)) {
                //自分は未読として登録しない
                continue;
            }
            
            UnreadMessageModel model = new UnreadMessageModel();
            model.setMemberKey(memberKey);
            model.setMessageKey(param.messageKey);
            unreadMessageDao.put(model);
        }
    }

    /**
     * Comment登録.
     * @param param 登録情報
     */
    private void putComment(CreateCommentParam param) {
        CommentModel model = new CommentModel();
        model.setBody(new Text(param.body));
        model.setCreateMemberKey(param.createMemberKey);
        model.setLastUpdate(CurrentDateUtils.getInstance().getCurrentDateTime());
        model.setMessageKey(param.messageKey);
        model.setNo(messageSeqDao.createMessageSeq());
        commentDao.put(model);
    }

    /**
     * メッセージの宛先、未読情報作成.
     * @param messageKey MessageKey
     * @param memberKeys 宛先MemberKey配列
     * @param groupKey GroupKey
     */
    private void createUnreadMessageAddress(Key messageKey, Key[] memberKeys, Key groupKey) {
        for(Key memberKey : memberKeys) {
            MessageAddressModel model = new MessageAddressModel();
            model.setGroupKey(groupKey);
            model.setMemberKey(memberKey);
            model.setMessageKey(messageKey);
            messageAddressDao.put(model);
            
            UnreadMessageModel unreadMessage = new UnreadMessageModel();
            unreadMessage.setMemberKey(memberKey);
            unreadMessage.setMessageKey(messageKey);
            unreadMessageDao.put(unreadMessage);
        }
    }

    /**
     * Message登録.
     * 引数の情報で新規Messageを登録します。
     * @param param 作成パラメータ
     * @return MessageKey
     */
    private Key putMessage(CreateParam param) {
        MessageModel message = new MessageModel();
        message.setBody(new Text(param.body));
        message.setCreateMemberKey(param.createMemberKey);
        message.setGroupKey(param.groupKey);
        message.setLastUpdate(CurrentDateUtils.getInstance().getCurrentDateTime());
        message.setNo(messageSeqDao.createMessageSeq());
        messageDao.put(message);
        return message.getKey();
    }

    /**
     * 宛先MemberKey配列作成.
     * Messageの宛先になるMemberのKey配列を作成します。
     * @param param 作成パラメータ
     * @return 宛先MemberKey配列(登録する宛先が存在しない場合、サイズ0の配列)
     */
    private Key[] createAddressMemberKeys(CreateParam param) {
        if(param.memberKeyStrings == null || param.memberKeyStrings.length == 0) {
            return new Key[0];
        }
        //グループに紐付く全Memberの情報を取得する
        Set<Key> allMemberKey = memberGroupConnDao.getMemberSet(param.groupKey);
        
        Set<Key> targetMemberKeySet = new LinkedHashSet<Key>();
        for(String targetMemberKey : param.memberKeyStrings) {
            if(TARGET_ALL.equals(targetMemberKey)) {
                //全てのMemberが対象の場合、グループに紐付く全Member
                return allMemberKey.toArray(new Key[0]);
            }
            Key memberKey = Datastore.stringToKey(targetMemberKey);
            if(allMemberKey.contains(memberKey)) {
                targetMemberKeySet.add(memberKey);
            }
        }
        //作成者も宛先に含める
        targetMemberKeySet.add(param.createMemberKey);
        return targetMemberKeySet.toArray(new Key[0]);
    }

    /**
     * MemberSet生成.
     * @param list MessageAddressModelのList
     * @return MemberのSet
     */
    private Set<Key> createMemberSet(List<MessageAddressModel> list) {
        
        Set<Key> memberSet = new HashSet<Key>();
        for(MessageAddressModel target : list) {
            memberSet.add(target.getMemberKey());
        }
        return memberSet;
    }
}
