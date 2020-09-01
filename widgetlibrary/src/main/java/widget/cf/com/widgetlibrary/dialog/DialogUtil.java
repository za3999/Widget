package widget.cf.com.widgetlibrary.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;

import java.lang.reflect.Field;

public class DialogUtil {

    private static final int DISMISS = 0x43;
    private static final int CANCEL = 0x44;
    private static final int SHOW = 0x45;

    public static void setOnCancelListener(Dialog dialog, @Nullable DialogInterface.OnCancelListener listener) {
        if (dialog == null) {
            return;
        }
        Handler handler = getListenersHandler(dialog);
        if (listener != null && handler != null) {
            Message message = handler.obtainMessage();
            message.setTarget(handler);
            message.what = CANCEL;
            message.obj = listener;
            dialog.setCancelMessage(message);
        } else if (handler == null) {
            dialog.setOnCancelListener(listener);
        } else {
            dialog.setCancelMessage(null);
        }
    }

    public static void setOnDismissListener(Dialog dialog, @Nullable DialogInterface.OnDismissListener listener) {
        if (dialog == null) {
            return;
        }
        Handler handler = getListenersHandler(dialog);
        if (listener != null && handler != null) {
            Message message = handler.obtainMessage();
            message.setTarget(handler);
            message.what = DISMISS;
            message.obj = listener;
            dialog.setDismissMessage(message);
        } else if (handler == null) {
            dialog.setOnDismissListener(listener);
        } else {
            dialog.setDismissMessage(null);
        }
    }

    public static void setOnShowListener(Dialog dialog, @Nullable DialogInterface.OnShowListener listener) {
        if (dialog == null) {
            return;
        }
        Handler handler = getListenersHandler(dialog);
        if (listener != null && handler != null) {
            Message message = handler.obtainMessage();
            message.setTarget(handler);
            message.what = SHOW;
            message.obj = listener;
            setOnShowMessage(dialog, message);
        } else if (handler == null) {
            dialog.setOnShowListener(listener);
        } else {
            setOnShowMessage(dialog, null);
        }
    }

    public abstract static class OnClickListenerWrapper<I> implements DialogInterface.OnClickListener {

        I input;

        public OnClickListenerWrapper(I input) {
            this.input = input;
        }

        public abstract void onClick(I input, DialogInterface dialog, int which);

        @Override
        public void onClick(DialogInterface dialog, int which) {
            onClick(input, dialog, which);
        }
    }

    public abstract static class OnCancelListenerWrapper<I> implements DialogInterface.OnCancelListener {

        I input;

        public OnCancelListenerWrapper(I input) {
            this.input = input;
        }

        public abstract void onCancel(I input, DialogInterface dialog);

        @Override
        public void onCancel(DialogInterface dialog) {
            onCancel(input, dialog);
        }
    }


    private static void setOnShowMessage(Dialog dialog, Message message) {
        if (dialog != null) {
            try {
                Field field = getField(dialog, "mShowMessage");
                if (field != null) {
                    field.setAccessible(true);
                    field.set(dialog, message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static Field getField(Dialog dialog, String fieldName) {
        Field field = getField(dialog.getClass(), fieldName);
        Class clazz = dialog.getClass().getSuperclass();
        while (field == null && clazz != null) {
            field = getField(clazz, fieldName);
            clazz = clazz.getSuperclass();
        }
        return field;
    }

    private static Field getField(Class clazz, String fieldName) {
        Field field = null;
        if (clazz != null) {
            try {
                field = clazz.getDeclaredField(fieldName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return field;
    }

    private static Handler getListenersHandler(Dialog dialog) {
        Handler handler = (Handler) getObject(dialog, "mListenersHandler", dialog.getClass());
        Class clazz = dialog.getClass().getSuperclass();
        while (handler == null && clazz != null) {
            handler = (Handler) getObject(dialog, "mListenersHandler", clazz);
            clazz = clazz.getSuperclass();
        }
        return handler;
    }

    private static Object getObject(Dialog dialog, String fieldName, Class clazz) {
        Object object = null;
        if (dialog != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                object = field.get(dialog);
            } catch (Exception e) {
                //do nothing
            }
        }
        return object;
    }
}
