/**
 * 
 */
package org.yamcs.xtce.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yamcs.utils.StringConverter;
import org.yamcs.xtce.AlarmLevels;
import org.yamcs.xtce.AlarmRanges;
import org.yamcs.xtce.BinaryDataEncoding;
import org.yamcs.xtce.BinaryParameterType;
import org.yamcs.xtce.BooleanParameterType;
import org.yamcs.xtce.Calibrator;
import org.yamcs.xtce.Comparison;
import org.yamcs.xtce.ComparisonList;
import org.yamcs.xtce.ContainerEntry;
import org.yamcs.xtce.DataEncoding;
import org.yamcs.xtce.DynamicIntegerValue;
import org.yamcs.xtce.EnumeratedParameterType;
import org.yamcs.xtce.EnumerationAlarm;
import org.yamcs.xtce.FixedIntegerValue;
import org.yamcs.xtce.FloatDataEncoding;
import org.yamcs.xtce.FloatParameterType;
import org.yamcs.xtce.FloatRange;
import org.yamcs.xtce.IntegerDataEncoding;
import org.yamcs.xtce.IntegerParameterType;
import org.yamcs.xtce.IntegerValue;
import org.yamcs.xtce.MatchCriteria;
import org.yamcs.xtce.NameDescription;
import org.yamcs.xtce.NameReference;
import org.yamcs.xtce.Repeat;
import org.yamcs.xtce.UnitType;
import org.yamcs.xtce.NameReference.ResolvedAction;
import org.yamcs.xtce.NameReference.Type;
import org.yamcs.xtce.NumericAlarm;
import org.yamcs.xtce.NumericContextAlarm;
import org.yamcs.xtce.OperatorType;
import org.yamcs.xtce.Parameter;
import org.yamcs.xtce.ParameterEntry;
import org.yamcs.xtce.ParameterInstanceRef;
import org.yamcs.xtce.ParameterType;
import org.yamcs.xtce.PolynomialCalibrator;
import org.yamcs.xtce.SequenceContainer;
import org.yamcs.xtce.SequenceEntry;
import org.yamcs.xtce.SpaceSystem;
import org.yamcs.xtce.SplineCalibrator;
import org.yamcs.xtce.SplinePoint;
import org.yamcs.xtce.StringDataEncoding;
import org.yamcs.xtce.StringDataEncoding.SizeType;

import io.netty.handler.codec.FixedLengthFrameDecoder;

import org.yamcs.xtce.StringParameterType;
import org.yamcs.xtce.ValueEnumerationRange;
import org.yamcs.xtce.Header;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;


/**
 * This class reads the XTCE XML files. XML document is accessed with the use of
 * the Stax Iterator API.
 * 
 * @author mu
 * 
 */
public class XtceStaxReader {

    // XTCE Schema defined tags, to minimize the mistyping errors
    private static final String  XTCE_SpaceSystem               = "SpaceSystem";
    private static final String  XTCE_AliasSet                  = "AliasSet";
    private static final String  XTCE_Alias                     = "Alias";
    private static final String  XTCE_LongDescription           = "LongDescription";
    private static final String  XTCE_Header                    = "Header";

    private static final String  XTCE_AuthorSet                 = "AuthorSet";
    private static final String  XTCE_NoteSet                   = "NoteSet";
    private static final String  XTCE_HistorySet                = "HistorySet";

    private static final String  XTCE_TelemetryMetaData         = "TelemetryMetaData";
    private static final String  XTCE_ParameterTypeSet          = "ParameterTypeSet";
    private static final String  XTCE_BooleanParameterType      = "BooleanParameterType";
    private static final String  XTCE_EnumeratedParameterType   = "EnumeratedParameterType";
    private static final String  XTCE_EnumerationList           = "EnumerationList";
    private static final String  XTCE_Enumeration               = "Enumeration";
    private static final String  XTCE_RangeEnumeration          = "RangeEnumeration";
    private static final String  XTCE_IntegerParameterType      = "IntegerParameterType";
    private static final String  XTCE_StringParameterType       = "StringParameterType";
    private static final String  XTCE_BinaryParameterType       = "BinaryParameterType";
    private static final String  XTCE_FloatParameterType        = "FloatParameterType";
    private static final String  XTCE_RelativeTimeParameterType = "RelativeTimeParameterType";
    private static final String  XTCE_AbsoluteTimeParameterType = "AbsoluteTimeParameterType";
    private static final String  XTCE_ArrayParameterType        = "ArrayParameterType";
    private static final String  XTCE_AggregateParameterType    = "AggregateParameterType";

    private static final String  XTCE_ParameterSet              = "ParameterSet";
    private static final String  XTCE_Parameter                 = "Parameter";
    private static final String  XTCE_ParameterRef              = "ParameterRef";
    private static final String  XTCE_ParameterProperties       = "ParameterProperties";
    private static final String  XTCE_ValidityCondition         = "ValidityCondition";
    private static final String  XTCE_ComparisonList            = "ComparisonList";
    private static final String  XTCE_Comparison                = "Comparison";
    private static final String  XTCE_BooleanExpression         = "BooleanExpression";
    private static final String  XTCE_CustomAlgorithm           = "CustomAlgorithm";
    private static final String  XTCE_RestrictionCriteria       = "RestrictionCriteria";

    private static final String  XTCE_SystemName                = "SystemName";
    private static final String  XTCE_PhysicalAddressSet        = "PhysicalAddressSet";
    private static final String  XTCE_TimeAssociation           = "TimeAssociation";

    private static final String  XTCE_ContainerSet              = "ContainerSet";
    private static final String  XTCE_BaseContainer             = "BaseContainer";
    private static final String  XTCE_MessageSet                = "MessageSet";
    private static final String  XTCE_StreamSet                 = "StreamSet";
    private static final String  XTCE_AlgorithmSet              = "AlgorithmSet";

    private static final String  XTCE_CommandMetaData           = "CommandMetaData";
    private static final String  XTCE_SequenceContainer         = "SequenceContainer";
    private static final String  XTCE_EntryList                 = "EntryList";
    private static final String  XTCE_ParameterRefEntry         = "ParameterRefEntry";

    private static final String  XTCE_LocationInContainerInBits = "LocationInContainerInBits";
    private static final String  XTCE_RepeatEntry               = "RepeatEntry";
    private static final String  XTCE_IncludeCondition          = "IncludeCondition";

    private static final String  XTCE_ParameterSegmentRefEntry  = "ParameterSegmentRefEntry";
    private static final String  XTCE_ContainerRefEntry         = "ContainerRefEntry";
    private static final String  XTCE_ContainerSegmentRefEntry  = "ContainerSegmentRefEntry";
    private static final String  XTCE_StreamSegmentEntry        = "StreamSegmentEntry";
    private static final String  XTCE_IndirectParameterRefEntry = "IndirectParameterRefEntry";
    private static final String  XTCE_ArrayParameterRefEntry    = "ArrayParameterRefEntry";

    private static final String  XTCE_UnitSet                   = "UnitSet";
    private static final String  XTCE_Unit                      = "Unit";
    private static final String  XTCE_FloatDataEncoding         = "FloatDataEncoding";
    private static final String  XTCE_BinaryDataEncoding        = "BinaryDataEncoding";
    private static final String  XTCE_SizeInBits                = "SizeInBits";
    private static final String  XTCE_FixedValue                = "FixedValue";
    private static final String  XTCE_DynamicValue              = "DynamicValue";
    private static final String  XTCE_DiscreteLookupList        = "DiscreteLookupList";
    private static final String  XTCE_IntegerDataEncoding       = "IntegerDataEncoding";
    private static final String  XTCE_StringDataEncoding       = "StringDataEncoding";
    private static final String  XTCE_ContextAlarmList          = "ContextAlarmList";
    private static final String  XTCE_ContextAlarm              = "ContextAlarm";
    private static final String  XTCE_ContextMatch              = "ContextMatch";
    private static final String  XTCE_DefaultCalibrator         = "DefaultCalibrator";
    private static final String  XTCE_ContextCalibratorList     = "ContextCalibratorList";
    private static final String  XTCE_SplineCalibrator          = "SplineCalibrator";
    private static final String  XTCE_PolynomialCalibrator      = "PolynomialCalibrator";
    private static final String  XTCE_MathOperationCalibrator   = "MathOperationCalibrator";
    private static final String  XTCE_Term                      = "Term";
    private static final String  XTCE_SplinePoint               = "SplinePoint";
    private static final String  XTCE_Count                     = "Count";
    private static final String  XTCE_IntegerValue              = "IntegerValue";
    private static final String  XTCE_ParameterInstanceRef      = "ParameterInstanceRef";
    private static final String  XTCE_StaticAlarmRanges         = "StaticAlarmRanges";
    private static final String  XTCE_DefaultAlarm              = "DefaultAlarm";
    private static final String  XTCE_Fixed                     = "Fixed";
    private static final String  XTCE_TerminationChar           = "TerminationChar";
    private static final String  XTCE_LeadingSize               = "LeadingSize";
        
    /**
     * Logging subsystem
     */
    private static Logger                log                            = LoggerFactory.getLogger(XtceStaxReader.class);

    /**
     * XML Event reader
     */
    private XMLEventReader       xmlEventReader                 = null;

    /**
     * XML Event
     */
    private XMLEvent             xmlEvent                       = null;

    /**
     * Statistics about the skipped sections. (good for overview about unimplemented features)
     */
    private Map<String, Integer> xtceSkipStatistics             = new HashMap<String, Integer>();
    private Set<String> excludedContainers = new HashSet<String>();

    /**
     * Constructor
     */
    public XtceStaxReader() {
    }


    /**
     * Reading of the XML XTCE file
     * @param fileName 
     * 
     * @return returns the SpaceSystem read from the XML file
     * @throws XMLStreamException 
     * @throws IOException 
     * 
     */
    public SpaceSystem readXmlDocument(String fileName) throws XMLStreamException, IOException  {

        xmlEventReader = initEventReader(fileName);
        xmlEvent = null;
        SpaceSystem spaceSystem = null;
        while (true) {
            xmlEvent = xmlEventReader.nextEvent();
            int eventType = xmlEvent.getEventType();
            if (eventType == XMLStreamConstants.COMMENT) {
                continue;
            }
            
            if (eventType == XMLStreamConstants.START_DOCUMENT) {
                onStartDocument((StartDocument) xmlEvent);
            } else if (eventType == XMLStreamConstants.START_ELEMENT) {
                spaceSystem = readSpaceSystem();
            } else if (eventType == XMLStreamConstants.END_DOCUMENT) {
                onEndDocument();
                break;
            } else if (isStartElementWithName(XTCE_SpaceSystem)) {
                SpaceSystem ss = readSpaceSystem();
                spaceSystem.addSpaceSystem(ss);
            } else {
                // something went wrong, all options should be handled by the
                // upper if-branches
                // log.error("XML document parser error, unhandled event type."
                // + XMLStreamConstants.CHARACTERS);
                // throw new IllegalStateException();
                log.error("Unhandled event: {} ", xmlEvent);
            }

            if (xmlEventReader.peek() == null) {
                // assert false; document must be closed gracefully
                this.xmlEvent = null;
                this.xmlEventReader.close();
                this.xmlEventReader = null;
                throw new IllegalStateException("XML file parsing error");
            }
        }
        log.info("XTCE file parsing finished successfully");
        return spaceSystem;
    }

    /**
     * Method called on start document event. Currently just logs the
     * information contained in the xml preamble of the parsed file.
     * 
     * @param start
     *            Start document event object
     */
    private void onStartDocument(StartDocument start) {
        log.trace("XML version=\"" + start.getVersion() + "\" encoding: \""
                + start.getCharacterEncodingScheme() + "\"");
    }

    /**
     * Start of reading at the root of the document. According to the XTCE
     * schema the root element is &lt;SpaceSystem&gt;
     * 
     * @throws XMLStreamException
     */
    private SpaceSystem readSpaceSystem() throws XMLStreamException {
        checkStartElementPreconditions();

        String value = readAttribute("name", xmlEvent.asStartElement());
        SpaceSystem spaceSystem = new SpaceSystem(value);

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_AliasSet)) {
                XtceAliasSet aliasSet=readXtceAliasSet();
                spaceSystem.setAliasSet(aliasSet);
            } else if (isStartElementWithName(XTCE_Header)) {
                readXtceHeader(spaceSystem);
            } else if (isStartElementWithName(XTCE_TelemetryMetaData)) {
                readXtceTelemetryMetaData(spaceSystem);
            } else if (isStartElementWithName(XTCE_CommandMetaData)) {
                readXtceCommandMetaData(spaceSystem);
            } else if (isStartElementWithName(XTCE_SpaceSystem)) {
                SpaceSystem ss= readSpaceSystem();
                spaceSystem.addSpaceSystem(ss);
            } else if (isEndElementWithName(XTCE_SpaceSystem)) {
                return spaceSystem;
            }
        }
    }

    /**
     * Action taken on end of the document event
     */
    private void onEndDocument() {
        try {
            log.trace("End of XML document");
            if (xmlEventReader != null) {
                xmlEventReader.close();
            }
        } catch (XMLStreamException e) {
            // exception thrown by the close method
            e.printStackTrace();
        } finally {
            xmlEventReader = null;
        }
    }

    /**
     * Extraction of the AliasSet section Current implementation does nothing,
     * just skips whole section
     * 
     * @return Set of aliases defined for the object
     * @throws XMLStreamException
     */
    private XtceAliasSet readXtceAliasSet() throws XMLStreamException {
        log.trace(XTCE_AliasSet);
        checkStartElementPreconditions();

        XtceAliasSet xtceAliasSet = new XtceAliasSet();

        while (true) {

            xmlEvent = xmlEventReader.nextEvent();

            // <Alias> sections
            if (isStartElementWithName(XTCE_Alias)) {
                readXtceAlias(xtceAliasSet);
            } else if (isEndElementWithName(XTCE_AliasSet)) {
                return xtceAliasSet;
            }
        }
    }

    /**
     * Extraction of the AliasSet section Current implementation does nothing,
     * just skips whole section
     * 
     * @throws XMLStreamException
     */
    private void readXtceAlias(XtceAliasSet aliasSet) throws XMLStreamException {
        log.trace(XTCE_Alias);
        checkStartElementPreconditions();

        String nameSpace = readAttribute("nameSpace", xmlEvent.asStartElement());
        if (nameSpace == null) {
            throw new XMLStreamException("Namespace attribute is missing");
        }
        nameSpace = nameSpace.intern();
        String alias = readAttribute("alias", xmlEvent.asStartElement());
        if (alias == null) {
            throw new XMLStreamException("Alias attribute is missing");
        }

        aliasSet.addAlias(nameSpace, alias);

        // read end element
        xmlEvent = xmlEventReader.nextEvent();
        if (!isEndElementWithName(XTCE_Alias)) {
            throw new IllegalStateException(XTCE_Alias + " end element expected");
        }
    }

    /**
     * Extraction of the Header section Current implementation does nothing,
     * just skips whole section
     * @param spaceSystem 
     * 
     * @throws XMLStreamException
     */
    private void readXtceHeader(SpaceSystem spaceSystem) throws XMLStreamException {
        log.trace(XTCE_Header);
        checkStartElementPreconditions();
        Header h=new Header();

        String value = readAttribute("version", xmlEvent.asStartElement());
        h.setVersion(value);

        value = readAttribute("date", xmlEvent.asStartElement());
        h.setDate(value);
        spaceSystem.setHeader(h);

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_AuthorSet)) {
                skipXtceSection(XTCE_AuthorSet);
            } else if (isStartElementWithName(XTCE_NoteSet)) {
                skipXtceSection(XTCE_NoteSet);
            } else if (isStartElementWithName(XTCE_HistorySet)) {
                skipXtceSection(XTCE_HistorySet);
            } else if (isEndElementWithName(XTCE_Header)) {
                return;
            }
        }
    }

    /**
     * Extraction of the TelemetryMetaData section Current implementation does
     * nothing, just skips whole section
     * @param spaceSystem 
     * 
     * @throws XMLStreamException
     */
    private void readXtceTelemetryMetaData(SpaceSystem spaceSystem) throws XMLStreamException {
        log.trace(XTCE_TelemetryMetaData);
        checkStartElementPreconditions();

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_ParameterTypeSet)) {
                readXtceParameterTypeSet(spaceSystem);
            } else if (isStartElementWithName(XTCE_ParameterSet)) {
                readXtceParameterSet(spaceSystem);
            } else if (isStartElementWithName(XTCE_ContainerSet)) {
                readXtceContainerSet(spaceSystem); // the result is created by access to
                // member variable
            } else if (isStartElementWithName(XTCE_MessageSet)) {
                readXtceMessageSet();
            } else if (isStartElementWithName(XTCE_StreamSet)) {
                readXtceStreamSet();
            } else if (isStartElementWithName(XTCE_AlgorithmSet)) {
                readXtceAlgorithmSet(spaceSystem);
            } else if (isEndElementWithName(XTCE_TelemetryMetaData)) {
                return;
            }
        }
    }

    /**
     * @param spaceSystem 
     * @throws XMLStreamException
     */
    private void readXtceParameterTypeSet(SpaceSystem spaceSystem) throws XMLStreamException {
        log.trace(XTCE_ParameterTypeSet);
        checkStartElementPreconditions();

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();
            ParameterType parameterType = null;

            if (isStartElementWithName(XTCE_BooleanParameterType)) {
                parameterType = readXtceBooleanParameterType();
            } else if (isStartElementWithName(XTCE_EnumeratedParameterType)) {
                parameterType = readXtceEnumeratedParameterType();
            } else if (isStartElementWithName(XTCE_FloatParameterType)) {
                parameterType = readXtceFloatParameterType(spaceSystem);
            } else if (isStartElementWithName(XTCE_IntegerParameterType)) {
                parameterType = readXtceIntegerParameterType(spaceSystem);
            } else if (isStartElementWithName(XTCE_BinaryParameterType)) {
                parameterType = readXtceBinaryParameterType(spaceSystem);
            } else if (isStartElementWithName(XTCE_StringParameterType)) {
                parameterType = readXtceStringParameterType(spaceSystem);
            } else if (isStartElementWithName(XTCE_RelativeTimeParameterType)) {
                parameterType = readXtceRelativeTimeParameterType();
            } else if (isStartElementWithName(XTCE_AbsoluteTimeParameterType)) {
                parameterType = readXtceAbsoluteTimeParameterType();
            } else if (isStartElementWithName(XTCE_ArrayParameterType)) {
                parameterType = readXtceArrayParameterType();
            } else if (isStartElementWithName(XTCE_AggregateParameterType)) {
                parameterType = readXtceAggregateParameterType();
            }

            if (parameterType != null) {
                spaceSystem.addParameterType(parameterType);
            }

            if (isEndElementWithName(XTCE_ParameterTypeSet)) {
                return;
            }
        }
    }

    private BooleanParameterType readXtceBooleanParameterType() throws IllegalStateException, XMLStreamException {
        log.trace(XTCE_BooleanParameterType);
        checkStartElementPreconditions();

        // name attribute
        BooleanParameterType boolParamType = null;
        String name = readAttribute("name", xmlEvent.asStartElement());
        if (name != null) {
            boolParamType = new BooleanParameterType(name);
        } else {
            throw new XMLStreamException("Unnamed boolean parameter type");
        }

        // read all parameters

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();
            if (isStartElementWithName(XTCE_UnitSet)) {
                boolParamType.addAllUnits(readXtceUnitSet());               
            } else if (isStartElementWithName(XTCE_IntegerDataEncoding)) {
                boolParamType.setEncoding(readXtceIntegerDataEncoding());
            } else if (isEndElementWithName(XTCE_BooleanParameterType)) {
                return boolParamType;
            }
        }
    }

    private ParameterType readXtceAggregateParameterType() throws IllegalStateException,
    XMLStreamException {
        skipXtceSection(XTCE_AggregateParameterType);
        return null;
    }

    private ParameterType readXtceArrayParameterType() throws IllegalStateException,
    XMLStreamException {
        skipXtceSection(XTCE_ArrayParameterType);
        return null;
    }

    private ParameterType readXtceAbsoluteTimeParameterType() throws IllegalStateException,
    XMLStreamException {
        skipXtceSection(XTCE_AbsoluteTimeParameterType);
        return null;
    }

    private ParameterType readXtceRelativeTimeParameterType() throws IllegalStateException,
    XMLStreamException {
        skipXtceSection(XTCE_RelativeTimeParameterType);
        return null;
    }

    private FloatParameterType readXtceFloatParameterType(SpaceSystem spaceSystem) throws IllegalStateException,  XMLStreamException {
        FloatParameterType floatParamType = null;

        
        log.trace(XTCE_FloatParameterType);
        checkStartElementPreconditions();

        // name attribute
        String value = readAttribute("name", xmlEvent.asStartElement());
        if (value != null) {
            floatParamType = new FloatParameterType(value);
        } else {
            throw new XMLStreamException("Unnamed float parameter type");
        }

        value = readAttribute("sizeInBits", xmlEvent.asStartElement());
        
        if (value != null) {
            int sizeInBits = Integer.parseInt(value);
            if(sizeInBits!=32 && sizeInBits!=64){
                throw new XMLStreamException("Float encoding "+sizeInBits+" not supported; Only 32 and 64 bits are supported");
            }
            floatParamType.setSizeInBits(sizeInBits);
        } 
        
        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_UnitSet)) {
                floatParamType.addAllUnits(readXtceUnitSet());
            } else if (isStartElementWithName(XTCE_IntegerDataEncoding)) {
                floatParamType.setEncoding(readXtceIntegerDataEncoding());
            } else if (isStartElementWithName(XTCE_FloatDataEncoding)) {
                floatParamType.setEncoding(readXtceFloatDataEncoding());
            } else if (isStartElementWithName(XTCE_DefaultAlarm)) {
                floatParamType.setDefaultAlarm(readDefaultAlarm());
            } else if (isStartElementWithName(XTCE_ContextAlarmList)) {
                floatParamType.setContextAlarmList(readNumericContextAlarmList(spaceSystem));                
            } else if (isEndElementWithName(XTCE_FloatParameterType)) {
                return floatParamType;
            }
        }
    }

    private FloatDataEncoding readXtceFloatDataEncoding() throws XMLStreamException {
        log.trace(XTCE_FloatDataEncoding);
        checkStartElementPreconditions();

        FloatDataEncoding floatDataEncoding = null;
        String name = "";

        // sizeInBits attribute
        String value = readAttribute("sizeInBits", xmlEvent.asStartElement());
        if (value != null) {
            floatDataEncoding = new FloatDataEncoding(Integer.parseInt(value));
        } else {
            // default value is 32
            floatDataEncoding = new FloatDataEncoding(32);
        }

        // encoding attribute
        value = readAttribute("encoding", xmlEvent.asStartElement());
        if (value != null) {
            if ("IEEE754_1985".equalsIgnoreCase(value)) {
                // ok, this encoding is supported by the class implicitly
            } else if ("MILSTD_1750A".equalsIgnoreCase(value)) {
                log.error("Encoding MILSTD_1750A is not currently supported.");
                throw new XMLStreamException("Encoding MILSTD_1750A is not currently supported.");
            } else {
                throw new XMLStreamException();
            }
        } else {
            // default is IEEE754_1985
            // this encoding is supported by the class implicitly
        }

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_DefaultCalibrator)) {
                floatDataEncoding.setDefaultCalibrator(readDefaultCalibrator());
            } else if (isStartElementWithName(XTCE_ContextCalibratorList)) {
                skipXtceSection(XTCE_ContextCalibratorList);
            } else if (isEndElementWithName(XTCE_FloatDataEncoding)) {
                return floatDataEncoding;
            }
        }
    }
        
    private List<NumericContextAlarm> readNumericContextAlarmList(SpaceSystem spaceSystem) throws IllegalStateException, XMLStreamException  {
        log.trace(XTCE_ContextAlarmList);
        List<NumericContextAlarm> contextAlarmList = new ArrayList<NumericContextAlarm>();
        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_ContextAlarm)) {
                contextAlarmList.add(readNumericContextAlarm(spaceSystem));
            } else if (isEndElementWithName(XTCE_ContextAlarmList)) {
                return contextAlarmList;
            }
        }
    }
    
    private NumericAlarm readDefaultAlarm() throws XMLStreamException {
        NumericAlarm na = new NumericAlarm();
        readAlarmAttributes(na);
        
        while (true) {
            xmlEvent = xmlEventReader.nextEvent();            
            if (isEndElementWithName(XTCE_DefaultAlarm)) {
                return na;
            } else if(xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT){
                readNumericAlarmElement(na);
            }
        }
    }
    private NumericContextAlarm readNumericContextAlarm(SpaceSystem spaceSystem) throws XMLStreamException {
        log.trace(XTCE_ContextAlarm);
        NumericContextAlarm nca = new NumericContextAlarm();
        readAlarmAttributes(nca);
        
        while (true) {
            xmlEvent = xmlEventReader.nextEvent();            
            if (isStartElementWithName(XTCE_ContextMatch)) {
                nca.setContextMatch(readMatchCriteria(spaceSystem));
            } else if (isEndElementWithName(XTCE_ContextAlarm)) {
                return nca;
            } else if(xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT){
                readNumericAlarmElement(nca);
            }
        }
    }
    
    private void readNumericAlarmElement(NumericAlarm numericAlarm) throws XMLStreamException {
        if (isStartElementWithName(XTCE_StaticAlarmRanges)) {
            numericAlarm.setStaticAlarmRanges(readAlarmRanges());
        }
    }
    
    private AlarmRanges readAlarmRanges() throws XMLStreamException {
        String tag = xmlEvent.asStartElement().getName().getLocalPart();
        AlarmRanges ar = new AlarmRanges();
        while (true) {
            xmlEvent = xmlEventReader.nextEvent();
            if (isStartElementWithName("WatchRange")) {
                ar.addWatchRange(readFloatRange());
            }  else if (isStartElementWithName("WarningRange")) {
                ar.addWarningRange(readFloatRange());
            } else if (isStartElementWithName("DistressRange")) {
                ar.addDistressRange(readFloatRange());
            } else if (isStartElementWithName("CriticalRange")) {
                ar.addCriticalRange(readFloatRange());
            } else if (isStartElementWithName("SevereRange")) {
                ar.addSevereRange(readFloatRange());
            } else if (isEndElementWithName(tag)) {
                return ar;
            }
        }
    }


    private FloatRange readFloatRange() {
        StartElement e = xmlEvent.asStartElement();
        double minInclusive = Double.MIN_VALUE;
        double maxInclusive = Double.MAX_VALUE;
        
        String value = readAttribute("minInclusive", e);        
        if (value != null) {
            minInclusive = Double.parseDouble(value);            
        }
        value = readAttribute("maxInclusive", e);        
        if (value != null) {
            maxInclusive = Double.parseDouble(value);            
        }
        return new FloatRange(minInclusive, maxInclusive);
    }


    private void readAlarmAttributes(NumericAlarm numericAlarm) {
        String value = readAttribute("minViolations", xmlEvent.asStartElement());
        if (value != null) {
            int minViolations = Integer.parseInt(value);
            numericAlarm.setMinViolations(minViolations);
        }
    }
    
    
    private BinaryParameterType readXtceBinaryParameterType(SpaceSystem spaceSystem) throws IllegalStateException, XMLStreamException {
        log.trace(XTCE_BinaryParameterType);
        checkStartElementPreconditions();

        // name attribute
        BinaryParameterType binaryParamType = null;
        String name = readAttribute("name", xmlEvent.asStartElement());
        if (name != null) {
            binaryParamType = new BinaryParameterType(name);
        } else {
            throw new XMLStreamException("Unnamed binary parameter type");
        }

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();
            if (isStartElementWithName(XTCE_UnitSet)) {
                binaryParamType.addAllUnits(readXtceUnitSet());
            } else if (isStartElementWithName(XTCE_IntegerDataEncoding)) {
                binaryParamType.setEncoding(readXtceIntegerDataEncoding());
            } else if (isStartElementWithName(XTCE_BinaryDataEncoding)) {
                binaryParamType.setEncoding(readXtceBinaryDataEncoding(spaceSystem));
            } else if (isEndElementWithName(XTCE_BinaryParameterType)) {
                return binaryParamType;
            }
        }
    }

    private DataEncoding readXtceBinaryDataEncoding(SpaceSystem spaceSystem) throws XMLStreamException {
        log.trace(XTCE_BinaryDataEncoding);
        checkStartElementPreconditions();

        BinaryDataEncoding binaryDataEncoding = null;
        String name = "";

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_SizeInBits)) {
                IntegerValue v = readXtceIntegerValue(spaceSystem);
                if(v instanceof FixedIntegerValue) {
                    binaryDataEncoding = new BinaryDataEncoding(name, (int)((FixedIntegerValue) v).getValue());
                } else {
                    throwException("Only FixedIntegerValue supported for sizeInBits");
                }
            } else if (isStartElementWithName("FromBinaryTransformAlgorithm")) {
                skipXtceSection("FromBinaryTransformAlgorithm");
            } else if (isStartElementWithName("ToBinaryTransformAlgorithm")) {
                skipXtceSection("ToBinaryTransformAlgorithm");
            } else if (isEndElementWithName(XTCE_BinaryDataEncoding)) {
                return binaryDataEncoding;
            }
        }
    }

    private void throwException(String msg) throws XMLStreamException {
        throw new XMLStreamException(msg+" at "+xmlEvent.getLocation().getLineNumber()+": "+xmlEvent.getLocation().getColumnNumber());
    }
  

    private int readIntegerValue() throws XMLStreamException {
        checkStartElementPreconditions();
        String tag = xmlEvent.asStartElement().getName().getLocalPart();

        int sizeInBits = 0;

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();
           
            if (xmlEvent.isCharacters()) {
                sizeInBits = getIntegerCharacter();
            } else if (isEndElementWithName(tag)) {
                return sizeInBits;
            }
        }
    }
    int getIntegerCharacter() throws XMLStreamException { 
        if (xmlEvent.isCharacters()) {
            String value = xmlEvent.asCharacters().getData();
            try {
                return Integer.parseInt(value);
            } catch(NumberFormatException e) {
                throw new XMLStreamException("Cannot parse integer '"+value+"' at "+xmlEvent.getLocation().getLineNumber()+":"+xmlEvent.getLocation().getColumnNumber());
            }
        } else {
            throw new IllegalStateException();
        }
    }
    private StringParameterType readXtceStringParameterType(SpaceSystem spaceSystem) throws IllegalStateException, XMLStreamException {
        log.trace(XTCE_IntegerParameterType);
        checkStartElementPreconditions();
        StringParameterType stringParamType = null;
        
        // name attribute
        String value = readAttribute("name", xmlEvent.asStartElement());
        if (value != null) {
            stringParamType = new StringParameterType(value);
        } else {
            throw new XMLStreamException("Unnamed string parameter type");
        }

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_UnitSet)) {
                stringParamType.addAllUnits(readXtceUnitSet());
            } else if (isStartElementWithName(XTCE_StringDataEncoding)) {
                stringParamType.setEncoding(readXtceStringDataEncoding(spaceSystem));
            } else if (isStartElementWithName(XTCE_ContextAlarmList)) {
                skipXtceSection(XTCE_ContextAlarmList);
            } else if (isEndElementWithName(XTCE_StringParameterType)) {
                return stringParamType;
            }
        }
    }
    

    private StringDataEncoding readXtceStringDataEncoding(SpaceSystem spaceSystem) throws XMLStreamException {
        checkStartElementPreconditions();

        StringDataEncoding stringDataEncoding = new StringDataEncoding();;
      
        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_SizeInBits)) {
                readStringSizeInBits(spaceSystem, stringDataEncoding);
            } else if (isEndElementWithName(XTCE_StringDataEncoding)) {
                return stringDataEncoding;
            }
        }
    }


    private void readStringSizeInBits(SpaceSystem spaceSystem, StringDataEncoding stringDataEncoding) throws XMLStreamException {
        checkStartElementPreconditions();
        while (true) {
            xmlEvent = xmlEventReader.nextEvent();
            if (isStartElementWithName(XTCE_Fixed)) {
                IntegerValue v = readXtceIntegerValue(spaceSystem);
                if(v instanceof FixedIntegerValue) {
                    stringDataEncoding.setSizeType(SizeType.Fixed);
                    stringDataEncoding.setSizeInBits((int)((FixedIntegerValue) v).getValue());
                } else {
                    throwException("Only FixedValue supported for string size in bits");
                }
             } if (isStartElementWithName(XTCE_TerminationChar)) {
                stringDataEncoding.setSizeType(SizeType.TerminationChar);
                byte[] x = readHexBinary();
                if(x==null || x.length!=1) throw new XMLStreamException("Terminated strings have to have the size of the termination character of 1");
                stringDataEncoding.setTerminationChar(x[0]);
            } else if (isStartElementWithName(XTCE_LeadingSize)) {
                stringDataEncoding.setSizeType(SizeType.LeadingSize);
                String value = readAttribute("sizeInBitsOfSizeTag", xmlEvent.asStartElement());
                stringDataEncoding.setSizeInBitsOfSizeTag(Integer.valueOf(value));
            } else if (isEndElementWithName(XTCE_SizeInBits)) {
                return;
            }
        }
    }


    private byte[] readHexBinary() throws XMLStreamException {
        checkStartElementPreconditions();
        String tag = xmlEvent.asStartElement().getName().getLocalPart();
        byte[] b=null;

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();
            if (xmlEvent.isCharacters()) {
                b = StringConverter.hexStringToArray(xmlEvent.asCharacters().getData());
            } else if (isEndElementWithName(tag)) {
                return b;
            }
        }
    }


    private IntegerParameterType readXtceIntegerParameterType(SpaceSystem spaceSystem) throws IllegalStateException, XMLStreamException {
        IntegerParameterType integerParamType = null;

        log.trace(XTCE_IntegerParameterType);
        checkStartElementPreconditions();

        // name attribute
        String value = readAttribute("name", xmlEvent.asStartElement());
        if (value != null) {
            integerParamType = new IntegerParameterType(value);
        } else {
            throw new XMLStreamException("Unnamed integer parameter type");
        }

        value = readAttribute("sizeInBits", xmlEvent.asStartElement());
        if (value != null) {
            int sizeInBits = Integer.parseInt(value);
            integerParamType.setSizeInBits(sizeInBits);
        }
        value = readAttribute("signed", xmlEvent.asStartElement());
        if (value != null) {
            boolean signed = Boolean.parseBoolean(value);
            integerParamType. setSigned(signed);
        }

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_UnitSet)) {
                integerParamType.addAllUnits(readXtceUnitSet());
            } else if (isStartElementWithName(XTCE_IntegerDataEncoding)) {
                integerParamType.setEncoding(readXtceIntegerDataEncoding());
            } else if (isStartElementWithName(XTCE_ContextAlarmList)) {
                integerParamType.setContextAlarmList(readNumericContextAlarmList(spaceSystem));
            } else if (isEndElementWithName(XTCE_IntegerParameterType)) {
                return integerParamType;
            }
        }
    }

    private IntegerDataEncoding readXtceIntegerDataEncoding() throws IllegalStateException,
    XMLStreamException {
        log.trace(XTCE_IntegerDataEncoding);
        checkStartElementPreconditions();

        IntegerDataEncoding integerDataEncoding = null;
        String name = "";

        // sizeInBits attribute
        String value = readAttribute("sizeInBits", xmlEvent.asStartElement());
        if (value != null) {
            integerDataEncoding = new IntegerDataEncoding(Integer.parseInt(value));
        } else {
            // default value is 8
            integerDataEncoding = new IntegerDataEncoding(8);
        }

        // encoding attribute
        value = readAttribute("encoding", xmlEvent.asStartElement());
        if (value != null) {
            if ("unsigned".equalsIgnoreCase(value)) {
                integerDataEncoding.setEncoding(IntegerDataEncoding.Encoding.unsigned);
            } else if ("signMagnitude".equalsIgnoreCase(value)) {
                integerDataEncoding.setEncoding(IntegerDataEncoding.Encoding.signMagnitude);
            } else if ("twosComplement".equalsIgnoreCase(value)) {
                integerDataEncoding.setEncoding(IntegerDataEncoding.Encoding.twosComplement);
            } else if ("onesComplement".equalsIgnoreCase(value)) {
                integerDataEncoding.setEncoding(IntegerDataEncoding.Encoding.onesComplement);
            } else if ("BCD".equalsIgnoreCase(value)) {
                integerDataEncoding.setEncoding(IntegerDataEncoding.Encoding.BCD);
            } else if ("packedBCD".equalsIgnoreCase(value)) {
                integerDataEncoding.setEncoding(IntegerDataEncoding.Encoding.packedBCD);
            } else if ("twosCompliment".equalsIgnoreCase(value)) { //this is for compatibility with CD-MCS/CGS SCOE XML exporter
                integerDataEncoding.setEncoding(IntegerDataEncoding.Encoding.twosComplement);
            } else {
                throwException("Unsupported encoding '"+value+"'");
            }
        } else {
            // default is unsigned
            integerDataEncoding.setEncoding(IntegerDataEncoding.Encoding.unsigned);
        }

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_DefaultCalibrator)) {
                integerDataEncoding.setDefaultCalibrator(readDefaultCalibrator());
            } else if (isStartElementWithName(XTCE_ContextCalibratorList)) {
                skipXtceSection(XTCE_ContextCalibratorList);
            } else if (isEndElementWithName(XTCE_IntegerDataEncoding)) {
                return integerDataEncoding;
            }
        }
    }

    private Calibrator readDefaultCalibrator() throws IllegalStateException, XMLStreamException {
        log.trace(XTCE_DefaultCalibrator);
        checkStartElementPreconditions();

        Calibrator calibrator = null;

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_PolynomialCalibrator)) {
                calibrator = readXtcePolynomialCalibrator();
            } else if (isStartElementWithName(XTCE_MathOperationCalibrator)) {
                skipXtceSection(XTCE_MathOperationCalibrator);
            } else if (isStartElementWithName(XTCE_SplineCalibrator)) {
                calibrator = readXtceSplineCalibrator();
            } else if (isEndElementWithName(XTCE_DefaultCalibrator)) {
                return calibrator;
            }
        }
    }



    /**
     * Instantiate the SplineCalibrator element.
     * @return
     * @throws XMLStreamException
     */
    private Calibrator readXtceSplineCalibrator() throws XMLStreamException {
        log.trace(XTCE_SplineCalibrator);
        checkStartElementPreconditions();

        ArrayList<SplinePoint> splinePoints = new ArrayList<SplinePoint>();

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_SplinePoint)) {
                splinePoints.add(readXtceSplinePoint());
            } else if (isEndElementWithName(XTCE_SplineCalibrator)) {
                return new SplineCalibrator(splinePoints);
            }
        }
    }

    /**
     * Instantiate SplinePoint element.
     * This element has two required attributes: raw, calibrated
     * @return
     * @throws XMLStreamException 
     */
    private SplinePoint readXtceSplinePoint() throws XMLStreamException {
        log.trace(XTCE_SplinePoint);
        checkStartElementPreconditions();

        double raw = 0.0d;
        double calibrated = 0.0d;

        String attributeValue = readAttribute("raw", xmlEvent.asStartElement());
        if (attributeValue != null) {
            raw = Double.parseDouble(attributeValue);
        } else {
            throw new XMLStreamException();
        }

        attributeValue = readAttribute("calibrated", xmlEvent.asStartElement());
        if (attributeValue != null) {
            calibrated = Double.parseDouble(attributeValue);
        } else {
            throw new XMLStreamException();
        }

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isEndElementWithName(XTCE_SplinePoint)) {
                return new SplinePoint(raw, calibrated);
            }
        }
    }

    private Calibrator readXtcePolynomialCalibrator() throws XMLStreamException {
        log.trace(XTCE_PolynomialCalibrator);
        checkStartElementPreconditions();

        int maxExponent = 0;
        HashMap<Integer, Double> polynome = new HashMap<Integer, Double>();

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_Term)) {
                XtceTerm term = readXtceTerm();
                if (term.getExponent() > maxExponent) {
                    maxExponent = term.getExponent();
                }
                polynome.put(term.getExponent(), term.getCoefficient());
            } else if (isEndElementWithName(XTCE_PolynomialCalibrator)) {
                double[] coefficients = new double[maxExponent + 1];
                for (Map.Entry<Integer, Double> entry:polynome.entrySet()) {
                    coefficients[entry.getKey()] = entry.getValue();
                }
                return new PolynomialCalibrator(coefficients);
            }
        }
    }

    private XtceTerm readXtceTerm() throws XMLStreamException {
        log.trace(XTCE_Term);
        checkStartElementPreconditions();

        int exponent = 0;
        double coefficient = 0.0;

        String value = readAttribute("coefficient", xmlEvent.asStartElement());
        if (value != null) {
            coefficient = Double.parseDouble(value);
        } else {
            throw new XMLStreamException();
        }

        value = readAttribute("exponent", xmlEvent.asStartElement());
        if (value != null) {
            exponent = (int) Double.parseDouble(value);
        } else {
            throw new XMLStreamException();
        }

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isEndElementWithName(XTCE_Term)) {
                return new XtceTerm(exponent, coefficient);
            }
        }
    }

    private List<UnitType> readXtceUnitSet() throws IllegalStateException, XMLStreamException {
        log.trace(XTCE_UnitSet);

        List<UnitType> units = new ArrayList<UnitType>();
        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_Unit)) {
                UnitType u = readXtceUnit();
                units.add(u);
            } else if (isEndElementWithName(XTCE_UnitSet)) {
                return units;
            }
        }
    }


    private UnitType readXtceUnit() throws XMLStreamException {
        log.trace(XTCE_Unit);
        checkStartElementPreconditions();


        StartElement element = xmlEvent.asStartElement();

        String powerValue = readAttribute("power", element);
        String factorValue = readAttribute("factor", element);
        String descriptionValue = readAttribute("description", element);
        String unit;


        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (xmlEvent.isCharacters()) {
                unit = xmlEvent.asCharacters().getData();
                break;
            } else if (isEndElementWithName(XTCE_Unit)) {
                return null;
            }
        }

        UnitType unitType = new UnitType(unit);
        if ( powerValue != null ) {
            unitType.setPower( Double.parseDouble(powerValue));
        }
        if ( factorValue != null ) {
            unitType.setFactor( factorValue );
        }
        if ( descriptionValue != null ) {
            unitType.setDescription( descriptionValue );
        }

        return unitType;
    }

    private EnumeratedParameterType readXtceEnumeratedParameterType() throws IllegalStateException, XMLStreamException {
        EnumeratedParameterType enumParamType = null;

        log.trace(XTCE_EnumeratedParameterType);
        checkStartElementPreconditions();

        // name attribute
        String value = readAttribute("name", xmlEvent.asStartElement());
        if (value != null) {
            enumParamType = new EnumeratedParameterType(value);
        } else {
            throw new XMLStreamException();
        }

        // defaultValue attribute
        value = readAttribute("defaultValue", xmlEvent.asStartElement());
        if (value != null) {
            enumParamType.setInitialValue(value);
        }

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_UnitSet)) {
                enumParamType.addAllUnits(readXtceUnitSet());               
            } else if (isStartElementWithName(XTCE_IntegerDataEncoding)) {
                enumParamType.setEncoding(readXtceIntegerDataEncoding());
            } else if (isStartElementWithName(XTCE_EnumerationList)) {
                readXtceEnumerationList(enumParamType);
            } else if (isStartElementWithName(XTCE_DefaultAlarm)) {
                enumParamType.setDefaultAlarm(readXtceEnumerationAlarm(enumParamType));
            } else if (isEndElementWithName(XTCE_EnumeratedParameterType)) {
                return enumParamType;
            }
        }
    }

    private void readXtceEnumerationList(EnumeratedParameterType enumParamType) throws XMLStreamException {
        log.trace(XTCE_EnumerationList);
        checkStartElementPreconditions();

        // initialValue attribute
        String initialValue = readAttribute("initialValue", xmlEvent.asStartElement());
        if (initialValue != null) {
            enumParamType.setInitialValue(initialValue);
        }

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_Enumeration)) {
                readXtceEnumeration(enumParamType);
            } else if (isStartElementWithName(XTCE_RangeEnumeration)) {
                enumParamType.addEnumerationRange(readXtceRangeEnumeration());
            } else if (isEndElementWithName(XTCE_EnumerationList)) {
                return;
            }
        }
    }
    
    private EnumerationAlarm readXtceEnumerationAlarm(EnumeratedParameterType enumParamType) throws XMLStreamException {
        checkStartElementPreconditions();
        String tag = xmlEvent.asStartElement().getName().getLocalPart();
        EnumerationAlarm alarm = new EnumerationAlarm();
        
        // initialValue attribute
        String v = readAttribute("minViolations", xmlEvent.asStartElement());
        if (v != null) {
            alarm.setMinViolations(Integer.parseInt(v));
        }

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName("EnumerationAlarm")) {
                String label = readAttribute("enumerationLabel", xmlEvent.asStartElement());
                 
                if(!enumParamType.hasLabel(label)) {
                    throw new XMLStreamException("Reference to invalid enumeration label '"+label+"'");
                }
                AlarmLevels level = getAlarmLevel(readAttribute("alarmLevel", xmlEvent.asStartElement()));
                alarm.addAlarm(label, level);
            } else if (isEndElementWithName(tag)) {
                return alarm;
            }
        }
    }
    
    private AlarmLevels getAlarmLevel(String l) throws XMLStreamException {
        try {
            return AlarmLevels.valueOf(l.toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new XMLStreamException("Invalid alarm level '"+l+"'; use one of: "+AlarmLevels.values());
        }
    }
    

    private ValueEnumerationRange readXtceRangeEnumeration() throws XMLStreamException {
        log.trace(XTCE_RangeEnumeration);
        checkStartElementPreconditions();

        boolean isMinInclusive = true;
        boolean isMaxInclusive = true;
        double min = 0.0;
        double max = 0.0;

        String value = readAttribute("maxInclusive", xmlEvent.asStartElement());
        if (value != null) {
            max = Double.parseDouble(value);
            isMaxInclusive = true;
        }

        value = readAttribute("minInclusive", xmlEvent.asStartElement());
        if (value != null) {
            isMinInclusive = true;
            min = Double.parseDouble(value);
        }

        value = readAttribute("maxExclusive", xmlEvent.asStartElement());
        if (value != null) {
            max = Double.parseDouble(value);
            isMaxInclusive = false;
        }

        value = readAttribute("minExclusive", xmlEvent.asStartElement());
        if (value != null) {
            isMinInclusive = false;
            min = Double.parseDouble(value);
        }

        value = readAttribute("label", xmlEvent.asStartElement());
        if (value == null) {
            log.error("Attribute label is missing.");
            value = "UNDEF";
        }

        ValueEnumerationRange range = new ValueEnumerationRange(min, max, isMinInclusive,
                isMaxInclusive, value);

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isEndElementWithName(XTCE_RangeEnumeration)) {
                return range;
            }
        }
    }

    private void readXtceEnumeration(EnumeratedParameterType enumParamType)
            throws XMLStreamException {
        log.trace(XTCE_Enumeration);
        checkStartElementPreconditions();

        long longValue = 0;
        String value = readAttribute("value", xmlEvent.asStartElement());
        if (value != null) {
            longValue = Long.parseLong(value);
        } else {
            throw new XMLStreamException();
        }

        value = readAttribute("label", xmlEvent.asStartElement());
        if (value == null) {
            throw new XMLStreamException();
        }

        enumParamType.addEnumerationValue(longValue, value);

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();
            if (isEndElementWithName(XTCE_Enumeration)) {
                return;
            }
        }
    }

    /**
     * @param spaceSystem 
     * @todo Must be implemented to be able to read current XTCE files
     */
    private void readXtceParameterSet(SpaceSystem spaceSystem) throws IllegalStateException, XMLStreamException {
        log.trace(XTCE_ParameterSet);
        checkStartElementPreconditions();

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_Parameter)) {
                readXtceParameter(spaceSystem); // the parameter is registered inside
            } else if (isStartElementWithName(XTCE_ParameterRef)) {
                readXtceParameterRef();
            } else if (isEndElementWithName(XTCE_ParameterSet)) {
                return;
            }
        }
    }

    
    /**
     * @todo
     * @return
     * @throws IllegalStateException
     * @throws XMLStreamException
     */
    private XtceNotImplemented readXtceParameterRef() throws IllegalStateException, XMLStreamException {
        skipXtceSection(XTCE_ParameterRef);
        return null;
    }

    /**
     * 
     * @return Parameter instance
     * @throws IllegalStateException
     * @throws XMLStreamException
     */
    private Parameter readXtceParameter(SpaceSystem spaceSystem) throws IllegalStateException, XMLStreamException {
        log.trace(XTCE_Parameter);
        checkStartElementPreconditions();

        Parameter parameter = null;

        // name
        StartElement element = xmlEvent.asStartElement();
        String value = readAttribute("name", element);
        if (value != null) {
            parameter = new Parameter(value);
        } else {
            throw new XMLStreamException("Missing name for the parameter");
        }

        // parameterTypeRef
        value = readAttribute("parameterTypeRef", element);
        if (value != null) {
            ParameterType ptype = spaceSystem.getParameterType(value);
            if(ptype!=null) {
                parameter.setParameterType(ptype);
            } else {
                final Parameter p=parameter;
                NameReference nr=new NameReference(value, Type.PARAMETER_TYPE,
                        new ResolvedAction() {
                    @Override
                    public boolean resolved(NameDescription nd) {
                        p.setParameterType((ParameterType) nd);
                        return true;
                    }
                });
                spaceSystem.addUnresolvedReference(nr);
            }
        } else {
            throw new XMLStreamException("Cannot find parameterTypeRef in element: "+element);
        }

        // shortDescription
        value = readAttribute("shortDescription", element);
        parameter.setShortDescription(value);

        // register the parameter now, because parameter can refer to
        // self in the parameter properties
        spaceSystem.addParameter(parameter);

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_AliasSet)) {
                parameter.setAliasSet(readXtceAliasSet());
            } else if (isStartElementWithName(XTCE_ParameterProperties)) {
                readXtceParameterProperties(spaceSystem);
            } else if (isStartElementWithName(XTCE_LongDescription)) {
                parameter.setLongDescription(readXtceLongDescription(spaceSystem));
            } else if (isEndElementWithName(XTCE_Parameter)) {
                return parameter;
            }
        }
    }

    private XtceParameterProperties readXtceParameterProperties(SpaceSystem spaceSystem) throws XMLStreamException {
        log.trace(XTCE_ParameterProperties);
        checkStartElementPreconditions();

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            //MatchCriteria criteria = null;

            if (isStartElementWithName(XTCE_ValidityCondition)) {
                readMatchCriteria(spaceSystem);
            } else if (isStartElementWithName(XTCE_PhysicalAddressSet)) {
                skipXtceSection(XTCE_PhysicalAddressSet);
            } else if (isStartElementWithName(XTCE_SystemName)) {
                skipXtceSection(XTCE_SystemName);
            } else if (isStartElementWithName(XTCE_TimeAssociation)) {
                skipXtceSection(XTCE_TimeAssociation);
            } else if (isEndElementWithName(XTCE_ParameterProperties)) {
                return null;
            }
        }
    }

    private String readXtceLongDescription(SpaceSystem spaceSystem) throws XMLStreamException {
        checkStartElementPreconditions();

        StringBuilder longDescr = new StringBuilder();
        while(true) {
            xmlEvent = xmlEventReader.nextEvent();
            if(isEndElementWithName(XTCE_LongDescription)) break;
            if(!xmlEvent.isCharacters()) {
                throw new IllegalStateException(XTCE_LongDescription + " characters or end element expected but instead got "+xmlEvent);
            }
            longDescr.append(xmlEvent.asCharacters().getData());
        }

        return longDescr.toString();
    }

    private MatchCriteria readMatchCriteria(SpaceSystem spaceSystem) throws XMLStreamException {
        log.trace("MatchCriteria");
        checkStartElementPreconditions();
        String tag = xmlEvent.asStartElement().getName().getLocalPart();
        MatchCriteria criteria = null;

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();
            if (isStartElementWithName(XTCE_Comparison)) {
                criteria = readXtceComparison(spaceSystem);
            } else if (isStartElementWithName(XTCE_ComparisonList)) {
                criteria = readXtceComparisonList(spaceSystem);
            } else if (isStartElementWithName(XTCE_BooleanExpression)) {
                skipXtceSection(XTCE_BooleanExpression);
            } else if (isStartElementWithName(XTCE_CustomAlgorithm)) {
                skipXtceSection(XTCE_CustomAlgorithm);
            } else if (isEndElementWithName(tag)) {
                return criteria;
            }
        }
    }

    private ComparisonList readXtceComparisonList(SpaceSystem spaceSystem) throws XMLStreamException {
        log.trace(XTCE_ComparisonList);
        checkStartElementPreconditions();

        ComparisonList list = new ComparisonList();

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_Comparison)) {
                list.addComparison(readXtceComparison(spaceSystem));
            } else if (isEndElementWithName(XTCE_ComparisonList)) {
                return list;
            }
        }
    }

    /**
     * Reads the definition of the containers
     * @param spaceSystem 
     * 
     * @todo Must be implemented to be able to read current XTCE files
     * @return
     * @throws IllegalStateException
     * @throws XMLStreamException
     */
    private void readXtceContainerSet(SpaceSystem spaceSystem) throws IllegalStateException, XMLStreamException {
        log.trace(XTCE_ContainerSet);
        checkStartElementPreconditions();

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_SequenceContainer)) {
                SequenceContainer sc = readXtceSequenceContainer(spaceSystem);
                if(excludedContainers.contains(sc.getName())) {
                    log.debug("Not adding '"+sc.getName()+"' to the SpaceSystem because excluded by configuration");
                } else {
                    spaceSystem.addSequenceContainer(sc);
                }
                if((sc.getBaseContainer()==null) && (spaceSystem.getRootSequenceContainer()==null)) {
                    spaceSystem.setRootSequenceContainer(sc);
                }
            } else if (isEndElementWithName(XTCE_ContainerSet)) {
                return;
            }
        }
    }

    private SequenceContainer readXtceSequenceContainer(SpaceSystem spaceSystem) throws XMLStreamException {
        log.trace(XTCE_SequenceContainer);
        checkStartElementPreconditions();

        SequenceContainer seqContainer = null;

        String value = readAttribute("name", xmlEvent.asStartElement());
        if (value != null) {
            seqContainer = new SequenceContainer(value);
        } else {
            throw new XMLStreamException("Name is missing for container");
        }

        value = readAttribute("shortDescription", xmlEvent.asStartElement());
        if (value != null) {
            seqContainer.setShortDescription(value);
        }

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_AliasSet)) {
                seqContainer.setAliasSet(readXtceAliasSet());
            } else if (isStartElementWithName(XTCE_EntryList)) {
                readXtceEntryList(spaceSystem, seqContainer);
            } else if (isStartElementWithName(XTCE_BaseContainer)) {
                readXtceBaseContainer(spaceSystem, seqContainer);
            } else if (isStartElementWithName(XTCE_LongDescription)) {
                seqContainer.setLongDescription(readXtceLongDescription(spaceSystem));
            }  else if (isEndElementWithName(XTCE_SequenceContainer)) {
                return seqContainer;
            }
        }
    }

    private void readXtceBaseContainer(SpaceSystem spaceSystem, SequenceContainer seqContainer)  throws IllegalStateException, XMLStreamException {
        log.trace(XTCE_BaseContainer);
        checkStartElementPreconditions();

        String refName = readAttribute("containerRef", xmlEvent.asStartElement());
        if (refName != null) {
            if(excludedContainers.contains(refName)) {
                log.debug("adding "+seqContainer.getName()+" to the list of the excluded containers because its parent is excluded");
                excludedContainers.add(seqContainer.getName());
            } else {
                // find base container in the set of already defined containers
                SequenceContainer baseContainer = spaceSystem.getSequenceContainer(refName);
                if (baseContainer != null) {
                    seqContainer.setBaseContainer(baseContainer);
                } else { //must come from somwhere else
                    final SequenceContainer finalsc=seqContainer;
                    NameReference nr=new NameReference(refName, Type.SEQUENCE_CONTAINTER, new ResolvedAction() {
                        @Override
                        public boolean resolved(NameDescription nd) {
                            finalsc.setBaseContainer((SequenceContainer) nd);
                            return true;
                        }
                    });
                    spaceSystem.addUnresolvedReference(nr);
                }
            }
        } else {
            throw new XMLStreamException("Reference on base container is missing");
        }

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_RestrictionCriteria)) {
                MatchCriteria criteria = readMatchCriteria(spaceSystem);
                seqContainer.setRestrictionCriteria(criteria);
            } else if (isEndElementWithName(XTCE_BaseContainer)) {
                return;
            }
        }
    }

    private void readXtceEntryList(SpaceSystem spaceSystem, SequenceContainer seqContainer) throws XMLStreamException {
        log.trace(XTCE_EntryList);
        checkStartElementPreconditions();

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_ParameterRefEntry)) {
                SequenceEntry entry = readXtceParameterRefEntry(spaceSystem);
                entry.setSequenceContainer(seqContainer);
                seqContainer.addEntry(entry);
            } else if (isStartElementWithName(XTCE_ParameterSegmentRefEntry)) {
                skipXtceSection(XTCE_ParameterSegmentRefEntry);
            } else if (isStartElementWithName(XTCE_ContainerRefEntry)) {
                ContainerEntry entry = readXtceConteinerRefEntry(spaceSystem);                 
                entry.setSequenceContainer(seqContainer);
                seqContainer.addEntry(entry);
            } else if (isStartElementWithName(XTCE_ContainerSegmentRefEntry)) {
                skipXtceSection(XTCE_ContainerSegmentRefEntry);
            } else if (isStartElementWithName(XTCE_StreamSegmentEntry)) {
                skipXtceSection(XTCE_StreamSegmentEntry);
            } else if (isStartElementWithName(XTCE_IndirectParameterRefEntry)) {
                skipXtceSection(XTCE_IndirectParameterRefEntry);
            } else if (isStartElementWithName(XTCE_ArrayParameterRefEntry)) {
                skipXtceSection(XTCE_ArrayParameterRefEntry);
            } else if (isEndElementWithName(XTCE_EntryList)) {
                return;
            }
        }

    }

    private SequenceEntry readXtceParameterRefEntry(SpaceSystem spaceSystem) throws XMLStreamException {
        log.trace(XTCE_ParameterRefEntry);
        checkStartElementPreconditions();

        String refName = readAttribute("parameterRef", xmlEvent.asStartElement());
        if (refName == null) {
            throw new XMLStreamException("Reference to parameter is missing");
        }

        SequenceEntry.ReferenceLocationType locationType = SequenceEntry.ReferenceLocationType.previousEntry; //default
        Parameter parameter = spaceSystem.getParameter(refName);
        ParameterEntry parameterEntry=null;
        if (parameter != null) {
            parameterEntry = new ParameterEntry(-1, null, 0, locationType, parameter);
        } else {
            parameterEntry = new ParameterEntry(-1, null, 0, locationType);
            final ParameterEntry finalpe=parameterEntry;
            NameReference nr=new NameReference(refName, Type.PARAMETER_TYPE,
                    new ResolvedAction() {
                @Override
                public boolean resolved(NameDescription nd) {
                    finalpe.setParameter((Parameter) nd);
                    return true;
                }
            });
            spaceSystem.addUnresolvedReference(nr);
        }

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_LocationInContainerInBits)) {
                readXtceLocationInContainerInBits(parameterEntry);
            } else if (isStartElementWithName(XTCE_RepeatEntry)) {
                Repeat r = readXtceRepeatEntry(spaceSystem);
                parameterEntry.setRepeatEntry(r);
            } else if (isStartElementWithName(XTCE_IncludeCondition)) {
                skipXtceSection(XTCE_IncludeCondition);
            } else if (isEndElementWithName(XTCE_ParameterRefEntry)) {
                return parameterEntry;
            }
        }
    }

    

    private ContainerEntry readXtceConteinerRefEntry(SpaceSystem spaceSystem) throws XMLStreamException {
        log.trace(XTCE_ContainerRefEntry);
        checkStartElementPreconditions();

        String refName = readAttribute("containerRef", xmlEvent.asStartElement());
        if (refName == null) {
            throw new XMLStreamException("Reference to container is missing");
        }

        SequenceEntry.ReferenceLocationType locationType = SequenceEntry.ReferenceLocationType.previousEntry; //default
        SequenceContainer container = spaceSystem.getSequenceContainer(refName);
        ContainerEntry containerEntry=null;
        if (container != null) {
            containerEntry = new ContainerEntry(-1, null, 0, locationType, container);
        } else {
            containerEntry = new ContainerEntry(-1, null, 0, locationType);
            final ContainerEntry finalce = containerEntry;
            NameReference nr=new NameReference(refName, Type.SEQUENCE_CONTAINTER, new ResolvedAction() {
                @Override
                public boolean resolved(NameDescription nd) {
                    finalce.setRefContainer((SequenceContainer) nd);
                    return true;
                }
            });
            spaceSystem.addUnresolvedReference(nr);
        }

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_LocationInContainerInBits)) {
                readXtceLocationInContainerInBits(containerEntry);
            } else if (isStartElementWithName(XTCE_RepeatEntry)) {
                Repeat r = readXtceRepeatEntry(spaceSystem);
                containerEntry.setRepeatEntry(r);
            } else if (isStartElementWithName(XTCE_IncludeCondition)) {
                skipXtceSection(XTCE_IncludeCondition);
            } else if (isEndElementWithName(XTCE_ContainerRefEntry)) {
                return containerEntry;
            }
        }
    }

    
    private Repeat readXtceRepeatEntry(SpaceSystem spaceSystem) throws  XMLStreamException {
        log.trace(XTCE_RepeatEntry);
        Repeat r = new Repeat();
        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_Count)) {
                r.setCount(readXtceIntegerValue(spaceSystem));
            } else if (isStartElementWithName("FromBinaryTransformAlgorithm")) {
                skipXtceSection("FromBinaryTransformAlgorithm");
            } else if (isStartElementWithName("ToBinaryTransformAlgorithm")) {
                skipXtceSection("ToBinaryTransformAlgorithm");
            } else if (isEndElementWithName(XTCE_RepeatEntry)) {
                return r;
            }
        }
    }



    private IntegerValue readXtceIntegerValue(SpaceSystem spaceSystem) throws XMLStreamException {
        String tag = xmlEvent.asStartElement().getName().getLocalPart();
        
        checkStartElementPreconditions();
        IntegerValue v = null;

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();
            if (isStartElementWithName(XTCE_FixedValue)) {
                v = new FixedIntegerValue(readIntegerValue());
            } else if (isStartElementWithName(XTCE_DynamicValue)) {
                v = readDynamicValue(spaceSystem);
            } else if (isEndElementWithName(tag)) {
                return v;
            }
        }
    }


    private DynamicIntegerValue readDynamicValue(SpaceSystem spaceSystem) throws XMLStreamException {
        log.trace(XTCE_DynamicValue);

        checkStartElementPreconditions();
        DynamicIntegerValue v = new DynamicIntegerValue();

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();
            if (isStartElementWithName(XTCE_ParameterInstanceRef)) {
                ParameterInstanceRef pir = readXtceParameterInstanceRef(spaceSystem);
                v.setParameterInstanceRef(pir);
            } else if (isEndElementWithName(XTCE_DynamicValue)) {
                return v;
            }
        }
    }


    private ParameterInstanceRef readXtceParameterInstanceRef(SpaceSystem spaceSystem) throws XMLStreamException {
        log.trace(XTCE_ParameterInstanceRef);
        String paramRef = readAttribute("parameterRef", xmlEvent.asStartElement());
        if(paramRef==null) {
            throw new XMLStreamException("Reference to parameter is missing");
        }

        final ParameterInstanceRef instanceRef = new ParameterInstanceRef(true);

        NameReference nr=new NameReference(paramRef, Type.PARAMETER_TYPE,
                new ResolvedAction() {
            @Override
            public boolean resolved(NameDescription nd) {
                instanceRef.setParameter((Parameter) nd);
                return true;
            }
        });

        Parameter parameter = spaceSystem.getParameter(paramRef);
        if(parameter!=null) {
            if(!nr.resolved(parameter)) {
                spaceSystem.addUnresolvedReference(nr);
            }
        } else {
            spaceSystem.addUnresolvedReference(nr);
        }

        return instanceRef;
    }


    private void readXtceLocationInContainerInBits(SequenceEntry entry) throws XMLStreamException {
        log.trace(XTCE_LocationInContainerInBits);
        checkStartElementPreconditions();

        int locationInContainerInBits = 0;

        String value = readAttribute("referenceLocation", xmlEvent.asStartElement());
        if (value == null) {
            value = "previousEntry"; // default
        }

        if (value.equalsIgnoreCase("previousEntry")) {
            entry.setReferenceLocation(SequenceEntry.ReferenceLocationType.previousEntry);
        } else if (value.equalsIgnoreCase("containerStart")) {
            entry.setReferenceLocation(SequenceEntry.ReferenceLocationType.containerStart);
        } else {
            throw new XMLStreamException("Currently unsupported reference location: " + value);
        }

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isStartElementWithName(XTCE_FixedValue)) {
                locationInContainerInBits = readIntegerValue();
            } else if (isStartElementWithName(XTCE_DynamicValue)) {
                skipXtceSection(XTCE_DynamicValue);
            } else if (isStartElementWithName(XTCE_DiscreteLookupList)) {
                skipXtceSection(XTCE_DiscreteLookupList);
            } else if (isEndElementWithName(XTCE_LocationInContainerInBits)) {
                entry.setLocationInContainerInBits(locationInContainerInBits);
                return;
            }
        }
    }

   
    private Comparison readXtceComparison(SpaceSystem spaceSystem) throws XMLStreamException {
        log.trace(XTCE_Comparison);
        checkStartElementPreconditions();


        String paramRef = readAttribute("parameterRef", xmlEvent.asStartElement());
        if(paramRef==null) {
            throw new XMLStreamException("Reference to parameter is missing");
        }

        String value = readAttribute("comparisonOperator", xmlEvent.asStartElement());
        if (value == null) {
            value = "=="; // default value
        }

        OperatorType optype = Comparison.stringToOperator(value);

        String theValue;
        theValue = readAttribute("value", xmlEvent.asStartElement());
        if (theValue == null) {
            throw new XMLStreamException("Value for comparison is missing");
        }

        boolean useCalibratedValue = true; // default value
        value = readAttribute("useCalibratedValue", xmlEvent.asStartElement());
        if (value != null) {
            useCalibratedValue = value.equalsIgnoreCase("true");
        }


        final ParameterInstanceRef instanceRef = new ParameterInstanceRef(useCalibratedValue);
        Comparison comparison = new Comparison(instanceRef, theValue, optype);
        
        NameReference nr = new NameReference(paramRef, Type.PARAMETER,  new ResolvedAction() {
            @Override
            public boolean resolved(NameDescription nd) {
                Parameter p=(Parameter)nd;
                instanceRef.setParameter((Parameter) nd);                                
                if(p.getParameterType()==null) return false;
                comparison.resolveValueType();
                return true;
            }
        });


        Parameter parameter = spaceSystem.getParameter(paramRef);
        if(parameter!=null) {
            if(!nr.resolved(parameter)) {
                spaceSystem.addUnresolvedReference(nr);
            }
        } else {
            spaceSystem.addUnresolvedReference(nr);
        }

        while (true) {
            xmlEvent = xmlEventReader.nextEvent();

            if (isEndElementWithName(XTCE_Comparison)) {
                return comparison;
            }
        }
    }

    /**
     * Just skips the whole section.
     * 
     * @return
     * @throws IllegalStateException
     * @throws XMLStreamException
     */
    private XtceNotImplemented readXtceMessageSet() throws IllegalStateException,
    XMLStreamException {
        skipXtceSection(XTCE_MessageSet);
        return null;
    }

    /**
     * Just skips the whole section.
     * 
     * @return
     * @throws IllegalStateException
     * @throws XMLStreamException
     */
    private XtceNotImplemented readXtceStreamSet() throws IllegalStateException, XMLStreamException {
        skipXtceSection(XTCE_StreamSet);
        return null;
    }

    /**
     * Just skips the whole section.
     * @param spaceSystem 
     * 
     * @return
     * @throws IllegalStateException
     * @throws XMLStreamException
     */
    private XtceNotImplemented readXtceAlgorithmSet(SpaceSystem spaceSystem) throws IllegalStateException, XMLStreamException {
        skipXtceSection(XTCE_AlgorithmSet);
        return null;
    }

    /**
     * Extraction of the CommandMetaData section Current implementation does
     * nothing, just skips whole section
     * @param spaceSystem 
     * 
     * @throws XMLStreamException
     */
    private void readXtceCommandMetaData(SpaceSystem spaceSystem) throws XMLStreamException {
        skipXtceSection(XTCE_CommandMetaData);
    }

    /**
     * Increase the skip statistics for the section.
     * @param xtceSectionName Name of the skipped section.
     */
    private void addToSkipStatistics(String xtceSectionName) {
        Integer count = xtceSkipStatistics.get(xtceSectionName);
        if (count == null) {
            xtceSkipStatistics.put(xtceSectionName, new Integer(1));
        } else {
            xtceSkipStatistics.put(xtceSectionName, count + 1);
        } 
    }

    /**
     * Write statistics.
     */
    public void writeStatistics() {

        log.info("------------------");
        log.info("Statistics of skipped elements: ");
        for (Map.Entry<String, Integer> entry : xtceSkipStatistics.entrySet()) {
            log.info(">> " + entry.getKey() + ": " + entry.getValue());
        }
        log.info("------------------");
    }

    /**
     * Skips whole section in the document.
     * 
     * @param sectionName
     *            name of the section to skip
     * @throws XMLStreamException
     * @throws IllegalStateException
     *             Exception on algorithm error
     */
    private void skipXtceSection(String sectionName) throws XMLStreamException,
    IllegalStateException {
        log.trace(sectionName);
        checkStartElementPreconditions();

        addToSkipStatistics(sectionName);

        while (true) {
            // just skip whole section, read events until the
            // end element of the section occurs
            try {
                xmlEvent = xmlEventReader.nextEvent();

                if (isStartElementWithName(sectionName)) {
                    // skip wrapped sections with the same name
                    skipXtceSection(sectionName);
                } else if (isEndElementWithName(sectionName)) {
                    log.info("Section <" + sectionName + "> skipped");
                    return;
                }
            } catch (NoSuchElementException e) {
                throw new IllegalStateException("End of section unreachable: " + sectionName);
            }
        }
    }

    /**
     * 
     * @param filename
     * @return
     * @throws FileNotFoundException
     * @throws XMLStreamException
     */
    private XMLEventReader initEventReader(String filename) throws FileNotFoundException, XMLStreamException {
        InputStream in = new FileInputStream(new File(filename));
        XMLInputFactory factory = XMLInputFactory.newInstance();
        return factory.createXMLEventReader(in);
    }

    /**
     * Examines element for presence of attributes
     * 
     * @param element
     *            Element to be examined, should not be null
     * @return True, if the element contains attributes, otherwise false
     */
    @SuppressWarnings("unused")
    private boolean hasAttributes(StartElement element) {
        if (element == null) {
            log.info("element param is null");
            return false;
        }
        return element.getAttributes().hasNext();
    }

    /**
     * Check if xml element is a start element with particular name
     * 
     * @param localName
     *            Name of the element
     * @return True if element is start element with the given name, otherwise
     *         false
     */
    private boolean isStartElementWithName(String localName) {
        return (xmlEvent.getEventType() == XMLStreamConstants.START_ELEMENT && xmlEvent
                .asStartElement().getName().getLocalPart().equals(localName));
    }

    /**
     * Test if the xmlEvent is of type END_ELEMENT and has particular local
     * name. This test is used to identify the end of section.
     * 
     * @param localName
     *            Local name of the element (we neglect namespace for now)
     * @return True if current xmlEvent is of type END_ELEMENT and has
     *         particular local name, otherwise false
     */
    private boolean isEndElementWithName(String localName) {
        return (xmlEvent.getEventType() == XMLStreamConstants.END_ELEMENT && xmlEvent
                .asEndElement().getName().getLocalPart().equals(localName));
    }

    /**
     * Checks preconditions before the dedicated code for section reading will
     * run
     * 
     * @throws IllegalStateException If the conditions are not met
     */
    private void checkStartElementPreconditions() throws IllegalStateException {
        if (xmlEvent == null) {
            throw new IllegalStateException("xmlEvent is null");
        }
        if (xmlEvent.getEventType() != XMLStreamConstants.START_ELEMENT) {
            throw new IllegalStateException("xmlEvent type is not start element");
        }
    }

    /**
     * Get attribute values as string
     * 
     * @param attName
     *            Name of the attribute
     * @param element
     *            Start element which the attribute is read from
     * @return Attribute's value as string
     */
    private String readAttribute(String attName, StartElement element) {
        Attribute attribute = element.getAttributeByName(new QName(attName));
        if (attribute != null) {
            return attribute.getValue();
        }
        return null;
    }

    /**
     * Test method. 
     * @param args XTCE document to load.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        XtceStaxReader reader = new XtceStaxReader();

        if (args.length == 1) {
            reader.readXmlDocument(args[0]);
            reader.writeStatistics();
        } else {
            System.out.println("Wrong arguments, exactly one argument allowed");
        }

    }


    public void setExcludedContainers(Set<String> excludedContainers) {
        this.excludedContainers = excludedContainers;
    }
}
