package widget.cf.com.widgetlibrary.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 * Function：封装的SharedPreferences工具类
 * Desc：
 * 功能列表
 * 1.保存数据到SP文件
 * 2.获取SP中保存的值
 * 3.移除某一个值
 * 4.移除所有值
 * 5.检测文件中是否包含某个值
 * 6.获取某个SP文件中的所有值
 */
public class SPUtil {

    public static final String DEFAULT_SP_NAME = "default";


    /**
     * 保存数据到默认的Preferences中
     *
     * @param context 上下文
     * @param key     键名
     * @param object  要保存的值
     */
    public static void put(Context context, String key, Object object) {
        put(DEFAULT_SP_NAME, context, key, object);
    }

    /**
     * 从默认的Preferences获取指定键名对应的值
     *
     * @param context
     * @param key
     * @param object
     */
    public static <T> T get(Context context, String key, Object object, Class<T> tClass) {
        return get(DEFAULT_SP_NAME, context, key, object, tClass);
    }

    /**
     * 保存数据
     * 会根据传入类型自动判断保存类型
     *
     * @param context 上下文
     * @param key     键名
     * @param object  要保存的值
     */
    public static void put(String fileName, Context context, String key, Object object) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (object instanceof String) {
            editor.putString(key, (String) object);
        } else if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        } else if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        } else if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        } else if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 获取指定键名对应的值
     * 会根据传入的默认值判断获取对应类型的值
     *
     * @param context 上下文
     * @param key     键名
     * @param def     默认值
     * @return
     */
    public static <T> T get(String fileName, Context context, String key, Object def, Class<T> tClass) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        if (def instanceof String) {
            return tClass.cast(sp.getString(key, (String) def));
        } else if (def instanceof Integer) {
            return tClass.cast(sp.getInt(key, (Integer) def));
        } else if (def instanceof Boolean) {
            return tClass.cast(sp.getBoolean(key, (Boolean) def));
        } else if (def instanceof Float) {
            return tClass.cast(sp.getFloat(key, (Float) def));
        } else if (def instanceof Long) {
            return tClass.cast(sp.getLong(key, (Long) def));
        }
        return null;
    }

    /**
     * 移除某个值
     *
     * @param context  上下文
     * @param fileName 文件名
     * @param key      键值
     */
    public static void remove(Context context, String fileName, String key) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除文件中的所有数据
     *
     * @param context  上下文
     * @param fileName 文件名
     */
    public static void clearAll(Context context, String fileName) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param context  上下文
     * @param fileName 文件名
     * @param key      键名
     * @return true（包含）、false（不包含）
     */
    public static boolean contains(Context context, String fileName, String key) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @param context  上下名
     * @param fileName 文件名
     * @return
     */
    public static Map<String, ?> getAll(Context context, String fileName) {
        SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     *
     * @author zhy
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }
}