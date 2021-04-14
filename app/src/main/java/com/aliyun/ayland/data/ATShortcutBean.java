package com.aliyun.ayland.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import static com.aliyun.ayland.data.ATSceneManualAutoBean.NOTSHOW;

public class ATShortcutBean implements Parcelable {
    private int isShowing = NOTSHOW;
    private String itemId;
    private int operateType;
    private int shortcutType;
    private String itemIcon;
    private String itemName;
    private String openId;
    private String shortcutSort;
    private String categoryKey;
    private String productKey;
    private int status;
    private List<AttributesBean> attributes;

    public int getIsShowing() {
        return isShowing;
    }

    public void setIsShowing(int isShowing) {
        this.isShowing = isShowing;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getOperateType() {
        return operateType;
    }

    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }

    public int getShortcutType() {
        return shortcutType;
    }

    public void setShortcutType(int shortcutType) {
        this.shortcutType = shortcutType;
    }

    public String getItemIcon() {
        return itemIcon;
    }

    public void setItemIcon(String itemIcon) {
        this.itemIcon = itemIcon;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getShortcutSort() {
        return shortcutSort;
    }

    public void setShortcutSort(String shortcutSort) {
        this.shortcutSort = shortcutSort;
    }

    @Override
    public String toString() {
        return "ShortcutBean{" +
                "itemId='" + itemId + '\'' +
                ", operateType=" + operateType +
                ", shortcutType=" + shortcutType +
                ", itemIcon='" + itemIcon + '\'' +
                ", itemName='" + itemName + '\'' +
                ", openId='" + openId + '\'' +
                ", shortcutSort='" + shortcutSort + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(itemId);
        out.writeString(itemIcon);
        out.writeString(itemName);
        out.writeString(openId);
        out.writeString(shortcutSort);
        out.writeString(categoryKey);
        out.writeString(productKey);
        out.writeInt(operateType);
        out.writeInt(shortcutType);
        out.writeInt(status);
    }

    public static final Creator<ATShortcutBean> CREATOR = new Creator<ATShortcutBean>() {
        @Override
        public ATShortcutBean[] newArray(int size) {
            return new ATShortcutBean[size];
        }

        @Override
        public ATShortcutBean createFromParcel(Parcel in) {
            return new ATShortcutBean(in);
        }
    };

    public ATShortcutBean() {

    }

    public ATShortcutBean(Parcel in) {
        itemId = in.readString();
        itemIcon = in.readString();
        itemName = in.readString();
        openId = in.readString();
        shortcutSort = in.readString();
        categoryKey = in.readString();
        productKey = in.readString();
        operateType = in.readInt();
        shortcutType = in.readInt();
        status = in.readInt();
    }

    public String getCategoryKey() {
        return categoryKey;
    }

    public void setCategoryKey(String categoryKey) {
        this.categoryKey = categoryKey;
    }

    public String getProductKey() {
        return productKey;
    }

    public void setProductKey(String productKey) {
        this.productKey = productKey;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<AttributesBean> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<AttributesBean> attributes) {
        this.attributes = attributes;
    }

    public static class AttributesBean {
        /**
         * specs : {"0":"关闭","1":"开启"}
         * dataType : BOOL
         * name : 电源开关
         * attribute : PowerSwitch
         * value : 1
         */

        private SpecsBean specs;
        private String dataType;
        private String name;
        private String attribute;
        private String value;

        public SpecsBean getSpecs() {
            return specs;
        }

        public void setSpecs(SpecsBean specs) {
            this.specs = specs;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAttribute() {
            return attribute;
        }

        public void setAttribute(String attribute) {
            this.attribute = attribute;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public static class SpecsBean {
            /**
             * 0 : 关闭
             * 1 : 开启
             */

            @SerializedName("0")
            private String _$0;
            @SerializedName("1")
            private String _$1;

            public String get_$0() {
                return _$0;
            }

            public void set_$0(String _$0) {
                this._$0 = _$0;
            }

            public String get_$1() {
                return _$1;
            }

            public void set_$1(String _$1) {
                this._$1 = _$1;
            }
        }
    }
}
