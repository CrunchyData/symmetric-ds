
package org.jumpmind.symmetric.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jumpmind.properties.TypedProperties;
import org.jumpmind.symmetric.config.IParameterFilter;
import org.jumpmind.symmetric.model.DatabaseParameter;

/**
 * Get and set application wide configuration information.
 */
public interface IParameterService {

    public BigDecimal getDecimal(String key);
    
    public BigDecimal getDecimal(String key, BigDecimal defaulVal);

    public boolean is(String key);
    
    public boolean is(String key, boolean defaultVal);

    public int getInt(String key);
    
    public int getInt(String key, int defaultVal);

    public long getLong(String key);
    
    public long getLong(String key, long defaultVal);

    public String getString(String key);
    
    public String getString(String key, String defaultVal);

    public void saveParameter(String key, Object paramValue, String lastUpdateBy);

    public void saveParameter(String externalId, String nodeGroupId, String key, Object paramValue, String lastUpdateBy);

    public void saveParameters(String externalId, String nodeGroupId, Map<String, Object> parameters, String lastUpdateBy);
    
    public void deleteParameter(String externalId, String nodeGroupId, String key);

    public void rereadParameters();

    public Date getLastTimeParameterWereCached();
    
    public List<DatabaseParameter> getDatabaseParametersFor(String paramKey);
    
    public TypedProperties getDatabaseParametersByNodeGroupId(String nodeGroupId);

    public TypedProperties getAllParameters();

    public void setParameterFilter(IParameterFilter f);
    
    public boolean isRegistrationServer();
    
    public boolean refreshFromDatabase();

    /**
     * Get the group id for this instance
     */
    public String getNodeGroupId();

    /**
     * Get the external id for this instance
     */
    public String getExternalId();
    
    public Map<String,String> getReplacementValues();

    /**
     * Provide the url used to register at to get initial configuration
     * information
     */
    public String getRegistrationUrl();

    /**
     * Provide information about the URL used to contact this symmetric instance
     */
    public String getSyncUrl();

    public String getTablePrefix();
    
    public String getEngineName();
    
    public String getTempDirectory();
    
}