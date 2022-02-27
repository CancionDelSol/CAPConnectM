public class Util {
    static class RefSupport<T> {
        private T _ref = null;
        public RefSupport(T obj) {
            _ref = obj;
        }
        public void setRef(T ref) {
            _ref = ref;
        }
        public T getRef() {
            return _ref;
        }
    }
    public static final double Epsilon = 1E-8;
}