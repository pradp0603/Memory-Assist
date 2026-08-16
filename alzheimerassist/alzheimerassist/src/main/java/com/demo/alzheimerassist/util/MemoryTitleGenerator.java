package com.demo.alzheimerassist.util;

import com.demo.alzheimerassist.entity.MemoryType;

public final class MemoryTitleGenerator {

    private MemoryTitleGenerator() {
    }

    public static String generateTitle(MemoryType type) {

        return switch (type) {

            case ADDRESS -> "Home Address";

            case PHONE -> "Phone Number";

            case PASSWORD -> "Password";

            case DOCTOR -> "Doctor Details";

            case MEDICATION -> "Medication";

            case OBJECT_LOCATION -> "Object Location";

            default -> "Memory";

        };

    }

}