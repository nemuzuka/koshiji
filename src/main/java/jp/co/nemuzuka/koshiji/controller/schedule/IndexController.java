package jp.co.nemuzuka.koshiji.controller.schedule;

import org.slim3.controller.Navigation;

import jp.co.nemuzuka.controller.HtmlController;
import jp.co.nemuzuka.koshiji.entity.ScheduleEntity;
import jp.co.nemuzuka.utils.CurrentDateUtils;

/**
 * ScheduleTOP画面のController.
 * @author kazumune
 */
public class IndexController extends HtmlController {

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.controller.HtmlController#execute()
     */
    @Override
    protected Navigation execute() throws Exception {
        
        //Session情報初期化
        ScheduleEntity entity = new ScheduleEntity();
        entity.baseDate = CurrentDateUtils.getInstance().getCurrentDate();
        sessionScope(ScheduleEntity.KEY_NAME, entity);
        
        return forward("/schedule/index.jsp");
    }
}
