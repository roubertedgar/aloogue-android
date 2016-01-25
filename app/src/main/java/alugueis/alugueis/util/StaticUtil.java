package alugueis.alugueis.util;

import android.content.Context;

import alugueis.alugueis.model.UserApp;
import alugueis.alugueis.model.UserApp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Pedreduardo / Edgar on 08/12/2015.
 */
public class StaticUtil {
    public static final String LOGGED_USER = "loggedUser";

    public static Object getObject(Context context, String key) throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput(key);
        ObjectInputStream ois = new ObjectInputStream(fis);
        return ois.readObject();
    }

    public static void setOject(Context context, String key, Object object) throws IOException {
        FileOutputStream fos = context.openFileOutput(key, Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }


    public static void remove(Context context, String key) throws IOException {
        context.deleteFile(key);
    }
}
