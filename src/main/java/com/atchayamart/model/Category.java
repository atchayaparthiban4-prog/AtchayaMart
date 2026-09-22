package com.atchayamart.model;

public enum Category {
    FRUITS("Fruits", "\uD83C\uDF4E"),
    VEGETABLES("Vegetables", "\uD83E\uDD66"),
    RICE_GRAINS("Rice & Grains", "\uD83C\uDF3E"),
    DALS_PULSES("Dals & Pulses", "\uD83E\uDEDA"),
    COOKING_ESSENTIALS("Cooking Essentials", "\uD83E\uDDC2"),
    SPICES_MASALA("Spices & Masala", "\uD83C\uDF36\uFE0F"),
    DAIRY_EGGS("Dairy & Eggs", "\uD83E\uDD5A"),
    BEVERAGES("Beverages", "\u2615"),
    SNACKS("Snacks", "\uD83C\uDF6A"),
    PERSONAL_CARE("Personal Care", "\uD83E\uDDFC"),
    HOUSEHOLD("Household", "\uD83E\uDDF9");

    private final String label;
    private final String icon;

    Category(String label, String icon) {
        this.label = label;
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }
}