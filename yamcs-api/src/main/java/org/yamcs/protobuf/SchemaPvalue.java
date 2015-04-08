// Generated by http://code.google.com/p/protostuff/ ... DO NOT EDIT!
// Generated from pvalue.proto

package org.yamcs.protobuf;


public final class SchemaPvalue
{

    public static final class ParameterValue
    {
        public static final org.yamcs.protobuf.SchemaPvalue.ParameterValue.MessageSchema WRITE =
            new org.yamcs.protobuf.SchemaPvalue.ParameterValue.MessageSchema();
        public static final org.yamcs.protobuf.SchemaPvalue.ParameterValue.BuilderSchema MERGE =
            new org.yamcs.protobuf.SchemaPvalue.ParameterValue.BuilderSchema();
        
        public static class MessageSchema implements com.dyuproject.protostuff.Schema<org.yamcs.protobuf.Pvalue.ParameterValue>
        {
            public void writeTo(com.dyuproject.protostuff.Output output, org.yamcs.protobuf.Pvalue.ParameterValue message) throws java.io.IOException
            {
                if(message.hasId())
                    output.writeObject(1, message.getId(), org.yamcs.protobuf.SchemaYamcs.NamedObjectId.WRITE, false);

                if(message.hasRawValue())
                    output.writeObject(2, message.getRawValue(), org.yamcs.protobuf.SchemaYamcs.Value.WRITE, false);

                if(message.hasEngValue())
                    output.writeObject(3, message.getEngValue(), org.yamcs.protobuf.SchemaYamcs.Value.WRITE, false);

                if(message.hasAcquisitionTime())
                    output.writeInt64(4, message.getAcquisitionTime(), false);
                if(message.hasGenerationTime())
                    output.writeInt64(5, message.getGenerationTime(), false);
                if(message.hasAcquisitionStatus())
                    output.writeEnum(6, message.getAcquisitionStatus().getNumber(), false);
                if(message.hasProcessingStatus())
                    output.writeBool(7, message.getProcessingStatus(), false);
                if(message.hasMonitoringResult())
                    output.writeEnum(8, message.getMonitoringResult().getNumber(), false);
                if(message.hasAcquisitionTimeUTC())
                    output.writeString(11, message.getAcquisitionTimeUTC(), false);
                if(message.hasGenerationTimeUTC())
                    output.writeString(12, message.getGenerationTimeUTC(), false);
                if(message.hasWatchLow())
                    output.writeDouble(13, message.getWatchLow(), false);
                if(message.hasWatchHigh())
                    output.writeDouble(14, message.getWatchHigh(), false);
                if(message.hasWarningLow())
                    output.writeDouble(15, message.getWarningLow(), false);
                if(message.hasWarningHigh())
                    output.writeDouble(16, message.getWarningHigh(), false);
                if(message.hasDistressLow())
                    output.writeDouble(17, message.getDistressLow(), false);
                if(message.hasDistressHigh())
                    output.writeDouble(18, message.getDistressHigh(), false);
                if(message.hasCriticalLow())
                    output.writeDouble(19, message.getCriticalLow(), false);
                if(message.hasCriticalHigh())
                    output.writeDouble(20, message.getCriticalHigh(), false);
                if(message.hasSevereLow())
                    output.writeDouble(21, message.getSevereLow(), false);
                if(message.hasSevereHigh())
                    output.writeDouble(22, message.getSevereHigh(), false);
            }
            public boolean isInitialized(org.yamcs.protobuf.Pvalue.ParameterValue message)
            {
                return message.isInitialized();
            }
            public java.lang.String getFieldName(int number)
            {
                return org.yamcs.protobuf.SchemaPvalue.ParameterValue.getFieldName(number);
            }
            public int getFieldNumber(java.lang.String name)
            {
                return org.yamcs.protobuf.SchemaPvalue.ParameterValue.getFieldNumber(name);
            }
            public java.lang.Class<org.yamcs.protobuf.Pvalue.ParameterValue> typeClass()
            {
                return org.yamcs.protobuf.Pvalue.ParameterValue.class;
            }
            public java.lang.String messageName()
            {
                return org.yamcs.protobuf.Pvalue.ParameterValue.class.getSimpleName();
            }
            public java.lang.String messageFullName()
            {
                return org.yamcs.protobuf.Pvalue.ParameterValue.class.getName();
            }
            //unused
            public void mergeFrom(com.dyuproject.protostuff.Input input, org.yamcs.protobuf.Pvalue.ParameterValue message) throws java.io.IOException {}
            public org.yamcs.protobuf.Pvalue.ParameterValue newMessage() { return null; }
        }
        public static class BuilderSchema implements com.dyuproject.protostuff.Schema<org.yamcs.protobuf.Pvalue.ParameterValue.Builder>
        {
            public void mergeFrom(com.dyuproject.protostuff.Input input, org.yamcs.protobuf.Pvalue.ParameterValue.Builder builder) throws java.io.IOException
            {
                for(int number = input.readFieldNumber(this);; number = input.readFieldNumber(this))
                {
                    switch(number)
                    {
                        case 0:
                            return;
                        case 1:
                            builder.setId(input.mergeObject(org.yamcs.protobuf.Yamcs.NamedObjectId.newBuilder(), org.yamcs.protobuf.SchemaYamcs.NamedObjectId.MERGE));

                            break;
                        case 2:
                            builder.setRawValue(input.mergeObject(org.yamcs.protobuf.Yamcs.Value.newBuilder(), org.yamcs.protobuf.SchemaYamcs.Value.MERGE));

                            break;
                        case 3:
                            builder.setEngValue(input.mergeObject(org.yamcs.protobuf.Yamcs.Value.newBuilder(), org.yamcs.protobuf.SchemaYamcs.Value.MERGE));

                            break;
                        case 4:
                            builder.setAcquisitionTime(input.readInt64());
                            break;
                        case 5:
                            builder.setGenerationTime(input.readInt64());
                            break;
                        case 6:
                            builder.setAcquisitionStatus(org.yamcs.protobuf.Pvalue.AcquisitionStatus.valueOf(input.readEnum()));
                            break;
                        case 7:
                            builder.setProcessingStatus(input.readBool());
                            break;
                        case 8:
                            builder.setMonitoringResult(org.yamcs.protobuf.Pvalue.MonitoringResult.valueOf(input.readEnum()));
                            break;
                        case 11:
                            builder.setAcquisitionTimeUTC(input.readString());
                            break;
                        case 12:
                            builder.setGenerationTimeUTC(input.readString());
                            break;
                        case 13:
                            builder.setWatchLow(input.readDouble());
                            break;
                        case 14:
                            builder.setWatchHigh(input.readDouble());
                            break;
                        case 15:
                            builder.setWarningLow(input.readDouble());
                            break;
                        case 16:
                            builder.setWarningHigh(input.readDouble());
                            break;
                        case 17:
                            builder.setDistressLow(input.readDouble());
                            break;
                        case 18:
                            builder.setDistressHigh(input.readDouble());
                            break;
                        case 19:
                            builder.setCriticalLow(input.readDouble());
                            break;
                        case 20:
                            builder.setCriticalHigh(input.readDouble());
                            break;
                        case 21:
                            builder.setSevereLow(input.readDouble());
                            break;
                        case 22:
                            builder.setSevereHigh(input.readDouble());
                            break;
                        default:
                            input.handleUnknownField(number, this);
                    }
                }
            }
            public boolean isInitialized(org.yamcs.protobuf.Pvalue.ParameterValue.Builder builder)
            {
                return builder.isInitialized();
            }
            public org.yamcs.protobuf.Pvalue.ParameterValue.Builder newMessage()
            {
                return org.yamcs.protobuf.Pvalue.ParameterValue.newBuilder();
            }
            public java.lang.String getFieldName(int number)
            {
                return org.yamcs.protobuf.SchemaPvalue.ParameterValue.getFieldName(number);
            }
            public int getFieldNumber(java.lang.String name)
            {
                return org.yamcs.protobuf.SchemaPvalue.ParameterValue.getFieldNumber(name);
            }
            public java.lang.Class<org.yamcs.protobuf.Pvalue.ParameterValue.Builder> typeClass()
            {
                return org.yamcs.protobuf.Pvalue.ParameterValue.Builder.class;
            }
            public java.lang.String messageName()
            {
                return org.yamcs.protobuf.Pvalue.ParameterValue.class.getSimpleName();
            }
            public java.lang.String messageFullName()
            {
                return org.yamcs.protobuf.Pvalue.ParameterValue.class.getName();
            }
            //unused
            public void writeTo(com.dyuproject.protostuff.Output output, org.yamcs.protobuf.Pvalue.ParameterValue.Builder builder) throws java.io.IOException {}
        }
        public static java.lang.String getFieldName(int number)
        {
            switch(number)
            {
                case 1: return "id";
                case 2: return "rawValue";
                case 3: return "engValue";
                case 4: return "acquisitionTime";
                case 5: return "generationTime";
                case 6: return "acquisitionStatus";
                case 7: return "processingStatus";
                case 8: return "monitoringResult";
                case 11: return "acquisitionTimeUTC";
                case 12: return "generationTimeUTC";
                case 13: return "watchLow";
                case 14: return "watchHigh";
                case 15: return "warningLow";
                case 16: return "warningHigh";
                case 17: return "distressLow";
                case 18: return "distressHigh";
                case 19: return "criticalLow";
                case 20: return "criticalHigh";
                case 21: return "severeLow";
                case 22: return "severeHigh";
                default: return null;
            }
        }
        public static int getFieldNumber(java.lang.String name)
        {
            java.lang.Integer number = fieldMap.get(name);
            return number == null ? 0 : number.intValue();
        }
        private static final java.util.HashMap<java.lang.String,java.lang.Integer> fieldMap = new java.util.HashMap<java.lang.String,java.lang.Integer>();
        static
        {
            fieldMap.put("id", 1);
            fieldMap.put("rawValue", 2);
            fieldMap.put("engValue", 3);
            fieldMap.put("acquisitionTime", 4);
            fieldMap.put("generationTime", 5);
            fieldMap.put("acquisitionStatus", 6);
            fieldMap.put("processingStatus", 7);
            fieldMap.put("monitoringResult", 8);
            fieldMap.put("acquisitionTimeUTC", 11);
            fieldMap.put("generationTimeUTC", 12);
            fieldMap.put("watchLow", 13);
            fieldMap.put("watchHigh", 14);
            fieldMap.put("warningLow", 15);
            fieldMap.put("warningHigh", 16);
            fieldMap.put("distressLow", 17);
            fieldMap.put("distressHigh", 18);
            fieldMap.put("criticalLow", 19);
            fieldMap.put("criticalHigh", 20);
            fieldMap.put("severeLow", 21);
            fieldMap.put("severeHigh", 22);
        }
    }

    public static final class ParameterData
    {
        public static final org.yamcs.protobuf.SchemaPvalue.ParameterData.MessageSchema WRITE =
            new org.yamcs.protobuf.SchemaPvalue.ParameterData.MessageSchema();
        public static final org.yamcs.protobuf.SchemaPvalue.ParameterData.BuilderSchema MERGE =
            new org.yamcs.protobuf.SchemaPvalue.ParameterData.BuilderSchema();
        
        public static class MessageSchema implements com.dyuproject.protostuff.Schema<org.yamcs.protobuf.Pvalue.ParameterData>
        {
            public void writeTo(com.dyuproject.protostuff.Output output, org.yamcs.protobuf.Pvalue.ParameterData message) throws java.io.IOException
            {
                for(org.yamcs.protobuf.Pvalue.ParameterValue parameter : message.getParameterList())
                    output.writeObject(1, parameter, org.yamcs.protobuf.SchemaPvalue.ParameterValue.WRITE, true);

                if(message.hasGroup())
                    output.writeString(2, message.getGroup(), false);
                if(message.hasGenerationTime())
                    output.writeInt64(3, message.getGenerationTime(), false);
                if(message.hasSeqNum())
                    output.writeInt32(4, message.getSeqNum(), false);
            }
            public boolean isInitialized(org.yamcs.protobuf.Pvalue.ParameterData message)
            {
                return message.isInitialized();
            }
            public java.lang.String getFieldName(int number)
            {
                return org.yamcs.protobuf.SchemaPvalue.ParameterData.getFieldName(number);
            }
            public int getFieldNumber(java.lang.String name)
            {
                return org.yamcs.protobuf.SchemaPvalue.ParameterData.getFieldNumber(name);
            }
            public java.lang.Class<org.yamcs.protobuf.Pvalue.ParameterData> typeClass()
            {
                return org.yamcs.protobuf.Pvalue.ParameterData.class;
            }
            public java.lang.String messageName()
            {
                return org.yamcs.protobuf.Pvalue.ParameterData.class.getSimpleName();
            }
            public java.lang.String messageFullName()
            {
                return org.yamcs.protobuf.Pvalue.ParameterData.class.getName();
            }
            //unused
            public void mergeFrom(com.dyuproject.protostuff.Input input, org.yamcs.protobuf.Pvalue.ParameterData message) throws java.io.IOException {}
            public org.yamcs.protobuf.Pvalue.ParameterData newMessage() { return null; }
        }
        public static class BuilderSchema implements com.dyuproject.protostuff.Schema<org.yamcs.protobuf.Pvalue.ParameterData.Builder>
        {
            public void mergeFrom(com.dyuproject.protostuff.Input input, org.yamcs.protobuf.Pvalue.ParameterData.Builder builder) throws java.io.IOException
            {
                for(int number = input.readFieldNumber(this);; number = input.readFieldNumber(this))
                {
                    switch(number)
                    {
                        case 0:
                            return;
                        case 1:
                            builder.addParameter(input.mergeObject(org.yamcs.protobuf.Pvalue.ParameterValue.newBuilder(), org.yamcs.protobuf.SchemaPvalue.ParameterValue.MERGE));

                            break;
                        case 2:
                            builder.setGroup(input.readString());
                            break;
                        case 3:
                            builder.setGenerationTime(input.readInt64());
                            break;
                        case 4:
                            builder.setSeqNum(input.readInt32());
                            break;
                        default:
                            input.handleUnknownField(number, this);
                    }
                }
            }
            public boolean isInitialized(org.yamcs.protobuf.Pvalue.ParameterData.Builder builder)
            {
                return builder.isInitialized();
            }
            public org.yamcs.protobuf.Pvalue.ParameterData.Builder newMessage()
            {
                return org.yamcs.protobuf.Pvalue.ParameterData.newBuilder();
            }
            public java.lang.String getFieldName(int number)
            {
                return org.yamcs.protobuf.SchemaPvalue.ParameterData.getFieldName(number);
            }
            public int getFieldNumber(java.lang.String name)
            {
                return org.yamcs.protobuf.SchemaPvalue.ParameterData.getFieldNumber(name);
            }
            public java.lang.Class<org.yamcs.protobuf.Pvalue.ParameterData.Builder> typeClass()
            {
                return org.yamcs.protobuf.Pvalue.ParameterData.Builder.class;
            }
            public java.lang.String messageName()
            {
                return org.yamcs.protobuf.Pvalue.ParameterData.class.getSimpleName();
            }
            public java.lang.String messageFullName()
            {
                return org.yamcs.protobuf.Pvalue.ParameterData.class.getName();
            }
            //unused
            public void writeTo(com.dyuproject.protostuff.Output output, org.yamcs.protobuf.Pvalue.ParameterData.Builder builder) throws java.io.IOException {}
        }
        public static java.lang.String getFieldName(int number)
        {
            switch(number)
            {
                case 1: return "parameter";
                case 2: return "group";
                case 3: return "generationTime";
                case 4: return "seqNum";
                default: return null;
            }
        }
        public static int getFieldNumber(java.lang.String name)
        {
            java.lang.Integer number = fieldMap.get(name);
            return number == null ? 0 : number.intValue();
        }
        private static final java.util.HashMap<java.lang.String,java.lang.Integer> fieldMap = new java.util.HashMap<java.lang.String,java.lang.Integer>();
        static
        {
            fieldMap.put("parameter", 1);
            fieldMap.put("group", 2);
            fieldMap.put("generationTime", 3);
            fieldMap.put("seqNum", 4);
        }
    }

}