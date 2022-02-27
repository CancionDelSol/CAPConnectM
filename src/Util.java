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
}