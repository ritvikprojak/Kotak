package com.sb.filenet.genericapi.service.impl;


import com.sb.filenet.genericapi.service.IAttribute;
import com.sb.filenet.genericapi.util.DataType;

/**
 * @author MK
 * 
 */
public class DocAttribute implements IAttribute
{
    /**
     * 
     */
    private static final long serialVersionUID = 3725823518613403868L;
    private final String name;
    private final String value;
    private final DataType dataType;
    private boolean isRequired = false;

    /**
     * @param name
     * @param value
     * @param dataType
     */
    public DocAttribute(String name, String value, DataType dataType)
    {
        super();
        this.name = name;
        this.dataType = dataType;
        this.value = value;

    }
    
    /**
     * @param name
     * @param value
     * @param dataType
     * @param isRequired
     */
    public DocAttribute(String name, String value, boolean isRequired, DataType dataType)
    {
        super();
        this.name = name;
        this.dataType = dataType;
        this.value = value;
        this.isRequired = isRequired;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the dataType
     */
    public DataType getDataType()
    {
        return dataType;
    }

    public String getValue()
    {
        // TODO Auto-generated method stub
        return value;
    }

    public boolean isRequired()
    {
        return isRequired;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DocAttribute [");
        if (name != null)
            builder.append("name=").append(name).append(", ");
        if (value != null)
            builder.append("value=").append(value).append(", ");
        if (dataType != null)
            builder.append("dataType=").append(dataType).append(", ");
        builder.append("isRequired=").append(isRequired).append("]");
        return builder.toString();
    }

    
    /**
     * @return the dataCardinality
     */
    /*
     * public DataCardinality getDataCardinality() { return dataCardinality; }
     */

}