package jp.co.nemuzuka.koshiji.controller.message;

import org.slim3.controller.Navigation;

import jp.co.nemuzuka.controller.HtmlController;

/**
 * MessageTOP画面のController.
 * @author kazumune
 */
public class IndexController extends HtmlController {

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.controller.HtmlController#execute()
     */
    @Override
    protected Navigation execute() throws Exception {
        return forward("/message/index.jsp");
    }
}
