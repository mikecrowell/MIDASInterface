package prjAMLprjResponseprjResultOrderprjAMLToRR1162956282;


import org.aurora.acl.engine.utils.Constants;
import org.aurora.acl.engine.utils.QueueRouting;
import org.aurora.acl.engine.error.ErrorHandler;
import java.rmi.RemoteException;
import org.aurora.acl.engine.utils.Validations;
import org.aurora.acl.engine.error.ValidationException;
import java.io.*;
import java.util.*;


public class jcdProcessAMLToRRE
{

    public com.stc.codegen.logger.Logger logger;

    public com.stc.codegen.alerter.Alerter alerter;

    public com.stc.codegen.util.CollaborationContext collabContext;

    public com.stc.codegen.util.TypeConverter typeConverter;

    private final String HL7_PARSING_ERROR = "E1001S";

    private final String FIELD_SEP = "\\|";

    private final String COMP_SEP = "\\^";

    private final String PROPERTIES = "/jcap/jcaps512/EngineConfig/Config.properties";

    public void receive( com.stc.connectors.jms.Message input, com.stc.connectors.jms.JMS JMS_Data, com.stc.connectors.jms.JMS JMS_Midas, com.stc.connectors.jms.JMS JMS_Error, com.stc.SeeBeyond.OTD_Library.HL7.Generic.HL7_GENERIC_EVT.GENERIC_EVT hl7Generic )
        throws Throwable
    {
        // Send to RRE
        JMS_Data.send( input );
        try {
            hl7Generic.unmarshalFromString( input.getTextMessage() );
            if (isORU( hl7Generic )) {
                String[] advHIDArray = getAdvHIDArray();
                if (advHIDArray != null) {
                    if (isBB( hl7Generic ) && isAdvocateHID( advHIDArray, hl7Generic )) {
                        // Send to Midas
                        JMS_Midas.send( input );
                    }
                }
            }
        } catch ( Exception e ) {
            ErrorHandler.getInstance().handleError( input, JMS_Error, e, collabContext, logger, alerter, Constants.SEVERITY_FATAL );
        }
    }

    // Check if transaction is ORU
    private boolean isORU( com.stc.SeeBeyond.OTD_Library.HL7.Generic.HL7_GENERIC_EVT.GENERIC_EVT hl7 )
        throws Exception
    {
        String MSH9 = "";
        String MSH91 = "";
        String[] MSH9FieldArray;
        try {
            MSH9 = hl7.letMSH().letMsh9MessageType().getMSG().getN188MessageCode();
            MSH9FieldArray = MSH9.toString().split( COMP_SEP );
            if (MSH9FieldArray.length > 0) {
                MSH91 = MSH9FieldArray[0];
            }
            if (MSH91.equals( "ORU" )) {
                return true;
            } else {
                return false;
            }
        } catch ( Exception e ) {
            throw new ValidationException( HL7_PARSING_ERROR );
        }
    }

    // Get list of advocate HIDs stored in engine config file
    private String[] getAdvHIDArray()
        throws Exception
    {
        java.util.Properties prop = new java.util.Properties();
        java.io.FileInputStream inputFile = new java.io.FileInputStream( PROPERTIES );
        prop.load( inputFile );
        inputFile.close();
        if (prop.getProperty( "qualifyingHIDsForAMLToAdvocateMidas" ) != null) {
            return prop.getProperty( "qualifyingHIDsForAMLToAdvocateMidas" ).split( "," );
        } else {
            return null;
        }
    }

    // Check PID3.4 for one of the Advocate HIDs
    private boolean isAdvocateHID( String[] advocateHIDs, com.stc.SeeBeyond.OTD_Library.HL7.Generic.HL7_GENERIC_EVT.GENERIC_EVT hl7 )
        throws Exception
    {
        boolean isAdv = false;
        String PID = "";
        String PID3 = "";
        String PID34 = "";
        String[] PID3FieldArray;
        String[] PIDSegArray;
        try {
            if (hl7.hasSEG()) {
                for (int i = 0; i < hl7.countSEG(); i++) {
                    if (hl7.getSEG( i ).startsWith( "PID" )) {
                        PID = hl7.getSEG( i ).toString();
                        PIDSegArray = PID.toString().split( FIELD_SEP );
                        if (PIDSegArray.length > 3) {
                            PID3 = PIDSegArray[3];
                            PID3FieldArray = PID3.toString().split( COMP_SEP );
                            if (PID3FieldArray.length > 3) {
                                PID34 = PID3FieldArray[3];
                            }
                        }
                        break;
                    }
                }
            }
            for (int i = 0; i < advocateHIDs.length; i++) {
                if (advocateHIDs[i].equals( PID34 )) {
                    isAdv = true;
                    break;
                }
            }
            return isAdv;
        } catch ( Exception e ) {
            throw new ValidationException( HL7_PARSING_ERROR );
        }
    }

    // Check if OBR-24 is "BB"
    private boolean isBB( com.stc.SeeBeyond.OTD_Library.HL7.Generic.HL7_GENERIC_EVT.GENERIC_EVT hl7 )
        throws Exception
    {
        String OBR = "";
        String OBR24 = "";
        String[] OBRSegArray;
        try {
            if (hl7.hasSEG()) {
                for (int i = 0; i < hl7.countSEG(); i++) {
                    if (hl7.getSEG( i ).startsWith( "OBR" )) {
                        OBR = hl7.getSEG( i ).toString();
                        OBRSegArray = OBR.toString().split( FIELD_SEP );
                        if (OBRSegArray.length > 24) {
                            OBR24 = OBRSegArray[24];
                        }
                        break;
                    }
                }
            }
            if (OBR24.equals( "BB" )) {
                return true;
            } else {
                return false;
            }
        } catch ( Exception e ) {
            throw new ValidationException( HL7_PARSING_ERROR );
        }
    }

}
