package de.theshadowsdust.containersort.util;

public final class Permissions {

    public static final String PERMISSION_SORT_ALLOW = "containersort.allow";
    public static final String PERMISSION_SORT_ALLOW_OTHERS = "containersort.allow.others";

    public static final String PERMISSION_SORT_CREATE = "containersort.create";
    public static final String PERMISSION_SORT_CREATE_OTHERS = "containersort.create.others";

    public static final String PERMISSION_SORT_BREAK_OTHERS = "containersort.break.others";

    private Permissions() {
        throw new IllegalStateException("Utility class");
    }
}
