package team_ky.androidteambuildinghackathon.encode;

/**
 * Created by kinagafuji on 15/11/22.
 */
public final class Preconditions {
    private Preconditions() {
    }

    public static void checkArgument(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static void checkArgument(boolean expression,
                                     String errorMessageTemplate, Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(
                    format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static void checkState(boolean expression) {
        if (!expression) {
            throw new IllegalStateException();
        }
    }

    public static void checkState(boolean expression, Object errorMessage) {
        if (!expression) {
            throw new IllegalStateException(String.valueOf(errorMessage));
        }
    }

    public static void checkState(boolean expression,
                                  String errorMessageTemplate, Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalStateException(
                    format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }

    public static <T> T checkNotNull(T reference, Object errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }

    public static <T> T checkNotNull(T reference, String errorMessageTemplate,
                                     Object... errorMessageArgs) {
        if (reference == null) {
            // If either of these parameters is null, the right thing happens anyway
            throw new NullPointerException(
                    format(errorMessageTemplate, errorMessageArgs));
        }
        return reference;
    }

    public static void checkElementIndex(int index, int size) {
        checkElementIndex(index, size, "index");
    }

    public static void checkElementIndex(int index, int size, String desc) {
        checkArgument(size >= 0, "negative size: %s", size);
        if (index < 0) {
            throw new IndexOutOfBoundsException(
                    format("%s (%s) must not be negative", desc, index));
        }
        if (index >= size) {
            throw new IndexOutOfBoundsException(
                    format("%s (%s) must be less than size (%s)", desc, index, size));
        }
    }

    public static void checkPositionIndex(int index, int size) {
        checkPositionIndex(index, size, "index");
    }

    public static void checkPositionIndex(int index, int size, String desc) {
        checkArgument(size >= 0, "negative size: %s", size);
        if (index < 0) {
            throw new IndexOutOfBoundsException(format(
                    "%s (%s) must not be negative", desc, index));
        }
        if (index > size) {
            throw new IndexOutOfBoundsException(format(
                    "%s (%s) must not be greater than size (%s)", desc, index, size));
        }
    }

    public static void checkPositionIndexes(int start, int end, int size) {
        checkPositionIndex(start, size, "start index");
        checkPositionIndex(end, size, "end index");
        if (end < start) {
            throw new IndexOutOfBoundsException(format(
                    "end index (%s) must not be less than start index (%s)", end, start));
        }
    }

    // VisibleForTesting
    static String format(String template, Object... args) {
        // start substituting the arguments into the '%s' placeholders
        StringBuilder builder = new StringBuilder(
                template.length() + 16 * args.length);
        int templateStart = 0;
        int i = 0;
        while (i < args.length) {
            int placeholderStart = template.indexOf("%s", templateStart);
            if (placeholderStart == -1) {
                break;
            }
            builder.append(template.substring(templateStart, placeholderStart));
            builder.append(args[i++]);
            templateStart = placeholderStart + 2;
        }
        builder.append(template.substring(templateStart));

        // if we run out of placeholders, append the extra args in square braces
        if (i < args.length) {
            builder.append(" [");
            builder.append(args[i++]);
            while (i < args.length) {
                builder.append(", ");
                builder.append(args[i++]);
            }
            builder.append("]");
        }

        return builder.toString();
    }
}
