package com.aliyun.ayland.data;

import java.util.List;

public class AllVillageDetailBean1 {
    /**
     * area : [{"immeuble":[{"unit":[{"floor":[{"room":[{"buildingCode":"100015","name":"3室"},{"buildingCode":"100078","name":"1"}],"buildingCode":"100014","name":"3层"}],"buildingCode":"100013","name":"3单元"}],"buildingCode":"100012","name":"3栋"}],"buildingCode":"100011","name":"3区"}]
     * buildingCode : 100010
     * name : 3期
     */

    private String buildingCode;
    private String name;
    private List<AreaBean> area;

    public String getBuildingCode() {
        return buildingCode;
    }

    public void setbuildingCode(String buildingCode) {
        this.buildingCode = buildingCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AreaBean> getArea() {
        return area;
    }

    public void setArea(List<AreaBean> area) {
        this.area = area;
    }

    public static class AreaBean {
        /**
         * immeuble : [{"unit":[{"floor":[{"room":[{"buildingCode":"100015","name":"3室"},{"buildingCode":"100078","name":"1"}],"buildingCode":"100014","name":"3层"}],"buildingCode":"100013","name":"3单元"}],"buildingCode":"100012","name":"3栋"}]
         * buildingCode : 100011
         * name : 3区
         */

        private String buildingCode;
        private String name;
        private List<ImmeubleBean> immeuble;

        public String getBuildingCode() {
            return buildingCode;
        }

        public void setbuildingCode(String buildingCode) {
            this.buildingCode = buildingCode;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<ImmeubleBean> getImmeuble() {
            return immeuble;
        }

        public void setImmeuble(List<ImmeubleBean> immeuble) {
            this.immeuble = immeuble;
        }

        public static class ImmeubleBean {
            /**
             * unit : [{"floor":[{"room":[{"buildingCode":"100015","name":"3室"},{"buildingCode":"100078","name":"1"}],"buildingCode":"100014","name":"3层"}],"buildingCode":"100013","name":"3单元"}]
             * buildingCode : 100012
             * name : 3栋
             */

            private String buildingCode;
            private String name;
            private List<UnitBean> unit;

            public String getBuildingCode() {
                return buildingCode;
            }

            public void setbuildingCode(String buildingCode) {
                this.buildingCode = buildingCode;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public List<UnitBean> getUnit() {
                return unit;
            }

            public void setUnit(List<UnitBean> unit) {
                this.unit = unit;
            }

            public static class UnitBean {
                /**
                 * floor : [{"room":[{"buildingCode":"100015","name":"3室"},{"buildingCode":"100078","name":"1"}],"buildingCode":"100014","name":"3层"}]
                 * buildingCode : 100013
                 * name : 3单元
                 */

                private String buildingCode;
                private String name;
                private List<FloorBean> floor;

                public String getBuildingCode() {
                    return buildingCode;
                }

                public void setbuildingCode(String buildingCode) {
                    this.buildingCode = buildingCode;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public List<FloorBean> getFloor() {
                    return floor;
                }

                public void setFloor(List<FloorBean> floor) {
                    this.floor = floor;
                }

                public static class FloorBean {
                    /**
                     * room : [{"buildingCode":"100015","name":"3室"},{"buildingCode":"100078","name":"1"}]
                     * buildingCode : 100014
                     * name : 3层
                     */

                    private String buildingCode;
                    private String name;
                    private List<RoomBean> room;

                    public String getBuildingCode() {
                        return buildingCode;
                    }

                    public void setbuildingCode(String buildingCode) {
                        this.buildingCode = buildingCode;
                    }

                    public String getName() {
                        return name;
                    }

                    public void setName(String name) {
                        this.name = name;
                    }

                    public List<RoomBean> getRoom() {
                        return room;
                    }

                    public void setRoom(List<RoomBean> room) {
                        this.room = room;
                    }

                    public static class RoomBean {
                        /**
                         * buildingCode : 100015
                         * name : 3室
                         */

                        private String buildingCode;
                        private String name;

                        public String getBuildingCode() {
                            return buildingCode;
                        }

                        public void setbuildingCode(String buildingCode) {
                            this.buildingCode = buildingCode;
                        }

                        public String getName() {
                            return name;
                        }

                        public void setName(String name) {
                            this.name = name;
                        }
                    }
                }
            }
        }
    }
}