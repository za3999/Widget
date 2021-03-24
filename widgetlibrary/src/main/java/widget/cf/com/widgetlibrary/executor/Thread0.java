package widget.cf.com.widgetlibrary.executor;


public class Thread0 extends PoolThread<Void, Void> implements Runnable {

    private Runnable runnable;

    public Thread0() {
        super(null);
    }

    public Thread0(Runnable runnable) {
        super(null);
        this.runnable = runnable;
    }

    @Override
    public final Void run(Void data) {
        if (runnable != null) {
            runnable.run();
        }
        run();
        return null;
    }

    @Override
    public void run() {

    }
}
