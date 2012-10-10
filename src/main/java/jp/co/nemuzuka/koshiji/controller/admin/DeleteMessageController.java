package jp.co.nemuzuka.koshiji.controller.admin;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import jp.co.nemuzuka.controller.AbsController;
import jp.co.nemuzuka.entity.GlobalTransaction;
import jp.co.nemuzuka.entity.TransactionEntity;
import jp.co.nemuzuka.koshiji.service.MessageDeleteService;
import jp.co.nemuzuka.koshiji.service.impl.MessageDeleteServiceImpl;
import jp.co.nemuzuka.utils.ConvertUtils;
import jp.co.nemuzuka.utils.CurrentDateUtils;
import jp.co.nemuzuka.utils.DateTimeUtils;

import org.slim3.controller.Navigation;

import com.google.appengine.api.datastore.Key;

/**
 * 一定期間過ぎたMessageを削除するController.
 * backendsで呼ばれることを想定します。
 * @author kazumune
 */
public class DeleteMessageController extends AbsController {

    private final static Logger LOG = 
            Logger.getLogger(DeleteMessageController.class.getName());
    
    private MessageDeleteService messageDeleteService = MessageDeleteServiceImpl.getInstance();

    /* (非 Javadoc)
     * @see org.slim3.controller.Controller#run()
     */
    @Override
    protected Navigation run() throws Exception {
        LOG.info("started.");
        setTransaction();
        
        //システム日付から保存期間を引いた日付以前のMessageが削除対象
        Date targetDate = CurrentDateUtils.getInstance().getCurrentDateTime();
        int date = ConvertUtils.toInteger(System.getProperty("jp.co.nemuzuka.message.delete.day", "30"));
        targetDate = DateTimeUtils.addDays(targetDate, (date * -1));
        
        //削除対象のデータ取得
        List<Key> list = messageDeleteService.getDeleteTarget(targetDate);
        
        //データ、関連データを削除
        int cnt = 0;
        for(Key target : list) {
            
            messageDeleteService.delete(target);
            
            //1件処理を行うたびにCommit
            TransactionEntity entity = GlobalTransaction.transaction.get();
            entity.commit();
            entity.begin();
            cnt++;
        }
        LOG.info("delete count is " + cnt);
        return null;
    }

}
