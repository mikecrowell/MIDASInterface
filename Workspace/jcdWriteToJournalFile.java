package prjCommonProcessprjCommonJcds;


import java.lang.StringBuffer;
import java.text.SimpleDateFormat;


public class jcdWriteToJournalFile
{

    public com.stc.codegen.logger.Logger logger;

    public com.stc.codegen.alerter.Alerter alerter;

    public com.stc.codegen.util.CollaborationContext collabContext;

    public com.stc.codegen.util.TypeConverter typeConverter;

    public void receive( com.stc.connectors.jms.Message input, com.stc.eways.batchext.BatchLocal local )
        throws Throwable
    {
        try {
            writeToFile( input, local );
        } catch ( Exception e ) {
            e.printStackTrace();
            throw e;
        }
    }

    private void writeToFile( com.stc.connectors.jms.Message input, com.stc.eways.batchext.BatchLocal local )
        throws Exception
    {
        SimpleDateFormat sdf = new SimpleDateFormat( "MM.dd.yyyy HH:mm:ss z" );
        StringBuffer payload = new StringBuffer( "\r****************Logged the message at " + sdf.format( new java.util.Date() ) + "****************\r" );
        payload.append( input.getTextMessage() );
        payload.append( "\n\n" );
        local.getConfiguration().setTargetDirectoryNameIsPattern( true );
        local.getConfiguration().setTargetFileNameIsPattern( true );
        local.getConfiguration().setAppend( true );
        local.getClient().setPayload( payload.toString().getBytes() );
        logger.info( "8888888" );
        local.getClient().put();
    }

}