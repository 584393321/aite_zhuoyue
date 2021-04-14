package com.aliyun.ayland.data;

import java.util.List;

public class ATFindFamilyMemberForOutAbnormalBean {
    /**
     * propertyStatus : 1
     * code : 200
     * size : 0
     * members : [{"cycleList":[{"beginTime":333,"createPerson":100002,"createTime":1587629326000,"endTime":777,"id":18,"personCode":100002,"weekDay":3},{"beginTime":222,"createPerson":100002,"createTime":1587629326000,"endTime":555,"id":19,"personCode":100002,"weekDay":4}],"firstname":"云","householdtype":101,"lastname":"林","personCode":100002,"status":1}]
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
         * cycleList : [{"beginTime":333,"createPerson":100002,"createTime":1587629326000,"endTime":777,"id":18,"personCode":100002,"weekDay":3},{"beginTime":222,"createPerson":100002,"createTime":1587629326000,"endTime":555,"id":19,"personCode":100002,"weekDay":4}]
         * firstname : 云
         * householdtype : 101
         * lastname : 林
         * personCode : 100002
         * status : 1
         */

        private String firstname;
        private int householdtype;
        private String lastname;
        private int personCode;
        private int status;
        private List<CycleListBean> cycleList;

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

        public List<CycleListBean> getCycleList() {
            return cycleList;
        }

        public void setCycleList(List<CycleListBean> cycleList) {
            this.cycleList = cycleList;
        }

        public static class CycleListBean {
            /**
             * beginTime : 333
             * createPerson : 100002
             * createTime : 1587629326000
             * endTime : 777
             * id : 18
             * personCode : 100002
             * weekDay : 3
             */

            private int beginTime;
            private int createPerson;
            private long createTime;
            private int endTime;
            private int id;
            private int personCode;
            private int weekDay;
            private boolean hide;

            public int getBeginTime() {
                return beginTime;
            }

            public void setBeginTime(int beginTime) {
                this.beginTime = beginTime;
            }

            public int getCreatePerson() {
                return createPerson;
            }

            public void setCreatePerson(int createPerson) {
                this.createPerson = createPerson;
            }

            public long getCreateTime() {
                return createTime;
            }

            public void setCreateTime(long createTime) {
                this.createTime = createTime;
            }

            public int getEndTime() {
                return endTime;
            }

            public void setEndTime(int endTime) {
                this.endTime = endTime;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getPersonCode() {
                return personCode;
            }

            public void setPersonCode(int personCode) {
                this.personCode = personCode;
            }

            public int getWeekDay() {
                return weekDay;
            }

            public void setWeekDay(int weekDay) {
                this.weekDay = weekDay;
            }

            public boolean isHide() {
                return hide;
            }

            public void setHide(boolean hide) {
                this.hide = hide;
            }

            @Override
            public String toString() {
                return "CycleListBean{" +
                        "beginTime=" + beginTime +
                        ", createPerson=" + createPerson +
                        ", createTime=" + createTime +
                        ", endTime=" + endTime +
                        ", id=" + id +
                        ", personCode=" + personCode +
                        ", weekDay=" + weekDay +
                        ", hide=" + hide +
                        '}';
            }
        }
    }
}