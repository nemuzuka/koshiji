package jp.co.nemuzuka.koshiji.controller.admin;

import java.util.logging.Logger;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

/**
 * Backendsが起動した際に呼び出される
 * @author kazumune
 */
public class StartBackendsController extends Controller {

    private final static Logger LOG = 
            Logger.getLogger(StartBackendsController.class.getName());
    /* (非 Javadoc)
     * @see org.slim3.controller.Controller#run()
     */
    @Override
    protected Navigation run() throws Exception {
        LOG.info("called.");
        
        response.setContentType("text/plain");
        response.getWriter().println("called");
        return null;
    }

}
