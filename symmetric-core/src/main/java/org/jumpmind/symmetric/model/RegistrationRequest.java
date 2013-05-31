package org.jumpmind.symmetric.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class RegistrationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    public static enum RegistrationStatus {
        OK, RQ, IG, RR, ER
    };

    private String nodeGroupId;
    private String externalId;
    private RegistrationStatus status;
    private String hostName;
    private String ipAddress;
    private long attemptCount;
    private String registeredNodeId;
    private String errorMessage;
    private Date createTime = new Date();
    private String lastUpdateBy = "engine";
    private Date lastUpdateTime = new Date();
    
    public RegistrationRequest() {
    }
    
    public RegistrationRequest(Node node, RegistrationStatus status,
            String hostName, String ipAddress) {
        this.nodeGroupId = node.getNodeGroupId();
        this.externalId = node.getExternalId();
        this.registeredNodeId = node.getNodeId();
        this.status = status;
        this.hostName = hostName == null ? "unknown" : hostName;
        setIpAddress(ipAddress);
    }

    public String getNodeGroupId() {
        return nodeGroupId;
    }

    public void setNodeGroupId(String nodeGroupId) {
        this.nodeGroupId = nodeGroupId;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = StringUtils.left(ipAddress == null ? "unknown" : ipAddress,
                NodeHost.MAX_IP_ADDRESS_SIZE);
    }

    public long getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(long attemptCount) {
        this.attemptCount = attemptCount;
    }

    public String getRegisteredNodeId() {
        return registeredNodeId;
    }

    public void setRegisteredNodeId(String registeredNodeId) {
        this.registeredNodeId = registeredNodeId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
    
    public void setErrorMessage(String message) {
        this.errorMessage = message;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }

}
