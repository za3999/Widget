package widget.cf.com.widgetlibrary.executor;

public abstract class PoolThread2<I, I2, R> extends PoolThread<I, R> {

    private I2 second;

    public PoolThread2(I data, I2 second) {
        super(data);
        this.second = second;
    }

    public I2 getSecond() {
        return second;
    }
}
