package com.aliyun.ayland.data;

import java.util.List;

public class ATFindFamilyMemberForOutAloneBean {
    /**
     * propertyStatus : 0
     * code : 200
     * size : 0
     * members : [{"firstname":"狗","householdtype":102,"lastname":"新","personCode":100133,"status":1}]
     * message : success
     * operatorName : 林云
     * operator : 100002
     */

    private int propertyStatus;
    private int code;
    private int size;
    private String message;
    private String operatorName;
    private int operator;
    private List<MembersBean> members;

    public int getPropertyStatus() {
        return propertyStatus;
    }

    public void setPropertyStatus(int propertyStatus) {
        this.propertyStatus = propertyStatus;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public int getOperator() {
        return operator;
    }

    public void setOperator(int operator) {
        this.operator = operator;
    }

    public List<MembersBean> getMembers() {
        return members;
    }

    public void setMembers(List<MembersBean> members) {
        this.members = members;
    }

    public static class MembersBean {
        /**
         * firstname : 狗
         * householdtype : 102
         * lastname : 新
         * personCode : 100133
         * status : 1
         */

        private String firstname;
        private int householdtype;
        private String lastname;
        private int personCode;
        private int status;

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public int getHouseholdtype() {
            return householdtype;
        }

        public void setHouseholdtype(int householdtype) {
            this.householdtype = householdtype;
        }

        public String getLastname() {
            return lastname;
        }

        public void setLastname(String lastname) {
            this.lastname = lastname;
        }

        public int getPersonCode() {
            return personCode;
        }

        public void setPersonCode(int personCode) {
            this.personCode = personCode;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}