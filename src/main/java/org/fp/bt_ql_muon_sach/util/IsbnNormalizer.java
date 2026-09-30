package org.fp.bt_ql_muon_sach.util;

public final class IsbnNormalizer {

    private IsbnNormalizer() {
    }

    public static String normalize(String raw) {
        if (raw == null) {
            return "";
        }
        return raw.replace("-", "").replace(" ", "").toUpperCase();
    }

    public static boolean isValid(String normalized) {
        return normalized.matches("\\d{9}[\\dX]") || normalized.matches("\\d{13}");
    }
}
