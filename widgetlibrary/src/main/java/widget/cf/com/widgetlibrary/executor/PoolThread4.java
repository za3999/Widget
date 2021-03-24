package widget.cf.com.widgetlibrary.executor;

public abstract class PoolThread4<I, I2, I3, I4, R> extends PoolThread3<I, I2, I3, R> {

    private I4 four;

    public PoolThread4(I data, I2 second, I3 third, I4 four) {
        super(data, second, third);
        this.four = four;
    }

    public I4 getFour() {
        return four;
    }
}
