package com.wuli.delivery.portal.bean;


public class Expressage {

    private final static String[] EXPRESSAGERELEASESTATUSLIST = {"等待接单", "已被接单", "已完成"};
    private final static String[] EXPRESSAGERECEIVESTATUSLIST = {"正在代领", "已完成"};

    /**
     * 快递id
     */
    private String expressageID;
    /**
     * 快递代领号
     */
    private String expressageLeadNum;
    /**
     * 快递送达时间
     */
    private String deliveryTime;
    /**
     * 快递发布时间
     */
    private String releaseTime;
    /**
     * 快递送达地点
     */
    private String deliverySite;
    /**
     * 包裹描述
     */
    private String expressageDesc;
    /**
     * 快递代领报酬
     */
    private String expressageLeadReward;

    /**
     * 快递代领类型 1发代领，2帮代领
     */
    private String expressageLeadType;
    /**
     * 收件人姓名
     */
    private String deliveryUserName;

    /**
     * 收件人手机号码

     */
    private String deliveryPhoneNumber;
    /**
     * 快递代领备注
     */
    private String expressageLeadRemark;
    /**
     * 自己发布的包裹状态
     */
    private String expressageReleaseStatus;

    /**
     * 代领的包裹状态
     */
    private String expressageReceiveStatus;

    /**
     * 快递类型 韵达快递 申通快递
     */
    private String expressageType;

    public Expressage() {
        super();
    }

    public String getExpressageID() {
        return expressageID;
    }

    public void setExpressageID(String expressageID) {
        this.expressageID = expressageID;
    }

    public String getExpressageLeadNum() {
        return expressageLeadNum;
    }

    public void setExpressageLeadNum(String expressageLeadNum) {
        this.expressageLeadNum = expressageLeadNum;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getDeliverySite() {
        return deliverySite;
    }

    public void setDeliverySite(String deliverySite) {
        this.deliverySite = deliverySite;
    }

    public String getExpressageLeadReward() {
        return expressageLeadReward;
    }

    public void setExpressageLeadReward(String expressageLeadReward) {
        this.expressageLeadReward = expressageLeadReward;
    }

    public String getDeliveryUserName() {
        return deliveryUserName;
    }

    public void setDeliveryUserName(String deliveryUserName) {
        this.deliveryUserName = deliveryUserName;
    }

    public String getDeliveryPhoneNumber() {
        return deliveryPhoneNumber;
    }

    public void setDeliveryPhoneNumber(String deliveryPhoneNumber) {
        this.deliveryPhoneNumber = deliveryPhoneNumber;
    }

    public String getExpressageLeadRemark() {
        return expressageLeadRemark;
    }

    public void setExpressageLeadRemark(String expressageLeadRemark) {
        this.expressageLeadRemark = expressageLeadRemark;
    }

    public String getExpressageReleaseStatus() {
        return expressageReleaseStatus;
    }

    public void setExpressageReleaseStatus(String expressageReleaseStatus) {
        this.expressageReleaseStatus = expressageReleaseStatus;
    }

    public String getExpressageReceiveStatus() {
        return expressageReceiveStatus;
    }

    public void setExpressageReceiveStatus(String expressageReceiveStatus) {
        this.expressageReceiveStatus = expressageReceiveStatus;
    }

    public String getExpressageType() {
        return expressageType;
    }

    public void setExpressageType(String expressageType) {
        this.expressageType = expressageType;
    }

    public String getExpressageLeadType() {
        return expressageLeadType;
    }

    public void setExpressageLeadType(String expressageLeadType) {
        this.expressageLeadType = expressageLeadType;
    }

    public String getExpressageDesc() {
        return expressageDesc;
    }

    public void setExpressageDesc(String expressageDesc) {
        this.expressageDesc = expressageDesc;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getExpressageStatus() {
        if ("1".equals(expressageLeadType)) {
            return EXPRESSAGERELEASESTATUSLIST[Integer.valueOf(expressageReleaseStatus)];
        } else {
            return EXPRESSAGERECEIVESTATUSLIST[Integer.valueOf(expressageReceiveStatus)];
        }
    }

    Expressage(String expressageID, String expressageLeadNum, String deliveryTime, String releaseTime, String deliverySite, String expressageDesc, String expressageLeadReward, String deliveryUserName, String deliveryPhoneNumber, String expressageLeadRemark, String expressageStatus, String expressageReceiveStatus, String expressageType, String expressageLeadType) {
        this.expressageID = expressageID;
        this.expressageLeadNum = expressageLeadNum;
        this.deliveryTime = deliveryTime;
        this.releaseTime = releaseTime;
        this.deliverySite = deliverySite;
        this.expressageDesc = expressageDesc;
        this.expressageLeadReward = expressageLeadReward;
        this.deliveryUserName = deliveryUserName;
        this.deliveryPhoneNumber = deliveryPhoneNumber;
        this.expressageLeadRemark = expressageLeadRemark;
        this.expressageReleaseStatus = expressageStatus;
        this.expressageReceiveStatus = expressageReceiveStatus;
        this.expressageType = expressageType;
        this.expressageLeadType = expressageLeadType;

    }

    public final static class Builder {

        /**
         * 快递id
         */
        private String expressageID;
        /**
         * 快递代领号
         */
        private String expressageLeadNum;
        /**
         * 快递送达时间
         */
        private String deliveryTime;
        /**
         * 快递发布时间
         */
        private String releaseTime;
        /**
         * 快递送达地点
         */
        private String deliverySite;
        /**
         * 包裹描述
         */
        private String expressageDesc;
        /**
         * 快递代领报酬
         */
        private String expressageLeadReward;
        /**
         * 快递代领类型
         */
        private String expressageLeadType;
        /**
         * 收件人姓名
         */
        private String deliveryUserName;
        /**
         * 收件人手机号码
         */
        private String deliveryPhoneNumber;
        /**
         * 快递代领备注
         */
        private String expressageLeadRemark;
        /**
         * 自己发布的包裹状态
         */
        private String expressageReleaseStatus;

        /**
         * 代领的包裹状态
         */
        private String expressageReceiveStatus;
        /**
         * 快递类型 韵达快递 申通快递
         */
        private String expressageType;

        public Builder expressageID(String expressageID) {
            this.expressageID = expressageID;
            return this;
        }

        public Builder expressageLeadNum(String expressageLeadNum) {
            this.expressageLeadNum = expressageLeadNum;
            return this;
        }

        public Builder deliveryTime(String deliveryTime) {
            this.deliveryTime = deliveryTime;
            return this;
        }

        public Builder deliverySite(String deliverySite) {
            this.deliverySite = deliverySite;
            return this;
        }

        public Builder releaseTime(String releaseTime) {
            this.releaseTime = releaseTime;
            return this;
        }

        public Builder expressageLeadReward(String expressageLeadReward) {
            this.expressageLeadReward = expressageLeadReward;
            return this;
        }

        public Builder deliveryUserName(String deliveryUserName) {
            this.deliveryUserName = deliveryUserName;
            return this;
        }

        public Builder deliveryPhoneNumber(String deliveryPhoneNumber) {
            this.deliveryPhoneNumber = deliveryPhoneNumber;
            return this;
        }

        public Builder expressageLeadRemark(String expressageLeadRemark) {
            this.expressageLeadRemark = expressageLeadRemark;
            return this;
        }

        public Builder expressageReleaseStatus(String expressageReleaseStatus) {
            this.expressageReleaseStatus = expressageReleaseStatus;
            return this;
        }

        public Builder expressageReceiveStatus(String expressageReceiveStatus) {
            this.expressageReceiveStatus = expressageReceiveStatus;
            return this;
        }

        public Builder expressageType(String expressageType) {
            this.expressageType = expressageType;
            return this;
        }

        public Builder expressageLeadType(String expressageLeadType) {
            this.expressageLeadType = expressageLeadType;
            return this;
        }

        public Builder expressageDesc(String expressageDesc) {
            this.expressageDesc = expressageDesc;
            return this;
        }

        public Builder() {

        }

        public Builder(Expressage expressage) {
            this.expressageID = expressage.expressageID;
            this.expressageLeadNum = expressage.expressageLeadNum;
            this.deliveryTime = expressage.deliveryTime;
            this.releaseTime = expressage.releaseTime;
            this.deliverySite = expressage.deliverySite;
            this.expressageDesc = expressage.expressageDesc;
            this.expressageLeadReward = expressage.expressageLeadReward;
            this.deliveryUserName = expressage.deliveryUserName;
            this.deliveryPhoneNumber = expressage.deliveryPhoneNumber;
            this.expressageLeadRemark = expressage.expressageLeadRemark;
            this.expressageReleaseStatus = expressage.expressageReleaseStatus;
            this.expressageReceiveStatus = expressage.expressageReceiveStatus;
            this.expressageType = expressage.expressageType;
            this.expressageLeadType = expressage.expressageLeadType;
        }

        public Expressage build() {
            return new Expressage(this.expressageID, this.expressageLeadNum, this.deliveryTime, this.releaseTime,
                    this.deliverySite, this.expressageDesc, this.expressageLeadReward, this.deliveryUserName,
                    this.deliveryPhoneNumber, this.expressageLeadRemark, this.expressageReleaseStatus, this.expressageReceiveStatus, this.expressageType, this.expressageLeadType);
        }

    }


}
