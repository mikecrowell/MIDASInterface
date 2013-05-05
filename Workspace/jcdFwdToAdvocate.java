package prjAMLprjFwdToDomainsprjFwdToARDomain;


import org.aurora.acl.engine.utils.Constants;
import org.aurora.acl.engine.utils.QueueRouting;
import org.aurora.acl.engine.error.ErrorHandler;
import java.rmi.RemoteException;


public class jcdFwdToAdvocate
{

    public com.stc.codegen.logger.Logger logger;

    public com.stc.codegen.alerter.Alerter alerter;

    public com.stc.codegen.util.CollaborationContext collabContext;

    public com.stc.codegen.util.TypeConverter typeConverter;

    private final String DOMAIN_CONNECTIVITY_ISSUE = "E1009C";

    public void receive( com.stc.connectors.jms.Message input, com.stc.connectors.jms.JMS qErrorMessage )
        throws Throwable
    {
        try {
            QueueRouting queueRoute = new QueueRouting();
            // Route to Midas queue in Advocate domain
            queueRoute.routeToQueueInDomain( "localhost", 41007, "qEndPointAdvMidas", input );
        } catch ( Exception e ) {
            // Sends error message to qErrorMessage. This will also stop the jcd if the exception is remote exception and shutdowns the jcd.
            ErrorHandler.getInstance().handleError( input, qErrorMessage, new Exception( DOMAIN_CONNECTIVITY_ISSUE ), collabContext, logger, alerter, Constants.SEVERITY_FATAL );
            alerter.fatal( DOMAIN_CONNECTIVITY_ISSUE + " : " + e.getMessage(), "HL7" );
        }
    }

}