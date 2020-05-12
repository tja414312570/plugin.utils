
package com.YaNan.frame.utils.asserts;

/**
 * @since   plugin 2.0
 */
public
class AssertTrueException extends RuntimeException {
    private static final long serialVersionUID = 5162710183389028792L;

    /**
     * Constructs a {@code AssertTrueException} with no detail message.
     */
    public AssertTrueException() {
        super();
    }

    /**
     * Constructs a {@code AssertTrueException} with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public AssertTrueException(String s) {
        super(s);
    }
}
