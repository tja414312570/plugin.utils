
package com.YaNan.frame.utils.asserts;

/**
 * @since   plugin 2.0
 */
public
class AssertNotNullException extends RuntimeException {
    private static final long serialVersionUID = 5162710183389028792L;

    /**
     * Constructs a {@code AssertNotNullException} with no detail message.
     */
    public AssertNotNullException() {
        super();
    }

    /**
     * Constructs a {@code AssertNotNullException} with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public AssertNotNullException(String s) {
        super(s);
    }
}
