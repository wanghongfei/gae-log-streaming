package org.fh.gae.streaming.task.log;


import static org.apache.commons.lang3.math.NumberUtils.toInt;
import static org.apache.commons.lang3.math.NumberUtils.toLong;

public class SearchLog extends GaeLog {
    private String sid;

    private long ts;

    // 流量方id
    private String tid = "-";
    // 流量方请求id
    private String requestId = "-";
    // 资源类型
    private long resourceType = 0;
    // 广告位编码
    private String slotCode = "-";
    //  广告位类型
    private long slotType = 0;
    //  广告位宽
    private int width = 0;
    //  广告位高
    private int height = 0;
    //  物料类型
    private String materialType = "-";
    //  设备mac
    private String mac = "-";
    //  设备ip
    private String ip = "-";
    //  推广计划id
    private int planId = 0;
    //  推广单元id
    private int unitId = 0;
    //  创意id
    private String ideaId = "-";
    // 广告唯一标识
    private String adCode = "-";
    // 命中的标签信息,tagType:tagId逗号分隔, 如 1:1200,2:2100,3:4500
    private String tagIds = "-";
    // 地域id
    private int regionId = 0;
    // 出价, cpm
    private long bid = 0;


    public SearchLog() {
        super(LogType.SEARCH_LOG);
    }

    public static SearchLog ofString(String[] terms) {
        if (terms.length != 20) {
            return null;
        }

        String sid = terms[1];
        long ts = toLong(terms[2]);
        String tid = terms[3];
        String requestId = terms[4];
        long resourceType = toLong(terms[5]);
        String slotCode = terms[6];
        long slotType = toLong(terms[7]);
        int width = toInt(terms[8]);
        int height = toInt(terms[9]);
        String materialType = terms[10];
        String mac = terms[11];
        String ip = terms[12];
        int planId = toInt(terms[13], -1);
        int unitId = toInt(terms[14]);
        String ideaId = terms[15];
        String adCode = terms[16];
        String tagIds = terms[17];
        int regionId = toInt(terms[18]);
        long bid = Long.valueOf(terms[19]);

        SearchLog searchLog = new SearchLog();
        searchLog.setSid(sid);
        searchLog.setTs(ts);
        searchLog.setTid(tid);
        searchLog.setRequestId(requestId);
        searchLog.setResourceType(resourceType);
        searchLog.setSlotCode(slotCode);
        searchLog.setSlotType(slotType);
        searchLog.setWidth(width);
        searchLog.setHeight(height);
        searchLog.setMaterialType(materialType);
        searchLog.setMac(mac);
        searchLog.setIp(ip);
        searchLog.setPlanId(planId);
        searchLog.setUnitId(unitId);
        searchLog.setIdeaId(ideaId);
        searchLog.setAdCode(adCode);
        searchLog.setTagIds(tagIds);
        searchLog.setRegionId(regionId);
        searchLog.setBid(bid);

        return searchLog;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(50);

        sb.append(getLogType().code()).append('\t');
        sb.append(sid).append('\t');
        sb.append(ts).append('\t');
        sb.append(tid).append('\t');
        sb.append(requestId).append('\t');
        sb.append(resourceType).append('\t');
        sb.append(slotCode).append('\t');
        sb.append(slotType).append('\t');
        sb.append(width).append('\t');
        sb.append(height).append('\t');
        sb.append(materialType).append('\t');
        sb.append(mac).append('\t');
        sb.append(ip).append('\t');
        sb.append(planId).append('\t');
        sb.append(unitId).append('\t');
        sb.append(ideaId).append('\t');
        sb.append(adCode).append('\t');
        sb.append(tagIds).append('\t');
        sb.append(regionId).append('\t');
        sb.append(bid);

        return sb.toString();
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public long getResourceType() {
        return resourceType;
    }

    public void setResourceType(long resourceType) {
        this.resourceType = resourceType;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public void setSlotCode(String slotCode) {
        this.slotCode = slotCode;
    }

    public long getSlotType() {
        return slotType;
    }

    public void setSlotType(long slotType) {
        this.slotType = slotType;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public int getUnitId() {
        return unitId;
    }

    public void setUnitId(int unitId) {
        this.unitId = unitId;
    }

    public String getIdeaId() {
        return ideaId;
    }

    public void setIdeaId(String ideaId) {
        this.ideaId = ideaId;
    }

    public String getAdCode() {
        return adCode;
    }

    public void setAdCode(String adCode) {
        this.adCode = adCode;
    }

    public String getTagIds() {
        return tagIds;
    }

    public void setTagIds(String tagIds) {
        this.tagIds = tagIds;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public long getBid() {
        return bid;
    }

    public void setBid(long bid) {
        this.bid = bid;
    }
}
