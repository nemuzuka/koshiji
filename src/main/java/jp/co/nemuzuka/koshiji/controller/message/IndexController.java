package jp.co.nemuzuka.koshiji.controller.message;

import org.slim3.controller.Navigation;
import org.slim3.datastore.Datastore;

import jp.co.nemuzuka.controller.HtmlController;
import jp.co.nemuzuka.koshiji.service.GroupMemberService;
import jp.co.nemuzuka.koshiji.service.impl.GroupMemberServiceImpl;

/**
 * MessageTOP画面のController.
 * @author kazumune
 */
public class IndexController extends HtmlController {

    GroupMemberService service = GroupMemberServiceImpl.getInstance();
    
    /* (非 Javadoc)
     * @see jp.co.nemuzuka.controller.HtmlController#execute()
     */
    @Override
    protected Navigation execute() throws Exception {
        requestScope("members", 
            service.getMemberList(Datastore.stringToKey(getUserInfo().selectedGroupKeyString)));
        return forward("/message/index.jsp");
    }
}
