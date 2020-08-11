
package com.yanan.utils.asserts;

/**
 * @since   plugin 2.0
 */
public
class AssertFalseException extends RuntimeException {
    private static final long serialVersionUID = 5162710183389028792L;

    /**
     * Constructs a {@code AssertTrueException} with no detail message.
     */
    public AssertFalseException() {
        super();
    }

    /**
     * Constructs a {@code AssertTrueException} with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public AssertFalseException(String s) {
        super(s);
    }
}
