package widget.cf.com.widgetlibrary.executor;


public class Thread0 extends PoolThread<Void, Void> {

    private Runnable runnable;

    public Thread0(Runnable runnable) {
        super(null);
        this.runnable = runnable;
    }

    @Override
    public Void run(Void data) {
        runnable.run();
        return null;
    }
}
