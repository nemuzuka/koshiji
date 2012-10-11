package jp.co.nemuzuka.koshiji.controller.admin;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;

import com.google.appengine.api.backends.BackendServiceFactory;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions.Builder;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

/**
 * 一定期間過ぎたMessageを削除する処理をTaskqueueから起動する
 * @author kazumune
 */
public class DeletemessagetaskController extends Controller {

    /* (非 Javadoc)
     * @see org.slim3.controller.Controller#run()
     */
    @Override
    protected Navigation run() throws Exception {
        Queue queue = QueueFactory.getDefaultQueue();
        queue.add(Builder.withUrl("/admin/deletemessagetask").method(Method.GET).header("Host", 
            BackendServiceFactory.getBackendService().getBackendAddress("deleteMessage")));
        response.setContentType("text/plain");
        response.getWriter().println("called");
        return null;
    }

}
