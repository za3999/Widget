package widget.cf.com.widgetlibrary.base;

public class BaseCallBack {

    public interface CallBack {
        void onCallBack();
    }

    public interface CallBack1<T> {
        void onCallBack(T t);
    }

    public interface CallBack2<T, K> {
        void onCallBack(T t, K k);
    }

    public interface CallBack3<T, K, M> {
        void onCallBack(T t, K k, M m);
    }

    public abstract static class InputCallBack<I, T> implements CallBack1<T> {
        public I input;

        public InputCallBack(I input) {
            this.input = input;
        }

        public I getInput() {
            return input;
        }

        public abstract void onCallBack(I input, T t);

        @Override
        public void onCallBack(T t) {
            onCallBack(input, t);
        }
    }

    public abstract static class InputCallBack2<I, T, K> implements CallBack2<T, K> {
        public I input;

        public InputCallBack2(I input) {
            this.input = input;
        }

        public I getInput() {
            return input;
        }

        public abstract void onCallBack(I input, T t, K k);

        @Override
        public void onCallBack(T t, K k) {
            onCallBack(input, t, k);
        }
    }
}
