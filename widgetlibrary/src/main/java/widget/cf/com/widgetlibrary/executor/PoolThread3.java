package widget.cf.com.widgetlibrary.executor;

public abstract class PoolThread3<I, I2, I3, R> extends PoolThread2<I, I2, R> {

    private I3 third;

    public PoolThread3(I data, I2 second, I3 third) {
        super(data, second);
        this.third = third;
    }

    public I3 getThird() {
        return third;
    }
}
