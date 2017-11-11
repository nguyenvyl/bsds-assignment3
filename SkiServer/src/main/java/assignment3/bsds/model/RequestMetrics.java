/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment3.bsds.model;

/**
 *
 * @author Vy
 */
public class RequestMetrics implements Comparable<RequestMetrics> {
   private String requestType;
   private double responseTime;
   private double dbQueryTime;
   private int errorCode;
   private String serverId;

    public RequestMetrics() {
    }
   
    public RequestMetrics(String requestType, int responseTime, int dbQueryTime, String serverId) {
        this.requestType = requestType;
        this.responseTime = responseTime;
        this.dbQueryTime = dbQueryTime;
        this.serverId = serverId;
    }
   
    public RequestMetrics(String requestType, int responseTime, int dbQueryTime, String serverId, int errorCode) {
        this.requestType = requestType;
        this.responseTime = responseTime;
        this.dbQueryTime = dbQueryTime;
        this.serverId = serverId;
        this.errorCode = errorCode;
    }

    public double getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(double responseTime) {
        this.responseTime = responseTime;
    }

    public double getDbQueryTime() {
        return dbQueryTime;
    }

    public void setDbQueryTime(double dbQueryTime) {
        this.dbQueryTime = dbQueryTime;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }


    @Override
    public int compareTo(RequestMetrics requestMetrics) {
        Double result = this.responseTime - requestMetrics.getResponseTime();
        return result.intValue();
    }
}
