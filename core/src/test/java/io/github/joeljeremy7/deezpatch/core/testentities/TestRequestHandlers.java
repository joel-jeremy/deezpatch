package io.github.joeljeremy7.deezpatch.core.testentities;

import io.github.joeljeremy7.deezpatch.core.RequestHandler;

public class TestRequestHandlers {
    private TestRequestHandlers() {}

    public static TestRequestHandler testRequestHandler() {
        return new TestRequestHandler();
    }

    public static PrimitiveRequestHandler primitiveRequestHandler() {
        return new PrimitiveRequestHandler();
    }

    public static VoidRequestHandler voidRequestHandler() {
        return new VoidRequestHandler();
    }

    public static ThrowingIntegerRequestHandler throwingIntegerRequestHandler(
            RuntimeException toThrow
    ) {
        return new ThrowingIntegerRequestHandler(toThrow);
    }

    public static ThrowingVoidRequestHandler throwingVoidRequestHandler(
            RuntimeException toThrow
    ) {
        return new ThrowingVoidRequestHandler(toThrow);
    }

    public static InvalidRequestHandler invalidRequestHandler() {
        return new InvalidRequestHandler();
    }

    public static InvalidRequestHandlerMultipleParams invalidRequestHandlerMultipleParams() {
        return new InvalidRequestHandlerMultipleParams();
    }

    public static IncompatibleRequestHandler incompatibleRequestHandler() {
        return new IncompatibleRequestHandler();
    }

    public static class PrimitiveRequestHandler extends TrackableHandler {
        private PrimitiveRequestHandler() {}

        // Valid. Defines a primitive int instead of Integer.
        @RequestHandler
        public int handle(IntegerRequest request) {
            track(request);
            return Integer.parseInt(request.parameter());
        }

        // Valid. Defines a primitive short instead of Short.
        @RequestHandler
        public short handle(ShortRequest request) {
            track(request);
            return Short.parseShort(request.parameter());
        }

        // Valid. Defines a primitive long instead of Long.
        @RequestHandler
        public long handle(LongRequest request) {
            track(request);
            return Long.parseLong(request.parameter());
        }

        // Valid. Defines a primitive float instead of Float.
        @RequestHandler
        public float handle(FloatRequest request) {
            track(request);
            return Float.parseFloat(request.parameter());
        }

        // Valid. Defines a primitive double instead of Double.
        @RequestHandler
        public double handle(DoubleRequest request) {
            track(request);
            return Double.parseDouble(request.parameter());
        }

        // Valid. Defines a primitive byte instead of Byte.
        @RequestHandler
        public byte handle(ByteRequest request) {
            track(request);
            return Byte.parseByte(request.parameter());
        }

        // Valid. Defines a primitive boolean instead of Boolean.
        @RequestHandler
        public boolean handle(BooleanRequest request) {
            track(request);
            return Boolean.parseBoolean(request.parameter());
        }

        // Valid. Defines a primitive char instead of Character.
        @RequestHandler
        public char handle(CharacterRequest request) {
            track(request);
            return request.parameter().charAt(0);
        }
    }

    public static class TestRequestHandler extends TrackableHandler {
        private TestRequestHandler() {}

        @RequestHandler
        public TestResult handle(TestRequest request) {
            track(request);
            return new TestResult(request.parameter());
        }
    }

    public static class VoidRequestHandler extends TrackableHandler {
        private VoidRequestHandler() {}

        @RequestHandler
        public void handle(VoidRequest request) {
            // Fire! Then forget.
            track(request);
        }
    }

    public static class ThrowingIntegerRequestHandler extends TrackableHandler {
        private final RuntimeException toThrow;

        private ThrowingIntegerRequestHandler(RuntimeException toThrow) {
            this.toThrow = toThrow;
        }

        @RequestHandler
        public Integer handle(IntegerRequest request) {
            track(request);
            throw toThrow;
        }
    }

    public static class ThrowingVoidRequestHandler extends TrackableHandler {
        private final RuntimeException toThrow;

        private ThrowingVoidRequestHandler(RuntimeException toThrow) {
            this.toThrow = toThrow;
        }

        @RequestHandler
        public void handle(VoidRequest request) {
            // Fire! Then throw.
            track(request);
            throw toThrow;
        }
    }

    public static class InvalidRequestHandler {
        private InvalidRequestHandler() {}

        @RequestHandler
        public void invalid() {
            // Invalid.
        }
    }

    public static class InvalidRequestHandlerMultipleParams {
        private InvalidRequestHandlerMultipleParams() {}

        @RequestHandler
        public void invalid(IntegerRequest command, String anotherParameter) {
            // Invalid.
        }
    }

    public static class IncompatibleRequestHandler {
        private IncompatibleRequestHandler() {}

        // Invalid. Expected result is Integer/int but method returns a float.
        @RequestHandler
        public float incompatible(IntegerRequest request) {
            return 1.0F;
        }
    }
}